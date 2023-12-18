package com.sa.info_5126project

data class APIFormat(
    var copyright: String,
    var results: Results
)
data class Results (
    var books:List<Book>
)

data class Book(
    var title: String,
    var author: String,
    var description: String,
    var url: String,
    var category: String,
    var book_image:String,
    var rank:Int,
    var amazon_product_url: String,
    )

