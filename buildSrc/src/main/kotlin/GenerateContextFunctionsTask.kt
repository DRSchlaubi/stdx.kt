import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeAliasSpec
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.joinToCode
import org.gradle.api.tasks.TaskAction

const val returnTypeParameter = "ReturnType"

abstract class GenerateContextFunctionsTask : AbstractGenerateFilesTask() {
    @TaskAction
    fun generate() {
        generateFile("common", "ContextFunctions", {
            it.replace("import `?lambda_A(, [A-Z])*`?\n".toRegex(), "")
                .replaceFirst("lambda_A", "A.() -> ReturnType")
                .replace("`lambda_A(?:, ((?:(?:, )?[A-Z])*)`)?".toRegex(), "context($1) A.() -> ReturnType")
        }) {
            val invocationKind = ClassName("kotlin.contracts", "InvocationKind")
            addImport("kotlin.contracts", "contract")

            val alphabet = ('A'..'Z').toList()

            val functions = ArrayList<FunSpec>(alphabet.size)

            repeat(alphabet.size) {
                val count = it + 1
                val current = alphabet.take(count)
                val typeVariables = (current.map(Char::toString) + returnTypeParameter).map { name ->
                    TypeVariableName(name)
                }


                val lambda = "lambda_${typeVariables.dropLast(1).joinToString(", ") { variable -> variable.name }}"

                val typeAlias = ClassName(packageName, "ContextReceiver$count")

                addTypeAlias(
                    TypeAliasSpec.builder(typeAlias.simpleName, ClassName("", lambda))
                        .addModifiers(KModifier.PUBLIC)
                        .addTypeVariables(typeVariables)
                        .addKdoc(
                            """
                            Lambda with $count context receivers.
                            
                            @see context""".trimIndent()
                        )
                        .build()
                )

                val last = CodeBlock.of("%N", "a")
                val others = typeVariables.subList(1, typeVariables.size - 1).map {
                    CodeBlock.of("%N", it.name.toLowerCase())
                }

                val argumentList = (others + last).joinToCode(", ")

                val function = FunSpec.builder("context")
                    .addModifiers(KModifier.PUBLIC, KModifier.INLINE)
                    .addTypeVariables(typeVariables)
                    .apply {
                        typeVariables.dropLast(1).forEach { type ->
                            addParameter(type.name.toLowerCase(), type)
                        }
                    }
                    .addParameter("block", typeAlias.parameterizedBy(typeVariables))
                    .returns(typeVariables.last())
                    .addKdoc(
                        """
                        Adds ${
                            typeVariables.dropLast(1)
                                .joinToString("], [", "[", "]") { it.name.toLowerCase() }
                        } to the context of [block] and returns its return value.
                        """.trimIndent()
                    )
                    .beginControlFlow("contract")
                    .addStatement("callsInPlace(%N, %T.%N)", "block", invocationKind, "EXACTLY_ONCE")
                    .endControlFlow()
                    .addStatement("return block.invoke(%L)", argumentList)
                    .build()

                functions += function
            }

            functions.forEach { addFunction(it) }
        }
    }
}
