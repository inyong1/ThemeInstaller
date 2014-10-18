package inyong.tools.themeinstaller.util;

import inyong.tools.themeinstaller.*;
import java.util.*;
import java.io.*;
import inyong.tools.themeinstaller.adapter.*;
import android.content.*;
import android.app.*;
import android.graphics.drawable.*;
import android.graphics.*;
import android.content.res.*;
import com.stericson.RootTools.*;
import java.security.cert.*;

public class ListUtil
{

		String homeFolder =MainActivity.sdcardFolder;
		String[] nama2TemaAsset =MainActivity.folderTema;
		File homeFolderFile= new File(homeFolder);
		FileUtil fileUtil=new FileUtil();
		private static ArrayList<ItemDetils> listTemaAsset;
		Context context;

		public ListUtil(Context context)
		{
				this.context = context;
				listTemaAsset = getListItemAsset();
		}


		public ArrayList<String> getListFolderTema()
		{
				ArrayList<String> hasil = new ArrayList<String>();
				String[] nama2TemaSdcard=homeFolderFile.list();
				for (String namaTemaSdcard:nama2TemaSdcard)
				{
						for (String namaTemaAsset:nama2TemaAsset)
						{
								if (namaTemaAsset.equals(namaTemaSdcard))
								{
										RootTools.deleteFileOrDirectory(homeFolder + "/" + namaTemaSdcard, false);
								}
						}
				}
				for (File f:homeFolderFile.listFiles())
				{
						if (f.isDirectory())
						{
								String[] splited =f.toString().split("/");
								hasil.add(splited[splited.length - 1]);
						}
				}

				return hasil;
		}

		private ArrayList<ItemDetils> getListItemAsset()
		{
				ArrayList<ItemDetils> hasil=new ArrayList<ItemDetils>();
				ItemDetils item;
				for (String namaTema:nama2TemaAsset)
				{
						item = new ItemDetils();
						item.setNamaTema(namaTema);
						AssetManager am=context.getAssets();
						try
						{
								String[] FilesTema=am.list(namaTema);
								String image="";
								for (String file:FilesTema)
								{
										if (file.endsWith("jpg") || file.endsWith("png"))
										{
												image = file;
												break;
										}
								}
								if (image != "")
								{
										item.setImageAda(true);
										item.setImagePreview(new BitmapFactory().decodeStream(am.open(namaTema + "/" + image)));
								}
								String detilTema="";
								String[] componentTema=new String[]{"SystemUI.apk","framework-res.apk"};
								for (String s:componentTema)
								{
										if (am.open(namaTema + "/" + s) != null)
										{
												detilTema += s + " ";
												if (s.contains("UI"))
												{
														item.setSystemUiAda(true);
												}
												else if (s.contains("framework"))
												{
														item.setFrameworkAda(true);
												}
										}
								}
								item.setDetilTema(detilTema.trim());
						}
						catch (IOException e)
						{}
						hasil.add(item);
				}
				return hasil;
		}

		public  ArrayList<ItemDetils> getListItem()
		{
				ArrayList<ItemDetils> hasil=new ArrayList<ItemDetils>();
				hasil.addAll(listTemaAsset);

				ItemDetils item;

				for (String namaTema:getListFolderTema())
				{
						item = new ItemDetils();
						item.setNamaTema(namaTema);
						String detilTema="", imageFile="";
						File[] file2tema=new File(homeFolder + "/" + namaTema).listFiles();
						for (File file:file2tema)
						{	
								String fileToStr = file.toString();
								if (fileToStr.endsWith("SystemUI.apk"))
								{
										item.setSystemUiAda(true);
										detilTema += "SystemUI.apk ";
								}
								else if (fileToStr.endsWith("framework-res.apk"))
								{
										item.setFrameworkAda(true);
										detilTema += "framework-res.apk ";
								}
								else if (fileToStr.endsWith(".jpg") || fileToStr.endsWith(".png"))
								{
										item.setImageAda(true);
										imageFile=fileToStr;
								}
						}
						item.setDetilTema(detilTema);
						if (imageFile!="")
						{
								Bitmap bm=BitmapFactory.decodeFile(imageFile);
								item.setImagePreview(bm);
						}
						hasil.add(item);
				}
				return hasil;
		}
}
