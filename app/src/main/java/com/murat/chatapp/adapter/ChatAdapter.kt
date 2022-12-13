package com.murat.chatapp.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.murat.chatapp.R

import com.murat.chatapp.model.ChatMessage
import de.hdodenhof.circleimageview.CircleImageView


class ChatAdapter(
    private var data: ArrayList<ChatMessage> = arrayListOf(),
            private var senderId : String? = null,
    private var receiverProfileImage : Bitmap



): RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    companion object {
        private const val VIEW_TAPE_SEND = 1
        private const val VIEW_TAPE_RECEIVED = 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TAPE_SEND){
            SentMessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_right,parent,false))
        }else ReceivedMessageViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_received_message, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val message = data[position]
      when(holder){
        is SentMessageViewHolder -> holder.onBind(message)
          is ReceivedMessageViewHolder -> holder.onBind(message)

      }
    }

    override fun getItemCount(): Int = data.size


    override fun getItemViewType(position: Int): Int {
        return if (data[position].senderId.equals(senderId)) {
            VIEW_TAPE_SEND
        } else VIEW_TAPE_RECEIVED
    }


    inner class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private var tvMassage: TextView? = null
        private var dataTime : TextView? = null
        fun onBind(message: ChatMessage){
            tvMassage?.text = message.message
            dataTime?.text = message.dataTime
        }


        init {
            tvMassage = itemView.findViewById(R.id.right_message)
            dataTime = itemView.findViewById(R.id.data_time)

        }
    }



    inner class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var tvMassage: TextView? = null
        private var image : CircleImageView? = null
        private var dataTime : TextView? = null
        fun onBind(message: ChatMessage){
            tvMassage?.text = message.message
            image?.setImageBitmap(receiverProfileImage)
            dataTime?.text = message.dataTime



        }


        init {
            tvMassage = itemView.findViewById(R.id.text_message)
            image = itemView.findViewById(R.id.image_profile)
            dataTime = itemView.findViewById(R.id.text_data_time)
        }
    }

    }








