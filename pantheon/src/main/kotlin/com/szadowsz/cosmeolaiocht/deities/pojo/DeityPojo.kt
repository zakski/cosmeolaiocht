package com.szadowsz.cosmeolaiocht.deities.pojo

data class DeityPojo(
    val name: String,
    val gender: String,
    val pantheon: String,
    val group: String?,
    val isMajor: Boolean,
    val isPrimordial: Boolean,
    val aspects: List<String>,
    val parents: List<String>,
    val consorts: List<String>,
    val lovers: List<String>,
    val siblings: List<String>,
    val children: List<String>
) {

    /**
     * Key For Map objects to avoid confusion between deities with the same name
     * @return a tuple of deity name and pantheon
     */
    fun key(): Pair<String, String> {
        return Pair(name, pantheon)
    }

}
