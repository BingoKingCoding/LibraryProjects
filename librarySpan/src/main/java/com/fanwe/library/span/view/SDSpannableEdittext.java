package com.fanwe.library.span.view;

import android.content.Context;
import android.text.Spannable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.widget.EditText;

import com.fanwe.library.span.model.MatcherInfo;
import com.fanwe.library.span.model.SpanInfo;

public class SDSpannableEdittext extends EditText
{

	private SparseArray<SpanInfo> arrSpan = new SparseArray<SpanInfo>();

	public SDSpannableEdittext(Context context)
	{
		this(context, null);
	}

	public SDSpannableEdittext(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	private void init()
	{
	}

	public void insertSpan(Object span, String key)
	{
		SpanInfo spanInfo = new SpanInfo(span);

		MatcherInfo matcherInfo = new MatcherInfo();
		matcherInfo.setKey(key);

		spanInfo.setMatcherInfo(matcherInfo);

		insertSpan(spanInfo);
	}

	private void insertSpan(SpanInfo spanInfo)
	{
		MatcherInfo matcherInfo = spanInfo.getMatcherInfo();
		Object span = spanInfo.getSpan();

		matcherInfo.setStart(getSelectionStart());
		insert(matcherInfo.getKey());
		getText().setSpan(span, matcherInfo.getStart(), matcherInfo.getEnd(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		arrSpan.put(matcherInfo.getEnd(), spanInfo);
	}

	private void insert(String text)
	{
		int index = getSelectionStart();
		getText().insert(index, text);
	}

	public void delete()
	{
		int index = getSelectionStart();
		SpanInfo spanInfo = arrSpan.get(index);
		if (spanInfo != null)
		{
			MatcherInfo matcherInfo = spanInfo.getMatcherInfo();
			getText().delete(matcherInfo.getStart(), matcherInfo.getEnd());
			arrSpan.remove(index);
		}
	}

}
