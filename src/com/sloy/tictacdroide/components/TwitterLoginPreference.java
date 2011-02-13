package com.sloy.tictacdroide.components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.sloy.tictacdroide.R;

import twitter4j.ProfileImage;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.io.IOException;
import java.net.MalformedURLException;

public class TwitterLoginPreference extends Preference {
	
	private static final int COMPLETE = 0;
    private static final int FAILED = 1;

	private Drawable mDrawable;
	private ProgressBar mProgressBar;
	private ImageView mImageView;
	public Twitter mTwitter;
	
	@Override
	protected void onBindView(View view) {
		super.onBindView(view);
		if(true)return;
		mProgressBar = (ProgressBar)view.findViewById(R.id.twitter_login_load);
		mImageView = (ImageView)view.findViewById(R.id.twitter_login_pic);
		
		if(Utils.isNetworkAvailable(getContext())){
			if(mTwitter!=null){
				// Logged in
				mImageView.setVisibility(View.GONE);
				mProgressBar.setVisibility(View.VISIBLE);
				new Thread(){
	                public void run() {
                        try {
                        	String url = mTwitter.getProfileImage(mTwitter.getScreenName(), ProfileImage.NORMAL).getURL();
                            mDrawable = Drawable.createFromStream(((java.io.InputStream)new java.net.URL(url).getContent()), "name");
                            imageLoadedHandler.sendEmptyMessage(COMPLETE);
                        } catch (MalformedURLException e) {
                            imageLoadedHandler.sendEmptyMessage(FAILED);
                        } catch (IOException e) {
                            imageLoadedHandler.sendEmptyMessage(FAILED);	
                        } catch (IllegalStateException e) {
                        	imageLoadedHandler.sendEmptyMessage(FAILED);
						} catch (TwitterException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                };
				}.start();
			}else{
				//logged out
				setTitle("Log in");
				setSummary("Conect to your Twitter account");
				mImageView.setImageResource(R.drawable.ic_menu_login);
				mImageView.setVisibility(View.VISIBLE);
				mProgressBar.setVisibility(View.GONE);
			}
		}else{
    		//No hay conexión a Internet
			mImageView.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.GONE);
    		mImageView.setImageResource(R.drawable.ic_menu_x);
    		this.setSummary("FUUU");
    		setEnabled(false);
    	}
	}

	/**
     * Callback that is received once the image has been downloaded
     */
    private final Handler imageLoadedHandler = new Handler(new Callback() {
            @Override
            public boolean handleMessage(Message msg) {
            	
            		mImageView.setVisibility(View.VISIBLE);
                	mProgressBar.setVisibility(View.GONE);
                    switch (msg.what) {
                    case COMPLETE:
                            mImageView.setImageDrawable(mDrawable);
                            break;
                    case FAILED:
                    	
                    default:
                    	mImageView.setImageResource(R.drawable.ic_menu_report_image);
                            // Could change image here to a 'failed' image
                            // otherwise will just keep on spinning
                            break;
                    }
            	
            	return true;
            }              
    });

	public TwitterLoginPreference(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public TwitterLoginPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public TwitterLoginPreference(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

}
