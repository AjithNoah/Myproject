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
import com.reward.dne.activity.AppDetailActivity
import com.reward.dne.model.HotAppList
import com.reward.dne.utils.CustomTextViewBold
import com.reward.dne.utils.CustomTextViewNormal

class AdapterHotApps (val context: Context,val list: ArrayList<HotAppList>) : RecyclerView.Adapter<AdapterOffers.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): AdapterOffers.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_offers, parent, false)
        return AdapterOffers.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: AdapterOffers.ViewHolder, position: Int) {

        val offerList:HotAppList = list.get(position)

        holder.textAppName.text = offerList.name
        holder.textDescription.text = "Download and Earn ${offerList.points} free points"
        val url = context.getString(R.string.image_url)+offerList.image
        Glide.with(context).load(url)
                .apply(RequestOptions().placeholder(R.drawable.android_default).error(R.drawable.android_default))
                .into(holder.imgApplogo)

        holder.cardDetail.setOnClickListener {
            val intent = Intent(context, AppDetailActivity::class.java)
            intent.putExtra("app_id", offerList.id)
            intent.putExtra("type", offerList.type)
            intent.putExtra("app_name", offerList.name)
            intent.putExtra("offer_id", offerList.offer_id)
            context.startActivity(intent)
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val cardDetail = itemView.findViewById(R.id.cardDetail) as CardView
        val textAppName  = itemView.findViewById(R.id.textAppName) as CustomTextViewBold
        val textDescription = itemView.findViewById<CustomTextViewNormal>(R.id.textDescription)
        val imgApplogo = itemView.findViewById<ImageView>(R.id.imgApplogo)

    }
}