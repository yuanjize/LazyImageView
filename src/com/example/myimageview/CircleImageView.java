package com.example.myimageview;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

public class CircleImageView extends ImageView {
	private Paint paint;
	private Drawable drawable;
	private Xfermode xfermode = new PorterDuffXfermode(Mode.DST_IN);
	private WeakReference<Bitmap> cachebitmap;
	private float scale;
	private Bitmap maskbitmap;
	private int minScale;
	private BitmapDrawable bitmapDrawable;

	public CircleImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint();
		paint.setAntiAlias(true);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		minScale = Math.min(getMeasuredHeight(), getMeasuredWidth());
		setMeasuredDimension(minScale, minScale);

	}

	public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		this(context, attrs);
		if (paint == null) {
			paint = new Paint();
			paint.setAntiAlias(true);
		}
	}

	public CircleImageView(Context context) {
		this(context, null);
		if (paint == null) {
			paint = new Paint();
			paint.setAntiAlias(true);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Log.e("YUAN", drawable + "...............AAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		if (drawable == null) {
			drawable = getDrawable();
		}
		scale = 1.0f;
		Bitmap bitmap = cachebitmap == null ? null : cachebitmap.get();
		if (bitmap == null || bitmap.isRecycled()) {
			bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
					Bitmap.Config.ARGB_4444);

			Canvas drawCanvas = new Canvas(bitmap);
			if (drawable != null) {
				int min = Math.min(drawable.getIntrinsicHeight(),
						drawable.getIntrinsicWidth());
				scale = (getWidth() * 1.0F / min);
			}
			drawable.setBounds(0, 0,
					(int) (scale * drawable.getIntrinsicWidth()),
					(int) (scale * drawable.getIntrinsicHeight()));

			drawable.draw(drawCanvas);
			if (maskbitmap == null || maskbitmap.isRecycled()) {
				maskbitmap = getBitmap();
			}
			paint.reset();
			paint.setFilterBitmap(false);
			paint.setXfermode(xfermode);
			drawCanvas.drawBitmap(maskbitmap, 0, 0, paint);
			cachebitmap = new WeakReference<Bitmap>(bitmap);
		}
		paint.setXfermode(null);
		canvas.drawBitmap(bitmap, 0, 0, null);
	}

	private Bitmap getBitmap() {

		Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
				Bitmap.Config.ARGB_4444);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.BLACK);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawCircle(getWidth() / 2, getWidth() / 2, getWidth() / 2, paint);
		return bitmap;
	}

	@Override
	public void invalidate() {
		cachebitmap = null;
		if (maskbitmap != null) {
			maskbitmap.recycle();
			maskbitmap = null;
		}
		super.invalidate();

	}

	@Override
	public void setImageBitmap(Bitmap bm) {

		super.setImageBitmap(bm);
		invalidate();
	}
}
