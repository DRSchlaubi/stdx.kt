public actual object Platform {
    public actual val isBrowser: Boolean = js(
        "typeof window !== 'undefined' && typeof window.document !== 'undefined' || typeof self !== 'undefined' && typeof self.location !== 'undefined'" // ktlint-disable max-line-length
    ) as Boolean
    public actual val isNode: Boolean = js(
        "typeof process !== 'undefined' && process.versions != null && process.versions.node != null"
    ) as Boolean
    public actual val isJs: Boolean = true
    public actual val isNative: Boolean = false
    public actual val isJvm: Boolean = false
}
