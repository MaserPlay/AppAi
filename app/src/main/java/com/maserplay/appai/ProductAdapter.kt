package com.maserplay.appai

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.maserplay.AppAi.R


@Suppress("NAME_SHADOWING")
internal class ProductAdapter(
    context: Context,
    private val layout: Int,
    private val layout2: Int,
    private val layout3: Int,
    private val layout4: Int,
    private val productList: ArrayList<Product>
) : ArrayAdapter<Product>(context, layout, layout2, productList as List<Product> )
{
    private val inflater: LayoutInflater
    private val who: ArrayList<Product> = productList

    init {
        inflater = LayoutInflater.from(context)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
            convertView = when (who[position].who) {
                1 -> {
                    inflater.inflate(layout, parent, false)
                }
                2 -> {
                    inflater.inflate(layout2, parent, false)
                }
                3 -> {
                    inflater.inflate(layout3, parent, false)
                }
                4 -> {
                    inflater.inflate(layout4, parent, false)
                }
                else -> {
                    Log.e("Adapter", "Error")
                    inflater.inflate(layout3, parent, false)
                }
            }
        val viewHolder = ViewHolder(convertView)
            convertView.tag = viewHolder
        val product = productList[position]
        viewHolder.nameView.text = product.name

        return convertView!!
    }

    private inner class ViewHolder(view: View) {
        val nameView: TextView

        init {
            nameView = view.findViewById(R.id.nameView)
        }
    }
}