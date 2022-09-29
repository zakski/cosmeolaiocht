package com.szadowsz.cosmeolaiocht.deities

class AspectReport(private val aspect: Aspect, private val deities: List<Deity>) {
    val name: String = aspect.name
    val roles: List<String> = aspect.getRoles()
    val totalStats: AspectStats = AspectStats(name, deities)
    val perPantheonStats: Map<String, AspectStats> =
        filteredPantheons(aspect, deities).map { (p, ds) -> Pair(p, AspectStats(name, ds)) }.toMap()
    val relatedAspects: Map<String, Int> = countAspects()
    val relatedParentAspects: Map<String, Int> = countParentAspects()
    val relatedSiblingAspects: Map<String, Int> = countSiblingAspects()
    val relatedConsortAspects: Map<String, Int> = countConsortAspects()
    val relatedLoverAspects: Map<String, Int> = countLoverAspects()
    val relatedChildrenAspects: Map<String, Int> = countChildrenAspects()
    val allAspects: Map<String, Int> = allRelatedAspects()

    private fun filterSelf(aspects: List<Aspect>): List<Aspect> {
        return aspects.filterNot { a -> a.name == name }
    }

    private fun filteredPantheons(aspect: Aspect, deities: List<Deity>): Map<String, List<Deity>> {
        return deities.groupBy { d -> d.pantheon }
            .filter { (p, deities) -> deities.any { d -> d.aspects.contains(aspect) } }
    }

    private fun countAspects(deities: List<Deity>): List<Pair<String, Int>> {
        return filterSelf(deities.flatMap{d -> d.aspects})
            .groupBy{n -> n.name}
            .mapValues { (k,v) -> v.size}
            .toList()
            .sortedWith(compareBy({ -it.second }, { it.first }))
    }

    private fun countAllAspects(aspects: List<Pair<String, Int>>): Map<String, Int> {
        return aspects.groupBy { it.first }
            .mapValues { (k,v) -> v.map { it.second }.sum() }
            .toList()
            .sortedWith(compareBy({ -it.second }, { it.first }))
            .toMap()
    }

    private fun allRelatedAspects(): Map<String, Int> {
        return countAllAspects(
            relatedAspects.toList() +
                    relatedParentAspects.toList() +
                    relatedSiblingAspects.toList() +
                    relatedConsortAspects.toList() +
                    relatedLoverAspects.toList() +
                    relatedChildrenAspects.toList()
        )
    }

    private fun countAspects(): Map<String, Int> {
        return countAspects(aspect.deities.toList()).toMap()
    }

    private fun countParentAspects(): Map<String, Int> {
        return countAspects(aspect.deities.flatMap{d -> d.parents}.toList()).toMap()
    }

    private fun countSiblingAspects(): Map<String, Int> {
        return countAspects(aspect.deities.flatMap{d -> d.siblings}.toList()).toMap()
    }

    private fun countConsortAspects(): Map<String, Int> {
        return countAspects(aspect.deities.flatMap{d -> d.consorts}.toList()).toMap()
    }

    private fun countLoverAspects(): Map<String, Int> {
        return countAspects(aspect.deities.flatMap{d -> d.lovers}.toList()).toMap()
    }

    private fun countChildrenAspects(): Map<String, Int> {
        return countAspects(aspect.deities.flatMap{d -> d.children}.toList()).toMap()
    }

    /**
     * Checks if the aspects is in one or more groups
     *
     * @return if roles is nonEmpty
     */
    fun hasRoles(): Boolean {
        return roles.isNotEmpty()
    }
}