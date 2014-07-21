package me.fuchen.ccutil;

import android.app.Activity;

import com.google.android.gms.ads.*;

public class AdMobHelper {
	static AdMobHelper instance;
	
	public static void onCreate(Activity activity, String bannerId, String fullscreenId) {
		instance = new AdMobHelper(activity, bannerId, fullscreenId);
	}
	
	public static void onDestroy() {
		instance = null;
	}
	
	public static void displayFullscreenAd() {		
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (instance != null)
					instance.displayFullscreenAdWorker();
			}
		});		
	}
	
	private static void runOnUiThread(Runnable action) {
		if (instance == null)
			return;		
		instance.activity.runOnUiThread(action);
	}	
	
	Activity activity;
    InterstitialAd fullscreenAd;
    
	String bannerAdId;
	String fullscreenAdId;
	
	AdMobHelper(Activity activity, String bannerId, String fullscreenId) {
		this.activity = activity;
		this.bannerAdId = bannerId;
		this.fullscreenAdId = fullscreenId;
		
		createInterstitialAd();
	}
	
	void createInterstitialAd() {
        // Create the interstitial.
		fullscreenAd = new InterstitialAd(activity);
		fullscreenAd.setAdUnitId(fullscreenAdId);
		fullscreenAd.setAdListener(new MyAdListener());

        // Create ad request.
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("4F53F53355F4B43C118B5C357EBBBCDF") // My MI2 phone
                .build();

        // Begin loading your interstitial.
        fullscreenAd.loadAd(adRequest);

    }
	
	boolean fullscreenAdIsReady() {
		return fullscreenAd != null && fullscreenAd.isLoaded();
	}
	
	void displayFullscreenAdWorker() {
        if (fullscreenAdIsReady()) {
        	fullscreenAd.show();
        }
    }
	
	class MyAdListener extends com.google.android.gms.ads.AdListener {
        public void onAdFailedToLoad(int errorCode) {
            createInterstitialAd();
        }

        public void onAdClosed() {
            createInterstitialAd();
        }
    }
}
