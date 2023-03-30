package com.checkmyself.utils

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

class BindingViewHolder<T>(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
    val binding: T = DataBindingUtil.bind<ViewDataBinding>(itemView!!) as T

}