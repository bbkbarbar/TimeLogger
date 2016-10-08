package hu.barbar.timelogger.util;

import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import hu.barbar.timelogger.R;
import hu.barbar.timelogger.view.TimePreference;

/**
 * Contains methods for get different preferences of TimeLoggerApp
 *  
 * @author Andris
 */
public class Preferences {
	
	/**
	 * Default values of preferences 
	 */
	public static class Defaults {
		public static final boolean BLUETOOTH_WHEN_DRIVING = true;
		public static final boolean AUTO_ROTATE_WHEN_DRIVING = true;
		public static final boolean NEED_START_OTHER_APP_WHEN_DRIVING = true;
		public static final String DRIVING_APP_PACKAGE = "org.prowl.torque";
		public static final boolean NEED_TO_SHOW_DIALOG_FUEL_CONSUMED_WHEN_PARKING = true;
		public static final boolean NEED_TO_SHOW_DIALOG_WHEN_ADD_INFO = true;
		public static final String MESSAGE_APP_PACKAGE = "com.google.android.talk";
		public static final boolean NEED_START_OTHER_APP_WHEN_STOP_DRIVING = true;
		protected static final int AFTER_TIME_HOURS = 7;
		protected static final int AFTER_TIME_MINUTES = 0;
		public static final String START_OTHER_APP_WHEN_STOP_DRIVING_AFTER_TIME = AFTER_TIME_HOURS + TimePreference.TIME_SEPARATOR + AFTER_TIME_MINUTES + AFTER_TIME_MINUTES;
		protected static final int BEFORE_TIME_HOURS = 11;
		protected static final int BEFORE_TIME_MINUTES = 0;
		public static final String START_OTHER_APP_WHEN_STOP_DRIVING_BEFORE_TIME = BEFORE_TIME_HOURS + TimePreference.TIME_SEPARATOR + BEFORE_TIME_MINUTES + BEFORE_TIME_MINUTES;
		public static final boolean NEED_TO_USE_TIME_RANGE_FOR_MESSAGE_APP = true;
		public static final String INIT_DATE = "2010-07-13 00:00";
		public static final boolean NEED_TO_SHOW_CALCULATED_END_OF_WORKTIME = true;
		public static final String LENGTH_OF_WORKTIME = "8:20";
		public static final String EMAIL_FLAG_FOR_WORKTIME = myContext.getResources().getString(R.string.email_flag_for_worktime);
		public static final String EMAIL_FLAG_FOR_DRIVING = myContext.getResources().getString(R.string.email_flag_for_driving);
		public static final boolean NEED_TO_AUTOMATIC_FLAGGING_LOG_EMAILS = true;
		public static final String EMAIL_RECIPIENTS = "";
	}
	
	/**
	 * Preference keys of TimeLoggerApp 
	 */
	protected static class Keys {
		public static final String FIRST_RUN = myContext.getResources().getString(R.string.key_first_run);
		public static final String ENABLE_BT_WHEN_DRIVING = myContext.getResources().getString(R.string.key_enable_bluetooth_when_driving);
		public static final String AUTO_ROTATE_WHEN_DRIVING = myContext.getResources().getString(R.string.key_enable_autorotate_when_driving);
		public static final String DRIVING_APP_PACKAGE = myContext.getResources().getString(R.string.key_package_of_driving_app);
		public static final String NEED_TO_START_OTHER_APP_WHEN_DRIVING = myContext.getResources().getString(R.string.key_need_to_start_other_app_when_driving);
		public static final String NEED_TO_SHOW_DIALOG_FUEL_CONSUMED_WHEN_PARKING = myContext.getResources().getString(R.string.key_need_to_show_dialog_for_fuel_when_parking);
		public static final String NEED_TO_SHOW_DIALOG_WHEN_ADD_INFO = myContext.getResources().getString(R.string.key_need_to_show_dialog_when_add_info);
		public static final String MESSAGE_APP_PACKAGE = myContext.getResources().getString(R.string.key_package_of_message_app);
		public static final String NEED_TO_START_OTHER_APP_WHEN_STOP_DRIVING = myContext.getResources().getString(R.string.key_need_to_start_other_app_when_stop_driving);
		public static final String NEED_TO_START_OTHER_APP_WHEN_STOP_DRIVING_AFTER_TIME = myContext.getResources().getString(R.string.key_show_message_app_time_after);
		public static final String NEED_TO_START_OTHER_APP_WHEN_STOP_DRIVING_BEFORE_TIME = myContext.getResources().getString(R.string.key_show_message_app_time_before);
		public static final String NEED_TO_USE_TIME_RANGE_FOR_MESSAGE_APP = myContext.getResources().getString(R.string.key_use_time_range_of_message_app);
		public static final String INIT_DATE = myContext.getResources().getString(R.string.key_log_init_date);
		public static final String NEED_TO_SHOW_CALCULATED_END_OF_WORKTIME = myContext.getResources().getString(R.string.key_enable_calculate_end_of_worktime);
		public static final String LENGTH_OF_WORKTIME = myContext.getResources().getString(R.string.key_length_of_worktime);
		public static final String NEED_TO_AUTOMATIC_FLAGGING_LOG_EMAILS = myContext.getResources().getString(R.string.key_automatic_flagging_log_emails);
		public static final String EMAIL_RECIPIENTS = myContext.getResources().getString(R.string.key_email_recipients);
	}


