package com.example.chatappclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.squareup.picasso.Picasso

class ViewFullImageActivity : AppCompatActivity() {


    private var imageviewfull :ImageView? = null

    private var imageUrl:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_full_image)


        imageUrl = intent.getStringExtra("url")
        imageviewfull=findViewById(R.id.imageviewfull)
        Picasso.get().load(imageUrl).into(imageviewfull)
    }
}