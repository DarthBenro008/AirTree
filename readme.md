![AiRTree Banner](assets/airtree_banner1.png)

# AirTree

> An AR Based Plant that teaches you about your environment

## What is AirTree?

AirTree is an AR Based Plant that helps and tells you about your surrounding environment. This will educate how pure the air arrouding your is.

Your Plant is also affected by this!
So its a challenge to grow this virtual plant and a positive approach to do good for the environment by educating and visualising metric data!

## TechStack

![TechStack](assets/airtree_techstack.png)

- Android App written in Kotlin
- Uses Jetpack and AndroidX
- ARCore
- [OpenWeather API](http://openweathermap.org/)


## Installation

- Clone the project

```bash
git clone https://github.com/DarthBenro008/AirTree
```

- Create a file named `Secrets.kt` inside your src folder and populate it like the following

- Go to [OpenWeather](http://openweathermap.org/) API and get your API KEY, and place it inside quotes like shown below

```kotlin
package com.benrostudios.airtree

object Secrets {
    const val API_KEY: String = "<Your OpenWeather API Key>"
}

```

- Build and run the app using gradle

## Author

👨‍💻 Hemanth Krishna [@DarthBenro008](http://github.com/DarthBenro008)

## Show your support

Give a ⭐ if you liked this project!

Spread the word to your fellows to help grow a healthy environment for us!

## Contributions

- Feel Free to Open a PR/Issue for any feature or bug(s).
- Make sure you follow the community guidelines!
- Feel free to open an issue to ask a question/discuss anything about AirTree.
- Have a feature request? Open an Issue!

## License

Copyright 2021 Hemanth Krishna

Licensed under MIT License : https://opensource.org/licenses/MIT
