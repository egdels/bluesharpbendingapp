# Release Process Documentation

This document describes the release process for the Bluesharp Bending App across all platforms.

## Prerequisites

Before starting a release, ensure you have the following:

1. Fastlane installed (`gem install fastlane`)
2. All required environment variables set:
   - `KEYSTORE_PATH`: Path to the Android keystore
   - `KEYSTORE_PASSWORD`: Password for the keystore
   - `KEYSTORE_ALIAS`: Alias for the key in the keystore
   - `KEY_PASSWORD`: Password for the key
   - `GITHUB_API_TOKEN`: GitHub API token for creating releases

## Release Commands

### Release All Platforms

To release all platforms at once with a single command:

```bash
fastlane release_all [version_type:patch]
```

This command will:
1. Increment the version in the root build.gradle file
2. Generate release notes based on Git commits since the last tag
3. Build and release the Android app to Google Play
4. Build and release the desktop app to GitHub

The `version_type` parameter can be one of:
- `patch`: Increment the patch version (e.g., 1.2.3 -> 1.2.4) - default
- `minor`: Increment the minor version (e.g., 1.2.3 -> 1.3.0)
- `major`: Increment the major version (e.g., 1.2.3 -> 2.0.0)

### Platform-Specific Releases

If you need to release only a specific platform, you can use the following commands:

#### Android Release

```bash
fastlane android release [version_type:patch]
```

This command will:
1. Increment the version in the root build.gradle file
2. Increment the Android versionCode
3. Build a signed AAB and APK
4. Generate release notes
5. Create a Git tag and GitHub release
6. Deploy to Google Play Store

#### Mac/Desktop Release

```bash
fastlane mac release [version_type:patch]
```

This command will:
1. Increment the version in the root build.gradle file
2. Build the desktop package
3. Generate release notes
4. Create a Git tag and GitHub release

## Manual Steps

If you need more control over the release process, you can use the individual lanes:

### Android

- `fastlane android prepare_release [version_type:patch]`: Increments version and builds the app
- `fastlane android tag_and_push_bundle`: Creates a Git tag and GitHub release
- `fastlane android deploy`: Deploys to Google Play Store

### Mac/Desktop

- `fastlane mac prepare_release [version_type:patch]`: Increments version and builds the package
- `fastlane mac build_release_package_and_push_to_tag`: Creates a Git tag and GitHub release

## Release Notes

Release notes are automatically generated based on Git commits since the last tag. Commits are categorized as follows:

- **What's New**: Commits starting with "feat", "feature", or "add"
- **Fixes**: Commits starting with "fix", "bug", or "issue"
- **Other Changes**: All other commits

Release notes are saved to:
- `fastlane/metadata/changelogs/{version}.txt` (for all platforms)
- `fastlane/metadata/android/en-US/changelogs/{version}.txt` (for Android)
- `fastlane/metadata/desktop/changelogs/{version}.txt` (for desktop)

## Troubleshooting

If a release fails, check the Fastlane logs for details. Common issues include:

- Missing environment variables
- Failed builds
- Failed uploads to Google Play or GitHub

You can retry individual steps using the platform-specific lanes mentioned above.