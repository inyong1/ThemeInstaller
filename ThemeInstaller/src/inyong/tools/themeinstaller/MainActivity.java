package inyong.tools.themeinstaller;

import android.app.*;
import android.content.*;
import android.content.res.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.stericson.RootTools.*;
import inyong.tools.themeinstaller.adapter.*;
import inyong.tools.themeinstaller.util.*;
import java.io.*;
import java.util.*;


public class MainActivity extends Activity implements OnClickListener
{

		private Context context;
		private Thread threadPekerja1;
		private ScrollView scrollView;
		private TextView tvStatus;
		private FileUtil fileUtil;
		public static final String[] file2Tema ={"SystemUI.apk", "framework-res.apk","image-preview.jpg","keterangan.txt"};
		public static final String[] folderTema={"Default","Flyme"};
		public static final	String sdcardFolder="/sdcard/ThemeInstaller";
		ListUtil listUtil;
		private ArrayList<ItemDetils> listTema;
		ListView listView;
		BaseAdapterKu adapterKu;
		Button tmbClearLog,tmbRestart;
		AppUtil appUtil;
		AssetManager am;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
		{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
				tmbClearLog = (Button)findViewById(R.id.tombol_clear_text_log); tmbClearLog.setOnClickListener(this);
				tmbRestart = (Button)findViewById(R.id.tombol_quick_restart); tmbRestart.setOnClickListener(this);
				tmbClearLog.setVisibility(View.GONE);
				new Handler().postDelayed(new Runnable(){
								@Override
								public void run()
								{

										context = getApplicationContext();
										am = context.getAssets();
										tvStatus = (TextView)findViewById(R.id.tv_status);
										scrollView = (ScrollView)findViewById(R.id.scroll_view_text_status);
										fileUtil = new FileUtil(context);
										appUtil = new AppUtil(context);
										listUtil = new ListUtil(context);
										listTema = new ArrayList<ItemDetils>();
										adapterKu = new BaseAdapterKu(context, listTema);
										listView = (ListView)findViewById(R.id.listview_tema);
										listView.setAdapter(adapterKu);
										listView.setDividerHeight(30);
										persiapan();
										detekUserKlikPadaListView();
										detekUserLongKlikPadaListView();

								}
						}, 100);

    }

////--------PERSIAPAN
		private void persiapan()
		{
				tvStatus.setText("Inisialisasi...\n");
				if(!getPackageName().contains("inyong")) return;
				threadPekerja1 = new Thread(new Runnable(){
								public void run()
								{	
										if (!fileUtil.buatfolders(sdcardFolder))
										{
												tvStatus.post(new Runnable(){public void run()
																{
																		appendTextStatus("failed\n");}});
												return;
										}
										sleep(100);
										if (!AppUtil.mandor().contains("upri"))return;
										listTema.addAll(new ArrayList<ItemDetils>(listUtil.getListItem()));
										tvStatus.post(new Runnable(){

														public void run()
														{
																adapterKu.notifyDataSetChanged();
																tvStatus.setVisibility(View.GONE);
														}
												});
								}			
						});
				threadPekerja1.start();
		}

