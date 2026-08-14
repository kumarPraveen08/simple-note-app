package com.example.myapplication.data.util

import com.example.myapplication.data.model.ChecklistItem
import java.util.UUID

object ChecklistCodec {
    fun encode(items: List<ChecklistItem>): String =
        items.joinToString("\n") { item ->
            val checked = if (item.checked) "1" else "0"
            "${item.id}|$checked|${item.text.replace("|", "/").replace("\n", " ")}"
        }

    fun decode(raw: String): List<ChecklistItem> {
        if (raw.isBlank()) return emptyList()
        return raw.lineSequence()
            .mapNotNull { line ->
                val parts = line.split("|", limit = 3)
                if (parts.size < 3) return@mapNotNull null
                ChecklistItem(
                    id = parts[0].ifBlank { UUID.randomUUID().toString() },
                    checked = parts[1] == "1",
                    text = parts[2]
                )
            }
            .toList()
    }
}

object TagCodec {
    fun encode(tags: List<String>): String =
        tags.map { it.trim() }.filter { it.isNotEmpty() }.distinct().joinToString(",")

    fun decode(raw: String): List<String> =
        raw.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
}
