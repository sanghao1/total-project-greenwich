package com.example.chatappclone.AdapterMessage

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.chatappclone.Fragments.ProfileFragment
import com.example.chatappclone.MainActivity
import com.example.chatappclone.Model.User
import com.example.chatappclone.R
import com.example.chatappclone.SendMessageChatAppActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_profile.view.*

class UserAdapterMess (private  var mContext : Context,
                       private var mUser:List<User>,
                       private  var isMessCheck: Boolean = false) : RecyclerView.Adapter<UserAdapterMess.Viewholder?>()
{
    private  var isFragment: Boolean = false


    class Viewholder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var usernameTxT:TextView
        var profileImageViewMess:CircleImageView
        var onlineImageView:CircleImageView
        var oflineImageView:CircleImageView = itemView.findViewById(R.id.image_offline)
        var LastMess:TextView = itemView.findViewById(R.id.message_last)
        init {
            usernameTxT= itemView.findViewById(R.id.usernmae_mess)
            profileImageViewMess =  itemView.findViewById(R.id.profile_image_mess)
            onlineImageView = itemView.findViewById(R.id.image_online)
            oflineImageView=itemView.findViewById(R.id.image_offline)
            LastMess=itemView.findViewById(R.id.message_last)

        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.message_user_search_layout,parent ,false)
        return UserAdapterMess.Viewholder(view)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {

        val user =mUser[position]
        holder.usernameTxT.text = user.getUsername()
        Picasso.get().load(user.getImage()).placeholder(R.drawable.profile1).into(holder.profileImageViewMess)

        holder.itemView.setOnClickListener {
            val options= arrayOf<CharSequence>(
                "Send Message",
                "Call Video"
            )
            val builder:androidx.appcompat.app.AlertDialog.Builder=androidx.appcompat.app.AlertDialog.Builder(mContext)
            builder.setTitle("What do you want ?...")
            builder.setItems(options,DialogInterface .OnClickListener { dialog, which ->
                if (which==0)
                {
                    val intent = Intent(mContext , SendMessageChatAppActivity::class.java)
                    intent.putExtra("visit_id", user.getUID())
                    mContext.startActivity(intent)
                }
                if (which==1)
                {
                }

            })

            builder.show()
        }
    }
    override fun getItemCount(): Int
    {
        return mUser.size
    }
}