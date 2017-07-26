package com.king.tv.mvp.fragment;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.king.base.adapter.ViewPagerFragmentAdapter;
import com.king.base.util.LogUtils;
import com.king.tv.R;
import com.king.tv.bean.Room;
import com.king.tv.mvp.base.BaseFragment;
import com.king.tv.mvp.presenter.RoomPresenter;
import com.king.tv.mvp.view.IRoomView;
import com.king.tv.util.DensityUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 半屏播放页面 用到 VideoFragment（七牛直播云 PLVideoTextureView）
 * 需要 uid
 */

public class RoomFragment extends BaseFragment<IRoomView, RoomPresenter> implements IRoomView {

    private VideoFragment videoFragment;

    @BindView(R.id.frameVideo)
    FrameLayout frameVideo;
    @BindView(R.id.ivBack)
    ImageView ivBack;
    @BindView(R.id.ivShare)
    ImageView ivShare;
    @BindView(R.id.tvRoomTitle)
    TextView tvRoomTitle;
    @BindView(R.id.ivFullScreen)
    ImageView ivFullScreen;
    @BindView(R.id.rlRoomInfo)
    RelativeLayout rlRoomInfo;
    @BindView(R.id.llRoomChat)
    LinearLayout llRoomChat;

    @BindView(R.id.videoContent)
    RelativeLayout videoContent;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.tvFollow)
    TextView tvFollow;
    @BindView(R.id.viewPager)
    ViewPager viewPager;


    private ViewPagerFragmentAdapter viewPagerFragmentAdapter;
    private List<CharSequence> listTitle;
    private List<Fragment> listData;
    private Room room;
    private String uid;
    private AnchorInfoFragment anchorInfoFragment;

    public static RoomFragment newInstance(String uid) {
        Bundle args = new Bundle();
        RoomFragment fragment = new RoomFragment();
        fragment.uid = uid;
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public int getRootViewId() {
        return R.layout.fragment_room;
    }

    @Override
    public void initUI() {

        updateVideoLayoutParams();

        listTitle = new ArrayList<>();

        listTitle.add(getText(R.string.room_chat));
        listTitle.add(getText(R.string.room_ranking));
        listTitle.add(getText(R.string.room_anchor));
        listData = new ArrayList<>();
        listData.add(ChatFragment.newInstance());
        listData.add(RankFragment.newInstance());
        anchorInfoFragment = AnchorInfoFragment.newInstance(room);
        listData.add(anchorInfoFragment);
        viewPagerFragmentAdapter = new ViewPagerFragmentAdapter(getFragmentManager(), listData, listTitle);
        viewPager.setAdapter(viewPagerFragmentAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    /**
     * 设置视频的比例
     */
    public void updateVideoLayoutParams(){
        ViewGroup.LayoutParams lp = videoContent.getLayoutParams();
        if(isLandscape()){
            lp.height = DensityUtil.getDisplayMetrics(context).heightPixels;
        }else{
            lp.height = (int)(DensityUtil.getDisplayMetrics(context).widthPixels / 16.0f * 9.0f);
        }

        videoContent.setLayoutParams(lp);
    }

    @Override
    public void initData() {
        getPresenter().enterRoom(uid);
    }

    @Override
    public RoomPresenter createPresenter() {
        return new RoomPresenter(getApp());
    }

    /**
     * uid 进入了方剂，刷新页面而已，不是重点
     */
    @Override
    public void enterRoom(Room room) {
        this.room = room;
        Log.e("chris","enterRoom");
        anchorInfoFragment.onUpdateAnchor(room);// 另一个Fragment更新信息，不重要
        viewPagerFragmentAdapter.notifyDataSetChanged();// 更新视频下面的信息，不重要
    }

    /**
     * 关键看这里
     *
     * 获取了服务器返回的 url
     *
     * 开始播放视频
     */
    @Override
    public void playUrl(String url) {
        Log.e("chris","playUrl:" + url);
        if (videoFragment == null) {
            videoFragment = VideoFragment.newInstance(url,false);
        }

        // 替换 videoFragment 进来，把url给他就完事 了
        replaceChildFragment(R.id.frameVideo, videoFragment);
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {

    }

    //-------------------------------

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(isLandscape()){
            llRoomChat.setVisibility(View.GONE);
            ivFullScreen.setVisibility(View.GONE);
        }else{
            llRoomChat.setVisibility(View.VISIBLE);
            ivFullScreen.setVisibility(View.VISIBLE);
        }
        updateVideoLayoutParams();

    }

    public void clickBack(){
        if(isLandscape()){
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }else{
            finish();
        }
    }

    public void clickShare(){

    }

    public boolean isLandscape(){
        return getActivity().getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
    }

    public void clickFullScreen(){
        if(isLandscape()){
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }else{
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    public void clickFollow(){
        startLogin();
    }

    @OnClick({ R.id.ivBack, R.id.ivShare, R.id.ivFullScreen, R.id.tvFollow})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.ivBack:
                clickBack();
                break;
            case R.id.ivShare:
                clickShare();
                break;
            case R.id.ivFullScreen:
                clickFullScreen();
                break;
            case R.id.tvFollow:
                clickFollow();
                break;
        }
    }
}
