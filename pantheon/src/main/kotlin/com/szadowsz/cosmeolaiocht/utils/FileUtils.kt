package com.szadowsz.cosmeolaiocht.utils

import java.io.File

object FileUtils {
    /**
     * Method to recursively delete json files from a directory
     *
     * @param dir the root directory to extract the json files from
     * @return an array of json files
     */
    fun deleteJson(dir: String) {
        deleteJson(File(dir))
    }

    /**
     * Method to recursively delete json files from a directory
     *
     * @param dir the current directory to extract json files from
     * @return an array of json files
     */
    fun deleteJson(dir: File) {
        dir.listFiles()
            ?.filter { f -> f.isDirectory || f.name.endsWith(".json") }
            ?.forEach { f -> if (f.isDirectory) deleteJson(f) else f.delete() }
        dir.delete()
    }
}