package com.yingshi.toutiao.view;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.yingshi.toutiao.R;
import com.yingshi.toutiao.TouTiaoApp;
import com.yingshi.toutiao.actions.ParallelTask;
import com.yingshi.toutiao.util.PhotoUtil;
import com.yingshi.toutiao.util.Utils;

public class CustomizeImageView extends ImageView{
    private static final String tag = "TT-CustomizeImageView";
    private static String IMAGE_CACHE_PATH = null;

    public static interface LoadImageCallback{
    	void onImageLoaded(Drawable drawable);
    }
    
    public CustomizeImageView(Context context) {
        super(context);
        if(IMAGE_CACHE_PATH == null){
        	IMAGE_CACHE_PATH = ((TouTiaoApp)context.getApplicationContext()).getCachePath()  + "/images/";
        }
    }
    
    public CustomizeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(IMAGE_CACHE_PATH == null){
        	IMAGE_CACHE_PATH = ((TouTiaoApp)context.getApplicationContext()).getCachePath()  + "/images/";
        }
    }

    public void loadImage(final String url){
    	loadImage(url, null);
    }

    public void loadImageToSrc(final String url){
    	loadImage(url, null, false );
    }
    
    public void loadImage(final String url, final LoadImageCallback mLoadImageCallback){
    	loadImage(url, mLoadImageCallback, true );
    }//list_item_thumbnail
    
    public void loadRoundImage(final String url, int pixes){
    	loadImage(url, null, true, pixes);
    }
    
    public void loadRoundImageWithDefault(final String url, int resId, int pixes){
    	loadImage(url, resId, null, true, pixes);
    }
    
    private void loadImage(final String url, final LoadImageCallback mLoadImageCallback, final boolean background){
    	loadImage(url, mLoadImageCallback, background, 0 );
    }
    
    private void loadImage(final String url, final LoadImageCallback mLoadImageCallback, final boolean background, final int cornerPixes){
    	loadImage(url, 0, mLoadImageCallback, background, 0 );
    }
    
    private void loadImage(final String url, final int defaultRes, final LoadImageCallback mLoadImageCallback, final boolean background, final int cornerPixes){
    	if(TextUtils.isEmpty(url))
    		return;
        new ParallelTask<Drawable>(){
            @SuppressWarnings("deprecation")
			protected Drawable doInBackground(Void... params) {
                try {
                    String cachedFileDir = IMAGE_CACHE_PATH + url.hashCode();
                    File existingFile = new File(cachedFileDir);
                    if(existingFile.exists()){
                        if(cornerPixes > 0){
                        	Bitmap roundBitmap = toRoundCorner(BitmapFactory.decodeFile(cachedFileDir), cornerPixes);
                        	return new BitmapDrawable(roundBitmap);
                        }
                    	return Drawable.createFromPath(cachedFileDir);
                    }
                    Log.d(tag, "Loading img : " + url);
                    InputStream is = (InputStream) new URL(url).getContent();
                    Utils.saveDataToFile(is, cachedFileDir);
                    if(cornerPixes > 0){
                    	Bitmap roundBitmap = toRoundCorner(BitmapFactory.decodeFile(cachedFileDir), cornerPixes);
                    	return new BitmapDrawable(roundBitmap);
                    }
                    return Drawable.createFromPath(cachedFileDir);
                } catch (Exception e) {
                	Log.e(tag, "Failed to load image : " + url +". Error: " + e.getMessage());
                	if(defaultRes > 0){
                        if(cornerPixes > 0){
                        	Bitmap roundBitmap = toRoundCorner(BitmapFactory.decodeResource(getContext().getResources(), defaultRes), cornerPixes);
                        	return new BitmapDrawable(roundBitmap);
                        }
                        return new BitmapDrawable(BitmapFactory.decodeResource(getContext().getResources(), defaultRes));
                	}
                	return null;
                }
            }
            @SuppressWarnings("deprecation")
			protected void onPostExecute(Drawable drawable){
                if(drawable != null){
                	if(background)
                		setBackgroundDrawable(drawable);
                	else
                		setImageDrawable(drawable);
                }
                if(mLoadImageCallback != null){
                	mLoadImageCallback.onImageLoaded(drawable);
                }
            }
        }.execute();
    }
    
    
    
    public static String getCachedImagePath(String url){
        String cachedFileDir = IMAGE_CACHE_PATH + url.hashCode();
        File existingFile = new File(cachedFileDir);
        if(existingFile.exists()){
        	return cachedFileDir;
        }else{
        	return null;
        }
    }
    
    public void loadImage(final InputStream is){
    	loadImage(is, null);
    }
    
    public void loadImage(final byte[] data){
    	if(data == null || data.length == 0)
    		return;
        new ParallelTask<Drawable>(){
            protected Drawable doInBackground(Void... params) {
            	return PhotoUtil.bytes2Drawable(data);
            }
            @SuppressWarnings("deprecation")
			protected void onPostExecute(Drawable drawable){
                if(drawable != null){
                	setBackgroundDrawable(drawable);
              }
            }
        }.execute();
    }
    
    public void loadImage(final InputStream is, final LoadImageCallback callback){
        new AsyncTask<Void, Void, Drawable>(){
            protected Drawable doInBackground(Void... params) {
                try {
                    return Drawable.createFromStream(is, "src name");
                } catch (Exception e) {
                    return null;
                }
            }
            @SuppressWarnings("deprecation")
			protected void onPostExecute(Drawable drawable){
                if(drawable != null){
//                    setImageDrawable(drawable);
                    setBackgroundDrawable(drawable);
//                    setBackground(drawable);
                }
                if(callback != null)
                	callback.onImageLoaded(drawable);
            }
        }.execute();
    }
    
    /**
     * 获取圆角位图的方法
     * @param bitmap 需要转化成圆角的位图
     * @param pixels 圆角的度数，数值越大，圆角越大
     * @return 处理后的圆角位图
     */
    private static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
    	Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
    			bitmap.getHeight(), Config.ARGB_8888);
    	Canvas canvas = new Canvas(output);
    	final int color = 0xff424242;
    	final Paint paint = new Paint();
    	final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
    	final RectF rectF = new RectF(rect);
    	final float roundPx = pixels;
    	paint.setAntiAlias(true);
    	canvas.drawARGB(0, 0, 0, 0);
    	paint.setColor(color);
    	canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
    	paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
    	canvas.drawBitmap(bitmap, rect, rect, paint);
    	return output;
    }
}
