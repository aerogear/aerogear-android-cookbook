# Basic Mobile Application client to AeroDoc backend

Author: Daniel Passos (dpassos)   
Level: Beginner   
Technologies: Java, Android   
Summary: A client of AeroDoc backend   
Target Product: -   
Product Versions: -   
Source: https://github.com/aerogear/aerogear-android-cookbook/tree/master/AeroDoc   

## What is it?

_AeroDoc_ project demonstrates how to integrate to a backend using Pipe, Auth, Store and Push functionality in Android applications.

## How do I run it?

### System Requirements

* [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [Gradle](http://www.gradle.org/)
* [Android SDK](https://developer.android.com/sdk/index.html) and [Platform](http://developer.android.com/tools/revisions/platforms.html)

### Configure

The project source code must be customized with the unique metadata assigned to the application variant by the AeroGear UnifiedPush Server and FCM and AeroDoc URL. 

1. Open ```/path/to/AeroDoc/app/src/main/java/org/jboss/aerogear/android/cookbook/aerodoc/AeroDocApplication.java``` for editing.
1. Enter the application variant values allocated by the AeroGear of UnifiedPush Server and GCM for the following constants:

    ```java
    private static final String BASE_BACKEND_URL = "";

    private static final String UNIFIED_PUSH_URL = "";
    private static final String GCM_SENDER_ID = "";
    private static final String VARIANT_ID = "";
    private static final String SECRET = "";
    ```
1. Save the file.

### Build Application

```shell
$ cd /path/to/AeroDoc/
$ ./gradlew clean build
```

### Running the app

To deploy, run and debug the application on an Android device attached to your system, on the command line enter the following:

1. Install generated apk to device

    ```shell
    $ cd /path/to/AeroDoc
    $ ./gradlew installDebug
    ```

1. Open app on device

## How does it work?

```AeroDocApplication``` is invoked when open the application. The Application life cycle ```onCreate``` is called creating and AuthenticationModule, two pipes and a local Store.

```java
@Override
public void onCreate() {
    super.onCreate();

    configureBackendAuthentication();
    createApplicationPipes();
    createLocalStorage();
}

private void configureBackendAuthentication() {

    try {

        final URL serverURL = new URL(BASE_BACKEND_URL);

        AuthenticationManager
                .registerConfigurationProvider(AeroDocAuthenticationConfiguration.class,
                        new AeroDocAuthenticationConfigurationProvider());

        authenticationModule = AuthenticationManager
                .config("AeroDocAuth", AeroDocAuthenticationConfiguration.class)
                .baseURL(serverURL)
                .asModule();

    } catch (MalformedURLException e) {
        throw new RuntimeException(e);
    }

}

private void createApplicationPipes() {

    try {

        final URL serverURL = new URL(BASE_BACKEND_URL);

        PipeManager.config("lead", RestfulPipeConfiguration.class)
                .withUrl(UrlUtils.appendToBaseURL(serverURL, "/rest/leads"))
                .module(authenticationModule)
                .forClass(Lead.class);

        PipeManager.config("agent", RestfulPipeConfiguration.class)
                .withUrl(UrlUtils.appendToBaseURL(serverURL, "/rest/saleagents"))
                .module(authenticationModule)
                .forClass(SaleAgent.class);

    } catch (MalformedURLException e) {
        throw new RuntimeException(e);
    }

}

private void createLocalStorage() {

    DataManager.config("lead", SQLStoreConfiguration.class)
            .withContext(getApplicationContext())
            .forClass(Lead.class)
            .store();

    localStore = (SQLStore) DataManager.getStore("lead");
    localStore.openSync();

}
```

After login the ```LoginActivity``` will register device on push server

```java
public void registerDeviceOnPushServer(String alias) {

    try {

        RegistrarManager.config("AeroDoc", AeroGearGCMPushConfiguration.class)
                .setPushServerURI(new URI(UNIFIED_PUSH_URL))
                .setSenderIds(GCM_SENDER_ID)
                .setVariantID(VARIANT_ID)
                .setSecret(SECRET)
                .setAlias(alias)
                .setCategories(Arrays.asList("lead"))
                .asRegistrar();

        PushRegistrar registrar = RegistrarManager.getRegistrar("AeroDoc");
        registrar.register(getApplicationContext(), new Callback<Void>() {
            @Override
            public void onSuccess(Void data) {
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    } catch (URISyntaxException e) {
        throw new RuntimeException(e);
    }

}
```
