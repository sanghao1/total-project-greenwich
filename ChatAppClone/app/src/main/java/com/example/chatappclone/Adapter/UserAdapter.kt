package com.example.chatappclone.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.chatappclone.Fragments.ProfileFragment
import com.example.chatappclone.MainActivity
import com.example.chatappclone.Model.User
import com.example.chatappclone.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.HashMap

class UserAdapter (private  var mContext : Context,
                   private var mUser:List<User>,
                   private  var isFragment: Boolean = false) : RecyclerView.Adapter<UserAdapter.Viewholder>()
{
        private var firebaseUser: FirebaseUser?= FirebaseAuth.getInstance().currentUser

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.Viewholder {
            val view = LayoutInflater.from(mContext).inflate(R.layout.user_item_layout,parent ,false)
            return UserAdapter.Viewholder(view)



        }

        override fun getItemCount(): Int {
            return mUser.size

        }

        override fun onBindViewHolder(holder: UserAdapter.Viewholder, position: Int) {
            val user =mUser[position]
            holder.userNameTextView.text = user.getUsername()
            holder.userFullNameTextView.text = user.getFullname()
            Picasso.get().load(user.getImage()).placeholder(R.drawable.profile1).into(holder.userProfileImage)
            checkFollowingStatus(user.getUID(),holder.followButton)

            holder.itemView.setOnClickListener(View.OnClickListener {
               if (isFragment)
               {
                   val pref = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                   pref.putString("profileId",user.getUID())
                   pref.apply()
                   (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                       .replace(R.id.fragment_container,ProfileFragment()).commit()
               }
                else {
                    val intent =Intent(mContext , MainActivity::class.java)
                   intent.putExtra("publisherId" , user.getUID())
                   mContext.startActivity(intent)
                }
            })

            holder.followButton.setOnClickListener {
                if (holder.followButton.text.toString()=="Follow") {
                    firebaseUser?.uid.let { itl ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(itl.toString())
                            .child("Following").child(user.getUID())
                            .setValue(true).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    firebaseUser?.uid.let { itl ->
                                        FirebaseDatabase.getInstance().reference
                                            .child("Follow").child(user.getUID())
                                            .child("Followers").child(itl.toString())
                                            .setValue(true).addOnCompleteListener { task ->
                                                if (task.isSuccessful)
                                                {

                                                }
                                            }
                                    }
                                }
                            }
                    }
                    addNotification(user.getUID())
                }
                else
                {
                    firebaseUser?.uid.let { itl ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(itl.toString())
                            .child("Following").child(user.getUID())
                            .removeValue().addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    firebaseUser?.uid.let { itl ->
                                        FirebaseDatabase.getInstance().reference
                                            .child("Follow").child(user.getUID())
                                            .child("Followers").child(itl.toString())
                                            .removeValue().addOnCompleteListener { task ->
                                                if (task.isSuccessful)
                                                {

                                                }
                                            }
                                    }
                                }
                            }
                    }
                }
            }

        }
        class Viewholder (@NonNull itemView: View) : RecyclerView.ViewHolder(itemView)
        {
            var userNameTextView: TextView =itemView.findViewById(R.id.user_name_search)
            var userFullNameTextView: TextView =itemView.findViewById(R.id.user_fullname_search)
            var userProfileImage: CircleImageView =itemView.findViewById(R.id.user_prifile_image_search)
            var followButton: Button =itemView.findViewById(R.id.Follow_search)

        }
        private fun checkFollowingStatus(uid: String, followButton: Button)
        {
            val followingRef= firebaseUser?.uid.let { itl ->
                FirebaseDatabase.getInstance().reference
                    .child("Follow").child(itl.toString())
                    .child("Following")
            }
            followingRef.addValueEventListener(object : ValueEventListener
            {
                override fun onDataChange(dataSnapshot: DataSnapshot)
                {
                    if (dataSnapshot.child(uid).exists())
                    {
                        followButton.text="Following"
                    }
                    else
                    {
                        followButton.text="Follow"
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })

        }

        private fun addNotification(userId:String)
        {
            val notiRef = FirebaseDatabase.getInstance().reference
                .child("Notifications")
                .child(userId)

            val notiMap = HashMap<String,Any>()
            notiMap["userid"] = firebaseUser!!.uid
            notiMap["text"] = "...start following you"
            notiMap["postid"] = ""
            notiMap["ispost"] = false
            notiRef.push().setValue(notiMap)
    }
}
