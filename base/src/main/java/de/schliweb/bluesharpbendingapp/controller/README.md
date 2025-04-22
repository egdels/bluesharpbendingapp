# Platform-Specific Controller Layer

This package contains interfaces and implementations for platform-specific controllers in the Bluesharp Bending App. The goal is to provide a common interface layer that allows platform-specific functionality to be implemented in a consistent way across different platforms.

## Core Interfaces

### PlatformSettingsHandler

The `PlatformSettingsHandler` interface represents a handler for managing platform-specific settings. It provides operations for handling settings specific to different platforms.

```java
public interface PlatformSettingsHandler {
    void initPlatformSettings();
}
```

## Platform-Specific Implementations

### Android

- `AndroidSettingsHandler`: Extends `PlatformSettingsHandler` to provide Android-specific functionality, including methods for handling lock screen settings.

```java
public interface AndroidSettingsHandler extends PlatformSettingsHandler {
    void handleLockScreenSelection(int lookScreenIndex);
    void initLockScreen();
    
    @Override
    default void initPlatformSettings() {
        initLockScreen();
    }
}
```

## Usage

### In MainController

The `MainController` class implements both `AndroidSettingsHandler` and `PlatformSettingsHandler` interfaces to provide backward compatibility while supporting the new platform-agnostic approach:

```java
@Slf4j
public class MainController implements AndroidSettingsHandler, PlatformSettingsHandler {
    // ...
    
    @Override
    public void handleLockScreenSelection(int lockScreenIndex) {
        log.info("Handling lock screen selection: lockScreenIndex={}", lockScreenIndex);
        this.model.setSelectedLockScreenIndex(lockScreenIndex);
        this.model.setStoredLockScreenIndex(lockScreenIndex);
        modelStorageService.storeModel(model);
        log.info("Lock screen selection handled successfully.");
    }

    @Override
    public void initLockScreen() {
        log.info("Initializing lock screen (Android-specific)...");
        if (window.isAndroidSettingsViewActive()) {
            AndroidSettingsView androidSettingsView = window.getAndroidSettingsView();
            if (androidSettingsView != null) {
                androidSettingsView.setSelectedLockScreen(model.getSelectedLockScreenIndex());
                log.info("Lock screen initialized successfully.");
            } else {
                log.warn("Android settings view is null. Lock screen initialization skipped.");
            }
        } else {
            log.warn("Android settings view is not active. Lock screen initialization skipped.");
        }
    }

    @Override
    public void initPlatformSettings() {
        log.info("Initializing platform-specific settings...");
        if (window.isPlatformSettingsViewActive()) {
            PlatformSettingsView platformSettingsView = window.getPlatformSettingsView();
            if (platformSettingsView != null) {
                platformSettingsView.initialize();
                
                // If the platform settings view is an Android settings view, initialize the lock screen
                if (platformSettingsView instanceof AndroidSettingsView androidSettingsView) {
                    androidSettingsView.setSelectedLockScreen(model.getSelectedLockScreenIndex());
                }
                
                log.info("Platform settings initialized successfully.");
            } else {
                log.warn("Platform settings view is null. Platform settings initialization skipped.");
            }
        } else {
            log.warn("Platform settings view is not active. Platform settings initialization skipped.");
        }
    }
}
```

## Extending for New Platforms

To add support for a new platform:

1. Create a new interface that extends `PlatformSettingsHandler` for the platform-specific handler.
2. Implement this interface in the platform-specific module.
3. Update the `MainController` to handle the new platform-specific settings.

For example, to add support for Desktop:

```java
public interface DesktopSettingsHandler extends PlatformSettingsHandler {
    void handleDesktopSpecificSetting(int settingValue);
    
    @Override
    default void initPlatformSettings() {
        // Initialize Desktop-specific settings
    }
}
```

Then in the `MainController`, you can implement this interface:

```java
@Slf4j
public class MainController implements AndroidSettingsHandler, DesktopSettingsHandler {
    // ...
    
    @Override
    public void handleDesktopSpecificSetting(int settingValue) {
        // Handle Desktop-specific setting
    }
    
    @Override
    public void initPlatformSettings() {
        log.info("Initializing platform-specific settings...");
        if (window.isPlatformSettingsViewActive()) {
            PlatformSettingsView platformSettingsView = window.getPlatformSettingsView();
            if (platformSettingsView != null) {
                platformSettingsView.initialize();
                
                if (platformSettingsView instanceof AndroidSettingsView androidSettingsView) {
                    // Android-specific handling
                    androidSettingsView.setSelectedLockScreen(model.getSelectedLockScreenIndex());
                } else if (platformSettingsView instanceof DesktopSettingsView) {
                    // Desktop-specific handling
                    DesktopSettingsView desktopSettingsView = (DesktopSettingsView) platformSettingsView;
                    desktopSettingsView.setDesktopSpecificSetting(model.getDesktopSpecificSettingValue());
                }
                
                log.info("Platform settings initialized successfully.");
            } else {
                log.warn("Platform settings view is null. Platform settings initialization skipped.");
            }
        } else {
            log.warn("Platform setti
```

This approach allows for a clean separation of platform-specific functionality while maintaining a common interface layer.