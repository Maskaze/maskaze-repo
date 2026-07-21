import com.lagradost.cloudstream3.gradle.CloudstreamExtension

plugins {
    id("com.android.library")
    kotlin("android")
    id("com.lagradost.cloudstream3.gradle")
}

cloudstream {
    name = "SupJav NSFW"
    description = "Pulls video content streams from SupJav website source"
    providerClass = "com.myplugins.NSFWProvider"
    authors = listOf("Maskaze")
}
