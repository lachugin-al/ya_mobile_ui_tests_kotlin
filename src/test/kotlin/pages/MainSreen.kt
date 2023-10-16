package pages

import app.App
import org.openqa.selenium.By

class MainScreen(app: App): PageObject(app) {

    val exampleButton = by(
        By.id("loginButton"),
        By.id("")
    )

    val closeButton = by(
        By.id("closeButton"),
        By.id("null")
    )

    val navMainButton = by(
        By.id("nav_main"),
        By.id("null")
    )

    val navCatalogButton = by(
        By.id("nav_catalog"),
        By.id("null")
    )

    val navProductsButton = by(
        By.id("nav_products"),
        By.id("null")
    )

    val navCartButton = by(
        By.id("nav_cart"),
        By.id("null")
    )

    val navProfileButton = by(
        By.id("nav_profile"),
        By.id("null")
    )
}