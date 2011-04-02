package com.sloy.tictacdroide.components;

import android.util.Log;

public class MyLog {

	public static final String TAG = "TicTacDroide";
	private static final boolean active = true;

	public static void d(Object msg) {
		if (active)
			Log.d(TAG, msg.toString());
	}

	public static void e(Object ctx, Exception e) {
		if (active)
			Log.e(TAG, ctx.getClass().getName() + ": " + e.getMessage());
	}

	public static void e(Exception e) {
		if (active)
			Log.e(TAG, e.getMessage());
	}
	
	public static void e(Object e) {
		if (active)
			Log.e(TAG, e.toString());
	}

}
