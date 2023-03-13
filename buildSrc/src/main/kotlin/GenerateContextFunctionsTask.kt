import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import org.gradle.api.tasks.TaskAction
import java.util.*
import kotlin.collections.ArrayList


abstract class GenerateContextFunctionsTask : AbstractGenerateFilesTask() {
    @OptIn(ExperimentalKotlinPoetApi::class)
    @TaskAction
    fun generate() {
        generateFile("common", "ContextFunctions") {
            addImport("kotlin.contracts", "InvocationKind", "contract")

            val alphabet = ('A'..'Z').toList()

            val functions = ArrayList<FunSpec>(alphabet.size)

            repeat(alphabet.size) {
                val count = it + 1
                val current = alphabet.take(count)
                val returnTypeParameter = TypeVariableName("ReturnType")
                val typeVariables = (current.map(Char::toString)).map { name ->
                    TypeVariableName(name)
                } + returnTypeParameter

                val (receiver) = typeVariables
                val contextReceivers = typeVariables.subList(1, typeVariables.size - 1)
                val lambda = LambdaTypeName.get(receiver = receiver, returnType = returnTypeParameter, contextReceivers = contextReceivers)
                val typeAlias = ClassName(packageName, "ContextReceiver$count")

                addTypeAlias(
                    TypeAliasSpec.builder(typeAlias.simpleName, lambda)
                        .addModifiers(KModifier.PUBLIC)
                        .addTypeVariables(typeVariables)
                        .addKdoc(
                            """
                            Lambda with $count context receivers.
                            
                            @see context""".trimIndent()
                        )
                        .build()
                )

                val argumentList = if (count > 1) {
                    (typeVariables.subList(1, typeVariables.size - 1)
                        .map { variable -> variable.name.lowercase(Locale.getDefault()) } + 'a').joinToString(
                        ", "
                    )
                } else {
                    'a'
                }

                val function = FunSpec.builder("context")
                    .addModifiers(KModifier.PUBLIC, KModifier.INLINE)
                    .addTypeVariables(typeVariables)
                    .apply {
                        typeVariables.dropLast(1).forEach { type ->
                            addParameter(type.name.lowercase(Locale.getDefault()), type)
                        }
                    }
                    .addParameter("block", typeAlias.parameterizedBy(typeVariables))
                    .returns(typeVariables.last())
                    .addKdoc(
                        """
                        Adds ${
                            typeVariables.dropLast(1)
                                .joinToString("], [", "[", "]") { it.name.lowercase(Locale.getDefault()) }
                        } to the context of [block] and returns its return value.
                        """.trimIndent()
                    )
                    .addCode(
                        """
                        contract {
                            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
                        }

                        return block.invoke($argumentList)
                        """.trimIndent()
                    )
                    .build()

                functions += function
            }

            functions.forEach { addFunction(it) }
        }
    }
}
