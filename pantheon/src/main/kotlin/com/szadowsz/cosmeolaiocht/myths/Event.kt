package com.szadowsz.cosmeolaiocht.myths

import com.szadowsz.cosmeolaiocht.deities.Deity

/**
 * For generated events:
 * bigbang occurs first
 * then primordial events
 * creation of world can occur after primordial events before eschatological
 * birthOfDeity should occur after primordial events
 * any deathOfDeity should occur after the primordial/birthOfDeity
 * serpentslaying optional
 * last eschatological
 */
data class Event(val pantheon: String, val id : String, val type : EventType, val precedence: Int, val deities : List<Deity>)
