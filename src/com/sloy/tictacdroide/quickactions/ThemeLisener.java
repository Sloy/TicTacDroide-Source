package com.sloy.tictacdroide.quickactions;

import android.view.View;
import android.view.View.OnClickListener;

import com.sloy.tictacdroide.components.Theme;

public abstract class ThemeLisener implements OnClickListener {

	Theme theme;
	
	public Theme getTheme() {
		return theme;
	}

	public void setTheme(Theme mTheme) {
		this.theme = mTheme;
	}

	@Override
	public abstract void onClick(View v);
	
	

}
