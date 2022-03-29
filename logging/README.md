# logging

Extensions to [kotlin-logging](https://github.com/MicroUtils/kotlin-logging/)

# Examples
## InlineLogger
```kotlin
private val LOG = KotlinLogging.logger {}

suspend fun main() {
    LOG.debug {
        delay(1.minutes)
        "Delayed LOG message"
    }
}
```

# Download
Please refer to [this](../README.md#download) page

# Documentation
Documentation can be found [here](https://stdx.schlau.bi/logging)
