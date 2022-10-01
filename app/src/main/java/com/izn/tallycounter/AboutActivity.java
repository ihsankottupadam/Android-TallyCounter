package  com.izn.tallycounter;

import android.app.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.content.*;
import android.graphics.*;
import android.media.*;
import android.net.*;
import android.text.*;
import android.util.*;
import android.webkit.*;
import java.util.*;
import java.text.*;

public class AboutActivity extends Activity 
{
	private ImageView image_back;
	private TextView textDeveloper;
	private Intent intent_view = new Intent();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_view);
		initialize();
		
		
	}
	private void  initialize() {
		textDeveloper = (TextView) findViewById(R.id.textview5);
		image_back = (ImageView) findViewById(R.id.image_back);
		image_back.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View _v) { 
					intent_view.setClass(getApplicationContext(), MainActivity.class);
					intent_view.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent_view);
				}
			});
			
		textDeveloper.setText("Ihsan Kottupadam");
	}
}
