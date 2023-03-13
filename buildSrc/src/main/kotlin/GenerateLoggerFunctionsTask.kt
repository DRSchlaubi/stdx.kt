import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.intellij.lang.annotations.Language
import java.util.*

abstract class GenerateLoggerFunctionsTask : AbstractGenerateFilesTask() {

    @get:Input
    abstract val logLevels: ListProperty<String>

    @Suppress("PrivatePropertyName")
    private val KLogger = ClassName("mu", "KLogger")

    @TaskAction
    fun generate() {
        generateFile("common", "InlinedLogger") {
            logLevels.get().forEach { level ->
                val functionName = "${level}Inlined"

                debugLevelInlined(functionName, level)
                debugLevelInlinedWithThrowable(functionName, level)
            }
        }

        generateFile("jvm", "InlinedLogger") {
            generateLoggerFunctions { level, functionName ->
                // e.g debug -> isDebugEnabled
                val enabledPropertyName = "is${level[0].uppercaseChar()}${level.drop(1)}Enabled"

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

        generateNonSLF4JFile("js")
        generateNonSLF4JFile("native")
    }

    private fun generateNonSLF4JFile(name: String) {
        generateFile(name, "InlinedLogger") {
            addImport("mu", "KotlinLoggingLevel", "isLoggingEnabled")

            generateLoggerFunctions { level, functionName ->
                @Language("kotlin")
                fun code(call: String) = """
                                if (KotlinLoggingLevel.${level.uppercase(Locale.getDefault())}.isLoggingEnabled()) {
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
}
