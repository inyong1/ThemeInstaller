package inyong.tools.themeinstaller;

import android.app.*;
import android.content.*;
import android.graphics.drawable.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import inyong.tools.themeinstaller.util.*;
import java.util.*;



public class About extends Activity
{
	ArrayList<DetilBaris> listdetil;
	DetilBaris db;
	AdapterKu adapter;
	ListView lv;
	ClipboardUtil clipboardUtil;
	private static final String[] text = new String[]{//text[].length dan ikon[].lenght harus slalu sama. pass null pada ikon jika tidk menggunakan. 
	"Theme Installer V3.2", //0
	"",//1
	"Created by inyong (Supri)",//2
	" PIN 7E63C905 (click to copy)", //3 untuk menambahkan PIN bb ikuti format penulisan ini agar saat user klik pin labgsung dikopi. perhatikan huruf besar dan spasi sesuai contoh
	" www.facebook.com/supri.yadi79",//4
	" forum.xda-developers.com/member.php?u=4963386", //5 url akan otomatis klickable, jagn memakai http://. ikuti contoh penulisan url
	" freemakemoney.tripod.com",//6
	"",//7
	"Modified by Yanu",//8
	" www.facebook.com/yanuabialwi",//9
	" PIN 75A7A28A (click to copy)"//10
	};


	public void onCreate(Bundle b)
	{
		super.onCreate(b);
		setContentView(R.layout.about);
		listdetil = new ArrayList<DetilBaris>();
		adapter = new AdapterKu(getApplicationContext(), listdetil);
		lv = (ListView)findViewById(R.id.list_view_about);
		lv.setDividerHeight(0);
		lv.setAdapter(adapter);

		new Handler().postDelayed(new Runnable(){@Override public void run()
				{

					Drawable[] ikon=new Drawable[11];
					ikon[0] = getResources().getDrawable(R.drawable.ic_launcher);
					ikon[1] = null;
					ikon[2] = null;
					ikon[3] = getResources().getDrawable(R.drawable.bbm);
					ikon[4] = getResources().getDrawable(R.drawable.fb);
					ikon[5] = getResources().getDrawable(R.drawable.xda);
					ikon[6] = null;
					ikon[7] = null;
					ikon[8] = null;
					ikon[9] = getResources().getDrawable(R.drawable.fb);
					ikon[10] = getResources().getDrawable(R.drawable.bbm);


					listdetil.clear();
					for (int i=0;i < text.length;i++)
					{
						db = new DetilBaris();
						db.st = text[i];
						db.dw = ikon[i];
						listdetil.add(db);
					}
					adapter.notifyDataSetChanged();
					clipboardUtil = new ClipboardUtil();
					lv.setOnItemClickListener(detekKlik());
				}}, 100);
	}

	private AdapterView.OnItemClickListener detekKlik()
	{
		return new AdapterView.OnItemClickListener(){

			public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
			{
				// TODO: Implement this method
				if (text[p3].contains("PIN")) if (clipboardUtil.copyToClipboard(getApplicationContext(), text[p3].trim().split(" ")[1]))Toast.makeText(getApplicationContext(), "PIN coppied", 500).show();
				if (text[p3].contains(".com"))startActivity(Intent.createChooser(new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + text[p3].trim())), "Open with"));
			}
		};
	}

	private class DetilBaris
	{
		Drawable dw;
		String st;
	}

	private class AdapterKu extends BaseAdapter
	{
		List<DetilBaris> list = new ArrayList<DetilBaris>();
		LayoutInflater li;
		AdapterKu(Context c, ArrayList<DetilBaris> list)
		{
			li = LayoutInflater.from(c);
			this.list = list;
		}
		public int getCount()
		{
			// TODO: Implement this method
			return	list.size();
		}

		public Object getItem(int p1)
		{
			// TODO: Implement this method
			return list.get(p1);
		}

		public long getItemId(int p1)
		{
			// TODO: Implement this method
			return p1;
		}

		class Vh
		{
			ImageView iv;
			TextView tv;
		}

		public View getView(int p1, View p2, ViewGroup p3)
		{
			// TODO: Implement this method
			Vh h;

			if (p2 == null)
			{
				p2 = li.inflate(R.layout.row_list_view_about, null);

				h = new Vh();

				h.iv = (ImageView) p2.findViewById(R.id.about_row_ikon);
				h.tv = (TextView) p2.findViewById(R.id.about_row_text);
				p2.setTag(h);
			}
			else
			{
				h = (Vh) p2.getTag();
			}

			if (list.get(p1) != null)	h.iv.setImageDrawable(list.get(p1).dw);
			h.tv.setText(list.get(p1).st);
			return p2;
		}


	}

	public static String JAGA()
	{
		return text[2];
	}

}
