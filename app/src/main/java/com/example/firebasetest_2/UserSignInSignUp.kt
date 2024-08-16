package com.example.firebasetest_2

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginScreens {

        private val firebaseAuth = FirebaseAuth.getInstance()

        // Function to handle user sign-up
        fun signUp(email: String, password: String,OnSuccess:()->Unit,emailSent: ()->Unit,  OnSameEmail:() -> Unit) {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    val user = firebaseAuth.currentUser
                    val verified = mutableStateOf(false)
                    if (task.isSuccessful) {
                        if (user != null) {
                            user.sendEmailVerification()
                                .addOnCompleteListener { emailSent ->
                                    if (emailSent.isSuccessful) {
                                        emailSent()
                                        OnSuccess()

                                    } else {
                                        println("Email not sent")
                                    }
                                }
                            println("Email sent")
                        }
                        firebaseAuth.signOut()

                    } else if (task.exception is FirebaseAuthUserCollisionException) {
                        Log.d(tag, "Same email and password")
                        OnSameEmail()
                    } else {
                        Log.d(tag, "sign up Error")
                    }
                }
        }



        // Function to handle user sign-in
        fun signIn(email: String, password: String,navController: NavController,verifyEmail:() -> Unit,OnWrongEmail:() -> Unit) {
            val verified = mutableStateOf(false)
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (firebaseAuth.currentUser != null) {
                        if (firebaseAuth.currentUser!!.isEmailVerified) {
                            verified.value = true
                        }
                        if (task.isSuccessful && verified.value) {
                            Log.d(tag, "sign in success")
                            navController.navigate(Screen.User.route)
                        } else if(!task.isSuccessful){
                            Log.d(tag, "wrong email")
                            OnWrongEmail()
                        }
                        else if(!task.isSuccessful || !verified.value) {
                            println("Verify email")
                            firebaseAuth.currentUser!!.sendEmailVerification()
                                .addOnCompleteListener{
                                    if(it.isSuccessful)
                                    {verifyEmail()
                                    firebaseAuth.signOut()
                                    }
                                    else{println("ERROR Sending email: ${it.exception}")}
                                }
                        }
                        else{
                            println("error signing in the user")
                        }
                    }
                }
        }

    }
