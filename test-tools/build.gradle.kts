plugins {
    `stdx-module`
    `all-platforms`
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
}
