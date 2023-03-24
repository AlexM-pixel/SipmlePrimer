package com.example.mysympleapplication.hw9.newDesign.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.domain.model.Images

class ImagesRvAdapter : RecyclerView.Adapter<ImagesRvAdapter.ImagesHolder>() {
    val listImages = Images.values()
    var row_index = -1
    lateinit var onImageClick: (nameImage: String) -> Unit

    inner class ImagesHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemImage: ImageView = itemView.findViewById(R.id.image)
        fun onBind(position: Int) {
            val image = listImages[position].imageSource
            val drawable = ContextCompat.getDrawable(itemImage.context, image)
            itemImage.apply {
                setImageDrawable(drawable)               // itemImage.setImageByDrawable(listImages[position].imageSource)   тормозит
                itemView.setOnClickListener {
                    onImageClick.invoke(listImages[position].nameImage)
                    row_index = position
                    notifyDataSetChanged()
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesHolder {
        return ImagesHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ImagesHolder, position: Int) {
        holder.onBind(position)
        if (row_index == position) {
            holder.itemView.setBackgroundResource(R.drawable.background_rv_nd)
        } else {
            holder.itemView.setBackgroundResource(R.drawable.frame)
        }
    }

    override fun getItemCount(): Int {
        return listImages.size
    }
}