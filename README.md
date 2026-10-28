<h1 align="center">
  Infinity For Reddit+ <a href="https://www.patreon.com/docile_alligator"><img src="https://img.shields.io/badge/Patreon-F96854?style=for-the-badge&logo=patreon&logoColor=white" alt="Patreon"></a>
  
</h1>

> [!NOTE]
> This is a fork of [Infinity for Reddit](https://github.com/Docile-Alligator/Infinity-For-Reddit). The work done in this fork was developed using AI. The rest of the app is the work of the upstream developers.

<div align="center">

A Reddit client on Android written in Java. It does not have any ads and it features a clean UI and smooth browsing experience

<img align="right" src="https://raw.githubusercontent.com/Docile-Alligator/Infinity-For-Reddit/master/fastlane/metadata/android/en-US/images/icon.png" width=200>

</div>

### About this fork

This fork adds support for e-ink displays. Everything is under **Settings → E-ink** and off by default, so the app behaves like upstream until you turn it on:

- **Disable animations:** screen transitions, the navigation drawer, comment collapse/expand, tab switching and jumps to a post or comment happen instantly.
- **E-ink theme:** a high-contrast light theme with black text on pure white, no tinted backgrounds, and dark accents that stay readable on color e-ink.
- **Gallery navigation:** swipe to change images instantly and/or tap the left or right edge of an image; swipe up or down to close the full-screen viewer.
- **Page-turn tap zones:** tap a strip along the screen edge to scroll posts and comments one page at a time, for devices without page-turn buttons.
- **E-ink preset:** applies all of the above in one tap, along with a larger Atkinson Hyperlegible font, no video autoplay and fewer moving parts. It's offered automatically on BOOX devices.

It also builds locally with your own Reddit API key: copy `.env.example` to `.env`, then run `scripts/setup-build-env.sh` and `scripts/build-apk.sh`.

Tested on a BOOX Note Air 4C (Android 13) and a Samsung Galaxy S24 Ultra.

<br>

<div>

Infinity for Reddit+ is available on Google Play and GitHub. There’s also a Patreon version that works without Google Play Services. You can find it on the <a href="https://github.com/Docile-Alligator/Infinity-For-Reddit/releases">GitHub releases page</a>. Just look for the APK file that contains <strong><i>Infinity-Patreon</i></strong> in its name.

<div align="center">
  <a href="https://play.google.com/store/apps/details?id=ml.docilealligator.infinityforreddit.plus">
      <img alt="Get it on Google Play" height="80" src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png" />
  </a>
</div>
</div>

<div align="center">

<br>
    <a href="https://github.com/Docile-Alligator/Infinity-For-Reddit/wiki"><strong>Explore the docs »</strong></a>
<br>

<a href="https://github.com/Docile-Alligator/Infinity-For-Reddit/issues">Report a Bug</a>
·
<a href="https://github.com/Docile-Alligator/Infinity-For-Reddit/discussions/categories/ideas">Request a Feature</a>
·
<a href="https://github.com/Docile-Alligator/Infinity-For-Reddit/discussions/categories/q-a">Ask a Question</a>

</div>

<br>

<div align="center">

[![release](https://img.shields.io/github/v/release/Docile-Alligator/Infinity-For-Reddit)](https://github.com/Docile-Alligator/Infinity-For-Reddit/releases)
[![license](https://img.shields.io/github/license/Docile-Alligator/Infinity-For-Reddit)](LICENSE)
[![GitHub issues](https://img.shields.io/github/issues/Docile-Alligator/Infinity-For-Reddit)](https://github.com/Docile-Alligator/Infinity-For-Reddit/issues)

</div>

### 📱 Also available on iOS
Infinity for Reddit is finally on iOS! Check it out here: [Infinity for Reddit](https://github.com/foxanastudio/Infinity-For-Reddit-iOS)

<a href="https://apps.apple.com/us/app/infinity-for-reddit/id6759064642">
  <img alt="Download on the App Store" height="80" src="fastlane/metadata/android/en-US/images/app_store_badge.svg" />
</a>

## Donation
<p>Infinity for Reddit++:</p>
<a href="https://play.google.com/store/apps/details?id=ml.docilealligator.infinityforreddit.plus2">
    <img alt="Get it on Google Play" height="100" src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png" />
</a> 

Patreon: https://www.patreon.com/docile_alligator

Bitcoin: bc1qxtkd5ap9na7uy8nr9qpt6jny6tdwaj4v43ddle

<details>
  <summary>Table of Contents</summary>

- [About](#about)
  - [Built With](#built-with)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Usage](#usage)
    - [Cookiecutter template](#cookiecutter-template)
    - [Manual setup](#manual-setup)
    - [Variables reference](#variables-reference)
- [Roadmap](#roadmap)
- [Contributing](#contributing)
- [Support](#support)
- [License](#license)
- [Acknowledgements](#acknowledgements)

</details>

---

## About The Project

<table>
<tr>
<td>

Key features of **Infinity For Reddit**:

- Lazy mode: Automatic scrolling of posts enables you to enjoy amazing posts without moving your thumb.
- Browsing posts
- View comments
- Expand and collapse comments section
- Vote posts and comments
- Save posts
- Write comments
- Edit comments and delete comments
- Submit posts (text, link, image and video)
- Edit posts (mark and unmark NSFW and spoiler and edit flair) and delete posts
- See all the subscribed subreddits and followed users
- View the messages
- Get notifications of unread messages
- etc...

</td>
</tr>
</table>

<img 
  src="https://raw.githubusercontent.com/Wladefant/Infinity-For-Reddit/master/fastlane/metadata/android/en-US/images/phoneScreenshots/1.png" 
  alt="Screenshot 1"
  height="200" >
<img 
  src="https://raw.githubusercontent.com/Wladefant/Infinity-For-Reddit/master/fastlane/metadata/android/en-US/images/phoneScreenshots/2.png" 
  alt="Screenshot 2"
  height="200" >
<img 
  src="https://raw.githubusercontent.com/Wladefant/Infinity-For-Reddit/master/fastlane/metadata/android/en-US/images/phoneScreenshots/3.png" 
  alt="Screenshot 3"
  height="200" >
<img 
  src="https://raw.githubusercontent.com/Wladefant/Infinity-For-Reddit/master/fastlane/metadata/android/en-US/images/phoneScreenshots/4.png" 
  alt="Screenshot 4"
  height="200" >
<img 
  src="https://raw.githubusercontent.com/Wladefant/Infinity-For-Reddit/master/fastlane/metadata/android/en-US/images/phoneScreenshots/5.png" 
  alt="Screenshot 5"
  height="200" >
<img 
  src="https://raw.githubusercontent.com/Wladefant/Infinity-For-Reddit/master/fastlane/metadata/android/en-US/images/phoneScreenshots/6.png" 
  alt="Screenshot 6"
  height="200" >
<img 
  src="https://raw.githubusercontent.com/Wladefant/Infinity-For-Reddit/master/fastlane/metadata/android/en-US/images/phoneScreenshots/7.png" 
  alt="Screenshot 7"
  height="200" >

<p align="right">(<a href="#top">back to top</a>)</p>

## Contributing

First off, thanks for taking the time to contribute! Contributions are what makes the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request.
It's better to also open an issue describing the issue you want to fix. But it is not required.

Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch from `master` (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request to the `master` Branch

Here are other ways you can help:

- [Report Bugs](https://github.com/Docile-Alligator/Infinity-For-Reddit/issues)
- [Make Suggestions](https://github.com/Docile-Alligator/Infinity-For-Reddit/discussions)
- [Translate The App](https://poeditor.com/join/project?hash=b2IRyfaJv6)

<p align="right">(<a href="#top">back to top</a>)</p>

## License

Distributed under the AGPL-3.0 License. See <a href="https://github.com/Docile-Alligator/Infinity-For-Reddit/blob/master/LICENSE">LICENSE</a> for more information.

<p align="right">(<a href="#top">back to top</a>)</p>

## Contact

[u/Hostilenemy](https://www.reddit.com/user/Hostilenemy) -
docilealligator.app@gmail.com (Owner)

or [u/Wladefant](https://www.reddit.com/user/Wladefant) - wladefant@gmail.com (Collaborator)

Project Link: [https://github.com/Docile-Alligator/Infinity-For-Reddit](https://github.com/Docile-Alligator/Infinity-For-Reddit)

<p align="right">(<a href="#top">back to top</a>)</p>
