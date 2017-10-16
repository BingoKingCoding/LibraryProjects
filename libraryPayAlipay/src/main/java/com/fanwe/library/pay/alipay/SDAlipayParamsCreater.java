package com.fanwe.library.pay.alipay;

import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

import android.text.TextUtils;

public class SDAlipayParamsCreater
{

	/** 签约合作者身份(PID) */
	public String partner;
	/** 签约卖家支付宝账号 */
	public String seller_id;
	/** 商户私钥，pkcs8格式 */
	public String seller_private_pkcs8;
	/** 商户网站唯一订单号 */
	public String out_trade_no;
	/** 商品名称 */
	public String subject;
	/** 商品详情 */
	public String body;
	/** 商品金额 */
	public String total_fee;
	/** 服务器异步通知页面路径 */
	public String notify_url;
	/** 服务接口名称， 固定值 */
	private String service = "mobile.securitypay.pay";
	/** 支付类型， 固定值 */
	private String payment_type = "1";
	/** 参数编码， 固定值 */
	private String _input_charset = "utf-8";
	/** m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭） */
	public String it_b_pay = "30m";
	/** 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空 */
	public String return_url;

	public String sign_type = "RSA";

	private String wrapperQuotes(String content)
	{
		if (!TextUtils.isEmpty(content))
		{
			content = "\"" + content + "\"";
		}
		return content;
	}

	private String creatOrderInfo()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("partner=").append(wrapperQuotes(partner));
		sb.append("&seller_id=").append(wrapperQuotes(seller_id));
		sb.append("&out_trade_no=").append(wrapperQuotes(out_trade_no));
		sb.append("&subject=").append(wrapperQuotes(subject));
		sb.append("&body=").append(wrapperQuotes(body));
		sb.append("&total_fee=").append(wrapperQuotes(total_fee));
		sb.append("&notify_url=").append(wrapperQuotes(notify_url));
		sb.append("&service=").append(wrapperQuotes(service));
		sb.append("&payment_type=").append(wrapperQuotes(payment_type));
		sb.append("&_input_charset=").append(wrapperQuotes(_input_charset));
		sb.append("&it_b_pay=").append(wrapperQuotes(it_b_pay));

		if (!TextUtils.isEmpty(return_url))
		{
			sb.append("&return_url=").append(wrapperQuotes(return_url));
		}

		return sb.toString();
	}

	public String createPayInfo() throws Exception
	{
		String payInfo = null;
		String orderInfo = creatOrderInfo();
		if (!TextUtils.isEmpty(orderInfo))
		{
			String sign = sign(orderInfo, seller_private_pkcs8);
			sign = URLEncoder.encode(sign, "UTF-8");
			// 完整的符合支付宝参数规范的订单信息
			StringBuilder sb = new StringBuilder();
			sb.append(orderInfo);
			sb.append("&sign=").append(wrapperQuotes(sign));
			sb.append("&sign_type=").append(wrapperQuotes(sign_type));
			payInfo = sb.toString();
		}
		return payInfo;
	}

	public static String sign(String content, String sellerPrivatePkcs8)
	{
		try
		{
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decode(sellerPrivatePkcs8));
			KeyFactory keyf = KeyFactory.getInstance("RSA");
			PrivateKey priKey = keyf.generatePrivate(priPKCS8);
			java.security.Signature signature = java.security.Signature.getInstance("SHA1WithRSA");
			signature.initSign(priKey);
			signature.update(content.getBytes("UTF-8"));
			byte[] signed = signature.sign();
			return Base64.encode(signed);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

}
