/*
 * Copyright (C) 2011 VOV IO (http://vov.io/)
 */

package com.example.recordandplay;

import com.example.recordandplay.R;
//import com.nmbb.oplayer.exception.Logger;
//import com.nmbb.oplayer.ui.vitamio.LibsChecker;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;
import Data.CfgData;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

public class VideoPlayV2Activity extends Activity implements OnCompletionListener, OnInfoListener {

	private String mPath;
	private String mTitle;
	private VideoView mVideoView;
	private View mVolumeBrightnessLayout;
	private ImageView mOperationBg;
	private ImageView mOperationPercent;
	private AudioManager mAudioManager;
	/** ������� */
	private int mMaxVolume;
	/** ��ǰ���� */
	private int mVolume = -1;
	/** ��ǰ���� */
	private float mBrightness = -1f;
	/** ��ǰ����ģʽ */
	private int mLayout = VideoView.VIDEO_LAYOUT_ZOOM;
	private GestureDetector mGestureDetector;
	private MediaController mMediaController;
	private View mLoadingView;
	private boolean mIsLive = false;
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		// ~~~ ���Vitamio�Ƿ��ѹ�����
        //����׼������
		if (!LibsChecker.checkVitamioLibs(this))
			return;
		
		// ~~~ ��ȡ���ŵ�ַ
		Intent intent = getIntent();
		mPath = intent.getStringExtra("uri");
		mIsLive = intent.getBooleanExtra("isLive", false);

		// ~~~ �󶨿ؼ�
		setContentView(R.layout.video_play_v2);
		mVideoView = (VideoView) findViewById(R.id.surface_view);
		mVolumeBrightnessLayout = findViewById(R.id.operation_volume_brightness);
		mOperationBg = (ImageView) findViewById(R.id.operation_bg);
		mOperationPercent = (ImageView) findViewById(R.id.operation_percent);
		mLoadingView = findViewById(R.id.video_loading);		
		
		// ~~~ ���¼�
		mVideoView.setOnCompletionListener(this);
		mVideoView.setOnInfoListener(this);

		//if (mPath.startsWith("http:") || mPath.startsWith("rtsp:"))
			//mVideoView.setVideoURI(Uri.parse(mPath));
		//else
		//CfgData.GetCfgInfo(this);
		//mVideoView.setBufferSize(Integer.parseInt(CfgData.getBufferSize()) * 1024);
		
		//������ʾ����
		if (mIsLive == false) {
			mMediaController = new MediaController(this);
			mMediaController.setFileName(mTitle);
			mVideoView.setMediaController(mMediaController);
		}
		else
		{
			CfgData.GetCfgInfo(this);
			mVideoView.setBufferSize(Integer.parseInt(CfgData.getBufferSize()) * 1024);			
		}
		
		// ~~~ ������
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		mVideoView.setVideoPath(mPath);
		
