# AGDroid CarStore: Basic Mobile Application showing the AeroGear Store feature on Android
---------
Author: Daniel Passos (dpassos) & Summers Pittman (supittma)   
Level: Beginner   
Technologies: Java, Android   
Summary: A basic example of Store : CRUD   
Target Product: -   
Product Versions: -   
Source: https://github.com/aerogear/aerogear-android-cookbook/tree/master/CarStore   

## What is it?

The ```AGDroid CarStore``` project demonstrates how to include Store functionality in Android applications.

This simple project consists in CRUD for Android application.

When the application is deployed to an Android device, the application will show all previous data saved

## How do I run it?

### 0. System Requirements

* [Java 7](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [Gradle 2.2.1](http://www.gradle.org/)
* Latest [Android SDK](https://developer.android.com/sdk/index.html) and [Platform version 21](http://developer.android.com/tools/revisions/platforms.html)

### 1. Build Application

```shell
$ cd /path/to/carStore/
$ gradle clean build
```

### 2. Test Application

To deploy, run and debug the application on an Android device attached to your system, on the command line enter the following:

2.1) Install generated apk to device

```shell
$ cd /path/to/carStore
$ gradle installDebug
```

2.2) Open app on device

Application output is displayed in the command line window.

## How does it work?

```CarStoreApplication``` is invoked when open the application. The Application life cycle ```onStart``` is called creating and opening the Car Store database instance.

```java
DataManager.config("carStore", SQLStoreConfiguration.class)
        .withContext(getApplicationContext())
        .forClass(Car.class)
        .store();

store = (SQLStore) DataManager.getStore("carStore");
store.openSync();
```
After that the ```CarStoreActivity``` will be invoked. The Activity life cycle ```onResume``` is called first retriving all data

```java
Collection<Car> cars = storeApplication.getStore().readAll();
```
