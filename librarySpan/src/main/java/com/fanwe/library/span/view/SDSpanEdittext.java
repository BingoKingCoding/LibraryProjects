package com.fanwe.library.span.view;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.text.Spannable;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.widget.EditText;

import com.fanwe.library.span.model.MatcherInfo;
import com.fanwe.library.span.model.SDSpanInfo;

public class SDSpanEdittext extends EditText
{

	private Map<Integer, SDSpanInfo> mMapSpan = new HashMap<Integer, SDSpanInfo>();

	public SDSpanEdittext(Context context)
	{
		this(context, null);
	}

	public SDSpanEdittext(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	private void init()
	{
	}

	public void insert(String text)
	{
		int index = getSelectionStart();
		getText().insert(index, text);
	}

	public void insertImageRresource(int resId)
	{
		ImageSpan span = new ImageSpan(getContext(), resId);
		insertSpan(span);
	}

	/**
	 * 此方法会为span创建默认的key占位，如果需要指定key，用public void insertSpan(SDSpanInfo
	 * spanInfo)方法
	 * 
	 * @param span
	 */
	public void insertSpan(Object span)
	{
		SDSpanInfo spanInfo = new SDSpanInfo(this, span);
		insertSpan(spanInfo);
	}

	public void insertSpan(SDSpanInfo spanInfo)
	{
		MatcherInfo matcherInfo = spanInfo.getMatcherInfo();
		Object span = spanInfo.getSpan();

		matcherInfo.setStart(getSelectionStart());
		insert(matcherInfo.getKey());
		getText().setSpan(span, matcherInfo.getStart(), matcherInfo.getEnd(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		mMapSpan.put(matcherInfo.getEnd(), spanInfo);
	}

	public void deleteLastSpan()
	{
		int index = getSelectionStart();
		SDSpanInfo spanInfo = mMapSpan.get(index);
		if (spanInfo != null)
		{
			MatcherInfo matcherInfo = spanInfo.getMatcherInfo();
			getText().delete(matcherInfo.getStart(), matcherInfo.getEnd());
			mMapSpan.remove(index);
		}
	}

}
