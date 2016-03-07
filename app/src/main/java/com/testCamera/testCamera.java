package com.testCamera;

import java.util.Timer;
import java.util.TimerTask;

import android.graphics.BitmapFactory;
import android.widget.Button;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.Android.PlanarYUVLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import android.app.Activity;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class testCamera extends Activity {
	/** Called when the activity is first created. */
	private SurfaceView sfvCamera;
	private SFHCamera sfhCamera;
	private ImageView imgView;
	private ImageView imgView2;
	private View centerView;
	private TextView txtScanResult;
	private Timer mTimer;
	private MyTimerTask mTimerTask;
	// 按照标准HVGA
	static int width = 480;
	static int height = 320;
	int dstLeft, dstTop, dstWidth, dstHeight;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		this.setTitle("Android条码/二维码识别Demo-----hellogv");
		width = getWindowManager().getDefaultDisplay().getWidth();
		height = getWindowManager().getDefaultDisplay().getHeight();
		imgView = (ImageView) this.findViewById(R.id.ImageView01);
		imgView2 = (ImageView) this.findViewById(R.id.ImageView02);
		centerView = (View) this.findViewById(R.id.centerView);
		sfvCamera = (SurfaceView) this.findViewById(R.id.sfvCamera);
		sfhCamera = new SFHCamera(sfvCamera.getHolder(), width, height,
				previewCallback);
		Button click = (Button) findViewById(R.id.click);
		click.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						if (dstLeft == 0) {//只赋值一次
							dstLeft = centerView.getLeft() * width
									/ getWindowManager().getDefaultDisplay().getWidth();
							dstTop = centerView.getTop() * height
									/ getWindowManager().getDefaultDisplay().getHeight();
							dstWidth = (centerView.getRight() - centerView.getLeft()) * width
									/ getWindowManager().getDefaultDisplay().getWidth();
							dstHeight = (centerView.getBottom() - centerView.getTop()) * height
									/ getWindowManager().getDefaultDisplay().getHeight();
						}
						sfhCamera.AutoFocusAndPreviewCallback();
					}
				});
				thread.start();
			}
		});
		txtScanResult = (TextView) this.findViewById(R.id.txtScanResult);
		// 初始化定时器
//		mTimer = new Timer();
//		mTimerTask = new MyTimerTask();
//		mTimer.schedule(mTimerTask, 0, 80);
	}

		class MyTimerTask extends TimerTask {
			@Override
			public void run() {
				if (dstLeft == 0) {//只赋值一次
					dstLeft = centerView.getLeft() * width
							/ getWindowManager().getDefaultDisplay().getWidth();
					dstTop = centerView.getTop() * height
							/ getWindowManager().getDefaultDisplay().getHeight();
					dstWidth = (centerView.getRight() - centerView.getLeft()) * width
							/ getWindowManager().getDefaultDisplay().getWidth();
					dstHeight = (centerView.getBottom() - centerView.getTop()) * height
							/ getWindowManager().getDefaultDisplay().getHeight();
				}
				sfhCamera.AutoFocusAndPreviewCallback();
			}
		}

		/**
		 *  自动对焦后输出图片
		 */
		private Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
			@Override
			public void onPreviewFrame(byte[] data, Camera arg1) {
				//取得指定范围的帧的数据
				PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(
						data, width, height, dstLeft, dstTop, dstWidth, dstHeight);
				//取得灰度图
				Bitmap mBitmap = source.renderCroppedGreyscaleBitmap();
				//显示灰度图
				Bitmap orgMap = BitmapFactory.decodeByteArray(data, 0, data.length);
				imgView.setImageBitmap(mBitmap);
				imgView2.setImageBitmap(orgMap);
				BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
				MultiFormatReader reader = new MultiFormatReader();
				try {
					Result result = reader.decode(bitmap);
					String strResult = "BarcodeFormat:"
							+ result.getBarcodeFormat().toString() + "  text:"
							+ result.getText();
					txtScanResult.setText(strResult);
				} catch (Exception e) {
					txtScanResult.setText("Scanning");
				}
			}
		};

}