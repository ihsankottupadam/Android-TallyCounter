 package  com.izn.tallycounter;

import android.app.*;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.view.KeyEvent;
import android.view.View.*;
import java.util.List;
import android.app.ActivityManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.view.WindowManager.LayoutParams;
import android.util.*;
import android.preference.PreferenceManager;
import android.content.*;
import android.content.Context;

public class MainActivity extends Activity 
{
	private TextView txtdisplay;
	private LinearLayout linear_base;
	private Button optbtn;
	private ScrollView optscroll;
	private LinearLayout texttarget;
	private ImageButton tgtclose;
	private Button addbtn;
	private Button minusbtn;
	private LinearLayout linear_button;

	private Intent intent_view = new Intent();
	private SharedPreferences prefs;
	private Vibrator vibrater;
	private MediaPlayer mp;
	private MediaPlayer my;
    private MediaPlayer mz;

	private int Count = 0;
	private int back = 0;
	private boolean isbeep;
	private boolean isvibrate;
	private boolean remreset=true;
	private boolean iswake;
	private boolean volbtn;
	private boolean isAlert;
	private String de_number= "";
	private int target=0;
	private String cType="";
	Context context;
	private String cTypePrev;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		initialize ();
		initl();
		try{
			if(isMyServiceRunning(this,MyService.class)){
			    stopService(new Intent(this,MyService.class));
			}
			
		}catch (Exception e){
			
		}
		mp = MediaPlayer.create(getApplicationContext(), R.raw.clicks);
		my = MediaPlayer.create(getApplicationContext(), R.raw.alert);
		mz=MediaPlayer.create(getApplicationContext(),R.raw.blank);
		if(iswake){
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}

    }

	private void initialize(){
		txtdisplay = (TextView) findViewById(R.id.txtdisp);
		linear_base= (LinearLayout) findViewById(R.id.linear_base);
		optbtn= (Button) findViewById(R.id.optbtn);
		tgtclose= (ImageButton) findViewById(R.id.tgtclose);
		texttarget =(LinearLayout) findViewById(R.id.lineartarget);
		optscroll=(ScrollView) findViewById(R.id.Scroll);
		addbtn= (Button) findViewById(R.id.addbtn);
		minusbtn =(Button)findViewById(R.id.minusbtn);
		linear_button=(LinearLayout)findViewById(R.id.linear_button);

		vibrater = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		Context context=this;
		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		isvibrate = prefs.getBoolean("isvibrate", true);
		linear_base.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View _v) {
					if (optscroll.getVisibility() == View.VISIBLE) {
						optscroll.setVisibility(View.GONE);
						return;
					}
					if(cType.equals("Tap")){
						addCount();
					}
				}
			});
		tgtclose.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View _v) { 
					if (texttarget.getVisibility() == View.VISIBLE) {
						Animation animation1 = 
							AnimationUtils.loadAnimation(getApplicationContext(), R.anim.movet);
						texttarget.startAnimation(animation1);
						texttarget.setVisibility(View.GONE);
					}
				}
			});
		addbtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View _v) { 
					addCount();
				}
			});
		minusbtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View _v) {
					minusCount();
				}
			});
		optbtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View _v) { 
					if (optscroll.getVisibility() == View.VISIBLE) {
						optscroll.setVisibility(View.GONE);
					} else {
						optscroll.setVisibility(View.VISIBLE);
					}
				}
			});
	}
	private void initl(){
		String font=prefs.getString("font","Digital");
		Typeface face;
		if(font.equals("Digital")){
		face= Typeface.createFromAsset(getAssets(), "fonts/digital.ttf");
		
		}
		else{
			face= Typeface.createFromAsset(getAssets(), "fonts/osp_din.ttf");
		}
		txtdisplay.setTypeface(face);
		optscroll.setVisibility(View.GONE);
		target=prefs.getInt("target",0);
		isvibrate = prefs.getBoolean("isvibrate", true);
		cType= prefs.getString("cType","Tap");
		isbeep = prefs.getBoolean("isbeep", false);
		volbtn= prefs.getBoolean("volbtn", false);
		Count=prefs.getInt("count",0);
		iswake= prefs.getBoolean("iswake", false);
		cTypePrev= prefs.getString("cTypePrev","Button");
		isAlert=prefs.getBoolean("isAlert",true);
		txtdisplay.setText(String.valueOf(Count));
		if(cType.equals("Tap")){
			linear_button.setVisibility(View.GONE);
		}
		if(cType.equals("Tap") && cTypePrev.equals("Button")){
			showMessage("Touch anywher to count");
		}
		prefs.edit().putString("cTypePrev",cType).commit();
		cTypePrev=cType;
	}

	public void optclick(View v)
	{
		optscroll.setVisibility(View.GONE);
		switch (v.getId()){
			case R.id.ACTION_RESET:
				LayoutInflater factory = LayoutInflater.from(this);
				final View DialogView = factory.inflate(R.layout.dialog_reset, null);
				final AlertDialog cDialog = new AlertDialog.Builder(this).create();
				cDialog.setView(DialogView);
				cDialog.setCanceledOnTouchOutside(true);
				DialogView.findViewById(R.id.dr_ok).setOnClickListener(new OnClickListener() {    
						@Override
						public void onClick(View v) {
							prefs.edit().putInt("count", 0).commit();
							txtdisplay.setText("0");
							cDialog.dismiss();
							if (texttarget.getVisibility() == View.VISIBLE) {
								Animation animation1 = 
									AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
								texttarget.startAnimation(animation1);
								texttarget.setVisibility(View.GONE);
							}
							vibrater.vibrate((short)(20));

							if(!remreset){target=0;prefs.edit().putString("target", "0").commit();}
						}
					});
				DialogView.findViewById(R.id.dr_cancel).setOnClickListener(new OnClickListener() {    
						@Override
						public void onClick(View v) {
							cDialog.dismiss();    
						}
					});

				cDialog.show();
				break;
			case R.id.ACTION_TARGET:
				LayoutInflater factoryt = LayoutInflater.from(this);
				final View DialogtView = factoryt.inflate(R.layout.dialog_edit, null);
				final AlertDialog ctDialog = new AlertDialog.Builder(this).create();
				ctDialog.setView(DialogtView);
				ctDialog.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
				ctDialog.setCanceledOnTouchOutside(true);
				final TextView dttitle =(TextView) DialogtView.findViewById(R.id.de_title);
				dttitle.setText("Enter target");
				final EditText dtdit = (EditText) DialogtView.findViewById(R.id.de_etext);
				if(target>0){
					dtdit.setText((CharSequence)(Integer.toString((int)target)));
				}
				DialogtView.findViewById(R.id.de_ok).setOnClickListener(new OnClickListener() {    
						@Override
						public void onClick(View v) {
							de_number=dtdit.getText().toString();
							if(de_number.isEmpty())
							{
								return;
							}
							else{
								int denum=Integer.parseInt(de_number);
								prefs.edit().putInt("target",denum).commit();
								target=denum;
								if (texttarget.getVisibility() == View.VISIBLE) {
									Animation animation1 = 
										AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
									texttarget.startAnimation(animation1);
									texttarget.setVisibility(View.GONE);
							    }
								ctDialog.dismiss();
							}
						}
					});
				DialogtView.findViewById(R.id.de_cancel).setOnClickListener(new OnClickListener() {    
						@Override
						public void onClick(View v) {
							ctDialog.dismiss();    
						}
					});
				ctDialog.show();
				break;
			case R.id.ACTION_SETTINGS:
				intent_view.setClass(getApplicationContext(), SettingsActivity.class);
				intent_view.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent_view);
				break;
			case R.id.ACTION_SETVALUE:
				LayoutInflater factorye = LayoutInflater.from(this);
				final View DialogeView = factorye.inflate(R.layout.dialog_edit, null);
				final AlertDialog ceDialog = new AlertDialog.Builder(this).create();
				ceDialog.setView(DialogeView);
				ceDialog.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
				final TextView detitle =(TextView) DialogeView.findViewById(R.id.de_title);
				detitle.setText("Enter initial value");
				final EditText dedit = (EditText) DialogeView.findViewById(R.id.de_etext);
				ceDialog.setCanceledOnTouchOutside(true);
				DialogeView.findViewById(R.id.de_ok).setOnClickListener(new OnClickListener() {    
						@Override
						public void onClick(View v) {
							de_number=dedit.getText().toString();
							if(de_number.isEmpty())
							{
								return;
							}
							else{
								int denum=Integer.parseInt(de_number);
								prefs.edit().putInt("count", denum).commit();
								txtdisplay.setText(de_number);
								if (texttarget.getVisibility() == View.VISIBLE) {
									Animation animation1 = 
										AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
									texttarget.startAnimation(animation1);
									texttarget.setVisibility(View.GONE);
							    }
							    ceDialog.dismiss();
							}
						}
					});
				DialogeView.findViewById(R.id.de_cancel).setOnClickListener(new OnClickListener() {    
						@Override
						public void onClick(View v) {
							ceDialog.dismiss();    
						}
					});
				ceDialog.show();
			    break;
			case R.id.ACTION_MINI_COUNTER:
				startService(new Intent(getBaseContext(), MyService.class));
				finish();
				finishAffinity();
				break;
			case R.id.ACTION_ABOUT:
				
				intent_view.setClass(getApplicationContext(), AboutActivity.class);
				intent_view.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent_view);
				break;
			default:
			    break;
		}
	}
	@Override 
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if(volbtn){
			if(keyCode==KeyEvent.KEYCODE_VOLUME_UP){
				addCount();
				return true;

			}

			if(keyCode==KeyEvent.KEYCODE_VOLUME_DOWN){
				minusCount();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume()
	{
				Count=prefs.getInt("count",0);
				txtdisplay.setText(String.valueOf(Count));
		super.onResume();
	}
	
	private void addCount(){
		Count = Integer.parseInt(txtdisplay.getText().toString());
		Count++;
		if(Count>999999)
		{
			showMessage("maximum digit reached");
			vibrater.vibrate((long)(100));
			return;
		}
		prefs.edit().putInt("count",Count).commit();
		txtdisplay.setText(String.valueOf(Count));

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
		else{
			mz.start();
		}
		if(Count==target){
			texttarget.setVisibility(View.VISIBLE);
			Animation animation1 = 
				AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move);
			texttarget.startAnimation(animation1);
			vibrater.vibrate((long)(1000));
			if(isAlert){my.start();}
			if(!remreset)target=0;
		}
	}
	private void minusCount(){
		Count = Integer.parseInt(txtdisplay.getText().toString());
		if(Count<=0)
		{
			showMessage("Cannot minus from zero");
			vibrater.vibrate((long)(100));
			return;
		}
		Count--;

		prefs.edit().putInt("count", Count).commit();
		txtdisplay.setText(String.valueOf(Count));
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
		else{

			mz.start();
		}
	}
	@Override
	protected void onDestroy()
	{
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		if(mp!=null){mp.release();mp=null;}mz.release();if(mz!=null){my.release();my=null;}if(mp!=null){mz.release();mz=null;}
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		if (optscroll.getVisibility() == View.VISIBLE) {
			optscroll.setVisibility(View.GONE);
			return;
		}
		if (back == 0) {
			back = 1;
			showMessage("Press again to exit");
			back++;
		}
		else {
			finish();
		}
	}
    public  boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (services != null) {
            for (int i = 0; i < services.size(); i++) {
                if ((serviceClass.getName()).equals(services.get(i).service.getClassName()) && services.get(i).pid != 0) {
                    return true;
                }
            }
        }
        return false;
    }
	private void showMessage(String _s) {
		Toast.makeText(this, _s, Toast.LENGTH_SHORT).show();
	}
}
