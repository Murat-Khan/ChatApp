package com.murat.chatapp.ui.user

import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore

import com.murat.chatapp.R
import com.murat.chatapp.adapter.UserAdapter
import com.murat.chatapp.databinding.FragmentUsersBinding

import com.murat.chatapp.model.User
import com.murat.listeners.UserListener
import com.murat.util.BaseFragment
import com.murat.util.Constants
import com.murat.util.PreferenceManager

class UsersFragment : BaseFragment(), UserListener  {
    private lateinit var binding: FragmentUsersBinding
    private lateinit var sharedPreferences: PreferenceManager
    private lateinit var db : FirebaseFirestore




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUsersBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = PreferenceManager(requireContext())
        db = FirebaseFirestore.getInstance()
        setListener()
        getUsers()
    }
    private fun getUsers(){
        loading(true)
        db.collection(Constants.KEY_COLLECTION_USERS)
            .get()
            .addOnCompleteListener {
                loading(false)
                val currentUserId = sharedPreferences.getString(Constants.KEY_USER_ID)
                if (it.isSuccessful && it.result != null){
                    val user : ArrayList<User> = arrayListOf()

                    for (queryDocumentSnapshot in it.result){
                        if (currentUserId.equals(queryDocumentSnapshot.id)){
                            continue
                        }
                        val users = User()
                        users.name = queryDocumentSnapshot.getString(Constants.KEY_NAME)
                        users.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL)
                        users.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE)
                        users.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN)
                        users.id = queryDocumentSnapshot.id
                        user.add(users)

                    }

                    if (user.size > 0){
                        val adapter = UserAdapter(user,this)
                        binding.usersRecycler.adapter = adapter
                        binding.usersRecycler.isVisible = true

                    }else showErrorMessage()

                }else showErrorMessage()
            }
    }

    private fun setListener(){
        binding.imageBack.setOnClickListener {
            findNavController().navigate(R.id.cahtFragment)
        }
    }

    private fun showErrorMessage(){
        binding.textErrorMessage.text = String.format("%s","No user available")
        binding.textErrorMessage.isVisible = true

    }


    private fun loading( isLoading:Boolean) {
        if (isLoading) binding.progressBar.isVisible = true
        else {
            binding.progressBar.isVisible = false
        }
    }

    override fun onUserClicked(user: User) {
        findNavController().navigate(R.id.messageFragment, bundleOf(Constants.KEY_USER to user))

    }
}