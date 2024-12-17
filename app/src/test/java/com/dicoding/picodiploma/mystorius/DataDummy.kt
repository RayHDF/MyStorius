package com.dicoding.picodiploma.mystorius

import com.dicoding.picodiploma.mystorius.data.api.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                id = i.toString(),
                name = "name + $i",
                description = "description + $i"
            )
            items.add(story)
        }
        return items
    }
}