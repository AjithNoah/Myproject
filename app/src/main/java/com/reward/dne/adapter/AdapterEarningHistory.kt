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
import com.reward.dne.model.EarningHistory
import com.reward.dne.utils.CustomTextViewBold
import com.reward.dne.utils.CustomTextViewNormal
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AdapterEarningHistory (val context: Context,val list: ArrayList<EarningHistory>,var amount:String) : RecyclerView.Adapter<AdapterEarningHistory.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): AdapterEarningHistory.ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_earninghistory, parent, false)
        return AdapterEarningHistory.ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return  list.size
    }

    override fun onBindViewHolder(holder: AdapterEarningHistory.ViewHolder, position: Int) {

        val offerList:EarningHistory = list[position]

        holder.textAppName.text = offerList.name
        holder.textDescription.text = "Installed this App"
        holder.textAmount.text = "₹"+offerList.amount
        val url = context.getString(R.string.image_url)+offerList.app_icon
        if (offerList.type.equals("Refer a Friend")){
            Glide.with(context).load(R.mipmap.applogo)
                    .apply( RequestOptions().placeholder(R.drawable.android_default).error(R.drawable.android_default))
                    .into(holder.imgApplogo)
        }
        else {
            Glide.with(context).load(url)
                    .apply( RequestOptions().placeholder(R.drawable.android_default).error(R.drawable.android_default))
                    .into(holder.imgApplogo)
        }


        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = dateFormat.parse(offerList.created_at)
        val dateFormat1 = SimpleDateFormat("dd-MM-yyy")
        try {
            val date = Date()
            val dateTime = dateFormat1.format(date)
            println("Current Date Time : $dateTime")
            holder.textDate.text = dateTime
        } catch (e: ParseException) {
            e.printStackTrace()
        }




       /* holder.cardDetail.setOnClickListener {
            val intent = Intent(context,AppDetailActivity::class.java)
            context.startActivity(intent)
        }*/

       /* holder.cardDetail.setOnClickListener {
            val intent = Intent(context, AppDetailActivity::class.java)
            context.startActivity(intent)
        }*/
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val cardDetail = itemView.findViewById(R.id.cardDetail) as CardView
        val textAppName  = itemView.findViewById(R.id.textAppName) as CustomTextViewBold
        val textDescription  = itemView.findViewById(R.id.textDescription) as CustomTextViewNormal
        val imgApplogo  = itemView.findViewById(R.id.imgApplogo) as ImageView
        val textAmount = itemView.findViewById<CustomTextViewBold>(R.id.textAmount)
        val textDate = itemView.findViewById<CustomTextViewNormal>(R.id.textDate)


    }
}