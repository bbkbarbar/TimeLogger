package hu.barbar.timelogger.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.os.Environment;
import android.widget.Toast;
import hu.barbar.timelogger.TimeLoggerApp;
import hu.barbar.timelogger.R;

public class FileHandler {

	public static final String path = "aaa_TimeLogs";
	public static final String fileName = "TimeLog";
	public static final String fileExt = "txt";
	public static String TIMESTAMP_FORMAT = "_yyyy_MM_dd__HH_mm_ss";	//"yyyy_MM_dd__HH_mm_ss"
	
	public static final int RESULT_OK = 0;
	public static final int RESULT_ERROR_WRITE_EMPTY = -1;
	public static final int RESULT_ERROR_WRITE_NULL_CONTENT = -2;
	public static final int RESULT_EXCEPTION_WHILE_TRY_TO_WRITE_FILE = -3;
	public static final int RESULT_EXCEPTION_WHILE_TRY_TO_LOAD_FILE = -13;
	
	private TimeLoggerApp myParent = null;
	private File extStoreage = null;
	
	
	public FileHandler(TimeLoggerApp parent){
		
		myParent = parent;
		extStoreage = Environment.getExternalStorageDirectory();
		
	}
	
	
	public int saveFile(ArrayList<String> lines, boolean needToAttachTimeStamp){
		
		if(lines == null){
			return FileHandler.RESULT_ERROR_WRITE_NULL_CONTENT;
		}
		if(lines.size() == 0){
			return FileHandler.RESULT_ERROR_WRITE_EMPTY;
		}
		
		try {

			File fileToSave = new File(generateFilepath(needToAttachTimeStamp));
			
			if( !fileToSave.exists() || !fileToSave.canRead() ){
				fileToSave.createNewFile();
			}

			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(fileToSave), "UTF-8");
			
			for(int i=0; i<lines.size(); i++){
				osw.write(lines.get(i) + "\n");
			}

			osw.flush();
			osw.close();
            
        } catch (Exception e) {
        	return FileHandler.RESULT_EXCEPTION_WHILE_TRY_TO_WRITE_FILE;
        }
		
		return 0;
	}

	
	public String generateFilepath(boolean needToAttachTimeStamp) {
		return extStoreage.getPath() + "/" + fileName + (needToAttachTimeStamp?(getTimeStamp() + "."):".") + fileExt;
	}


	public static String getTimeStamp() {
	    SimpleDateFormat sdfDate = new SimpleDateFormat(FileHandler.TIMESTAMP_FORMAT);//dd/MM/yyyy
	    Date now = new Date();
	    String strDate = sdfDate.format(now);
	    return strDate;
	}
	
	
	public ArrayList<String> loadFile() {
		
		ArrayList<String> res = new ArrayList<String>();
		
		try {
			String aDataRow = "";
			File myFile = new File(generateFilepath(false));
			if(!myFile.canRead()){
				Toast.makeText(
						myParent.getBaseContext(), 
						myParent.getResources().getString(R.string.msg_file_can_not_read_toast),
						Toast.LENGTH_SHORT
				).show();
				return null;
			}
			FileInputStream fIn = new FileInputStream(myFile);
			BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
			while ((aDataRow = myReader.readLine()) != null) {
				res.add(aDataRow);
			}
			
			
			
			myReader.close();

		} catch (Exception e) {
			Toast.makeText(myParent.getBaseContext(), e.getMessage(),
					Toast.LENGTH_LONG).show();
			return null;
		}
		
		return res;
		
	}

	
	public boolean isFileMissing() {
		File myFile = new File(generateFilepath(false));
		return (!myFile.canRead());
	}
	
	public String showExtStoragePath() {
		return extStoreage.getPath();
	}
	
	
}
