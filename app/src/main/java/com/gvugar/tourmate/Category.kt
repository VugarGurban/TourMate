package com.gvugar.tourmate

enum class Category(val id: Int, val displayName: String) {
    ALL(-1,"All"),
    HISTORICAL(0,"Historical"),
    MUSEUMS(1,"Museums"),
    PUBLIC(2,"Public");

    companion object {
        fun fromId(id: Int): Category {
            return entries.firstOrNull { it.id == id } ?: ALL
        }
    }
}

