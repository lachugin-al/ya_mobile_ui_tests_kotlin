# Mobile UI Tests on Kotlin

Данный проект выполняет E2E тесты для тестового приложения на платформах Android и iOS. В качестве фреймворка используется Appium.

## Структура проекта

Весь код тестов находится в папке src/test/java:

* app - классы для подключения к приложению, общие для всех тест-кейсов:
    * Configuration - содержит параметры запуска: платформу (Android, iOS).
    * AppLauncher - главный класс, отвечает за запуск приложения с различными параметрами.
    * App - класс запущенного приложения. Содержит в себе драйвер.
    * AndroidDriver, IosDriver - настройки драйверов по платформам (Android, iOS).
* pages - классы page object-ов
* uitests - классы E2E UI тестов
* utils - утилитарные классы

#### Локальный запуск тестов

1. Склонируйте репозиторий;
2. Запустите Android Studio, откройте папку проекта;
3. Положите версию приложения (пример: android.apk) в корневую папку проекта.
4. Запустите виртуальный девайс;
5. Запустите Appium в терминале (appium server -p 4723 -a 127.0.0.1 -pa /wd/hub).
6. Выбертите тесты которые хотите запустить