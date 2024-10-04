package org.dbs.service.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("GroceryItem")
data class FakedDocument(
    @field:Id
    val id: String,
    val name: String,
    val itemQuantity: Int,
    val category: String
)