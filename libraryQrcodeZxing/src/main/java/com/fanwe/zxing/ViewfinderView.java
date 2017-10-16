/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fanwe.zxing;

import java.util.Collection;
import java.util.HashSet;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.fanwe.library.qrcode.zxing.R;
import com.google.zxing.ResultPoint;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder
 * rectangle and partial transparency outside it, as well as the laser scanner
 * animation and result points. �Զ����View������ʱ�м���ʾ��
 */
public final class ViewfinderView extends View
{

	private static final int[] SCANNER_ALPHA = { 0, 64, 128, 192, 255, 192, 128, 64 };

	/**
	 * 刷新界面的时间
	 */
	private static final long ANIMATION_DELAY = 50L;
	private static final int OPAQUE = 0xFF;

	private final Paint paint;
	private Bitmap resultBitmap;
	private final int maskColor;
	private final int resultColor;
	private final int frameColor;
	private final int laserColor;
	private final int resultPointColor;
	private int scannerAlpha;
	private Collection<ResultPoint> possibleResultPoints;
	private Collection<ResultPoint> lastPossibleResultPoints;

	/**
	 * 四个绿色边角对应的长度
	 */
	private int ScreenRate;

	/**
	 * 四个绿色边角对应的宽度
	 */
	private static final int CORNER_WIDTH = 5;
	/**
	 * 扫描框中的中间线的宽度
	 */
	private static final int MIDDLE_LINE_WIDTH = 6;

	/**
	 * 扫描框中的中间线的与扫描框左右的间隙
	 */
	private static final int MIDDLE_LINE_PADDING = 5;

	/**
	 * 中间那条线每次刷新移动的距离
	 */
	private static int SPEEN_DISTANCE = 10;
	/**
	 * 中间滑动线的最顶端位置
	 */
	private int slideTop;

	/**
	 * 中间滑动线的最底端位置
	 */
	private int slideBottom;

	/**
	 * 字体大小
	 */
	private static final int TEXT_SIZE = 16;
	/**
	 * 字体距离扫描框下面的距离
	 */
	private static final int TEXT_PADDING_TOP = 30;

	/**
	 * 手机的屏幕密度
	 */
	private static float density;
	boolean isFirst;

	// This constructor is used when the class is built from an XML resource.
	public ViewfinderView(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		density = context.getResources().getDisplayMetrics().density;
		// 将像素转换成dp
		ScreenRate = (int) (25 * density);

		// Initialize these once for performance rather than calling them every
		// time in onDraw().
		paint = new Paint();
		Resources resources = getResources();
		maskColor = resources.getColor(R.color.viewfinder_mask);
		resultColor = resources.getColor(R.color.result_view);
		frameColor = resources.getColor(R.color.viewfinder_frame);
		laserColor = resources.getColor(R.color.viewfinder_laser);
		resultPointColor = resources.getColor(R.color.possible_result_points);
		scannerAlpha = 0;
		possibleResultPoints = new HashSet<ResultPoint>(5);
	}

