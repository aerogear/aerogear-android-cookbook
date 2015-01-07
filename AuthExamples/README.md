# AGDroid Authentication Examples: HTTP Basic and Digest Authentication for Aerogear
---------
Author: Summers Pittman (secondsun)  
Level: Beginner  
Technologies: Java, Android  
Summary: A basic example of HTTP Authentication  
Target Product: -  
Product Versions: -  
Source: https://github.com/aerogear/aerogear-android-cookbook/tree/master/AuthExamples  

## What is it?

The ```AGDroid Auth Examples``` project demonstrates how to integrate HTTP Basic or HTTP Digest authentication into applications.

This simple project consists of a simple authenticated REST GET call.

This application is run on an Android device or emulator.  It also requires the [Authentication Server](https://github.com/aerogear/aerogear-backend-cookbook/tree/master/Authentication) be running.

## How do I run it?

### 0. System Requirements

* [Java 7](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [Gradle 2.2.1](http://www.gradle.org/)
* Latest [Android SDK](https://developer.android.com/sdk/index.html) and [Platform version 21](http://developer.android.com/tools/revisions/platforms.html)
* [Authentication Server](https://github.com/aerogear/aerogear-backend-cookbook/tree/master/Authentication)
* [Maven 3.1 or greater](http://maven.apache.org/download.cgi)

### 1. Run Server

```shell
$ cd /path/to/authServer/
$ mvn clean install wildfly:run
```

### 2. Configure Application

Edit org.jboss.aerogear.android.cookbook.authexamples.Constants#URL_BASE to be the path of the application.

In the case of a emulator and server running on the same machine the URL will be http://10.0.2.2:8080/authentication/rest.

### 3. Build Application

```shell
$ cd /path/to/authExamples/
$ gradle clean build
```



### 4. Test Application

To deploy, run and debug the application on an Android device attached to your system, on the command line enter the following:

```shell
$ cd /path/to/authExamples
$ gradle installDebug
```

Now just run the app on your device/emulator

## How does it work?

```HowToUseHttpBasicAuthentication``` and ```HowToUseDigestAuthentication``` are two activities which create appropriate ```AuthenticationModules``` and associate them with ```Pipe``` objects.  The UI provides buttons to log in, log out, and fetch data from the Authentication Server.

```java
//create a Authentication Module
//see HowToUseHttpBasicAuthentication.java#L79
    HttpBasicAuthenticationConfiguration authenticationConfig = null;
    try {
        authenticationConfig = AuthenticationManager.config("login", HttpBasicAuthenticationConfiguration.class)
                .baseURL(new URL(Constants.URL_BASE));
    } catch (MalformedURLException e) {
        e.printStackTrace();
    }

    AuthenticationModule module = authenticationConfig.asModule();
//create the pipe
//see HowToUseHttpBasicAuthentication.java#L91
    RestfulPipeConfiguration pipeConfig = PipeManager.config("beer", RestfulPipeConfiguration.class)
        .module(authModule)
        .withUrl(new URL(Constants.URL_BASE + "/grocery/beers"));

    Pipe<String> pipe = pipeConfig.forClass(String.class);
///use the pipe as normal
    pipe.read(new YourCallback());

```

## Form Based Authenticator
[AGReddit](../AGReddit) provides an example of custom Authentication using the Authentication libraries.

## Notes
There are two regressions to note for the 2.0.0 release with fixes scheduled for the 2.1.0 release.

### Http Digest Authentication onLogout errors
[This JIRA](https://issues.jboss.org/browse/AGDROID-349) is tracking the fix.  Digest Authentication log outs are not required to make a server call.  In cases where the server doesn't support a logout method (such as this) authentication will be cleared but the http request will fail.

### Callbacks happen off the main thread
[This JIRA](https://issues.jboss.org/browse/AGDROID-350) is tracking the fix.  In 1.x we use Loaders to wrap callbacks and ensure methods were called on the main thread.  In 2.0 we have removed Loaders from auth.
