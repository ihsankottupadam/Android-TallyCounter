package  com.izn.tallycounter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import
android.view.View.OnClickListener;
import
android.view.View.OnTouchListener;
import android.view.WindowManager;
import
android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.Toast;
import android.widget.*;
import android.view.*;
import android.view.View.*;
import android.graphics.*;
import android.app.*;
import android.support.v4.app.*;
import android.content.*;
import android.preference.*;
import android.media.*;
import android.os.*;
public class MyService
extends Service implements
OnTouchListener, OnClickListener {
	private View topLeftView;
	private View overlayedButton;
	private LinearLayout li;
	private ImageButton btnopen;
	private LayoutInflater inflate;
	public TextView dis;
	private float offsetX;
	private float offsetY;
	private long startClickTime;
	private int originalXPos;
	private int originalYPos;
	private boolean moving;
	private boolean canMove=true;
	private WindowManager wm;
	private SharedPreferences prefs;
	private MediaPlayer mp;
	private MediaPlayer ma;
	private Vibrator vibrater;

	private static final String prefs_PREF="prefs";
	private static final String IS_VIB="isvibrate";
	private static final String IS_VOL="volbtn";
	private static final String IS_BEEP="isbeep";
	private static final String IS_ALERT="isAlert";
	private static final String TARGET="target";
	private static final String L_COUNT="count";
	private static final String CLOSE_ACTION="com.Ihsan.TallyCounter.Close";
	private static final String RESET_ACTION="com.Ihsan.TallyCounter.Reset";

	private static int target;
	private static boolean isvibrate;
	private static boolean isbeep;
	private static boolean volbtn;
	private static boolean isAlert;
	public static int Count=0;
	private int val=0;
	@Override
	public IBinder onBind (Intent intent ) {
		return null;
	}
	@Override
	public void onCreate () {
		super.onCreate();
		Intent intentClose = new Intent();
		intentClose.setAction(CLOSE_ACTION);
		PendingIntent pendingIntentClose =PendingIntent .getBroadcast( this , 12345, intentClose, PendingIntent .FLAG_UPDATE_CURRENT);
		Intent intentReset = new Intent();
		intentReset.setAction(RESET_ACTION);
		PendingIntent pendingIntentReset =PendingIntent .getBroadcast(this , 0 , intentReset,PendingIntent .FLAG_UPDATE_CURRENT );
		Intent notificationIntent = new Intent(this,MainActivity. class ); 
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Bitmap micon=BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher);
		PendingIntent pendingIntent =
			PendingIntent .getActivity( this , 0 , notificationIntent, 0 );
		    Notification notification = new
			NotificationCompat. Builder (this)
			.setContentTitle( "Tally Counter")
			.setContentIntent(pendingIntent)
			.setOngoing( true)
		    .setPriority(NotificationCompat. PRIORITY_HIGH )
			.setSmallIcon(R.drawable.ic_noti_small)
			.setLargeIcon(micon)
			.setLights(0xFFFF0000,3000,300)
			.addAction(R.drawable.ic_reset, "RESET", pendingIntentReset)
			.addAction(R.drawable.ic_close_white, "CLOSE", pendingIntentClose)
			.build();
		startForeground(300 , notification);
		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		vibrater = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		Count=prefs.getInt(L_COUNT,0);
		target=prefs.getInt(TARGET,0);
		isvibrate = prefs.getBoolean(IS_VIB, true);
		isbeep = prefs.getBoolean(IS_BEEP, false);
		volbtn= prefs.getBoolean(IS_VOL, false);
		isAlert=prefs.getBoolean(IS_ALERT,true);
		mp = MediaPlayer.create(getApplicationContext(), R.raw.clicks);
		ma = MediaPlayer.create(getApplicationContext(), R.raw.alert);

		wm = (WindowManager) getSystemService(Context .WINDOW_SERVICE);
		inflate = ( LayoutInflater ) getBaseContext()
			.getSystemService
		(Context.LAYOUT_INFLATER_SERVICE);
		overlayedButton = inflate.inflate
		(R.layout.count_view, null);
		li =  ( LinearLayout) overlayedButton.findViewById
		(R.id.main_lay);
		btnopen=(ImageButton)overlayedButton.findViewById(R.id.Counter_open);
		dis = ( TextView) overlayedButton.findViewById
		(R.id.Countdisp);
		String font=prefs.getString("font","Digital");
		Typeface face;
		if(font.equals("Digital")){
			face= Typeface.createFromAsset(getAssets(), "fonts/digital.ttf");

		}
		else{
			face= Typeface.createFromAsset(getAssets(), "fonts/osp_din.ttf");
		}
		dis.setTypeface(face);
		dis.setText(String.valueOf(Count));
		li.setOnClickListener(this);
		li.setOnTouchListener(this);
		li.setOnLongClickListener( new
			View. OnLongClickListener () {
				@Override
				public boolean onLongClick( View v) {
					canMove = true;
					return false ;
				}
			});
		btnopen.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View _v) { 
					Intent intent = new Intent
					(getBaseContext(),MainActivity .class );
					intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
					getApplication().startActivity(intent);

				}
			});



		WindowManager .LayoutParams params
			= new WindowManager.LayoutParams
		(WindowManager .LayoutParams . WRAP_CONTENT,
		 WindowManager .LayoutParams . WRAP_CONTENT,
		 WindowManager .LayoutParams . TYPE_SYSTEM_ALERT ,
		 WindowManager .LayoutParams . FLAG_NOT_FOCUSABLE |
		 WindowManager .LayoutParams . FLAG_NOT_TOUCH_MODAL ,
		 PixelFormat .TRANSLUCENT );
		params .gravity =
			Gravity.LEFT | Gravity .TOP;
		params .x = 0 ;
		params .y = 0 ;
		wm .addView(overlayedButton, params);
		topLeftView = new View( this);
		WindowManager .LayoutParams topLeftParams = new
			WindowManager.LayoutParams
		(WindowManager .LayoutParams . WRAP_CONTENT,
		 WindowManager .LayoutParams . WRAP_CONTENT,
		 WindowManager .LayoutParams . TYPE_SYSTEM_ALERT ,
		 WindowManager .LayoutParams . FLAG_NOT_FOCUSABLE |
		 WindowManager .LayoutParams . FLAG_NOT_TOUCH_MODAL ,
		 PixelFormat .TRANSLUCENT );
		topLeftParams. gravity =
			Gravity.LEFT | Gravity .TOP;
		topLeftParams. x = 0 ;
		topLeftParams. y = 0 ;
		topLeftParams. width = 0 ;
		topLeftParams. height = 0 ;
		wm .addView(topLeftView, topLeftParams);
    }
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (overlayedButton != null) {
			wm .removeView(overlayedButton);
			wm .removeView(topLeftView);
			overlayedButton = null;
			topLeftView = null;
		}
	}

	@Override
	public boolean onTouch(View v ,
						   MotionEvent event ) {
		if (event . getAction() ==
			MotionEvent .ACTION_DOWN ) {
			startClickTime =System .currentTimeMillis();
			float x = event . getRawX();
			float y = event . getRawY();
			moving = false ;
			int[] location = new int[ 2 ];
			overlayedButton .getLocationOnScreen(location);
			originalXPos = location[0 ];
			originalYPos = location[1 ];
			offsetX = originalXPos - x;
			offsetY = originalYPos - y;
		} else if (event . getAction() ==
				   MotionEvent .ACTION_MOVE&&canMove ) {
			int[] topLeftLocationOnScreen = new int
				[2 ];
			topLeftView .getLocationOnScreen(topLeftLocationOnScreen);
			float x = event . getRawX();
			float y = event . getRawY();
			WindowManager .LayoutParams params
				= (LayoutParams) overlayedButton .getLayoutParams();
			int newX = ( int ) (offsetX + x);
			int newY = ( int ) (offsetY + y);
			if (Math. abs(newX - originalXPos) < 1 &&
				Math.abs(newY - originalYPos) < 1 && !
				moving) {
				return false ;
			}
			params .x = newX - (topLeftLocationOnScreen[0 ]);
			params .y = newY - (topLeftLocationOnScreen[1 ]);
			wm .updateViewLayout(overlayedButton, params);
			moving = true ;
		} else if (event . getAction() ==
				   MotionEvent .ACTION_UP ) {
			if(System .currentTimeMillis() - startClickTime <
			   ViewConfiguration.getTapTimeout()) {


				Count++;
				if(Count>999999)
				{
					Toast.makeText( this, "maximum digit reached", Toast .LENGTH_SHORT ).show();
					vibrater.vibrate((long)(100));
					return true;
				}
				prefs.edit().putInt(L_COUNT,Count).commit();
				dis.setText(String.valueOf(Count));
				canMove=false;
				if (isvibrate) {
					vibrater.vibrate((long)(35));
				}
				if (isbeep) {
					if (mp.isPlaying()) {
						mp.stop();
						mp.release();
						mp = MediaPlayer.create(getApplicationContext(), R.raw.clicks);
					}
					mp.start();
				}
				if(Count==target){
					if(isAlert){ma.start();}
				}
			}
			else{
				canMove=true;
			}
			if (moving) {
				return true;
			}
		}
		return false ;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		if(intent.getExtras()!=null){
		    Bundle Act=intent.getExtras();
			if(Act.getString("Action").equals("Reset")){
				reset();
			}
		}
		Count=prefs.getInt(L_COUNT,0);
		target=prefs.getInt(TARGET,0);
		dis.setText(String.valueOf(Count));
		return super.onStartCommand(intent, flags, startId);
	}

	private void reset(){
		dis.setText("0");
		prefs.edit().putInt(L_COUNT,0).commit();
		Count=0;
	}
	@Override
	public void onClick(View v ) {

	}
}
