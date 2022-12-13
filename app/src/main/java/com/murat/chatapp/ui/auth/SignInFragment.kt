package com.murat.chatapp.ui.auth

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.murat.chatapp.R
import com.murat.chatapp.databinding.FragmentSignInBinding
import com.murat.showToast
import com.murat.util.Constants
import com.murat.util.PreferenceManager
import java.util.Objects

class SignInFragment : Fragment() {
    private lateinit var binding: FragmentSignInBinding
    lateinit var sharedPreferences: PreferenceManager
    private lateinit var db : FirebaseFirestore



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = PreferenceManager(requireContext())
        if (sharedPreferences.getBoolean(Constants.KEY_IS_SIGNED_IN)){
            findNavController().navigate(R.id.cahtFragment)

        }
        db = FirebaseFirestore.getInstance()
        setListeners()
    }

    private fun setListeners() {
        binding.textCreateNewAccount.setOnClickListener {
            findNavController().navigate(R.id.signingUpFragment)
        }
        binding.buttonSignIn.setOnClickListener {
            if (isValidSignUpDetails()){
                signIn()
            }
        }

    }
    private fun signIn(){
        loading(true)
        db.collection(Constants.KEY_COLLECTION_USERS)
            .whereEqualTo(Constants.KEY_EMAIL,binding.inputEmail.text.toString())
            .whereEqualTo(Constants.KEY_PASSWORD,binding.inputPassword.text.toString())
            .get()
            .addOnCompleteListener {
                if(it.isSuccessful && it.result != null && it.result.documents.size > 0){

                    val documentSnapshot : DocumentSnapshot = it.result.documents[0]
                    sharedPreferences.putBoolean(Constants.KEY_IS_SIGNED_IN,true)
                    sharedPreferences.putString(Constants.KEY_USER_ID,documentSnapshot.id)
                    documentSnapshot.getString(Constants.KEY_NAME)
                        ?.let { it1 -> sharedPreferences.putString(Constants.KEY_NAME, it1) }
                    documentSnapshot.getString(Constants.KEY_IMAGE)
                        ?.let { it1 -> sharedPreferences.putString(Constants.KEY_IMAGE, it1) }
                    findNavController().navigate(R.id.cahtFragment)

                }else{
                    loading(false)
                    showToast("Unable to sign in")
                }
            }
    }
    private fun loading( isLoading:Boolean){
        if (isLoading){
            binding.buttonSignIn.isVisible = false
            binding.progressBar.isVisible = true
        }else{
            binding.progressBar.isVisible = false
            binding.buttonSignIn.isVisible = true
        }
    }

    private fun isValidSignUpDetails() : Boolean{
        return if (binding.inputEmail.text.toString().isEmpty()){
            showToast("Enter email")
            false
        }else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString()).matches()){
            showToast("Enter valid email")
            false
        }else if (binding.inputPassword.text.toString().isEmpty()){
            showToast("Enter password")
            false
        }else true

    }
}