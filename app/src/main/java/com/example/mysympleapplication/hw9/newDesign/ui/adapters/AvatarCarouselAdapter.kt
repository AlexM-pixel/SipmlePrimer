 package com.example.mysympleapplication.hw9.newDesign.ui.adapters

    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.ImageView
    import androidx.recyclerview.widget.RecyclerView
    import com.example.mysympleapplication.R
    import com.google.android.material.card.MaterialCardView

    class AvatarCarouselAdapter(private val avatars: List<String>) :
        RecyclerView.Adapter<AvatarCarouselAdapter.AvatarHolder>() {

        private var selectedIndex = 0

        // Метод для обновления рамки при скролле
        fun setSelected(position: Int) {
            val oldIndex = selectedIndex
            selectedIndex = position
            notifyItemChanged(oldIndex)
            notifyItemChanged(selectedIndex)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvatarHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_avatar_carousel, parent, false)
            return AvatarHolder(view)
        }

        override fun onBindViewHolder(holder: AvatarHolder, position: Int) {
            val avatarName = avatars[position]
            holder.bind(avatarName, position == selectedIndex)
        }

        override fun getItemCount(): Int = avatars.size

        inner class AvatarHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val cardView = itemView.findViewById<MaterialCardView>(R.id.card_avatar_item)
            private val ivAvatar = itemView.findViewById<ImageView>(R.id.iv_avatar_img)

            fun bind(avatarName: String, isSelected: Boolean) {
                // Превращаем строку в R.drawable.id
                val context = itemView.context
                val resId = context.resources.getIdentifier(avatarName, "drawable", context.packageName)
                if (resId != 0) {
                    ivAvatar.setImageResource(resId)
                }

                // Включаем рамку, если это центральный элемент
                cardView.strokeWidth = if (isSelected) 2 else 0 // 8px толщина рамки

                // Если элемент не выбран, делаем его фон чуть прозрачнее
                ivAvatar.alpha = if (isSelected) 1.0f else 0.6f
            }
        }
    }
