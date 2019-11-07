package com.reward.dne.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.reward.dne.R
import com.reward.dne.model.ModelAppCheckOffer
import com.reward.dne.utils.CustomTextViewBold
import com.reward.dne.utils.CustomTextViewNormal

class AdapterOffers(val context: Context,val list: ArrayList<ModelAppCheckOffer>) : RecyclerView.Adapter<AdapterOffers.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): AdapterOffers.ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_offers, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: AdapterOffers.ViewHolder, position: Int) {

        val offerList = list.get(position)

        holder.textAppName.text = offerList.appName
        holder.textDescription.text = "Download and Earn "+offerList.appPoints+" free points"
        val url = context.getString(R.string.image_url)+offerList.appImg
        if (offerList.appImg!=""){
            Glide.with(context).load(url)
                    .apply(RequestOptions().placeholder(R.drawable.android_default).error(R.drawable.android_default))
                    .into(holder.imgApplogo)
        }
        val type = offerList.type
        if (type!=null){
            when {
                offerList.type ==1 -> holder.imgDownload.setImageResource(R.mipmap.download)
                offerList.type ==2 -> holder.imgDownload.setImageResource(R.mipmap.story)
                offerList.type == 3 -> holder.imgDownload.setImageResource(R.mipmap.form)
            }
        }

        /*val url = context.getString(R.string.image_url)+offerList.app_image

        Glide.with(context).load(url).into(holder.imgApplogo)*/
        //holder.imgApplogo.setImageResource(offerList.app_image)


       /* holder.cardDetail.setOnClickListener {
            val intent = Intent(context,AppDetailActivity::class.java)
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
        val imgDownload = itemView.findViewById<ImageView>(R.id.imgDownload)

    }
}