package hu.barbar.timelogger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import hu.barbar.timelogger.util.DeviceHandler;
import hu.barbar.timelogger.util.FileHandler;
import hu.barbar.timelogger.util.Preferences;

public class TimeLoggerApp extends Activity {
	
	public static final boolean DEBUG_MODE = true;
	public static final String TAG = "TimeLogger";

	public static String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";	//"yyyy-MM-dd HH:mm:ss"
	public static String DATEFORMAT_FOR_EMAIL_SUBJECT = "yyyy-MM-dd HH:mm";	//"yyyy-MM-dd HH:mm:ss"
	public static String SEPARATOR_AFTER_TIMESTAMP = " - ";
	
	private EditText logArea = null;
	
	private ImageButton btnDrive = null;
	private ImageButton btnParking = null;
	private ImageButton btnCheckIn = null;
	private ImageButton btnCheckOut = null;
	private ImageButton btnWork = null;
	private ImageButton btnWorkEnd = null;
	private ImageButton btnInfo = null;
	
	public static final int DRIVE = 0,
							PARKING = 1,
							CHECK_IN = 2,
							CHECK_OUT = 3,
							WORK = 4,
							WORK_END = 5,
							INFO = 6,
							UNDEFINED = -1;
	
	private int lastUsedFunction = UNDEFINED;
	
	private OnClickListener myOnClickListener = null;
	
	private FileHandler myFileHandler = null; 
	private DeviceHandler myDeviceHandler = null;
	private Preferences myPreferences = null;
	
	private Date dateOfLastEntry = null;
	
	String dialogRes = "";
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		myFileHandler = new FileHandler(TimeLoggerApp.this);
		myDeviceHandler = new DeviceHandler(TimeLoggerApp.this);
		myPreferences = new Preferences(TimeLoggerApp.this);
		
