# Yamba
A Twitter client for Android as an exercise of the Mobile Devices subject of CS Engineering degree at University of Valladolid.

## Features
- Post tweets on your Twitter account

## Development

### Getting started
Follow the next steps to get the project ready for development:
1. Clone this repository.
2. Download the appropriate [JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) for your system. We are currently on JDK 8.
3. [Install Android Studio](https://developer.android.com/sdk/index.html).
4. Import the project. Open Android Studio, click `Open an existing Android Studio project` and select the project. Gradle will build the project.
5. Create a new Twitter app on https://apps.twitter.com.
6. Fill the `./res/values/secrets.xml` file with your Twitter API keys by replacing the `xxx`'s.
7. Run the app. Click `Run > Run 'app'`. After the project builds you'll be prompted to build or launch an emulator.

## Built with
- [Twitter4J](http://twitter4j.org/en/index.html) - Twitter API client for Java.

## Resources
A list of resources that have been useful for the development or related to the technology of the app:
- [Article: Storing Secret Keys in Android](https://guides.codepath.com/android/Storing-Secret-Keys-in-Android)
