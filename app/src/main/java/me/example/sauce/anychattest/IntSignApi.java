package me.example.sauce.anychattest;

import java.io.File;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Version ${versionName}
 * Created by sauce on 16/10/19.
 */

public interface IntSignApi {
    @Multipart
    @POST("/icr/recognize_id_card")
    Call<IdCardBean> postIdCard(@Query("user") String user, @Query("password") String password, @Part MultipartBody.Part file);


    @Multipart
    @POST("/icr/recognize_bank_card")
    Call<BankCardBean> postBankCard(@Query("user") String user, @Query("password") String password, @Part MultipartBody.Part file);

}
