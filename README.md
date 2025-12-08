# TerminalApp

This is a fork of [Android 16 Terminal](https://cs.android.com/android/platform/superproject/+/android-latest-release:packages/modules/Virtualization/android/TerminalApp/) with the dependency on Android 16 Virtualization Framework removed.

It is designed for use as a frontend for the `ttyd` package of the [Termux Native Android Terminal](https://f-droid.org/en/packages/com.termux/).

### System Requirements

- Android 9 or newer
- Any ROM vendor
- Any hardware
- Android Virtualization Framework not required

### How to Use

1. Install [Termux](https://f-droid.org/en/packages/com.termux/) and open it

2. Run these commands in Termux:

```bash
pkg upgrade
pkg install ttyd
ttyd --writable login
```

3. Install TerminalApp and open it

<img height="1000" alt="Screenshot_20251207-135733_Terminal" src="https://github.com/user-attachments/assets/15c13873-dfba-43a4-a014-aa67f6370789" />
