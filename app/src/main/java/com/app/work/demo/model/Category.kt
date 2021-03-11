package com.app.work.demo.model

data class Response (
    val categories: List<Category>
)
data class Category (
    val name: String,
    val videos: ArrayList<Video>
)

data class Video (
    val description: String,
    val sources: List<String>,
    val subtitle: Any,
    val thumb: String,
    val title: String,
    var percentage: String,
    var status: Status = Status.NONE
)

enum class Status {
    NONE, DOWNLOADING, COMPlETED
}



enum class Subtitle(val value: String)
