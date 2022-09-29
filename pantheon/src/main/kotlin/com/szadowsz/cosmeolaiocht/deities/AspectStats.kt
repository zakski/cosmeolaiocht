package com.szadowsz.cosmeolaiocht.deities

import java.math.RoundingMode
import java.text.DecimalFormat

data class AspectStats(
    val overall: Double,
    val major: Double,
    val minor: Double,
    val primordial: Double,
    val male: Double,
    val female: Double,
    val whoAreMajor: Double,
    val whoArePrimordial: Double,
    val whoAreMale: Double
) {
    companion object {
        private val decimalFormatter = initFormatter()

        private fun initFormatter(): DecimalFormat {
            val df = DecimalFormat("#.####")
            df.setRoundingMode(RoundingMode.HALF_UP)
            return df
        }

        private fun average(s: List<Int>): Double {
            // s.foldLeft((0.0, 1))((acc, i) => ((acc._1 + (i - acc._1) / acc._2), acc._2 + 1))._1
            return s.foldIndexed(0.0,{index, acc, i -> acc + (i - acc)/(index+1)})
        }

        private fun countDeities(deities: List<Deity>, cond: (Deity) -> Boolean): Double {
            return deities.count(cond) / deities.size.toDouble()
        }

        /**
         * Overall percentage of Deities that have this Aspect
         *
         * @param name    Aspect name
         * @param deities overall list of deities
         * @return 0.0 < X <= 1.0
         */
        private fun overallPercentage(name: String, deities: List<Deity>): Double {
            return countDeities(deities, {d -> d.aspects.any{a -> a.name == name}})
        }

        /**
         * Percentage of Major Deities that have this Aspect
         *
         * @param name    Aspect name
         * @param deities overall list of deities
         * @return 0.0 <= X <= 1.0
         */
        private fun majorPercentage(name: String, deities: List<Deity>): Double {
            return countDeities(deities, {d -> d.isMajor && d.aspects.any{a -> a.name == name}})
        }

        /**
         * Percentage of Minor Deities that have this Aspect
         *
         * @param name    Aspect name
         * @param deities overall list of deities
         * @return 0.0 <= X <= 1.0
         */
        private fun minorPercentage(name: String, deities: List<Deity>): Double {
            return countDeities(deities, {d -> !d.isMajor && d.aspects.any {a -> a.name == name}})
        }

        /**
         * Percentage of Primordial Deities that have this Aspect
         *
         * @param name    Aspect name
         * @param deities overall list of deities
         * @return 0.0 <= X <= 1.0
         */
        private fun primordialPercentage(name: String, deities: List<Deity>): Double {
            return countDeities(deities, {d -> d.isPrimordial && d.aspects.any {a -> a.name == name}})
        }

        /**
         * Overall percentage of Deities that have this Aspect
         *
         * @param name    Aspect name
         * @param deities overall list of deities
         * @return 0.0 <= X <= 1.0
         */
        private fun malePercentage(name: String, deities: List<Deity>): Double {
            return countDeities(deities, {d -> d.gender == Gender.male && d.aspects.any {a -> a.name == name}})
        }

        /**
         * Overall percentage of Deities that have this Aspect
         *
         * @param name    Aspect name
         * @param deities overall list of deities
         * @return 0.0 <= X <= 1.0
         */
        private fun femalePercentage(name: String, deities: List<Deity>): Double {
            return countDeities(deities, {d -> d.gender == Gender.female && d.aspects.any {a -> a.name == name}})
        }

        /**
         * Overall percentage of Deities that have this Aspect
         *
         * @param name    Aspect name
         * @param deities overall list of deities
         * @return 0.0 <= X <= 1.0
         */
        private fun whoAreMajorPercentage(name: String, deities: List<Deity>): Double {
            return countDeities(deities.filter{d -> d.aspects.any{a -> a.name == name}}, {d -> d.isMajor})
        }

        /**
         * Overall percentage of Deities that have this Aspect
         *
         * @param name    Aspect name
         * @param deities overall list of deities
         * @return 0.0 <= X <= 1.0
         */
        private fun whoArePrimordialPercentage(name: String, deities: List<Deity>): Double {
            return countDeities(deities.filter{d -> d.aspects.any {a -> a.name == name}}, {d -> d.isPrimordial})
        }

        /**
         * Overall percentage of Deities that have this Aspect
         *
         * @param name    Aspect name
         * @param deities overall list of deities
         * @return 0.0 <= X <= 1.0
         */
        private fun whoAreMalePercentage(name: String, deities: List<Deity>): Double {
            return countDeities(deities.filter{d -> d.aspects.any {a -> a.name == name}}, {d -> d.gender == Gender.male})
        }

        /**
         * Overall percentage of Deities that have this Aspect
         *
         * @param name    Aspect name
         * @param deities overall list of deities
         * @return 0.0 <= X <= 1.0
         */
        private fun averageNumberOfRelatedAspects(name: String, deities: List<Deity>): Double {
            return average(deities.filter{d -> d.aspects.any {a -> a.name == name}}.map{d -> d.aspects.size - 1})
        }
    }

    constructor(name: String, deities: List<Deity>) : this(
        decimalFormatter.format(overallPercentage(name, deities)).toDouble(),
        decimalFormatter.format(majorPercentage(name, deities)).toDouble(),
        decimalFormatter.format(minorPercentage(name, deities)).toDouble(),
        decimalFormatter.format(primordialPercentage(name, deities)).toDouble(),
        decimalFormatter.format(malePercentage(name, deities)).toDouble(),
        decimalFormatter.format(femalePercentage(name, deities)).toDouble(),
        decimalFormatter.format(whoAreMajorPercentage(name, deities)).toDouble(),
        decimalFormatter.format(whoArePrimordialPercentage(name, deities)).toDouble(),
        decimalFormatter.format(whoAreMalePercentage(name, deities)).toDouble()
    )
}