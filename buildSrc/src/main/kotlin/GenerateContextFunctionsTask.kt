import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeAliasSpec
import com.squareup.kotlinpoet.TypeVariableName
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
            addImport("kotlin.contracts", "InvocationKind", "contract")

            val alphabet = ('A'..'Z').toList()

            val functions = ArrayList<FunSpec>(alphabet.size)

            repeat(alphabet.size) {
                val count = it + 1
                val current = alphabet.take(count)
                val typeVariables = (current.map(Char::toString) + returnTypeParameter).map { name ->
                    TypeVariableName(name)
                }

                val lambdaRaw = LambdaTypeName.get(
                    typeVariables.first(),
                    returnType = typeVariables.last()
                ).toString()

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

                val argumentList = if (count > 1) {
                    (typeVariables.subList(1, typeVariables.size - 1)
                        .map { variable -> variable.name.toLowerCase() } + 'a').joinToString(
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
