package com.catchy.fishingpoll;

import com.parse.Parse;
import com.parse.ParseACL;

import com.parse.ParseUser;

import android.app.Application;

public class FishingPollApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		// Add your initialization code here
		Parse.initialize(this, "IbuHwtODdUY7nCQ91Y5wgcynuyhmWb0w2SbDtT1A", "dfRUwewbI9zzOKdBdSGISYcaWVl8z6U4poIQ0d4J");

		ParseUser.enableAutomaticUser();
		ParseACL defaultACL = new ParseACL();
	    
		// If you would like all objects to be private by default, remove this line.
		defaultACL.setPublicReadAccess(true);
		
		ParseACL.setDefaultACL(defaultACL, true);
	}

}
