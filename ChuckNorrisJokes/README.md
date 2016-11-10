# Mobile app showing the AeroGear Pipe feature on Android

Author: Daniel Passos (dpassos)
Level: Beginner
Technologies: Java, Android
Summary: A basic example of Pipe
Source: https://github.com/aerogear/aerogear-android-cookbook/tree/master/ChuckNorrisJokes

## What is it?

The _Chuck Norris Jokes_ project demonstrates how to include retrive data from a REST endpoint

When the application is deployed to an Android device, the application will retrieve a Chuck Norris joke

## How do I run it?

### System Requirements

* [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [Gradle](http://www.gradle.org/)
* [Android SDK](https://developer.android.com/sdk/index.html) and [Platform](http://developer.android.com/tools/revisions/platforms.html)

### Build Application

```shell
$ cd /path/to/app
$ ./gradlew clean build
```

### Running the app

To deploy, run and debug the application on an Android device attached to your system, on the command line enter the following:

1. Install generated apk to device

    ```shell
    $ cd /path/to/app
    $ ./gradlew installDebug
    ```
1. Open app on device

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
