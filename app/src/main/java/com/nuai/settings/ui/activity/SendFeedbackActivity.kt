package com.nuai.settings.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.nuai.R
import com.nuai.base.BaseActivity
import com.nuai.databinding.SendFeedbackActivityBinding
import com.nuai.network.ResponseStatus
import com.nuai.network.Status
import com.nuai.profile.model.api.request.SendFeedbackRequest
import com.nuai.profile.viewmodel.ProfileViewModel
import com.nuai.utils.AnimationsHandler
import com.nuai.utils.CommonUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SendFeedbackActivity : BaseActivity(), View.OnClickListener {
    companion object {
        fun startActivity(activity: Activity) {
            Intent(activity, SendFeedbackActivity::class.java).run {
                activity.startActivity(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }

    private lateinit var binding: SendFeedbackActivityBinding
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.send_feedback_activity)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.app_name))
        showToolbarIcon(true)
        initClickListener()
        initObserver()
        init()
    }

    private fun init() {
    }

    private fun initObserver() {
        lifecycleScope.launch {
            profileViewModel.sendFeedbackState.collect {
                when (it.status) {
                    Status.LOADING -> {
                        showHideProgress(it.data == null)
                    }
                    Status.SUCCESS -> {
                        if (it.data != null && (it.code == ResponseStatus.STATUS_CODE_SUCCESS)) {
                            CommonUtils.showToast(this@SendFeedbackActivity, it.data.message)
                            finish()
                        }
                    }
                    Status.ERROR -> {
                        showHideProgress(false)
                        CommonUtils.showToast(this@SendFeedbackActivity, it.message)
                    }
                }
            }
        }
    }

    private fun initClickListener() {
        binding.onClickListener = this
        binding.feedbackTitleEdit.addTextChangedListener(textWatcher)
        binding.feedbackMessageEdit.addTextChangedListener(textWatcher)
        enableDisableButton(binding.submitBtn, true)
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        @SuppressLint("SetTextI18n")
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (binding.feedbackTitleEdit.hasFocus()) {
                binding.tvFeedbackTitleLength.text =
                    "${binding.feedbackTitleEdit.text.toString().length}/30"
            } else if (binding.feedbackMessageEdit.hasFocus()) {
                binding.tvFeedbackMessageLength.text =
                    "${binding.feedbackMessageEdit.text.toString().length}/300"
            }
//            updateButtonView()
        }

        override fun afterTextChanged(s: Editable?) {
        }
    }

//    private fun updateButtonView() {
//        enableDisableButton(
//            binding.submitBtn,
//            (binding.feedbackTitleEdit.text.toString().trim().isNotEmpty()
//                    && binding.feedbackMessageEdit.text.toString().trim().isNotEmpty()
//                    )
//
//        )
//    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.submit_btn -> {
                if (binding.feedbackTitleEdit.text.toString().trim().isEmpty()) {
                    CommonUtils.showToast(this, getString(R.string.pls_enter_feedback_title))
                } else if (binding.feedbackMessageEdit.text.toString().trim().isEmpty()) {
                    CommonUtils.showToast(this, getString(R.string.pls_enter_feedback))
                } else {
                    val request = SendFeedbackRequest(
                        binding.feedbackTitleEdit.text.toString().trim(),
                        binding.feedbackMessageEdit.text.toString().trim(),
                        binding.ratingBar.rating
                    )
                    profileViewModel.sendFeedback(request)
                }
            }

        }
    }

}