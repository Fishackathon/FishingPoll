package com.catchy.fishingpoll;

import java.nio.channels.AlreadyConnectedException;
import java.util.Locale;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class FragmentLogin extends Fragment {
	View		fragmentView;

    public void xonCreate(Bundle savedInstanceState) {
        // super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// View		fragmentView;

		// If activity recreated (such as from screen rotate), restore
		// the previous article selection set by onSaveInstanceState().
		// This is primarily necessary when in the two-pane layout.
		if (savedInstanceState != null)
		{
			// mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
		}

		fragmentView = inflater.inflate(R.layout.fragment_login, container, false);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        // mSectionsPagerAdapter = new SectionsPagerAdapter(getActivity().getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        // mViewPager = (ViewPager) findViewById(R.id.pager);
        // mViewPager.setAdapter(mSectionsPagerAdapter);
		PPPinAvatarImageButton		i;
		i = (PPPinAvatarImageButton)fragmentView.findViewById(R.id.account_avatar);
		i.klugeToWorkInAFragment(this);

		Spinner s;
		s = (Spinner)fragmentView.findViewById(R.id.launch_spinner_language);
		s.setOnItemSelectedListener(new LanguageSelector(getActivity()));

		EditText	p;
		PasswordMatcher	m;
		p = (EditText)fragmentView.findViewById(R.id.account_password_verification);
		m = new PasswordMatcher(p);
		p.addTextChangedListener(m);

		CheckBox	c;
		c = (CheckBox)fragmentView.findViewById(R.id.account_new);
		c.setOnCheckedChangeListener(new LoginFormChangedListener((LoginActivity) getActivity()));

		Button		b;
		b = (Button)fragmentView.findViewById(R.id.account_action);
		b.setOnClickListener(new LoginOrRegisterListener((LoginActivity) getActivity()));

		toggleRegistrationForm(false);
		return	(fragmentView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	public	class	MessageDialogFragment	extends DialogFragment
	{
		private String message = null;

		public MessageDialogFragment()
		{
		}

		public MessageDialogFragment(String aMessage)
		{
			this.message = aMessage;
		}

		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			AlertDialog.Builder		builder = new AlertDialog.Builder(getActivity());
			AlertDialog				alert;

			builder.setMessage(message)
			.setCancelable(false)
			.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						dialog.cancel();
					}
				});

			alert = builder.create();
			return	(alert);
		}
	}

	String			unavailableName = null;

	static class LanguageSelector implements AdapterView.OnItemSelectedListener
	{
		Context		hostActivity;

		public LanguageSelector(Context aContext)
		{
			hostActivity = aContext;
		}

		@Override
		public void onItemSelected(AdapterView<?> aArg0, View aArg1, int aArg2, long aArg3)
		{
			setDefaultLocale(hostActivity, "en");
		}

		@Override
		public void onNothingSelected(AdapterView<?> aArg0)
		{
			// TODO Auto-generated method stub
			
		}
	}

	public static void setDefaultLocale(Context context, String locale)
	{
		Locale locJa = new Locale(locale);
		Locale.setDefault(locJa);

		Configuration config = new Configuration();
		config.locale = locJa;

		context.getResources().updateConfiguration(config, context.getResources()
				.getDisplayMetrics());
	}

	private void launchHomeActivity()
	{
		Intent mainIntent;

		/*
		finish();
		// PPPinMapActivity.setUserLoggedIn(this, true);
		mainIntent = new Intent();
		mainIntent.setClass(this, PPPinMapActivity.class);
		startActivity(mainIntent);
		*/
	}

	public	void	toggleRegistrationForm(Boolean isChecked)
	{
		TextView	f;
		Button		b;
		String		label;
		boolean		alreadyTaken;
		String		username;
		int			updatedVisibility;

		f = (TextView)fragmentView.findViewById(R.id.account_text_name_warning);
		// username = f.getText().toString();
		alreadyTaken = false;	// username.equals(unavailableName);		// Warning displayed only if seen before (by having submitted signup) for this name.

		updatedVisibility = isChecked ? View.VISIBLE : View.GONE;
		f = (TextView)fragmentView.findViewById(R.id.account_password_verification);
		f.setVisibility(updatedVisibility);
		f = (TextView)fragmentView.findViewById(R.id.account_email);
		f.setVisibility(updatedVisibility);

		b = (Button)fragmentView.findViewById(R.id.account_action);
		label = isChecked ? getString(R.string.sign_up) : getString(R.string.log_in);
		b.setText(label);
	}

	static	class	LoginFormChangedListener implements CompoundButton.OnCheckedChangeListener
	{
		LoginActivity		hostActivity;

		public	LoginFormChangedListener(LoginActivity aContext)
		{
			hostActivity = aContext;
		}

		@Override
		public void onCheckedChanged(CompoundButton aButtonView, boolean isChecked)
		{
			hostActivity.toggleRegistrationForm(isChecked);
		}
	}

	static	class	LoginOrRegisterListener implements View.OnClickListener
	{
		LoginActivity		hostActivity;
		PPPinLogInCallback	loginCallback;
		PPPinSignUpCallback	signupCallback;
		private	ProgressDialog	progressDialog	= null;

		class PPPinLogInCallback extends LogInCallback
		{
			public void done(ParseUser aUser, ParseException anException)
			{

				progressDialog.dismiss();
				if(aUser != null)
				{
					hostActivity.launchHomeActivity();
				}
				else
				{
					// Sign up didn't succeed. Look at the ParseException
					// to figure out what went wrong
					hostActivity.displayMessage(anException.getLocalizedMessage());
				}
			}
		}

		class PPPinSignUpCallback extends SignUpCallback
		{
			public void done(ParseException e)
			{

				progressDialog.dismiss();
				if(e == null)
				{
					// Hooray! Let them use the app now.
					hostActivity.launchHomeActivity();
				}
				else
				{
					// Sign up didn't succeed. Look at the ParseException
					// to figure out what went wrong
					hostActivity.displayMessage(e.getLocalizedMessage());
				}
			}
		}

		public LoginOrRegisterListener(LoginActivity aLoginActivity)
		{

			hostActivity = aLoginActivity;
			loginCallback = new PPPinLogInCallback();
			signupCallback = new PPPinSignUpCallback();
		}

		@Override
		public void onClick(View aView)
		{
			TextView		fTextView;
			CheckBox		cbNewUser;
			String			username;
			String			password;
			String			emailAddress;
			PPPinAvatarImageButton	fAvatar;
			Object			avatar;
			ParseUser		user;

			if (progressDialog == null)
			{
				progressDialog = ProgressDialog.show(hostActivity, null , "Please Wait...");
				progressDialog.setIndeterminate(true);
			}
			else
			{
				progressDialog.show();
			}
			fTextView = (TextView)hostActivity.findViewById(R.id.account_username);
			username = fTextView.getText().toString();
			fTextView = (TextView)hostActivity.findViewById(R.id.account_password);
			password = fTextView.getText().toString();
			fTextView = (TextView)hostActivity.findViewById(R.id.account_email);
			emailAddress = fTextView.getText().toString();
			fAvatar = (PPPinAvatarImageButton)hostActivity.findViewById(R.id.account_avatar);
			avatar = fAvatar.getContent();

			cbNewUser = (CheckBox)hostActivity.findViewById(R.id.account_new);
			if(cbNewUser.isChecked())
			{
				boolean			alreadyTaken;
				int				visibility;

				alreadyTaken = false;
				if(alreadyTaken != true)
				{
					user = new ParseUser();
					user.setUsername(username);
					user.setPassword(password);
					user.setEmail(emailAddress);
					user.put(FPUser.FIELDNAME_AVATAR, avatar);

					// other fields can be set just like with ParseObject
//					user.put("phone", "650-253-0000");

					user.signUpInBackground(signupCallback);

					visibility = View.GONE;
					hostActivity.unavailableName = null;
				}
				else
				{
					visibility = View.VISIBLE;
					hostActivity.unavailableName = username;
					progressDialog.dismiss();
				}
				fTextView = (TextView)hostActivity.findViewById(R.id.account_text_name_warning);
				fTextView.setVisibility(visibility);
			}
			else
			{
				ParseUser.logInInBackground(username, password, loginCallback);
				user = ParseUser.getCurrentUser();
			}
		}
	}

	@Override
	public	void	onActivityResult(int requestCode, int resultCode, Intent data)
	{
		PPPinAvatarImageButton		pictureSubform;

		if(resultCode == Activity.RESULT_OK)
		{
			if(requestCode == PPPinAvatarImageButton.SELECT_PICTURE)
			{
				pictureSubform = (PPPinAvatarImageButton)getView().findViewById(R.id.account_avatar);
				pictureSubform.onIntentResult(requestCode, resultCode, data);
			}
		}
	}

	static	class	SkipLoginListener implements View.OnClickListener
	{
		LoginActivity		hostActivity;

		public SkipLoginListener(LoginActivity aLoginActivity)
		{
			hostActivity = aLoginActivity;
		}

		@Override
		public void onClick(View aView)
		{
			ParseUser		user;

			user = ParseUser.getCurrentUser();
			if(user != null)
			{
				hostActivity.launchHomeActivity();
			}
			else
			{
				// TODO: Explain why PPPin couldn't enter anonymous use.
			}
		}
	}

	class	PasswordMatcher	implements TextWatcher
	{
		Observer	observer;
		Object		supportObject;
		EditText	verifier;
		View				rootView;
		CheckBox			createAccountCheckbox;

		public PasswordMatcher(EditText aSubForm)
		{

			super();
			verifier = aSubForm;
			rootView = verifier.getRootView();
			createAccountCheckbox = (CheckBox)verifier.getRootView().findViewById(R.id.account_new);
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
			String		verifierText;

			verifierText = verifier.getText().toString();
			if(createAccountCheckbox.isChecked())
			{
				boolean		alreadyTaken;
				String		s;
				int			updatedVisibility;
				TextView	f;
				EditText	p;

				p = (EditText)rootView.findViewById(R.id.account_password);
				s = p.getText().toString();
				alreadyTaken = verifierText.equals(s) != true;
				updatedVisibility = alreadyTaken ? View.VISIBLE : View.GONE;
				f = (TextView)rootView.findViewById(R.id.account_text_password_warning);
				f.setVisibility(updatedVisibility);
			}
		}
	}

	EditText	verifier;
	View				rootView;

	public	boolean	passwordsMatch()
	{
		String		verifierText;
		boolean		exact;
		String		s;
		EditText	p;

		verifierText = verifier.getText().toString();

		p = (EditText)rootView.findViewById(R.id.account_password);
		s = p.getText().toString();
		exact = verifierText.equals(s) != true;
		return	(exact);
	}

	private	void	displayMessage(String aMessage)
	{
		MessageDialogFragment	alert;

		alert = new MessageDialogFragment(aMessage);
		alert.show(getActivity().getSupportFragmentManager(), aMessage);
	}
    

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_login, container, false);
            // TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            // textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

}
