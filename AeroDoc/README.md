# AGDroid AeroDoc: Basic Mobile Application client to AeroDoc backend
---------
Author: Daniel Passos (dpassos)   
Level: Beginner   
Technologies: Java, Android   
Summary: A client of AeroDoc backend   
Target Product: -   
Product Versions: -   
Source: https://github.com/aerogear/aerogear-android-cookbook/tree/master/AeroDoc   

## What is it?

The ```AGDroid AeroDoc``` project demonstrates how to integrate to a backend using Pipe, Auth, Store and Push functionality in Android applications.

## How do I run it?

### 0. System Requirements

* [Java 7](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [Gradle 2.2.1](http://www.gradle.org/)
* Latest [Android SDK](https://developer.android.com/sdk/index.html) and [Platform version 21](http://developer.android.com/tools/revisions/platforms.html)

### 1. Customize Application

The project source code must be customized with the unique metadata assigned to the application variant by the AeroGear UnifiedPush Server and GCM and AeroDoc URL. 

1.1. Open ```/path/to/AeroDoc/app/src/main/java/org/jboss/aerogear/android/cookbook/aerodoc/AeroDocApplication.java``` for editing.

1.2. Enter the application variant values allocated by the AeroGear of UnifiedPush Server and GCM for the following constants:

```java
private static final String BASE_BACKEND_URL = "";

private static final String UNIFIED_PUSH_URL = "";
private static final String GCM_SENDER_ID = "";
private static final String VARIANT_ID = "";
private static final String SECRET = "";
```
1.3. Save the file.

### 2. Build Application

```shell
$ cd /path/to/AeroDoc/
$ gradle clean build
```

### 3. Test Application

To deploy, run and debug the application on an Android device attached to your system, on the command line enter the following:

3.1. Install generated apk to device

```shell
$ cd /path/to/AeroDoc
$ gradle installDebug
```

3.2. Open app on device

Application output is displayed in the command line window.

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

After login the ```AeroDocActivity``` will register device on push server

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
