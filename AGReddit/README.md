# AGDroid GDrive: Basic Mobile Application showing the AeroGear Authz (OAuth2) feature on Android
---------
Author: Summers Pittman (supittma)   
Level: Beginner  
Technologies: Java, Android  
Summary: A basic example of data access with Pagination and optional Form based Authentication
Target Product: -   
Product Versions: -   
Source: https://github.com/aerogear/aerogear-android-cookbook/tree/master/AGReddit

## What is it?

The ```AGReddit``` project demonstrates how to access remote data, page through the data, and engage in form based authenticaiton with a remote service.

This simple project consists of a ready-to-build Android application. If you do not have a Reddit account, you can still use the application.  If you create an account on [Reddit](http://www.reddit.com) you can log in and browser your personal front page.


## How do I run it?

### 0. System Requirements

* [Java 7](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [Gradle 2.2](http://www.gradle.org/)
* Latest [Android SDK](https://developer.android.com/sdk/index.html) and [Platform version 21](http://developer.android.com/tools/revisions/platforms.html)

### 1. Building the Application

```shell
$ cd /path/to/AGReddit/
$ gradle clean build
```

### 2. Test Application

To deploy, run and debug the application on an Android device attached to your system, on the command line enter the following:

```shell

1. Install generated apk to device
$ cd /path/to/AGReddit
$ gradle installDebug

2. Open app on device

```

Application output is displayed in the command line window.

## How does it work?


