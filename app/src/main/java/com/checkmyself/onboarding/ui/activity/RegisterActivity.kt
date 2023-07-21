package com.checkmyself.onboarding.ui.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.checkmyself.R
import com.checkmyself.base.BaseActivity
import com.checkmyself.databinding.RegisterActivityBinding
import com.checkmyself.onboarding.model.api.request.RegisterRequest
import com.checkmyself.onboarding.ui.adapters.SpinnerAdapter
import com.checkmyself.utils.AnimationsHandler
import com.checkmyself.utils.CommonUtils
import com.checkmyself.utils.DateFormatter
import com.checkmyself.utils.Enums
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList


@AndroidEntryPoint
class RegisterActivity : BaseActivity(), View.OnClickListener {
    companion object {
        fun startActivity(activity: Activity) {
            Intent(activity, RegisterActivity::class.java).run {
                activity.startActivity(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }

    private lateinit var binding: RegisterActivityBinding
    private var dob = ""
    private var genderList: ArrayList<String> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.register_activity)
        initClickListener()
        init()
    }

    private fun init() {
        genderList.add(getString(R.string.select_gender))
        genderList.add(getString(R.string.male))
        genderList.add(getString(R.string.female))
//        genderList.add(getString(R.string.other))
        initGenderSpinnerAdapter()
    }

    private fun initClickListener() {
        binding.onClickListener = this
        binding.firstEdit.addTextChangedListener(textWatcher)
        binding.lastEdit.addTextChangedListener(textWatcher)
        binding.emailEdit.addTextChangedListener(textWatcher)
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
            binding.registerBtn,
            (binding.firstEdit.text.toString().trim().isNotEmpty()
                    && binding.lastEdit.text.toString().trim().isNotEmpty()
                    && binding.emailEdit.text.toString().trim().isNotEmpty()
                    && binding.dobEdit.text.toString().trim().isNotEmpty()
                    && binding.genderSpinner.selectedItemPosition > 0
                    && binding.heightEdit.text.toString().trim().isNotEmpty()
                    && binding.weightEdit.text.toString().trim().isNotEmpty()
                    )

        )
    }

    override fun onClick(v: View?) {
        val errorColor = ContextCompat.getColor(this, R.color.error_msg_text_color)
        val normalColor = ContextCompat.getColor(this, R.color.et_hint_color)
        when (v!!.id) {
            R.id.ivBgImage -> {
                finish()
            }
            R.id.iv_email -> {
                binding.emailEdit.setText("")
                binding.emailDivider.setBackgroundColor(normalColor)
                binding.emailErrorText.visibility = View.GONE
                binding.ivEmail.visibility = View.GONE
            }

            R.id.cr_dob, R.id.dob_edit -> {
                showDatePicker()
            }

            R.id.register_btn -> {
                binding.firstDivider.setBackgroundColor(normalColor)
                binding.firstErrorText.visibility = View.GONE
                binding.lastDivider.setBackgroundColor(normalColor)
                binding.lastErrorText.visibility = View.GONE
                binding.emailDivider.setBackgroundColor(normalColor)
                binding.emailErrorText.visibility = View.GONE
                val email = binding.emailEdit.text.toString().trim()
                val firstName = binding.firstEdit.text.toString().trim()
                if (email.isEmpty()) {
                    binding.emailErrorText.text = getString(R.string.pls_enter_email)
                    return
                } else if (!CommonUtils.isValidEmail(email)) {
                    binding.ivEmail.setImageResource(R.drawable.cross)
                    binding.ivEmail.visibility = View.VISIBLE
                    binding.ivEmail.isEnabled = true
                    binding.emailDivider.setBackgroundColor(errorColor)
                    binding.emailErrorText.text = getString(R.string.pls_enter_valid_email)
                    binding.emailErrorText.visibility = View.VISIBLE
                    return
                } else if (firstName.isEmpty()) {
                    binding.firstErrorText.text = getString(R.string.pls_enter_first_name)
                    return
                } else {
                    val gender = when (binding.genderSpinner.selectedItemPosition) {
                        1 -> Enums.Gender.MALE.toString()
                        2 -> Enums.Gender.FEMALE.toString()
                        3 -> Enums.Gender.OTHER.toString()
                        else -> ""
                    }
                    val request = RegisterRequest(
                        binding.firstEdit.text.toString().trim(),
                        binding.lastEdit.text.toString().trim(),
                        binding.emailEdit.text.toString().trim(),
                        dob,
                        gender,
                        binding.weightEdit.text.toString().trim().toDouble(),
                        binding.heightEdit.text.toString().trim().toDouble(),
                        null
                    )
                    CreatePasswordActivity.startActivity(this, request)
                }
            }
            R.id.already_have_account_text -> {
                LoginActivity.startActivity(this)
                finish()
                AnimationsHandler.playActivityAnimation(
                    this, AnimationsHandler.Animations.RightToLeft
                )
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
            adapter = SpinnerAdapter(this@RegisterActivity, R.layout.spinner_item, genderList)
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