package com.catchy.fishingpoll;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

class	EmptySaveCallback	extends	SaveCallback
{

	@Override
	public void done(ParseException anException)
	{
		// TODO Make sure we retry saving the file.
		// Track the avatar's objectId, so it can be deleted if abandoned or posted to user's account if saved.
		if(anException != null)
		{
		}
	}
}

class ProgressFormUpdate extends ProgressCallback
{

	@Override
	public void done(Integer aPercentDone)
	{
	}
}

public class PPPinAvatarImageButton extends ImageButton
{
	ParseFile									content;
	private			final	LoadAvatarImage		loadCallBack;
	private					List<Observer>		observers = new ArrayList<Observer>();
	private					Object				observerSupportObject;
	private	static	final	EmptySaveCallback	emptyCallBack = new EmptySaveCallback();
	private	static	final	ProgressFormUpdate	progressCallBack = new ProgressFormUpdate();

	final	String								MIMETYPE_IMAGE_ANY = "image/*";
	static	final	int							SELECT_PICTURE = 1;
	private			Fragment					hostFragment;

	static	ParseFile	ParseFileFromUri(ContentResolver aResolver, Uri aUri)
	{
		ParseFile	avatarFile;
		byte[]		avatarImageData;
		long		length;
		InputStream			is;
		Bitmap				pic;

		avatarFile = null;
		try
		{

			length = getContentLength(aResolver, aUri);
			if(length > 0)
			{

				avatarImageData = new byte[(int)length];
				is = aResolver.openInputStream(aUri);
				is.read(avatarImageData);

				pic = BitmapFactory.decodeByteArray(avatarImageData, 0, avatarImageData.length);
				if(pic != null)
				{
					avatarFile = new ParseFile(aUri.getLastPathSegment(), avatarImageData);
					avatarFile.saveInBackground(emptyCallBack, progressCallBack);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return	(avatarFile);
	}

	private	static	long	getContentLength(ContentResolver aResolver, Uri aUri) 
	{
		String[]	projection;
		Cursor		cursor;
		int			column_index;
		long		length;

		length = 0;
		try
		{
			projection = new String[] { MediaStore.Images.Media.SIZE };
			cursor = aResolver.query(aUri, projection, null, null, null);
			if(cursor != null)
			{
				if(cursor.moveToFirst())
				{
					column_index = cursor.getColumnIndex(MediaStore.Images.Media.SIZE);
					if(column_index >= 0)
						length = cursor.getLong(column_index);
				}
				cursor.close();
			}
		}
		catch(Exception anException)
		{
		}
		return	(length);
	}

	class LoadAvatarImage extends GetDataCallback
	{
		private	ImageView	pictureField;

		public LoadAvatarImage(ImageView aPictureField)
		{

			super();
			pictureField = aPictureField;
		}

		@Override
		public void done(byte[] aData, ParseException anException)
		{
			Bitmap		pic;

			if(aData != null)
			{

				pic = BitmapFactory.decodeByteArray(aData, 0, aData.length);
				if(pic != null)
					pictureField.setImageBitmap(pic);
			}
		}
	}

	class	PickReplacementAvatar	implements	View.OnClickListener
	{

		public void onClick(View arg0)
		{
			Activity	hostContext;
			Intent		intent;
			String		promptString;

			intent = new Intent();
			intent.setType(MIMETYPE_IMAGE_ANY);
			intent.setAction(Intent.ACTION_GET_CONTENT);
			hostContext = (Activity)getContext();
			promptString = hostContext.getString(R.string.intent_pick_avatar);
			hostFragment.startActivityForResult(Intent.createChooser(intent, promptString), SELECT_PICTURE);
		}
	}

	public PPPinAvatarImageButton(Context aContext, AttributeSet anAttrs)
	{
		super(aContext, anAttrs);
		setOnClickListener(new PickReplacementAvatar());
		loadCallBack = new LoadAvatarImage(this);
	}

	public	ParseFile	getContent()
	{
		return	(content);
	}

	public	void	setContent(ParseFile aContent)
	{

		if(aContent != content)
		{
			content = aContent;
			if(content != null)
			{
				content.getDataInBackground(loadCallBack, progressCallBack);
			}
			for(Observer eachObserver : observers)
				eachObserver.observe(this, observerSupportObject);
		}
	}

	public	void	addObserver(Observer anObserver, Object aSupportObject)
	{

		observers.add(anObserver);
		observerSupportObject = aSupportObject;		// TODO: This must be bound to the entry for its specific observer
	}

	public	void	removeObserver(Observer anObserver)
	{
		observers.remove(anObserver);
	}

	public	void	onIntentResult(int aRequestCode, int aResultCode, Intent anIntent)
	{
		Uri						selectedImageUri;
		ParseFile				avatarFile;

		selectedImageUri = anIntent.getData();
		avatarFile = PPPinAvatarImageButton.ParseFileFromUri(getContext().getContentResolver(), selectedImageUri);
		setContent(avatarFile);
	}

	public void klugeToWorkInAFragment(Fragment aFragment)
	{
		hostFragment = aFragment;
	}
}
