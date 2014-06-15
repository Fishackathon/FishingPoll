package com.catchy.fishingpoll;

import com.parse.ParseUser;

public class FPUser {
	static	final	String		FIELDNAME_HANDLE		= "handle";
	static	final	String		FIELDNAME_AVATAR		= "avatarImageId";
	static	final	String		FIELDNAME_NAME_WHOLE	= "name";
	static	final	String		FIELDNAME_NAME_TITLE	= "name_title";
	static	final	String		FIELDNAME_NAME_FIRST	= "name_first";
	static	final	String		FIELDNAME_NAME_MIDDLE	= "name_middle";
	static	final	String		FIELDNAME_NAME_LAST		= "name_last";
	static	final	String		FIELDNAME_NAME_SUFFIX	= "name_suffix";
	static	final	String		FIELDNAME_BIO			= "bio";
	static	final	String		FIELDNAME_OCCUPATION	= "occupation";
	static	final	String		FIELDNAME_EMAIL			= "email";
	static	final	String		FIELDNAME_SKILLS		= "skills";
	public	static	final	String	FIELDNAME_INTERESTS	= "interests";
	static	final	String		FIELDNAME_USER			= "user";
	static	final	String				CLASSNAME				= "_User";
	static	final	String		FIELDNAME_USERNAME		= "username";
	static	final	String		FIELDNAME_PASSWORD		= "password";
	static	final	String		FIELDNAME_IDENTITIES	= "identities";
	public	static	final	String		CLASSNAME_INTEREST		= "Interest";
	public	static	final	String		CLASSNAME_PERSON		= "Person";
	public	static	final	String		FIELDNAME_SUBJECT		= "subject";
	public	static	final	int[]	DEFAULT_SUBJECTS	= new int[] { R.string.interests_system, R.string.interests_alert };
	private	static	final	int		INTERESTS_COUNT		= 9;

	ParseUser		userBackend;

	public	static	ParseUser	getCurrentUser()
	{
		ParseUser				user;

		user = ParseUser.getCurrentUser();
		if(user != null)
		{
		}
		return	(user);
	}

}
