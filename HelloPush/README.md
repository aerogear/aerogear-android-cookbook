# helloworld-push-android: Basic Mobile Application showing the AeroGear Push feature on Android
---------
Author: Daniel Passos (dpassos)  
Level: Beginner  
Technologies: Java, Android  
Summary: A basic example of Push : Registration and receiving messages.  
Target Product: JBoss Mobile Add-On  
Product Versions: 1.0.0  
Source: https://github.com/aerogear/aerogear-push-helloworld/tree/master/android 

## What is it?
The ```helloworld``` project demonstrates how to include basic push functionality in Android applications.

This simple project consists of a ready-to-build Android application. Before building the application, you must register the Android variant of the application with a running AeroGear UnifiedPush Server instance and Google Cloud Messaging for Android. The resulting unique IDs and other parameters must then be inserted into the application source code. After this is complete, the application can be built and deployed to Android devices.

When the application is deployed to an Android device, the push functionality enables the device to register with the running AeroGear UnifiedPush Server instance and receive push notifications.

## How do I run it?

### 0. System Requirements
* [Java 7](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [Gradle 2.2.1](https://www.gradle.org/downloads)
* Latest [Android SDK](https://developer.android.com/sdk/index.html)
* [Platform version 22](http://developer.android.com/tools/revisions/platforms.html)
* [Build Tools 22.0.1](https://developer.android.com/tools/revisions/build-tools.html)
* Latest [Android Support Library](http://developer.android.com/tools/support-library/index.html)
* [Google Play Services](http://developer.android.com/google/play-services/index.html)

### 1. Register Application with Push Services
First, you must register the application with Google Cloud Messaging for Android and enable access to the Google Cloud Messaging for Android APIs and Google APIs. This ensures access to the APIs by the UnifiedPush Server when it routes push notification requests from the application to the GCM. Registering an application with GCM requires that you have a Google account. For information on setting your Google account to use Google’s services, follow the [Google Setup Guide](https://aerogear.org/docs/unifiedpush/aerogear-push-android/guides/#google-setup).

Second, you must register the application and an Android variant of the application with the UnifiedPush Server. This requires a running AeroGear UnifiedPush Server instance and uses the unique metadata assigned to the application by GCM. For information on installing the AeroGear UnifiedPush Server, see the README distributed with the AeroGear UnifiedPush Server or the [UPS guide](https://aerogear.org/docs/unifiedpush/ups_userguide/index/).

1. Log into the UnifiedPush Server console.
2. In the ```Applications``` view, click ```Create Application```.
3. In the ```Name``` and ```Description``` fields, type values for the application and click ```Create```.
4. When created, under the application click ```No variants```.
5. Click ```Add Variant```.
6. In the ```Name``` and ```Description``` fields, type values for the Android application variant.
7. Click ```Android``` and in the ```Google Cloud Messaging Key``` and ```Project Number``` fields type the values assigned to the project by GCM.
8. Click ```Add```.
9. When created, expand the variant name and make note of the ```Server URL```, ```Variant ID```, and ```Secret```.

### 2. Customize and Build Application
The project source code must be customized with the unique metadata assigned to the application variant by the AeroGear UnifiedPush Server and GCM.

1. Open ```/path/to/helloworld/android/app/src/main/assets/push-config.json``` for editing.
2. Enter the application variant values allocated by the AeroGear UnifiedPush Server and GCM for the following constants:
```js
{
  "pushServerURL": "pushServerURL (e.g http(s)//host:port/context)",
  "android": {
    "senderID": "senderID (e.g Google Project ID only for android)",
    "variantID": "variantID (e.g. 1234456-234320)",
    "variantSecret": "variantSecret (e.g. 1234456-234320)"
  }
}
```
3. Save the file.
4. Build and launch
```shell
$ cd /path/to/helloworld/android
$ gradle installDebug
```

### 3. Test Application

#### 0. Prerequisites
The AeroGear UnifiedPush Server must be running before the application is deployed to ensure that the device successfully registers with the AeroGear UnifiedPush Server on application deployment.

#### 1. Deploy for testing
The application can be tested on physical Android devices only; push notifications are not available for Android emulators. To deploy, run and debug the application on an Android device attached to your system, on the command line enter the following:
```shell
$ cd /path/to/helloworld/android
$ gradle clean build
$ gradle installDebug
```

Application output is displayed in the command line window.

#### 2. Send a Push Message
You can send a push notification to your device using the AeroGear UnifiedPush Server console by completing the following steps:

1. Log into the UnifiedPush Server console.
2. Click ```Send Push```.
3. From the ```Applications``` list, select the application.
4. In the ```Messages``` field, type the text to be sent as the push notification.
5. Click ```Send Push Notification```.

![import](../cordova/doc/compose-message.png)

## How does it work?

### Registration

```RegisterActivity``` is invoked right after a successful application login. The Activity life cycle ```onCreate``` is called first invoking the ```register``` method — attempting to register the application to receive push notifications.

```java
RegistrarManager.config(PUSH_REGISTER_NAME, AeroGearGCMPushJsonConfiguration.class)
        .loadConfigJson(getApplicationContext())
        .asRegistrar();

PushRegistrar registrar = RegistrarManager.getRegistrar(PUSH_REGISTER_NAME);
registrar.register(getApplicationContext(), new Callback<Void>() {
    @Override
    public void onSuccess(Void data) {
        Toast.makeText(getApplicationContext(),
            getApplicationContext().getString(R.string.registration_successful),
            Toast.LENGTH_LONG).show();

        Intent intent = new Intent(getApplicationContext(), MessagesActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onFailure(Exception e) {
        Log.e(TAG, e.getMessage());
        Toast.makeText(getApplicationContext(),
                getApplication().getString(R.string.registration_error),
                Toast.LENGTH_LONG).show();
        finish();
    }
});
```

In case of a successful registration with the UnifiedPush Server the application starts the `MessagesActivity`. In case of an error the `onFailure` method is invoked the application shows a message with details of the failure and the application will be closed.

### Receiving Notifications

Before the usage of GCM notifications on Android, we need to include some permissions for GCM and a broadcast receiver to handle push messages from the service.

To enable the permissions we add these as child of the manifest element.

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.GET_ACCOUNTS" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="com.google.android.c2dm.permission.RECEIVE" />

<permission
    android:name="com.mypackage.C2D_MESSAGE"
    android:protectionLevel="signature" />

<uses-permission android:name="org.jboss.aerogear.unifiedpush.helloworld" />
```

And add this element as a child of the application element, to register the default AeroGear Android broadcast receiver. It will receive all messages and dispatch the message to registered handlers.

```xml
<receiver
    android:name="org.jboss.aerogear.android.unifiedpush.AeroGearGCMMessageReceiver"
    android:permission="com.google.android.c2dm.permission.SEND" >
    <intent-filter>
        <action android:name="com.google.android.c2dm.intent.RECEIVE" />
        <category android:name="org.jboss.aerogear.unifiedpush.helloworld" />
    </intent-filter>
</receiver>
```

All push messages are received by an instance of ```AeroGearGCMMessageReceiver```. They are processed and passed to Registrations via the ```notifyHandlers``` method.

The ```NotificationBarMessageHandler``` is able to receive that message and show it in the Notification Bar.

In the ```MessagesActivity``` we need to remove the handler when the Activity goes into the background and re-enable it when it comes into the foreground.

```java
@Override
protected void onResume() {
    super.onResume();
    Registrations.registerMainThreadHandler(this);
    Registrations.unregisterBackgroundThreadHandler(NotificationBarMessageHandler.instance);
}

@Override
protected void onPause() {
    super.onPause();
    Registrations.unregisterMainThreadHandler(this);
    Registrations.registerBackgroundThreadHandler(NotificationBarMessageHandler.instance);
}
```

