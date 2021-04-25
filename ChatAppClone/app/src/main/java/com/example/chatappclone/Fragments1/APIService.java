package com.example.chatappclone.Fragments1;

import com.example.chatappclone.Notifications.Myresponse;
import com.example.chatappclone.Notifications.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService
{
    @Headers({
            "Content-Type:application/json",
            "Authorization:Key=AAAAOcYkrjQ:APA91bGZRIkVztIRZ1xiOGXR3JKwktwRQDcbxvfnhjsd4of_rqZthN14uwQPGhDmoalBzHPBog79S2bO4XZ4WKjQ_LbDgbAuUgbaQQDHdww1FLJyLC9aaU4Rec1JOk9GsOG3b3Yubwa5"
    })
    @POST("fcm/send")
    Call<Myresponse> sendNotification(@Body Sender body);
}
