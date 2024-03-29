import com.squareup.kotlinpoet.FileSpec
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import java.nio.file.Files
import java.nio.file.Path

abstract class AbstractGenerateFilesTask : DefaultTask() {

    @get:Input
    abstract val `package`: Property<String>

    @get:OutputDirectory
    protected val outputDir = project.projectDir
        .toPath()
        .resolvePaths("src")

    protected fun generateFile(
        sourceSet: String,
        fileName: String,
        filter: (CharSequence) -> CharSequence = { it },
        block: FileSpec.Builder.() -> Unit
    ) {
        val file = FileSpec.builder(`package`.get(), fileName)
            .apply(block)
            .addKotlinDefaultImports(includeJvm = false, includeJs = false)
            .addFileComment("This file is generated by Gradle task $name please do not edit it manually")
            .build()

        val builder = StringBuilder()
        file.writeTo(builder)
        val newOutput = filter(builder)

        val out = outputDir
            .resolve("${sourceSet}Generated")
            .resolvePaths(*file.packageName.split('.').toTypedArray())
            .resolve("$fileName.kt")
        Files.createDirectories(out.parent)
        Files.writeString(out, newOutput)
    }

    protected fun Path.resolvePaths(vararg relative: String) =
        relative.toList().fold(this) { parent, currentRelative -> parent.resolve(currentRelative) }
}
