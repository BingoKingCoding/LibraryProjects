package com.fanwe.library.span;

import android.text.style.ClickableSpan;
import android.view.View;

import com.fanwe.library.span.model.SDSpanInfo;

public class SDClickableSpan extends ClickableSpan
{

	protected SDSpanInfo spanInfo;

	public SDClickableSpan(SDSpanInfo spanInfo)
	{
		super();
		this.spanInfo = spanInfo;
	}

	@Override
	public void onClick(View widget)
	{

	}

}
