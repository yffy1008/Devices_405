package com.yffy.devices_405;

import java.io.IOException;
import java.util.Locale;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.pm.ActivityInfo;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.VideoView;

@SuppressLint("HandlerLeak")
public class MainActivity extends Activity {

	private final int NEXT = 0;

	private int currentIndex = 1;
	private VideoView vv;
	private TextView ads_tv;

	private MediaPlayer mp;
	private AudioManager am;
	private StringBuilder information;
	
	private TextToSpeech tts;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 隐藏标题栏
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // 设置水平
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN); // 隐藏状态栏
		setContentView(R.layout.activity_main);
		
		initialWidget();
	}

	@Override
	protected void onStart() {
		super.onStart();
		startPlay(currentIndex + "", NEXT);
	}

	@Override
	protected void onStop() {
		super.onStop();
		vv.stopPlayback();
		if (mp != null) {
			mp.release();
			mp = null;
		}
		if (tts != null) {
			tts.shutdown();
			tts = null;
		}
		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		controlVolume(keyCode);
		switch (keyCode) {
		case Params.KeyValue.NUMBER_0:
			information.append("0");
			break;
		case Params.KeyValue.NUMBER_1:
			information.append("1");
			break;
		case Params.KeyValue.NUMBER_2:
			information.append("2");
			break;
		case Params.KeyValue.NUMBER_3:
			information.append("3");
			break;
		case Params.KeyValue.NUMBER_4:
			information.append("4");
			break;
		case Params.KeyValue.NUMBER_5:
			information.append("5");
			break;
		case Params.KeyValue.NUMBER_6:
			information.append("6");
			break;
		case Params.KeyValue.NUMBER_7:
			information.append("7");
			break;
		case Params.KeyValue.NUMBER_8:
			information.append("8");
			break;
		case Params.KeyValue.NUMBER_9:
			information.append("9");
			break;
		case Params.KeyValue.NUMBER_BACK:
			buttonDelete(information);
			break;
		case Params.KeyValue.NUMBER_ENTER:
			buttonEnter(information);
			break;
		case Params.KeyValue.NUMBER_DEL:
			ads_tv.setText("");
			break;
		}
		return true;
	}

	// 播放控制
	private void startPlay(String number, int mode) {
		currentIndex = 1;
		vv.setVideoPath(Params.Video.HOME_PATH + "001.avi");
		vv.start();
	}

	private void playDingDong() {
		try {
			mp.reset();
			mp.setDataSource(Params.Video.HOME_PATH + "dingdong.mp3");
			mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mp.prepare();
			mp.start();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean controlVolume(int id){
		switch (id) {
		case Params.KeyValue.NUMBER_PLUS:
			am.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI); 
			return true;
		case Params.KeyValue.NUMBER_REDUCE:
			am.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);// 调低声音
			return true;
		}
		return false;
	}
	
	private void buttonDelete(CharSequence number){
		StringBuffer sb = new StringBuffer(number);
		if (sb.length() >= 1) {
			sb.deleteCharAt(number.length() - 1);
			ads_tv.setText(sb.toString());
		} else {
			vv.start();
		}
	}
	
	private void buttonEnter(CharSequence number){
		if (number.length() == 0) return;
		vv.pause();
		playDingDong();
		ads_tv.setVisibility(View.VISIBLE);
		ads_tv.setText("请" + number.toString() + "号贵宾就餐");
		tts.speak("     请" + number.toString() + "号贵宾就餐", TextToSpeech.QUEUE_ADD, null);
		information.setLength(0);
	}
	
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
	
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				vv.start();
				ads_tv.setVisibility(View.GONE);
				information.setLength(0);     
				break;
			}
		};
	};

	private void initialWidget() {
		vv = (VideoView)findViewById(R.id.vv);
		vv.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				vv.stopPlayback();
				currentIndex++;
				startPlay(currentIndex+"", NEXT);
			}
		});

		mp = new MediaPlayer();
		mp.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp1) {
//				mp.release();
			}
		});

		ads_tv = (TextView) findViewById(R.id.ads_textview);
		ads_tv.setVisibility(View.GONE);
		am = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
		
		tts = new TextToSpeech(MainActivity.this,new OnInitListener() {
			@Override
			public void onInit(int status) {
				tts.setLanguage(Locale.CHINESE);
			}
		});
		
		information = new StringBuilder();
	}
	
}
