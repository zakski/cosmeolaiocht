package cosmeolaiocht.app

import com.szadowsz.cosmeolaiocht.deities.PantheonProcessor

fun main() {
    val pantheons = PantheonProcessor.process("./data/pantheons","./data/roles")

    // for human-readable debugging purposes
    pantheons.printAspectReports()
}
