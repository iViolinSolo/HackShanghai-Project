package com.eva.me.hackshanghai_diffchoice;

import java.io.File;
import java.io.IOException;
import java.text.BreakIterator;
import java.util.List;

import com.eva.me.hackshanghai_diffchoice.listener.ZoomListener;
import com.eva.me.hackshanghai_diffchoice.listener.onZoomLargeListener;
import com.eva.me.hackshanghai_diffchoice.listener.onZoomSmallListener;
import com.eva.me.hackshanghai_diffchoice.util.FileUtils;
import com.eva.me.hackshanghai_diffchoice.util.ImagePiece;
import com.eva.me.hackshanghai_diffchoice.util.ImageUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Camera2FileActivity extends Activity {

	private Button btnCamera, btnConfirm;
	private Bitmap bm=null;
	private ImageView ivReveal;
	private TextView tvRight, tvTop;
	private String TAG = "Camera2FileActivity";
	private Context context;
	
	private int sizeVertical = 2;
	private int sizeHorizontal = 2;
	//max value 
	private final int maxHorizontalSize  = 20;
	private final int maxVerticalSize = 20;
	
	private boolean hasResult  = false;
	
	//TEMP
	Handler mHandler;
	List<ImagePiece> lPieces;
	Runnable runnable;
	int label = 0;
	
	private void showToast(Context context, String str) {
		Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera2_file);
		init();
	}

	private void init() {
		context = Camera2FileActivity.this;
		//init views
		btnCamera = (Button) findViewById( R.id.btnCamera);
		btnConfirm = (Button) findViewById(R.id.btnConfirm);
		ivReveal = (ImageView) findViewById( R.id.imageView1);
		tvRight = (TextView) findViewById(R.id.tvRight);
		tvTop = (TextView) findViewById(R.id.tvTop);
		
		btnCamera.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent iStartCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				File f = null;
				f = FileUtils.getPhotoDir();
				iStartCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
				startActivityForResult(iStartCamera, 1);
//				startActivity(iStartCamera);
			}
			
		});
		
		btnConfirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (bm != null) {
					lPieces = ImageUtils.split(bm, sizeHorizontal, sizeVertical);
					
					mHandler.obtainMessage(1223).sendToTarget();
				}else {
					showToast(context, "没有图片~ 请先拍摄...");
				}
			}
		});
		
		//TEMP
		mHandler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case 1223:
					mHandler.postDelayed(runnable, 1000);
					break;

				default:
					break;
				}
			};
		};
		
		runnable = new Runnable() {
			
			@Override
			public void run() {
				if (label >= lPieces.size()) {
					return;
				}
				ImagePiece temp = lPieces.get(label);
				ivReveal.setImageBitmap(temp.bitmap);
				label++;
				mHandler.postDelayed(runnable, 1000);
			}
		};
		
		ZoomListener mZoomListener = new ZoomListener();
		mZoomListener.setOnZoomLargeListener(new onZoomLargeListener() {
			
			@Override
			public void onZoomLarge(int direction) {
				switch (direction) {
				case ZoomListener.LeftRight:
//					sizeHorizontal+=1;
					sizeHorizontal = (sizeHorizontal+1)>maxHorizontalSize? maxHorizontalSize: (sizeHorizontal+1) ;
					tvTop.setText("当前横向格子数："+sizeHorizontal);
					break;
				case ZoomListener.UpDown:
//					sizeVertical+=1;
					sizeVertical = (sizeVertical+1) > maxVerticalSize?maxVerticalSize:(sizeVertical+1);
					tvRight.setText("当前纵向格子数："+sizeVertical);
					break;

				default:
					break;
				}
			}
		});
		mZoomListener.setOnZoomSmallListener(new  onZoomSmallListener() {
			
			@Override
			public void onZoomSmall(int direction) {
				switch (direction) {
				case ZoomListener.LeftRight:
//					sizeHorizontal-=1;
					sizeHorizontal = (sizeHorizontal-1)<2?2:(sizeHorizontal-1);
					tvTop.setText("当前横向格子数："+sizeHorizontal);  
					break;
				case ZoomListener.UpDown:
//					sizeVertical-=1;
					sizeVertical = (sizeVertical-1)<2?2:(sizeVertical-1);
					tvRight.setText("当前纵向格子数："+sizeVertical);
					break;

				default:
					break;
				}
			}
		});
		ivReveal.setOnTouchListener(mZoomListener);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
//			Bundle bundle = data.getExtras();//用这两句就有问题 妈蛋。。。也是醉了
//			Bitmap bm = (Bitmap) bundle.get("data");
			Log.d(TAG, "onActivityResult in");//曲线救国喽~
			bm = FileUtils.getLoacalBitmap();
			ivReveal.setImageBitmap(bm);
			hasResult = true;
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.camera2_file, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
