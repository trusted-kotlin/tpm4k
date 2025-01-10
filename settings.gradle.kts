pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://gitlab.com/api/v4/projects/65231927/packages/maven")
    }
}

include(":tpm4k")
rootProject.name = "tpm4k"
