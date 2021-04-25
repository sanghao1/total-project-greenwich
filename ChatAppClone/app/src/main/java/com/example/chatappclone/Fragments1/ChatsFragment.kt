package com.example.chatappclone.Fragments1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatappclone.Adapter.UserAdapter
import com.example.chatappclone.AdapterMessage.UserAdapterMess
import com.example.chatappclone.Model.ChatList
import com.example.chatappclone.Model.User
import com.example.chatappclone.Notifications.Token
import com.example.chatappclone.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChatsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatsFragment : Fragment()
{

    private var usersAdaptermess : UserAdapterMess?    =null
    private  var mUsers :List<User>?=null
    private  var usersChatlist :List<ChatList>? = null
    lateinit var recyler_view_chatlist: RecyclerView
    private var  firebaseUser : FirebaseUser?  = null


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =   inflater.inflate(R.layout.fragment_chats, container, false)

        recyler_view_chatlist = view.findViewById(R.id.recyler_view_chatlist)
        recyler_view_chatlist.setHasFixedSize(true)
        recyler_view_chatlist.layoutManager = LinearLayoutManager(context)


        firebaseUser = FirebaseAuth.getInstance().currentUser


        usersChatlist = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseUser!!.uid)
        ref!!.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot)
            {
                (usersChatlist as ArrayList).clear()
                for (datasnapshot in snapshot.children)
                {
                    val chatlist = datasnapshot.getValue(ChatList::class.java)

                    (usersChatlist as ArrayList).add(chatlist!!)
                }
                retrieveChatList()

            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })


updateToken(FirebaseInstanceId.getInstance().token)

        return view


    }

    private fun updateToken(token: String?)
    {
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1 = Token(token!!)
        ref.child(firebaseUser!!.uid).setValue(token1)
    }

    private fun retrieveChatList()
    {
        mUsers = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("Users")
        ref!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot)
            {
                (mUsers as ArrayList).clear()
                for (datasnapshot in snapshot.children)
                {
                    val user = datasnapshot.getValue(User::class.java)

                    for ( eachChatList in usersChatlist!!)
                    {
                       if (user!!.getUID().equals(eachChatList.getId()))
                       {
                           (mUsers as ArrayList).add(user!!)
                       }
                    }
                }

                 usersAdaptermess = UserAdapterMess(context!!,(mUsers as ArrayList<User>),false)

                recyler_view_chatlist.adapter = usersAdaptermess
            }

            override fun onCancelled(error: DatabaseError)
            {

            }

        })

    }
}