import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.intellij.lang.annotations.Language
import java.nio.file.Path

abstract class GenerateLoggerFunctionsTask : DefaultTask() {

    @get:Input
    abstract val `package`: Property<String>

    @get:Input
    abstract val logLevels: ListProperty<String>

    private val outputDir = project.buildDir
        .toPath()
        .resolvePaths("generated", "src")

    @Suppress("PrivatePropertyName")
    private val KLogger = ClassName("mu", "KLogger")

    init {
        outputs.dir(outputDir)
    }

    @TaskAction
    fun generate() {
        generateFile("commonMain") {
            logLevels.get().forEach { level ->
                val functionName = "${level}Inlined"

                debugLevelInlined(functionName, level)
                debugLevelInlinedWithThrowable(functionName, level)
            }
        }

        generateFile("jvmMain") {
            generateLoggerFunctions { level, functionName ->
                // e.g debug -> isDebugEnabled
                val enabledPropertyName = "is${level[0].toUpperCase()}${level.drop(1)}Enabled"

                debugLevelInlined(functionName, level, KModifier.ACTUAL) {
                    val code = """
                        if ($enabledPropertyName) {
                            $level(message())
                        }
                    """.trimIndent()
                    addCode(code)
                }
                debugLevelInlinedWithThrowable(functionName, level, KModifier.ACTUAL) {
                    val code = """
                        if ($enabledPropertyName) {
                            val computedMessage = message()
                            
                            $level(throwable) { computedMessage }
                        }
                    """.trimIndent()
                    addCode(code)
                }
            }
        }

        generateNonSLF4JFile("jsMain")
        generateNonSLF4JFile("nativeMain")
    }

    private fun generateNonSLF4JFile(name: String) {
        generateFile(name) {
            addImport("mu", "KotlinLoggingLevel")
            addImport("mu", "isLoggingEnabled")

            generateLoggerFunctions { level, functionName ->
                @Language("kotlin")
                fun code(call: String) = """
                        if (KotlinLoggingLevel.${level.toUpperCase()}.isLoggingEnabled()) {
                            val computedLogMessage = message()
                            $call
                        }
                    """.trimIndent()

                debugLevelInlined(functionName, level, KModifier.ACTUAL) {
                    addCode(code("$level { computedLogMessage }"))
                }
                debugLevelInlinedWithThrowable(functionName, level, KModifier.ACTUAL) {
                    addCode(code("$level(throwable) { computedLogMessage }"))
                }
            }
        }
    }

    private inline fun generateLoggerFunctions(onFunction: (level: String, functionName: String) -> Unit) {
        logLevels.get().forEach { level ->
            val functionName = "${level}Inlined"

            onFunction(level, functionName)
        }
    }

    private fun generateFile(sourceSet: String, block: FileSpec.Builder.() -> Unit) {
        val file = FileSpec.builder(`package`.get(), "InlinedLogger")
            .apply {
                commonImports()
            }
            .apply(block)
            .build()

        file.writeTo(outputDir.resolve(sourceSet))
    }

    private fun FileSpec.Builder.debugLevelInlined(
        functionName: String,
        level: String?,
        mppModifier: KModifier = KModifier.EXPECT,
        code: FunSpec.Builder.() -> Unit = {}
    ) {
        addFunction(
            FunSpec
                .builder(functionName)
                .receiver(KLogger)
                .addKdoc("""Inline version of [KLogger.$level] so it can call suspend functions""")
                .addModifiers(
                    KModifier.PUBLIC,
                    KModifier.INLINE,
                    mppModifier
                )
                .apply(code)
                .addParameter(
                    ParameterSpec
                        .builder("message", ClassName(`package`.get(), "LazyLogMessage"))
                        .build()
                )
                .build()
        )
    }

    private fun FileSpec.Builder.debugLevelInlinedWithThrowable(
        functionName: String,
        level: String?,
        mppModifier: KModifier = KModifier.EXPECT,
        code: FunSpec.Builder.() -> Unit = {}
    ) {
        debugLevelInlined(functionName, level, mppModifier) {
            addParameter(
                ParameterSpec
                    .builder("throwable", Throwable::class)
                    .defaultValue(null)
                    .build()
            )
            code()
        }
    }


    private fun FileSpec.Builder.commonImports() {
        addImport(KLogger.packageName, KLogger.simpleName)
    }
}

private fun Path.resolvePaths(vararg relative: String) =
    relative.toList().fold(this) { parent, currentRelative -> parent.resolve(currentRelative) }
