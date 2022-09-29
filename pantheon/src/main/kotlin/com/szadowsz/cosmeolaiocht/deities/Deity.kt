package com.szadowsz.cosmeolaiocht.deities

import com.szadowsz.cosmeolaiocht.deities.pojo.DeityPojo

/**
 * Deity with relationship info
 *
 * @param name the name of the deity
 * @param gender male/female
 * @param pantheon the pantheon the deity is in
 * @param group the sub-group the deity is in
 * @param isMajor if they are an important deity in the pantheon
 * @param isPrimordial if they are one of the primordial deities that are a part of the origin story
 * @param aspects the portfolio of things mortals prayed to them for
 * @param parents their deity parents
 * @param consorts their deity partners
 * @param lovers their deity one night stands
 * @param siblings their deity brothers and sisters
 * @param children their deity children
 */
data class Deity(
    val name: String,
    val gender: Gender,
    val isMajor: Boolean,
    val isPrimordial: Boolean,
    val pantheon: String,
    val group: String?,
    val aspects: MutableList<Aspect>,
    val parents: MutableList<Deity>,
    val consorts: MutableList<Deity>,
    val lovers: MutableList<Deity>,
    val siblings: MutableList<Deity>,
    val children: MutableList<Deity>
) {

    /**
     * Convert DeityPojo to Deity
     * @param pojo the pojo to convert
     * @return fully-fledged deity instance
     */
    constructor(pojo: DeityPojo) :
        this(
            pojo.name,
            Gender.valueOf(pojo.gender),
            pojo.isMajor,
            pojo.isPrimordial,
            pojo.pantheon,
            pojo.group,
            ArrayList(),
            ArrayList(),
            ArrayList(),
            ArrayList(),
            ArrayList(),
            ArrayList()
        ){
    }

    fun key(): Pair<String,String> {
        return Pair(name, pantheon)
    }

    override fun toString(): String  {
     return   """
      |Deity:
      |   name: ${name}
      |   pantheon: ${pantheon}
      |   group: ${group}
      |   gender: ${gender}
      |   aspects: ${aspects.map{a -> a.name}.joinToString("[",", ","]")}
      |   parents: ${parents.map{a -> a.name}.joinToString("[", ", ", "]")}
      |   consorts: ${consorts.map{a -> a.name}.joinToString("[", ", ", "]")}
      |   lovers: ${lovers.map{a -> a.name}.joinToString("[", ", ", "]")}
      |   siblings: ${siblings.map{a -> a.name}.joinToString("[", ", ", "]")}
      |   children: ${children.map{a -> a.name}.joinToString("[", ", ", "]")}
      |
      |""".trimMargin("|")
    }
}
