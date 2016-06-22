package com.example.codepractice.home;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.codepractice.R;
import com.example.codepractice.rest.RestClient;
import com.example.codepractice.storage.UserPreferences;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observables.ConnectableObservable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

public class UserActivity extends AppCompatActivity {

    private String URL = "https://api.github.com/users/shivam340/repos";
    private static final String TAG = "UserActivity";
    private TextView information;
    private Button usingOkHttp;
    private Button usingRetrofit;
    private Button usingRxAndroid;
    private Subscription subscription = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usingOkHttp = (Button) findViewById(R.id.ok_http);
        usingRetrofit = (Button) findViewById(R.id.retrofit);
        usingRxAndroid = (Button) findViewById(R.id.rx_android);
        information = (TextView) findViewById(R.id.information);

        usingOkHttp.setOnClickListener(new HandleOnClickListener());
        usingRetrofit.setOnClickListener(new HandleOnClickListener());
        usingRxAndroid.setOnClickListener(new HandleOnClickListener());
    }


    /**
     * Synchronous api call.
     */
    private void setUsingOkHttp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final OkHttpClient client = new OkHttpClient();
                    Request.Builder requestBuilder = new Request.Builder();
                    requestBuilder.url(URL);
                    final Request request = requestBuilder.build();
                    Response response = client.newCall(request).execute();
                    Log.d(TAG, "onCreate: " + response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * Asynchronous api call.
     */
    private void setUsingOkHttpAsync() {

        try {

            final OkHttpClient client = new OkHttpClient();
            Request.Builder requestBuilder = new Request.Builder();
            requestBuilder.url(URL);
            final Request request = requestBuilder.build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    information.setText("failed to get response using async ok http");
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    Log.d(TAG, "onResponse1:" + Thread.currentThread().getName());
                    /*
                    Callbacks run on a background thread.
                    If you want to immediately process something in the UI you will need to post to the main thread.
                    - by jake wharton
                    */

                    /**
                     * response.body().string() in MainActivity throws android.os.NetworkOnMainThreadException
                     * (because it's being executed on the main thread). Does this call do any network op ?
                     *
                     *
                     *
                     OkHttp knows nothing about the main thread. It's not an Android library, it's a Java library.
                     Furthermore, by calling you back on the initial data it allows streaming responses and potentially
                     incrementally updating the UI before the entire response has been consumed
                     */

                    /*information.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "onResponse2:" + Thread.currentThread().getName());
                            // response.body().string() will throw an network operation on main thread.

                            try {
                                information.setText(response.body().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });*/


                    // consuming data on background thread
                    Gson gson = new GsonBuilder().create();

                    /*
                        here data is coming as json array, it will throw an
                        com.google.gson.JsonSyntaxException: java.lang.IllegalStateException: Expected BEGIN_OBJECT but was BEGIN_ARRAY
                        exception.
                        UserModel user = gson.fromJson(response.body().charStream(), UserModel.class);
                     */
                    Type collectionType = new TypeToken<List<UserModel>>() {
                    }.getType();
                    List<UserModel> userModels = gson
                            .fromJson(response.body().charStream(), collectionType);
                    //Note that the response.body().string() method on the response body will load the entire data into memory.
                    // To make more efficient use of memory, it is recommended that the response be processed as
                    // a stream by using response.body().charStream() instead.

                    /*information.post(new Runnable() {
                        @Override
                        public void run() {
                        information.setText(responseData);  // already read data on background thread.
                        }
                    });*/

                    gson = new GsonBuilder().create();
                    String userData = gson.toJson(userModels);
                    Log.d(TAG, "response data from toJson" + userData);
                    try {
                        final JSONArray jsonArray = new JSONArray(userData);
                        information.post(new Runnable() {
                            @Override
                            public void run() {
                                information.setText(jsonArray.toString());
                                // already read data on background thread.
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class HandleOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.ok_http:
                    information.setText("Using OkHttp");
                    //setUsingOkHttp();
                    setUsingOkHttpAsync();
                    break;
                case R.id.retrofit:
                    information.setText("Using Retrofit");
                    usingRetrofit();
                    break;

                case R.id.rx_android:
                    information.setText("Using RxAndroid");
                    if (subscription != null && !subscription.isUnsubscribed()) {
                        subscription.unsubscribe();
                        subscription = null;
                    }
                    usingHotObservable();
                    usingRxAndroid();
                    usingMap();
                    usingFlatMap();
                    break;
            }
        }
    }


    private void usingRetrofit() {

        RestClient restClient = new RestClient();
        retrofit2.Call<List<UserModel>> call = restClient.getApiService().getUserDetail("shivam340");
        //Asynchronous execution
        call.enqueue(new retrofit2.Callback<List<UserModel>>() {
            @Override
            public void onResponse(retrofit2.Call<List<UserModel>> call, retrofit2.Response<List<UserModel>> response) {

                Gson gson = new GsonBuilder().create();
                String data = gson.toJson(response);
                information.setText(data);

            }

            @Override
            public void onFailure(retrofit2.Call<List<UserModel>> call, Throwable t) {
                information.setText("failed to get response using retrofit");
            }
        });

        /*
         Synchronous execution
        try {
            retrofit2.Response<List<UserModel>> listResponse = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

    }


    // Observables emit any number of items to be processed
    // The type of the item to be processed needs to be specified as a "generic type"
    // In this case, the item type is `String`
    Observable<String> myObservable = Observable.create(
            new Observable.OnSubscribe<String>() {
                @Override
                public void call(Subscriber<? super String> sub) {
                    // "Emit" any data to the subscriber
                    sub.onNext("a");
                    //if called on error then remaining data will n't be emitted.
                    // sub.onError(new Throwable("demo"));
                    sub.onNext("b");
                    sub.onNext("c");

                    // Trigger the completion of the event
                    sub.onCompleted();
                }
            }
    );


    Observable<String> myObservable2 = Observable.create(
            new Observable.OnSubscribe<String>() {
                @Override
                public void call(Subscriber<? super String> sub) {
                    // "Emit" any data to the subscriber
                    sub.onNext("a2");
                    //if called on error then remaining data will n't be emitted.
                    // sub.onError(new Throwable("demo"));
                    sub.onNext("b2");
                    sub.onNext("c2");

                    // Trigger the completion of the event
                    sub.onCompleted();
                }
            }
    );


    Observer<String> myObserver = new Observer<String>() {
        // Triggered for each emitted value
        @Override
        public void onNext(String s) {
            Log.d(TAG, "onNext: myObserver " + s);
        }

        // Triggered once the observable is complete
        @Override
        public void onCompleted() {
            Log.d(TAG, "onCompleted: myObserver");
        }

        // Triggered if there is any errors during the event
        @Override
        public void onError(Throwable e) {
            Log.d(TAG, "onError: myObserver");
        }
    };


    Observer<String> myObserver2 = new Observer<String>() {
        // Triggered for each emitted value
        @Override
        public void onNext(String s) {
            Log.d(TAG, "onNext: myObserver2 " + s);
        }

        // Triggered once the observable is complete
        @Override
        public void onCompleted() {
            Log.d(TAG, "onCompleted: myObserver2");
        }

        // Triggered if there is any errors during the event
        @Override
        public void onError(Throwable e) {
            Log.d(TAG, "onError: myObserver2");
        }
    };


    private void usingRxAndroid() {


        /*Observable concatObservable = Observable.merge(myObservable, myObservable2);
        concatObservable.doOnNext(new Action1() {
            @Override
            public void call(Object o) {
                Log.d(TAG, "call: in doOnNext");
            }
        });

        subscription = concatObservable.subscribe(myObserver);
        */


        /*Observable concatObservable = myObservable.concatWith(myObservable2);
        concatObservable.subscribe(myObserver);*/



        RestClient restClient = new RestClient();

        /*Only the original thread that created a view hierarchy can touch its views.

        Observable<List<UserModel>> observable = restClient.getApiServiceForRx().getUserDetailUsingRx("shivam340");
        observable.subscribeOn(Schedulers.io());
        observable.observeOn(AndroidSchedulers.mainThread());
        observable.subscribe(new Subscriber<List<UserModel>>() {
        */


        final Subscription subscription =
                restClient.getApiServiceForRx().getUserDetailUsingRx("shivam340")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<List<UserModel>>() {

                            @Override
                            public void onCompleted() {
                                unsubscribe();
                            }

                            @Override
                            public void onError(final Throwable e) {
                                UserActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        information.setText("failed to get response using retrofit");
                                        e.printStackTrace();
                                    }
                                });


                                unsubscribe();
                            }

                            @Override
                            public void onNext(List<UserModel> userModels) {
                                if (userModels != null && userModels.size() > 0) {
                                    Gson gson = new GsonBuilder().create();
                                    String data = gson.toJson(userModels);
                                    Log.d(TAG, "onNext: " + data);
                                    information.setText(data);
                                } else {
                                    information.setText("Empty data");
                                }
                            }
                        });
    }



    private void usingHotObservable(){
        ConnectableObservable<String> connectableObservable = myObservable.publish();
        // observer is subscribing
        connectableObservable.observeOn(AndroidSchedulers.mainThread()).
                observeOn(Schedulers.immediate()).subscribe(myObserver);
        connectableObservable.observeOn(AndroidSchedulers.mainThread()).
                observeOn(Schedulers.immediate()).subscribe(myObserver2);

        // initiate the network request
        connectableObservable.connect();


        Func1 func1 = new Func1() {
            @Override
            public Object call(Object o) {
                return null;
            }
        };
    }


    private void usingMap(){

        // Observables emit any number of items to be processed
        // The type of the item to be processed needs to be specified as a "generic type"
        // In this case, the item type is `String`
         Observable.create(
                new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> sub) {
                        // "Emit" any data to the subscriber
                        sub.onNext("Testing");
                        sub.onNext("shivam");
                        sub.onNext("shivam.gosavi@gmail.com");

                        // Trigger the completion of the event
                        sub.onCompleted();
                    }
                }
        ).map(new Func1<String, Integer>() {
            @Override
            public Integer call(String s) {
                Log.d(TAG, "call() called with: " + "s = [" + s + "]");
                return s.length();
            }
        }).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "onNext() called with: " + "lenght is = [" + integer + "]");
            }
        });

    }

    private void usingFlatMap(){

        // Observables emit any number of items to be processed
        // The type of the item to be processed needs to be specified as a "generic type"
        // In this case, the item type is `String`
        Observable.create(
                new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> sub) {
                        // "Emit" any data to the subscriber
                        sub.onNext("Testing");
                        sub.onNext("shivam");
                        sub.onNext("shivam.gosavi@gmail.com");

                        // Trigger the completion of the event
                        sub.onCompleted();
                    }
                }
        ).flatMap(new Func1<String, Observable<Integer>>() {
            @Override
            public Observable<Integer> call(String s) {

                Log.d(TAG, "call() called with: " + "s = [" + s + "]");
                return Observable.just(s.length());
            }
        }).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "onNext() called with: " + "lenght is = [" + integer + "]");
            }
        });

    }
}