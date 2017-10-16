package com.fanwe.library.span.model;

public class SpanInfo
{

	private Object span;
	private MatcherInfo matcherInfo = new MatcherInfo();

	public SpanInfo(Object span)
	{
		super();
		this.span = span;
	}

	public Object getSpan()
	{
		return span;
	}

	public void setSpan(Object span)
	{
		this.span = span;
	}

	public MatcherInfo getMatcherInfo()
	{
		return matcherInfo;
	}

	public void setMatcherInfo(MatcherInfo matcherInfo)
	{
		this.matcherInfo = matcherInfo;
	}

}
