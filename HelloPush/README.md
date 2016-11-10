# Mobile Application showing the AeroGear Push feature on Android

Author: Daniel Passos (dpassos)  
Level: Beginner  
Technologies: Java, Android  
Summary: A basic example of Push : Registration and receiving messages.  
Source: https://github.com/aerogear/aerogear-android-cookbook/tree/master/HelloPush

## What is it?

_HelloPush_ project demonstrates how to include basic push functionality in Android applications.

This simple project consists of a ready-to-build Android application. Before building the application, you must register the Android variant of the application with a running AeroGear UnifiedPush Server instance and Firebase Cloud Messaging for Android. The resulting unique IDs and other parameters must then be inserted into the application source code. After this is complete, the application can be built and deployed to Android devices.

When the application is deployed to an Android device, the push functionality enables the device to register with the running AeroGear UnifiedPush Server instance and receive push notifications.

## How do I run it?

### System Requirements

* [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [Gradle](https://www.gradle.org/downloads)
* [Android SDK](https://developer.android.com/sdk/index.html)
* [Platform](http://developer.android.com/tools/revisions/platforms.html)
* [Google Play Services](http://developer.android.com/google/play-services/index.html)

### Register Application with Push Services

First, you must register the application with Firebase Cloud Messaging for Android and enable access to the Firebase Cloud Messaging for Android APIs and Google APIs. This ensures access to the APIs by the UnifiedPush Server when it routes push notification requests from the application to the FCM. Registering an application with FCM requires that you have a Google account. For information on setting your Google account to use Google’s services, follow the [Google Setup Guide](https://aerogear.org/docs/unifiedpush/aerogear-push-android/guides/#google-setup).

Second, you must register the application and an Android variant of the application with the UnifiedPush Server. This requires a running AeroGear UnifiedPush Server instance and uses the unique metadata assigned to the application by FCM. For information on installing the AeroGear UnifiedPush Server, see the README distributed with the AeroGear UnifiedPush Server or the [UPS guide](https://aerogear.org/docs/unifiedpush/ups_userguide/index/).

1. Log into the UnifiedPush Server console.
1. In the ```Applications``` view, click ```Create Application```.
1. In the ```Name``` and ```Description``` fields, type values for the application and click ```Create```.
1. When created, under the application click ```No variants```.
1. Click ```Add Variant```.
1. In the ```Name``` and ```Description``` fields, type values for the Android application variant.
1. Click ```Android``` and in the ```Server Key``` and ```Sender ID``` fields type the values assigned to the project by FCM.
1. Click ```Add```.
1. When created, expand the variant name and make note of the ```Server URL```, ```Variant ID```, and ```Secret```.

### Customize

The project source code must be customized with the unique metadata assigned to the application variant by the AeroGear UnifiedPush Server and FCM.

1. Download the ```google-services.json``` file from the Firebase Console and place it into the ```/app``` folder.
1. Open ```/path/to/helloworld/android/app/src/main/assets/push-config.json``` for editing.
1. Enter the application variant values allocated by the AeroGear UnifiedPush Server and FCM for the following constants:

    ```js
    {
        "pushServerURL": "pushServerURL (e.g http(s)//host:port/context)",
        "android": {
            "senderID": "senderID (e.g Firebase Sender ID only for android)",
            "variantID": "variantID (e.g. 1234456-234320)",
            "variantSecret": "variantSecret (e.g. 1234456-234320)"
        }
    }
    ```
1. Save the file.

### Running the app

#### Prerequisites

The AeroGear UnifiedPush Server must be running before the application is deployed to ensure that the device successfully registers with the AeroGear UnifiedPush Server on application deployment.

#### Deploy for testing

The application can be tested on physical Android devices only; push notifications are not available for Android emulators. To deploy, run and debug the application on an Android device attached to your system, on the command line enter the following:

```shell
$ cd /path/to/helloworld/android
$ ./gradlew clean build
$ ./gradlew installDebug
```

#### Send a Push Message

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
RegistrarManager.config(PUSH_REGISTER_NAME, AeroGearFCMPushJsonConfiguration.class)
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

Before the usage of FCM notifications on Android, we need to include a few permissions for FCM.

To enable the permissions we add these as child of the manifest element.

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.GET_ACCOUNTS" />
```

And add this element as a child of the application element, to register the default MessageHandler. It will receive all messages and dispatch the message to registered handlers.

```xml
<meta-data 
    android:name="DEFAULT_MESSAGE_HANDLER_KEY"
    android:value="org.jboss.aerogear.unifiedpush.helloworld.handler.NotificationBarMessageHandler" />
```

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

