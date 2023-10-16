package app

import io.appium.java_client.AppiumDriver
import io.appium.java_client.MobileElement
import io.appium.java_client.ios.IOSDriver
import io.appium.java_client.remote.AutomationName
import io.appium.java_client.remote.IOSMobileCapabilityType
import io.appium.java_client.remote.MobileCapabilityType
import org.openqa.selenium.Platform
import org.openqa.selenium.SessionNotCreatedException
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.remote.DesiredCapabilities
import utils.Constants
import java.io.File
import java.io.IOException
import java.net.URL
import java.util.*

class IosDriver(private val autoLaunch: Boolean) {
    fun getIOSDriver(retryCount: Int): AppiumDriver<MobileElement> {
        try {
            return IOSDriver(URL("http://localhost:4723/wd/hub"), getCapabilities())
        } catch (e: SessionNotCreatedException) {
            e.printStackTrace()
            if (retryCount > 0) {
                println("Failed to init iOS driver. Retry")
                return getIOSDriver(retryCount - 1)
            } else {
                throw RuntimeException("Failed to init iOS driver. Please check platform version and device name.\n" +
                        "To see available simulators run 'xcrun simctl list devices available'", e)
            }
        } catch (e: WebDriverException) {
            throw RuntimeException("Failed to init iOS driver. Please check if Appium is running", e)
        } catch (e: IOException) {
            throw RuntimeException("Failed to init iOS driver", e)
        }
    }

    fun getCapabilities(): DesiredCapabilities {
        val appFile = File(Constants.IOS_APP)
        if (!appFile.exists()) {
            throw RuntimeException("No ${Constants.IOS_APP} at project root.\n" +
                    "Please build App for android, and copy APP to ${appFile.absolutePath}")
        }

        val capabilities = DesiredCapabilities()
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, Platform.IOS)
        capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, Configuration.getIosVersion())
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, Configuration.getIosDeviceName())
        capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, AutomationName.IOS_XCUI_TEST)
        capabilities.setCapability(MobileCapabilityType.APP, appFile.absolutePath)
        capabilities.setCapability(IOSMobileCapabilityType.CONNECT_HARDWARE_KEYBOARD, false)
        capabilities.setCapability(IOSMobileCapabilityType.AUTO_ACCEPT_ALERTS, true)
        capabilities.setCapability(IOSMobileCapabilityType.SHOW_IOS_LOG, false)
        capabilities.setCapability("settings[mjpegServerScreenshotQuality]", 100)
        capabilities.setCapability("appium:screenshotQuality", 0)
        capabilities.setCapability("appium:autoLaunch", autoLaunch)

        val processArguments = HashMap<String, Array<String>>()
        capabilities.setCapability(IOSMobileCapabilityType.PROCESS_ARGUMENTS, processArguments)
        capabilities.setCapability("settings[customSnapshotTimeout]", 3)
        return capabilities
    }
}