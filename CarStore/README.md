# Mobile app showing the AeroGear Store feature on Android

Author: Daniel Passos (dpassos)   
Level: Beginner   
Technologies: Java, Android   
Summary: A basic example of Store : CRUD   
Source: https://github.com/aerogear/aerogear-android-cookbook/tree/master/CarStore   

## What is it?

The _CarStore_ project demonstrates how to include Store functionality in Android applications.

This simple project consists in CRUD for Android application.

When the application is deployed to an Android device, the application will show all previous data saved

## How do I run it?

### System Requirements

* [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [Gradle](http://www.gradle.org/)
* [Android SDK](https://developer.android.com/sdk/index.html) and [Platform](http://developer.android.com/tools/revisions/platforms.html)

### Build Application

```shell
$ cd /path/to/carStore/
$ gradle clean build
```

### Running the app

To deploy, run and debug the application on an Android device attached to your system, on the command line enter the following:

1. Install generated apk to device

    ```shell
    $ cd /path/to/carStore
    $ gradle installDebug
    ```
1. Open app on device

## How does it work?

```CarStoreActivity``` is invoked when open the application. The Application life cycle ```onStart``` is called creating and opening the Car Store database instance.

```java
carStore = (SQLStore<Car>) DataManager.config("carStore", SQLStoreConfiguration.class)
                .withContext(getApplicationContext())
                .store(Car.class);
carStore.openSync();                
```

After that the ```CarStoreActivity``` will be invoked. The Activity life cycle ```onResume``` is called first retriving all data

```java
Collection<Car> cars = storeApplication.getStore().readAll();
```
