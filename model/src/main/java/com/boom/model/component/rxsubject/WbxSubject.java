package com.boom.model.component.rxsubject;

import com.boom.android.log.Dogger;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

public class WbxSubject<T> {
    public static final String TAG = "subject";
    private T val = null;
    private Subject<T> mSubject;
    private boolean alwaysEmit = false;
    private String mTag = null;
    private long delay = 0;

    public WbxSubject(T t){
        val = t;
        mSubject = BehaviorSubject.createDefault(val).toSerialized();
    }

    public void setAlwaysEmit(boolean emit){
        alwaysEmit = emit;
    }

    /**
     *
     * @param delay
     */
    public void setAlwaysEmitInDelay(long delay) {
        this.delay = delay;
        this.alwaysEmit = true;
//        Observable.range(1,100).observeOn(Schedulers.computation()).subscribe(time->delayChange());
    }

    private Disposable subscription;

    void resume() {
        subscription = Observable.interval(delay, TimeUnit.MILLISECONDS)
                .startWith(0L)
                .subscribeOn(Schedulers.computation())
                .subscribe(tick -> {
                    // send out request and end self.
                    if(hasNewData.compareAndSet(true,false)) {
                        mSubject.onNext(val);
                        Dogger.d(Dogger.RX_SUBJECT, "data changed, send out change", "WbxSubject", "resume");
                    } else {
                        stop();
                        Dogger.d(Dogger.RX_SUBJECT, "data not changed, stop self", "WbxSubject", "resume");
                    }
                });
    }

    synchronized void stop() {
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
        }
    }

    private AtomicBoolean hasNewData = new AtomicBoolean(false);


    private synchronized void delayChange() {
        hasNewData.set(true);
        Dogger.i(Dogger.RX_SUBJECT, "value changed", "WbxSubject", "delayChange");
        if (subscription == null || subscription.isDisposed()) {
            Dogger.d(Dogger.RX_SUBJECT, "subscription disposed or null create one", "WbxSubject", "delayChange");
            resume();
        }
    }

    public void setVal(T t){
        if(t == null){
            return;
        }
        if(alwaysEmit){
            val = t;
            if(delay > 0) {
                delayChange();
            } else {
                traceChange(t);
                mSubject.onNext(t);
            }
        }else{
            if(t != null && t.equals(val)){
                return;
            }else{
                val = t;
                traceChange(t);
                mSubject.onNext(t);
            }
        }
    }

    public T getVal(){
        return val;
    }

    public Observable<T> getObservable(){
        return mSubject.hide();
    }

    public void setTag(String tag){
        this.mTag = tag;
    }

    private void traceChange(T t){
        if(this.mTag!=null && this.mTag.length()>0){
            Dogger.d(Dogger.RX_SUBJECT, this.mTag+"----->"+t.toString(), "WbxSubject", "traceChange");
        }
    }
}
