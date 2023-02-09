package com.example.detect_voice_app.data.repository.impl

import com.example.detect_voice_app.data.remote.api.Service
import com.example.detect_voice_app.data.repository.Repository
import com.squareup.moshi.Json
import javax.inject.Inject


class RepositoryImpl @Inject constructor(
    private val service: Service
) : Repository {
    override suspend fun getVoice(): String? {
        val data = service.getVoice()
        return data.preview
    }

}

data class Mp3 (
    val id: Long,
    val readable: Boolean,
    val title: String,

    @Json(name = "title_short")
    val titleShort: String,

    @Json(name = "title_version")
    val titleVersion: String,

    val isrc: String,
    val link: String,
    val share: String,
    val duration: Long,

    @Json(name = "track_position")
    val trackPosition: Long,

    @Json(name = "disk_number")
    val diskNumber: Long,

    val rank: Long,

    @Json(name = "release_date")
    val releaseDate: String,

    @Json(name = "explicit_lyrics")
    val explicitLyrics: Boolean,

    @Json(name = "explicit_content_lyrics")
    val explicitContentLyrics: Long,

    @Json(name = "explicit_content_cover")
    val explicitContentCover: Long,

    val preview: String,
    val bpm: Double,
    val gain: Double,

    @Json(name = "available_countries")
    val availableCountries: List<String>,

    val contributors: List<Artist>,

    @Json(name = "md5_image")
    val md5Image: String,

    val artist: Artist,
    val album: Album,
    val type: String
)

data class Album (
    val id: Long,
    val title: String,
    val link: String,
    val cover: String,

    @Json(name = "cover_small")
    val coverSmall: String,

    @Json(name = "cover_medium")
    val coverMedium: String,

    @Json(name = "cover_big")
    val coverBig: String,

    @Json(name = "cover_xl")
    val coverXl: String,

    @Json(name = "md5_image")
    val md5Image: String,

    @Json(name = "release_date")
    val releaseDate: String,

    val tracklist: String,
    val type: String
)

data class Artist (
    val id: Long,
    val name: String,
    val link: String,
    val share: String,
    val picture: String,

    @Json(name = "picture_small")
    val pictureSmall: String,

    @Json(name = "picture_medium")
    val pictureMedium: String,

    @Json(name = "picture_big")
    val pictureBig: String,

    @Json(name = "picture_xl")
    val pictureXl: String,

    val radio: Boolean,
    val tracklist: String,
    val type: String,
    val role: String? = null
)