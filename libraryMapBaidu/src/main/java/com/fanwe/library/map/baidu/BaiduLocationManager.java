package com.fanwe.library.map.baidu;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 百度定位管理类
 */
public class BaiduLocationManager implements BDLocationListener
{
    public static final String TAG = "BaiduLocationManager";

    private static BaiduLocationManager sInstance;

    private boolean mIsInit = false;
    private LocationClient mLocationClient;

    private BDLocation mBDLocation;
    private int[] mArrSuccessLocationType = new int[]{BDLocation.TypeGpsLocation, BDLocation.TypeNetWorkLocation, BDLocation.TypeOffLineLocation};
    private LocationClientOption mLocationOption;

    private Map<LocationListener, LocationConfig> mMapLocationListener = new WeakHashMap<>();

    public static BaiduLocationManager getInstance()
    {
        if (sInstance == null)
        {
            synchronized (BaiduLocationManager.class)
            {
                if (sInstance == null)
                {
                    sInstance = new BaiduLocationManager();
                }
            }
        }
        return sInstance;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public synchronized void init(Context context)
    {
        if (mIsInit)
        {
            return;
        }

        context = context.getApplicationContext();

        SDKInitializer.initialize(context);
        mLocationClient = new LocationClient(context);
        mLocationClient.registerLocationListener(this);

        mIsInit = true;
    }

    /**
     * 获得默认定位配置
     *
     * @return
     */
    public LocationClientOption newLocationOption()
    {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy); //高精度
        option.setOpenGps(true); //打开gps
        option.setIsNeedAddress(true); //返回地址信息
        option.setScanSpan(3 * 1000); //定位间隔
        option.setNeedDeviceDirect(true);
        option.setCoorType("bd09ll"); //坐标类型
        option.SetIgnoreCacheException(true); //不收集CRASH信息
        return option;
    }

    /**
     * 获得定位地址
     *
     * @return
     */
    public String getAddress()
    {
        if (mBDLocation != null)
        {
            return mBDLocation.getAddrStr();
        } else
        {
            return null;
        }
    }

    /**
     * 获得定位城市
     *
     * @return
     */
    public String getCity()
    {
        if (mBDLocation != null)
        {
            return mBDLocation.getCity();
        } else
        {
            return null;
        }
    }

    public String getCityShort()
    {
        String city = getCity();
        if (!TextUtils.isEmpty(city) && city.contains("市"))
        {
            city = city.replace("市", "");
        }
        return city;
    }

    /**
     * 获得定位区域
     *
     * @return
     */
    public String getDistrict()
    {
        if (mBDLocation != null)
        {
            return mBDLocation.getDistrict();
        } else
        {
            return null;
        }
    }

    public String getDistrictShort()
    {
        String district = getDistrict();
        if (!TextUtils.isEmpty(district))
        {
            if (district.length() >= 3)
            {
                if (district.endsWith("区") || district.endsWith("市") || district.endsWith("县"))
                {
                    district = district.substring(0, district.length() - 1);
                }
            }
        }
        return district;
    }

    /**
     * 是否定位成功过
     *
     * @return
     */
    public boolean hasLocationSuccess()
    {
        return hasLocationSuccess(mBDLocation);
    }

    /**
     * location是否定位成功
     *
     * @param location
     * @return
     */
    public boolean hasLocationSuccess(BDLocation location)
    {
        if (location == null)
        {
            return false;
        }

        int type = location.getLocType();
        for (int typeSuccess : mArrSuccessLocationType)
        {
            if (type == typeSuccess)
            {
                return true;
            }
        }
        return false;
    }

    public BDLocation getLocation()
    {
        return mBDLocation;
    }

    /**
     * 纬度(ypoint)
     *
     * @return
     */
    public double getLatitude()
    {
        if (mBDLocation != null)
        {
            return mBDLocation.getLatitude();
        }
        return 0;
    }

    /**
     * 经度(xpoint)
     *
     * @return
     */
    public double getLongitude()
    {
        if (mBDLocation != null)
        {
            return mBDLocation.getLongitude();
        }
        return 0;
    }

