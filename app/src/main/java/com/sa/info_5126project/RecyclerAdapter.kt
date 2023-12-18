package com.sa.info_5126project

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class RecyclerAdapter(private var dataSet: List<Book>) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textViewAuthor: TextView = view.findViewById(R.id.textViewAuthor)
        var textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        var bookImage: ImageView = view.findViewById(R.id.imageView)

        // Initialize values
        init {
            view.setOnClickListener {
                val intent = Intent(view.context, BookDetailActivity::class.java)
                val book = dataSet[adapterPosition]

                intent.putExtra("rank", book.rank)
                intent.putExtra("author", book.author)
                intent.putExtra("title", book.title)
                intent.putExtra("description", book.description)
                intent.putExtra("book_image", book.book_image)
                intent.putExtra("amazon_product_url", book.amazon_product_url)

                view.context.startActivity(intent)
            }
        }
    }

    // Update data function
    fun updateData(newData: List<Book>) {
        dataSet = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_item_card_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewAuthor.text = dataSet[position].author
        holder.textViewTitle.text = dataSet[position].title
        Picasso.get().load(dataSet[position].book_image).into(holder.bookImage)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}