	private static final String LIST_SEPARATOR = ";";
	
	
	/**
	 *  Context instance for reach String constants from xml files.<br>
	 *  App's mainActivty is used as context. 
	 */
	protected static Context myContext = null;
	
	/**
	 * SharedPreferences instance for reach and store preferences.
	 */
	protected SharedPreferences mySharedPreferences = null;
	
	
	public Preferences(Context context) {
		myContext = context;
		mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(myContext);
	}
	
	
	public boolean runFirst() {
		return mySharedPreferences.getBoolean(Keys.FIRST_RUN, true);
	}
	
	
	public void setFirstRunFalse() {
		SharedPreferences.Editor myEditor = mySharedPreferences.edit();
		myEditor.putBoolean(Keys.FIRST_RUN, false);
		myEditor.commit();
	}
	
	
	public boolean needToEnableBluetoothWhenDriving() {
		return mySharedPreferences.getBoolean(Keys.ENABLE_BT_WHEN_DRIVING, Defaults.BLUETOOTH_WHEN_DRIVING);
	}


	public boolean needToEnableAutoRotateWhenDriving() {
		return mySharedPreferences.getBoolean(Keys.AUTO_ROTATE_WHEN_DRIVING, Defaults.AUTO_ROTATE_WHEN_DRIVING);
	}


	public String getDrivingAppPackage() {
		return mySharedPreferences.getString(Keys.DRIVING_APP_PACKAGE, Defaults.DRIVING_APP_PACKAGE);
	}


	public boolean needToStartOtherAppWhenDriving() {
		return mySharedPreferences.getBoolean(Keys.NEED_TO_START_OTHER_APP_WHEN_DRIVING, Defaults.NEED_START_OTHER_APP_WHEN_DRIVING);
	}


	public boolean needToShowInputDialogWhenStopDriving() {
		return mySharedPreferences.getBoolean(Keys.NEED_TO_SHOW_DIALOG_FUEL_CONSUMED_WHEN_PARKING, Defaults.NEED_TO_SHOW_DIALOG_FUEL_CONSUMED_WHEN_PARKING);
	}


	public boolean needToShowInputDialogWhenAddInfo() {
		return mySharedPreferences.getBoolean(Keys.NEED_TO_SHOW_DIALOG_WHEN_ADD_INFO, Defaults.NEED_TO_SHOW_DIALOG_WHEN_ADD_INFO);
	}


	public String getMessageAppPackage() {
		return mySharedPreferences.getString(Keys.MESSAGE_APP_PACKAGE, Defaults.MESSAGE_APP_PACKAGE);
	}


