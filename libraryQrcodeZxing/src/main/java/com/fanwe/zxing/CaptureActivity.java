package com.fanwe.zxing;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.LinearLayout;

import com.fanwe.library.qrcode.zxing.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Initial the camera
 *
 * @author Ryan.Tang
 */
public class CaptureActivity extends Activity implements Callback
{

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    protected LinearLayout ll_all;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_capture);
        initialize();
        init();
    }

    private void initialize()
    {
        CameraManager.init(getApplication());
        viewfinderView = (ViewfinderView) findViewById(R.id.act_capture_vv_viewfinder_view);
        ll_all = (LinearLayout) findViewById(R.id.act_capture_ll_content);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }

    protected void init()
    {

    }

    public void setLayoutId(int layoutId)
    {
        ll_all.removeAllViews();
        getLayoutInflater().inflate(layoutId, ll_all, true);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.act_capture_sv_preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface)
        {
            initCamera(surfaceHolder);
        } else
        {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;
    }

    private void notifyFinishScanning()
    {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                onFinishScanning();
            }
        });
    }

    private void notifyFailureScanning()
    {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                onFailureScanning();
            }
        });
    }

    private void notifySuccessScanning(final Result result)
    {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                onSuccessScanning(result);
            }
        });
    }

    private void notifyStartScanning()
    {
        runOnUiThread(new Runnable()
        {

            @Override
            public void run()
            {
                onStartScanning();
            }
        });
    }

    protected void onStartScanning()
    {

    }

    protected void onSuccessScanning(Result result)
    {

    }

    protected void onFailureScanning()
    {

    }

    protected void onFinishScanning()
    {

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (handler != null)
        {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy()
    {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * Handler scan result
     *
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode)
    {
        inactivityTimer.onActivity();
        notifySuccessScanning(result);
    }

    private void initCamera(SurfaceHolder surfaceHolder)
    {
        try
        {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe)
        {
            return;
        } catch (RuntimeException e)
        {
            return;
        }
        if (handler == null)
        {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        if (!hasSurface)
        {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        hasSurface = false;
    }

    public ViewfinderView getViewfinderView()
    {
        return viewfinderView;
    }

    public Handler getHandler()
    {
        return handler;
    }

    public void drawViewfinder()
    {
        viewfinderView.drawViewfinder();

    }

    /**
     * 异步扫描二维码方法，会回调处理方法
     *
     * @param path
     */
    public void scanningImageAsync(final String path)
    {
        new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                try
                {
                    notifyStartScanning();
                    Result result = scanningImage(path);
                    if (result != null)
                    {
                        notifySuccessScanning(result);
                    } else
                    {
                        notifyFailureScanning();
                    }
                } catch (Exception e)
                {
                    notifyFailureScanning();
                } finally
                {
                    notifyFinishScanning();
                }
            }
        }).start();
    }

    public Result scanningImage(String path)
    {
        return scanningImage(createBitmapFrom(path));
    }

    public Result scanningImage(Bitmap bitmap)
    {
        if (bitmap == null)
        {
            return null;
        }
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8"); // 设置二维码内容的编码

        RGBLuminanceSource source = new RGBLuminanceSource(bitmap);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try
        {
            return reader.decode(bitmap1, hints);
        } catch (NotFoundException e)
        {
            e.printStackTrace();
        } catch (ChecksumException e)
        {
            e.printStackTrace();
        } catch (FormatException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public Bitmap createBitmapFrom(String path)
    {
        Bitmap bitmap = null;
        if (!TextUtils.isEmpty(path))
        {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            int sampleSize = (int) (options.outHeight / (float) 200);
            if (sampleSize <= 0)
            {
                sampleSize = 1;
            }
            options.inSampleSize = sampleSize;
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(path, options);
        }
        return bitmap;
    }

}

final class RGBLuminanceSource extends LuminanceSource
{

    private final byte[] luminances;

    public RGBLuminanceSource(String path) throws FileNotFoundException
    {
        this(loadBitmap(path));
    }

    public RGBLuminanceSource(Bitmap bitmap)
    {
        super(bitmap.getWidth(), bitmap.getHeight());

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        // In order to measure pure decoding speed, we convert the entire image
        // to a greyscale array
        // up front, which is the same as the Y channel of the
        // YUVLuminanceSource in the real app.
        luminances = new byte[width * height];
        for (int y = 0; y < height; y++)
        {
            int offset = y * width;
            for (int x = 0; x < width; x++)
            {
                int pixel = pixels[offset + x];
                int r = (pixel >> 16) & 0xff;
                int g = (pixel >> 8) & 0xff;
                int b = pixel & 0xff;
                if (r == g && g == b)
                {
                    // Image is already greyscale, so pick any channel.
                    luminances[offset + x] = (byte) r;
                } else
                {
                    // Calculate luminance cheaply, favoring green.
                    luminances[offset + x] = (byte) ((r + g + g + b) >> 2);
                }
            }
        }
    }

    @Override
    public byte[] getRow(int y, byte[] row)
    {
        if (y < 0 || y >= getHeight())
        {
            throw new IllegalArgumentException("Requested row is outside the image: " + y);
        }
        int width = getWidth();
        if (row == null || row.length < width)
        {
            row = new byte[width];
        }

        System.arraycopy(luminances, y * width, row, 0, width);
        return row;
    }

    // Since this class does not support cropping, the underlying byte array
    // already contains
    // exactly what the caller is asking for, so give it to them without a copy.
    @Override
    public byte[] getMatrix()
    {
        return luminances;
    }

    private static Bitmap loadBitmap(String path) throws FileNotFoundException
    {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        if (bitmap == null)
        {
            throw new FileNotFoundException("Couldn't open " + path);
        }
        return bitmap;
    }

}
