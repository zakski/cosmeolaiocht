package com.szadowsz.cosmeolaiocht.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File

object JsonMapper {

    private val mapper = ObjectMapper()
        .registerModule(KotlinModule.Builder().configure(KotlinFeature.NullToEmptyCollection, true).build())
        .configure(SerializationFeature.INDENT_OUTPUT, true)

    //.mapper.configOverride(List.class).setSetterInfo(JsonSetter.Value.forContentNulls(Nulls.AS_EMPTY));
    private val writer = mapper.writerWithDefaultPrettyPrinter()

    /**
     * Method to recursively extract json files from a directory
     *
     * @param dir the current directory to extract json files from
     * @return an array of json files
     */
    private fun getJson(dir: File): List<File> {
        return dir.listFiles()
                .filter { f -> f.isDirectory || f.name.endsWith(".json") }
                .flatMap { f ->
                    if (f.isDirectory) {
                        getJson(f).asIterable()
                    } else {
                        Array(1) { f }.asIterable()
                    }
                }
    }

    /**
     * Method to recursively extract json files from a directory
     *
     * @param dir the root directory to extract the json files from
     * @return an array of json files
     */
    private fun getJson(dir: String): List<File> {
        return getJson(File(dir))
    }

    private fun <T> mapFile(c: Class<T>, f: File): T {
        try {
            return mapper.readValue(f, c)
        } catch (t: Throwable) { // TODO sort out error handling
            System.err.println("Error with file " + f.name)
            throw t
        }
    }

    /**
     * Methods to load pojo objects from json files
     *
     * @param directory the directory to extract the json files from
     * @param c the class to map the json to
     * @tparam T the type of class to map the json to
     * @return List of converted T instances
     */
    fun <T> read(directory: String, c: Class<T>): List<T> {
        return getJson(directory).map { f -> mapFile(c, f) }
    }

    fun <T> write(f: File, value: T) {
        writer.writeValue(f, value)
    }
}