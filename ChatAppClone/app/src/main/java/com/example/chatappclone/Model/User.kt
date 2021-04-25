package com.example.chatappclone.Model

class User
{
    private var username: String=""
    private var fullname: String=""
    private var bio: String=""
    private var image: String=""
    private var uid: String=""
    private var status: String=""

    constructor()
    constructor( status:String ,username: String, fullname: String, bio: String, image: String, uid: String)
    {
        this.username = username
        this.fullname = fullname
        this.bio = bio
        this.image = image
        this.uid = uid
        this.status = status

    }

    fun getUsername():String
    {
        return username
    }
    fun setUsername(username: String)
    {
        this.username = username
    }


    fun getFullname():String
    {
        return fullname
    }
    fun setFullname(fullname: String)
    {
        this.fullname = fullname
    }


    fun getBio():String
    {
        return bio
    }
    fun setBio(bio: String)
    {
        this.bio = bio
    }


    fun getImage():String
    {
        return image
    }
    fun setImage(image: String)
    {
        this.image = image
    }


    fun getUID():String
    {
        return uid
    }
    fun setUID(uid: String)
    {
        this.uid = uid
    }

    fun getStatus():String
    {
        return status
    }
    fun setStatus(status: String)
    {
        this.status = status
    }

}