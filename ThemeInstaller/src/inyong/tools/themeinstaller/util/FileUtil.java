package inyong.tools.themeinstaller.util;
import android.content.*;
import com.stericson.RootTools.*;
import com.stericson.RootTools.exceptions.*;
import com.stericson.RootTools.execution.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import com.stericson.RootTools.internal.*;
import android.content.res.*;

public class FileUtil
{
	private Context context;
	CommandCapture cc;
	AssetManager am;
	
	public FileUtil(Context context)
	{
		this.context = context;
		am=context.getAssets();
	}
	public FileUtil()
	{}

	public String bacaTextFileAsset(String namaTema)
	{
		String hasil="";
		try
		{
			BufferedReader br=new BufferedReader(new InputStreamReader(am.open(namaTema)));
			String s="";
			while((s=br.readLine())!=null){
				hasil+=s+"\n";
			}
		}
		catch (IOException e)
		{}
		return hasil;
	}

	public boolean extractAssetKeSdcard(String assetPath, String sdcardPath) throws IOException
	{
		boolean hasil=false;
		InputStream fromFile = am.open(assetPath);
		OutputStream toFile = new FileOutputStream(sdcardPath);
		byte[] buffer =new byte[1024];
		int i;
		while ((i = fromFile.read(buffer)) > 0)
		{
			toFile.write(buffer, 0, i);
		}
		toFile.flush();
		toFile.close();
		fromFile.close();

		if (cekFileAda(sdcardPath)) hasil = true;
		return hasil;
	}

	///////// BACKUP framework
	public boolean backupFramework(String dst){
		boolean hasil=false;
		if(RootTools.copyFile("/system/framework/framework-res.apk",dst,false,false)){
			hasil=true;
		}else{
			if(coppyFile("/system/framework/framework-res.apk",dst)){
				hasil=true;
			}
		}
		return hasil;
	}
	
	/////\////////// Backup Systemui
	public boolean backupSystemUi(String dst){
		boolean hasil=false;
		String src ="/system/app/SystemUI.apk";
		if(!RootTools.exists(src) || !new File(src).exists()){
			src="/system/priv-app/SystemUI.apk";
		}
		
		if(RootTools.copyFile(src,dst,false,false)){
			hasil=true;
		}else{
			if(coppyFile(src,dst)){
				hasil=true;
			}
		}
		return hasil;
	}
	
	public boolean cekFileAda(String path)
	{
		boolean hasil=false;

		if (new File(path).exists() || RootTools.exists(path))
			hasil = true;
		return hasil;
	}

	////////////
	public boolean mountrw(String path)
	{
		boolean hasil=false;
		if (RootTools.remount(path, "rw"))
		{
			hasil = true;
		}
		else
		{
			cmdRoot("mount -o remount,rw " + path, true);
			cmdRoot("echo test > " + path + "/test.txt", true);
			if (cekFileAda(path + "/test"))
			{
				hasil = true;
				cmdRoot("rm " + path + "/test.txt", true);
			}
		}
		return hasil;
	}

	/////////////
	public boolean coppyFile(String src, String dst)
	{
		boolean hasil = false;
		if (RootTools.copyFile(src, dst, true, false))
		{
			hasil = true;
		}
		else
		{
			cmdRoot("cp -r " + src + " " + dst, true);
			if (cekFileAda(dst)) hasil = true;
		}
		return hasil;
	}

	//////////////
	public boolean buatfolders(String path)
	{
		boolean hasil=false;
		File f =new File(path);

		if (!f.mkdir()) 	f.mkdirs();	

		if (cekFileAda(path))
		{
			return true;
		}
		else
		{
			cmdRoot("mkdir " + path, false);
		}
		if (cekFileAda(path))
		{
			return true;
		}
		else
		{
			cmdRoot("busybox mkdir -p " + path, false);}

		if (cekFileAda(path)) hasil = true;

		return hasil;
	}


	///////////
	public boolean cmdRoot(String line, boolean b)
	{boolean exitcode=false;
		cc = new CommandCapture(0, false, line);
		if (b)
		{
			try
			{
				Shell.startRootShell().add(cc);
				commandWait(Shell.startRootShell(), cc);
				exitcode = cc.getExitCode() == 0;
			}
			catch (TimeoutException e)
			{}
			catch (RootDeniedException e)
			{}
			catch (IOException e)
			{}
			catch (Exception e)
			{}
		}
		else
		{
			try
			{
				Shell.startShell().add(cc);
				commandWait(Shell.startShell(), cc);
				exitcode = cc.getExitCode() == 0;
			}
			catch (TimeoutException e)
			{}
			catch (IOException e)
			{}
			catch (Exception e)
			{}
		}

		return exitcode;
	}


	public boolean chmod(String path)
	{
		boolean	hasil=false;

		if (cmdRoot("chmod 644 " + path, true))
		{
			hasil = true;
		}
		else
		{
			if (cmdRoot("busybox chmod 644 " + path, true))
			{
				hasil = true;
				}else{
					if(!RootTools.remount(path,"rw")){
						cmdRoot("mount -o remount,rw /system",true);
					}
					if(cmdRoot("chmod 644 "+path,true))hasil=true;
				}
		}


		return hasil;
	}
	
	///////////// Install systemUi.apk
	public boolean pindahkanFile(String src,String dst){
		boolean hasil=false;
		mountrw("/system");
		String[] cmds=new String[]{
			"mv "+src+" "+dst,
			"mv -f "+src+" "+dst,
			"busybox mv -f "+src+" "+dst
		};
		for(String s:cmds){
			if(cmdRoot(s,true)){
				hasil=true;
				break;
			}
		}
		return hasil;
	}
	
	////////// BACA FILE TEXT KE DALAM STRING
	public String bacaTextFile(String filePath){
		String hasil="";
		try
		{
			LineNumberReader lnr=new LineNumberReader(new FileReader(filePath));
			String line;
			try
			{
				while ((line = lnr.readLine()) != null)
				{
					hasil+=line+"\n";
				}
			}
			catch (IOException e)
			{}
		}
		catch (FileNotFoundException e)
		{}
		return hasil;
	}

	///////////////
	private void commandWait(Shell shell, Command cmd) throws Exception
	{

        while (!cmd.isFinished())
		{
            synchronized (cmd)
			{
                try
				{
                    if (!cmd.isFinished())
					{
                        cmd.wait(2000);
                    }
                }
				catch (InterruptedException e)
				{
                    e.printStackTrace();
                }
            }
		}
	}
}
