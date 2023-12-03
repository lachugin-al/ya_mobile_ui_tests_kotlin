# Mobile UI Tests on Kotlin

This project performs E2E tests for a test application on Android and iOS platforms. Appium is used as the framework.
Project Structure

## Project Structure

All test code is located in the src/test/java folder:

* app - classes for connecting to the application, common to all test cases:
    * Configuration - contains launch parameters: platform (Android, iOS).
    * AppLauncher - the main class, responsible for launching the application with various parameters.
    * App - the launched application class. Contains the driver.
    * AndroidDriver, IosDriver - driver settings for platforms (Android, iOS).
* pages - page object classes
* uitests - E2E UI test classes
* utils - utility classes

#### Local Test Execution

1. Clone the repository;
2. Launch Android Studio and open the project folder;
3. Place the application version (example: android.apk) in the root folder of the project.
4. Launch a virtual device;
5. Start Appium in the terminal (appium server -p 4723 -a 127.0.0.1 -pa /wd/hub).
6. Select the tests you want to run.