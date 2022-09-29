package cosmeolaiocht.app

import com.szadowsz.cosmeolaiocht.deities.PantheonProcessor

fun main() {
    val pantheons = PantheonProcessor.process("./cribs/refs/real/religion/deities/pantheons","./cribs/refs/real/religion/deities/roles")

    // for human-readable debugging purposes
    pantheons.printAspectReports()
}
