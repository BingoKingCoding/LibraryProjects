package com.fanwe.library.span.builder;

import java.util.HashMap;
import java.util.Map;

import android.widget.TextView;

import com.fanwe.library.span.model.MatcherInfo;

public abstract class SDMapDataSpanBuilder<T> extends SDSpanBuilder
{
	protected Map<String, T> mMapData = new HashMap<String, T>();

	public SDMapDataSpanBuilder(TextView textView)
	{
		super(textView);
	}

	public void setMapData(Map<String, T> mapData)
	{
		this.mMapData = mapData;
	}

	private T getData(String key)
	{
		T value = null;
		if (mMapData != null)
		{
			value = mMapData.get(key);
		}
		return value;
	}

	@Override
	protected Object createSpanByFindKey(MatcherInfo matcherInfo)
	{
		T data = getData(matcherInfo.getKey());
		return createSpanByFindKey(matcherInfo, data);
	}

	protected abstract Object createSpanByFindKey(MatcherInfo matcherInfo, T data);

}
