package com.mobapproject.eventsroom.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.mobapproject.eventsroom.databinding.FragmentThirdPartySignInBinding

/**
 * A simple [Fragment] subclass.
 */
class ThirdPartySignInFragment : Fragment() {


    private lateinit var binding: FragmentThirdPartySignInBinding
    private var RC_SIGN_IN: Int = 100
    private lateinit var signInBotton: SignInButton
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val TAG = "SIGNIN ERROR"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentThirdPartySignInBinding.inflate(inflater)
        (activity as AppCompatActivity).supportActionBar?.hide()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        //======================================================


        //======================================================


        mGoogleSignInClient = GoogleSignIn.getClient(context!!, gso)

        signInBotton = binding.signInButton

        signInBotton.setSize(SignInButton.SIZE_STANDARD)
        signInBotton.setOnClickListener {
            //signIn()
        }

        return binding.root
    }
//=====================================

//==============================================

    /*private fun signIn() {

       val signInintent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInintent, RC_SIGN_IN)

    }*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /* if (requestCode == RC_SIGN_IN){
             val task = GoogleSignIn.getSignedInAccountFromIntent(data)
             handleSignInResult(task)
         }*/
//===================================================
        /*if (requestCode == RC_LOG_IN){
            val myResponse = IdpResponse.fromResultIntent(data)
            if (resultCode == RESULT_OK){
                val user = FirebaseAuth.getInstance().currentUser
                Toast.makeText(activity, user?.email, Toast.LENGTH_LONG).show()
            }else{
                Log.i(TAG, myResponse?.error?.errorCode.toString())
            }
        }*/
        //===============================================================
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>?) {
        try {
            val account = completedTask!!.getResult(ApiException::class.java)
            updatUI(account)
        } catch (e: Exception) {
            Log.w(TAG, "Signin Result: " + e)
            updatUI(null)
        }
    }

    private fun updatUI(account: GoogleSignInAccount?) {

        // this.findNavController().navigate(ThirdPartySignInFragmentDirections.actionGoogleSignInFragmentToEventListFragment())
    }

    override fun onStart() {
        super.onStart()
        // val account = GoogleSignIn.getLastSignedInAccount(context)
        // updatUI(account)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mGoogleSignInClient.signOut()
        (activity as AppCompatActivity).supportActionBar?.show()
    }
}
