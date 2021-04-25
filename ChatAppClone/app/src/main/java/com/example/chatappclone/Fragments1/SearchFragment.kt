package com.example.chatappclone.Fragments1

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatappclone.AdapterMessage.UserAdapterMess
import com.example.chatappclone.Model.User
import com.example.chatappclone.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_search2.*
import kotlinx.android.synthetic.main.fragment_search2.view.*


class SearchFragment : Fragment() {


    private  var recyclerView : RecyclerView? =null
    private var usersAdaptermess : UserAdapterMess?    =null
    private  var mUser :List<User>?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view :View = inflater.inflate(R.layout.fragment_search2, container, false)
        recyclerView = view.findViewById(R.id.searchList)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager= LinearLayoutManager(context)


        mUser = ArrayList()
        retrieveUsers()

       view.search_mess.addTextChangedListener(object: TextWatcher
        {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int)
            {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
            {
                searchUser(s.toString().toLowerCase())


            }
            override fun afterTextChanged(s: Editable?)
            {
            }
        })

        return view

    }

    private fun retrieveUsers()
    {
        val firebaseUserID =FirebaseAuth.getInstance().currentUser!!.uid
        val refUsers= FirebaseDatabase.getInstance().getReference().child("Users")
        refUsers.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot)
            {
                (mUser as ArrayList<User>).clear()
                if (search_mess!!.text.toString()=="") {
                    for (snap in snapshot.children) {
                        val user = snap.getValue(User::class.java)
                        if (!(user!!.getUID()).equals(firebaseUserID)) {
                            (mUser as ArrayList<User>).add(user)
                        }


                    }
                    usersAdaptermess = UserAdapterMess(context!!, mUser!!, false)
                    recyclerView!!.adapter = usersAdaptermess

                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }


    private fun searchUser(input: String)
    {
        val firebaseUserID =FirebaseAuth.getInstance().currentUser!!.uid
        val queryUsers= FirebaseDatabase.getInstance().getReference()
            .child("Users")
            .orderByChild("username")
            .startAt(input)
            .endAt(input + "\uf8ff")

        queryUsers.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                (mUser as ArrayList<User>).clear()

                for (snap in dataSnapshot.children)
                {
                    val user = snap.getValue(User::class.java)
                    if (!(user!!.getUID()).equals(firebaseUserID))
                    {
                        (mUser as ArrayList<User>).add(user)
                    }
                }
                usersAdaptermess= UserAdapterMess(context!!,mUser!!, false)
                recyclerView!!.adapter=usersAdaptermess

            }

            override fun onCancelled(p0: DatabaseError)
            {

            }
        })
    }
}