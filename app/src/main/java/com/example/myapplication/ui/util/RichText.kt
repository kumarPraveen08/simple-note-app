package com.example.myapplication.ui.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle

object RichText {
    fun wrapSelection(text: String, start: Int, end: Int, marker: String): String {
        if (start < 0 || end > text.length || start > end) return text
        val selected = text.substring(start, end).ifEmpty { "text" }
        return text.replaceRange(start, end, "$marker$selected$marker")
    }

    fun toAnnotated(text: String): AnnotatedString = buildAnnotatedString {
        var i = 0
        while (i < text.length) {
            when {
                text.startsWith("**", i) -> {
                    val close = text.indexOf("**", i + 2)
                    if (close != -1) {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(text.substring(i + 2, close))
                        }
                        i = close + 2
                    } else {
                        append(text[i])
                        i++
                    }
                }
                text.startsWith("__", i) -> {
                    val close = text.indexOf("__", i + 2)
                    if (close != -1) {
                        withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                            append(text.substring(i + 2, close))
                        }
                        i = close + 2
                    } else {
                        append(text[i])
                        i++
                    }
                }
                text.startsWith("*", i) && !text.startsWith("**", i) -> {
                    val close = text.indexOf("*", i + 1)
                    if (close != -1) {
                        withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                            append(text.substring(i + 1, close))
                        }
                        i = close + 1
                    } else {
                        append(text[i])
                        i++
                    }
                }
                else -> {
                    append(text[i])
                    i++
                }
            }
        }
    }

    fun plainPreview(text: String, max: Int = 120): String {
        val plain = text
            .replace("**", "")
            .replace("__", "")
            .replace("*", "")
            .replace("\n", " ")
            .trim()
        return if (plain.length <= max) plain else plain.take(max) + "…"
    }
}

fun Long.toComposeColorOrNull(): Color? = if (this == 0L) null else Color(this)
