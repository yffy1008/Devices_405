package com.yffy.devices_405;

import java.io.IOException;
import java.util.Locale;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
	private TextView called_tv,input__tv;

	private MediaPlayer mp;
	private AudioManager am;
	
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
		if  (tts.isSpeaking())  return true;
		switch (keyCode) {
		case Params.KeyValue.NUMBER_0:
			input__tv.append("0");
			break;
		case Params.KeyValue.NUMBER_1:
			input__tv.append("1");
			break;
		case Params.KeyValue.NUMBER_2:
			input__tv.append("2");
			break;
		case Params.KeyValue.NUMBER_3:
			input__tv.append("3");
			break;
		case Params.KeyValue.NUMBER_4:
			input__tv.append("4");
			break;
		case Params.KeyValue.NUMBER_5:
			input__tv.append("5");
			break;
		case Params.KeyValue.NUMBER_6:
			input__tv.append("6");
			break;
		case Params.KeyValue.NUMBER_7:
			input__tv.append("7");
			break;
		case Params.KeyValue.NUMBER_8:
			input__tv.append("8");
			break;
		case Params.KeyValue.NUMBER_9:
			input__tv.append("9");
			break;
		case Params.KeyValue.NUMBER_BACK:
			buttonDelete(input__tv.getText());
			break;
		case Params.KeyValue.NUMBER_ENTER:
			buttonEnter(input__tv.getText());
			break;
		case Params.KeyValue.NUMBER_DEL:
			called_tv.setText("");
			input__tv.setText("");
			called_tv.setVisibility(View.GONE);
			if  (!vv.isPlaying())  vv.start();
			break;
		case Params.KeyValue.NUMBER_PLUS:
			am.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI); 
			break;
		case Params.KeyValue.NUMBER_REDUCE:
			am.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);// 调低声音
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

	private void buttonDelete(CharSequence number){
		StringBuffer sb = new StringBuffer(number);
		if (sb.length() >= 1) {
			sb.deleteCharAt(number.length() - 1);
			input__tv.setText(sb.toString());
		}
	}
	
	private void buttonEnter(CharSequence number){
		if  (number.length() == 0)  return;
		vv.pause();
		playDingDong();
		input__tv.setText("");
		called_tv.setVisibility(View.VISIBLE);
		called_tv.setText("请" + number.toString() + "号贵宾就餐");
		tts.speak("请" + number.toString() + "号贵宾就餐", TextToSpeech.QUEUE_ADD, null);
		mHandler.sendMessageDelayed(mHandler.obtainMessage(1) , 3000);
	}
	
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				vv.start();
				called_tv.setVisibility(View.GONE);
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

		called_tv = (TextView) findViewById(R.id.ads_textview);
		called_tv.setVisibility(View.GONE);
		input__tv = (TextView)findViewById(R.id.input_info);
		am = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
		
		tts = new TextToSpeech(MainActivity.this,new OnInitListener() {
			@Override
			public void onInit(int status) {
				tts.setLanguage(Locale.CHINESE);
			}
		});
		
	}
	
}
