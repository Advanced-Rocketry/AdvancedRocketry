import com.matthewprenger.cursegradle.CurseArtifact
import com.matthewprenger.cursegradle.CurseProject
import com.matthewprenger.cursegradle.CurseRelation
import org.ajoberstar.grgit.Grgit
import org.gradle.internal.jvm.Jvm
import se.bjurr.gitchangelog.plugin.gradle.GitChangelogTask
import java.util.Date
import java.text.SimpleDateFormat
import java.util.TimeZone

plugins {
    idea
    id("net.minecraftforge.gradle") version "5.1.+"
    id("wtf.gofancy.fancygradle") version "1.1.+"
    id("org.ajoberstar.grgit") version "4.1.1"
    id("com.matthewprenger.cursegradle") version "1.4.0"
    id("se.bjurr.gitchangelog.git-changelog-gradle-plugin") version "1.72.0"
    `maven-publish`
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

val startGitRev: String by project

group = "zmaster587.advancedRocketry"
setProperty("archivesBaseName", archiveBase)

val buildNumber: String by lazy { System.getenv("BUILD_NUMBER") ?: getDate() }

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
        name = "LibVulpes"
        url = uri("http://maven.dmodoomsirius.me/")
        isAllowInsecureProtocol = true
    }
    flatDir {
        dirs("libs")
    }
}

dependencies {
    minecraft(group = "net.minecraftforge", name = "forge", version = "$mcVersion-$forgeVersion")

    compileOnly("net.industrial-craft:industrialcraft-2:$icVersion:dev")
    //implementation("zmaster587.libVulpes:LibVulpes:$mcVersion-$libVulpesVersion-$libVulpesBuildNum-deobf")

    compileOnly("micdoodle8.mods:galacticraft-api:$gcVersion")
    compileOnly("micdoodle8.mods:galacticraft-core:$gcVersion")
    compileOnly("micdoodle8.mods:galacticraft-planets:$gcVersion")
    compileOnly("micdoodle8.mods:micdoodlecore:$gcVersion")

    compileOnly(fg.deobf("mezz.jei:jei_${mcVersion}:${jeiVersion}:api"))
    runtimeOnly(fg.deobf("mezz.jei:jei_${mcVersion}:${jeiVersion}"))
    implementation ("zmaster587.libVulpes:libVulpes:1.12.2-0.4.2+:deobf")
}

tasks.processResources {
    //includeEmptyDirs = false
    inputs.properties(
        "advRocketryVersion" to project.version,
        "mcVersion" to mcVersion,
        "libVulpesVersion" to libVulpesVersion
    )

    filesMatching("mcmod.info") {
        expand(
            "advRocketryVersion" to project.version,
            "mcVersion" to mcVersion,
            "libVulpesVersion" to libVulpesVersion
        )
    }

    exclude("**/*.sh")
}

tasks.register("cloneLibVulpes") {
    group = "build setup"
    doLast {
        val libVulpesRepo: String by project
        val libVulpesBranch: String? by project

        val repo = Grgit.clone {
            dir = "$projectDir/libVulpes"
            uri = libVulpesRepo
            if(libVulpesBranch != null)
                refToCheckout = libVulpesBranch
        }
        println("Cloned libVulpes repository from $libVulpesRepo (current branch: ${repo.branch.current().name})")
    }
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
}

tasks.build {
    dependsOn(deobfJar)
}

val makeChangelog by tasks.creating(GitChangelogTask::class.java) {
    file = file("changelog.html")
    untaggedName = "Current release ${project.version}"

    //Get the last commit from the cache or config if no cache exists
    val lastHashFile = file("lasthash.txt")
    
    fromCommit = if (!lastHashFile.exists())
        startGitRev
    else
        lastHashFile.readText()

    lastHashFile.writeText(gitHash)

    toRef = "HEAD"
    gitHubIssuePattern = "nonada123";
    templateContent = """
        {{#tags}}
          <h3>{{name}}</h3>
          <ul>
            {{#commits}}
            <li> <a href="https://github.com/zmaster587/AdvancedRocketry/commit/{{hash}}" target=_blank> {{{message}}}</a>
        </li>
            {{/commits}}
          </ul>
        {{/tags}}
    """.trimIndent()
}

curseforge {
    apiKey = (project.findProperty("thecursedkey") as String?).orEmpty()

    project(closureOf<CurseProject> {
        id = "236542"
        relations(closureOf<CurseRelation> {
            requiredDependency("libvulpes")
        })
        changelog = file("changelog.html")
        changelogType = "html"
        // Why is it hardcoded to beta tho?..
        releaseType = "beta"
        addGameVersion(mcVersion)
        mainArtifact(tasks.jar, closureOf<CurseArtifact> {
            displayName = "AdvancedRocketry ${ project.version } build $buildNumber for $mcVersion"
        })
        addArtifact(deobfJar, closureOf<CurseArtifact> {
            displayName = "AdvancedRocketry ${ project.version }-deobf build $buildNumber for $mcVersion"
        })
    })
}

tasks.curseforge {
    dependsOn(makeChangelog)
}

publishing {
    repositories {
        maven {
            url = if (project.findProperty("local") == "true")
                uri("$buildDir/build/maven")
            else
                uri("file:///usr/share/nginx/maven/")
        }
    }
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])

            artifact(tasks.jar.get())
            artifact(deobfJar.get())
            artifact(makeChangelog.file)
        }
    }
}

tasks.publish {
    dependsOn(makeChangelog)
}

idea {
    module {
        inheritOutputDirs = true
    }
}