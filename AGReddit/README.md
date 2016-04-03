# AGReddit : A basic example of data access with Pagination and optional Form based Authentication
---------
Author: Summers Pittman (supittma)   
Level: Beginner  
Technologies: Java, Android  
Summary: A basic example of data access with Pagination and optional Form based Authentication
Target Product: -   
Product Versions: -   
Source: https://github.com/aerogear/aerogear-android-cookbook/tree/master/AGReddit

## What is it?

The ```AGReddit``` project demonstrates how to access remote data, page through the data, and engage in form based authenticaiton with a remote service.

This simple project consists of a ready-to-build Android application. If you do not have a Reddit account, you can still use the application.  If you create an account on [Reddit](http://www.reddit.com) you can log in and browse your personal front page.

## Out of Date

Reddit has changed their authentication system to use exclusively OAuth2.  The authentication portions will no longer work with the live Reddit systems.  

Please follow the [update JIRA](https://issues.jboss.org/browse/AGDROID-533) for more information.

## How do I run it?

### 0. System Requirements

* [Java 7](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [Gradle 2.2](http://www.gradle.org/)
* Latest [Android SDK](https://developer.android.com/sdk/index.html) and [Platform version 21](http://developer.android.com/tools/revisions/platforms.html)

### 1. Building the Application

```shell
$ cd /path/to/AGReddit/
$ gradle clean build
```

### 2. Test Application

To deploy, run and debug the application on an Android device attached to your system, on the command line enter the following:

```shell

1. Install generated apk to device
$ cd /path/to/AGReddit
$ gradle installDebug

2. Open app on device

```

Application output is displayed in the command line window.

## How does it work?

### AeroGear Setup
`StoryListApplication` is invoked and setups up the custom AuthenticationModule, `RedditAuthenticationModule` as well as the Pipe instance and its pagination configuration.

```java
@Override
	public void onCreate() {
		super.onCreate();
		URL redditURL;
		try {
			redditURL = new URL(getString(R.string.reddit_base) +"hot.json");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}

		authModule = new RedditAuthenticationModule(getApplicationContext());

        /* The Reddit pagination information is embedded in the body of a Listing response.
        *  This block instructs AeroGear how to extract the information.
        *
        *  see : https://www.reddit.com/dev/api#listings
        *
        */
        PageConfig pageConfig = new PageConfig();

        /* We should limit the number of items in a response to 25*/
        pageConfig.setLimitValue(25);
        /* AeroGear should look in the body of the response for paging metadata. */
        pageConfig.setMetadataLocation(PageConfig.MetadataLocations.BODY);
        /* The metadata for the next data set is the after property of the response data. */
        pageConfig.setNextIdentifier("data.after");
        /* The metadata for the previous data set is the before property of the response data. */
        pageConfig.setPreviousIdentifier("data.before");
        /* PageConsumer is responsible for extracting paging data from the response and providing it
          to the pipe*/
        pageConfig.setPageParameterExtractor(new PageConsumer());



        RestfulPipeConfiguration config = PipeManager.config("listing", RestfulPipeConfiguration.class);
        config.withUrl(redditURL)
                /* Use Reddit's authentication for access.*/
              .module(authModule)
                /* Reddit includes some unique structures in its response that we must configure.*/
              .responseParser(new PagingGsonResponseParser(GSON, pageConfig))
                /* Apply our paging configuration*/
              .pageConfig(pageConfig)
                /* Create the Pipe and register it with PipeManager*/
              .forClass(Listing.class);

	}
```


### Authentication

`LoginDialogFragment` collects the user's credentials and calls the login method on the `RedditAuthenticationModule` instance configured in the application.

```java
authModule.login(username, password,
		new Callback<HeaderAndBody>() {
                        /* See LoginDialogFragment line 35 for the call back implementation*/
			public void onSuccess(
					HeaderAndBody data) {
				//reload Data and dismiss Dialog
			}

			public void onFailure(Exception e) {
                                // Log the failure and display a toast
			}
		});
```

### Loading Stories

`StoryListFragment` is responsible for loading and displaying the List of Stories from the Reddit home page.  The `reload` method is called whenever the Fragment is created, when the user logs in, or when the user pages through data.  The `reset` parameter instructs the Loader which backs the Pipe to clear its cached data and perform a refresh.

`readCallback` is an instance of `StoryListCallback` and takes the data from the Pipe and puts it in an ArrayAdapter as well as inflating the necessary views.

```java
public void reload(boolean reset) {
	StoryListApplication application = (StoryListApplication) getActivity().getApplication();
	LoaderPipe<Listing> listing = (LoaderPipe<Listing>) application.getListing(this);

        //Clear the Loader's cache and force a load from the network
        if (reset) {
            listing.reset();
        }

	listing.read(new ReadFilter(), readCallback);
}
```

### Pagination

The `Next` and `Previous` buttons in the action bar call the `next` and `previous` methods of the Activity.  `listings` is the current result from the Pipe and an instance of WrappedPagingList which is a class in AeroGear to facilitate paging through data.

```java

public void next() {
	listings.next(readCallback);
}

public void previous() {
	listings.previous(readCallback);
}
```

