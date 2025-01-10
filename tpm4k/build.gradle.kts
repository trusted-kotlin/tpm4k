import de.undercouch.gradle.tasks.download.Download
import org.jetbrains.kotlin.konan.target.KonanTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.download)
}

group = "de.cacheoverflow"
version = libs.versions.tpm4k.get()

val downloadTpmCppTask = tasks.create<Download>("downloadTpmCppArtifacts") {
    group = "tpm-cpp"
    val tpmCppVersion = libs.versions.tpmcpp.get()
    src("https://github.com/trusted-kotlin/tpm-cpp/releases/download/v$tpmCppVersion/artifacts.zip")
    dest(layout.buildDirectory.file("tpm-cpp-artifacts.zip").get())
    overwrite(false)
}

val downloadTpmCppHeadersTask = tasks.create<Download>("downloadTpmCppHeaders") {
    group = "tpm-cpp"
    src("https://raw.githubusercontent.com/trusted-kotlin/tpm-cpp/refs/heads/main/include/tpm-cpp/tpm-c.h")
    dest(layout.buildDirectory.file("tpm-cpp/include/tpm-cpp/tpm-c.h").get())
    overwrite(false)
}

val unzipTpmCppTask = tasks.create<Copy>("unzipTpmCpp") {
    group = "tpm-cpp"
    dependsOn(downloadTpmCppTask)
    from(zipTree(layout.buildDirectory.file("tpm-cpp-artifacts.zip").get()))
    into(layout.buildDirectory.dir("tpm-cpp").get())
}

kotlin {
    jvm {
        withJava()
    }
    listOf(linuxX64(), mingwX64()).forEach { target -> // TODO: Add Linux arm64 Support
        target.binaries.sharedLib()

        target.compilations["main"].cinterops {
            val tpm by creating {
                includeDirs(layout.buildDirectory.dir("tpm-cpp/include").get().asFile.absolutePath)
                tasks.named(interopProcessingTaskName) {
                    dependsOn(unzipTpmCppTask, downloadTpmCppHeadersTask)
                }
            }
        }

        target.compilerOptions {
            freeCompilerArgs.addAll(
                "-include-binary", layout.buildDirectory.file(
                    "tpm-cpp/artifacts/${
                        when (target.konanTarget) {
                            KonanTarget.MINGW_X64 -> "tpm-cpp.lib"
                            KonanTarget.LINUX_X64 -> "libtpm-cpp.a"
                            else -> throw UnsupportedOperationException()
                        }
                    }"
                ).get().asFile.absolutePath
            )
        }
    }

    sourceSets {
        all {
            compilerOptions {
                optIn.add("kotlinx.cinterop.ExperimentalForeignApi")
            }
        }
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.core)
            api(libs.kotlinx.io.core)
        }
        jvmMain.dependencies {
            implementation(libs.jna)
        }
    }
}
