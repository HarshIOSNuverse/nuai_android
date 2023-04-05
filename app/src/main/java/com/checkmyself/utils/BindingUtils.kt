package com.checkmyself.utils

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.util.*

@BindingAdapter("android:visibility")
fun hideVisibilityIfNull(view: View, value: String?) {
    view.visibility = if (value.isNullOrEmpty() || value == Enums.N) View.GONE else View.VISIBLE
}

@BindingAdapter("android:visibility")
fun hideVisibility(view: View, value: Boolean) {
    view.visibility = if (value) View.VISIBLE else View.GONE
}

@BindingAdapter("android:visibility")
fun hideVisibilityIfNull(view: View, value: Double?) {
    view.visibility = if (value == null || value == 0.0) View.GONE else View.VISIBLE
}

@BindingAdapter("android:text")
fun setFloatText(view: View, value: Float?) {
    if (view is TextView) {
        view.text = if (value != null) {
            if (value % 1 == 0f) {
                String.format(Locale.US, "%,.0f", value)
            } else {
                "$value"
            }
        } else "0"
    }
}

@BindingAdapter(value = ["prefix_message", "rating", "postfix_message"], requireAll = false)
fun setRatingText(view: View, prefix: String?, value: Float?, postFix: String?) {
    val prefixMessage = if (!prefix.isNullOrEmpty()) prefix else ""
    val postFixMessage = if (!postFix.isNullOrEmpty()) postFix else ""
    val message = StringBuilder()
    message.append(prefixMessage)
    if (value != null) {
        if (value % 1 == 0f) {
            message.append(String.format(Locale.US, "%,.0f", value))
        } else {
            message.append("$value")
        }
    } else message.append("0")
    message.append(postFixMessage)
    if (view is TextView) {
        view.text = message
    }
}

@BindingAdapter(value = ["prefix_message", "review", "postfix_message"], requireAll = false)
fun setReviewText(view: View, prefix: String?, value: Long?, postFix: String?) {
    val prefixMessage = if (!prefix.isNullOrEmpty()) prefix else ""
    val postFixMessage = if (!postFix.isNullOrEmpty()) postFix else ""
    val message = StringBuilder()
    message.append(prefixMessage)
    message.append(if (value != null) "$value" else "0")
    message.append(postFixMessage)
    if (view is TextView) {
        view.text = message
    }
}

@BindingAdapter("onClick")
fun onClick(view: View, onClick: () -> Unit) {
    view.setOnClickListener {
        onClick()
    }
}
