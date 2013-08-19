# AeroGear Android Cookbook README

The cookbook application contains an interactive version of the code samples found in the [online documentation](http://aerogear.org/docs/guides/aerogear-android/).  

# Building and Installing

## Prerequisites

* Maven 3.0.x 
* Android SDK
* Git
* maven-android-deployer

## Downloading and building

The Cookbook is a Maven project, but the Android dependencies are not in Maven Central. We must deploy them locally using the [Maven Android SDK Deployer](https://github.com/mosabua/maven-android-sdk-deployer).  The [README](https://github.com/mosabua/maven-android-sdk-deployer/blob/master/README.markdown) for the deployer will cover getting it installed and configured.

Once the deployer has finished you can clone and build the project.

````bash
git clone https://github.com/aerogear/aerogear-android-cookbook;
cd aerogear-android-cookbook;
mvn clean install;
````

If you have a running emulator OR have a device with usb debugging turned on you can now deploy the app.

````bash
mvn android:deploy;
````

# Running
## Configure Push
To use push messaging, you need to have a Google Cloud Messaging enabled project as well as a unified push server running.  The [Android Push Tutorial](http://aerogear.org/docs/guides/aerogear-push-android/) will walk you through setting up your Google project and the unified push server.  

Once you have your registered your application with the push server, you can fill out the values in `org.jboss.aerogear.cookbook.push.PushConstants`.  After that, rebuild and rerun your application to use and experiment with Push.
 
# Troubleshooting
 
 If you are having troubles, feel free to check out our channel, #aerogear, on Freenode or our mailing list, [ aerogear-dev@lists.jboss.org](mailto:aerogear-dev@lists.jboss.org)
 
## Maven Android Deployer
### Can't deploy gridlayout
 
 If you are running a 64-bit operating system and you are having trouble deploying the Android dependencies, please make sure you have the appropriate 32 bit libraries installed.  
 
 For Fedora these are:
* glibc.i686
* libstdc++.i686
* ncurses-lib.i686
* zlib.i686
 
### Error when using Maven 3.1.x
 
 The deployer does not run on Maven 3.1.  Please downgrade to Maven 3.0.x.
 
## Push Setup
### Registration fails
 
 If you are receiving the message "Registration failed" when you try to use Push messaging, please make sure that your `PushConstants` file has the correct values.

