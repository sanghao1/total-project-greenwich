package com.example.chatappclone

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        Signin_Account_link.setOnClickListener {
            startActivity(Intent(this,SignInActivity::class.java))
        }
        Sigup_link.setOnClickListener {
            CreateAccount()
        }
    }

    private fun CreateAccount()
    {
        val fullName= full_name_singup.text.toString()
        val userName= user_name_singup.text.toString()
        val email= email_signup_logo.text.toString()
        val password= pass_signup_logo.text.toString()
        when{
            TextUtils.isEmpty(fullName) -> Toast.makeText(this,"Full Name is Required",Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(userName) -> Toast.makeText(this,"User Name is Required",Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(email) -> Toast.makeText(this,"Email is Required",Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this,"Password is Required",Toast.LENGTH_LONG).show()
            else -> {
                val progressDialog = ProgressDialog (this@SignUpActivity)
                progressDialog.setTitle("SignUp")
                progressDialog.setMessage("please wait , this may take a while ")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()
                val mAuth : FirebaseAuth = FirebaseAuth.getInstance()
                mAuth.createUserWithEmailAndPassword(email , password).
                addOnCompleteListener{ task ->
                    if (task.isSuccessful)
                    {

                        SaveUserInfo( fullName ,userName ,email ,progressDialog)
                    }
                    else
                    {
                        val message = task.exception!!.toString()
                        Toast.makeText(this,"Error:  $message",Toast.LENGTH_LONG).show()
                        mAuth.signOut()
                        progressDialog.dismiss()
                    }
                }
            }
        }
    }


    private fun SaveUserInfo(fullName: String, userName: String, email: String , progressDialog: ProgressDialog)
    {
        val curentUserID= FirebaseAuth.getInstance().currentUser!!.uid
        val UserRef: DatabaseReference=FirebaseDatabase.getInstance().reference.child("Users")

        val userMap = HashMap<String , Any >()
        userMap["uid"] = curentUserID
        userMap["fullname"] = fullName.toLowerCase()
        userMap["username"] = userName.toLowerCase()
        userMap["eamil"]    = email
        userMap["status"]    = "ofline"
        userMap["bio"]      = "hey man.....i love you ..good luck "
        userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/chatapp-9f1af.appspot.com/o/Default%20Image%2Fprofile1.png?alt=media&token=b4eb50f6-86f6-42eb-aef7-d2d38c6fe0a9"
        UserRef.child(curentUserID).setValue(userMap)
            .addOnCompleteListener{task ->
                if (task.isSuccessful)
                {
                    progressDialog.dismiss()
                    Toast.makeText(this,"Account has been create successfuly...",Toast.LENGTH_LONG).show()



                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(curentUserID)
                            .child("Following").child(curentUserID)
                            .setValue(true)


                    val intent = Intent(this@SignUpActivity , MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                else
                {
                    val message = task.exception!!.toString()
                    Toast.makeText(this,"Error:  $message",Toast.LENGTH_LONG).show()
                    FirebaseAuth.getInstance().signOut()
                    progressDialog.dismiss()
                }
            }
    }
}



