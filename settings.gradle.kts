pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        jcenter()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
       /* maven{ setUrl("https://www.jitpack.io") }*/
        jcenter()
    }
}

rootProject.name = "yunda"
include(":app")
 