@file:OptIn(ExperimentalWasmInterop::class, UnsafeWasmMemoryApi::class)

package dev.schlaubi.envconf

import kotlin.wasm.unsafe.*

@WasmImport("wasi_snapshot_preview1", "environ_sizes_get")
private external fun wasiEnvironSizesGet(environCount: UInt, environBufSize: UInt): Int

@WasmImport("wasi_snapshot_preview1", "environ_get")
private external fun wasiEnvironGet(environ: UInt, environBuf: UInt): Int

@PublishedApi
internal var environmentMap: Map<String, String?>? = null

internal inline fun <R> loadEnvironmentIfNeeded(block: Map<String, String?>.() -> R): R {
    if (environmentMap == null) {
        readEnvironmentVariables()
    }
    @Suppress("ReplaceNotNullAssertionWithElvisReturn")
    return environmentMap!!.block()
}

// This function is adapted from: https://github.com/luca992/getenv-kt/blob/main/getenv/src/wasmWasiMain/kotlin/io/getenv.wasmWasi.kt
private fun readEnvironmentVariables() = withScopedMemoryAllocator { allocator ->
    val environCountPtr = allocator.allocate(4)
    val environBufSizePtr = allocator.allocate(4)
    val resultSizes = wasiEnvironSizesGet(environCountPtr.address, environBufSizePtr.address)
    if (resultSizes != 0) error("wasi error code: $resultSizes")

    val environCount = environCountPtr.loadInt()
    val environBufSize = environBufSizePtr.loadInt()

    val environPtrs = allocator.allocate(environCount * 4)
    val environBuf = allocator.allocate(environBufSize)

    val resultEnviron = wasiEnvironGet(environPtrs.address, environBuf.address)
    if (resultEnviron != 0) error("wasi error code: $resultEnviron")

    // Parse the environ buffer and split into environmentMap variables
    val envVars = ArrayList<String>(environCount)
    var currentVar = StringBuilder()

    for (i in 0 until environBufSize) {
        val byte = (environBuf + i).loadByte()
        if (byte.toInt() == 0) {
            envVars.add(currentVar.toString())
            currentVar = StringBuilder()
        } else {
            currentVar.append(byte.toInt().toChar())
        }
    }

    environmentMap = envVars.associate {
        val (key, value) = it.split('=', limit = 2)
        key to value
    }
}
