package com.example.codepractice.home;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.codepractice.R;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

public class RxPracticeActivity extends AppCompatActivity {

    private static final String TAG = "RxPracticeActivity";

    private Button mapButton;
    private Button flatMapButton;
    private Button concatButton;
    private Button mergeButton;
    private Button zipButton;
    private Button timerButton;
    private Button repeatButton;
    private Button intervalButton;
    private Button hotObservableButton;
    private Button debounceButton;
    private Button subscriptionButton;
    private Subscription subscription = null;



    private Observer<String> myObserver = new Observer<String>() {
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


    private Observer<String> myObserver2 = new Observer<String>() {
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_practice);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mapButton = (Button) findViewById(R.id.map);
        flatMapButton = (Button) findViewById(R.id.flatmap);
        concatButton = (Button) findViewById(R.id.concat);
        zipButton = (Button) findViewById(R.id.zip);
        mergeButton = (Button) findViewById(R.id.merge);
        timerButton = (Button) findViewById(R.id.timer);
        repeatButton = (Button) findViewById(R.id.repeat);
        intervalButton = (Button) findViewById(R.id.interval);
        subscriptionButton = (Button) findViewById(R.id.subscribe);
        hotObservableButton = (Button) findViewById(R.id.hot_observable);

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usingMap();
            }
        });

        flatMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usingFlatMap();
            }
        });


        hotObservableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usingHotObservable();
            }
        });


        timerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unsubscribe();
                usingTimer();
            }
        });

        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unsubscribe();
                 usingRepeat();
            }
        });


        concatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usingConcat();
            }
        });

        mergeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usingMerge();
            }
        });

        intervalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unsubscribe();
                usingInterval();
            }
        });


        zipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unsubscribe();
                usingZip();
            }
        });

        subscriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (unsubscribe()){
                        Toast.makeText(RxPracticeActivity.this,
                                "Previous subscription stopped successful", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean unsubscribe() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
            subscription = null;
            return true;
        }
        return false;
    }

    private void usingTimer() {

        Observable observable = Observable.timer(5, TimeUnit.SECONDS);
        observable.subscribe(new Observer<Long>(){
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Long aLong) {
                Log.d(TAG, "onNext() timer called with: " + "aLong = [" + aLong + "]");
            }
        });
    }


    private void usingRepeat() {

        subscription = myObservable.repeat(5).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {}

            @Override
            public void onNext(String s) {
                Log.d(TAG, "onNext() repeat called with: " + "s = [" + s + "]");
            }
        });
    }


    private void usingInterval() {

        Observable observable = Observable.interval(5, TimeUnit.SECONDS);
        subscription = observable.subscribe(new Observer<Long>(){
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Long aLong) {
                Log.d(TAG, "onNext() interval called with: " + "aLong = [" + aLong + "]");
            }
        });
    }


    private void usingConcat() {
            Observable concatObservable = Observable.concat(myObservable, myObservable2);
        concatObservable.doOnNext(new Action1() {
            @Override
            public void call(Object o) {
                Log.d(TAG, "call: in doOnNext");
            }
        });

        subscription = concatObservable.subscribe(myObserver);

        // you can also use concatWith method of observable to achieve the same as concat.
        /*Observable concatObservable = myObservable.concatWith(myObservable2);
        concatObservable.subscribe(myObserver);*/

    }


    private void usingMerge() {
        Observable concatObservable = Observable.merge(myObservable, myObservable2);
        concatObservable.doOnNext(new Action1() {
            @Override
            public void call(Object o) {
                Log.d(TAG, "call: in doOnNext");
            }
        });

        subscription = concatObservable.subscribe(myObserver);
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




    private void usingZip() {
        Observable zipObservable = Observable.zip(myObservable, myObservable2, new Func2<String, String, String>() {
            @Override
            public String call(String stringFromMyObservable, String stringFromMyObservable2) {
                return stringFromMyObservable + stringFromMyObservable2;
            }
        });

        zipObservable.subscribe(new Subscriber<String>(){
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, "onNext() zip called with: " + "s = [" + s + "]");
            }
        });

    }



    private void usingHotObservable() {
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


}
