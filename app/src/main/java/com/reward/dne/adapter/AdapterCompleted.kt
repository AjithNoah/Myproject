package com.reward.dne.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.reward.dne.R
import com.reward.dne.model.Offerlist
import com.reward.dne.model.PendingModel
import com.reward.dne.utils.CustomTextViewBold
import com.reward.dne.utils.CustomTextViewNormal


class AdapterCompleted(val context: Context, val list: ArrayList<Offerlist>) : RecyclerView.Adapter<AdapterCompleted.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): AdapterCompleted.ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_completed, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: AdapterCompleted.ViewHolder, position: Int) {

        val offerList = list.get(position)

        holder.textAppName.text = offerList.name
        holder.textDescription.text = "Completed & earned "+offerList.ch_points+" points"
        val url = context.getString(R.string.image_url)+offerList.image
        Glide.with(context).load(url)
                .apply(RequestOptions().placeholder(R.drawable.android_default).error(R.drawable.android_default))
                .into(holder.imgApplogo)
        //holder.imgApplogo.setImageResource(offerList.image)


        /*holder.cardDetail.setOnClickListener {
            val intent = Intent(context, AppDetailActivity::class.java)
            intent.putExtra("appname",offerList.name)
            intent.putExtra("appicon",offerList.image)
            context.startActivity(intent)
        }*/

    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val cardDetail = itemView.findViewById(R.id.cardDetail) as CardView
        val textAppName  = itemView.findViewById(R.id.textAppName) as CustomTextViewBold
        val textDescription = itemView.findViewById<CustomTextViewNormal>(R.id.textDescription)
        val imgApplogo = itemView.findViewById<ImageView>(R.id.imgApplogo)

    }
}