	public boolean needToStartOtherAppWhenStopDriving() {
		return mySharedPreferences.getBoolean(Keys.NEED_TO_START_OTHER_APP_WHEN_STOP_DRIVING, Defaults.NEED_START_OTHER_APP_WHEN_STOP_DRIVING);
	}
	
	
	public Date getTimeAfterNeedToRunMessageApp() {
		
		int hours = Defaults.AFTER_TIME_HOURS;
		int minutes = Defaults.AFTER_TIME_MINUTES;
		
		try{
			String timeStr = mySharedPreferences.getString(Keys.NEED_TO_START_OTHER_APP_WHEN_STOP_DRIVING_AFTER_TIME, Defaults.START_OTHER_APP_WHEN_STOP_DRIVING_AFTER_TIME);
			String[] timeParts = timeStr.split(TimePreference.TIME_SEPARATOR);
			hours = Integer.valueOf(timeParts[TimePreference.HOUR]);
			minutes = Integer.valueOf(timeParts[TimePreference.MINUTE]);
		}catch(Exception e){
			Log.e("Preferences","Exception while try to parse \"time after\" value");
			//In case of exception set back to defaults
			hours = Defaults.AFTER_TIME_HOURS;
			minutes = Defaults.AFTER_TIME_MINUTES;
		}
		
		@SuppressWarnings("deprecation")
		Date dateAfter = new Date(0,0,0,hours,minutes,0);
		
		return dateAfter;
	}
	
	
	public Date getTimeBeforeNeedToRunMessageApp() {
		
		
		int hours = Defaults.BEFORE_TIME_HOURS;
		int minutes = Defaults.BEFORE_TIME_MINUTES;
		
		try{
			String timeStr = mySharedPreferences.getString(Keys.NEED_TO_START_OTHER_APP_WHEN_STOP_DRIVING_BEFORE_TIME, Defaults.START_OTHER_APP_WHEN_STOP_DRIVING_BEFORE_TIME);
			String[] timeParts = timeStr.split(TimePreference.TIME_SEPARATOR);
			hours = Integer.valueOf(timeParts[TimePreference.HOUR]);
			minutes = Integer.valueOf(timeParts[TimePreference.MINUTE]);
		}catch(Exception e){
			Log.e("Preferences","Exception while try to parse \"time before\" value");
			//In case of exception set back to defaults
			hours = Defaults.BEFORE_TIME_HOURS;
			minutes = Defaults.BEFORE_TIME_MINUTES;
		}
		
		@SuppressWarnings("deprecation")
		Date dateAfter = new Date(0,0,0,hours,minutes,0);
		
		return dateAfter;
	}

	
	public boolean needToUseTimeRangeForMessageApp() {
		return mySharedPreferences.getBoolean(Keys.NEED_TO_USE_TIME_RANGE_FOR_MESSAGE_APP, Defaults.NEED_TO_USE_TIME_RANGE_FOR_MESSAGE_APP);
	}


	public void storeLogInitDate(String dateStr) {

		SharedPreferences.Editor myEditor = mySharedPreferences.edit();
		myEditor.putString(Keys.INIT_DATE, dateStr);
		myEditor.commit();
		
	}
	
	
	public String getLogInitDate() {
		
		return mySharedPreferences.getString(Keys.INIT_DATE, Defaults.INIT_DATE);
		
	}


	public boolean needToAddLineWithCalculatedEndOfWorktime() {
		return mySharedPreferences.getBoolean(Keys.NEED_TO_SHOW_CALCULATED_END_OF_WORKTIME, Defaults.NEED_TO_SHOW_CALCULATED_END_OF_WORKTIME);
	}


	public String getWorkTime() {
		return mySharedPreferences.getString(Keys.LENGTH_OF_WORKTIME, Defaults.LENGTH_OF_WORKTIME);
	}


	public String getEmailFlagForWorktime() {
		return Defaults.EMAIL_FLAG_FOR_WORKTIME; //TODO: change this for a preference based one.
	}


	public String getEmailFlagForDriving() {
		return Defaults.EMAIL_FLAG_FOR_DRIVING; //TODO: change this for a preference based one.
	}


	public boolean needToAutomaticFlaggingLogEmails() {
		return mySharedPreferences.getBoolean(Keys.NEED_TO_AUTOMATIC_FLAGGING_LOG_EMAILS, Defaults.NEED_TO_AUTOMATIC_FLAGGING_LOG_EMAILS);
	}


	/**
	 * @return an array of email addresses (without whitespace characters) <br>
	 * or an empty array if there is no email addresses defined.
	 */
	public String[] getEmailRecipients() {
		
		// get String what contatins email addresses (or empty if there is no email addresses defined).
		String emails = mySharedPreferences.getString(Keys.EMAIL_RECIPIENTS, Defaults.EMAIL_RECIPIENTS);
		
		// returns an empty array if there is no email addresses defined.
		if(emails == null || emails.trim().length() == 0){
			return new String[]{};
		}
		
		// generate an array Strings what contatins email addresses (without whitespace characters)
		String[] result = emails.split(LIST_SEPARATOR);
		for(int i = 0; i<result.length; i++){
			result[i] = result[i].trim();
		}
		
		return result;
	}
	
	
}
