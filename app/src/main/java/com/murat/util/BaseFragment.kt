package com.murat.util

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.murat.chatapp.R



 abstract class BaseFragment : Fragment() {

    private lateinit var documentReference : DocumentReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferenceManager = PreferenceManager(requireContext())
        val database = FirebaseFirestore.getInstance()
        documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
            .document(preferenceManager.getString(Constants.KEY_USER_ID)!!)
    }

    override fun onPause() {
        super.onPause()
        documentReference.update(Constants.KEY_AVAILABILITY,0)
    }

    override fun onResume() {
        super.onResume()
        documentReference.update(Constants.KEY_AVAILABILITY,1)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_base, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


}