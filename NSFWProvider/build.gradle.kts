plugins {
    id("com.android.library")
    id("kotlin-android")
    id("com.lagradost.cloudstream3.gradle")
}

cloudstream {
    // Corrects the indexing reference alignment so the plugin registers inside the final file
    providerClass = "com.myplugins.NSFWProvider"
    authors = listOf("Maskaze")
}
