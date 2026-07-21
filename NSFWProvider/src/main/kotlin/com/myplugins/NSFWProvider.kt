package com.myplugins

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import org.jsoup.nodes.Element

class NSFWProvider : MainAPI() {
    override var mainUrl = "https://supjav.com"
    override var name = "SupJav NSFW"
    override val supportedTypes = setOf(TvType.NSFW)
    override var lang = "en"
    override val hasMainPage = true

    override suspend fun getMainPage(page: Int, request: HomePageRequest): HomePageResponse {
        val document = app.get(mainUrl).document
        val items = document.select(".post") // Finds the video boxes
        val homeResults = items.mapNotNull { it.toSearchResult() }
        return HomePageResponse(listOf(HomePageList("Recent Releases", homeResults)), hasNext = false)
    }

    private fun Element.toSearchResult(): SearchResponse? {
        val title = this.selectFirst(".entry-title a")?.text() ?: return null
        val href = this.selectFirst(".entry-title a")?.attr("href") ?: return null
        val posterUrl = this.selectFirst("img")?.attr("src")
        return newMovieSearchResponse(title, href, TvType.NSFW) {
            this.posterUrl = posterUrl
        }
    }

    override suspend fun load(url: String): LoadResponse? {
        val document = app.get(url).document
        val title = document.selectFirst(".entry-title")?.text() ?: return null
        val poster = document.selectFirst(".poster img")?.attr("src")
        
        return newMovieLoadResponse(title, url, TvType.NSFW, url) {
            this.posterUrl = poster
        }
    }

    override suspend fun loadLinks(data: String, isCasting: Boolean, subtitleCallback: (SubtitleFile) -> Unit, callback: (ExtractorLink) -> Unit): Boolean {
        val document = app.get(data).document
        // Finds player streaming sources hidden in the webpage layout
        val iframeUrl = document.selectFirst("iframe")?.attr("src") ?: return false
        
        callback.invoke(
            ExtractorLink(
                name,
                "Primary Stream",
                iframeUrl,
                referer = mainUrl,
                quality = Qualities.P720.value
            )
        )
        return true
    }
}