	@Override
	public void onDraw(Canvas canvas)
	{
		// 中间的扫描框，你要修改扫描框的大小，去CameraManager里面修改
		Rect frame = CameraManager.get().getFramingRect();
		if (frame == null)
		{
			return;
		}

		// 初始化中间线滑动的最上边和最下边
		if (!isFirst)
		{
			isFirst = true;
			slideTop = frame.top;
			slideBottom = frame.bottom;
		}

		// 获取屏幕的宽和高
		int width = canvas.getWidth();
		int height = canvas.getHeight();

		// Draw the exterior (i.e. outside the framing rect) darkened
		paint.setColor(resultBitmap != null ? resultColor : maskColor);

		// 画出扫描框外面的阴影部分，共四个部分，扫描框的上面到屏幕上面，扫描框的下面到屏幕下面
		// 扫描框的左边面到屏幕左边，扫描框的右边到屏幕右边
		canvas.drawRect(0, 0, width, frame.top, paint);
		canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
		canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
		canvas.drawRect(0, frame.bottom + 1, width, height, paint);

		if (resultBitmap != null)
		{
			// Draw the opaque result bitmap over the scanning rectangle
			paint.setAlpha(OPAQUE);
			canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
		} else
		{

			// Draw a two pixel solid black border inside the framing rect
			// paint.setColor(frameColor);
			// canvas.drawRect(frame.left, frame.top, frame.right + 1, frame.top
			// + 2, paint);
			// canvas.drawRect(frame.left, frame.top + 2, frame.left + 2,
			// frame.bottom - 1, paint);
			// canvas.drawRect(frame.right - 1, frame.top, frame.right + 1,
			// frame.bottom - 1, paint);
			// canvas.drawRect(frame.left, frame.bottom - 1, frame.right + 1,
			// frame.bottom + 1, paint);

			// 画扫描框边上的角，总共8个部分
			paint.setColor(Color.GREEN);
			canvas.drawRect(frame.left - CORNER_WIDTH, frame.top - CORNER_WIDTH, frame.left + ScreenRate - CORNER_WIDTH, frame.top + CORNER_WIDTH
					- CORNER_WIDTH, paint);
			canvas.drawRect(frame.left - CORNER_WIDTH, frame.top - CORNER_WIDTH, frame.left + CORNER_WIDTH - CORNER_WIDTH, frame.top + ScreenRate
					- CORNER_WIDTH, paint);
			canvas.drawRect(frame.right - ScreenRate + CORNER_WIDTH, frame.top - CORNER_WIDTH, frame.right + CORNER_WIDTH, frame.top + CORNER_WIDTH
					- CORNER_WIDTH, paint);
			canvas.drawRect(frame.right - CORNER_WIDTH + CORNER_WIDTH, frame.top - CORNER_WIDTH, frame.right + CORNER_WIDTH, frame.top + ScreenRate
					- CORNER_WIDTH, paint);
			canvas.drawRect(frame.left - CORNER_WIDTH, frame.bottom - CORNER_WIDTH + CORNER_WIDTH, frame.left + ScreenRate - CORNER_WIDTH,
					frame.bottom + CORNER_WIDTH, paint);
			canvas.drawRect(frame.left - CORNER_WIDTH, frame.bottom - ScreenRate + CORNER_WIDTH, frame.left + CORNER_WIDTH - CORNER_WIDTH,
					frame.bottom + CORNER_WIDTH, paint);
			canvas.drawRect(frame.right - ScreenRate + CORNER_WIDTH, frame.bottom + CORNER_WIDTH - CORNER_WIDTH, frame.right + CORNER_WIDTH,
					frame.bottom + CORNER_WIDTH, paint);
			canvas.drawRect(frame.right - CORNER_WIDTH + CORNER_WIDTH, frame.bottom - ScreenRate + CORNER_WIDTH, frame.right + CORNER_WIDTH,
					frame.bottom + CORNER_WIDTH, paint);

			// Draw a red "laser scanner" line through the middle to show
			// decoding is active
			// paint.setColor(laserColor);
			// paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
			// scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
			// int middle = frame.height() / 2 + frame.top;
			// canvas.drawRect(frame.left + 2, middle - 1, frame.right - 1,
			// middle + 2, paint);

			// 绘制中间的线,每次刷新界面，中间的线往下移动SPEEN_DISTANCE
			slideTop += SPEEN_DISTANCE;
			if (slideTop >= frame.bottom)
			{
				slideTop = frame.bottom;
				SPEEN_DISTANCE = -10;
			}

			if (slideTop <= frame.top)
			{
				slideTop = frame.top;
				SPEEN_DISTANCE = 10;
			}

			Rect lineRect = new Rect();
			lineRect.left = frame.left;
			lineRect.right = frame.right;
			lineRect.top = slideTop;
			lineRect.bottom = slideTop + 18;
			canvas.drawBitmap(((BitmapDrawable) (getResources().getDrawable(R.drawable.qrcode_scan_line))).getBitmap(), null, lineRect, paint);

			// 画扫描框下面的字
			paint.setColor(Color.WHITE);
			paint.setTextSize(TEXT_SIZE * density);
			paint.setAlpha(0x40);
			paint.setTypeface(Typeface.create("System", Typeface.BOLD));
			canvas.drawText(getResources().getString(R.string.scan_text), frame.left, (float) (frame.bottom + (float) TEXT_PADDING_TOP * density),
					paint);

			Collection<ResultPoint> currentPossible = possibleResultPoints;
			Collection<ResultPoint> currentLast = lastPossibleResultPoints;
			if (currentPossible.isEmpty())
			{
				lastPossibleResultPoints = null;
			} else
			{
				possibleResultPoints = new HashSet<ResultPoint>(5);
				lastPossibleResultPoints = currentPossible;
				paint.setAlpha(OPAQUE);
				paint.setColor(resultPointColor);
				for (ResultPoint point : currentPossible)
				{
					canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 6.0f, paint);
				}
			}
			if (currentLast != null)
			{
				paint.setAlpha(OPAQUE / 2);
				paint.setColor(resultPointColor);
				for (ResultPoint point : currentLast)
				{
					canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 3.0f, paint);
				}
			}

			// Request another update at the animation interval, but only
			// repaint the laser line,
			// not the entire viewfinder mask.
			// 只刷新扫描框的内容，其他地方不刷新
			postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);
		}
	}

	public void drawViewfinder()
	{
		resultBitmap = null;
		invalidate();
	}

	/**
	 * Draw a bitmap with the result points highlighted instead of the live
	 * scanning display.
	 * 
	 * @param barcode
	 *            An image of the decoded barcode.
	 */
	public void drawResultBitmap(Bitmap barcode)
	{
		resultBitmap = barcode;
		invalidate();
	}

	public void addPossibleResultPoint(ResultPoint point)
	{
		possibleResultPoints.add(point);
	}

}
