package fr.isen.zachee.ski_station

import kotlin.random.Random

data class Lift(
    val name: String = Random(0).toString(),
    val status: Boolean = true
)
