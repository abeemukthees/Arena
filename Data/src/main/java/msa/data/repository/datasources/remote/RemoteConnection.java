/*
 * Copyright 2017, Abhi Muktheeswarar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package msa.data.repository.datasources.remote;

import com.ihsanbal.logging.Level;
import com.ihsanbal.logging.LoggingInterceptor;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import msa.arena.data.BuildConfig;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.internal.platform.Platform;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by Abhimuktheeswarar on 01-05-2017.
 */

public class RemoteConnection {

    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    //private static final String BASE_URL = BuildConfig.MEDI_BASE_URL;

    private static HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
    private static LoggingInterceptor loggingInterceptor = new LoggingInterceptor.Builder()
            .loggable(BuildConfig.DEBUG)
            .setLevel(Level.BODY)
            .log(Platform.INFO)
            .request("Request")
            .response("Response")
            .addHeader("version", BuildConfig.VERSION_NAME)
            .build();
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder = new Retrofit.Builder().baseUrl(BASE_URL).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).addConverterFactory(JacksonConverterFactory.create());
    private static boolean isNetworkAvailable;

    static {

        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
    }

    public static <S> S createService(Class<S> serviceClass) {
        httpClient.addInterceptor(httpLoggingInterceptor);
        httpClient.readTimeout(30, TimeUnit.SECONDS);
        httpClient.connectTimeout(30, TimeUnit.SECONDS);
        httpClient.writeTimeout(30, TimeUnit.SECONDS);
        httpClient.addInterceptor(chain -> {

            Request original = chain.request();
            HttpUrl originalHttpUrl = original.url();

            HttpUrl url = originalHttpUrl.newBuilder()
                    .addQueryParameter("api_key", BuildConfig.THEMOVIEDB_API_KEY)
                    .build();

            Request.Builder requestBuilder = original.newBuilder()
                    .header("Accept", "application/json")
                    //.header(BuildConfig.MEDI_TOKEN_KEY, BuildConfig.MEDI_TOKEN)
                    .method(original.method(), original.body())
                    .url(url);

            Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = builder.client(client).build();
        return retrofit.create(serviceClass);
    }

    public static <S> S createService(Class<S> serviceClass, Observable<Boolean> observable) {

        httpClient.addInterceptor(httpLoggingInterceptor);
        httpClient.readTimeout(30, TimeUnit.SECONDS);
        httpClient.connectTimeout(30, TimeUnit.SECONDS);
        httpClient.writeTimeout(30, TimeUnit.SECONDS);
        httpClient.addInterceptor(chain -> {

            Request original = chain.request();
            HttpUrl originalHttpUrl = original.url();

            HttpUrl url = originalHttpUrl.newBuilder()
                    .addQueryParameter("api_key", BuildConfig.THEMOVIEDB_API_KEY)
                    .build();

            Request.Builder requestBuilder = original.newBuilder()
                    .header("Accept", "application/json")
                    //.header(BuildConfig.MEDI_TOKEN_KEY, BuildConfig.MEDI_TOKEN)
                    .method(original.method(), original.body())
                    .url(url);

            Request request = requestBuilder.build();
            return chain.proceed(request);
        });


        OkHttpClient client = httpClient.build();
        Retrofit retrofit = builder.client(client).build();
        return retrofit.create(serviceClass);
    }
}
