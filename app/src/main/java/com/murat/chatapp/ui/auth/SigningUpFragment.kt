package com.murat.chatapp.ui.auth


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.murat.chatapp.R
import com.murat.chatapp.databinding.FragmentSigningUpBinding

import com.murat.showToast
import com.murat.util.Constants
import com.murat.util.PreferenceManager
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream


class SigningUpFragment : Fragment() {

    lateinit var binding: FragmentSigningUpBinding
    private lateinit var encodeImage : String
    private lateinit var db : FirebaseFirestore
    lateinit var sharedPreferences: PreferenceManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSigningUpBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = PreferenceManager(requireContext())
        db = FirebaseFirestore.getInstance()
        setListener()
    }

    private fun setListener() {
        binding.textSignIn.setOnClickListener{
            findNavController().navigateUp()
        }
        binding.buttonSignUp.setOnClickListener {
            if (isValidSignUpDetails()){
                signUp()
            }
        }
        binding.layoutImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            pickImage.launch(intent)
        }


    }
    private fun isValidSignUpDetails() : Boolean {
        if (binding.inputName.text.toString().isEmpty()){
            showToast("Enter name")
            return false
        }else if (binding.inputEmail.text.toString().isEmpty()){
            showToast("Enter email")
            return false
        }/*else if (Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString()).matches()){
        showToast("Enter valid Image")
            return false
        }*/else if (binding.inputPassword.text.toString().isEmpty()){
            showToast("Enter password")
            return false
        }else if (binding.inputConfirmPassword.text.toString().isEmpty()){
            showToast("Confirm your password")
            return false
        }else if (binding.inputPassword.text.toString() != binding.inputConfirmPassword.text.toString()){
            showToast("password mast be sam")
            return false
    }else return true
    }
    private val pickImage : ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){result ->
        if (result.resultCode == RESULT_OK){
            if (result.data != null){
                val imageUri : Uri? = result.data!!.data
                try {
                    val inputStream : InputStream? = imageUri?.let {
                        requireActivity().contentResolver.openInputStream(
                            it) }
                    val bitmap : Bitmap = BitmapFactory.decodeStream(inputStream)
                    binding.imageProfile.setImageBitmap(bitmap)
                    binding.textAddImage.isVisible = false
                    encodeImage=encodeImage(bitmap)

                }catch (e : FileNotFoundException){
                    e.printStackTrace()
                }
            }
        }

    }



   /* private val pickImage : ActivityResultLauncher<Array<String>> =
        registerForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { result ->
            if (result != null) {
                encodeImage = binding.imageProfile.loadImage(result.toString()).toString()
               sharedPreferences.putString(Constants.KEY_IMAGE,result.toString())


                binding.textAddImage.isVisible = false

            } else {
                Log.d("lol", "onActivityResult: the result is null for some reason")
            }
        }*/
    private fun signUp(){
        loading(true)
        val user = hashMapOf<String, Any>()
        user[Constants.KEY_NAME] = binding.inputName.text.toString()
        user[Constants.KEY_EMAIL] = binding.inputEmail.text.toString()
        user[Constants.KEY_PASSWORD] = binding.inputPassword.text.toString()
        user[Constants.KEY_IMAGE] = encodeImage
        db.collection(Constants.KEY_COLLECTION_USERS)
            .add(user)
            .addOnSuccessListener{
                loading(false)
                sharedPreferences.putBoolean(Constants.KEY_IS_SIGNED_IN,true)
                sharedPreferences.putString(Constants.KEY_USER_ID,it.id)
                sharedPreferences.putString(Constants.KEY_NAME,binding.inputName.text.toString())
                sharedPreferences.putString(Constants.KEY_IMAGE,encodeImage)
                findNavController().navigate(R.id.cahtFragment)





            }.addOnFailureListener {
                loading(false)
                showToast(it.toString())

            }

    }

    private fun encodeImage(bitmap : Bitmap):String{
        val previewWidth = 150
        val previewHeight : Int = bitmap.height * previewWidth / bitmap.width
        val previewBitmap = Bitmap.createScaledBitmap(bitmap,previewWidth,previewHeight,false)
        val byteArrayOutStream = ByteArrayOutputStream()
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutStream)
        val bytes  = byteArrayOutStream.toByteArray()
        return Base64.encodeToString(bytes,Base64.DEFAULT)
    }

    private fun loading( isLoading:Boolean){
         if (isLoading){
             binding.buttonSignUp.isVisible = false
             binding.progressBar.isVisible = true
         }else{
             binding.progressBar.isVisible = false
             binding.buttonSignUp.isVisible = true
         }
    }
    }
