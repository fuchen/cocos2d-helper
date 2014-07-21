package me.fuchen.ccutil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Environment;

public class GameButtons {
	Activity activity;
	String appName;
	
	private static GameButtons instance;
	
	public static void onCreate(Activity activity, String appName) {
		activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		instance = new GameButtons(activity, appName);
	}
	
	public static void onDestroy() {
		instance = null;
	}
	
	public static void share(final String msg, final String imageFile) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (instance != null)
					instance.shareWorker(msg, imageFile);
			}
		});
	}
	
	public static void rate() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (instance != null)
					instance.rateWorker();
			}
		});
	}
	
	private static void runOnUiThread(Runnable action) {
		if (instance == null)
			return;		
		instance.activity.runOnUiThread(action);
	}
	
	
	private GameButtons(Activity activity, String appName) {
		this.activity = activity;
		this.appName = appName;
	}
	
	private void copyFile(File src, File dst) throws IOException {
	    InputStream in = new FileInputStream(src);
	    OutputStream out = new FileOutputStream(dst);

	    // Transfer bytes from in to out
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = in.read(buf)) > 0) {
	        out.write(buf, 0, len);
	    }
	    in.close();
	    out.close();
	}
	
	private String copyImageToPublicPath(String filename) {
		String imageFile = null;
		
		File src = new File(filename);
		File dstDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), appName);
		File dst = new File(dstDir, "score.jpg");
		
		if (dstDir.exists() || dstDir.mkdirs()) {
			try {
				copyFile(src, dst);
				imageFile = dst.getPath();
			} catch (IOException e) {
			}
		}
		
		return imageFile;
	}

	private void shareWorker(String msg, String filename) {
		String imageFile = null;
		if (!filename.isEmpty())
			imageFile = copyImageToPublicPath(filename);
		
		msg += " http://play.google.com/store/apps/details?id=" + activity.getPackageName();
		
		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
		
		if (imageFile != null) {
			File file = new File(imageFile);			
			sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
			sharingIntent.setType("image/*");
			sharingIntent.putExtra(Intent.EXTRA_TEXT, msg);
		} else {
			sharingIntent.putExtra(Intent.EXTRA_TEXT, msg);
			sharingIntent.setType("text/plain");
		}
		
		activity.startActivity(sharingIntent);
	}
	
	private void rateWorker() {
		String packageName = activity.getPackageName();
		Uri uri = Uri.parse("market://details?id=" + packageName);
		Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
		try {
			activity.startActivity(goToMarket);
		} catch (ActivityNotFoundException e) {
			activity.startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://play.google.com/store/apps/details?id=" + packageName)));
		}
	}
}
