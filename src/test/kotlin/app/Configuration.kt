package app

import kotlin.IllegalArgumentException

object Configuration {
    private enum class Platform {
        ANDROID, IOS
    }

    private var platform = Platform.ANDROID
    private var androidVersion = "11"
    private var iosVersion = "16.4"
    private var iosDeviceName = "iPhone 14"

    fun isAndroid(): Boolean {
        return platform == Platform.ANDROID
    }

    fun isiOS(): Boolean {
        return platform == Platform.IOS
    }

    fun getAndroidVersion(): String {
        return androidVersion
    }

    fun getIosVersion(): String {
        return iosVersion
    }

    fun getIosDeviceName(): String {
        return iosDeviceName
    }

    init {
        val newPlatform = System.getProperty("platform")
        if (newPlatform != null && newPlatform.isNotEmpty()) {
            when (newPlatform.lowercase()) {
                "ios" -> platform = Platform.IOS
                "android" -> platform = Platform.ANDROID
                else -> throw IllegalArgumentException("Unknown platform: $newPlatform")
            }
        }

        val newAndroidVersion = System.getProperty("androidVersion")
        if (newAndroidVersion != null && newAndroidVersion.isNotEmpty()) {
            androidVersion = newAndroidVersion
        }

        val newIosVersion = System.getProperty("iosVersion")
        if (newIosVersion != null && newIosVersion.isNotEmpty()) {
            iosVersion = newIosVersion
        }

        val newIosDeviceName = System.getProperty("iosDeviceName")
        if (newIosDeviceName != null && newIosDeviceName.isNotEmpty()) {
            iosDeviceName = newIosDeviceName
        }
    }
}
