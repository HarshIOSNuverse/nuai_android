package com.checkmyself.onboarding.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.checkmyself.R

class SpinnerAdapter(
    context: Context?, resourceId: Int, val list: List<Any>
) : ArrayAdapter<Any>(context!!, resourceId, list) {
    private var inflater: LayoutInflater? = null
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return rowView(convertView, position)
    }

    @SuppressLint("InflateParams")
    private fun rowView(convertView: View?, position: Int): View {
        val holder: ViewHolder
        var rowView = convertView
        if (rowView == null) {
            holder = ViewHolder()
            inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            rowView = inflater!!.inflate(
                R.layout.spinner_item,
                null,
                false
            )
            holder.txtTitle =
                rowView.findViewById<View>(R.id.text1) as TextView
            rowView.tag = holder
        } else {
            holder = rowView.tag as ViewHolder
        }
        /* when (list!![position]) {
             is Job -> {
                 holder.txtTitle!!.text = (list[position] as Job).title
             }
             is Offer -> {
                 holder.txtTitle!!.text = (list[position] as Offer).title
             }
             is Category -> {
                 holder.txtTitle!!.text = (list[position] as Category).name
             }
         }*/
        holder.txtTitle!!.text = (list[position] as String)
        return rowView!!
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val tv = view as TextView
        if (position == 0) {
            // Set the hint text color gray
            tv.setTextColor(ContextCompat.getColor(context, R.color.et_hint_color))
        } else {
            tv.setTextColor(ContextCompat.getColor(context, R.color.primary_text_color))
        }
        return view
    }

    override fun isEnabled(position: Int): Boolean {
        return position != 0
    }

    private inner class ViewHolder {
        var txtTitle: TextView? = null
    }
}