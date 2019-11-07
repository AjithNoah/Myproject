package com.reward.dne.adapter

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.reward.dne.R
import com.reward.dne.model.modelFAQ
import com.reward.dne.utils.CustomTextViewNormal
import com.reward.dne.utils.CustomTextViewSemiBold

class AdapterFAQ (val context: Context,var faqlist:ArrayList<modelFAQ>) : RecyclerView.Adapter<AdapterFAQ.ViewHolder>() {

    var expandable:Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): AdapterFAQ.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_faq, parent, false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return faqlist.size
    }

    override fun onBindViewHolder(holder: AdapterFAQ.ViewHolder, position: Int) {

        var model: modelFAQ = faqlist[position]

        holder.textTitle.text = model.title
        holder.textDescription.text = model.description

        holder.cardFaq.setOnClickListener {
            if (expandable){
                holder.textDescription.visibility = View.GONE
                holder.imgArrow.setImageResource(R.mipmap.arrowdown)
                expandable = false
            }
            else{
                holder.textDescription.visibility = View.VISIBLE
                holder.imgArrow.setImageResource(R.mipmap.arrowup)
                expandable = true
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgArrow = itemView.findViewById<ImageView>(R.id.imgArrow)
        val textDescription = itemView.findViewById<CustomTextViewNormal>(R.id.textDescription)
        val cardFaq = itemView.findViewById<CardView>(R.id.cardFaq)
        val textTitle = itemView.findViewById<CustomTextViewSemiBold>(R.id.textTitle);



    }
}