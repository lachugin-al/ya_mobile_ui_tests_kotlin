package pages

import app.App
import app.Configuration
import com.google.common.collect.ImmutableList
import io.appium.java_client.AppiumDriver
import io.appium.java_client.HasOnScreenKeyboard
import io.appium.java_client.LocksDevice
import io.appium.java_client.MobileElement
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.android.nativekey.AndroidKey
import io.appium.java_client.android.nativekey.KeyEvent
import io.appium.java_client.clipboard.HasClipboard
import org.openqa.selenium.By
import org.openqa.selenium.Dimension
import org.openqa.selenium.Point
import org.openqa.selenium.interactions.Pause
import org.openqa.selenium.interactions.PointerInput
import org.openqa.selenium.interactions.Sequence
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

open class PageObject(val app: App) {
    enum class To {
        TOP,
        BOTTOM,
        LEFT,
        RIGHT
    }

//    protected val app: App = app
    protected val driver: AppiumDriver<MobileElement>? = app.driver

    protected val defaultTimeout: Long = 5
    protected val defaultScrollIterations: Int = 5
    protected val defaultScrollRatio: Double = 0.5

    protected val pollingInterval: Duration = Duration.ofMillis(500)
    protected val scrollDurations: Duration = Duration.ofMillis(1000)

    protected val app_id: String = choiceText("ru.beru.android.qa", "ru.beru.android.qa")

    companion object {
        fun by(android: By, ios: By): By {
            return if (Configuration.isAndroid()) android else ios
        }

        fun choiceText(android: String, ios: String): String {
            return if (Configuration.isAndroid()) android else ios
        }
    }

