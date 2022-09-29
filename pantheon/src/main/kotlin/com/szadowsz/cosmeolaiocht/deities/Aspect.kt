package com.szadowsz.cosmeolaiocht.deities

import com.szadowsz.cosmeolaiocht.deities.pojo.RolePojo

/**
 * Generic Representation of Aspect of a Deity
 *
 * @param name    name of the Aspect
 * @param deities list of deities that have this Aspect in their portfolio
 */
data class Aspect(val name: String, val deities: MutableList<Deity>) {

    /**
     * Groupings of Related Aspects that this is a part of
     */
    private val roles = ArrayList<String>()

    constructor(name : String):this(name, mutableListOf<Deity>())

    /**
     * Checks if the aspects is in one or more groups
     *
     * @return if roles is nonEmpty
     */
    fun hasRoles(): Boolean {
        return roles.isNotEmpty()
    }

    /**
     * Get the list roles this aspect relates to, or unknown if it relates to none
     *
     * @return a list of Strings
     */
    fun getRoles(): List<String> {
        if (roles.isNotEmpty()) {
            return roles.toList()
        } else {
            return List(1) { "unknown" }
        }
    }

    /**
     * Find and Add Related Aspect roles
     *
     * @param rolePojos the list of specified roles
     */
    fun addRoles(rolePojos: List<RolePojo>) {
        val relatedRolePojos = rolePojos.filter{r -> r.aspects.contains(name)}
        roles.addAll(relatedRolePojos.map{r -> r.name})
    }

    fun toReport(deities : List<Deity>): AspectReport {
        return AspectReport(this, deities)
    }
}
