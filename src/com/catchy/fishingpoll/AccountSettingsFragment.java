package com.catchy.fishingpoll;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

abstract interface Observer
{
	static	final	int			OBSERVATION_FIELD_CONTEXT			= 0;
	static	final	int			OBSERVATION_FIELD_CONTENT_FIELDNAME	= 1;
	static	final	int			OBSERVATION_FIELD_ORIGINAL_VALUE	= 2;
	static	final	int			OBSERVATION_FIELD_MODIFIED			= 3;

	public	boolean	observe(Object anObject, Object aSupportObject);
}

public class AccountSettingsFragment extends Fragment implements Observer
{
	private	PPPinAvatarImageButton	pictureSubform;
	Button				saveButton;
	Map<View, Object[]>	subformStates;
	ParseObject			content;
	private View		settingsView;
	private	ArrayList<ParseObject>	identities;

	class	SaveAccountSettings	implements	View.OnClickListener
	{

		@Override
		public void onClick(View aV)
		{
			Object[]		subformObjects;
			ParseObject		instanceObject;
			Object			currentValue;

			for(View eachView : subformStates.keySet())
			{
				subformObjects = subformStates.get(eachView);
				if((Boolean)subformObjects[Observer.OBSERVATION_FIELD_MODIFIED])
				{
					if(eachView instanceof EditText)
						currentValue = ((EditText)eachView).getText().toString();
					else if(eachView instanceof PPPinAvatarImageButton)
						currentValue = ((PPPinAvatarImageButton)eachView).getContent();
					else
						currentValue = null;		// We shouldn't have an unknown view type, so force an exception.
					instanceObject = (ParseObject)subformObjects[OBSERVATION_FIELD_CONTEXT];
					instanceObject.put((String)subformObjects[OBSERVATION_FIELD_CONTENT_FIELDNAME], currentValue);
				}
			}
			content.saveInBackground();
			getActivity().finish();
		}
	}

	class	SubformWatcher	implements TextWatcher
	{
		Observer	observer;
		Object		supportObject;
		EditText	subform;

		public SubformWatcher(Observer anObserver, EditText aSubForm, Object aSupportObject)
		{

			super();
			observer = anObserver;
			supportObject = aSupportObject;
			subform = aSubForm;
			observer.observe(subform, supportObject);
		}

		@Override
		public void afterTextChanged(Editable aString)
		{
		}

		@Override
		public void beforeTextChanged(CharSequence aString, int aStart, int aCount, int aReplacementLength)
		{
		}

		@Override
		public void onTextChanged(CharSequence aString, int aStart, int aPreviousLength, int aCount)
		{
			observer.observe(subform, supportObject);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		// If activity recreated (such as from screen rotate), restore
		// the previous article selection set by onSaveInstanceState().
		// This is primarily necessary when in the two-pane layout.
		if (savedInstanceState != null)
		{
			// mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
		}

		settingsView = inflater.inflate(R.layout.fragment_login, container, false);
	    pictureSubform = (PPPinAvatarImageButton)settingsView.findViewById(R.id.account_avatar);
		saveButton = (Button)settingsView.findViewById(R.id.account_action);
		subformStates = new HashMap<View, Object[]>();

		ParseUser				user;
		ParseObject				identity;

		user = FPUser.getCurrentUser();
		if(user != null)
		{
			content = user;

			identities = (ArrayList<ParseObject>)user.get(FPUser.FIELDNAME_IDENTITIES);
			identity = identities.get(0);

			loadSubform(identity, FPUser.FIELDNAME_AVATAR, R.id.account_avatar);
			loadSubform(user, FPUser.FIELDNAME_USERNAME, R.id.account_username);
			loadSubform(user, FPUser.FIELDNAME_EMAIL, R.id.account_email);
			loadSubform(user, FPUser.FIELDNAME_PASSWORD, R.id.account_password);
			// loadSubform(identity, FPUser.FIELDNAME_NAME_TITLE, R.id.account_name_title);
			// loadSubform(identity, FPUser.FIELDNAME_NAME_FIRST, R.id.account_name_first);
			// loadSubform(identity, FPUser.FIELDNAME_NAME_MIDDLE, R.id.account_name_middle);
			// loadSubform(identity, FPUser.FIELDNAME_NAME_LAST, R.id.account_name_last);
			// loadSubform(identity, FPUser.FIELDNAME_NAME_SUFFIX, R.id.account_name_suffix);

			saveButton.setOnClickListener(new SaveAccountSettings());
		}
		return	(settingsView);
	}

	@Override
	public	void	onActivityResult(int requestCode, int resultCode, Intent data)
	{

		if(resultCode == Activity.RESULT_OK)
		{
			if(requestCode == PPPinAvatarImageButton.SELECT_PICTURE)
				pictureSubform.onIntentResult(requestCode, resultCode, data);
		}
	}

	public	void	loadSubform(ParseObject anObject, String aField, int aSubformId)
	{
		Object			value;
		EditText		editTextSubform;
		View			subForm;
		Object[]		subformObjects;
		PPPinAvatarImageButton	pictureField;

		if(anObject != null)
		{
			value = anObject.get(aField);
			subForm = settingsView.findViewById(aSubformId);
			if(subForm != null)
			{
				if(subForm instanceof EditText)
				{
					editTextSubform = (EditText)subForm;
					if(value != null)
						editTextSubform.setText((CharSequence)value);
					editTextSubform.addTextChangedListener(new SubformWatcher(this, editTextSubform, null));
				}
				else if(subForm instanceof PPPinAvatarImageButton)
				{
					pictureField = (PPPinAvatarImageButton)subForm;
					if(value != null)
						pictureField.setContent((ParseFile)value);
					pictureField.addObserver(this, null);
					pictureField.klugeToWorkInAFragment(this);
				}

				subformObjects = new Object[] { anObject, aField, value, false };
				subformStates.put(subForm, subformObjects);
			}
		}
	}

	@SuppressWarnings("null")
	@Override
	public boolean observe(Object anObject, Object aSupportObject)
	{
		Object[]	subformObjects;
		boolean		differsFromOriginal;
		boolean		stateChanged;
		boolean		aggregatedState;
		Object		currentValue;

		differsFromOriginal = false;
		stateChanged = false;
		if(anObject instanceof EditText)
		{
			currentValue = ((EditText)anObject).getText().toString();
		}
		else if(anObject instanceof PPPinAvatarImageButton)
		{
			currentValue = ((PPPinAvatarImageButton)anObject).getContent();
		}
		else
			currentValue = null;	// Trigger an exception if we didn't identify the observed object's type.
		subformObjects = subformStates.get(anObject);
		if(subformObjects != null)
		{
			differsFromOriginal = currentValue.equals(subformObjects[Observer.OBSERVATION_FIELD_ORIGINAL_VALUE]) != true;
			stateChanged = subformObjects[Observer.OBSERVATION_FIELD_MODIFIED].equals(differsFromOriginal) != true;
			if(stateChanged)
				subformObjects[Observer.OBSERVATION_FIELD_MODIFIED] = differsFromOriginal;
		}

		if(stateChanged)
		{
			aggregatedState = false;
			for(Object[] eachSubform : subformStates.values())
				aggregatedState |= (Boolean)eachSubform[Observer.OBSERVATION_FIELD_MODIFIED];
			saveButton.setEnabled(aggregatedState);
		}
		return	(true);
	}
}
