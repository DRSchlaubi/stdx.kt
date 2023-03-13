# envconf
Kotlin library which makes it easy to use env variables for configs

# Examples
```kotlin
// this gets the env variable "HELLO"
val HELLO by Environment

// this gets the env variable "HELLO_COUNT" and converts it into an int
val HELLO_COUNT by getEnv { it.toInt() }

// this does the same as the one above
val COUNT by getEnv("HELLO_") { it.toInt() }

// this does the same as the one above, but it uses 1 if the variable is missing
val COUNT by getEnv("HELLO_", 1) { it.toInt() }

// this does the same as the one above, but it uses null if the variable is missing
val COUNT by getEnv("HELLO_", String::toInt).optional()

class Konfig : Config("HELLO_") {
    // this outsources the prefix to the constructor
    val COUNT by getEnv("HELLO_", 1) { it.toInt() }
}
```

# Download
Please refer to [this](../README.md#download) page

# Documentation
Documentation can be found [here](https://stdx.schlau.bi/stdx-envconf)

# Archived version
You can find the old version here: https://github.com/DRSchlaubi/envconf