    /**
     * 获得当前位置的LatLng
     *
     * @return
     */
    public LatLng getLatLngCurrent()
    {
        LatLng ll = null;
        double lat = getLatitude();
        double lon = getLongitude();
        if (lat != 0 && lon != 0)
        {
            ll = new LatLng(lat, lon);
        }
        return ll;
    }

    /**
     * 获得两点之前的距离
     *
     * @param llStart
     * @param llEnd
     * @return
     */
    public double getDistance(LatLng llStart, LatLng llEnd)
    {
        if (llStart != null && llEnd != null)
        {
            return DistanceUtil.getDistance(llStart, llEnd);
        } else
        {
            return 0;
        }
    }

    /**
     * 获得当前位置到该点的距离
     *
     * @param llEnd
     * @return
     */
    public double getDistanceFrom(LatLng llEnd)
    {
        return getDistance(getLatLngCurrent(), llEnd);
    }

    /**
     * 获得当前位置和传进来的经纬度间的距离
     *
     * @param latitude  纬度(Ypoint)
     * @param longitude 经度(Xpoint)
     * @return
     */
    public double getDistanceFrom(double latitude, double longitude)
    {
        return getDistance(getLatLngCurrent(), new LatLng(latitude, longitude));
    }

    /**
     * 开始定位
     *
     * @param listener 定位监听(应该在界面销毁的时候置为null)
     */
    public void startLocation(LocationListener listener)
    {
        startLocation(false, null, listener);
    }

    /**
     * 开始定位
     *
     * @param listenAllTheTime 是否持续监听
     * @param listener         定位监听(应该在界面销毁的时候置为null)
     */
    public void startLocation(boolean listenAllTheTime, LocationListener listener)
    {
        startLocation(listenAllTheTime, null, listener);
    }

    /**
     * 开始定位
     *
     * @param listenAllTheTime 是否持续监听
     * @param option           定位参数
     * @param listener         定位监听(应该在界面销毁的时候置为null)
     */
    public void startLocation(boolean listenAllTheTime, LocationClientOption option, LocationListener listener)
    {
        if (listener == null)
        {
            return;
        }

        synchronized (mMapLocationListener)
        {
            LocationConfig config = new LocationConfig();
            config.listenAllTheTime = listenAllTheTime;

            mMapLocationListener.put(listener, config);

            if (option == null)
            {
                option = newLocationOption();
            }
            mLocationClient.setLocOption(option);
            mLocationClient.start();
        }
    }

    /**
     * 结束定位
     */
    private void stopLocation()
    {
        mLocationClient.stop();
        Log.i(TAG, "stopLocation isLocationStarted:" + isLocationStarted());
    }

    /**
     * 定位是否已经被启动
     *
     * @return
     */
    public boolean isLocationStarted()
    {
        return mLocationClient.isStarted();
    }

    @Override
    public void onReceiveLocation(BDLocation location)
    {
        Log.i(TAG, "onReceiveLocation isLocationStarted:" + isLocationStarted());

        synchronized (mMapLocationListener)
        {
            boolean success = false;
            if (hasLocationSuccess(location))
            {
                mBDLocation = location;
                success = true;
            }

            if (!mMapLocationListener.isEmpty())
            {
                Iterator<Map.Entry<LocationListener, LocationConfig>> iterator = mMapLocationListener.entrySet().iterator();
                while (iterator.hasNext())
                {
                    Map.Entry<LocationListener, LocationConfig> item = iterator.next();

                    LocationListener listener = item.getKey();
                    LocationConfig config = item.getValue();

                    listener.onResult(mBDLocation, success);
                    if (!config.listenAllTheTime)
                    {
                        iterator.remove();
                    }
                }
            }

            if (mMapLocationListener.isEmpty())
            {
                stopLocation();
            }
        }
    }

    @Override
    public void onConnectHotSpotMessage(String s, int i)
    {

    }

    private class LocationConfig
    {
        /**
         * 是否持续监听
         */
        public boolean listenAllTheTime;
    }

    public interface LocationListener
    {
        void onResult(BDLocation location, boolean success);
    }

}
