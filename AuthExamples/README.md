# Mobile app showing the AeroGear Auth feature on Android

Author: Summers Pittman (secondsun)  
Level: Beginner  
Technologies: Java, Android  
Summary: A basic example of HTTP Authentication  
Source: https://github.com/aerogear/aerogear-android-cookbook/tree/master/AuthExamples

## What is it?

_Auth_ project demonstrates how to integrate HTTP Basic or HTTP Digest authentication into applications.

This simple project consists of a simple authenticated REST GET call.

This application is run on an Android device or emulator.  It also requires the [Authentication Server](https://github.com/aerogear/aerogear-backend-cookbook/tree/master/Authentication) be running.

## How do I run it?

### System Requirements

* [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [Gradle](http://www.gradle.org/)
* [Android SDK](https://developer.android.com/sdk/index.html) and [Platform version](http://developer.android.com/tools/revisions/platforms.html)

### Configure

Edit ```org.jboss.aerogear.android.cookbook.authexamples.Constants#URL_BASE``` to be the path of the application.

> This application has an backend implementation in our [Backend Cookbook](https://github.com/aerogear/aerogear-backend-cookbook/tree/master/Authentication) and also an [only version](http://bacon-corinnekrych.rhcloud.com/rest/grocery/bacons)

> In the case of a emulator and server are running on the same machine the URL will be http://10.0.2.2:8080/authentication/rest.

### Build Application

```shell
$ cd /path/to/authExamples/
$ gradle clean build
```

### Running the app

To deploy, run and debug the application on an Android device attached to your system, on the command line enter the following:

```shell
$ cd /path/to/authExamples
$ gradle installDebug
```

## How does it work?

```HowToUseHttpBasicAuthentication``` and ```HowToUseDigestAuthentication``` are two activities which create appropriate ```AuthenticationModules``` and associate them with ```Pipe``` objects.  The UI provides buttons to log in, log out, and fetch data from the Authentication Server.

```java
// Create a Authentication Module
HttpBasicAuthenticationConfiguration authenticationConfig = null;
try {
    authenticationConfig = AuthenticationManager
        .config("login", HttpBasicAuthenticationConfiguration.class)
        .baseURL(new URL(Constants.URL_BASE));
} catch (MalformedURLException e) {
    e.printStackTrace();
}

AuthenticationModule module = authenticationConfig.asModule();

// Create the pipe
RestfulPipeConfiguration pipeConfig = PipeManager
    .config("beer", RestfulPipeConfiguration.class)
    .module(authModule)
    .withUrl(new URL(Constants.URL_BASE + "/grocery/beers"));

Pipe<String> pipe = pipeConfig.forClass(String.class);

//use the pipe as normal
pipe.read(new YourCallback());
```

## Notes
There are two regressions problems to be fixed:

* [AGDROID-349](https://issues.jboss.org/browse/AGDROID-349) - HttpDigest Module needs to be able to disable making a server call on logout
* [AGDROID-350](https://issues.jboss.org/browse/AGDROID-350) - HTTPBasic and HTTPDigest callbacks aren't triggered on the UI thread
