import com.squareup.kotlinpoet.*
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

abstract class GenerateLoggerFunctionsTask : AbstractGenerateFilesTask() {

    @get:Input
    abstract val logLevels: ListProperty<String>

    @Suppress("PrivatePropertyName")
    private val KLogger = ClassName("io.github.oshai.kotlinlogging", "KLogger")

    @TaskAction
    fun generate() {
        generateFile("common", "InlinedLogger") {
            generateLoggerFunctions { level, functionName ->
                // e.g debug -> isDebugEnabled
                val enabledPropertyName = "is${level[0].uppercaseChar()}${level.drop(1)}Enabled"

                debugLevelInlined(functionName, level) {
                    val code = """
                                if ($enabledPropertyName()) {
                                    val computedMessage = message()
                                    
                                    $level { computedMessage }
                                }
                            """.trimIndent()
                    addCode(code)
                }
                debugLevelInlinedWithThrowable(functionName, level) {
                    val code = """
                                if ($enabledPropertyName()) {
                                    val computedMessage = message()
                                    
                                    $level(throwable) { computedMessage }
                                }
                            """.trimIndent()
                    addCode(code)
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
        code: FunSpec.Builder.() -> Unit = {}
    ) {
        addFunction(
            FunSpec
                .builder(functionName)
                .receiver(KLogger)
                .addKdoc("""Inline version of [KLogger.$level] so it can call suspend functions""")
                .addModifiers(
                    KModifier.PUBLIC,
                    KModifier.INLINE
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
        code: FunSpec.Builder.() -> Unit = {}
    ) {
        debugLevelInlined(functionName, level) {
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
