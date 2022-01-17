pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "net.minecraftforge.gradle") {
                useModule("${requested.id}:ForgeGradle:${requested.version}")
            }
        }
    }

    repositories {
        gradlePluginPortal()
        maven {
            name = "MinecraftForge"
            url = uri("https://maven.minecraftforge.net/")
        }
        maven {
            name = "FancyGradle"
            url = uri("https://gitlab.com/api/v4/projects/26758973/packages/maven")
        }
    }
}

rootProject.name = "AdvancedRocketry"

//includeBuild("libVulpes")