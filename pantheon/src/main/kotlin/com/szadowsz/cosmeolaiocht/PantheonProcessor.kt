package com.szadowsz.cosmeolaiocht

import com.szadowsz.cosmeolaiocht.deities.Aspect
import com.szadowsz.cosmeolaiocht.deities.Deity
import com.szadowsz.cosmeolaiocht.deities.pojo.DeitiesPojo
import com.szadowsz.cosmeolaiocht.deities.pojo.DeityPojo
import com.szadowsz.cosmeolaiocht.deities.pojo.RolePojo
import com.szadowsz.cosmeolaiocht.myths.Event
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
    private fun processPojos(deityPojos: List<DeityPojo>, rolePojos: List<RolePojo>, eventsPojos: List<EventPojo>): Triple<List<Aspect>, List<Deity>, List<Event>> {
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

        return Triple(aspectMap.values.toList().sortedBy { a -> a.name }, deities,ArrayList<Event>())
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