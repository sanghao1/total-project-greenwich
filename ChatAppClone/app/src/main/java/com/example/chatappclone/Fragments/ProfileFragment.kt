package com.example.chatappclone.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatappclone.AccountsettingActivity
import com.example.chatappclone.Adapter.MymangerAdapter
import com.example.chatappclone.MessageActivity
import com.example.chatappclone.Model.Post
import com.example.chatappclone.Model.User
import com.example.chatappclone.R
import com.example.chatappclone.ShowUserActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.util.*
import kotlin.collections.ArrayList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ProfileFragment : Fragment() {
    private lateinit var profileId:String
    private lateinit var firebaseUser: FirebaseUser

    var postList:List<Post>?=null
    var MymangerAdapter:MymangerAdapter?=null

    var MymangerAdapterSaveImage:MymangerAdapter?=null
    var postListSaved:List<Post>?=null
    var mysaveimg:List<String>?=null




    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        firebaseUser= FirebaseAuth.getInstance().currentUser!!
        val pref=context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)
        if (pref != null)
        {
            this.profileId = pref.getString("profileId", "none")!!
        }
        if (profileId==firebaseUser.uid)
        {
            view.Edit_buttom_accuont.text="Edit Profile"
        }
        else if  (profileId!=firebaseUser.uid)
        {
            checkFollowAndFollowingButtonStatus()
        }


            //recycler view  for uploaded image
        var recyclerViewUploadImage:RecyclerView
        recyclerViewUploadImage=view.findViewById(R.id.recycler_view_upload_pio)
        recyclerViewUploadImage.setHasFixedSize(true)
        val linearLayoutManager:LinearLayoutManager = GridLayoutManager(context,3)
        recyclerViewUploadImage.layoutManager = linearLayoutManager

        postList=ArrayList()
        MymangerAdapter = context?.let { MymangerAdapter(it,postList as ArrayList<Post>) }
        recyclerViewUploadImage.adapter = MymangerAdapter


        //recycler view  for save image
        var recyclerViewSaveImage:RecyclerView
        recyclerViewSaveImage=view.findViewById(R.id.recycler_view_save_pio)
        recyclerViewSaveImage.setHasFixedSize(true)
        val linearLayoutManager2:LinearLayoutManager = GridLayoutManager(context,3)
        recyclerViewSaveImage.layoutManager = linearLayoutManager2

        postListSaved=ArrayList()
        MymangerAdapterSaveImage = context?.let { MymangerAdapter(it,postListSaved as ArrayList<Post>) }
        recyclerViewSaveImage.adapter = MymangerAdapterSaveImage


        recyclerViewSaveImage.visibility = View.GONE
        recyclerViewUploadImage.visibility = View.VISIBLE

        var uploadImagbtn: ImageButton
        uploadImagbtn = view.findViewById(R.id.img_gird_view_buttom)
        uploadImagbtn.setOnClickListener {
            recyclerViewSaveImage.visibility = View.GONE
            recyclerViewUploadImage.visibility = View.VISIBLE
        }

        var saveImagbtn: ImageButton
        saveImagbtn = view.findViewById(R.id.img_save_buttom)
        saveImagbtn.setOnClickListener {
            recyclerViewSaveImage.visibility = View.VISIBLE
            recyclerViewUploadImage.visibility = View.GONE
        }

        view.total_followers.setOnClickListener {
            val intent = Intent(context, ShowUserActivity::class.java)
            intent.putExtra("id",profileId)
            intent.putExtra("title","followers" )
            startActivity(intent)
        }



        view.options_view.setOnClickListener {
            val intent = Intent(context, MessageActivity::class.java)
            startActivity(intent)
        }

        view.total_following.setOnClickListener {
            val intent = Intent(context, ShowUserActivity::class.java)
            intent.putExtra("id",profileId)
            intent.putExtra("title","following" )
            startActivity(intent)

        }

        view.Edit_buttom_accuont.setOnClickListener {
            val getButtonText=view.Edit_buttom_accuont.text.toString()
            when{
                getButtonText=="Edit Profile"->startActivity(Intent(context, AccountsettingActivity::class.java))
                getButtonText=="Follow"->{
                    firebaseUser?.uid.let { itl ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(itl.toString())
                            .child("Following").child(profileId)
                            .setValue(true)
                    }
                    firebaseUser?.uid.let { itl ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(itl.toString())
                            .setValue(true)
                    }

                    addNotification()

                }

                getButtonText=="Following"->{
                    firebaseUser?.uid.let { itl ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(itl.toString())
                            .child("Following").child(profileId)
                            .removeValue()
                    }
                    firebaseUser?.uid.let { itl ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(itl.toString())
                            .removeValue()
                    }

                }
            }

        }

        getFolowers()
        getFolowings()
        unserInfo()
        myPhotos()
        getTotalNumberpost()
        mySave()
        return view
    }

    private fun checkFollowAndFollowingButtonStatus() {
        val followingRef= firebaseUser?.uid.let { itl ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(itl.toString())
                .child("Following")
        }
        if (followingRef != null)
        {
            followingRef.addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot)
                {
                    if (snapshot.child(profileId).exists())
                    {
                        view?.Edit_buttom_accuont?.text ="Following"
                    }
                    else{
                        view?.Edit_buttom_accuont?.text ="Follow"

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
    }
    private fun getFolowers()
    {
        val followersRef= FirebaseDatabase.getInstance().reference
                .child("Follow").child(profileId)
                .child("Followers")
        followersRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot)
            {
                if (snapshot.exists())
                {
                    view?.total_followers?.text=snapshot.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    private fun getFolowings()
    {
        val followersRef= FirebaseDatabase.getInstance().reference
                .child("Follow").child(profileId)
                .child("Following")


        followersRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot)
            {
                if (snapshot.exists())
                {
                    view?.total_following?.text=snapshot.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    private fun  unserInfo(){
        val userRef=FirebaseDatabase.getInstance().getReference().child("Users").child(profileId)
        userRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){
                    val user =snapshot.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile1).into(view?.pro_profile_fragment)

                    view?.profile_fragment_username?.text=user!!.getUsername()
                    view?.full_name?.text=user!!.getFullname()
                    view?.bio_profile?.text=user!!.getBio()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }



    private fun myPhotos()
    {
        val postsRef = FirebaseDatabase.getInstance()
            .reference.child("Posts")
        postsRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot)
            {
                if (snapshot.exists())
                {
                    ( postList as ArrayList<Post>).clear()
                    for (snap in snapshot.children){
                        val post = snap.getValue(Post::class.java)!!
                        if (post.getPublisher().equals(profileId))
                        {
                            ( postList as ArrayList<Post>).add(post)
                        }
                        Collections.reverse(postList)
                        MymangerAdapter!!.notifyDataSetChanged()


                    }
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }

    override fun onStart() {
        super.onStart()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId",firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId",firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId",firebaseUser.uid)
        pref?.apply()
    }


    private fun getTotalNumberpost()
    {
       val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")
        postsRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot)
            {
                if (snapshot.exists())
                {
                    var postCouter = 0
                    for (sanp in snapshot.children)
                    {
                        val post = sanp.getValue(Post::class.java)!!
                        if (post.getPublisher()==profileId)
                        {
                            postCouter++
                        }
                    }
                    total_posts.text = "" + postCouter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        } )
    }

    private fun mySave()
    {
        mysaveimg = ArrayList()
        val saveRef = FirebaseDatabase.getInstance().reference
            .child("Saves").child(firebaseUser.uid)

        saveRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot)
            {
                if (snapshot.exists())
                {
                    for (snap in snapshot.children)
                    {
                        (mysaveimg as ArrayList<String>).add(snap.key!!)
                    }
                    readSaveImageData()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    private fun readSaveImageData()
    {
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")
        postsRef.addValueEventListener(object :ValueEventListener{


            override fun onDataChange(snapshot: DataSnapshot)
            {
                if (snapshot.exists())
                {
                    (postListSaved as ArrayList<Post>).clear()
                    for (sna in snapshot.children)
                    {
                        val post  = sna.getValue(Post::class.java)
                        for (key in mysaveimg!!)
                        {
                            if (post!!.getPostid()==key)
                            {
                                (postListSaved as ArrayList<Post>).add(post!!)
                            }
                        }
                    }

                    MymangerAdapterSaveImage!!.notifyDataSetChanged()
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun addNotification()
    {
        val notiRef = FirebaseDatabase.getInstance().reference
            .child("Notifications")
            .child(profileId)

        val notiMap = HashMap<String,Any>()
        notiMap["userid"] = firebaseUser!!.uid
        notiMap["text"] = "...start following you"
        notiMap["postid"] = ""
        notiMap["ispost"] = false
        notiRef.push().setValue(notiMap)
    }

}