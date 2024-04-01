package fr.isen.zachee.ski_station.dataclass

import kotlin.random.Random

data class Slope(
    val name: String = Random(0).toString(),
    val color: String = "",
    var status: Boolean = true
)
