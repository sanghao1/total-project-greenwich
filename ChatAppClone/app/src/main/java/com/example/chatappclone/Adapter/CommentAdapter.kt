package com.example.chatappclone.Adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.chatappclone.Model.Comment
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

class CommentAdapter(private  val mCoontext: Context,
private val mComment:MutableList<Comment>?
):RecyclerView.Adapter<CommentAdapter.ViewHolder>()
{
    private var firebaseUser:FirebaseUser?=null



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentAdapter.ViewHolder
    {
        val view = LayoutInflater.from(mCoontext).inflate(R.layout.comment_layout,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentAdapter.ViewHolder, position: Int)
    {
        firebaseUser=FirebaseAuth.getInstance().currentUser
        val comment = mComment!![position]
        holder.commentTv.text =comment.getComment()
        getUserInfo(holder.imageProfile , holder.userNameTv,comment.getPublisher())
    }

    private fun getUserInfo(imageProfile: CircleImageView, userNameTv: TextView, publisher: String)
    {
        val UserRef = FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(publisher)
        UserRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot)
            {
                if (snapshot.exists())
                {
                    val user = snapshot.getValue(User::class.java)
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile1).into(imageProfile)
                    userNameTv.text =user!!.getUsername()
                }

            }

            override fun onCancelled(error: DatabaseError)
            {
            }
        })
    }

    override fun getItemCount(): Int
    {
        return mComment!!.size
    }
    inner class ViewHolder(@NonNull itemView :View) :RecyclerView.ViewHolder(itemView)
    {
        var imageProfile: CircleImageView
        var userNameTv :TextView
        var commentTv :TextView
        init {
            imageProfile = itemView.findViewById(R.id.user_profile_image_comment)
            userNameTv = itemView.findViewById(R.id.user_name_comment)
            commentTv = itemView.findViewById(R.id.Comment_comment)
        }
    }

}
