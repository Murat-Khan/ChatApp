package com.murat.chatapp.ui.chats

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.core.view.isVisible
import com.google.android.gms.tasks.OnCompleteListener

import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

import com.murat.chatapp.adapter.ChatAdapter
import com.murat.chatapp.databinding.FragmentMessageBinding

import com.murat.chatapp.model.ChatMessage
import com.murat.chatapp.model.User
import com.murat.network.ApiClient
import com.murat.network.ApiService
import com.murat.showToast
import com.murat.util.BaseFragment

import com.murat.util.Constants
import com.murat.util.PreferenceManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


@Suppress("CAST_NEVER_SUCCEEDS")
class MessageFragment : BaseFragment() {
   private lateinit var binding : FragmentMessageBinding
   private lateinit var receiverUser : User
    private var messages = arrayListOf<ChatMessage>()
    private lateinit var  adapter : ChatAdapter
    private lateinit var db : FirebaseFirestore
    private lateinit var sharedPreferences: PreferenceManager
    private var conversionId : String? = null
    private var isReceiverAvailable : Boolean = false





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMessageBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListener()
        loadReceiverDetails()
        init()
        listenerMessage()
    }
    private fun init (){

        sharedPreferences = PreferenceManager(requireContext())
        adapter = ChatAdapter(messages,
            sharedPreferences.getString(Constants.KEY_USER_ID),
        getBitmap(receiverUser.image!!))
        binding.rvMessage.adapter = adapter
        db = FirebaseFirestore.getInstance()
    }

    private fun sendMessage(){
        val message = hashMapOf<String,Any>()
        sharedPreferences.getString(Constants.KEY_USER_ID)
            ?.let { message.put(Constants.KEY_SENDER_ID, it) }
        message[Constants.KEY_RECEIVER_ID] = receiverUser.id!!
        message[Constants.KEY_MESSAGE] = binding.etMessage.text.toString()
        message[Constants.KEY_TIMES_TEMP] = Date()
        db.collection(Constants.KEY_COLLECTION_CHAT).add(message)
        if(conversionId != null){
            updateConversion(binding.etMessage.text.toString())
        }else{
            val conversion : kotlin.collections.HashMap<String,Any> = HashMap()
            conversion[Constants.KEY_SENDER_ID] = sharedPreferences.getString(Constants.KEY_USER_ID)!!
            conversion[Constants.KEY_SENDER_NAME] = sharedPreferences.getString(Constants.KEY_NAME)!!
            conversion[Constants.KEY_SENDER_IMAGE] = sharedPreferences.getString(Constants.KEY_IMAGE)!!
            conversion[Constants.KEY_RECEIVER_ID] = receiverUser.id!!
            conversion[Constants.KEY_RECEIVER_NAME] = receiverUser.name!!
            conversion[Constants.KEY_RECEIVER_IMAGE]= receiverUser.image!!
            conversion[Constants.KEY_LAST_MESSAGE] = binding.etMessage.text.toString()
            conversion[Constants.KEY_TIMES_TEMP] = Date()
            addConversion(conversion)


        }
        if (!isReceiverAvailable ){
            try {
                val tokens = JSONArray()
                tokens.put(receiverUser.token)

                val data = JSONObject()
                data.put(Constants.KEY_USER_ID,sharedPreferences.getString(Constants.KEY_USER_ID))
                data.put(Constants.KEY_NAME,sharedPreferences.getString(Constants.KEY_NAME))
                data.put(Constants.KEY_FCM_TOKEN,sharedPreferences.getString(Constants.KEY_FCM_TOKEN))
                data.put(Constants.KEY_MESSAGE, binding.etMessage.text.toString())

                val body = JSONObject()
                body.put(Constants.REMOTE_MSG_DATA,data)
                body.put(Constants.REMOTE_MSG_REGISTRATION_IDS,tokens)
                sendNotification(body.toString())



            }catch (e :Exception){
                e.message?.let { showToast(it) }
            }
        }
        binding.etMessage.text = null

    }

    private fun sendNotification(messageBody: String){
       ApiClient.client.create(ApiService::class.java)
           .sendMessage(Constants.getRemoteMsgHeaders(),
           messageBody
        ).enqueue(object : Callback<String>{

               @NonNull  override fun onResponse( call: Call<String>, response: Response<String>) {
                if (response.isSuccessful){
                    try {
                        if (response.body() != null){
                         val responseJson  = response.body()?.let { JSONObject(it) }
                            val result : JSONArray = responseJson!!.getJSONArray("result")
                            if (responseJson.getInt("failure") == 1){
                                val error : JSONObject = result.get(0) as JSONObject
                                showToast(error.getString("error"))
                                    return
                            }
                        }

                    }catch (e : JSONException){
                        e.printStackTrace()
                    }
                    showToast("Notification sent successfully")

                }else{
                    showToast("ошибка"+response.code())
                }
            }

               @NonNull override fun onFailure(call: Call<String>, t: Throwable) {
                t.message?.let { showToast(it) }
            }
        })


    }

    private fun listenAvailabilityOfReceiver()  {
        receiverUser.id?.let {
            db.collection(Constants.KEY_COLLECTION_USERS).document(
                it
            ).addSnapshotListener{ value, error,  ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (value != null) {
                    if (value.getLong(Constants.KEY_AVAILABILITY) != null) {
                        val availability: Int? = Objects.requireNonNull(
                            value.getLong(Constants.KEY_AVAILABILITY)
                        )?.toInt()
                        isReceiverAvailable = availability == 1
                    }
                    receiverUser.token = value?.getString(Constants.KEY_FCM_TOKEN)
                }
                if (isReceiverAvailable) {
                    binding.textAvailability.isVisible = true
                } else {
                    binding.textAvailability.isVisible = false
                }

            }
        }

    }


    private fun listenerMessage(){
        db.collection(Constants.KEY_COLLECTION_CHAT)
            .whereEqualTo(Constants.KEY_SENDER_ID,sharedPreferences.getString(Constants.KEY_USER_ID))
            .whereEqualTo(Constants.KEY_RECEIVER_ID,receiverUser.id)
            .addSnapshotListener(eventListener)
        db.collection(Constants.KEY_COLLECTION_CHAT)
            .whereEqualTo(Constants.KEY_SENDER_ID,receiverUser.id)
            .whereEqualTo(Constants.KEY_RECEIVER_ID,sharedPreferences.getString(Constants.KEY_USER_ID))
            .addSnapshotListener(eventListener)
    }

    private val eventListener = com.google.firebase.firestore.EventListener<QuerySnapshot>{value, error ->

        if (error != null){
            return@EventListener
        }
        if (value != null){
            var count : Int = messages.size
            for ( doc : DocumentChange in value.documentChanges){
                if (doc.type == DocumentChange.Type.ADDED) {

                    val chat = ChatMessage(
                        senderId = doc.document.getString(Constants.KEY_SENDER_ID),
                        receiverId = doc.document.getString(Constants.KEY_RECEIVER_ID),
                        message = doc.document.getString(Constants.KEY_MESSAGE),
                        dataTime = doc.document.getDate(Constants.KEY_TIMES_TEMP)
                            ?.let { getDate(it) },
                        dateObject = doc.document.getDate(Constants.KEY_TIMES_TEMP)!!)
                    messages.add(chat)
                }
            }
            messages.sortWith(Comparator { obj1, obj2 -> obj1.dateObject.compareTo(obj2.dateObject) })
            if (count==0){
                adapter.notifyDataSetChanged()
            }else{
                adapter.notifyItemRangeInserted(messages.size,messages.size)
                binding.rvMessage.scrollToPosition(messages.size -1)
            }
            binding.rvMessage.isVisible = true
        }
        binding.progressBar.isVisible = false
        if (conversionId ==null){
            checkForConversion()
        }

    }

    private fun getBitmap(encodeImage:String):Bitmap{
        val bytes = Base64.decode(encodeImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes,0,bytes.size)
    }

    private fun loadReceiverDetails(){
        receiverUser = arguments?.getSerializable(Constants.KEY_USER) as User
        binding.textName.text = receiverUser.name
    }

    private fun setListener(){
        binding.imageBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.layoutSend.setOnClickListener{
            sendMessage()
        }
    }

    private fun getDate(date : Date): String{
        return SimpleDateFormat("MMMM dd, yyyy = hh:mm a", Locale.getDefault()).format(date)
    }

    private fun addConversion(conversion : HashMap<String,Any>){
        db.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
            .add(conversion)
            .addOnSuccessListener { documentReference -> conversionId = documentReference.id }
    }

    private fun updateConversion(messageC : String){
        val documentReference : DocumentReference = db.collection(Constants.KEY_COLLECTION_CONVERSATIONS).document(conversionId!!)
        documentReference.update(
            Constants.KEY_LAST_MESSAGE,messageC,Constants.KEY_TIMES_TEMP,Date()

        )
    }

    private fun checkForConversion(){
        if (messages.size != 0){
            checkForConversionRemotely(
                sharedPreferences.getString(Constants.KEY_USER_ID), receiverUser.id)
            checkForConversionRemotely(
                receiverUser.id,
                sharedPreferences.getString(Constants.KEY_USER_ID)
            )


        }
    }

    private fun checkForConversionRemotely(senderId : String?, receiverId : String?){
        db.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
            .whereEqualTo(Constants.KEY_SENDER_ID, senderId)
            .whereEqualTo(Constants.KEY_RECEIVER_ID,receiverId)
            .get()
            .addOnCompleteListener(conversionOnCompleteListener())

    }

    private fun conversionOnCompleteListener() = OnCompleteListener<QuerySnapshot> {task ->
        if (task.isSuccessful && task.result != null && task.result.documents.size > 0){
            val documentSnapshot : DocumentSnapshot = task.result.documents[0]
            conversionId =documentSnapshot.id
        }
    }

    override fun onResume() {
        super.onResume()

        listenAvailabilityOfReceiver()
    }



}