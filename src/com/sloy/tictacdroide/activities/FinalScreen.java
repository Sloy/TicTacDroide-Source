package com.sloy.tictacdroide.activities;

import com.sloy.tictacinterface.R;

import android.app.Activity;
import android.os.Bundle;

public class FinalScreen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.final_screen);
        setTitle("Fin de partida");
	}

}
