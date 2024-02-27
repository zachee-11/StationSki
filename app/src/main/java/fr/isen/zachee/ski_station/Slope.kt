package fr.isen.zachee.ski_station

import kotlin.random.Random

data class Slope(
    val lift: Lift,
    val name: String = Random(0).toString(),
    val color: String = "",
    var status: Boolean = true
)
