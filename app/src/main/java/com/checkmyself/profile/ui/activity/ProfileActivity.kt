package com.checkmyself.profile.ui.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.checkmyself.R
import com.checkmyself.base.BaseActivity
import com.checkmyself.databinding.ProfileActivityBinding
import com.checkmyself.network.ResponseStatus
import com.checkmyself.network.Status
import com.checkmyself.onboarding.ui.adapters.SpinnerAdapter
import com.checkmyself.profile.model.api.request.UpdateProfileRequest
import com.checkmyself.profile.viewmodel.ProfileViewModel
import com.checkmyself.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*


@AndroidEntryPoint
class ProfileActivity : BaseActivity(), View.OnClickListener {
    companion object {
        fun startActivityForResult(
            activity: Activity,
            profileLauncher: ActivityResultLauncher<Intent>
        ) {
            Intent(activity, ProfileActivity::class.java).run {
                profileLauncher.launch(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }

    private lateinit var binding: ProfileActivityBinding
    private val profileViewModel: ProfileViewModel by viewModels()
    private var dob = ""
    private var genderList: ArrayList<String> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.profile_activity)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.profile))
        initClickListener()
        initObserver()
        Handler(Looper.getMainLooper()).postDelayed({
            init()
        }, 100)
    }

    private fun init() {
        CommonUtils.setBgColor(this, binding.crProfile.background, R.color.profile_top_bg_color)
        CommonUtils.setBgColor(this, binding.crBmi.background, R.color.bmi_bg_color)
        CommonUtils.setBgColor(this, binding.crCategory.background, R.color.category_bg_color)
        genderList.add(getString(R.string.select_gender))
        genderList.add(getString(R.string.male))
        genderList.add(getString(R.string.female))
//        genderList.add(getString(R.string.other))
        initGenderSpinnerAdapter()
        setUserDetails()
    }

    private fun initObserver() {
        lifecycleScope.launch {
            profileViewModel.updateProfileState.collect {
                when (it.status) {
                    Status.LOADING -> {
                        showHideProgress(it.data == null)
                    }
                    Status.SUCCESS -> {
                        if (it.data != null
                            && (it.code == ResponseStatus.STATUS_CODE_SUCCESS
                                    || it.code == ResponseStatus.STATUS_CODE_CREATED)
                        ) {
                            CommonUtils.showToast(this@ProfileActivity, it.data.message)
                            setResult(Activity.RESULT_OK)
                            profileViewModel.getMe()
                        }
                    }
                    Status.ERROR -> {
                        showHideProgress(false)
                        CommonUtils.showToast(this@ProfileActivity, it.message)
                    }
                }
            }
        }
        lifecycleScope.launch {
            profileViewModel.meApiState.collect {
                when (it.status) {
                    Status.LOADING -> {
                        showHideProgress(it.data == null)
                    }
                    Status.SUCCESS -> {
                        showHideProgress(false)
                        if (it.data != null && (it.code == ResponseStatus.STATUS_CODE_SUCCESS)) {
                            Pref.user = it.data.user
                            setUserDetails()
                        }
                    }
                    Status.ERROR -> {
                        showHideProgress(false)
                        CommonUtils.showToast(this@ProfileActivity, it.message)
                    }
                }
            }
        }
    }

    private fun setUserDetails() {
        val user = Pref.user
        binding.user = user
        if (!user?.bodyInfo?.gender.isNullOrEmpty()) {
            val gender = genderList.find { it.lowercase() == user?.bodyInfo?.gender!!.lowercase() }
            if (gender != null) {
                val index = genderList.indexOf(gender)
                if (index != -1 && index < genderList.size) {
                    binding.genderSpinner.setSelection(index)
                }
            }
        }
        if (!user?.bodyInfo?.dateOfBirth.isNullOrEmpty()) {
            binding.dobEdit.setText(
                DateFormatter.getFormattedDate(
                    DateFormatter.yyyy_MM_dd_DASH,
                    user!!.bodyInfo!!.dateOfBirth!!,
                    DateFormatter.dd_MM_yy_SLASH
                )
            )
            dob = user.bodyInfo?.dateOfBirth!!
        }
    }

    private fun initClickListener() {
        binding.onClickListener = this
        binding.firstEdit.addTextChangedListener(textWatcher)
        binding.lastEdit.addTextChangedListener(textWatcher)
        binding.dobEdit.addTextChangedListener(textWatcher)
        binding.weightEdit.addTextChangedListener(textWatcher)
        binding.heightEdit.addTextChangedListener(textWatcher)
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            updateButtonView()
        }

        override fun afterTextChanged(s: Editable?) {
        }
    }

    private fun updateButtonView() {
        enableDisableButton(
            binding.saveChangesBtn,
            (binding.firstEdit.text.toString().trim().isNotEmpty()
                    && binding.lastEdit.text.toString().trim().isNotEmpty()
                    && binding.dobEdit.text.toString().trim().isNotEmpty()
                    && binding.genderSpinner.selectedItemPosition > 0
                    && binding.heightEdit.text.toString().trim().isNotEmpty()
                    && binding.weightEdit.text.toString().trim().isNotEmpty()
                    )

        )
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ivBgImage -> {
                finish()
            }
            R.id.cr_dob, R.id.dob_edit -> {
                showDatePicker()
            }

            R.id.save_changes_btn -> {
                val gender = when (binding.genderSpinner.selectedItemPosition) {
                    1 -> Enums.Gender.MALE.toString()
                    2 -> Enums.Gender.FEMALE.toString()
                    3 -> Enums.Gender.OTHER.toString()
                    else -> ""
                }

                val request = UpdateProfileRequest(
                    binding.firstEdit.text.toString().trim(),
                    binding.lastEdit.text.toString().trim(),
                    dob,
                    gender,
                    binding.weightEdit.text.toString().trim().toDouble(),
                    binding.heightEdit.text.toString().trim().toDouble()
                )
                profileViewModel.updateProfile(request)
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val dpd = DatePickerDialog(
            this, R.style.DatePickerStyle, { _, year, monthOfYear, dayOfMonth ->
                calendar.set(year, monthOfYear, dayOfMonth)
                val selectedDate = DateFormatter.getFormattedByStringTimestamp(
                    DateFormatter.dd_MM_yy_SLASH, calendar.timeInMillis,
                    TimeZone.getTimeZone(DateFormatter.TIME_ZONE_UTC)
                )
                binding.dobEdit.setText(selectedDate)

                dob = DateFormatter.getFormattedByStringUTC(
                    DateFormatter.dd_MM_yy_SLASH, DateFormatter.yyyy_MM_dd_DASH,
                    selectedDate!!
                )
//                binding.dobEdit.setText(dob)

            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        dpd.datePicker.maxDate = System.currentTimeMillis()
        dpd.show()
        dpd.getButton(DatePickerDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(this, R.color.secondary_text_color))
        dpd.getButton(DatePickerDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(this, R.color.theme_color))
    }

    private fun initGenderSpinnerAdapter() {
        binding.genderSpinner.apply {
            adapter = SpinnerAdapter(this@ProfileActivity, R.layout.spinner_item, genderList)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View, position: Int, id: Long
                ) {
//                    subCategoryId = offersList[position].id
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }
    }

}