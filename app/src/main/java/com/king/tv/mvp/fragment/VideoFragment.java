package com.king.tv.mvp.fragment;


import android.os.Bundle;
import android.view.View;

import com.king.base.util.LogUtils;
import com.king.tv.R;
import com.king.tv.mvp.base.BaseFragment;
import com.king.tv.mvp.base.BasePresenter;
import com.king.tv.mvp.base.BaseView;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.pili.pldroid.player.widget.PLVideoView;

import butterknife.BindView;

/**
 * 七牛云直播，android播放客户端
 * 需要传一个 url
 * 页面只有一个全屏的 PLVideoTextureView
 */

public class VideoFragment extends BaseFragment<BaseView, BasePresenter<BaseView>> {


    @BindView(R.id.PLVideoTextureView)
    PLVideoTextureView PLVideoTextureView;

    private int mRotation;
    private String url;
    private boolean isFull;

    public static VideoFragment newInstance(String url,boolean isFull) {

        Bundle args = new Bundle();
        VideoFragment fragment = new VideoFragment();
        fragment.url = url;
        fragment.isFull = isFull;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getRootViewId() {
        return R.layout.fragment_video;
    }

    @Override
    public void initUI() {
        PLVideoTextureView.setVideoPath(url);
        if(isFull){
            PLVideoTextureView.setDisplayOrientation(PLVideoView.ASPECT_RATIO_PAVED_PARENT);
        }else{
            PLVideoTextureView.setDisplayOrientation(PLVideoView.ASPECT_RATIO_16_9);
        }
        PLVideoTextureView.setOnPreparedListener(new PLMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(PLMediaPlayer plMediaPlayer) {
                LogUtils.d("onPrepared:" + url);
                start();
            }
        });
        PLVideoTextureView.setOnBufferingUpdateListener(new PLMediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(PLMediaPlayer plMediaPlayer, int i) {
                if(i>0)
                    LogUtils.d("onBufferingUpdate|" + i);
            }
        });
        PLVideoTextureView.setOnCompletionListener(new PLMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(PLMediaPlayer plMediaPlayer) {
                LogUtils.d("onCompletion");
            }
        });
        PLVideoTextureView.setOnInfoListener(new PLMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(PLMediaPlayer plMediaPlayer, int i, int i1) {
                LogUtils.d("onInfo|i:" + i + "--i1:" + i1);
                return false;
            }
        });

        PLVideoTextureView.setOnErrorListener(new PLMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(PLMediaPlayer plMediaPlayer, int i) {
                LogUtils.w("onError:i:" + i);

                return false;
            }
        });

    }

    public PLVideoTextureView getVideoView(){
        return PLVideoTextureView;
    }

    public boolean isPlaying(){
        return PLVideoTextureView.isPlaying();
    }

    private void start(){
        if(PLVideoTextureView !=null)
            PLVideoTextureView.start();
    }
    public void pause(){
        if(PLVideoTextureView !=null)
            PLVideoTextureView.pause();
    }

    public void stopPlayback(){
        if(PLVideoTextureView !=null)
            PLVideoTextureView.stopPlayback();

    }

    public void seekTo(long i){
        PLVideoTextureView.seekTo(i);
    }

    @Override
    public void onResume() {
        super.onResume();
        start();
    }

    @Override
    public void onPause() {
        super.onPause();
        pause();
    }

    public int getDisplayAspectRatio(){
        return PLVideoTextureView.getDisplayAspectRatio();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPlayback();
    }


    public void onClickRotate(View v) {
        mRotation = (PLVideoTextureView.getDisplayAspectRatio() + 90) % 360;
        setDisplayAspectRatio(mRotation);
    }

    /**
     *
     * @param ratio
     *      PLVideoView.ASPECT_RATIO_ORIGIN
     *      PLVideoView.ASPECT_RATIO_FIT_PARENT
     *      PLVideoView.ASPECT_RATIO_PAVED_PARENT
     *      PLVideoView.ASPECT_RATIO_16_9
     *      PLVideoView.ASPECT_RATIO_4_3
     *
     */
    public void setDisplayAspectRatio(int ratio){
        PLVideoTextureView.setDisplayAspectRatio(ratio);
    }

    /**
     *
     * @param orientation
     *      0/90/180/270
     */
    public void setDisplayOrientation(int orientation){
        PLVideoTextureView.setDisplayOrientation(orientation);
    }

    public void play(String url){
        this.url = url;
        LogUtils.d("url:" + url);
        PLVideoTextureView.setVideoPath(url);
        PLVideoTextureView.start();
    }



    @Override
    public void initData() {

    }

    @Override
    public BasePresenter createPresenter() {
        return new BasePresenter(getApp());
    }


}
