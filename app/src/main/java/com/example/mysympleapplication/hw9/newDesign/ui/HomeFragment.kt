package com.example.mysympleapplication.hw9.newDesign.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.ViewCompat
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.base.BaseFragment
import com.example.mysympleapplication.hw9.newDesign.ui.adapters.ViewPagerAdapter

class HomeFragment : BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_nd, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //set viewPager
        val viewPager2 = view.findViewById<ViewPager2>(R.id.viewPager_home)
        viewPager2.adapter = ViewPagerAdapter()
        val transformerSideMargin =
            pixelToDp(activity!!, resources.getDimension(R.dimen.cardView_margin) * 2)
        viewPager2.setShowSideItems(transformerSideMargin, transformerSideMargin)
    }

    fun ViewPager2.setShowSideItems(pageMarginPx: Int, offsetPx: Int) {

        clipToPadding = false
        clipChildren = false
        offscreenPageLimit = 4

        setPageTransformer { page, position ->

            val offset = position * -(2 * offsetPx + pageMarginPx)
            if (this.orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
                if (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                    page.translationX = -offset
                } else {
                    page.translationX = offset
                }
            } else {
                page.translationY = offset
            }
        }
    }

    fun pixelToDp(context: Context, pixelValue: Float): Int {
        val scale: Float = context.resources.displayMetrics.density
        return (pixelValue / scale + 0.5f).toInt()
    }
}