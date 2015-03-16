# Shoot and Share: Basic Mobile Application showing AeroGear Pipe and Authorization with multiple service providers
---------
Authors: Summers Pittman (secondsun) - Daniel Passos (dpassos)   
Level: Advanced   
Technologies: Java, Android, Google, Facebook, KeyCloak   
Summary: And example of interacting with several modern web services.   
Target Product: -   
Product Versions: -   
Source: https://github.com/aerogear/aerogear-android-cookbook/tree/master/ShootAndShare   

## What is it?

The ```Shoot and Share``` project demonstrates how to authenticate with Google, KeyCloak, and Facebook as well as how to upload pictures to Google Drive, your Facebook Wall, or a KeyCloak protected web service.

This application also has an [iOS port](https://github.com/aerogear/aerogear-ios-cookbook/tree/master/Shoot) and a [companion Server](https://github.com/aerogear/aerogear-backend-cookbook/tree/master/Shoot) to host keycloak.

## How do I run it?

### 1. System Requirements

* [Java 7](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [Gradle 2.2.1](http://www.gradle.org/)
* Latest [Android SDK](https://developer.android.com/sdk/index.html) and [Platform version 21](http://developer.android.com/tools/revisions/platforms.html)

For the complete instructions about how to setup Google, Facebook or Keycloak credentials, visit our [OAuth2 documentation guide](https://aerogear.org/docs/guides/security/oauth2-guide/#_before_you_get_started)

### 2. Build Application

```shell
$ cd /path/to/app
$ gradle clean build
```

### 3. Test Application

To deploy, run and debug the application on an Android device attached to your system, on the command line enter the following:

5.1) Install generated apk to device

```shell
$ cd /path/to/app
$ gradle installDebug
```

5.2) Open app on device
Application output is displayed in the command line window.

## How does it work?

`CameraActivity` is responsible for taking pictures and saving them locally.  After you take a picture an intent will launch `PhotoActivity`.

`PhotoActivity` is the sharing UI.  In this class the calls are made to connect to the remote services using helper classes and uploads photos using an intent to start a background service.

`GooglePlusHelper`, `FacebookHelper`, and `KeycloakHelper` are utility classes which connect to remote services using `aerogear-android-authz` and upload photos to these services using `aerogear-android-pipe`.

`UploadService` performs the upload in the background so the UI is not blocked.