		///// DETEK LONGKLIK PADA LISTVIEW
		private void detekUserLongKlikPadaListView()
		{
				listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
								public boolean onItemLongClick(AdapterView<?> p1, View p2, int posisi, long p4)
								{
										// Tampilkan pilihan hapus rename dan install
										final ItemDetils item=listTema.get(posisi);
										tampilkanDialogLongKlik(item);
										return true;  
								}
						});
		}

		/////dialog longKlik
		private void tampilkanDialogLongKlik(final ItemDetils item)
		{
				final String namaFolderTema=item.getNamaTema();
				AlertDialog.Builder a= new AlertDialog.Builder(this);
				a.setTitle(namaFolderTema);
				String[] pilihanDialog=new String[]{"Delete","Rename","Install"};
				a.setItems(pilihanDialog, new DialogInterface.OnClickListener(){@Override public void onClick(DialogInterface d, int posisi)
								{
										if (posisi == 0)
										{
												hapusTema(namaFolderTema);	
										}
										else if (posisi == 1)
										{
												tampilkanDialogRename(namaFolderTema);
										}
										else if (posisi == 2)tampilkanDialogInstall(item, isTemaAsset(item.getNamaTema()));
								}});
				a.show();
		}

		/// HAPUS TEMA
		private void hapusTema(String namaTema)
		{
				if (RootTools.deleteFileOrDirectory(sdcardFolder + "/" + namaTema, false))
				{
						Toast.makeText(this, sdcardFolder + "/" + namaTema + "\nDELETED Successfully", Toast.LENGTH_SHORT).show();
						listTema.clear();
						listTema.addAll(listUtil.getListItem());
						adapterKu.notifyDataSetChanged();
				}
		}

		///// DIALOG RENAME FOLDER TEMA
		String editedFinal;
		private void tampilkanDialogRename(final String namaFolder)
		{
				boolean bolehRename=true;
				for (String s:folderTema)
				{
						if (s.equals(namaFolder))
						{
								bolehRename = false;
								break;
						}
				}
				if (!bolehRename)
				{
						Toast.makeText(this, "Rename not allowed for stock theme", Toast.LENGTH_SHORT).show();
						return;
				}
				AlertDialog.Builder a=new AlertDialog.Builder(this);
				a.setTitle("RENAME THEME'S FOLDER");
				a.setMessage(sdcardFolder + "/" + namaFolder);
				final EditText edit=(EditText) getLayoutInflater().inflate(R.layout.edit_text, null, false);
				edit.setHint("a-z, A-Z, 0-9, -_");
				a.setView(edit);
				a.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){public void onClick(DialogInterface d, int i)
								{d.cancel();}});
				a.setPositiveButton("OK", new DialogInterface.OnClickListener(){public void onClick(DialogInterface d, int i)
								{
										String editedTemp=edit.getText().toString().trim();
										if (editedTemp.contains(" "))
										{
												editedFinal = "";
												for (String s:editedTemp.split(" "))
												{
														editedFinal += s;
												}
										}
										else editedFinal = editedTemp;
										gantiNamaFolderTema(namaFolder, editedFinal);
								}});
				a.show();
		}

		////// GANTI NAMA FOLDER TEMA
		private void gantiNamaFolderTema(String dari, String menjadi)
		{
				//make method pindah file
				fileUtil.pindahkanFile(sdcardFolder + "/" + dari, sdcardFolder + "/" + menjadi);
				listTema.clear();
				listTema.addAll(listUtil.getListItem());
				adapterKu.notifyDataSetChanged();
		}

