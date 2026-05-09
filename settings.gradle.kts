pluginManagement {
    repositories {
        gradlePluginPortal()
        maven {
            name = "MinecraftForge"
            url = uri("https://maven.minecraftforge.net/")
        }
        maven {
            name = "FancyGradle Maven"
            url = uri("https://maven.gofancy.wtf/releases")
        }
        maven {
            name = "FancyGradle GitHub"
            url = uri("https://gitlab.com/api/v4/projects/26758973/packages/maven")
        }
        maven {
            name = "Curse Maven"
            url = uri("https://cursemaven.com")
            content {
                includeGroup("curse.maven")
            }
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