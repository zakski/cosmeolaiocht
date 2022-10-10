package com.szadowsz.cosmeolaiocht.myths

import com.szadowsz.cosmeolaiocht.deities.Deity

data class Event(val pantheon: String, val id : String, val type : EventType, val precedence: Int, val deities : List<Deity>)
