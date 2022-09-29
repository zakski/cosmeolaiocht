package com.szadowsz.cosmeolaiocht.deities.pojo

/**
 * Pojo to represent the JSON data of a family of related aspects
 *
 * @param name role name
 * @param desc brief description if any
 * @param aspects list of aspects that relate to it
 */
data class RolePojo(val name: String, val desc: String, val aspects: List<String>)
