# KeyCloak Authenticator Example

This is an implementation of AbstractAccountAuthenticator for KeyCloak.  

# Configure KeyCloak

Before the demo Authenticator can be used a KeyCloak installation must be configured.  If you do not have KeyCloak, you can follow the directions [here](http://docs.jboss.org/keycloak/docs/1.0.4.Final/userguide/html/server-installation.html).

 You will need to sign into KeyCloak and add a new Realm.

 ![KeyCloak Create Realm](docs/keycloak-0.png)

 After you create the realms, add a OAuth Client.  Use `http://oauth2callback` as the Redirect URI.

 ![KeyCloak Create OAuth Client](docs/keycloak-1.png)

After the Client is created, select Claims from the navigation and enable Username.

![KeyCloak Username Claim](docs/keycloak-2.png)

Finally, select 'Installation' from the navigation and copy the json in the text field.

![KeyCloak Installation](docs/keycloak-3.png)

# Configure the Application

Create a file 'keycloak.json' in the res/raw directory.  Paste the KeyCloak installation json into the file and save it.

Launch an emulator (or connect a device in debug mode) and run `./gradlew installDebug` from the command line.

Open the `Settings` App on Android and select `Add an account`.

![KeyCloak Installation](docs/android-0.png)

Select `KeyCloak Secured Account`

![KeyCloak Installation](docs/android-1.png)

Log into a KeyCloak account available to your realm.

![KeyCloak Installation](docs/android-2.png)

Approve the Grant.

You Account is now added.

![KeyCloak Installation](docs/android-3.png)

# What Now?

Any application which uses Android's Account Authenticator system can now use the KeyCloak account you have created.  The follow snippet is all they need to include.

````
Account account = am.getAccountsByType("org.keycloak.Account")[0];
````

There is a [sample app](https://github.com/secondsun/keycloak-account-authenticator-demo) using KeyCloak available as well.