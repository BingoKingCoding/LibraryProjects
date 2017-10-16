package com.fanwe.library.span.model;

import android.content.Context;
import android.content.res.Resources;
import android.widget.TextView;

public class SDSpanInfo
{
	private MatcherInfo matcherInfo = new MatcherInfo();
	private TextView textView;
	private boolean enable = true;

	private Context context;
	private Resources resources;
	private Object span;

	public SDSpanInfo(TextView textView, Object span)
	{
		this.textView = textView;
		this.span = span;
	}

	public void setSpan(Object span)
	{
		this.span = span;
	}

	public Object getSpan()
	{
		return span;
	}

	public void postInvalidate()
	{
		textView.postInvalidate();
	}

	public void postInvalidate(long delay)
	{
		textView.postInvalidateDelayed(delay);
	}

	public Resources getResources()
	{
		return resources;
	}

	public Context getContext()
	{
		return context;
	}

	public MatcherInfo getMatcherInfo()
	{
		return matcherInfo;
	}

	public void setMatcherInfo(MatcherInfo matcherInfo)
	{
		this.matcherInfo = matcherInfo;
	}

	public TextView getTextView()
	{
		return textView;
	}

	public void setTextView(TextView textView)
	{
		this.textView = textView;
		this.context = textView.getContext();
		this.resources = textView.getResources();
	}

	public boolean isEnable()
	{
		return enable;
	}

	public void setEnable(boolean enable)
	{
		this.enable = enable;
	}

}
