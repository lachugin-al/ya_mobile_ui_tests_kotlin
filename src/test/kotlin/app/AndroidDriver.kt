package app

import io.appium.java_client.AppiumDriver
import io.appium.java_client.MobileElement
import io.appium.java_client.remote.AndroidMobileCapabilityType
import io.appium.java_client.remote.AutomationName
import io.appium.java_client.remote.MobileCapabilityType
import org.openqa.selenium.Platform
import org.openqa.selenium.SessionNotCreatedException
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.remote.DesiredCapabilities
//import utils.AndroidDeviceUtils
import utils.Constants
//import utils.TerminalUtils
import java.io.File
import java.net.MalformedURLException
import java.net.URL

class AndroidDriver(private val autoLaunch: Boolean) {
    fun getAndroidDriver(retryCount: Int): AppiumDriver<MobileElement> {
        try {
            return io.appium.java_client.android.AndroidDriver(URL("http://localhost:4723/wd/hub"), getCapabilities())
        } catch (e: SessionNotCreatedException) {
            e.printStackTrace()
            if (retryCount > 0) {
                println("Failed to init Android driver")
//                AndroidDeviceUtils.checkDeviceAndRestartIfNeeded()
//                TerminalUtils.pushApkToEmulator()
                return getAndroidDriver(retryCount - 1)
            } else {
                throw RuntimeException("Failed to init Android driver. Please check if an emulator is running", e)
            }
        } catch (e: WebDriverException) {
            throw RuntimeException("Failed to init Android driver. Please check if Appium is running", e)
        } catch (e: MalformedURLException) {
            throw RuntimeException("Failed to init Android driver", e)
        }
    }

    fun getCapabilities(): DesiredCapabilities {
        val appFile = File(Constants.ANDROID_APP)
        if (!appFile.exists()) {
            throw RuntimeException("No ${Constants.ANDROID_APP} at project root.\n" +
                    "Please build App for android, and copy APK to ${appFile.absolutePath}")
        }
        val capabilities = DesiredCapabilities()
        capabilities.setCapability(MobileCapabilityType.APP, appFile.absolutePath)
        capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, AutomationName.ANDROID_UIAUTOMATOR2)
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, Platform.ANDROID.toString().toLowerCase())
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "Android Emulator")
        capabilities.setCapability(MobileCapabilityType.NO_RESET, true)
        capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, Configuration.getAndroidVersion())
        capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 100)
        capabilities.setCapability(AndroidMobileCapabilityType.UNICODE_KEYBOARD, true)
        capabilities.setCapability(AndroidMobileCapabilityType.ADB_EXEC_TIMEOUT, 40000)
        capabilities.setCapability(AndroidMobileCapabilityType.AUTO_GRANT_PERMISSIONS, true)
        capabilities.setCapability("autoLaunch", autoLaunch)
        capabilities.setCapability("settings[enableMultiWindows]", true)
        capabilities.setCapability("settings[mjpegServerScreenshotQuality]", 100)
        capabilities.setCapability("settings[mjpegScalingFactor]", 100)
        return capabilities
    }
}