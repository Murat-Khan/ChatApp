package com.murat.chatapp.ui.chats

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.messaging.FirebaseMessaging
import com.murat.chatapp.R
import com.murat.chatapp.adapter.RecentConversationsAdapter
import com.murat.chatapp.databinding.FragmentCahtBinding
import com.murat.chatapp.model.ChatMessage
import com.murat.chatapp.model.User
import com.murat.listeners.ConversionListener
import com.murat.showToast
import com.murat.util.BaseFragment
import com.murat.util.Constants
import com.murat.util.PreferenceManager
class CahtFragment : BaseFragment(),ConversionListener {
    lateinit var binding:FragmentCahtBinding
    lateinit var sharedPreferences: PreferenceManager
    private lateinit var db : FirebaseFirestore
    private var conversation  = arrayListOf<ChatMessage>()
    private lateinit var conversationsAdapter : RecentConversationsAdapter
    lateinit var documentReference : DocumentReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCahtBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = PreferenceManager(requireContext())
        init()
        loadUserDetails()
        getToken()
        setListener()
        listenerConversation()
    }

    private fun init(){
        conversation = ArrayList()
        conversationsAdapter = RecentConversationsAdapter(conversation,this)
        binding.conversationsRecycler.adapter = conversationsAdapter
        db = FirebaseFirestore.getInstance()
    }

    private fun setListener(){
        binding.imageSignOut.setOnClickListener {
            signOut()
        }
        binding.fabNewChat.setOnClickListener {
            findNavController().navigate(R.id.usersFragment)
        }
    }

    private fun listenerConversation(){
        db.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
            .whereEqualTo(Constants.KEY_SENDER_ID,sharedPreferences.getString(Constants.KEY_USER_ID))
            .addSnapshotListener(eventListener)
        db.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
            .whereEqualTo(Constants.KEY_RECEIVER_ID,sharedPreferences.getString(Constants.KEY_USER_ID))
            .addSnapshotListener(eventListener)

    }

    private val eventListener = EventListener<QuerySnapshot>{value, error ->
        if (error != null){
            return@EventListener
        }
        if (value != null){
            for (documentChange : DocumentChange in value.documentChanges){
                if (documentChange.type == DocumentChange.Type.ADDED){
                    val senderId : String? = documentChange.document.getString(Constants.KEY_SENDER_ID)
                    val receiverId : String? = documentChange.document.getString(Constants.KEY_RECEIVER_ID)
                    val chatMessage  = ChatMessage(
                        senderId = senderId,
                    receiverId = receiverId
                    )
                    if (sharedPreferences.getString(Constants.KEY_USER_ID).equals(senderId)){
                        chatMessage.conversionImage = documentChange.document.getString(Constants.KEY_RECEIVER_IMAGE)
                        chatMessage.conversionName = documentChange.document.getString(Constants.KEY_RECEIVER_NAME)
                        chatMessage.conversionId = documentChange.document.getString(Constants.KEY_RECEIVER_ID)
                    }else{
                        chatMessage.conversionImage = documentChange.document.getString(Constants.KEY_SENDER_IMAGE)
                        chatMessage.conversionName = documentChange.document.getString(Constants.KEY_SENDER_NAME)
                        chatMessage.conversionId = documentChange.document.getString(Constants.KEY_SENDER_ID)
                    }
                    chatMessage.message = documentChange.document.getString(Constants.KEY_LAST_MESSAGE)
                    chatMessage.dateObject = documentChange.document.getDate(Constants.KEY_TIMES_TEMP)!!
                    conversation.add(chatMessage)
                }else if(documentChange.type == DocumentChange.Type.MODIFIED){
                    for (i in 0..conversation.size){
                        val senderId : String? = documentChange.document.getString(Constants.KEY_SENDER_ID)
                        val receiverId : String? = documentChange.document.getString(Constants.KEY_RECEIVER_ID)
                        if (conversation[i].senderId.equals(senderId) && conversation[i].receiverId.equals(receiverId)){
                            conversation[i].message = documentChange.document.getString(Constants.KEY_LAST_MESSAGE)
                            conversation[i].dateObject = documentChange.document.getDate(Constants.KEY_TIMES_TEMP)!!
                            break
                        }
                    }
                }
            }
            conversation.sortWith(Comparator { obj1, obj2 -> obj1.dateObject.compareTo(obj2.dateObject) })
            conversationsAdapter.notifyDataSetChanged()
            binding.conversationsRecycler.scrollToPosition(0)
            binding.conversationsRecycler.isVisible = true
            binding.progressBar.isVisible = false

        }

    }

    private fun loadUserDetails(){
        binding.textName.text = sharedPreferences.getString(Constants.KEY_NAME)
        val bytes = Base64.decode(sharedPreferences.getString(Constants.KEY_IMAGE),Base64.DEFAULT)
        val bitmap : Bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
        binding.imageProfile.setImageBitmap(bitmap)
    }
    private fun getToken(){
        FirebaseMessaging.getInstance().token.addOnSuccessListener(this::updateToken)
    }

    private fun updateToken( token:String){
        sharedPreferences.putString(Constants.KEY_FCM_TOKEN,token)
        documentReference  = db.collection(Constants.KEY_COLLECTION_USERS)
            .document(sharedPreferences.getString(Constants.KEY_USER_ID).toString())
        documentReference.update(Constants.KEY_FCM_TOKEN,token)
            .addOnFailureListener { showToast("Unable to update token") }
    }
    private fun signOut(){
        showToast("Sign out...")
        documentReference = db.collection(Constants.KEY_COLLECTION_USERS).document(
            sharedPreferences.getString(Constants.KEY_USER_ID).toString()
        )
        val upDates = hashMapOf<String, Any>()
        upDates[Constants.KEY_FCM_TOKEN] = FieldValue.delete()
        documentReference.update(upDates).addOnSuccessListener {
            sharedPreferences.clear()
            findNavController().navigate(R.id.signInFragment)
        }.addOnFailureListener { showToast("Unable to sign out") }
    }

    override fun onConversionClicked(user: User) {
        findNavController().navigate(R.id.messageFragment, bundleOf(Constants.KEY_USER to user))
    }
}
