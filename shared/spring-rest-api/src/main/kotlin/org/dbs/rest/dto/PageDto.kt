package org.dbs.rest.dto

data class PageDto(
    val limit: Int, // лимит на страницу
    val items: Int, // количество товаров на текущей странице
    val current: Int, // текущая страница
    val last: Int, // последняя страница
    val total: Int // общее количество элементов
)