		mVideoView.requestFocus();	
		mGestureDetector = new GestureDetector(this, new MyGestureListener());
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mVideoView != null)
			mVideoView.pause();
		Log.i("liftCycle", "onPause");
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mVideoView != null)
		{
			mVideoView.start();
		}
		Log.i("liftCycle", "onResume");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mVideoView != null)
			mVideoView.stopPlayback();
		Log.i("liftCycle", "onDestroy");
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mGestureDetector.onTouchEvent(event))
			return true;

		// �������ƽ���
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_UP:
			endGesture();
			break;
		}

		return super.onTouchEvent(event);
	}

	/** ���ƽ��� */
	private void endGesture() {
		mVolume = -1;
		mBrightness = -1f;

		// ����
		mDismissHandler.removeMessages(0);
		mDismissHandler.sendEmptyMessageDelayed(0, 500);
	}

	private class MyGestureListener extends SimpleOnGestureListener {

		/** ˫�� */
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			if (mLayout == VideoView.VIDEO_LAYOUT_ZOOM)
				mLayout = VideoView.VIDEO_LAYOUT_ORIGIN;
			else
				mLayout++;
			if (mVideoView != null)
				mVideoView.setVideoLayout(mLayout, 0);
			return true;
		}

		/** ���� */
		@SuppressWarnings("deprecation")
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			float mOldX = e1.getX(), mOldY = e1.getY();
			int y = (int) e2.getRawY();
			//��õ�ǰ���ڵ���ʾ��Ϣ��
			Display disp = getWindowManager().getDefaultDisplay();
			int windowWidth = disp.getWidth();
			int windowHeight = disp.getHeight();

			if (mOldX > windowWidth * 4.0 / 5)// �ұ߻���
				onVolumeSlide((mOldY - y) / windowHeight);
			else if (mOldX < windowWidth / 5.0)// ��߻���
				onBrightnessSlide((mOldY - y) / windowHeight);

			return super.onScroll(e1, e2, distanceX, distanceY);
		}
	}

	/** ��ʱ���� */
	private Handler mDismissHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mVolumeBrightnessLayout.setVisibility(View.GONE);
		}
	};

	/**
	 * �����ı�������С
	 * 
	 * @param percent �����ı����
	 */
	private void onVolumeSlide(float percent) {
		if (mVolume == -1) {
			mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			if (mVolume < 0)
				mVolume = 0;

			// ��ʾ
			mOperationBg.setImageResource(R.drawable.video_volumn_bg);
			mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
		}

		int index = (int) (percent * mMaxVolume) + mVolume;
		if (index > mMaxVolume)
			index = mMaxVolume;
		else if (index < 0)
			index = 0;

		// �������
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

		// ���������
		ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
		lp.width = findViewById(R.id.operation_full).getLayoutParams().width * index / mMaxVolume;
		mOperationPercent.setLayoutParams(lp);
	}

	/**
	 * �����ı�����
	 * 
	 * @param percent
	 */
	private void onBrightnessSlide(float percent) {
		if (mBrightness < 0) {
			mBrightness = getWindow().getAttributes().screenBrightness;
			if (mBrightness <= 0.00f)
				mBrightness = 0.50f;
			if (mBrightness < 0.01f)
				mBrightness = 0.01f;

			// ��ʾ
			mOperationBg.setImageResource(R.drawable.video_brightness_bg);
			mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
		}
		WindowManager.LayoutParams lpa = getWindow().getAttributes();
		lpa.screenBrightness = mBrightness + percent;
		if (lpa.screenBrightness > 1.0f)
			lpa.screenBrightness = 1.0f;
		else if (lpa.screenBrightness < 0.01f)
			lpa.screenBrightness = 0.01f;
		getWindow().setAttributes(lpa);

		ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
		lp.width = (int) (findViewById(R.id.operation_full).getLayoutParams().width * lpa.screenBrightness);
		mOperationPercent.setLayoutParams(lp);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if (mVideoView != null)
			mVideoView.setVideoLayout(mLayout, 0);
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCompletion(MediaPlayer player) {
		finish();
	}

	private void stopPlayer() {
		if (mVideoView != null)
			mVideoView.pause();
		else {
			Toast.makeText(this, "stop,mVideoView is null!", Toast.LENGTH_LONG).show();
		}
	}

	private void startPlayer() {
		if (mVideoView != null)
			mVideoView.start();
		else {
			Toast.makeText(this, "start,mVideoView is null!", Toast.LENGTH_LONG).show();
		}
	}

	private boolean isPlaying() {
		return mVideoView != null && mVideoView.isPlaying();
	}

	/** �Ƿ���Ҫ�Զ��ָ����ţ������Զ���ͣ���ָ����� */
	private boolean needResume = false;
	/** ��ʼ��ʱ��û���ţ�mediaplay״̬Ϊ׼��״̬����Ӹ��״�״̬λ*/
	private boolean firstPlay = true;
	@Override
	public boolean onInfo(MediaPlayer arg0, int arg1, int arg2) {
		switch (arg1) {
		case MediaPlayer.MEDIA_INFO_BUFFERING_START:
			//��ʼ���棬��ͣ���ţ�if��ȫ�棬��ʼ�������޷�����			
			if (isPlaying()) {
				stopPlayer();
				needResume = true;
				Log.i("oninfo", "�ٴλ��棬��ͣ����!");
			}
			else if (firstPlay) {
				firstPlay = false;
				needResume = true;
				Log.i("oninfo", "��ʼ���棬��ͣ����!");
			}
			mLoadingView.setVisibility(View.VISIBLE);
			break;
		case MediaPlayer.MEDIA_INFO_BUFFERING_END:
			//������ɣ���������
			if (needResume)
			{
				startPlayer();								
				Log.i("oninfo", "������ɣ���������!");
			}
			mLoadingView.setVisibility(View.GONE);
			break;
		case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
			//��ʾ �����ٶ�
			//Logger.e("download rate:" + arg2);
			//mListener.onDownloadRateChanged(arg2);
			break;
		}
		return true;
	}
}
