import org.ajoberstar.grgit.Grgit
import org.gradle.internal.jvm.Jvm
import java.util.Date
import java.text.SimpleDateFormat
import java.util.TimeZone

plugins {
    idea
    id("net.minecraftforge.gradle") version "5.1.+"
    id("wtf.gofancy.fancygradle") version "1.1.+"
    id("org.ajoberstar.grgit") version "4.1.1"
}

val mcVersion: String by project
val forgeVersion: String by project
val modVersion: String by project
val archiveBase: String by project

val libVulpesVersion: String by project
val libVulpesBuildNum: String by project
val jeiVersion: String by project
val icVersion: String by project
val gcVersion: String by project

val bambooshortPlanName: String? by project

group = "zmaster587.advancedRocketry"
setProperty("archivesBaseName", archiveBase)

val buildNumber: String by lazy {
    if (System.getenv("BUILD_NUMBER") != null) {
        "-${System.getenv("BUILD_NUMBER")}"
    } else if (project.hasProperty("bambooBuildNumber")) {
        "-" + project.property("bambooBuildNumber")
    } else if (System.getenv("DRONE_BUILD_NUMBER") != null) {
        "-${System.getenv("DRONE_BUILD_NUMBER")}"
    } else if (System.getenv("TRAVIS_BUILD_NUMBER") != null) {
        "-${System.getenv("TRAVIS_BUILD_NUMBER")}"
    } else if (project.hasProperty("sbmBuild")) {
        "-" + project.property("sbmBuild")
    } else {
        getDate()
    }
}

fun getDate(): String {
    val format = SimpleDateFormat("HH-mm-dd-MM-yyyy")
    format.timeZone = TimeZone.getTimeZone("UTC")
    return format.format(Date())
}

version = "$modVersion-$buildNumber"

println("$archiveBase v$version")

//sourceCompatibility = targetCompatibility = '1.8' // Need this here so eclipse task generates correctly.
tasks.compileJava {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
}

minecraft {
    mappings("snapshot", "20170624-1.12")

    accessTransformer(file("src/main/resources/META-INF/accessTransformer.cfg"))

    runs {
        create("client") {
            properties(
                mapOf(
                    "forge.logging.markers" to "SCAN,REGISTRIES,REGISTRYDUMP,COREMODLOG",
                    "forge.logging.console.level" to "info"
                )
            )

            workingDirectory = file("run").canonicalPath

            mods {
                create("advancedrocketry") {
                    source(sourceSets["main"])
                }
            }
        }
        create("server") {
            properties(
                mapOf(
                    "forge.logging.markers" to "SCAN,REGISTRIES,REGISTRYDUMP,COREMODLOG",
                    "forge.logging.console.level" to "info"//, "fml.coreMods.load" to "com.gramdatis.core.setup.GramdatisPlugin"
                )
            )
            arg("nogui")

            workingDirectory = file("run-server").canonicalPath

            mods {
                create("advancedrocketry") {
                    source(sourceSets["main"])
                }
            }
        }
    }
}

fancyGradle {
    patches {
        resources
        coremods
        codeChickenLib
        asm
    }
}

repositories {
    mavenCentral()
    ivy {
        name = "LibVulpes"
        artifactPattern("http://65.78.115.159:8080/job/LibVulpes/job/libVulpes/job/1.12/$libVulpesBuildNum/artifact/output/[module]-[revision].[ext]")
    }
    maven {
        name = "mezz.jei"
        url = uri("https://dvs1.progwml6.com/files/maven/")
    }
    ivy {
        name = "industrialcraft-2"
        artifactPattern("http://jenkins.ic2.player.to/job/IC2_111/39/artifact/build/libs/[module]-[revision].[ext]")
    }
    maven {
        // location of a maven mirror for JEI files, as a fallback
        name = "ModMaven"
        url = uri("https://modmaven.k-4u.nl")
    }
    maven {
        name = "Galacticraft"
        url = uri("https://maven.galacticraft.dev")
    }
    maven {
        name = "CurseMaven"
        url = uri("https://www.cursemaven.com")
        content {
            includeGroup("curse.maven")
        }
    }
    flatDir {
        dirs("libs")
    }
}

dependencies {
    minecraft(group = "net.minecraftforge", name = "forge", version = "$mcVersion-$forgeVersion")

    compileOnly("net.industrial-craft:industrialcraft-2:$icVersion:dev")
    implementation("zmaster587.libVulpes:LibVulpes:$mcVersion-$libVulpesVersion-$libVulpesBuildNum-deobf")

    compileOnly("micdoodle8.mods:galacticraft-api:$gcVersion")
    compileOnly("micdoodle8.mods:galacticraft-core:$gcVersion")
    compileOnly("micdoodle8.mods:galacticraft-planets:$gcVersion")
    compileOnly("micdoodle8.mods:micdoodlecore:$gcVersion")

    compileOnly(fg.deobf("mezz.jei:jei_${mcVersion}:${jeiVersion}:api"))
    runtimeOnly(fg.deobf("mezz.jei:jei_${mcVersion}:${jeiVersion}"))
}

tasks.processResources {
    //includeEmptyDirs = false
    inputs.properties(
        "version" to project.version,
        "mcversion" to mcVersion
    )

    filesMatching("mcmod.info") {
        expand(
            "version" to project.version,
            "mcversion" to mcVersion
        )
    }

    exclude("**/*.sh")
}

val currentJvm: String = Jvm.current().toString()
println("Current Java version: $currentJvm")

val gitHash: String by lazy {
    val hash: String = if (File(projectDir, ".git").exists()) {
        val repo = Grgit.open(mapOf("currentDir" to project.rootDir))
        repo.log().first().abbreviatedId
    } else {
        "unknown"
    }
    println("GitHash: $hash")
    return@lazy hash
}

// Name pattern: [archiveBaseName]-[archiveAppendix]-[archiveVersion]-[archiveClassifier].[archiveExtension]
tasks.withType(Jar::class) {
    archiveAppendix.set(mcVersion)
    manifest {
        attributes(
                "Built-By" to System.getProperty("user.name"),
                "Created-By" to currentJvm,
                "Implementation-Title" to archiveBase,
                "Implementation-Version" to project.version,
                "Git-Hash" to gitHash,
                "FMLCorePlugin" to "zmaster587.advancedRocketry.asm.AdvancedRocketryPlugin",
                "FMLCorePluginContainsFMLMod" to "true"
        )
    }
}

val deobfJar by tasks.registering(Jar::class) {
    from(sourceSets["main"].output)
    //if project name is BBM-Dev it makes the deobf jar deobf-dev if not its just deobf
    if (bambooshortPlanName == "BBM-Dev")
        archiveClassifier.set("deobf-dev")
    else
        archiveClassifier.set("deobf")
    //sets where the jars go to.
    destinationDirectory.set(file("output"))
}

tasks.jar {
    //if project has the property BBM-Dev then it appends dev to indicate the dev branch
    if (bambooshortPlanName == "BBM-Dev")
        archiveClassifier.set("universal-dev")
    else
        archiveClassifier.set("universal")
    //sets where the jars go to.
    destinationDirectory.set(file("output"))
}

tasks.build {
    dependsOn(deobfJar)
}

idea {
    module {
        inheritOutputDirs = true
    }
}