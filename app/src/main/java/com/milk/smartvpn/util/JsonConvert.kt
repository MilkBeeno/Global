package com.milk.smartvpn.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.lang.reflect.Type

object JsonConvert {
    val gson: Gson by lazy {
        GsonBuilder()
            .registerTypeAdapter(Boolean::class.java, BooleanTypeAdapter())
            .registerTypeAdapter(String::class.java, StringTypeAdapter())
            .registerTypeAdapter(Int::class.java, IntTypeAdapter())
            .registerTypeAdapter(Double::class.java, DoubleTypeAdapter())
            .registerTypeAdapter(Long::class.java, LongTypeAdapter())
            .create()
    }

    fun <T> toModel(json: String, type: Class<T>): T? {
        return gson.fromJson(json, type)
    }

    fun <T> toModel(json: String, type: Type): T? {
        return gson.fromJson(json, type)
    }

    inline fun <reified T> toJson(model: T): String {
        return gson.toJson(model) ?: ""
    }


    class StringTypeAdapter : TypeAdapter<String>() {
        override fun write(out: JsonWriter?, value: String?) {
            out?.value(value ?: "")
        }

        override fun read(reader: JsonReader?): String {
            return when (reader?.peek()) {
                JsonToken.NULL -> {
                    reader.nextNull()
                    ""
                }
                JsonToken.BOOLEAN -> reader.nextBoolean().toString()
                JsonToken.NUMBER, JsonToken.STRING -> reader.nextString()
                else -> ""
            }
        }
    }

    class BooleanTypeAdapter : TypeAdapter<Boolean>() {
        override fun write(out: JsonWriter?, value: Boolean?) {
            if (value == null) {
                out?.value(false)
            } else {
                out?.value(value)
            }
        }

        override fun read(reader: JsonReader?): Boolean {
            return when (reader?.peek()) {
                JsonToken.NULL -> {
                    reader.nextNull()
                    false
                }
                JsonToken.BOOLEAN -> reader.nextBoolean()
                JsonToken.NUMBER -> reader.nextInt() == 1
                JsonToken.STRING -> {
                    val nextString = reader.nextString()
                    return nextString == "1" || nextString.equals("true", true)
                }
                else -> false
            }
        }
    }

    class IntTypeAdapter : TypeAdapter<Int>() {
        override fun write(out: JsonWriter?, value: Int?) {
            out?.value(value ?: 0)
        }

        override fun read(reader: JsonReader?): Int {
            return when (reader?.peek()) {
                JsonToken.NULL -> {
                    reader.nextNull()
                    0
                }
                JsonToken.BOOLEAN -> if (reader.nextBoolean()) 1 else 0
                JsonToken.NUMBER -> reader.nextInt()
                JsonToken.STRING -> {
                    return try {
                        reader.nextString().toInt()
                    } catch (e: Exception) {
                        0
                    }
                }
                else -> 0
            }
        }
    }

    class LongTypeAdapter : TypeAdapter<Long>() {
        override fun write(out: JsonWriter?, value: Long?) {
            out?.value(value ?: 0)
        }

        override fun read(reader: JsonReader?): Long {
            return when (reader?.peek()) {
                JsonToken.NULL -> {
                    reader.nextNull()
                    0L
                }
                JsonToken.BOOLEAN -> if (reader.nextBoolean()) 1L else 0L
                JsonToken.NUMBER -> reader.nextLong()
                JsonToken.STRING -> {
                    return try {
                        reader.nextString().toLong()
                    } catch (e: Exception) {
                        0L
                    }
                }
                else -> 0L
            }
        }
    }

    class DoubleTypeAdapter : TypeAdapter<Double>() {
        override fun write(out: JsonWriter?, value: Double?) {
            out?.value(value ?: 0.0)
        }

        override fun read(reader: JsonReader?): Double {
            return when (reader?.peek()) {
                JsonToken.NULL -> {
                    reader.nextNull()
                    0.0
                }
                JsonToken.BOOLEAN -> if (reader.nextBoolean()) 1.0 else 0.0
                JsonToken.NUMBER -> reader.nextDouble()
                JsonToken.STRING -> {
                    return try {
                        reader.nextString().toDouble()
                    } catch (e: Exception) {
                        0.0
                    }
                }
                else -> 0.0
            }
        }
    }
}