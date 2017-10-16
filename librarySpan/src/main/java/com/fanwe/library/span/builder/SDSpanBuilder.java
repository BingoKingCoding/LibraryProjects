package com.fanwe.library.span.builder;

import java.util.ArrayList;
import java.util.List;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.view.View;
import android.widget.TextView;

import com.fanwe.library.span.SDClickableSpan;
import com.fanwe.library.span.model.MatcherInfo;
import com.fanwe.library.span.model.SDSpanInfo;
import com.fanwe.library.span.utils.SDPatternUtil;

public abstract class SDSpanBuilder
{
	private TextView mTextView;
	private List<MatcherInfo> mListKey = new ArrayList<MatcherInfo>();
	private List<SDSpanInfo> mListSpanInfo = new ArrayList<SDSpanInfo>();
	private List<String> mListPattern = new ArrayList<String>();
	private SpannableStringBuilder mSb = new SpannableStringBuilder();

	protected OnClickSpanListener mListenerOnClickSpan;

	public void setmListenerOnClickSpan(OnClickSpanListener listenerOnClickSpan)
	{
		this.mListenerOnClickSpan = listenerOnClickSpan;
	}

	public SDSpanBuilder(TextView textView)
	{
		this.mTextView = textView;
	}

	public TextView getTextView()
	{
		return mTextView;
	}

	public boolean hasSpan()
	{
		return !mListSpanInfo.isEmpty();
	}

	public void setTextContent(String content)
	{
		reset();
		if (!TextUtils.isEmpty(content))
		{
			appendText(content);
			findKeys(content);
			for (MatcherInfo matcherInfo : mListKey)
			{
				Object span = createSpanByFindKey(matcherInfo);

				SDSpanInfo spanInfo = new SDSpanInfo(mTextView, span);
				spanInfo.setMatcherInfo(matcherInfo);

				addSpanInfo(spanInfo, true);
			}
		}
	}

	protected abstract Object createSpanByFindKey(MatcherInfo matcherInfo);

	private void findKeys(String text)
	{
		mListPattern = getPatternList();
		if (mListPattern != null)
		{
			mListKey.clear();
			for (String patternString : mListPattern)
			{
				if (!TextUtils.isEmpty(patternString))
				{
					List<MatcherInfo> listKey = SDPatternUtil.findMatcherInfo(patternString, text);
					if (listKey != null && !listKey.isEmpty())
					{
						mListKey.addAll(listKey);
					}
				}
			}
		}
	}

	protected abstract List<String> getPatternList();

	public SDSpanBuilder reset()
	{
		mListKey.clear();
		mListSpanInfo.clear();
		mSb.clear();
		mSb.clearSpans();
		return this;
	}

	public SDSpanBuilder appendText(String text)
	{
		mSb.append(text);
		return this;
	}

	/**
	 * 最终添加都要调用这个唯一入口
	 * 
	 * @param key
	 * @param spanInfo
	 */
	private void addSpanInfo(SDSpanInfo spanInfo, boolean isPattern)
	{
		if (spanInfo == null)
		{
		} else
		{
			String key = spanInfo.getMatcherInfo().getKey();

			if (!isPattern)
			{
				mSb.append(key);
			}

			int start = spanInfo.getMatcherInfo().getStart();
			// 验证position
			if (start < 0)
			{
				start = mSb.length() - key.length();
				spanInfo.getMatcherInfo().setStart(start);
			}
			if (spanInfo.getTextView() == null)
			{
				spanInfo.setTextView(mTextView);
			}
			mListSpanInfo.add(spanInfo);
		}
	}

	public SDSpanBuilder addSpan(Object span)
	{
		addSpanInfo(new SDSpanInfo(mTextView, span), false);
		return this;
	}

	public SDSpanInfo getSpanInfo(int position)
	{
		SDSpanInfo spanInfo = null;
		try
		{
			spanInfo = mListSpanInfo.get(position);
		} catch (Exception e)
		{
		}
		return spanInfo;
	}

	public String getPattern(int position)
	{
		String pattern = null;
		try
		{
			pattern = mListPattern.get(position);
		} catch (Exception e)
		{
		}
		return pattern;
	}

	public boolean isContentContainsKey(String key)
	{
		return mSb.toString().contains(key);
	}

	private void insertSpans()
	{
		for (SDSpanInfo spanInfo : mListSpanInfo)
		{
			MatcherInfo matcherInfo = spanInfo.getMatcherInfo();
			mSb.setSpan(spanInfo.getSpan(), matcherInfo.getStart(), matcherInfo.getEnd(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			mSb.setSpan(new SDClickableSpan(spanInfo)
			{
				@Override
				public void onClick(View widget)
				{
					if (mListenerOnClickSpan != null)
					{
						mListenerOnClickSpan.onClick(widget, this.spanInfo);
					}
					super.onClick(widget);
				}
			}, matcherInfo.getStart(), matcherInfo.getEnd(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}

	public SpannableStringBuilder build()
	{
		insertSpans();
		return mSb;
	}

	public void buildTextView(MovementMethod movementMethod)
	{
		mTextView.setText(build());
		if (movementMethod != null)
		{
			mTextView.setMovementMethod(movementMethod);
		}
	}

	public void buildTextView()
	{
		buildTextView(LinkMovementMethod.getInstance());
	}

	public interface OnClickSpanListener
	{
		public void onClick(View v, SDSpanInfo span);
	}

}
