/**
 * Allows retrieving Platform specific information.
 */
public expect object Platform {
    /**
     * Whether this test runs in the browser.
     */
    public val isBrowser: Boolean

    /**
     * Whether this test runs in NodeJS.
     */
    public val isNode: Boolean

    /**
     * Whether this test runs in JS.
     */
    public val isJs: Boolean

    /**
     * Whether this test runs in a native target.
     */
    public val isNative: Boolean

    /**
     * Whether this test runs on the JVM.
     */
    public val isJvm: Boolean
}