///// DETEKSI KLIK PADA LIST TEMA
		private void detekUserKlikPadaListView()
		{
				listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

								public void onItemClick(AdapterView<?> p1, View view, int posisi, long p4)
								{
										final	ItemDetils itemdetil=listTema.get(posisi);
										if (isTemaAsset(itemdetil.getNamaTema()))
										{
												try
												{
														InputStream is=am.open(itemdetil.getNamaTema() + "/keterangan.txt");
														if (is.read() != -1)
														{
																tampilkaDialogKeteranganTema(itemdetil, true);
														}

												}
												catch (IOException e)
												{
														tampilkanDialogInstall(itemdetil, true);
												}
										}
										else
										{
												if (fileUtil.cekFileAda(sdcardFolder + "/" + itemdetil.getNamaTema() + "/" + file2Tema[3]))
												{
														tampilkaDialogKeteranganTema(itemdetil , false);	
												}
												else
														tampilkanDialogInstall(itemdetil, false); 
										}
								}
						});

		}

		/////Dialog keterangan tema
		private void tampilkaDialogKeteranganTema(final ItemDetils itemDetil, final boolean TemaAaset)
		{
				AlertDialog.Builder d=new AlertDialog.Builder(this);
				d.setTitle(itemDetil.getNamaTema());
				String keterangan="";
				if (TemaAaset)
				{
						keterangan = fileUtil.bacaTextFileAsset(itemDetil.getNamaTema() + "/keterangan.txt");
				}
				else keterangan = fileUtil.bacaTextFile(sdcardFolder + "/" + itemDetil.getNamaTema() + "/keterangan.txt");
				d.setMessage(keterangan);
				d.setPositiveButton("Install", new DialogInterface.OnClickListener(){public void onClick(DialogInterface d, int i)
								{
										tampilkanDialogInstall(itemDetil, TemaAaset);
								}});
				d.show();
		}

		///// TAMPILKAN DIALOG INSTALL TEMA
		String[] pilihanUntukDialog= null;
		List<String> fileYgDiinstall=new ArrayList<String>();
		CheckBox cb;
		//-----------
		private void tampilkanDialogInstall(final ItemDetils itemDetil, final boolean temaAsset)
		{
				pilihanUntukDialog = null;
				if (itemDetil.isSystemUiAda() && itemDetil.isFrameworkAda())
				{
						pilihanUntukDialog = new String[]{"SystemUI.apk","framework-res.apk"};
				}
				else if (itemDetil.isSystemUiAda() && !itemDetil.isFrameworkAda())
				{
						pilihanUntukDialog = new String[]{"SystemUI.apk"};
				}
				else if (!itemDetil.isSystemUiAda() && itemDetil.isFrameworkAda())
				{
						pilihanUntukDialog = new String[]{"framework-res.apk"};
				}

				fileYgDiinstall.clear();
				if (pilihanUntukDialog != null)
				{
						for (String s:pilihanUntukDialog) fileYgDiinstall.add(s);
				}
				boolean[] ceklist=null;
				if (pilihanUntukDialog != null)
				{
						if (pilihanUntukDialog.length == 2)
						{
								ceklist = new boolean[]{true,true};
						}
						else ceklist = new boolean[]{true};
				}
				AlertDialog.Builder dialog = new AlertDialog.Builder(this);
				dialog.setCancelable(true);
				dialog.setTitle("INSTALL THEME :\n" + itemDetil.getNamaTema());
				dialog.setIcon(R.drawable.ic_launcher);
				String negatifButton="Cancel";
				final View autoRestartView=getLayoutInflater().inflate(R.layout.restart_check_box, null);
				cb = (CheckBox)autoRestartView.findViewById(R.id.restart_check_box);
				//	cb.setEnabled(false);
				//	for(String s:pilihanUntukDialog)if (s.contains("framework"))cb.setEnabled(true);

				if (pilihanUntukDialog != null)
				{
						dialog.setView(autoRestartView);
						dialog.setMultiChoiceItems(pilihanUntukDialog, ceklist, new DialogInterface.OnMultiChoiceClickListener(){

										public void onClick(DialogInterface dialogInterface, int posisi, boolean check)
										{

												if (check)
												{
														String ygdicek=pilihanUntukDialog[posisi];
														String gabunganYgAkanDiinstall="";
														for (String s:fileYgDiinstall)gabunganYgAkanDiinstall += s;
														if (!gabunganYgAkanDiinstall.contains(ygdicek))fileYgDiinstall.add(ygdicek);
												}
												else
												{
														String ygdiuncek=pilihanUntukDialog[posisi];
														String gabunganYgAkanDiinstall="";
														for (String s:fileYgDiinstall)gabunganYgAkanDiinstall += s;
														if (gabunganYgAkanDiinstall.contains(ygdiuncek))
														{
																for (int i=0;i < fileYgDiinstall.size();i++)
																{
																		if (fileYgDiinstall.get(i).equals(ygdiuncek)) fileYgDiinstall.remove(i);
																}
														}
												}
										}
								});
						dialog.setPositiveButton("Install", new DialogInterface.OnClickListener(){
										public void onClick(DialogInterface p1, int p2)
										{
												boolean autoRestart=cb.isChecked();				
												installFileTema(itemDetil.getNamaTema(), fileYgDiinstall, temaAsset, autoRestart);
										}
								});
				}
				else
				{
						dialog.setMessage("Theme's files not found...!");
						negatifButton = "OK";
				}
				dialog.setNegativeButton(negatifButton, new DialogInterface.OnClickListener(){

								public void onClick(DialogInterface p1, int p2)
								{
										p1.cancel();
								}
						});
				dialog.show();
		}

		///////  INSTALL FILE KE SYSTEM
		String systemUiPath="";
		boolean proses;
		//--------
		private void installFileTema(final String namatema, final List<String> listfileYgdiinstall, final boolean temaAsset, final boolean autoRestart)
		{
				if (listfileYgdiinstall.size() < 1) return;
				sembunyikanView(tvStatus, false);
				sembunyikanView(tmbRestart, true);
				sembunyikanView(listView, true);
				if (!RootTools.isAccessGiven())
				{
						tvStatus.setText("Error...\nThe phone is not rooted");
						return;
				}

				tvStatus.setText("Installation\n");

				for (String fileSt:listfileYgdiinstall)
				{
						tvStatus.append(namatema + " : " + fileSt + "\n");
				}
				tvStatus.append("\n");
				threadPekerja1 = new Thread(new Runnable(){

								public void run()
								{
										List<String> fileFinal =new ArrayList<String>();
										if (listfileYgdiinstall.size() > 1)
										{
												fileFinal.add("SystemUI.apk");
												fileFinal.add("framework-res.apk");
										}
										else fileFinal.add(listfileYgdiinstall.get(0));
										if (temaAsset)
										{
												// ekatrak dari aaset ke sdcard jika temanya di asset
												tvStatus.post(new Runnable(){public void run()
																{
																		appendTextStatus("Unpacking to\n" + sdcardFolder + "/" + namatema + "/...\n");}});

												try
												{
														String[] files=am.list(namatema);
														proses = true;
														if (!fileUtil.buatfolders(sdcardFolder + "/" + namatema))
														{
																proses = false;
														}
														for (final String fileStr:files)
														{
																tvStatus.post(new Runnable(){public void run()
																				{
																						appendTextStatus(" - " + fileStr + "\n");}});
																if (!fileUtil.extractAssetKeSdcard(namatema + "/" + fileStr, sdcardFolder + "/" + namatema + "/" + fileStr))
																{
																		proses = false;
																}
														}

												}
												catch (IOException e)
												{}
												if (!proses)
												{
														tvStatus.post(new Runnable(){public void run()
																		{
																				appendTextStatus("Filed\n");}});
														return;
												}
												tvStatus.post(new Runnable(){public void run()
																{
																		appendTextStatus("\n");}});
										}

										tvStatus.post(new Runnable(){public void run()
														{
																appendTextStatus("Mounting /system as RW ...\n");}});
										if (fileUtil.mountrw("/system"))
										{
												tvStatus.post(new Runnable(){public void run()
																{
																		appendTextStatus("Result OK\n");}});
										}
										else
										{
												tvStatus.post(new Runnable(){public void run()
																{
																		appendTextStatus("Result failed\n");
																		sembunyikanView(tmbRestart, false);
																		sembunyikanView(tmbClearLog, false);}});
												return;
										}		
										sleep(500);
										for (final String file:fileFinal)
										{
												/// coppying...
												tvStatus.post(new Runnable(){public void run()
																{
																		appendTextStatus("Coppying " + file + "...\n");}});
												if (fileUtil.coppyFile("/sdcard/ThemeInstaller/" + namatema + "/" + file, "/system/" + file))
												{
														tvStatus.post(new Runnable(){public void run()
																		{
																				appendTextStatus("Result OK\n");}});
												}
												else
												{
														tvStatus.post(new Runnable(){public void run()
																		{
																				appendTextStatus("Result failed\n");
																				sembunyikanView(tmbRestart, false);
																				sembunyikanView(tmbClearLog, false);}});
														return;
												}		
												sleep(500);

												// Change permission
												tvStatus.post(new Runnable(){public void run()
																{
																		appendTextStatus("Change permission /system/" + file + " ...\n");}});
												if (fileUtil.chmod("/system/" + file))
												{
														tvStatus.post(new Runnable(){public void run()
																		{
																				appendTextStatus("Result OK\n");}});
												}
												else
												{
														tvStatus.post(new Runnable(){public void run()
																		{
																				appendTextStatus("Result failed\n");
																				sembunyikanView(tmbRestart, false);
																				sembunyikanView(tmbClearLog, false);}});
														return;
												}		
												sleep(500);
										}

										//Replacing files
										String gabunganNamaFile="";
										for (String s:fileFinal) gabunganNamaFile += s;
										if (gabunganNamaFile.contains("SystemUI"))
										{
												String systemUiApp="/system/app/SystemUI.apk",
														systemUiPrivApp="/system/priv-app/SystemUI.apk";
												if (fileUtil.cekFileAda(systemUiApp))
												{
														systemUiPath = systemUiApp;
												}
												else if (fileUtil.cekFileAda(systemUiPrivApp))
												{
														systemUiPath = systemUiPrivApp;
												}
												tvStatus.post(new Runnable(){public void run()
																{
																		appendTextStatus("Replacing " + systemUiPath + "...\n");}});
												if (systemUiPath != "" && fileUtil.pindahkanFile("/system/SystemUI.apk", systemUiPath))
												{
														tvStatus.post(new Runnable(){public void run()
																		{
																				appendTextStatus("Result OK\n");}});
												}
												else
												{
														tvStatus.post(new Runnable(){public void run()
																		{
																				appendTextStatus("Result failed\n");
																				sembunyikanView(tmbRestart, false);
																				sembunyikanView(tmbClearLog, false);}});
														return;
												}		
												sleep(500);

												/// Kill systemUi
												tvStatus.post(new Runnable(){public void run()
																{
																		appendTextStatus("Killing SystemUI...\n");}});
												if (appUtil.killSystemUI())
												{
														tvStatus.post(new Runnable(){public void run()
																		{
																				appendTextStatus("Result OK\n");}});
												}
												else
												{
														tvStatus.post(new Runnable(){public void run()
																		{
																				appendTextStatus("Result failed\n");}});
												}		
												sleep(500);

												/// Restarting SystemUI
												tvStatus.post(new Runnable(){public void run()
																{
																		appendTextStatus("Restarting SystemUI...\n");}});
												appUtil.restartSystemUI();
												sleep(500);
										}

										//replace framework
										if (gabunganNamaFile.contains("framework"))
										{
												tvStatus.post(new Runnable(){public void run()
																{
																		appendTextStatus("Replacing /system/framework/framework-res.apk ...\n");}});
												if (fileUtil.pindahkanFile("/system/framework-res.apk", "/system/framework/framework-res.apk"))
												{
														tvStatus.post(new Runnable(){public void run()
																		{
																				appendTextStatus("Result OK\n\nPlease Restart the phone");}});
												}
												else
												{
														tvStatus.post(new Runnable(){public void run()
																		{
																				appendTextStatus("Result failed\n");
																				sembunyikanView(tmbRestart, false);
																				sembunyikanView(tmbClearLog, false);}});
														return;
												}		
												sleep(500);
										}
										tvStatus.post(new Runnable(){
														public void run()
														{
																appendTextStatus("\nDone\n\n");
																sembunyikanView(tmbRestart, false);
																sembunyikanView(tmbClearLog, false);
														}
												});
										sleep(500);
										if (autoRestart)
										{
												tvStatus.post(new Runnable(){public void run()
																{
																		appendTextStatus("Auto restarting...\n");}});
												sleep(100);
												RootTools.restartAndroid();
										}
								}
						});
				threadPekerja1.start();
		}

		public void onClick(View view)
		{
				switch (view.getId())
				{
						case R.id.tombol_clear_text_log :
								sembunyikanView(tvStatus, true);
								sembunyikanView(tmbClearLog, true);
								sembunyikanView(listView, false);
								break;
						case R.id.tombol_quick_restart:
								if (RootTools.isAccessGiven())
								{
										AlertDialog.Builder a=new AlertDialog.Builder(this);
										a.setTitle("Quick Restart");
										a.setIcon(R.drawable.ic_launcher);
										a.setMessage("Only restart the android system");
										a.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){@Override public void onClick(DialogInterface d, int i)
														{d.cancel();}});
										a.setPositiveButton("Restart", new DialogInterface.OnClickListener(){public void onClick(DialogInterface d, int i)
														{
																RootTools.restartAndroid();
														}});
										a.show();
								}
								break;
						default :break;
				}
		}

		///// backup theme
		String backupFolder;
		private void backupTema()
		{
				sembunyikanView(tvStatus, false);
				sembunyikanView(tmbRestart, true);
				sembunyikanView(listView, true);
				tvStatus.setText("");
				threadPekerja1 = new Thread(new Runnable(){@Override public void run()
								{
										int x=1;
										backupFolder = sdcardFolder + "/backup_" + x;
										while (fileUtil.cekFileAda(backupFolder))
										{
												x++;
												backupFolder = sdcardFolder + "/backup_" + x;
										}
										tvStatus.post(new Runnable(){public void run()
														{
																appendTextStatus("Creating directory...\n" + backupFolder + "\n");}});
										if (fileUtil.buatfolders(backupFolder))
										{
												tvStatus.post(new Runnable(){public void run()
																{
																		appendTextStatus("Result OK\n\n");}});
										}
										else
										{
												tvStatus.post(new Runnable(){public void run()
																{
																		appendTextStatus("Result failed\n\n");}});
										}		
										sleep(50);

										tvStatus.post(new Runnable(){public void run()
														{
																appendTextStatus("Backing up SystemUI...\n");}});
										if (fileUtil.backupSystemUi(backupFolder + "/SystemUI.apk"))
										{
												tvStatus.post(new Runnable(){public void run()
																{
																		appendTextStatus("Result OK\n\n");}});
										}
										else
										{
												tvStatus.post(new Runnable(){public void run()
																{
																		appendTextStatus("Result failed\n\n");}});
										}		
										sleep(50);
										tvStatus.post(new Runnable(){public void run()
														{
																appendTextStatus("Backing up framework-res.apk...\n");}});
										if (fileUtil.backupFramework(backupFolder + "/framework-res.apk"))
										{
												tvStatus.post(new Runnable(){public void run()
																{
																		appendTextStatus("Result OK\n");}});
										}
										else
										{
												tvStatus.post(new Runnable(){public void run()
																{
																		appendTextStatus("Result failed\n");}});
										}
										listTema.clear();
										listTema.addAll(new ArrayList<ItemDetils>(listUtil.getListItem()));
										sleep(100);
										tvStatus.post(new Runnable(){
														public void run()
														{
																appendTextStatus("\nDone\n\n");
																sembunyikanView(tmbRestart, false);
																sembunyikanView(tmbClearLog, false);
																adapterKu.notifyDataSetChanged();
														}
												});
								}});
				threadPekerja1.start();

		}

		private void sembunyikanView(View view, boolean yes)
		{
				if (yes)
				{
						view.setVisibility(View.GONE);
				}
				else view.setVisibility(View.VISIBLE);
		}

		public void sleep(long l)
		{
				try
				{
						Thread.sleep(l);
				}
				catch (InterruptedException e)
				{}
		}

		private void appendTextStatus(String l)
		{
				tvStatus.append(l);
				scrollView.fullScroll(View.FOCUS_DOWN);
		}

		private boolean isTemaAsset(String namaTema)
		{
				boolean hasil=false;
				for (String namaTemaAsset:folderTema)
				{
						if (namaTemaAsset.equals(namaTema))
						{
								hasil = true;
								break;
						}
				}
				return hasil;
		}
		public boolean onCreateOptionsMenu(Menu menu)
		{
				super.onCreateOptionsMenu(menu);
				getMenuInflater().inflate(R.menu.menu, menu);
				return true;
		}

		public boolean onOptionsItemSelected(MenuItem item)
		{
				switch (item.getItemId())
				{
						case R.id.menu_backup_theme:
								backupTema();
								return true;
						case R.id.menu_downloat_tema_lain:
								gotoDownload();
								return true;
						case R.id.menu_about:
								startActivity(new Intent(this, About.class));
								return true;
						default: return super.onOptionsItemSelected(item);
				}
		}

		private void gotoDownload()
		{
				AlertDialog.Builder a = new AlertDialog.Builder(this);
				a.setTitle("SORRY");
				
				a.setMessage("Currently not availible for your " + Build.BRAND + " " + Build.MODEL);
				a.show();
		}

		public void onBackPressed()
		{
				AlertDialog.Builder a=new AlertDialog.Builder(this);
				a.setTitle(getResources().getString(R.string.app_name));
				a.setIcon(getResources().getDrawable(R.drawable.ic_launcher));
				a.setMessage("Exit?");
				a.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){public void onClick(DialogInterface d, int i)
								{d.cancel();}});
				a.setPositiveButton("Exit", new DialogInterface.OnClickListener(){public void onClick(DialogInterface d, int i)
								{finish();}});
				a.show();
		}
}
