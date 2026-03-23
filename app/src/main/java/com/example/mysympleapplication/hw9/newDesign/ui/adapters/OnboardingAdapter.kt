package com.example.mysympleapplication.hw9.newDesign.ui.adapters


import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mysympleapplication.R

// Модель одного слайда
data class OnboardingSlide(
    val title: String,
    val subtitle: String? = null,
    val imageRes: Int,
    val showInput: Boolean = false
)

class OnboardingAdapter(
    private val slides: List<OnboardingSlide>,
    private val onBankNameChanged: (String) -> Unit // Колбэк для передачи текста во фрагмент
) : RecyclerView.Adapter<OnboardingAdapter.SlideViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlideViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_onboarding_slide, parent, false)
        return SlideViewHolder(view)
    }

    override fun onBindViewHolder(holder: SlideViewHolder, position: Int) {
        holder.bind(slides[position])
    }

    override fun getItemCount(): Int = slides.size

    inner class SlideViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        private val tvSubtitle: TextView = itemView.findViewById(R.id.tv_subtitle)
        private val ivImage: ImageView = itemView.findViewById(R.id.iv_image)
        private val etBankInput: EditText = itemView.findViewById(R.id.et_bank_input)

        fun bind(slide: OnboardingSlide) {
            tvTitle.text = slide.title
            ivImage.setImageResource(slide.imageRes)

            // Подзаголовок
            if (slide.subtitle != null) {
                tvSubtitle.text = slide.subtitle
                tvSubtitle.visibility = View.VISIBLE
            } else {
                tvSubtitle.visibility = View.GONE
            }

            // Поле ввода
            if (slide.showInput) {
                etBankInput.visibility = View.VISIBLE

                // Слушаем, что вводит юзер, и отправляем наверх
                etBankInput.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        onBankNameChanged(s.toString().trim())
                    }
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                })
            } else {
                etBankInput.visibility = View.GONE
            }
        }
    }
}