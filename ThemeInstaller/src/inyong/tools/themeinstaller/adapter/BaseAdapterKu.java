package inyong.tools.themeinstaller.adapter;

import android.content.*;
import android.view.*;
import android.widget.*;
import inyong.tools.themeinstaller.*;
import java.util.*;
import android.graphics.drawable.*;

public class BaseAdapterKu extends BaseAdapter
{

	private static ArrayList<ItemDetils> arrayListItemDetils;
	private LayoutInflater layoutInflater;

	public BaseAdapterKu(Context context, ArrayList<ItemDetils> arrayListItemDetils)
	{
		layoutInflater = LayoutInflater.from(context);
		this.arrayListItemDetils = arrayListItemDetils;
	}
	
	@Override
	public int getCount()
	{
		// TODO: Implement this method
		return arrayListItemDetils.size();
	}

	@Override
	public Object getItem(int p1)
	{
		// TODO: Implement this method
		return arrayListItemDetils.get(p1);
	}

	@Override
	public long getItemId(int p1)
	{
		// TODO: Implement this method
		return p1;
	}
	
	//////////
	static class ViewHolder
	{
		ImageView imageTema;
		TextView namaTema,detilTema;
	}

	public View getView(int p1, View p2, ViewGroup p3)
	{
		ViewHolder holder;

		if (p2 == null)
		{
			p2 = layoutInflater.inflate(R.layout.row_list_tema, null);

			holder = new ViewHolder();

			holder.imageTema= (ImageView) p2.findViewById(R.id.row_list_image);
			holder.namaTema = (TextView) p2.findViewById(R.id.row_list_text);
			holder.detilTema = (TextView) p2.findViewById(R.id.row_list_detil_tema);
			p2.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) p2.getTag();
		}

		if(arrayListItemDetils.get(p1).isImageAda()){
		holder.imageTema.setImageBitmap(arrayListItemDetils.get(p1).getImagePreview());
		}else{
			holder.imageTema.setImageResource(R.drawable.defaultpreview);
		}
		holder.namaTema.setText(arrayListItemDetils.get(p1).getNamaTema());
		holder.detilTema.setText(arrayListItemDetils.get(p1).getDetilTema());
		
		return p2;
	}

}
