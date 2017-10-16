package com.fanwe.library.pay.alipay;

import android.app.Activity;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;

public class SDAlipayer
{
	private Activity mActivity;

	public SDAlipayer(Activity activity)
	{
		this.mActivity = activity;
	}

	// ---------------gettter setter
	private SDAlipayerListener mListener;

	public SDAlipayerListener getListener()
	{
		return mListener;
	}

	public void setListener(SDAlipayerListener listener)
	{
		this.mListener = listener;
	}

	public void pay(String orderSpec, String sign)
	{
		pay(orderSpec, sign, "RSA");
	}

	public void pay(String orderSpec, String sign, String signType)
	{
		if (TextUtils.isEmpty(orderSpec))
		{
			notifyFailure(null, "order_spec为空");
			return;
		}
		if (TextUtils.isEmpty(sign))
		{
			notifyFailure(null, "sign为空");
			return;
		}
		if (TextUtils.isEmpty(signType))
		{
			notifyFailure(null, "signType为空");
			return;
		}
		String info = orderSpec + "&sign=" + "\"" + sign + "\"" + "&" + "sign_type=" + "\"" + signType + "\"";
		pay(info);
	}

	public void pay(final String payInfo)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					PayTask alipay = new PayTask(mActivity);
					String result = alipay.pay(payInfo, true);
					notifyResult(new PayResult(result));
				} catch (Exception e)
				{
					notifyFailure(e, null);
				}
			}
		}).start();
	}

	// ------------------------notify

	private void notifyFailure(Exception e, String msg)
	{
		if (mListener != null)
		{
			mListener.onFailure(e, msg);
		}
	}

	private void notifyResult(PayResult result)
	{
		if (mListener != null)
		{
			mListener.onResult(result);
		}
	}

	public interface SDAlipayerListener
	{
		public void onFailure(Exception e, String msg);

		public void onResult(PayResult result);
	}

}
