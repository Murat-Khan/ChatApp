package com.murat.chatapp.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.murat.chatapp.databinding.ItemRecentConversionBinding
import com.murat.chatapp.model.ChatMessage
import com.murat.chatapp.model.User
import com.murat.listeners.ConversionListener
import com.murat.listeners.UserListener

class RecentConversationsAdapter(
    private var data: ArrayList<ChatMessage> = arrayListOf(),
    var conversionListener : ConversionListener
):RecyclerView.Adapter<RecentConversationsAdapter.ConversionViewHolder>() {



    inner class ConversionViewHolder(
       private var binding: ItemRecentConversionBinding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(chatMessage: ChatMessage,listener : ConversionListener){
            binding.root.setOnClickListener {
                val user = User(id = chatMessage.conversionId,
                name = chatMessage.conversionName,
                image = chatMessage.conversionImage)
                listener.onConversionClicked(user)
            }
            binding.textRecentMessage.text = chatMessage.message
            binding.textName.text = chatMessage.conversionName
            binding.imageProfile.setImageBitmap(chatMessage.conversionImage?.let { getConversionImage(it)})


        }

    }
    private fun getConversionImage(encodeImage:String): Bitmap {
        val bytes = Base64.decode(encodeImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes,0,bytes.size)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversionViewHolder {
        return ConversionViewHolder(ItemRecentConversionBinding.inflate(LayoutInflater.from(parent.context),parent,false)
    )
    }

    override fun onBindViewHolder(holder: ConversionViewHolder, position: Int) {
        holder.onBind(data[position],conversionListener)
    }

    override fun getItemCount(): Int = data.size
}