package com.catchy.fishingpoll;

import java.util.ArrayList;
import java.util.List;

import com.catchy.fishingpoll.FragmentLogin.MessageDialogFragment;

import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class LoginActivity extends ActionBarActivity {

	String			unavailableName = null;

public class TabsPagerAdapter extends FragmentPagerAdapter {
	List<Fragment>		fragments = new ArrayList<Fragment>();
 
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

	@Override
	public Fragment getItem(int index) {
 
		switch(index)
		{
		case 1:
			if(fragments.size() < 2)
				fragments.add(new FragmentRoles());
			return	(fragments.get(index));

		case 0:
			if(fragments.size() < 1)
				fragments.add(new FragmentLogin());
			return	fragments.get(0);
		}

		return	(null);
	}

	@Override
	public	int
	getCount()
	{
		return	(2);
	}
}

	class	AccountPageChange implements ViewPager.OnPageChangeListener
	{

		@Override
		public void onPageScrollStateChanged(int aArg0)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageScrolled(int aArg0, float aArg1, int aArg2)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageSelected(int position)
		{
			actionBar.setSelectedNavigationItem(position);
		}
	}
    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;

    @Override
	protected void onCreate(Bundle aSavedInstanceState)
	{
		super.onCreate(aSavedInstanceState);

		setContentView(R.layout.activity_login);

		// Initialization
		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getSupportActionBar();
		mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

		viewPager.setAdapter(mAdapter);
		viewPager.setOnPageChangeListener(new AccountPageChange());
		actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Adding Tabs
		onAddTab("Login");
		onAddTab("Registration");
	}

	public void onAddTab(String aTitle)
	{
		final ActionBar		bar = getSupportActionBar();

		bar.addTab(bar.newTab()
				.setText(aTitle)
				.setTabListener(new TabListener(new TabContentFragment())));
	}

	public void onRemoveTab(String aTitle)
	{
		final ActionBar		bar = getSupportActionBar();

		bar.addTab(bar.newTab()
				.setText(aTitle)
				.setTabListener(new TabListener(new TabContentFragment())));
	}

	public void onToggleTabs(View v) {
		final ActionBar bar = getSupportActionBar();

		if (bar.getNavigationMode() == ActionBar.NAVIGATION_MODE_TABS) {
			bar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE, ActionBar.DISPLAY_SHOW_TITLE);
		} else {
			bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
		}
	}

	/**
	 * A TabListener receives event callbacks from the action bar as tabs
	 * are deselected, selected, and reselected. A FragmentTransaction
	 * is provided to each of these callbacks; if any operations are added
	 * to it, it will be committed at the end of the full tab switch operation.
	 * This lets tab switches be atomic without the app needing to track
	 * the interactions between different tabs.
	 *
	 * NOTE: This is a very simple implementation that does not retain
	 * fragment state of the non-visible tabs across activity instances.
	 * Look at the FragmentTabs example for how to do a more complete
	 * implementation.
	 */
	private class TabListener implements ActionBar.TabListener {
		private TabContentFragment mFragment;

		public TabListener(TabContentFragment fragment) {
			mFragment = fragment;
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// ft.add(R.id.fragment_content, mFragment, mFragment.getText());
			viewPager.setCurrentItem(tab.getPosition());
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			// ft.remove(mFragment);
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// Toast.makeText(ActionBarTabs.this, "Reselected!", Toast.LENGTH_SHORT).show();
		}
	}

	private class TabContentFragment extends Fragment {

		public TabContentFragment()
		{
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View fragView = inflater.inflate(R.layout.fragment_login, container, false);

			return	(fragView);
		}
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

	public void launchHomeActivity()
	{
		Intent mainIntent;

		finish();
		// PPPinMapActivity.setUserLoggedIn(this, true);
		mainIntent = new Intent();
		mainIntent.setClass(this, MainActivity.class);
		startActivity(mainIntent);
	}

	void	displayMessage(String aMessage)
	{
		MessageDialogFragment	alert;

		alert = new MessageDialogFragment(aMessage);
		alert.show(getSupportFragmentManager(), aMessage);
	}

	public	void	toggleRegistrationForm(Boolean isChecked)
	{
		TextView	f;
		Button		b;
		String		label;
		boolean		alreadyTaken;
		String		username;
		int			updatedVisibility;
		View		v;

		// v = this.getView();
		// username = f.getText().toString();
		alreadyTaken = false;	// username.equals(unavailableName);		// Warning displayed only if seen before (by having submitted signup) for this name.

		updatedVisibility = isChecked ? View.VISIBLE : View.GONE;
		f = (TextView)findViewById(R.id.account_password_verification);
		f.setVisibility(updatedVisibility);
		f = (TextView)findViewById(R.id.account_email);
		f.setVisibility(updatedVisibility);

		b = (Button)findViewById(R.id.account_action);
		label = isChecked ? getString(R.string.sign_up) : getString(R.string.log_in);
		b.setText(label);
	}
}
