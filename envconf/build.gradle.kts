import org.jetbrains.kotlin.gradle.internal.testing.TCServiceMessagesTestExecutionSpec
import org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest
import org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTestFramework
import org.jetbrains.kotlin.gradle.targets.js.testing.mocha.KotlinMocha
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest

plugins {
    `stdx-module`
    `stdx-ktlint`
    `stdx-publishing`
}

val testEnv = mapOf("HELLO" to "HELLO", "PREFIX_HELLO" to "HELLO")

tasks {
    withType<Test> {
        environment(testEnv)
    }

    withType<KotlinJsTest> {
        testFramework = EnvConfJsTestFramework(KotlinMocha(compilation, path))
    }

    withType<KotlinNativeTest> {
        environment = testEnv
    }
}

class EnvConfJsTestFramework(
    private val delegate: KotlinJsTestFramework
) : KotlinJsTestFramework by delegate {
    override fun createTestExecutionSpec(
        task: KotlinJsTest,
        forkOptions: ProcessForkOptions,
        nodeJsArgs: MutableList<String>,
        debug: Boolean
    ): TCServiceMessagesTestExecutionSpec {
        val newProcessForkOptions = forkOptions.apply {
            environment = testEnv
        }
        return delegate.createTestExecutionSpec(task, newProcessForkOptions, nodeJsArgs, debug)
    }
}
