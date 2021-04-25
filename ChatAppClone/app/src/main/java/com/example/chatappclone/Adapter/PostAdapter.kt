package com.example.chatappclone.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.chatappclone.CommentsActivity
import com.example.chatappclone.Fragments.ProfileFragment
import com.example.chatappclone.Fragments.postDetalFragment
import com.example.chatappclone.MainActivity
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
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_accountsetting.*
import kotlinx.android.synthetic.main.activity_comments.*
import java.util.HashMap

class PostAdapter(private val mContext:Context ,
                  private val mPost:List<Post>): RecyclerView.Adapter<PostAdapter.ViewHolder>() {
    private var firebaseUser: FirebaseUser? = null

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profileImage: CircleImageView
        var postImage: ImageView
        var likeButtom: ImageView
        var commentButtom: ImageView
        var saveButtom: ImageView
        var userName: TextView
        var likes: TextView
        var publisher: TextView
        var descriptions: TextView
        var comments: TextView

        init {
            profileImage = itemView.findViewById(R.id.user_profile_image_post)
            postImage = itemView.findViewById(R.id.post_image_post)
            likeButtom = itemView.findViewById(R.id.post_image_like_btn)
            commentButtom = itemView.findViewById(R.id.post_image_comment_btn)
            saveButtom = itemView.findViewById(R.id.post_save_comment_btn)
            userName = itemView.findViewById(R.id.user_name_post)
            likes = itemView.findViewById(R.id.likes)
            publisher = itemView.findViewById(R.id.publisher)
            descriptions = itemView.findViewById(R.id.description)
            comments = itemView.findViewById(R.id.comments)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.post_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        val post = mPost[position]
        Picasso.get().load(post.getPostimage()).into(holder.postImage)
        if (post.getDescription().equals("")) {
            holder.descriptions.visibility = View.GONE
        } else {
            holder.descriptions.visibility = View.VISIBLE
            holder.descriptions.setText(post.getDescription())
        }
        publisherInfo(holder.profileImage, holder.userName, holder.publisher, post.getPublisher())
        islikes(post.getPostid(), holder.likeButtom)
        numberLikes(holder.likes, post.getPostid())
        getToatalComment(holder.comments, post.getPostid())
        checkSave(post.getPostid(), holder.saveButtom)

        holder.postImage.setOnClickListener {

            val editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
            editor.putString("postId", post.getPostid())
            editor.apply()
            (mContext as FragmentActivity).getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, postDetalFragment()).commit()
        }


        holder.publisher.setOnClickListener {
            val editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
            editor.putString("profileId", post.getPublisher())
            editor.apply()
            (mContext as FragmentActivity).getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment()).commit()
        }


        holder.profileImage.setOnClickListener {
            val editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
            editor.putString("profileId", post.getPublisher())
            editor.apply()
            (mContext as FragmentActivity).getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment()).commit()
        }

        holder.postImage.setOnClickListener {
            val editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
            editor.putString("postId", post.getPostid())
            editor.apply()
            (mContext as FragmentActivity).getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, postDetalFragment()).commit()
        }




        holder.likeButtom.setOnClickListener {
            if (holder.likeButtom.tag == "Like") {
                FirebaseDatabase.getInstance().reference
                    .child("Likes")
                    .child(post.getPostid())
                    .child(firebaseUser!!.uid)
                    .setValue(true)
                addNotification(post.getPublisher(),post.getPostid())
            } else {
                FirebaseDatabase.getInstance().reference
                    .child("Likes")
                    .child(post.getPostid())
                    .child(firebaseUser!!.uid)
                    .removeValue()
                val intent = Intent(mContext, MainActivity::class.java)
                mContext.startActivity(intent)
            }
        }

            holder.likes.setOnClickListener {
                val intent = Intent(mContext, ShowUserActivity::class.java)
                intent.putExtra("id",post.getPostid())
                intent.putExtra("title","likes" )
              mContext.startActivity(intent)
            }


        holder.commentButtom.setOnClickListener {
            val intentComment = Intent(mContext, CommentsActivity::class.java)
            intentComment.putExtra("postId", post.getPostid())
            intentComment.putExtra("publisherId", post.getPublisher())
            mContext.startActivity(intentComment)
        }
        holder.comments.setOnClickListener {
            val intentComment = Intent(mContext, CommentsActivity::class.java)
            intentComment.putExtra("postId", post.getPostid())
            intentComment.putExtra("publisherId", post.getPublisher())
            mContext.startActivity(intentComment)
        }

        holder.saveButtom.setOnClickListener {
           if (holder.saveButtom.tag == "Save")
           {
               FirebaseDatabase.getInstance().reference
                   .child("Saves")
                   .child(firebaseUser!!.uid)
                   .child(post.getPostid())
                   .setValue(true)
           }
            else
           {
               FirebaseDatabase.getInstance().reference
                   .child("Saves")
                   .child(firebaseUser!!.uid)
                   .child(post.getPostid())
                   .removeValue()
           }
        }

    }


    private fun numberLikes(likes: TextView, postid: String) {
        val LikesRef = FirebaseDatabase.getInstance().reference
            .child("Likes").child(postid)

        LikesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    likes.text = snapshot.childrenCount.toString()!! + " likes"
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


    private fun getToatalComment(comments: TextView, postid: String) {
        val commentsRef = FirebaseDatabase.getInstance().reference
            .child("Comments").child(postid)

        commentsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    comments.text = "View all " + snapshot.childrenCount.toString()!! + " comments"
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }


    private fun islikes(postid: String, likeButtom: ImageView) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val LikesRef = FirebaseDatabase.getInstance().reference
            .child("Likes").child(postid)
        LikesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(firebaseUser!!.uid).exists()) {
                    likeButtom.setImageResource(R.drawable.heart_clicked)
                    likeButtom.tag = "Liked"
                } else {
                    likeButtom.setImageResource(R.drawable.heart_not_clicked)
                    likeButtom.tag = "Like"
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    private fun publisherInfo(
        profileImage: CircleImageView,
        userName: TextView,
        publisher: TextView,
        publisherID: String
    ) {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisherID)
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue<User>(User::class.java)
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile1)
                        .into(profileImage)
                    userName.text = user!!.getUsername()
                    publisher.text = user.getFullname()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


    private fun checkSave(postid: String , imageView: ImageView)
    {
        val saveRef = FirebaseDatabase.getInstance().reference
            .child("Saves")
            .child(firebaseUser!!.uid)


        saveRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot)
            {
                if (snapshot.child(postid).exists())
                {
                    imageView.setImageResource(R.drawable.save_large_icon)
                    imageView.tag ="Saved"
                }
                else
                {
                    imageView.setImageResource(R.drawable.save_unfilled_large_icon)
                    imageView.tag ="Save"

                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })

    }

    private fun addNotification(userId:String , postId:String)
    {
        val notiRef = FirebaseDatabase.getInstance().reference
            .child("Notifications")
            .child(userId)

        val notiMap = HashMap<String,Any>()
        notiMap["userid"] = firebaseUser!!.uid
        notiMap["text"] = "...liked you post"
        notiMap["postid"] = postId
        notiMap["ispost"] = true
        notiRef.push().setValue(notiMap)

    }
}
