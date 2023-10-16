package uitests

import app.App
import app.AppLauncher
import org.junit.jupiter.api.Test
import pages.MainScreen

class MainScreenTest {
    private var app: App? = null

    @Test
    fun checkOpenApp() {
        app = AppLauncher().launch()
        val mainScreen = MainScreen(app!!)
        mainScreen.waitForElementAndClickIfExists(mainScreen.closeButton)
        mainScreen.waitForElementAndClick(mainScreen.navMainButton)
            .waitForElementAndClick(mainScreen.navCatalogButton)
            .sleep(5.0)
            .pressNativeBack()
            .waitForElementAndClick(mainScreen.navCartButton)
            .pressNativeBack()
            .waitForElementAndClick(mainScreen.navProfileButton)
    }
}