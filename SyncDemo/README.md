# SyncDemo: A basic example to show how AeroGear Sync library works   
---------
Author: Daniel Bevenius (dbeveniu), Summers Pittman (supittma),  Daniel Passos (dpassos)   
Level: Beginner   
Technologies: Java, Android   
Summary: A basic example to show how AeroGear Sync library works   
Source: https://github.com/aerogear/aerogear-android-cookbook/tree/master/SyncDemo   

## What is it?

The ```SyncDemo``` project demonstrates how to use the [AeroGear Sync library](https://github.com/aerogear/aerogear-sync-server) in Android applications.

## How do I run it?

### 0. System Requirements

* [Java 7](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [Gradle 2.2.1](http://www.gradle.org/)
* Latest [Android SDK](https://developer.android.com/sdk/index.html) and [Platform version 21](http://developer.android.com/tools/revisions/platforms.html)

### 1. Customize Application

The project source code must be customized with SyncServer URL

* Open `AndroidManifest` for editing
* Change the serverHost `meta-data`

### 2. Build Application

```shell
$ cd /path/to/SyncDemo/
$ gradle clean build
```

### 3. Test Application

To deploy, run and debug the application on an Android device attached to your system, on the command line enter the following:

* Install generated apk to device

```shell
$ cd /path/to/SyncDemo
$ gradle installDebug
```

* Open app on device

Application output is displayed in the command line window.

## How does it work?

// TODO