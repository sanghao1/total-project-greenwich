@file:Suppress("DEPRECATION")

package com.example.chatappclone

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_in.quen
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.time.Instant


class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        Create_Account_link.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
        }
        quen.setOnClickListener {
            startActivity(Intent(this,pogotActivity::class.java))
        }

        Sigin_link.setOnClickListener {
            LoginUser();
        }


    }


    private fun LoginUser()
    {
        val email= Emai_logo.text.toString()
        val password= Pass_logo.text.toString()
        when
        {
            TextUtils.isEmpty(email) -> Toast.makeText(this,"Email is Required", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this,"Password is Required", Toast.LENGTH_LONG).show()
            else ->
            {
                val progressDialog = ProgressDialog (this@SignInActivity)
                progressDialog.setTitle("Login")
                progressDialog.setMessage("please wait , this may take a while ")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()
                val mAuth : FirebaseAuth = FirebaseAuth.getInstance()
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {task ->
                    if (task.isSuccessful)
                    {
                        progressDialog.dismiss()
                        val intent = Intent(this@SignInActivity , MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                    else
                    {
                        val message = task.exception!!.toString()
                        Toast.makeText(this,"Error:  $message", Toast.LENGTH_LONG).show()
                        FirebaseAuth.getInstance().signOut()
                        progressDialog.dismiss()
                    }
                }
            }
        }


    }

    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser != null)
        {
            val intent = Intent(this@SignInActivity , MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}