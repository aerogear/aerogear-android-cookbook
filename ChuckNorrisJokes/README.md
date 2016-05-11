# AGDroid Chuck Norris Jokes: Basic Mobile Application showing the AeroGear Pipe feature on Android
---------
Author: Daniel Passos (dpassos)
Level: Beginner   
Technologies: Java, Android   
Summary: A basic example of Pipe
Target Product: -   
Product Versions: -   
Source: https://github.com/aerogear/aerogear-android-cookbook/tree/master/ChuckNorrisJokes

## What is it?

The ```AGDroid Chuck Norris Jokes``` project demonstrates how to include retrive data from a REST endpoint

When the application is deployed to an Android device, the application will retrieve a Chuck Norris joke

## How do I run it?

### 0. System Requirements

* [Java 7](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [Gradle 2.2.1](http://www.gradle.org/)
* Latest [Android SDK](https://developer.android.com/sdk/index.html) and [Platform version 23](http://developer.android.com/tools/revisions/platforms.html)

### 1. Build Application

```shell
$ cd /path/to/app
$ gradle clean build
```

### 2. Test Application

To deploy, run and debug the application on an Android device attached to your system, on the command line enter the following:

2.1) Install generated apk to device

```shell
$ cd /path/to/app
$ gradle installDebug
```

2.2) Open app on device

Application output is displayed in the command line window.

## How does it work?


```ChuckNorrisJokesApplication``` is invoked when open the application. The Application life cycle ```onCreate``` is called creating a [Pipe](https://aerogear.org/docs/guides/aerogear-android/pipe/) instance.

```java
ResponseParser responseParser = new GsonResponseParser();
responseParser.getMarshallingConfig().setDataRoot("value");

PipeManager.config("chuckNorris", RestfulPipeConfiguration.class)
        .withUrl(new URL("http://api.icndb.com/jokes/random/"))
        .responseParser(responseParser)
        .forClass(Joke.class);

```
After that ```ChuckNorrisJokesActivity``` is invoked. The Activity life cycle ```onCreate``` is called first invoking the retrieveJoke method — attempting to retrive the joke from a REST endpoint.

```java
LoaderPipe<Joke> pipe = application.getPipe(this);
pipe.reset();
pipe.read(new AbstractActivityCallback<List<Joke>>() {
    @Override
    public void onSuccess(List<Joke> jokes) {
        // Show the Joke
    }

    @Override
    public void onFailure(Exception e) {
        // Notify  error
    }
});
```
