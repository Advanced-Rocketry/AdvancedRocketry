pluginManagement {
    repositories {
        gradlePluginPortal()
        maven {
            name = "MinecraftForge"
            url = uri("https://maven.minecraftforge.net/")
        }
        maven {
            name = "FancyGradle"
            url = uri("https://maven.gofancy.wtf/releases")
        }
    }
}

rootProject.name = "AdvancedRocketry"

if(file("libVulpes").exists()) {
    includeBuild("libVulpes") {
        dependencySubstitution {
            substitute(module("zmaster587.libVulpes:LibVulpes")).using(project(":"))
        }
    }
}