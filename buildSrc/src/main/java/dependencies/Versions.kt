package dependencies

private object Versions {
    const val androidTargetSdkVersion = 29
    const val androidCompileSdkVersion = 29
    const val androidMinSdkVersion = 21

    private const val versionMajor = 1
    private const val versionMinor = 0
    private const val versionPatch = 5
    private const val versionOffset = 0
    const val androidVersionCode =
        (versionMajor * 10000 + versionMinor * 100 + versionPatch) * 100 + versionOffset

    const val androidVersionName = "$versionMajor.$versionMinor.$versionPatch"
}
