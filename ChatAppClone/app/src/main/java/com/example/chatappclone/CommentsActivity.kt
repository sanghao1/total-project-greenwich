package com.example.chatappclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatappclone.Adapter.CommentAdapter
import com.example.chatappclone.Adapter.UserAdapter
import com.example.chatappclone.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_accountsetting.*
import kotlinx.android.synthetic.main.activity_accountsetting.picture_edit
import kotlinx.android.synthetic.main.activity_comments.*
import org.w3c.dom.Comment
import java.util.HashMap

class CommentsActivity : AppCompatActivity()
{
    private var postId=""
    private var publisherId=""
    private var firebaseUser:FirebaseUser?=null
    private var commentAdapter: CommentAdapter? = null
    private var commentList:MutableList<com.example.chatappclone.Model.Comment>?=null



    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)
        val intent = intent
        postId = intent.getStringExtra("postId")
        publisherId = intent.getStringExtra("publisherId")
        firebaseUser = FirebaseAuth.getInstance().currentUser
        var recyclerView:RecyclerView
        recyclerView = findViewById(R.id.recycler_view_comment)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        recyclerView.layoutManager = linearLayoutManager
        commentList=ArrayList()
        commentAdapter= CommentAdapter(this, commentList)
        recyclerView.adapter = commentAdapter


        userInfo()
        readComments()
        getPostImage()

        post_comment.setOnClickListener(View.OnClickListener {
            if (add_comments!!.text.toString()=="")
            {
                Toast.makeText(this@CommentsActivity , "Please write comment....", Toast.LENGTH_LONG).show()
            }
            else{
                addComment()
            }
        })
    }

    private fun addComment()
    {
        val commentsRef = FirebaseDatabase.getInstance().reference
            .child("Comments")
            .child(postId!!)

            val commentMap =HashMap<String , Any>()
        commentMap["comment"] =add_comments!!.text.toString()
        commentMap["publisher"] =firebaseUser!!.uid
        commentsRef.push().setValue(commentMap)
        addNotification()
        add_comments!!.text.clear()

    }

    private fun userInfo()
    {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        usersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val user = p0.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile1).into(profile_image_comment)

                }
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }
    
    private fun getPostImage()
    {
        val postRef = FirebaseDatabase.getInstance()
            .reference.child("Posts")
            .child(postId!!).child("postimage")

        postRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val image = p0.value.toString()

                    Picasso.get().load(image).placeholder(R.drawable.profile1).into(post_image_comment)

                }
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }



    private fun readComments()
    {
    val commentRef = FirebaseDatabase.getInstance().reference
        .child("Comments")
        .child(postId)
    commentRef.addValueEventListener(object :ValueEventListener{
        override fun onDataChange(p0: DataSnapshot)
        {
            if (p0.exists())
            {
                commentList!!.clear()
                for (snapsort in p0.children)
                {
                    val comment = snapsort.getValue(com.example.chatappclone.Model.Comment::class.java)
                    commentList!!.add(comment!!)
                }
                commentAdapter!!.notifyDataSetChanged()
            }
        }

        override fun onCancelled(error: DatabaseError)
                {

                }
             })
       }


    private fun addNotification()
    {
        val notiRef = FirebaseDatabase.getInstance().reference
            .child("Notifications")
            .child(publisherId!!)

        val notiMap = HashMap<String,Any>()
        notiMap["userid"] = firebaseUser!!.uid
        notiMap["text"] = "...commented :" + add_comments!!.text.toString()
        notiMap["postid"] = postId
        notiMap["ispost"] = true
        notiRef.push().setValue(notiMap)

    }
}