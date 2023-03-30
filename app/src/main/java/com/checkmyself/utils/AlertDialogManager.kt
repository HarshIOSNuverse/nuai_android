package com.checkmyself.utils

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.checkmyself.R
import com.checkmyself.databinding.DialogConfirmBinding
import com.checkmyself.databinding.DialogInformationBinding
import com.checkmyself.interfaces.DialogClickListener

object AlertDialogManager {

    fun showConfirmationDialog(
        activity: Activity,
        title: String? = null,
        message: String? = null,
        button1Message: String? = null,
        button1Color: Int = ContextCompat.getColor(activity, R.color.white),
        button1Drawable: Int = R.drawable.rc_red_filled_c25,
        button2Message: String? = null,
        button2Color: Int = ContextCompat.getColor(activity, R.color.white),
        button2Drawable: Int = R.drawable.rc_black_filled_c25,
        cancelable: Boolean = false,
        dialogClickListener: DialogClickListener? = null
    ) {
        Dialog(activity).apply {
//            requestWindowFeature(Window.FEATURE_NO_TITLE)
//            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCancelable(cancelable)
            val dialogConfirmBinding: DialogConfirmBinding = DataBindingUtil.inflate(
                LayoutInflater.from(activity),
                R.layout.dialog_confirm,
                null,
                false
            )
            setContentView(dialogConfirmBinding.root)
//            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialogConfirmBinding.title = title
            dialogConfirmBinding.message = message
            dialogConfirmBinding.button1Message = button1Message
            dialogConfirmBinding.button1.setTextColor(button1Color)
            if (button1Drawable != 0)
                dialogConfirmBinding.button1.setBackgroundResource(button1Drawable)
            dialogConfirmBinding.button1.setOnClickListener {
                dialogClickListener?.onButton1Clicked()
                dismiss()
            }
            dialogConfirmBinding.button2Message = button2Message
            dialogConfirmBinding.button2.setTextColor(button2Color)
            if (button2Drawable != 0)
                dialogConfirmBinding.button2.setBackgroundResource(button2Drawable)
            dialogConfirmBinding.button2.setOnClickListener {
                dialogClickListener?.onButton2Clicked()
                dismiss()
            }
            dialogConfirmBinding.ivClose.setOnClickListener {
                dismiss()
            }
        }.run {
            show()
            window?.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )
            window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
        }
    }

    fun showInformationDialog(
        activity: Activity,
        icon: Int = 0,
        title: String? = null,
        msg: String? = null,
        button1Message: String? = null,
        button1Color: Int = ContextCompat.getColor(activity, R.color.white),
        button1Drawable: Int = R.drawable.rc_black_filled_c25,
        isClose: Boolean = false,
        cancelable: Boolean = false,
        dialogClickListener: DialogClickListener? = null
    ) {
        Dialog(activity).apply {
//            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(cancelable)
            val informationBinding: DialogInformationBinding = DataBindingUtil.inflate(
                LayoutInflater.from(activity), R.layout.dialog_information, null, false
            )
            setContentView(informationBinding.root)
//            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            if (icon != 0) {
                informationBinding.imgTick.setImageResource(icon)
                informationBinding.imgTick.visibility = View.VISIBLE
            } else {
                informationBinding.imgTick.visibility = View.GONE
            }

            if (!title.isNullOrEmpty()) {
                informationBinding.txtTitle.text = title
                informationBinding.txtTitle.visibility = View.VISIBLE
            } else {
                informationBinding.txtTitle.visibility = View.GONE
            }

            informationBinding.imgClose.visibility = if (isClose) View.VISIBLE else View.GONE
            if (!msg.isNullOrEmpty()) {
                informationBinding.txtMessage.visibility = View.VISIBLE
                informationBinding.txtMessage.text = msg
            } else {
                informationBinding.txtMessage.visibility = View.GONE
                informationBinding.txtMessage.text = ""
            }
            informationBinding.button1.text = button1Message
            informationBinding.button1.setTextColor(button1Color)
            if (button1Drawable != 0)
                informationBinding.button1.setBackgroundResource(button1Drawable)
            informationBinding.button1.setOnClickListener {
                dialogClickListener?.onButton1Clicked()
                dismiss()
            }
            informationBinding.button1.visibility =
                if (!button1Message.isNullOrEmpty()) View.VISIBLE else View.GONE
            informationBinding.imgClose.setOnClickListener {
                dialogClickListener?.onCloseClicked()
                dismiss()
            }
        }.run {
            show()
            window?.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )
            window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
        }
    }
}
