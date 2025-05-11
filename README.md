# Ripple for Android

Note: This app is a work in progress and primarily lacks a user-friendly UX. Proper feedback is not yet implemented. Shizuku support is also planned.

Update 2025-02: I still plan to work on this, as I still use it despite its un-ease of use. It needs better Termux integration and perhaps an automated way of initially configuring Termux for accepting adb commands. 

---

Some Android smartphone manufacturers, such as Samsung and Oneplus, allow for the installation of copies of certain apps (typically messaging apps) using Android's native user system.
This app helps circumvent this limitation using ADB to install (almost) any app to the dual profile. It allows users to select an app and install it by sending the necessary ADB commands to Termux.
Permissions must also be manually granted since the system will think they are granted on the dual app when they are only granted for the main app.

This app requires a working installation of Termux which allows third-party apps and is running ADB (install android-tools)
