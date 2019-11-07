package com.reward.dne.adapter

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.reward.dne.R
import com.reward.dne.model.RedeemHistoryList
import com.reward.dne.utils.CustomTextViewBold
import com.reward.dne.utils.CustomTextViewNormal
import com.reward.dne.utils.CustomTextViewSemiBold
import java.text.FieldPosition

class AdapterRedeemHistory(val context: Context, var list:List<RedeemHistoryList>) : RecyclerView.Adapter<AdapterRedeemHistory.ViewHolder>()  {


    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): AdapterRedeemHistory.ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_redeem_history, parent, false)
        return AdapterRedeemHistory.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: AdapterRedeemHistory.ViewHolder, position: Int) {

        var item:RedeemHistoryList = list[position]

        holder.textBrandName.text = "Recharge of ${item.rdm_operator}"
        holder.textMbl.text = "Mobile ${item.rdm_phone}"
        holder.textOrderId.text = "Order ${item.rdm_txnid}"
        holder.textDate.text = item.created_at
        holder.textAmount.text = "₹ ${item.rdm_txnamount}"
        holder.textStatus.text = item.rdm_status_message

        Glide.with(context).load(item.images).into(holder.imgBrandLogo)

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val textMbl = itemView.findViewById(R.id.textMbl) as CustomTextViewNormal
        val textOrderId  = itemView.findViewById(R.id.textOrderId) as CustomTextViewSemiBold
        var textDate = itemView.findViewById<CustomTextViewNormal>(R.id.textDate)
        val textBrandName = itemView.findViewById<CustomTextViewSemiBold>(R.id.textBrandName)
        val imgBrandLogo = itemView.findViewById<ImageView>(R.id.imgBrandLogo)
        val textAmount = itemView.findViewById<CustomTextViewBold>(R.id.textAmount)
        val textStatus = itemView.findViewById(R.id.textStatus) as CustomTextViewNormal

    }
}