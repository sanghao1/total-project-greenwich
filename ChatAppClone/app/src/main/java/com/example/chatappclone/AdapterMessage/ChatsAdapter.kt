/*
package com.example.chatappclone.AdapterMessage

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatappclone.Model.Chat
import com.example.chatappclone.R
import com.example.chatappclone.ViewFullImageActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ChatsAdapter(
    mContext:Context,
    mChatList: List<Chat>,
    imageUrl:String
):RecyclerView.Adapter<ChatsAdapter.ViewHolder?>()
{

    private val  mContext:Context
    private val  mChatList :List<Chat>
    private val  imageUrl:String
    var firebaseUser:FirebaseUser = FirebaseAuth.getInstance().currentUser!!

    init {

        this.mChatList=mChatList
        this.mContext=mContext
        this.imageUrl = imageUrl
    }



    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder
    {
        return if (position == 1)
        {
            val view : View = LayoutInflater.from(mContext).inflate(com.example.chatappclone.R.layout.message_item_right,parent ,false)
            ViewHolder(view)
        }
        else
        {
            val view : View = LayoutInflater.from(mContext).inflate(com.example.chatappclone.R.layout.message_item_left,parent ,false)
            ViewHolder(view)
        }

    }

    override fun getItemCount(): Int
    {
        return mChatList.size

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val chat : Chat =mChatList[position]

        Picasso.get().load(imageUrl).into(holder.profile_imageleft)

        if (chat.getMessage().equals("Sent you an image") && chat.getUrl().equals(""))
        {
            if (chat.getSender().equals(firebaseUser!!.uid))
            {
                holder.show_textmess_username!!.visibility = View.GONE
                holder.right_image_view!!.visibility = View.VISIBLE
                Picasso.get().load(chat.getUrl()).into(holder.right_image_view)

                holder.right_image_view!!.setOnClickListener {
                    val options = arrayOf<CharSequence>(
                        "View Full Image",
                        "Delete Image",
                        "Cancel"
                    )
                    var builder:androidx.appcompat.app.AlertDialog.Builder = androidx.appcompat.app.AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("what do you want?")
                    builder.setItems(options , DialogInterface.OnClickListener{
                        dialog, which ->
                        if (which==0)
                        {
                            val intent = Intent(mContext,ViewFullImageActivity::class.java)
                            intent.putExtra("url" , chat.getUrl())
                            mContext.startActivity(intent)
                        }

                       else if (which==1)
                        {
                            deleteMessage(position, holder)
                        }
                    })
                    builder.show()
                }
            }

            else if (!chat.getSender().equals(firebaseUser!!.uid))
            {

                holder.show_textmess_username!!.visibility = View.GONE
                holder.left_image_view!!.visibility = View.VISIBLE
                Picasso.get().load(chat.getUrl()).into(holder.left_image_view)

                holder.left_image_view!!.setOnClickListener {
                    val options = arrayOf<CharSequence>(
                        "View Full Image",
                        "Cancel"
                    )
                    var builder:androidx.appcompat.app.AlertDialog.Builder = androidx.appcompat.app.AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("what do you want?")
                    builder.setItems(options , DialogInterface.OnClickListener{
                            dialog, which ->
                        if (which==0)
                        {
                            val intent = Intent(mContext,ViewFullImageActivity::class.java)
                            intent.putExtra("url" , chat.getUrl())
                            mContext.startActivity(intent)
                        }
                        else if (which ==1)
                        {
                            deleteMessage(position ,holder)
                        }
                    })
                    builder.show()
                }

            }

        }
        else
        {
            holder.show_textmess_username!!.text = chat.getMessage()

            if (firebaseUser!!.uid == chat.getSender())
            {
                holder.right_image_view!!.setOnClickListener {
                    val options = arrayOf<CharSequence>(
                        "Delete Message",
                        "Cancel"
                    )
                    var builder:androidx.appcompat.app.AlertDialog.Builder = androidx.appcompat.app.AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("what do you want?")
                    builder.setItems(options , DialogInterface.OnClickListener{
                            dialog, which ->
                        if (which==0)
                        {
                            deleteMessage(position, holder)
                        }
                    })
                    builder.show()
                }
            }

        }

        if (position== mChatList.size-1)
        {
            if (chat.isIsseen())
            {
                holder.text_seen!!.text = "Seen"

                if (chat.getMessage().equals("Sent you an image") && chat.getUrl().equals(""))
                {
                    val lp : RelativeLayout.LayoutParams?= holder.text_seen!!.layoutParams as RelativeLayout.LayoutParams?
                    lp!!.setMargins(0,245 , 10 ,0)
                    holder.text_seen!!.layoutParams = lp
                }
            }

            else
            {
                holder.text_seen!!.text = "Sent"

                if (chat.getMessage().equals("Sent you an image") && chat.getUrl().equals(""))
                {
                    val lp : RelativeLayout.LayoutParams?= holder.text_seen!!.layoutParams as RelativeLayout.LayoutParams?
                    lp!!.setMargins(0,245 , 10 ,0)
                    holder.text_seen!!.layoutParams = lp
                }
            }
        }
        else
        {
            holder.text_seen!!.visibility = View.GONE
        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        var profile_imageleft:CircleImageView? = null
        var show_textmess_username:TextView? = null
        var  left_image_view:ImageView? = null
        var  text_seen:TextView? = null
        var  right_image_view:ImageView? = null

        init {
            profile_imageleft = itemView.findViewById(R.id.profile_imageleft)
            show_textmess_username = itemView.findViewById(R.id.show_textmess_username)
            left_image_view = itemView.findViewById(R.id.left_image_view)
            text_seen = itemView.findViewById(R.id.text_seen)
            right_image_view = itemView.findViewById(R.id.right_image_view)


        }
    }

    override fun getItemViewType(position: Int): Int
    {
        return if (mChatList[position].getSender().equals(firebaseUser!!.uid))
        {
            1
        }
        else
        {
            0
        }
    }
    private fun deleteMessage(position:Int,holder: ChatsAdapter.ViewHolder)
    {
        val ref = FirebaseDatabase.getInstance().reference.child("Chats")
            .child(mChatList.get(position).getMessageId()!!)
            .removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                {
                    Toast.makeText(holder.itemView.context,"Delete." ,Toast.LENGTH_LONG).show()
                }
                else
                {
                    Toast.makeText(holder.itemView.context," No Delete." ,Toast.LENGTH_LONG).show()

                }
            }
    }
}*/
package com.example.chatappclone.AdapterMessage

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatappclone.Model.Chat
import com.example.chatappclone.R
import com.example.chatappclone.ViewFullImageActivity

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ChatsAdapter(
    mContext:Context,
    mChatList: List<Chat>,
    imageUrl:String
):RecyclerView.Adapter<ChatsAdapter.ViewHolder?>()
{

    private val  mContext:Context
    private val  mChatList :List<Chat>
    private val  imageUrl:String
    var firebaseUser:FirebaseUser = FirebaseAuth.getInstance().currentUser!!

    init {

        this.mChatList=mChatList
        this.mContext=mContext
        this.imageUrl = imageUrl
    }



    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder
    {
        return if (position == 1)
        {
            val view : View = LayoutInflater.from(mContext).inflate(com.example.chatappclone.R.layout.message_item_right,parent ,false)
            ViewHolder(view)
        }
        else
        {
            val view : View = LayoutInflater.from(mContext).inflate(com.example.chatappclone.R.layout.message_item_left,parent ,false)
            ViewHolder(view)
        }

    }

    override fun getItemCount(): Int
    {
        return mChatList.size

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val chat : Chat =mChatList[position]
        //get image chat

       /* Picasso.get().load(imageUrl).into(holder.profile_imageleft)*/

        if (chat.getMessage().equals("Sent you an image") && !chat.getUrl().equals(""))
        {
            if (chat.getSender().equals(firebaseUser!!.uid))
            {
                holder.show_textmess_username!!.visibility = View.GONE
                holder.right_image_view!!.visibility = View.VISIBLE
                Picasso.get().load(chat.getUrl()).into(holder.right_image_view)

                holder.right_image_view!!.setOnClickListener {
                    val options = arrayOf<CharSequence>(
                        "View Full Image ",
                        "delete",
                        "Cancel"
                    )

                    val builder:AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("What do you want ?")
                    builder.setItems(options, DialogInterface.OnClickListener{
                            dialog, which ->
                        if (which==0)
                        {
                            val intent  =Intent(mContext , ViewFullImageActivity::class.java)
                            intent.putExtra("url" , chat.getUrl())
                            mContext.startActivity(intent)

                        }
                        else if (which==1)
                        {
                            deletemessage(position , holder)

                        }

                    })
                    builder.show()
                }
            }

            else if (!chat.getSender().equals(firebaseUser!!.uid))
            {

                holder.show_textmess_username!!.visibility = View.GONE
                holder.left_image_view!!.visibility = View.VISIBLE
                Picasso.get().load(chat.getUrl()).into(holder.left_image_view)

                holder.left_image_view!!.setOnClickListener {
                    val options = arrayOf<CharSequence>(
                        "View Full Image ",
                        "Cancel"
                    )

                    val builder:AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("What do you want ?")
                    builder.setItems(options, DialogInterface.OnClickListener{
                            dialog, which ->
                        if (which==0)
                        {
                            val intent  =Intent(mContext , ViewFullImageActivity::class.java)
                            intent.putExtra("url" , chat.getUrl())
                            mContext.startActivity(intent)

                        }
                        else if (which==1)
                        {
                            deletemessage(position , holder)

                        }

                    })
                    builder.show()
                }


            }

        }
        else
        {
            holder.show_textmess_username!!.text = chat.getMessage()


            if (firebaseUser!!.uid == chat.getSender())
            {
                holder.show_textmess_username!!.setOnClickListener {
                    val options = arrayOf<CharSequence>(
                        "Delete ",
                        "Cancel"
                    )

                    val builder:AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("What do you want ?")
                    builder.setItems(options, DialogInterface.OnClickListener{
                            dialog, which ->
                        if (which==0)
                        {
                            deletemessage(position , holder)
                        }

                    })
                    builder.show()
                }
            }

        }

        if (position== mChatList.size-1)
        {
            if (chat.isIsseen())
            {
                holder.text_seen!!.text = "Seen"

                if (chat.getMessage().equals("Sent you an image") && !chat.getUrl().equals(""))
                {
                    val lp : RelativeLayout.LayoutParams?= holder.text_seen!!.layoutParams as RelativeLayout.LayoutParams?
                    lp!!.setMargins(0,245 , 10 ,0)
                    holder.text_seen!!.layoutParams = lp
                }
            }

            else
            {
                holder.text_seen!!.text = "Sent"

                if (chat.getMessage().equals("Sent you an image") && !chat.getUrl().equals(""))
                {
                    val lp : RelativeLayout.LayoutParams?= holder.text_seen!!.layoutParams as RelativeLayout.LayoutParams?
                    lp!!.setMargins(0,245 , 10 ,0)
                    holder.text_seen!!.layoutParams = lp
                }
            }
        }
        else
        {
            holder.text_seen!!.visibility = View.GONE
        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        /*var profile_imageleft:CircleImageView? = null*/
        var show_textmess_username:TextView? = null
        var  left_image_view:ImageView? = null
        var  text_seen:TextView? = null
        var  right_image_view:ImageView? = null

        init {
            /*profile_imageleft = itemView.findViewById(R.id.profile_imageleft)*/
            show_textmess_username = itemView.findViewById(R.id.show_textmess_username)
            left_image_view = itemView.findViewById(R.id.left_image_view)
            text_seen = itemView.findViewById(R.id.text_seen)
            right_image_view = itemView.findViewById(R.id.right_image_view)


        }
    }

    override fun getItemViewType(position: Int): Int
    {
        return if (mChatList[position].getSender().equals( firebaseUser!!.uid))
        {
            1
        }
        else
        {
            0
        }
    }

    private fun deletemessage(position: Int,holder: ViewHolder)
    {
        val ref =FirebaseDatabase.getInstance().reference.child("Chats")
            .child(mChatList.get(position).getMessageId()!!)
            .removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                {
                    Toast.makeText(holder.itemView.context , "Delete." , Toast.LENGTH_LONG).show()
                }
                else
                {
                    Toast.makeText(holder.itemView.context , " Not Delete." , Toast.LENGTH_LONG).show()

                }

            }
    }
}