		initGUI();
		
	}
	
	
	private void initGUI(){
		
		myOnClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if(v == btnDrive){
					onButtonPressed(TimeLoggerApp.DRIVE);
				}else
				if(v == btnParking){
					onButtonPressed(TimeLoggerApp.PARKING);
				}else
				if(v == btnCheckIn){
					onButtonPressed(TimeLoggerApp.CHECK_IN);
				}else
				if(v == btnCheckOut){
					onButtonPressed(TimeLoggerApp.CHECK_OUT);
				}else
				if(v == btnWork){
					onButtonPressed(TimeLoggerApp.WORK);
				}else
				if(v == btnWorkEnd){
					onButtonPressed(TimeLoggerApp.WORK_END);
				}else
				if(v == btnInfo){
					onButtonPressed(TimeLoggerApp.INFO);
				}
					
			}
		};
		
		btnDrive = (ImageButton) findViewById(R.id.btn_drive);
		btnParking = (ImageButton) findViewById(R.id.btn_parking);
		btnCheckIn = (ImageButton) findViewById(R.id.btn_checkin);
		btnCheckOut = (ImageButton) findViewById(R.id.btn_checkout);
		btnWork = (ImageButton) findViewById(R.id.btn_work);
		btnWorkEnd = (ImageButton) findViewById(R.id.btn_go_home);
		btnInfo = (ImageButton) findViewById(R.id.btn_info);
		
		btnDrive.setOnClickListener(myOnClickListener);
		btnParking.setOnClickListener(myOnClickListener);
		btnCheckIn.setOnClickListener(myOnClickListener);
		btnCheckOut.setOnClickListener(myOnClickListener);
		btnWork.setOnClickListener(myOnClickListener);
		btnWorkEnd.setOnClickListener(myOnClickListener);
		btnInfo.setOnClickListener(myOnClickListener);
		
		logArea = (EditText) findViewById(R.id.log_area);
		
	}
	
	
	public void onButtonPressed(int btnId){
		
		storeIdOfLastUsedFunction(btnId);
		
		switch (btnId) {
		case TimeLoggerApp.DRIVE:{
				logArea.append(getCurrentTimeStamp() + "Driving" + "\n");
				// enable bluetooth
				if(myPreferences.needToEnableBluetoothWhenDriving())
					myDeviceHandler.setBluetoothState(true);
				// enable rotation
				if(myPreferences.needToEnableAutoRotateWhenDriving())
					myDeviceHandler.setAutoOrientationState(TimeLoggerApp.this, true);
				// start other app when driving
				if(myPreferences.needToStartOtherAppWhenDriving())
					startDrivingApp();
			}
			break;
			
		case TimeLoggerApp.PARKING:{
				logArea.append(getCurrentTimeStamp() + "Stop driving" + "\n");
				// disable bluetooth
				if(myPreferences.needToEnableBluetoothWhenDriving())
					myDeviceHandler.setBluetoothState(false);
				// disable rotation
				if(myPreferences.needToEnableAutoRotateWhenDriving())
					myDeviceHandler.setAutoOrientationState(TimeLoggerApp.this, false);
				
				if(myPreferences.needToShowInputDialogWhenStopDriving())
					showDialogForExtraInfo( TimeLoggerApp.PARKING );
				
				// start other app when stop driving
				if(!myPreferences.needToShowInputDialogWhenStopDriving() && myPreferences.needToStartOtherAppWhenStopDriving())
					startMessageApp();
				// THIS FEATURE MOVED TO DIALOG-RESULT
				
			}
			break;
			
		case TimeLoggerApp.CHECK_IN:
			logArea.append(getCurrentTimeStamp() + "Check in" + "\n");
			break;
			
		case TimeLoggerApp.CHECK_OUT:
			logArea.append(getCurrentTimeStamp() + "Check out" + "\n");
			break;	
			
		case TimeLoggerApp.WORK: {
				logArea.append(getCurrentTimeStamp() + "Start worktime" + "\n");
				if(myPreferences.needToAddLineWithCalculatedEndOfWorktime()){
					String timeStampOfWorkEnd = getTimeStampOfWorkTimesEnd(new Date(), myPreferences.getWorkTime());
					if( timeStampOfWorkEnd != null && !timeStampOfWorkEnd.equals("") )
						logArea.append("Calculated end of worktime: " + timeStampOfWorkEnd);
					else
						showMsg("Unable to calculate end of worktime. :("); // TODO: change this to string constant from String.xml
				}
			}
			break;
			
		case TimeLoggerApp.WORK_END:
			logArea.append(getCurrentTimeStamp() + "End worktime" + "\n");
			break;
			
		case TimeLoggerApp.INFO: 
			if(myPreferences.needToShowInputDialogWhenAddInfo()){
				showDialogForExtraInfo( TimeLoggerApp.INFO );
				logArea.append(getCurrentTimeStamp(false));
			}else{
				logArea.append(getCurrentTimeStamp() + "Info" + "\n");
			}
			break;
			
		default:
			break;
		}
		
		
		// NOTE: hirtelen nem találom az okát, hogy miért lesznek itt-ott üres sorok indokolatlanul..
		clearEmptyLines();
		
	}


	private void storeIdOfLastUsedFunction(int functionId) {
		
		this.lastUsedFunction = functionId;
		
	}


	@Override
	protected void onPause() {
		
		// save log content without timestamp
		saveLogContent();
		myPreferences.setFirstRunFalse();
		
		super.onPause();
	}
	

	@Override
	protected void onResume() {
		
		super.onResume();
		
		if(!myPreferences.runFirst()) {
			loadDataFromFile();
		}else{	// in case of first run
			myPreferences.storeLogInitDate(getCurrentTimeStamp(false, DATEFORMAT_FOR_EMAIL_SUBJECT));
		}
			
	}
	
	
	private void sendCurrentLogViaEmail() {
		
		//save file before sending
		saveLogContent(); //TODO: ellenõrizni, hogy sikerült-e menteni, és ha nem akkor ne akarjunk emailt küldeni
		
		/**
		 * Generating subject of email (with adding [FLAG]-s according to logs content).
		 */
		String subject = "";
		if(myPreferences.needToAutomaticFlaggingLogEmails()){
			
			boolean flagWorktimeEntryFound = false;
			boolean flagDrivingEntryFound = false;
			
			String[] lines = logArea.getText().toString().split("\n");
			for(int i=0; i<lines.length; i++){
				if( !flagWorktimeEntryFound && lines[i].contains("worktime")){ 
					flagWorktimeEntryFound = true;
				}
				if( !flagDrivingEntryFound && lines[i].contains("driving")){
					flagDrivingEntryFound = true;
				}
			}
			
			if(flagWorktimeEntryFound)
				subject += myPreferences.getEmailFlagForWorktime() + " ";
			if(flagDrivingEntryFound)
				subject += myPreferences.getEmailFlagForDriving() + " ";
			
		} // AutomaticFlaggingLogEmails
		
		// TODO: Change hardcoded subject to a "preferences based" one..
		subject += "TimeLog " + myPreferences.getLogInitDate() + " - " + getCurrentTimeStamp(false, TimeLoggerApp.DATEFORMAT_FOR_EMAIL_SUBJECT);
		
		
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setType("text/plain");
		emailIntent.putExtra(Intent.EXTRA_EMAIL, myPreferences.getEmailRecipients());
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);	
		emailIntent.putExtra(Intent.EXTRA_TEXT, logArea.getText().toString());
		try {
		    startActivity(Intent.createChooser(emailIntent, "Send mail...")); // TODO replace with xml-based string
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(
		    		getBaseContext(),
		            "There are no email clients installed.",	 // TODO replace with xml-based string
		            Toast.LENGTH_SHORT
		    ).show();
		}
		
	}
	
	
	private void saveLogContent() {

		int res = myFileHandler.saveFile(getLinesFromLogArea(), false);
		if( res < FileHandler.RESULT_ERROR_WRITE_EMPTY){
			if(DEBUG_MODE)
				showMsg("Result: " + res);
		}
		
	}
	
	
	private void loadDataFromFile() {
		
		ArrayList<String> lines = myFileHandler.loadFile();
		
		if(lines != null){
			
			String buffer = "";
			for(int i=0; i<lines.size(); i++){
				buffer += lines.get(i) + "\n";
			}
			
			//show loaded data
			logArea.setText(buffer);
			
		}
		
		// NOTE: hirtelen nem találom az okát, hogy miért lesznek itt-ott üres sorok indokolatlanul..
		clearEmptyLines();
		
	}

	
	private void createNewLog() {
		
		// save log content with time stamp
		int res = myFileHandler.saveFile(getLinesFromLogArea(), true);
		
		if( DEBUG_MODE && res != FileHandler.RESULT_OK ){
			showMsg("Result: " + res);
		}else{
			
			// clean logArea
			logArea.setText("");
			
			// store init date of new log
			myPreferences.storeLogInitDate(getCurrentTimeStamp(false, DATEFORMAT_FOR_EMAIL_SUBJECT));
		}
		
	}
	
	
	private ArrayList<String> getLinesFromLogArea() {

		ArrayList<String> data = new ArrayList<String>();

		if(logArea == null){
			Log.e(TAG, "getLinesFromLogArea() :: logArea is null.");
			return data;
		}
		
		String[] lines = logArea.getText().toString().split("\n");
		for(int i=0; i<lines.length; i++){
			data.add(lines[i]);
		}
		
		return data;
	}

	
	public String getCurrentTimeStamp() {
		return getCurrentTimeStamp(true);
	}
	
	
	@SuppressLint("SimpleDateFormat")
	public String getCurrentTimeStamp(boolean needToAddSeparatorAfterTimeStamp) {
		return getCurrentTimeStamp(needToAddSeparatorAfterTimeStamp, TimeLoggerApp.DATEFORMAT);
	}
	
	
	@SuppressLint("SimpleDateFormat")
	public String getCurrentTimeStamp(boolean needToAddSeparatorAfterTimeStamp, String timeformat) {
	    SimpleDateFormat sdfDate = new SimpleDateFormat(timeformat);//dd/MM/yyyy
	    Date now = new Date();
	    //TODO HERE!!!
	    
	    String strDate = sdfDate.format(now) + (needToAddSeparatorAfterTimeStamp ? SEPARATOR_AFTER_TIMESTAMP : "");
	    
	    Date lastEntryDate = getLastEntryDate();
	    if(lastEntryDate != null){
	    	long diff = getDateDiff(lastEntryDate, now);
	    	
	    	/*
	    	Date date1 = new Date(2016, 10, 07, 21, 46, 00);
	    	Date date2 = new Date(2016, 10, 07, 21, 46, 15);
	    	long diff = getDateDiff(date1, date2);	/**/
	    	
	    	float d = (float)diff / 60;
	    	strDate += "  " + getElaspedTime(d) + "p";
	    	/*Toast.makeText(getApplicationContext(), "Diff: " + diff, 1).show();
	    	
	    	
	    	if(diff < 60) { // sec only:
	    		strDate += " 00:" + diff + "p";
	    	}else
	    	if(diff < 3600){
	    		float m = diff/60;
	    		int min = (int)m;
	    		int sec = (int) (diff - (min*60));
	    		strDate += " " + min + ":" + sec + "p";
	    	}else{
	    		float h = diff / 3600;
	    		int hours = (int)h;
	    		diff -= (hours*3600);
	    		float m = diff/60;
	    		int min = (int)m;
	    		int sec = (int) (diff - (min*60));
	    		strDate += " " + hours + ":" + min + ":" + sec + "p";
	    	}
	    	/**/
	    }
	    
	    return strDate;
	}
	
	
	private static String getElaspedTime(float valueInMin){
		
		if(valueInMin < 1){
			int sec = (int)(valueInMin*60f);
			return "00:" + (sec<10?"0":"") + sec;
		}else{
			int min = (int) valueInMin;
			int sec = (int)((float)(valueInMin - (float)(min))*60);
			return (min<10?"0":"") + min + ":" + (sec<10?"0":"") + sec;
		}
		
	}
	
	/**
	 * Get a diff between two dates
	 * @param date1 the oldest date
	 * @param date2 the newest date
	 * @return the diff value, in the provided unit
	 */
	private static long getDateDiff(Date date1, Date date2) {
		long d2 = getInSec(date2);
		long d1 = getInSec(date1);
		//return (date2.getTime() - date1.getTime());
		return (d2 - d1);
	}
	
	private static long getInSec(Date date){
		return (date.getSeconds()
				 + date.getMinutes() * 60
				 + date.getHours() * 3600
				 );
	}
	
	
	private Date getLastEntryDate() {
		
		if(logArea == null){
			return null;
		}
		String content = logArea.getText().toString();
		if(content == null) {
			return null;
		}
		String[] lines = content.split("\n");
		if(lines == null) {
			return null;
		}
		String lastLine = lines[lines.length-1];
		if(lastLine == null) {
			return null;
		}
		
		return parseDateFromString(lastLine);
		
	}
	
	@SuppressWarnings("deprecation")
	private Date parseDateFromString(String dateStr){
		
		// TODO: ISSUE
		
		/*
		Toast.makeText(getApplicationContext(), "Parse: |" + dateStr + "| as |" + timeformat + "|", 1).show();
		if(dateStr == null || timeformat == null){
			return null;
		}
	    DateFormat df = new SimpleDateFormat(timeformat);
	    Date result;
		try {
			result = df.parse(dateStr);
		} catch (ParseException e) {
			return null;
		}
	    return result;
	    /**/
		
		// 0123456789012345678
		// 2016-10-07 21:07:10
		int year =  Integer.parseInt(dateStr.substring(0, 4));
		int month = Integer.parseInt(dateStr.substring(5, 7));
		int day =   Integer.parseInt(dateStr.substring(8, 10));
		int hour =  Integer.parseInt(dateStr.substring(11, 13));
		int min =   Integer.parseInt(dateStr.substring(14, 16));
		int sec =   Integer.parseInt(dateStr.substring(17, 19));
		
		return new Date(year, month, day, hour, min, sec);
	}

	
	private static String getTimeStampOfWorkTimesEnd(Date date, String workTime) {
		String[] timeParts = workTime.split(":");
		if(timeParts.length < 1){
			return "";	//TODO log
		}
		try{
			date.setHours( date.getHours() + Integer.valueOf(timeParts[0]) );
			if(timeParts.length >= 2)
				date.setMinutes( date.getMinutes() + Integer.valueOf(timeParts[1]) );
		}catch(NumberFormatException nfe){
			return ""; //TODO log
		}
		SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm:ss"); // TODO: change this to a preference based one..
		return sdfDate.format(date);
	}
	
	
	public void showMsg(String str) {
		Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		switch (id) {
		
			case R.id.action_new:
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.msg_new_log_toast), Toast.LENGTH_SHORT).show();
				createNewLog();
				return true;
				
			case R.id.action_send_email:
				sendCurrentLogViaEmail();
				return true;
				
			case R.id.action_settings:
				showSettings();
				return true;
				
			case R.id.action_clear_empty_lines:
				clearEmptyLines();
				return true;
	
			default:
				break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	
	private void clearEmptyLines() {
		
		// get current content
		String content = logArea.getText().toString();
		
		// split content to lines
		String[] lines = content.split("\n");
		
		// get content without empty lines
		content = "";
		for(int i=0; i<lines.length; i++){
			if( !lines[i].equals("") ){
				content += lines[i] + "\n";
			}
		}
		
		//load back cleared content
		logArea.setText(content);
		
	}


	private void showSettings() {
    	
    	startActivity(new Intent(TimeLoggerApp.this, MyPreferenceActivity.class));
    	
    }

	
	private String cutOffLastLB(String str) {
	    if (str.length() > 0 && str.charAt(str.length()-1)=='\n') {
	      str = str.substring(0, str.length()-1);
	    }
	    return str;
	}
	
	
	public void showDialogForExtraInfo(final int dlgType) {
		
		dialogRes = "";
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		// Set up the input
		final EditText input = new EditText(this);
		
		switch (dlgType) {
		case TimeLoggerApp.PARKING:
			builder.setTitle(getResources().getString(R.string.dialog_title_parking));
			// Specify the type of input expected;
			input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
			break;

		default:
			builder.setTitle(getResources().getString(R.string.dialog_title_default));
			// Specify the type of input expected;
			input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
			break;
		}
		builder.setView(input);

		/*
		 *  Set up the buttons
		 */
		builder.setPositiveButton(getResources().getString(R.string.dialog_btn_positive), new DialogInterface.OnClickListener() { 
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	dialogRes = input.getText().toString();
		    	if(dlgType == TimeLoggerApp.PARKING){
		    		logArea.setText( cutOffLastLB(logArea.getText().toString()) + (" (" + dialogRes + getResources().getString(R.string.dialog_append_fuel_quantity_unit)+")\n") );
		    	}else{
		    		logArea.setText( cutOffLastLB(logArea.getText().toString()) + (SEPARATOR_AFTER_TIMESTAMP + dialogRes) + "\n");
		    	}
		    	
		    	// start other app when stop driving
				if( myPreferences.needToStartOtherAppWhenStopDriving() && (lastUsedFunction == PARKING) )
					startMessageApp();
		    }
		});
		
		
		builder.setNegativeButton(getResources().getString(R.string.dialog_btn_negative), new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.cancel();

		    	// start other app when stop driving
				if(myPreferences.needToStartOtherAppWhenStopDriving() && (lastUsedFunction == PARKING) )
					startMessageApp();
		    }
		});

		builder.show();
	}
	
	
	public void startDrivingApp() {
		Intent launchIntent = getPackageManager().getLaunchIntentForPackage( myPreferences.getDrivingAppPackage() );
		startActivity(launchIntent);
	}
	
	
	//TODO
	public void startMessageApp() {
		if( isInTimeRangeOfMessageApp() ){
			Intent launchIntent = getPackageManager().getLaunchIntentForPackage( myPreferences.getMessageAppPackage() );
			startActivity(launchIntent);
		}
	}
	
	
	//TODO: USE THIS
	public boolean isInTimeRangeOfMessageApp(){

		if( !myPreferences.needToUseTimeRangeForMessageApp() ){
			return true;
		}
		
		Date now = new Date();
		Date before = myPreferences.getTimeBeforeNeedToRunMessageApp();
		Date after = myPreferences.getTimeBeforeNeedToRunMessageApp();
		
		after.setYear(now.getYear());
		after.setMonth(now.getMonth());
		after.setDate(now.getDay());
		
		after.setYear(now.getYear());
		after.setMonth(now.getMonth());
		after.setDate(now.getDay());
		
		return (now.before(before) && now.after(after));
		
	}
	
	
}
