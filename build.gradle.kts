import com.android.build.gradle.BaseExtension
import com.lagradost.cloudstream3.gradle.CloudstreamExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }

    dependencies {
        classpath("com.android.tools.build:gradle:8.7.3")
        classpath("com.github.recloudstream:gradle:-SNAPSHOT")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.4.0")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

fun Project.cloudstream(configuration: CloudstreamExtension.() -> Unit) = extensions.getByName<CloudstreamExtension>("cloudstream").configuration()

fun Project.android(configuration: BaseExtension.() -> Unit) = extensions.getByName<BaseExtension>("android").configuration()

subprojects {
    apply(plugin = "com.android.library")
    apply(plugin = "kotlin-android")
    apply(plugin = "com.lagradost.cloudstream3.gradle")

    cloudstream {
        setRepo(System.getenv("GITHUB_REPOSITORY") ?: "user/repo")
    }

    android {
        namespace = "com.example"

        defaultConfig {
            minSdk = 21
            compileSdkVersion(35)
            targetSdk = 35
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }

        tasks.withType<KotlinJvmCompile> {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_1_8)
                freeCompilerArgs.addAll(
                    "-Xno-call-assertions",
                    "-Xno-param-assertions",
                    "-Xno-receiver-assertions"
                )
            }
            
            // Hard-strips the metadata signatures out of the compiled code classes right after generation
            doLast {
                outputs.files.asFileTree.filter { it.extension == "class" }.forEach { classFile ->
                    try {
                        val bytes = classFile.readBytes()
                        // Locates the runtime binary signature for Kotlin Metadata annotations and clears it out
                        val index = bytes.indexOf(byteArrayOf(0x4c, 0x6b, 0x6f, 0x74, 0x6c, 0x69, 0x6e, 0x2f, 0x4d, 0x65, 0x74, 0x61, 0x64, 0x61, 0x74, 0x61, 0x3b))
                        if (index != -1) {
                            val modifiedBytes = bytes.clone()
                            // Replaces the metadata reference string with a blank placeholder block
                            for (i in 0 until 17) {
                                modifiedBytes[index + i] = 0
                            }
                            classFile.writeBytes(modifiedBytes)
                        }
                    } catch (e: Exception) {
                        // Suppresses handling warnings to continue compiling cleanly
                    }
                }
            }
        }
    }

    dependencies {
        val cloudstream by configurations
        val implementation by configurations

        cloudstream("com.lagradost:cloudstream3:pre-release")

        implementation(kotlin("stdlib")) 
        implementation("com.github.Blatzar:NiceHttp:0.4.11") 
        implementation("org.jsoup:jsoup:1.18.3") 
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1") 
    }
}

// Extension utility helper function to look up byte signatures within files safely
fun ByteArray.indexOf(target: ByteArray): Int {
    if (target.isEmpty()) return -1
    for (i in 0..this.size - target.size) {
        var found = true
        for (j in target.indices) {
            if (this[i + j] != target[j]) {
                found = false
                break
            }
        }
        if (found) return i
    }
    return -1
}

task<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
