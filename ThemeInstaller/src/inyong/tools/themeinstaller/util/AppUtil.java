package inyong.tools.themeinstaller.util;
import android.content.*;
import android.os.*;
import inyong.tools.themeinstaller.*;

public class AppUtil
{	Context c;
	FileUtil fileUtil;
	public AppUtil(Context c)
	{
		this.c = c;
		fileUtil = new FileUtil();
	}

	public boolean killSystemUI()
	{
		//	ActivityManager am=(ActivityManager)c.getSystemService(Context.ACTIVITY_SERVICE);
		//	am.killBackgroundProcesses("com.bbm");
		//	am.restartPackage("com.bbm");
		return	fileUtil.cmdRoot("killall com.android.systemui", true);
	}

	public void restartSystemUI()
	{		
		Intent i=new Intent();
		ComponentName cn;
		
		int sdk =Build.VERSION.SDK_INT;
		int sdkHoneyComb=Build.VERSION_CODES.HONEYCOMB;
		if (sdk < sdkHoneyComb)
		{

			cn = new ComponentName("com.android.systemui", 
								   "com.android.systemui.statusbar.StatusBarService");
		}
		else
		{	
			cn = new ComponentName("com.android.systemui",
								   "com.android.systemui.SystemUIService");
		}
		i.setComponent(cn);
		c.startService(i);	
	}


	public void killApp()
	{


	}
//	"am","startservice","-n","com.android.systemui.statusbar.StatusBarService"
	public static String mandor(){
		return About.JAGA();
	}
}
