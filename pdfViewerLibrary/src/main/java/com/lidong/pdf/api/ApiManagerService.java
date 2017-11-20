package com.lidong.pdf.api;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface ApiManagerService {

    @GET
    rx.Observable<ResponseBody> downloadPicFromNet(@Url String fileUrl);
  
}  