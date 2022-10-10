package com.szadowsz.cosmeolaiocht.myths.pojo

import com.szadowsz.cosmeolaiocht.deities.Deity

data class EventPojo(val pantheon: String, val id : String, val type : String, val precedence: Int, val deities : List<String>)
