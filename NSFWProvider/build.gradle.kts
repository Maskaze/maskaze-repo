plugins {
    id("com.android.library")
    id("kotlin-android")
    id("com.lagradost.cloudstream3.gradle")
}

cloudstream {
    // Correct functions required by newer CloudStream core library templates
    setExtensions(listOf("com.myplugins.NSFWProvider"))
    authors = listOf("Maskaze")
}
