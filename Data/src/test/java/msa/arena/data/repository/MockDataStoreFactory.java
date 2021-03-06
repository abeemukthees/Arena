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

package msa.arena.data.repository;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

import msa.data.repository.DataStoreFactory;
import msa.data.repository.datasources.remote.ArenaApi;
import msa.data.repository.datasources.remote.RemoteDataSource;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.BufferedSource;
import okio.Okio;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by Abhimuktheeswarar on 08-09-2017.
 */

class MockDataStoreFactory extends DataStoreFactory {

    private final MockWebServer mockWebServer;
    private final RemoteDataSource mockedRemoteDataSource;

    MockDataStoreFactory(@NonNull Context context) {
        super(context);
        mockWebServer = new MockWebServer();
        ArenaApi arenaApi = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(ArenaApi.class);
        mockedRemoteDataSource = new RemoteDataSource(arenaApi, context);
    }

    @Override
    public RemoteDataSource getRemoteDataSource() {
        return mockedRemoteDataSource;
    }

    MockWebServer getMockWebServer() {
        return mockWebServer;
    }

    void enqueueResponse(String fileName) throws IOException {
        System.out.println("enqueueResponse: " + fileName);
        enqueueResponse(fileName, Collections.emptyMap());
    }

    private void enqueueResponse(String fileName, Map<String, String> headers) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("api-response/" + fileName);
        BufferedSource source = Okio.buffer(Okio.source(inputStream));
        MockResponse mockResponse = new MockResponse();
        for (Map.Entry<String, String> header : headers.entrySet()) {
            mockResponse.addHeader(header.getKey(), header.getValue());
        }
        getMockWebServer().enqueue(mockResponse.setBody(source.readString(StandardCharsets.UTF_8)));
    }
}
