package com.example.chatappclone.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.chatappclone.Fragments.postDetalFragment
import com.example.chatappclone.Model.Post
import com.example.chatappclone.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.image_item_layout.view.*

class MymangerAdapter(private val mcontext:Context , mPost:List<Post>)
    :RecyclerView.Adapter<MymangerAdapter.ViewHolder?>()
{
private var mPost:List<Post>?= null

    init {
        this.mPost = mPost
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = LayoutInflater.from(mcontext).inflate(R.layout.image_item_layout,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post:Post = mPost!![position]
        Picasso.get().load(post.getPostimage()).into(holder.postImage).toString()
        holder.postImage.setOnClickListener {
            val editor = mcontext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
            editor.putString("postId", post.getPostid())
            editor.apply()
            (mcontext as FragmentActivity).getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container,postDetalFragment()).commit()
        }



    }

    override fun getItemCount(): Int
    {
        return mPost!!.size
    }

    inner class ViewHolder(@NonNull itemView:View):RecyclerView.ViewHolder(itemView)
    {
        var postImage:ImageView
        init {
            postImage=itemView.findViewById(R.id.post_image)
        }
    }



}