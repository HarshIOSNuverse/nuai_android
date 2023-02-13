package com.nuai.onboarding.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.nuai.R
import com.nuai.base.BaseActivity
import com.nuai.databinding.TutorialActivityBinding
import com.nuai.onboarding.model.Tutorial
import com.nuai.onboarding.ui.adapters.TutorialAdapter
import com.nuai.utils.AnimationsHandler
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TutorialActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: TutorialActivityBinding
    private val listItem = ArrayList<Tutorial>()

    companion object {
        fun startActivity(activity: Activity) {
            Intent(activity, TutorialActivity::class.java).run {
                activity.startActivity(this)
                activity.finish()
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.tutorial_activity)
        binding.onClickLister = this
        setViewPager()
        initClick()
    }

    private fun setViewPager() {
        val tutorial0 = Tutorial(
            R.drawable.tutorial_0, getString(R.string.tutorial_0_title),
            getString(R.string.tutorial_0_message)
        )
        listItem.add(tutorial0)

        val tutorial1 = Tutorial(
            R.drawable.tutorial_1, getString(R.string.tutorial_1_title),
            getString(R.string.tutorial_1_message)
        )
        listItem.add(tutorial1)
        val tutorial2 = Tutorial(
            R.drawable.tutorial_2, getString(R.string.tutorial_2_title),
            getString(R.string.tutorial_2_message)
        )
        listItem.add(tutorial2)
        val tutorial3 = Tutorial(
            R.drawable.tutorial_3, getString(R.string.tutorial_3_title),
            getString(R.string.tutorial_3_message)
        )
        listItem.add(tutorial3)
        val tutorial4 = Tutorial(
            R.drawable.tutorial_4, getString(R.string.tutorial_4_title),
            getString(R.string.tutorial_4_message)
        )
        listItem.add(tutorial4)
//        val tutorial5 = Tutorial(
//            R.drawable.tutorial_5, getString(R.string.tutorial_5_title),
//            getString(R.string.tutorial_5_message)
//        )
//        listItem.add(tutorial5)

//        val tutorial6 = Tutorial(
//            R.drawable.tutorial_6, getString(R.string.tutorial_6_title),
//            getString(R.string.tutorial_6_message)
//        )
//        listItem.add(tutorial6)

        binding.viewPager.adapter = TutorialAdapter(this, listItem)
        binding.pageIndicator.setViewPager(binding.viewPager)
        binding.viewPager.currentItem = 0
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageSelected(position: Int) {
//                currentPage = position
                when (position) {
                    listItem.size - 1 -> {
                        binding.getStartedBtnText.visibility = View.VISIBLE
                    }
                    else -> {
                        binding.getStartedBtnText.visibility = View.INVISIBLE
                    }
                }
            }

            override fun onPageScrolled(pos: Int, arg1: Float, arg2: Int) {
            }

            override fun onPageScrollStateChanged(pos: Int) {
            }
        })
    }

    private fun initClick() {
        binding.onClickLister = this
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.get_started_btn_text -> {

            }
        }
    }

}