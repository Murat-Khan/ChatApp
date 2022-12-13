package com.murat.chatapp.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.murat.chatapp.databinding.UserItemBinding
import com.murat.chatapp.model.User
import com.murat.listeners.UserListener as UserListener

 class UserAdapter(
    private val data: ArrayList<User> = arrayListOf(),
             var userListener : UserListener
) : RecyclerView.Adapter<UserAdapter.UserHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        return  return UserHolder(
            UserItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
       holder.setUserData(data[position], userListener)
    }

    override fun getItemCount(): Int {
        return data.size
    }


    class UserHolder(private val binding: UserItemBinding) : RecyclerView.ViewHolder(binding.root) {



        fun setUserData(user: User, listener: UserListener){
            binding.root.setOnClickListener {
                listener.onUserClicked(user)
            }
            binding.textName.text = user.name
            binding.textEmail.text = user.email
            binding.imageProfile.setImageBitmap(user.image?.let { getUserImage(it) })

    }

        private fun getUserImage(encodedImage : String) : Bitmap {
            val bytes = Base64.decode(encodedImage, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }
}
}

