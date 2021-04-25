package com.example.chatappclone.Fragments

import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatappclone.*
import com.example.chatappclone.Adapter.PostAdapter
import com.example.chatappclone.Model.Post
import com.example.chatappclone.Model.Story
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_profile.view.*



 class HomeFragment() : Fragment(),Parcelable {


    private var postAdapter:PostAdapter?=null
    private var postList:MutableList<Post>?=null
    private var followingList:MutableList<String>?=null

    constructor(parcel: Parcel) : this() {

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?


    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        var recyclerView:RecyclerView?=null
        var recyclerViewStory :RecyclerView? = null
        recyclerView= view.findViewById(R.id.recycler_view_home)

        val linearLayoutManager=LinearLayoutManager(context)
        linearLayoutManager.reverseLayout=true
        linearLayoutManager.stackFromEnd=true
        recyclerView.layoutManager=linearLayoutManager



        recyclerViewStory= view.findViewById(R.id.recycler_view_story)
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager2=LinearLayoutManager(context ,LinearLayoutManager.HORIZONTAL , false)
        recyclerViewStory.layoutManager=linearLayoutManager2

        postList = ArrayList()
        postAdapter =   context?.let { PostAdapter(it, postList as ArrayList<Post>) }
        recyclerView.adapter= postAdapter
        checkFollowings()
        return view


    }


    private fun checkFollowings()
    {
        followingList = ArrayList()

        val followingRef= FirebaseDatabase.getInstance().reference
                .child("Follow").child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child("Following")
        followingRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot)
            {
            if (snapshot.exists())
            {
                (followingList!! as ArrayList<String>).clear()
                for (p0 in snapshot.children )
                {
                    p0.key?.let { (followingList!! as ArrayList<String>).add(it)}
                }
                retrievePosts()


            }
            }
            override fun onCancelled(error: DatabaseError)
            {
            }
        })
    }
    private fun retrievePosts()
    {
        val postsRef= FirebaseDatabase.getInstance().reference.child("Posts")
        postsRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                postList?.clear()
                for (p0 in snapshot.children )
                {
                    val post = p0.getValue(Post::class.java)
                    for (id in (followingList as ArrayList<String>))
                    {
                        if (post!!.getPublisher()==id)
                        {
                            postList!!.add(post)
                        }
                        postAdapter!!.notifyDataSetChanged()

                    }
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }




    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HomeFragment> {
        override fun createFromParcel(parcel: Parcel): HomeFragment {
            return HomeFragment(parcel)
        }

        override fun newArray(size: Int): Array<HomeFragment?> {
            return arrayOfNulls(size)
        }
    }


}