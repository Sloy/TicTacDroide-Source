package com.sloy.tictacdroide.components;

import android.graphics.Typeface;

public class FontConfig {

	private Typeface typeface;
	private Integer color;
	private float size;
	private boolean bold;
	
	
	public FontConfig(Typeface typeface, Integer color, float size, boolean bold) {
		this.typeface = typeface;
		this.color = color;
		this.size = size;
		this.bold = bold;
	}

	public Typeface getTypeface() {
		return typeface;
	}


	public Integer getColor() {
		return color;
	}


	public float getSize() {
		return size;
	}


	public boolean isBold() {
		return bold;
	}
	
	public int getStyle(){
		if(isBold()){
			return 1;
		}else{
			return 0;
		}
	}
	
	
	
}