    /*
 * Elements actions
 */
    fun waitForElement(by: By, timeout: Long = defaultTimeout): MobileElement {
        val wait = WebDriverWait(driver, timeout)
        wait.pollingEvery(pollingInterval)
        wait.withMessage("Element not find: $by\n")
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by)) as MobileElement
    }

    fun waitForElementAndClick(by: By): PageObject {
        waitForElement(by).click()
        return this
    }

    fun waitForElementOrNull(by: By, timeout: Long = defaultTimeout): MobileElement? {
        return try {
            waitForElement(by, timeout)
        } catch (e: Exception) {
            null
        }
    }

    fun waitForOverlappedElement(by: By, timeout: Long = defaultTimeout): MobileElement {
        val wait = WebDriverWait(driver, timeout)
        wait.pollingEvery(pollingInterval)
        wait.withMessage("Element not found: $by\n")
        return wait.until(ExpectedConditions.presenceOfElementLocated(by)) as MobileElement
    }

    fun waitForOverlappedElementOrNull(by: By, timeout: Long = defaultTimeout): MobileElement? {
        return try {
            waitForOverlappedElement(by, timeout)
        } catch (e: Exception) {
            null
        }
    }

    fun waitForElementDisappear(by: By, timeout: Long = defaultTimeout) {
        val wait = WebDriverWait(driver, timeout)
        wait.pollingEvery(pollingInterval)
        wait.withMessage("Element: $by not to be disappeared for $timeout sec.\n")
        wait.until(ExpectedConditions.invisibilityOfElementLocated(by))
    }

    fun waitForElementToBeClickable(by: By): MobileElement {
        val wait = WebDriverWait(driver, defaultTimeout)
        wait.pollingEvery(pollingInterval)
        wait.withMessage("Element $by not to be clickable\n")
        return wait.until(ExpectedConditions.elementToBeClickable(by)) as MobileElement
    }

    fun isElementVisible(element: MobileElement): Boolean {
        return java.lang.Boolean.parseBoolean(element.getAttribute("visible"))
    }

    fun waitForElementAndClickIfExists(by: By, timeout: Long = defaultTimeout) {
        val element = waitForElementOrNull(by, timeout)
        element?.click()
    }

    fun longTapForElement(element: MobileElement, duration: Long = defaultTimeout) {
        val center = element.center
        touchAndMove(center, center, Duration.ofSeconds(duration), Duration.ZERO)
    }

    fun longTapForPoint(point: Point, duration: Long) {
        touchAndMove(point, point, Duration.ofSeconds(duration), Duration.ZERO)
    }

    fun tap(point: Point) {
        touchAndMove(point, point, Duration.ofMillis(100), Duration.ZERO)
    }

    fun tapOverlappedElement(by: By) {
        tap(waitForOverlappedElement(by).center)
    }

    /*
 * Scrolling
 */
    fun scrollUpToElement(by: By, times: Int = defaultScrollIterations): MobileElement? {
        return scrollToElement(by, times, To.TOP)
    }

    fun scrollDownToElement(by: By, times: Int = defaultScrollIterations): MobileElement? {
        return scrollToElement(by, times, To.BOTTOM)
    }

    private fun scrollToElement(by: By, times: Int = defaultScrollIterations, to: To): MobileElement? {
        var element: MobileElement? = waitForElementOrNull(by)
        var iteration = 0
        while (element == null && iteration < times) {
            scrollScreen(to)
            element = waitForElementOrNull(by)
            iteration++
        }
        if (element == null) {
            throw RuntimeException("scrollToElement failed: unable to find $by")
        }
        return element
    }

    fun scrollScreen(to: To, scrollRatio: Double = defaultScrollRatio): PageObject {
        val screenSize = driver!!.manage().window().size
        val scrollAreaSize = Dimension((screenSize.width * scrollRatio).toInt(), (screenSize.height * scrollRatio).toInt())
        val scrollAreaLocation = Point((screenSize.width - scrollAreaSize.width) / 2, (screenSize.height - scrollAreaSize.height) / 2)
        scrollArea(scrollAreaLocation, scrollAreaSize, to)
        return this
    }

    protected fun scrollArea(location: Point, size: Dimension, to: To) {
        val centerX = location.x + size.width / 2
        val centerY = location.y + size.height / 2
        val width = (size.width * 0.99).toInt()
        val height = (size.height * 0.99).toInt()

        var dx = 0
        var dy = 0
        when (to) {
            To.TOP -> dy = 1
            To.BOTTOM -> dy = -1
            To.RIGHT -> dx = -1
            To.LEFT -> dx = 1
        }
        touchAndMove(
            Point(centerX - dx * width / 2, centerY - dy * height / 2),
            Point(centerX + dx * width / 2, centerY + dy * height / 2),
            Duration.ZERO,
            scrollDurations
        )
    }

    /*
 * Additional actions
 */

    fun dragAndDropElement(by: By, targetPoint: Point) {
        touchAndMove(waitForElement(by).center, targetPoint, Duration.ofMillis(500), scrollDurations)
    }

    private fun touchAndMove(start: Point, end: Point, hold: Duration, movement: Duration) {
        val input = PointerInput(PointerInput.Kind.TOUCH, "finger1")
        val swipe = Sequence(input, 0)
        var movement = movement
        swipe.addAction(input.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), start.x, start.y))
        swipe.addAction(input.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
        swipe.addAction(Pause(input, hold))
        swipe.addAction(input.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), start.x, start.y))

        if (Configuration.isiOS()) {
            swipe.addAction(Pause(input, movement))
            movement = Duration.ZERO
        }
        swipe.addAction(input.createPointerMove(movement, PointerInput.Origin.viewport(), end.x, end.y))
        if (Configuration.isAndroid()) {
            swipe.addAction(input.createPointerMove(movement, PointerInput.Origin.viewport(), end.x, end.y))
        }
        swipe.addAction(input.createPointerUp(PointerInput.MouseButton.LEFT.asArg()))
        driver!!.perform(ImmutableList.of(swipe))
        sleep(1.0)
    }

    fun sendText(textField: MobileElement, text: String): PageObject {
        textField.click()
        if (Configuration.isAndroid()) {
            textField.sendKeys(text)
        } else {
            textField.setValue(text)
        }
        return this
    }

    fun getClipboardText(): String {
        return (driver as HasClipboard).clipboardText
    }

    // TODO some additional actions from io.appium.java_client.MobileCommand
    fun isKeyboardShown(): Boolean {
        return (driver as HasOnScreenKeyboard).isKeyboardShown
    }

    fun sleep(seconds: Double): PageObject {
        try {
            Thread.sleep((seconds * 1000).toLong())
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return this
    }

    fun pressNativeBack(): PageObject {
        if (Configuration.isAndroid()) {
            (driver as AndroidDriver<MobileElement>).pressKey(KeyEvent(AndroidKey.BACK))
        }
        return this
    }

    fun deviceScreenOff() {
        (driver as LocksDevice).lockDevice(Duration.ofSeconds(defaultTimeout))
    }

    fun refreshScreen() {
        val size = driver!!.manage().window().size
        val centerX = size.width / 2
        val topY = size.height / 4
        val bottomY = size.height - topY

        touchAndMove(Point(centerX, topY), Point(centerX, bottomY), Duration.ZERO, Duration.ofMillis(300))
    }

    fun terminateApp() {
        driver!!.terminateApp(app_id)
    }

}