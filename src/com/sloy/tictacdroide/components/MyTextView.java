package com.sloy.tictacdroide.components;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class MyTextView extends TextView {

	public MyTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public MyTextView(Context context) {
        super(context);
        init(context);
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    private void init(Context context){
    	FontConfig config = ThemeManager.getFontConfig();
    	
        Typeface tf = null;
    	tf = config.getTypeface();
    	if(tf==null){
            tf = Typeface.createFromAsset(context.getAssets(), "themefont.ttf");
    	}
        this.setTypeface(tf,config.getStyle());
        
        /* Color */
        int color = config.getColor();
        if(color!=0){
        	setTextColor(color);
        }
        
        /* Tamaño */
        float size = config.getSize();
        float actual = this.getTextSize();
        if(actual!=14f){
        	//size *= actual/14;
        	size = actual;
        }
        setTextSize(size);
    }
}