# AGDroid Two-Factor: Basic Mobile Application showing the AeroGear OTP feature on Android
---------
Author: Daniel Passos (dpassos)
Level: Beginner
Technologies: Java, Android
Summary: A basic example how to use OTP
Source: https://github.com/aerogear/aerogear-android-cookbook/tree/master/Two-Factor

## What is it?

The ```AGDroid Two-Factor``` project demonstrates how to include OTP functionality in Android applications.

## How do I run it?

### 0. System Requirements

* [Java 7](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [Gradle 2.2.1](http://www.gradle.org/)
* Latest [Android SDK](https://developer.android.com/sdk/index.html) and [Platform version 22](http://developer.android.com/tools/revisions/platforms.html)

### Configuring a testing server

**Note**: The server side configuration is not mandatory for testing OTP with Android.

### Configuring a testing server

1. Follow directions to install [OTP-Demo](https://github.com/aerogear/aerogear-backend-cookbook/blob/master/OTP-demo/README.md)
1. Open OTP backend app [http://localhost:8080/otp-demo](http://localhost:8080/otp-demo)
1. Login with username: *user* and password: *password*.
1. Now open this "OTP client application" on your phone
1. Then scan the *Scan QRCode*
1. Enter the current OTP on your mobile into the form

For more details, please refer to our [documentation](http://aerogear.org/docs/specs/aerogear-security-otp/)

### 1. Build Application

```shell
$ cd /path/to/Two-Factor/
$ gradle clean build
```
### 2. Test Application

To deploy, run and debug the application on an Android device attached to your system, on the command line enter the following:

2.1) Install generated apk to device

```shell
$ cd /path/to/Two-Factor
$ gradle installDebug
```

2.2) Open app on device

Application output is displayed in the command line window.

## How does it work?

```OTPQRCodeActivity``` will invoke the [Barcode Scanner](https://play.google.com/store/apps/details?id=com.google.zxing.client.android)

```java
Intent intent = new Intent("com.google.zxing.client.android.SCAN");
intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
startActivityForResult(intent, requestCode);
```

After [Barcode Scanner](https://play.google.com/store/apps/details?id=com.google.zxing.client.android) finish the ```onActivityResult``` will be called, with a otpauth. It will be passed to OTPDisplay for show the token

```java
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == this.requestCode) {
        if (resultCode == RESULT_OK) {
            String otpauth = data.getStringExtra("SCAN_RESULT");
            Intent intent = new Intent(this, OTPDisplay.class);
            intent.putExtra("otpauth", otpauth);
            startActivity(intent);
            finish();
        }
    }
}
```

```parseOtpPath``` will parse the _secret_  and [Totp](https://github.com/aerogear/aerogear-otp-java/blob/master/src/main/java/org/jboss/aerogear/security/otp/Totp.java) to show generate the token

```java
private void parseOtpPath() {
    String otpauth = getIntent().getStringExtra("otpauth");
    Uri otpUri = Uri.parse(otpauth);

    name = otpUri.getQueryParameter("");
    secret = otpUri.getQueryParameter("secret");

    totp = new Totp(secret);
}
```
