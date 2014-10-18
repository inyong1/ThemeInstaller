package inyong.tools.themeinstaller.adapter;
import android.view.*;
import android.graphics.drawable.*;
import android.graphics.*;

public class ItemDetils
{
	private String namaTema, detilTema;
	private Bitmap imagePreview;
	private Drawable imagePreviewDrawable;
	private boolean systemUiAda=false, frameworkAda=false, imageAda=false;

	public void setImagePreviewDrawable(Drawable imagePreviewDrawable)
	{
		this.imagePreviewDrawable = imagePreviewDrawable;
	}

	public Drawable getImagePreviewDrawable()
	{
		return imagePreviewDrawable;
	}

	public void setDetilTema(String detilTema)
	{
		this.detilTema = detilTema;
	}

	public String getDetilTema()
	{
		return detilTema;
	}

	public void setSystemUiAda(boolean systemUiAda)
	{
		this.systemUiAda = systemUiAda;
	}

	public boolean isSystemUiAda()
	{
		return systemUiAda;
	}

	public void setFrameworkAda(boolean frameworkAda)
	{
		this.frameworkAda = frameworkAda;
	}

	public boolean isFrameworkAda()
	{
		return frameworkAda;
	}

	public void setImageAda(boolean imageAda)
	{
		this.imageAda = imageAda;
	}

	public boolean isImageAda()
	{
		return imageAda;
	}

	public void setImagePreview(Bitmap imagePreview)
	{
		this.imagePreview = imagePreview;
	}

	public Bitmap getImagePreview()
	{
		return imagePreview;
	}


	public void setNamaTema(String namaTema)
	{
		this.namaTema = namaTema;
	}

	public String getNamaTema()
	{
		return namaTema;
	}}
