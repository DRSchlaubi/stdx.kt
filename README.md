# stdx.kt

This project aims to combine all of my utility projects and util files into a single project

# Modules

| Module                   | Docs                                                 | Artifact          | JVM | JS  | Native |
|--------------------------|------------------------------------------------------|-------------------|-----|-----|--------|
| [core](core)             | [core](https://stdx.schlau.bi/stdx-core)             | `stdx-core`       | ✅   | ✅   | ✅      |
| [coroutines](coroutines) | [coroutines](https://stdx.schlau.bi/stdx-coroutines) | `stdx-coroutines` | ✅   | ✅   | ✅      |
| [envconf](coroutines)    | [envconf](https://stdx.schlau.bi/stdx-envconf)       | `stdx-envconf`    | ✅   | ✅²  | ✅¹     |
| [logging](logging)       | [logging](https://stdx.schlau.bi/stdx-logging)       | `stdx-envconf`    | ✅   | ✅   | ✅¹     |

¹ Except for tvOS, watchOS and iOS
² Only NodeJS

# Download

If you want to add all modules use the `stdx-full` dependency

<details open>
<summary>Gradle (Kotlin)</summary>

```kotlin
dependencies {
    implementation(platform("dev.schlaubi:stdx-bom:1.0.0"))
    // Then for each module
    implementation("dev.schlaubi", "stdx-core")
}
```

</details>

<details>
<summary>Gradle (Groovy)</summary>

```groovy
dependencies {
    implementation platform("dev.schlaubi:stdx-bom:1.0.0")
    // Then for each module
    implementation 'dev.schlaubi:stdx-core'
}
```

</details>

<details>
<summary>Maven</summary>

```xml

<project>
    <dependencies>
        <dependency>
            <groupId>dev.schlaubi</groupId>
            <!--core or any other module -->
            <artifactId>stdx-core-jvm</artifactId>
            <version>1.0.0</version>
        </dependency>
    </dependencies>
</project>
```

</details>

