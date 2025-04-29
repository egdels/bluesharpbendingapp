# Component Diagrams for Bluesharp Bending App

This document contains component diagrams for the Bluesharp Bending App, showing the high-level architecture and the relationships between components.

## High-Level Architecture

The Bluesharp Bending App is structured as a multi-platform application with a shared base module and platform-specific implementations for Android, Desktop, Web, and a separate Tuner module.

```plantuml
@startuml
package "Bluesharp Bending App" {
  [Base Module] as Base
  [Android Module] as Android
  [Desktop Module] as Desktop
  [Web Module] as Web
  [Tuner Module] as Tuner
  
  Android --> Base : depends on
  Desktop --> Base : depends on
  Web --> Base : depends on
  Tuner --> Base : depends on
}
@enduml
```

## Base Module

The Base Module contains the core functionality shared across all platforms, organized in an MVC (Model-View-Controller) architecture with dependency injection.

```plantuml
@startuml
package "Base Module" {
  package "Controller" {
    [MainController]
    [HarpController]
    [MicrophoneController]
    [TrainingController]
    [HarpViewHandler]
    [TrainingViewHandler]
  }
  
  package "Model" {
    [MainModel]
    [ModelStorageService]
    [VersionService]
    
    package "Harmonica" {
      [HarmonicaModel]
    }
    
    package "Microphone" {
      [MicrophoneModel]
    }
    
    package "Training" {
      [TrainingModel]
    }
  }
  
  package "View" {
    [MainWindow]
    [HarpView]
    [TrainingView]
    [HarpSettingsView]
    [MicrophoneSettingsView]
  }
  
  package "DI" {
    [AppComponent]
    [BaseModule]
    [ControllerModule]
    [ViewModule]
    [MicrophoneModule]
  }
  
  package "Utils" {
    [UtilityClasses]
  }
  
  Controller --> Model : uses
  Controller --> View : updates
  View --> Controller : triggers actions
  Controller ..> DI : injected by
  Model ..> DI : injected by
  View ..> DI : injected by
  Controller --> Utils : uses
  Model --> Utils : uses
  View --> Utils : uses
}
@enduml
```

## Platform-Specific Modules

Each platform-specific module (Android, Desktop, Web) extends the base module with platform-specific implementations.

### Android Module

```plantuml
@startuml
package "Android Module" {
  package "App" {
    [MainActivity]
    [BlueSharpBendingApplication]
  }
  
  package "DI" {
    [AndroidAppComponent]
    [AndroidModule]
  }
  
  package "Model" {
    [AndroidSpecificModels]
  }
  
  package "View" {
    [AndroidViews]
    [Fragments]
    [Activities]
  }
  
  App --> DI : uses
  View --> DI : injected by
  Model --> DI : injected by
}

package "Base Module" as Base {
  [BaseComponents]
}

Android Module --> Base : extends
@enduml
```

### Desktop Module

```plantuml
@startuml
package "Desktop Module" {
  package "App" {
    [DesktopApplication]
    [DesktopLauncher]
  }
  
  package "DI" {
    [DesktopAppComponent]
    [DesktopModule]
  }
  
  package "Model" {
    [DesktopSpecificModels]
  }
  
  package "View" {
    [DesktopViews]
    [DesktopWindows]
  }
  
  App --> DI : uses
  View --> DI : injected by
  Model --> DI : injected by
}

package "Base Module" as Base {
  [BaseComponents]
}

Desktop Module --> Base : extends
@enduml
```

### Web Module

```plantuml
@startuml
package "Web Module" {
  package "Webapp" {
    [WebApplication]
    [WebComponents]
  }
}

package "Base Module" as Base {
  [BaseComponents]
}

Web Module --> Base : extends
@enduml
```

### Tuner Module

```plantuml
@startuml
package "Tuner Module" {
  package "Tuner" {
    [TunerApplication]
    [TunerComponents]
  }
}

package "Base Module" as Base {
  [BaseComponents]
}

Tuner Module --> Base : extends
@enduml
```

## Component Interactions

This diagram shows how the main components interact with each other during typical application usage.

```plantuml
@startuml
actor User

User --> [MainWindow] : interacts with
[MainWindow] --> [MainController] : triggers actions
[MainController] --> [HarpController] : delegates harp-related actions
[MainController] --> [MicrophoneController] : delegates microphone-related actions
[MainController] --> [TrainingController] : delegates training-related actions

[HarpController] --> [HarpViewHandler] : updates view
[HarpController] --> [MainModel] : updates model
[MicrophoneController] --> [MainModel] : updates model
[TrainingController] --> [TrainingViewHandler] : updates view
[TrainingController] --> [MainModel] : updates model

[HarpViewHandler] --> [HarpView] : updates UI
[TrainingViewHandler] --> [TrainingView] : updates UI

[MainModel] --> [ModelStorageService] : persists data
@enduml
```

## Dependency Injection Structure

This diagram shows how dependency injection is structured in the application.

```plantuml
@startuml
package "DI" {
  [AppComponent] --> [BaseModule] : includes
  [AppComponent] --> [ControllerModule] : includes
  [AppComponent] --> [ViewModule] : includes
  [AppComponent] --> [MicrophoneModule] : includes
  
  [BaseModule] ..> [MainModel] : provides
  [BaseModule] ..> [ModelStorageService] : provides
  [BaseModule] ..> [VersionService] : provides
  
  [ControllerModule] ..> [MainController] : provides
  [ControllerModule] ..> [HarpController] : provides
  [ControllerModule] ..> [MicrophoneController] : provides
  [ControllerModule] ..> [TrainingController] : provides
  
  [ViewModule] ..> [MainWindow] : provides
  [ViewModule] ..> [HarpView] : provides
  [ViewModule] ..> [TrainingView] : provides
  
  [MicrophoneModule] ..> [MicrophoneModel] : provides
}

[DependencyInjectionInitializer] --> [AppComponent] : initializes
@enduml
```

These diagrams provide a comprehensive view of the application's architecture, showing the main components and their relationships. They can be used as a reference for understanding the codebase and for making architectural decisions in the future.