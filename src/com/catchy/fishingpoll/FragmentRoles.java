package com.catchy.fishingpoll;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

public class FragmentRoles extends Fragment {

	public	class	PickRole implements AdapterView.OnItemSelectedListener
	{
		int[] fragmentNames = new int[] { R.layout.fragment_fisherman, R.layout.fragment_merchant, R.layout.fragment_scientist };

		@Override
		public void onItemSelected(AdapterView<?> aParentView, View aSelectedView, int aViewPosition, long aRowNumber)
		{
			// getFragmentManager().beginTransaction().add(R.id.roles_fragment, new Fragment()).commit();
			View	roleView;
			ViewGroup	fragmentView;
			roleView = LayoutInflater.from(getActivity()).inflate(fragmentNames[(int)aRowNumber], null);
			fragmentView = (ViewGroup)getView().findViewById(R.id.roles_fragment);
			fragmentView.removeAllViews();
			fragmentView.addView(roleView);
		}

		@Override
		public void onNothingSelected(AdapterView<?> aParentView)
		{
		}
	}

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View		fragmentView;

		// If activity recreated (such as from screen rotate), restore
		// the previous article selection set by onSaveInstanceState().
		// This is primarily necessary when in the two-pane layout.
		if (savedInstanceState != null)
		{
			// mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
		}

		fragmentView = inflater.inflate(R.layout.fragment_roles, container, false);
		Spinner			spinnerRole;
		spinnerRole = (Spinner)fragmentView.findViewById(R.id.roles_spinner_role);
		spinnerRole.setOnItemSelectedListener(new PickRole());
		return	(fragmentView);
    }

}
