package com.fanwe.library.span.view;

import android.content.Context;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import com.fanwe.library.span.builder.SDSpanBuilder;

public abstract class SDSpanTextView extends TextView
{
	private SDSpanBuilder mSpanBuilder;

	public SDSpanTextView(Context context)
	{
		this(context, null);
	}

	public SDSpanTextView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	private void init()
	{
		mSpanBuilder = createSpanBuilder();
	}

	public abstract SDSpanBuilder createSpanBuilder();

	public void setTextContent(String content)
	{
		mSpanBuilder.setTextContent(content);
		mSpanBuilder.buildTextView();
	}

	public void setImageResource(int resId)
	{
		ImageSpan span = new ImageSpan(getContext(), resId);
		mSpanBuilder.reset().addSpan(span).buildTextView();
	}

	public void addImageResource(int resId)
	{
		ImageSpan span = new ImageSpan(getContext(), resId);
		mSpanBuilder.addSpan(span).buildTextView();
	}

	public SDSpanBuilder getSpanBuilder()
	{
		return mSpanBuilder;
	}

	@Override
	protected void onDetachedFromWindow()
	{
		mSpanBuilder.reset();
		super.onDetachedFromWindow();
	}

}
