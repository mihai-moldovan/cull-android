package ro.chiralinteriordesign.cull.model.shop

import java.io.Serializable

/**
 * Created by Mihai Moldovan on 24.06.2021.
 */
data class FiltersData(
    val categories: List<FilterItem>,
    val colors: List<FilterItem>,
    val materials: List<FilterItem>,
    val minPrice: Int,
    val maxPrice: Int,
) : Serializable


data class FilterItem(
    val key: String,
    val label: String,
    val photo: String?,
) : Serializable

