package com.example.myimageview;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

public class LazyImageView extends FrameLayout {
	private View view;
	private CircleImageView back;
	private CircleImageView front;
	private Context context;
	private Bitmap frontBitmap;
	private Bitmap backBitmap;
	private int changeX;
	private int changeY;
	private ProgressBar myprograssbar;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (changeY < frontBitmap.getHeight())
				this.sendEmptyMessageDelayed(0, 50);
			RefreshView();
		};
	};
	private LayoutParams params;
	private Options opt;

	public LazyImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initView();
	}

	public LazyImageView(Context context) {
		super(context);
		this.context = context;
		initView();
	}

	private void initView() {
		view = View.inflate(context, R.layout.lazyimageview, this);
		back = (CircleImageView) view.findViewById(R.id.back);
		front = (CircleImageView) view.findViewById(R.id.front);
		myprograssbar = (ProgressBar) view.findViewById(R.id.myprograssbar);

	}

	void setImages(int frontId, int backId) {
		if (opt == null) {
			opt = new Options();
			opt.inPreferredConfig = Bitmap.Config.ARGB_4444;
			opt.inMutable = true;
		}
		try {
			InputStream in = getResources().openRawResource(frontId);
			this.frontBitmap = BitmapFactory.decodeStream(in, null, opt);
			in.close();
			in = getResources().openRawResource(backId);
			this.backBitmap = BitmapFactory.decodeStream(in, null, opt);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		front.setImageBitmap(frontBitmap);
		back.setImageBitmap(backBitmap);
	}

	public Bitmap getFrontBitmap() {
		return frontBitmap;
	}

	public void setImages(Bitmap frontBitmap, Bitmap backBitmap) {
		this.frontBitmap = frontBitmap;
		this.backBitmap = backBitmap;

		front.setImageBitmap(frontBitmap);
		back.setImageBitmap(backBitmap);
	}

	public void startAnimation() {
		handler.sendEmptyMessage(0);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 必须测量之后才能确定图片大小
		setProgressParams();
	}

	private void setProgressParams() {
		params = new LayoutParams(back.getWidth() / 4, back.getWidth() / 4);
		params.gravity = Gravity.CENTER;
		myprograssbar.setLayoutParams(params);
	}

	private void RefreshView() {
		// 设置为透明显示下面的图片
		if (changeY < frontBitmap.getHeight()) {
			for (changeX = 0; changeX < frontBitmap.getWidth(); changeX++) {
				frontBitmap.setPixel(changeX, changeY, Color.TRANSPARENT);
			}
			front.setImageBitmap(frontBitmap);
			System.gc();
			changeY++;
		} else {
			myprograssbar.setVisibility(ProgressBar.GONE);
		}

	}

	@Override
	public void computeScroll() {
		
	}

}