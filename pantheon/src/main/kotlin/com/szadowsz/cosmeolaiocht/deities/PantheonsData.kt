package com.szadowsz.cosmeolaiocht.deities

import com.szadowsz.cosmeolaiocht.utils.FileUtils
import com.szadowsz.cosmeolaiocht.utils.JsonMapper
import java.io.File

data class PantheonsData(val roles: List<String>, val deities: List<Deity>, val aspects: List<Aspect>) {

    fun pantheonCounts(): Map<String, Int> {
        return deities.groupBy{d -> d.pantheon}.mapValues{(k,v) -> v.size}.toMap()
    }

    fun getAspectReports(): List<AspectReport> {
        return aspects.map{a -> a.toReport(deities)}
    }

    fun printAspectReports() {
        val f = File("./report/religion/aspects/unknown")
        FileUtils.deleteJson(f.parentFile)
        f.mkdirs()

        roles.forEach { r ->
            val f2 =  File("./report/religion/aspects/$r")
            f2.mkdirs()
        }
        getAspectReports().forEach { a ->
            try {
                val fileName = a.name.split("[ -]").filter{it.length > 0}.map{p -> p.first().uppercaseChar() + p.drop(1)}.joinToString(" ")
                if (a.hasRoles()) {
                    a.roles.forEach { role ->
                        val f = File("./report/religion/aspects/$role/$fileName.json")
                        JsonMapper.write(f, a)
                    }
                } else {
                    val f = File("./report/religion/aspects/unknown/$fileName.json")
                    JsonMapper.write(f, a)
                }
            } catch (t: Throwable){
                println("Error Writing ${a.name}") // TODO
                throw t
            }
        }
    }
}
