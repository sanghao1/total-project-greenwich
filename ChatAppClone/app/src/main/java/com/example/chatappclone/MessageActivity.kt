package com.example.chatappclone

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.DialogTitle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.chatappclone.Fragments1.ChatsFragment
import com.example.chatappclone.Fragments1.SearchFragment
import com.example.chatappclone.Fragments1.SettingsFragment
import com.example.chatappclone.Model.Chat
import com.example.chatappclone.Model.ChatList
import com.example.chatappclone.Model.User
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_message.*

class MessageActivity : AppCompatActivity()
{


    var refUsers:DatabaseReference? = null
    var firebaseUser:FirebaseUser? = null


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        setSupportActionBar(toolbarmainmesss)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference
            .child("Users").child(firebaseUser!!.uid)

        val toolbar:Toolbar = findViewById(R.id.toolbarmainmesss)
        setSupportActionBar(toolbar)
        supportActionBar!!.title=""


        val tabLayout:TabLayout =findViewById(R.id.tablayout)
        val viewPage:ViewPager=findViewById(R.id.view_sendmess)
        /*val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.addFragment(ChatsFragment(),"Chats")
        viewPagerAdapter.addFragment(SearchFragment(),"Search")
        viewPagerAdapter.addFragment(SettingsFragment(),"Setting")
        viewPage.adapter = viewPagerAdapter
        tabLayout.setupWithViewPager(viewPage)*/

        val ref = FirebaseDatabase.getInstance().reference.child("Chats")
        ref!!.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot)
            {
                val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
                var countUnreadMessages = 0
                for (datasnapshot in snapshot.children)
                {
                    val chat = datasnapshot.getValue(Chat::class.java)
                    if (chat!!.getReceiver().equals(firebaseUser!!.uid) && chat.isIsseen())
                    {
                        countUnreadMessages += 1
                    }
                }
                if (countUnreadMessages == 0)
                {
                    viewPagerAdapter.addFragment(ChatsFragment(),"Chats")
                }
                else
                {
                    viewPagerAdapter.addFragment(ChatsFragment(),"($countUnreadMessages) Chats")
                }
                viewPagerAdapter.addFragment(SearchFragment(),"Search")
               /* viewPagerAdapter.addFragment(SettingsFragment(),"Setting")*/
                viewPage.adapter = viewPagerAdapter
                tabLayout.setupWithViewPager(viewPage)

            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })

        //display username and
        refUsers!!.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot)
            {
                if (snapshot.exists())
                {
                    val user: User?=snapshot.getValue(User::class.java)
                    user_mess.text = user!!.getUsername()
                    Picasso.get().load(user.getImage()).placeholder(R.drawable.profile1).into(profile_mess)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    internal class ViewPagerAdapter(fragmentManager: FragmentManager):
        FragmentPagerAdapter(fragmentManager)
    {

        private val fragments:ArrayList<Fragment>
        private val titles:ArrayList<String>

        init {
            fragments = ArrayList<Fragment>()
            titles = ArrayList<String>()

        }
        override fun getCount(): Int {
            return  fragments.size

        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        fun addFragment(fragment: Fragment , title: String)
        {
            fragments.add(fragment)
            titles.add(title)
        }

        override fun getPageTitle(i: Int): CharSequence? {
            return titles[i]
        }

    }
}