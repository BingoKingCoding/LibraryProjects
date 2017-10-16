package com.fanwe.library.span.builder;

import java.util.List;

import com.fanwe.library.span.model.MatcherInfo;
import com.fanwe.library.span.utils.SDPatternUtil;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;

public class SDSpannableStringBuilder extends SpannableStringBuilder
{

	public static final String PATTERN_URL = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";
	public static final int DEFAULT_FLAG = Spannable.SPAN_EXCLUSIVE_EXCLUSIVE;

	public void reset()
	{
		clear();
		clearSpans();
	}

	public void setSpan(Object what, int start, int end)
	{
		setSpan(what, start, end, DEFAULT_FLAG);
	}

	public void appendSpan(Object span, CharSequence key)
	{
		appendSpan(span, key, DEFAULT_FLAG);
	}

	public void appendSpan(Object span, CharSequence key, int flags)
	{
		if (span != null && !TextUtils.isEmpty(key))
		{
			append(key);
			int end = length();
			int start = end - key.length();
			setSpan(span, start, end, flags);
		}
	}

	public void coverSpan(Object spanOld, Object spanNew)
	{
		coverSpan(spanOld, spanNew, DEFAULT_FLAG);
	}

	public void coverSpan(Object spanOld, Object spanNew, int flags)
	{
		if (spanNew != null && spanOld != null)
		{
			int end = getSpanEnd(spanOld);
			int start = getSpanStart(spanOld);

			setSpan(spanNew, start, end, flags);
		}
	}

	public List<MatcherInfo> Matche(String strPattern)
	{
		return SDPatternUtil.findMatcherInfo(strPattern, toString());
	}

	public List<MatcherInfo> MatcheUrl()
	{
		return SDPatternUtil.findMatcherInfo(PATTERN_URL, toString());
	}

	public void setSpan(Object what, MatcherInfo matcherInfo)
	{
		if (matcherInfo != null)
		{
			setSpan(what, matcherInfo.getStart(), matcherInfo.getEnd());
		}
	}

}
