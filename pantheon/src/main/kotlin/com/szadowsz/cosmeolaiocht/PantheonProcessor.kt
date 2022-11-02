package com.szadowsz.cosmeolaiocht

import com.szadowsz.cosmeolaiocht.deities.Aspect
import com.szadowsz.cosmeolaiocht.deities.Deity
import com.szadowsz.cosmeolaiocht.deities.pojo.DeitiesPojo
import com.szadowsz.cosmeolaiocht.deities.pojo.DeityPojo
import com.szadowsz.cosmeolaiocht.deities.pojo.RolePojo
import com.szadowsz.cosmeolaiocht.myths.Event
import com.szadowsz.cosmeolaiocht.myths.EventType
import com.szadowsz.cosmeolaiocht.myths.pojo.EventPojo
import com.szadowsz.cosmeolaiocht.myths.pojo.EventsPojo
import com.szadowsz.cosmeolaiocht.utils.JsonMapper

object PantheonProcessor {

    private fun addAspects(d: Deity, pojo: DeityPojo?, aspects: MutableMap<String, Aspect>) {
        d.aspects += (pojo?.aspects ?: listOf()).map{ a -> aspects.getOrPut(a, { -> Aspect(a) })}
        d.aspects.forEach { a -> a.deities += d }
    }

    private fun addRelationships(d: Deity, pojo: DeityPojo?, deityMap: Map<Pair<String, String>, Deity>) {
        d.children.addAll((pojo?.children ?: listOf()).map{n -> deityMap.get(Pair(n,d.pantheon))!!})
        d.lovers.addAll((pojo?.lovers ?: listOf()).map{n -> deityMap.get(Pair(n,d.pantheon))!!})
        d.parents.addAll((pojo?.parents ?: listOf()).map{n -> deityMap.get(Pair(n,d.pantheon))!!})
        d.consorts.addAll((pojo?.consorts ?: listOf()).map{n -> deityMap.get(Pair(n,d.pantheon))!!})
        d.siblings.addAll((pojo?.siblings ?: listOf()).map{n -> deityMap.get(Pair(n,d.pantheon))!!})
    }

    private fun addAspectsAndRelationships(
        d: Deity,
        aspects: MutableMap<String, Aspect>,
        deityPojoMap: Map<Pair<String, String>, DeityPojo>,
        deityMap: Map<Pair<String, String>, Deity>
    ) {
        val pojo = deityPojoMap.get(d.key())
        addAspects(d, pojo, aspects)
        addRelationships(d, pojo, deityMap)
    }


    /**
     * Method to convert Json Pojos into concrete objects
     *
     * @param deityPojos list of basic deity JSON data
     * @param rolePojos list of basic role JSON data
     * @return (list of aspects, list of deities)
     */
    private fun processPojos(deityPojos: List<DeityPojo>, rolePojos: List<RolePojo>, eventPojos: List<EventPojo>): Triple<List<Aspect>, List<Deity>, List<Event>> {
        // convert deity pojo list to a map so we can reference all the related elements later
        val deityPojoMap = deityPojos.map { pojo -> pojo.key() to pojo }.toMap()

        // build mostly empty deities so that we may add details below
        val deityMap = deityPojoMap.map { (k, v) -> Pair(k, Deity(v)) }.toMap()
        val deities = deityMap.values.toList().sortedBy { d -> d.name }

        val aspectMap = HashMap<String, Aspect>()

        deities.forEach { d ->
            try {
                addAspectsAndRelationships(d, aspectMap, deityPojoMap, deityMap)
            } catch (t: Throwable) {
                println("Error Reading " + d.name) // TODO
                throw t
            }
        }

        aspectMap.values.forEach { a -> a.addRoles(rolePojos) }

        val events = processEvents(deities, eventPojos)

        return Triple(aspectMap.values.toList().sortedBy { a -> a.name }, deities,events)
    }

