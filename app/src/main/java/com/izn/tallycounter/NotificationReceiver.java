package  com.izn.tallycounter;

import android.app.*;
import android.os.Bundle;
import java.util.List;
import android.content.BroadcastReceiver;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.content.*;
import android.preference.*;

public class NotificationReceiver extends BroadcastReceiver {
    Context context;
	private static final String L_COUNT="count";
	private static final String CLOSE_ACTION="com.Ihsan.TallyCounter.Close";
	private static final String RESET_ACTION="com.Ihsan.TallyCounter.Reset";
	private static final String START_ACTION="com.Ihsan.TallyCounter.Start";
	private SharedPreferences prefs;
    @Override
    public void onReceive(Context context, Intent intent) {
		this.context = context;
		prefs=PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
		String action = intent.getAction();
		if (CLOSE_ACTION.equals
			(action)) {
			Intent background = new Intent(context,MyService.class);
            context.stopService(background);
		}
		else if (RESET_ACTION.equals(action)) {
			prefs.edit().putInt(L_COUNT,0).commit();
			Intent background = new Intent(context,MyService.class);
			background.putExtra("Action","Reset");
            context.startService(background);
		}
		else if(START_ACTION.equals(action)){
			Intent background = new Intent(context,MyService.class);
            context.startService(background);
		}
    }

    }
