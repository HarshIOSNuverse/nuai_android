package com.checkmyself.onboarding.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.checkmyself.R
import com.checkmyself.base.BaseActivity
import com.checkmyself.databinding.TutorialActivityBinding
import com.checkmyself.home.ui.activity.HomeActivity
import com.checkmyself.onboarding.model.Tutorial
import com.checkmyself.onboarding.ui.adapters.TutorialAdapter
import com.checkmyself.utils.AnimationsHandler
import com.checkmyself.utils.IntentConstant
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TutorialActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: TutorialActivityBinding
    private val listItem = ArrayList<Tutorial>()
    private var from = From.SIGNUP

    companion object {
        object From {
            const val SIGNUP = 1
            const val SETTINGS = 2
        }

        fun startActivity(activity: Activity, from: Int) {
            Intent(activity, TutorialActivity::class.java).apply {
                putExtra(IntentConstant.FROM, from)
            }.run {
                activity.startActivity(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.tutorial_activity)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.app_name))
        showToolbarIcon(true)
        binding.onClickLister = this
        setViewPager()
        initClick()
        init()
    }

    private fun init() {
        intent?.run {
            from = getIntExtra(IntentConstant.FROM, From.SIGNUP)
            if (from == From.SETTINGS) {
                binding.llToolbar.visibility = View.VISIBLE
                binding.icon.visibility = View.GONE
                binding.titleText.visibility = View.GONE
                binding.getStartedBtnText.text = getString(R.string.got_it)
            } else {
                binding.llToolbar.visibility = View.GONE
                binding.icon.visibility = View.VISIBLE
                binding.titleText.visibility = View.VISIBLE
                binding.getStartedBtnText.text = getString(R.string.get_started)
            }
        }
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
                if (from == From.SETTINGS) {
                    finish()
                } else {
                    HomeActivity.startActivity(this)
                }
            }
        }
    }

}