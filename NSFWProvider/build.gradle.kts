plugins {
    id("com.android.library")
    id("kotlin-android")
    id("com.lagradost.cloudstream3.gradle")
}

cloudstream {
    providerClass = "com.myplugins.NSFWProvider"
    authors = listOf("Maskaze")
}
