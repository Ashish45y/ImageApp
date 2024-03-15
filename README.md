An android image compress library **imagecompressor** which compress the image without degradation the quality of image, this library used to reduce the size of large image.
# How to implement
To get a Git project into your build:
## Gradle
` Step 1:` Add it in your **root build.gradle**  at the end of repositories:
```kotlin
allprojects {
		repositories {
			...
			 maven { url = uri("https://jitpack.io" )}
		}
	}
```
`Step 2:` Add the dependency in your **project build.gradle**
```kotlin
dependencies {
	        implementation("com.github.Ashish45y:ImageApp:1.0.0")
	}
```
# Let's compress the image size!
         CoroutineScope(Dispatchers.IO).launch {
                                    ImageCompressor.compressImage(imagepath : String, imageQuality: int)
                                }
```
* **imagePath** parameter takes the absolute image path.
**imageQuality** is set in range between 1 to 100.
**For better Understanding i had added a base project which show the implemted guide**
