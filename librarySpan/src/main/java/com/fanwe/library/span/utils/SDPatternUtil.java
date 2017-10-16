package com.fanwe.library.span.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;

import com.fanwe.library.span.model.MatcherInfo;

public class SDPatternUtil
{

	/**
	 * 是否有匹配正则表达式的内容
	 * 
	 * @param strPattern
	 *            正则表达式
	 * @param strContent
	 *            内容
	 * @return
	 */
	public static boolean isMatch(String strPattern, String strContent)
	{
		boolean isMatch = false;
		Matcher matcher = matcher(strPattern, strContent);
		if (matcher != null && matcher.find())
		{
			isMatch = true;
		}
		return isMatch;
	}

	/**
	 * 获取所有匹配正则表达式的字符串
	 * 
	 * @param strPattern
	 *            正则表达式
	 * @param strContent
	 *            内容
	 * @return
	 */
	public static List<String> find(String strPattern, String strContent)
	{
		List<String> listKeys = new ArrayList<String>();

		Matcher matcher = matcher(strPattern, strContent);
		if (matcher != null)
		{
			while (matcher.find())
			{
				String key = matcher.group(0);
				listKeys.add(key);
			}
		}
		return listKeys;
	}

	public static Matcher matcher(String strPattern, String strContent)
	{
		Pattern pattern = Pattern.compile(strPattern);
		Matcher matcher = pattern.matcher(strContent);
		return matcher;
	}

	/**
	 * 找到关键字key在内容中的所有起始位置
	 * 
	 * @param content
	 *            内容
	 * @param key
	 *            关键字
	 * @return
	 */
	public static List<Integer> findPosition(String content, String key)
	{
		List<Integer> listPosition = new ArrayList<Integer>();
		if (content != null && !TextUtils.isEmpty(key))
		{
			int searchIndex = 0;
			int index = content.indexOf(key, searchIndex);
			while (index >= 0)
			{
				listPosition.add(index);
				searchIndex = index + 1;
				index = content.indexOf(key, searchIndex);
			}
		}
		return listPosition;
	}

	/**
	 * 找到关键字key在内容中的所有起始位置
	 * 
	 * @param content
	 *            内容
	 * @param key
	 *            关键字
	 * @return
	 */
	public static LinkedBlockingQueue<Integer> findPositionQueue(String content, String key)
	{
		LinkedBlockingQueue<Integer> listPosition = new LinkedBlockingQueue<Integer>();
		if (content != null && !TextUtils.isEmpty(key))
		{
			int searchIndex = 0;
			int index = content.indexOf(key, searchIndex);
			while (index >= 0)
			{
				listPosition.add(index);
				searchIndex = index + 1;
				index = content.indexOf(key, searchIndex);
			}
		}
		return listPosition;
	}

	/**
	 * 找到内容中所有匹配正则表达式的信息
	 * 
	 * @param strPattern
	 *            正则表达式
	 * @param strContent
	 *            内容
	 * @return
	 */
	public static List<MatcherInfo> findMatcherInfo(final String strPattern, String strContent)
	{
		List<MatcherInfo> listModel = new ArrayList<MatcherInfo>();
		List<String> listKey = find(strPattern, strContent);
		if (listKey != null)
		{
			Integer position = null;
			Map<String, LinkedBlockingQueue<Integer>> mapKeyPositions = new HashMap<String, LinkedBlockingQueue<Integer>>();
			for (String key : listKey)
			{
				LinkedBlockingQueue<Integer> queuePosition = null;
				if (!mapKeyPositions.containsKey(key))
				{
					queuePosition = findPositionQueue(strContent, key);
					mapKeyPositions.put(key, queuePosition);
				} else
				{
					queuePosition = mapKeyPositions.get(key);
				}

				if (queuePosition.size() > 0)
				{
					position = queuePosition.poll();
					if (position != null)
					{
						MatcherInfo matcherInfo = new MatcherInfo();
						matcherInfo.setKey(key);
						matcherInfo.setStart(position);
						matcherInfo.setPattern(strPattern);

						listModel.add(matcherInfo);
					}
				}
			}
		}
		return listModel;
	}
}
