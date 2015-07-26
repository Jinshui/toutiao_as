package com.yingshi.toutiao.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
public class PhotoUtil {
	public static Bitmap resId2Bitmap(int resId, Context context) {
		Resources res = context.getResources();
		return BitmapFactory.decodeResource(res, resId);
	}

	// 将byte[]转换成InputStream
	public static InputStream byte2InputStream(byte[] b) {
		ByteArrayInputStream bais = new ByteArrayInputStream(b);
		return bais;
	}

	// 将InputStream转换成byte[]
	public static byte[] inputStream2Bytes(InputStream is) {
		String str = "";
		byte[] readByte = new byte[1024];
		int readCount = -1;
		try {
			while ((readCount = is.read(readByte, 0, 1024)) != -1) {
				str += new String(readByte).trim();
			}
			return str.getBytes();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 将Bitmap转换成InputStream
	public static InputStream bitmap2InputStream(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		InputStream is = new ByteArrayInputStream(baos.toByteArray());
		return is;
	}

	// 将Bitmap转换成InputStream
	public static InputStream bitmap2InputStream(Bitmap bm, int quality) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, quality, baos);
		InputStream is = new ByteArrayInputStream(baos.toByteArray());
		return is;
	}

	// 将InputStream转换成Bitmap
	public static Bitmap inputStream2Bitmap(InputStream is) {
		return BitmapFactory.decodeStream(is);
	}

	// Drawable转换成InputStream
	public static InputStream drawable2InputStream(Drawable d) {
		Bitmap bitmap = drawable2Bitmap(d);
		return bitmap2InputStream(bitmap);
	}

	// InputStream转换成Drawable
	public static Drawable inputStream2Drawable(InputStream is) {
		Bitmap bitmap = inputStream2Bitmap(is);
		return bitmap2Drawable(bitmap);
	}

	// Drawable转换成byte[]
	public static byte[] drawable2Bytes(Drawable d) {
		Bitmap bitmap = drawable2Bitmap(d);
		return bitmap2Bytes(bitmap);
	}

	// byte[]转换成Drawable
	public static Drawable bytes2Drawable(byte[] b) {
		Bitmap bitmap = bytes2Bitmap(b);
		return bitmap2Drawable(bitmap);
	}

	// Bitmap转换成byte[]
	public static byte[] bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	// byte[]转换成Bitmap
	public static Bitmap bytes2Bitmap(byte[] b) {
		if (b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		}
		return null;
	}

	// Drawable转换成Bitmap
	public static Bitmap drawable2Bitmap(Drawable drawable) {
		Bitmap bitmap = Bitmap
				.createBitmap(
						drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight(),
						drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
								: Bitmap.Config.RGB_565);
		return bitmap;
	}

	// Bitmap转换成Drawable
	public static Drawable bitmap2Drawable(Bitmap bitmap) {
		BitmapDrawable bd = new BitmapDrawable(bitmap);
		Drawable d = (Drawable) bd;
		return d;
	}
}