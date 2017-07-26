package com.king.tv.mvp.presenter;

import android.util.Log;

import com.king.base.util.LogUtils;
import com.king.tv.App;
import com.king.tv.bean.Room;
import com.king.tv.bean.RoomLine;
import com.king.tv.mvp.base.BasePresenter;
import com.king.tv.mvp.view.IRoomView;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 直播房间里的 Presenter
 */

public class RoomPresenter extends BasePresenter<IRoomView> {

    public RoomPresenter(App app) {
        super(app);
    }

    /**
     * 用户id进入了房间
     */
    public void enterRoom(String uid) {
        enterRoom(uid, false);
    }

    public void enterRoom(String uid, final boolean isShowing) {
        if (isViewAttached()) getView().showProgress();

        // 调用接口 uid 进入房间
        getAppComponent().getAPIService()
                .enterRoom(uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Room>() {
                    @Override
                    public void onCompleted() {
                        if (isViewAttached())
                            getView().onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (isViewAttached())
                            getView().onError(e);
                    }

                    @Override
                    public void onNext(Room room) {
                        LogUtils.d("Response:" + room);

                        // 进入了房间调用成功，通知UI刷新
                        if (isViewAttached()) getView().enterRoom(room);

                        // 获取服务器返回的url
                        if (room != null) {
                            String url = null;
                            RoomLine roomLine = room.getLive().getWs();
                            RoomLine.FlvBean flv = roomLine.getFlv();
                            Log.e("chris","flv===" + flv);
                            if (flv != null) {
                                url = flv.getValue(isShowing).getSrc();
                            } else {
                                url = roomLine.getHls().getValue(isShowing).getSrc();
                            }

                            // 获取url成功，通知UI去播放视频
                            if (isViewAttached()) getView().playUrl(url);
                        }

                    }
                });


    }


}
