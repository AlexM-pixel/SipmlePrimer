package com.example.mysympleapplication.hw9.newDesign.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.mysympleapplication.R
import com.google.android.material.card.MaterialCardView

class AvatarCarouselAdapter(
    private val avatars: List<String>,
    private val onItemClick: (Int) -> Unit // <--- ДОБАВЛЯЕМ СЛУШАТЕЛЬ КЛИКА
) : RecyclerView.Adapter<AvatarCarouselAdapter.AvatarHolder>() {

    private var selectedIndex = -1

    fun setSelected(position: Int) {
        val oldIndex = selectedIndex
        selectedIndex = position
        notifyItemChanged(oldIndex)
        notifyItemChanged(selectedIndex)
    }

    override fun getItemCount(): Int = Int.MAX_VALUE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvatarHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_avatar_carousel, parent, false)
        return AvatarHolder(view)
    }

    override fun onBindViewHolder(holder: AvatarHolder, position: Int) {
        val realPosition = position % avatars.size
        val avatarName = avatars[realPosition]

        holder.bind(avatarName, position == selectedIndex, position) // Передаем position
    }

    inner class AvatarHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView = itemView.findViewById<MaterialCardView>(R.id.card_avatar_item)
        private val ivAvatar = itemView.findViewById<ImageView>(R.id.iv_avatar_img)
        private val badgeCheck = itemView.findViewById<View>(R.id.fl_check_badge)

        fun bind(avatarName: String, isSelected: Boolean, absolutePosition: Int) {
            val context = itemView.context
            val resId = context.resources.getIdentifier(avatarName, "drawable", context.packageName)
            if (resId != 0) {
                ivAvatar.setImageResource(resId)
            }

//            cardView.strokeWidth = if (isSelected) 3 else 0
//            ivAvatar.alpha = if (isSelected) 1.0f else 0.6f
            if (isSelected) {
                // Если элемент в ЦЕНТРЕ:
                cardView.strokeWidth = 3 // Показываем зеленую рамку (8px)
                badgeCheck.visibility = View.VISIBLE // Показываем галочку снизу
                ivAvatar.alpha = 1.0f
            } else {
                // Если элемент СБОКУ:
                cardView.strokeWidth = 0 // Убираем рамку
                badgeCheck.visibility = View.GONE // Прячем галочку
                ivAvatar.alpha = 0.6f // прозрачность
            }

            // <--- КЛИК ПО БОКОВОМУ ЭЛЕМЕНТУ --->
            itemView.setOnClickListener {
                onItemClick(absolutePosition)
            }
        }
    }
}