package com.example.all_together.Notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAhr_VBRA:APA91bEdvl9e_0jY2GxxmRjT97s_xwnzG1N8085t7Wjh4iXw5Xe8CtZtd3y1Br24Eo_XeDkD6qSUPNpODFFzaHikzJD3Lp6RlZAEt5i27f-xKuuSitE5r9vGnE4_4Im3tLhpC4Lr06rs"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