    private fun processEvents(deities: List<Deity>, eventPojos: List<EventPojo>): List<Event> {
        val deitiesByPantheon = deities.groupBy { d -> d.pantheon }
        val eventPojosByPantheon = eventPojos.groupBy { ep -> ep.pantheon}

        val eventsByPantheon = HashMap<String,List<Event>>()

        for (pantheon in deitiesByPantheon.keys){
            val deitiesInPantheon = deitiesByPantheon.get(pantheon).orEmpty()
            val eventPojosInPantheon = eventPojosByPantheon.get(pantheon).orEmpty()

            val eventsInPantheon = ArrayList<Event>()

            for (pojo in eventPojosInPantheon){
                eventsInPantheon.add(Event(pojo.pantheon, pojo.id, EventType.valueOf(pojo.type),pojo.precedence, deitiesInPantheon.filter { d -> pojo.deities.contains(d.name) }))
            }


            // bigbang occurs first, it's the starting marker
            eventsInPantheon.find { e -> e.type == EventType.bigBang}?: eventsInPantheon.add(Event(
                pantheon,
                pantheon + "-" + eventsInPantheon.size,
                EventType.bigBang,
                0,
                ArrayList<Deity>()
            ))

            // then primordial deities pop into existence
            val primordialEvents = eventsInPantheon.filter { e -> e.type == EventType.primordial }
            val primordialDeities = deitiesInPantheon.filter { d -> d.isPrimordial && !primordialEvents.any { e -> e.deities.contains(d) } }
            for (deity in primordialDeities) {
                eventsInPantheon.add(Event(
                    pantheon,
                    pantheon + "-" + eventsInPantheon.size,
                    EventType.primordial,
                    1,
                    arrayListOf(deity)
                ))
            }

            var birthEvents = eventsInPantheon.filter { e -> e.type == EventType.birthOfDeity }
            var birthDeities = deitiesInPantheon.filter { d -> !d.isPrimordial && !birthEvents.any { e -> e.deities.contains(d) } }

            var size = -1
            var precedence = 2
            while (birthDeities.isNotEmpty() && birthDeities.size != size) {
                size = birthDeities.size
                birthEvents = eventsInPantheon.filter { e -> e.type == EventType.birthOfDeity }
                val (toAdd, leftovers) = birthDeities.partition { d -> d.parents.isEmpty() || d.parents.all { p -> birthEvents.any { e -> e.deities.contains(p) } } }
                birthDeities = leftovers
                for (deity in toAdd) {
                    eventsInPantheon.add(Event(
                        pantheon,
                        pantheon + "-" + eventsInPantheon.size,
                        EventType.birthOfDeity,
                        precedence,
                        arrayListOf(deity)
                    ))
                }
                precedence += 1
            }

            // last is ragnarok, eschatological
            val deathEvents = eventsInPantheon.filter { e -> e.type == EventType.deathOfDeity }
            val aliveDeities = deitiesInPantheon.filter { d -> !deathEvents.any { e -> e.deities.contains(d) } }
            eventsInPantheon.find { e -> e.type == EventType.eschatological}?: eventsInPantheon.add(Event(
                pantheon,
                pantheon + "-" + eventsInPantheon.size,
                EventType.eschatological,
                eventsInPantheon.maxOf { e -> e.precedence } + 1,
                aliveDeities
            ))

            eventsByPantheon.put(pantheon, eventsInPantheon)
        }

        return eventsByPantheon.values.flatten();
    }

    /**
     * Read in the raw data and output something we can build usable generator stats from
     *
     * @param deitiesDir the deities directory
     * @param rolesDir the overarching roles assigned to deities
     * @return composite data of all loaded Pantheons
     */
    fun process(deitiesDir: String, rolesDir: String): PantheonsData {
        // load data from json files
        val deityPojos = JsonMapper.read(deitiesDir, DeityPojo::class.java,{f -> !f.nameWithoutExtension.equals("Myths") && !f.nameWithoutExtension.equals("Deities")}) +
                JsonMapper.read(deitiesDir, DeitiesPojo::class.java,{ f -> f.isDirectory || f.nameWithoutExtension.equals("Deities")}).flatMap { ds -> ds.deities}

        val rolePojos = JsonMapper.read(rolesDir, RolePojo::class.java)

        val eventPojos = JsonMapper.read(deitiesDir, EventsPojo::class.java,{ f -> f.isDirectory || f.nameWithoutExtension.equals("Myths")}).flatMap { ds -> ds.myths}

        // connect the deities and aspects together
        val (aspects, deities, events) = processPojos(deityPojos, rolePojos, eventPojos)
        return PantheonsData(rolePojos.map{it.name}, deities, aspects, events)
    }
}