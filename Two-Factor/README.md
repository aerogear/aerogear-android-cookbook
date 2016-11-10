# Mobile app showing the AeroGear OTP feature on Android

Author: Daniel Passos (dpassos)   
Level: Beginner   
Technologies: Java, Android   
Summary: A basic example how to use OTP   
Source: https://github.com/aerogear/aerogear-android-cookbook/tree/master/Two-Factor

## What is it?

_Two-Factor_ project demonstrates how to include OTP functionality in Android applications.

## How do I run it?

### System Requirements

* [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [Gradlew](http://www../gradlew.org/)
* [Android SDK](https://developer.android.com/sdk/index.html) and [Platform](http://developer.android.com/tools/revisions/platforms.html)

### Build Application

```shell
$ cd /path/to/Two-Factor/
$ ./gradlew clean build
```
### Running the app

To deploy, run and debug the application on an Android device attached to your system, on the command line enter the following:

1. Install generated apk to device

    ```shell
    $ cd /path/to/Two-Factor
    $ ./gradlew installDebug
    ```

1. Open app on device
1. You can use the [browser-authenticator website](https://daplie.github.io/browser-authenticator/) to play with the app.

## How does it work?

```OTPQRCodeActivity``` will invoke the [zxing](https://github.com/journeyapps/zxing-android-embedded)

```java
new IntentIntegrator(this).initiateScan();
```

After [zxing](https://github.com/journeyapps/zxing-android-embedded) finish the ```onActivityResult``` will be called, with a otpauth. It will be passed to OTPDisplay for show the token

```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
    if(result != null) {
        if(result.getContents() != null) {
            Log.d(TAG, result.getContents());
            Intent intent = new Intent(this, OTPCodeActivity.class);
            intent.putExtra("otpauth", result.getContents());
            startActivity(intent);
            finish();
        } else {
            // Cancelled
            finish();
        }
    } else {
        super.onActivityResult(requestCode, resultCode, data);
        showAlertDialog();
    }
}```

```parseOtpPath``` will parse the _secret_  and [Totp](https://github.com/aerogear/aerogear-otp-java/blob/master/src/main/java/org/jboss/aerogear/security/otp/Totp.java) to show generate the token

```java
private void parseOtpPath() {
    String otpauth = getIntent().getStringExtra("otpauth");
    Uri otpUri = Uri.parse(otpauth);

    name = otpUri.getQueryParameter("issuer");
    secret = otpUri.getQueryParameter("secret");

    totp = new Totp(secret);
}
```
