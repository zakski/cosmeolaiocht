package com.szadowsz.cosmeolaiocht

import com.szadowsz.cosmeolaiocht.deities.Aspect
import com.szadowsz.cosmeolaiocht.deities.AspectReport
import com.szadowsz.cosmeolaiocht.deities.Deity
import com.szadowsz.cosmeolaiocht.myths.Event
import com.szadowsz.cosmeolaiocht.myths.pojo.EventPojo
import com.szadowsz.cosmeolaiocht.utils.FileUtils
import com.szadowsz.cosmeolaiocht.utils.JsonMapper
import java.io.File

data class PantheonsData(
    val roles: List<String>,
    val deities: List<Deity>,
    val aspects: List<Aspect>,
    val events: List<Event>
) {

    fun pantheonCounts(): Map<String, Int> {
        return deities.groupBy{d -> d.pantheon}.mapValues{(k,v) -> v.size}.toMap()
    }

    fun getAspectReports(): List<AspectReport> {
        return aspects.map{a -> a.toReport(deities)}
    }

    fun printAspectReports() {
        val unknown = File("./report/religion/aspects/unknown")
        FileUtils.deleteJson(unknown.parentFile)

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
                    if (!unknown.exists()){
                        unknown.mkdirs()
                    }
                    val f = File("./report/religion/aspects/unknown/$fileName.json")
                    JsonMapper.write(f, a)
                }
            } catch (t: Throwable){
                println("Error Writing ${a.name}") // TODO
                throw t
            }
        }
    }

    fun printEventReports() {
        val dir = File("./report/religion/events/")
        FileUtils.deleteJson(dir)
        dir.mkdirs()
        val f = File("./report/religion/events/events.json")
        JsonMapper.write(f, events.map { e -> EventPojo(e.pantheon,e.id,e.type.name,e.precedence,e.deities.map { d->d.name }) })
    }
}
