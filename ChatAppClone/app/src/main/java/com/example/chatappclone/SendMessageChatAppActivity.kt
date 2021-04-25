package com.example.chatappclone

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatappclone.AdapterMessage.ChatsAdapter
import com.example.chatappclone.Fragments1.APIService
import com.example.chatappclone.Model.Chat
import com.example.chatappclone.Model.User
import com.example.chatappclone.Notifications.*
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_send_message_chat_app.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SendMessageChatAppActivity : AppCompatActivity()
{


    var userIdVisit:String=""
    var firebaseUser:FirebaseUser?=null
    var chatsAdapter:ChatsAdapter?=null
    var mChatList:List<Chat>? = null
    lateinit var recy_view_Chat:RecyclerView
    var reference:DatabaseReference? = null

    var notify = false

    var apiService : APIService? = null



    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_message_chat_app)


        val toolbar:androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_message_chat)
        setSupportActionBar(toolbar)

        supportActionBar!!.title=""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {

            val intent = Intent(this@SendMessageChatAppActivity, MessageActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        apiService = Client.Client.getClient("https://fcm.googleapis.com/")!!.create(APIService::class.java)

        intent = intent
        userIdVisit = intent!!.getStringExtra("visit_id")
        firebaseUser= FirebaseAuth.getInstance().currentUser

        recy_view_Chat=findViewById(R.id.recy_view_Chat)
        recy_view_Chat.setHasFixedSize(true)
        var linearLayoutManager=LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd= true
        recy_view_Chat.layoutManager = linearLayoutManager


         reference  = FirebaseDatabase.getInstance().reference
            .child("Users").child(userIdVisit)
        reference!!.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot)
            {
                val user:User?=snapshot.getValue(User::class.java)
                user_mess_chat.text = user!!.getUsername()
                Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile1).into(profile_mess_chat)
                retrieveMessage(firebaseUser!!.uid , userIdVisit, user.getImage())


            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })

        send_btn.setOnClickListener {
            notify = true
            val message = text_messsss_send.text.toString()
            if (message=="")
            {
                Toast.makeText(this@SendMessageChatAppActivity,"write soamething",Toast.LENGTH_LONG).show()
            }
            else
            {
                SendMessageToUser(firebaseUser!!.uid,userIdVisit,message)
            }

            text_messsss_send.setText("")
        }

        image_file_send.setOnClickListener {
            notify = true
            val intent=Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type="image/*"
            startActivityForResult(Intent.createChooser(intent,"Pick Image"),438)

        }

        seenMessage(userIdVisit)
    }

    private fun SendMessageToUser(senderId: String, receiverId: String?, message: String)
    {
        var reference  = FirebaseDatabase.getInstance().reference
        val messagekey = reference.push().key

        val messageHasMap = HashMap<String, Any?>()
        messageHasMap["sender"] = senderId
        messageHasMap["message"] = message
        messageHasMap["receiver"] = receiverId
        messageHasMap["isseen"] = false
        messageHasMap["url"] = ""
        messageHasMap["messageId"] = messagekey
        reference.child("Chats")
            .child(messagekey!!)
            .setValue(messageHasMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                {
                    val chatsListreference  = FirebaseDatabase.getInstance()
                        .reference.child("ChatList")
                        .child(firebaseUser!!.uid)
                        .child(userIdVisit)

                    chatsListreference.addListenerForSingleValueEvent(object :ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (!snapshot.exists())
                            {
                                chatsListreference.child("id").setValue(userIdVisit)
                            }

                            val chatsListReciessreference  = FirebaseDatabase.getInstance()
                                .reference.child("ChatList")
                                .child(userIdVisit)
                                .child(firebaseUser!!.uid)
                            chatsListReciessreference.child("id").setValue(firebaseUser!!.uid)


                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })

                    /*chatsListreference.child("id").setValue(firebaseUser!!.uid)*/

                }
            }

        val userRefreference  = FirebaseDatabase.getInstance().reference
            .child("Users").child(firebaseUser!!.uid)
        userRefreference.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot)
            {
                val user = snapshot.getValue(User::class.java)
                if (notify)
                {
                    sendNotiFication(receiverId, user!!.getUsername() , message)
                }
                notify = false

            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })


    }

    private fun sendNotiFication(receiverId: String?, username: String, message: String)
    {

        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val query = ref.orderByKey().equalTo(receiverId)
        query.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot)
            {
                for (datasnapshot in snapshot.children)
                {
                    val token: Token? = datasnapshot.getValue(Token::class.java)
                    val data = Data(
                        firebaseUser!!.uid ,
                        R.mipmap.ic_launcher,
                        "$username:$message",
                        "new Message",
                        userIdVisit
                    )

                    val sender =Sender(data!! , token!!.getToken().toString())
                    apiService!!.sendNotification(sender)
                        .enqueue(object :Callback<Myresponse>
                        {
                            override fun onResponse(
                                call: Call<Myresponse>,
                                response: Response<Myresponse>
                            )
                            {
                                if (response.code() == 200)
                                {
                                    if (response.body()!!.success !== 1)
                                    {
                                        Toast.makeText(this@SendMessageChatAppActivity , "" , Toast.LENGTH_LONG).show()
                                    }
                                }

                            }

                            override fun onFailure(call: Call<Myresponse>, t: Throwable)
                            {

                            }

                        })


                }


            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })




    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==438 && resultCode == RESULT_OK && data!=null && data!!.data!=null)
        {
            val processBar = ProgressDialog(this)
            processBar.setMessage("please wait , image is sending...")
            processBar.show()

            val fileUri = data.data
            val storageReference=FirebaseStorage.getInstance().reference.child("Chat Image")
            val ref  = FirebaseDatabase.getInstance().reference
            val messageId = ref.push().key
            val filePath= storageReference.child("$messageId.jpg")

            var uploadTask:StorageTask<*>
            uploadTask = filePath.putFile(fileUri!!)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot , Task<Uri>>{ task ->
                if (!task.isSuccessful)
                {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation filePath.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful)
                {
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    val messageHasMap = HashMap<String, Any?>()
                    messageHasMap["sender"] = firebaseUser!!.uid
                    messageHasMap["message"] = "sent you an image"
                    messageHasMap["receiver"] = userIdVisit
                    messageHasMap["isseen"] = false
                    messageHasMap["url"] = url
                    messageHasMap["messageId"] = messageId

                    ref.child("Chats").child(messageId!!).setValue(messageHasMap)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful)
                            {
                                processBar.dismiss()
                                val reference  = FirebaseDatabase.getInstance().reference
                                    .child("Users").child(firebaseUser!!.uid)
                                reference.addValueEventListener(object :ValueEventListener{
                                    override fun onDataChange(snapshot: DataSnapshot)
                                    {
                                        val user = snapshot.getValue(User::class.java)
                                        if (notify)
                                        {
                                            sendNotiFication(userIdVisit, user!!.getUsername() , "sent you an image")
                                        }
                                        notify = false

                                    }

                                    override fun onCancelled(error: DatabaseError)
                                    {

                                    }
                                })
                            }
                        }
                }
            }
        }
    }

    private fun retrieveMessage(senderId: String, receiverId: String?, receiverImagUrl: String)
    {
        mChatList  = ArrayList()
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot)
            {
                (mChatList as ArrayList<Chat>).clear()

                for (snap in snapshot.children)
                {
                    val chat = snap.getValue(Chat::class.java)
                    if (chat!!.getReceiver().equals(senderId) && chat.getSender().equals(receiverId)
                        || chat.getReceiver().equals(receiverId) && chat.getSender().equals(senderId))
                    {
                        (mChatList as ArrayList<Chat>).add(chat)
                    }

                    chatsAdapter = ChatsAdapter(this@SendMessageChatAppActivity, (mChatList as ArrayList<Chat>),receiverImagUrl!!)
                    recy_view_Chat.adapter = chatsAdapter

                }


            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }

    var seenListner : ValueEventListener? = null
    private fun seenMessage(userId:String)
    {
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")
        seenListner = reference!!.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot)
            {
                for (snap in snapshot.children)
                {
                    val chat = snap.getValue(Chat::class.java)
                    if (chat!!.getReceiver().equals(firebaseUser!!.uid) && chat!!.getSender().equals(userId))
                    {
                        val hasMap = HashMap<String,Any>()
                        hasMap["isseen"] = true
                        snap.ref.updateChildren(hasMap)
                    }
                }

            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })

    }

    override fun onPause() {
        super.onPause()
        reference!!.removeEventListener(seenListner!!)
    }

}