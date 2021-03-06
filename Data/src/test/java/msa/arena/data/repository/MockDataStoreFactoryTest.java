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

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;
import msa.data.repository.DataStoreFactory;
import msa.data.repository.datasources.remote.RemoteDataSource;
import msa.domain.holder.carrier.ResourceCarrier;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Abhimuktheeswarar on 31-08-2017.
 */
@Deprecated
@RunWith(JUnit4.class)
public class MockDataStoreFactoryTest {

    @Mock
    Context context;
    private DataStoreFactory dataStoreFactory;

    private RemoteDataSource remoteDataSource;


    @BeforeClass
    public static void setupClass() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(__ -> Schedulers.trampoline());
    }

    @Before
    public void setup() {
        dataStoreFactory = mock(DataStoreFactory.class);
        remoteDataSource = mock(RemoteDataSource.class);
    }

    @Test
    public void simpleCheck() {
        assertThat(dataStoreFactory, notNullValue());
        assertThat(remoteDataSource, notNullValue());
    }

    @Test
    public void checkNetwork() {

        when(dataStoreFactory.isNetworkAvailable()).thenReturn(true);
        assertThat(dataStoreFactory.isNetworkAvailable(), is(true));

        TestObserver<ResourceCarrier<RemoteDataSource>> testObserver = new TestObserver<>();

        when(dataStoreFactory.getRemoteDataSourceObservable()).thenReturn(Observable.just(ResourceCarrier.success(remoteDataSource)));

        dataStoreFactory.getRemoteDataSourceObservable().subscribe(testObserver);

        testObserver.assertNoErrors();
        testObserver.assertComplete();

        ResourceCarrier<RemoteDataSource> resourceCarrier = dataStoreFactory.getRemoteDataSourceObservable().blockingFirst();
    }
}
