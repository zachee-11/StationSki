package fr.isen.zachee.ski_station.dataclass

import java.io.Serializable
import kotlin.random.Random

data class Lift(
    val name: String = Random(0).toString(),
    val status: Boolean = true,
    var listslopes: List<NameSlopes> = listOf()
): Serializable
