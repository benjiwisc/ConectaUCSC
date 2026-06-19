package com.ucsc.conectaucsc.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserStats(
    val materias_count: Int,
    val promedio_general: Double,
    val sesiones_count: Int
)
