# Password Manager: Mobile Application to store your passwords in an Encrypted Store
---------
Author: Daniel Passos (dpassos)   
Level: Beginner   
Technologies: Java, Android   
Summary: A basic example of Encrypted Store   
Target Product: -   
Product Versions: -   
Source: https://github.com/aerogear/aerogear-android-cookbook/tree/master/PasswordManager   

## What is it?

The ```AGDroid Password Manager``` project demonstrates how to include Encrypted Store functionality in Android applications.

## How do I run it?

### 0. System Requirements

* [Java 7](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [Gradle 2.2.1](http://www.gradle.org/)
* Latest [Android SDK](https://developer.android.com/sdk/index.html) and [Platform version 21](http://developer.android.com/tools/revisions/platforms.html)

### 1. Build Application

```shell
$ cd /path/to/PasswordManager/
$ gradle clean build
```

### 2. Test Application

To deploy, run and debug the application on an Android device attached to your system, on the command line enter the following:

2.1) Install generated apk to device

```shell
$ cd /path/to/PasswordManager
$ gradle installDebug
```

2.2) Open app on device

Application output is displayed in the command line window.

## How does it work?

When application start, the ```PasswordManagerActivity``` will ask a passworkd and call ```PasswordManagerApplication.createStore```. 

if it's the first invoke time, the ```DataManager``` will create a new Encrypted Store using this password, if not it will try open the Encrypted database using that password. If this password is wrong, it will throw an ```InvalidKeySpecException```.

```java
store = (EncryptedSQLStore<Credential>) DataManager.config("pwdStore", EncryptedSQLStoreConfiguration.class)
        .withContext(getApplicationContext())
        .usingPassphrase(passphrase)
        .forClass(Credential.class)
        .store();

store.openSync();
```

