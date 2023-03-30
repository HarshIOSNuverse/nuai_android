package com.checkmyself.onboarding.ui.adapters

import android.content.Context
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.checkmyself.R
import com.checkmyself.databinding.ItemTutorialBinding
import com.checkmyself.onboarding.model.Tutorial
import java.util.*

class TutorialAdapter(private val context: Context, private val listItems: ArrayList<Tutorial>) :
    PagerAdapter() {

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return listItems.size
    }

    override fun instantiateItem(view: ViewGroup, position: Int): Any {
        val binding: ItemTutorialBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context), R.layout.item_tutorial, view, false
        )

        val tutorial = listItems[position]
        binding.ivTutorial.setImageResource(tutorial.image)
        binding.tvTutorialHeader.text = tutorial.header
        binding.tvTutorialDesc.text = tutorial.description

        view.addView(binding.root, 0)
        return binding.root
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}

    override fun saveState(): Parcelable? {
        return null
    }
}
