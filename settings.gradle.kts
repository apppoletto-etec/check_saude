pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")

            credentials {
                username = "ShawHack"
                password = "marginal36"
            }
        }
    }
}

rootProject.name = "CheckSaude"
include(":app")
