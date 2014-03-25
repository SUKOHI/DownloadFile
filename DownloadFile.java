package com.sukohi.lib;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.os.AsyncTask;

public class DownloadFile extends AsyncTask<String, Integer, String> {

	public static final int STATE_YET = 0;
	public static final int STATE_START = 1;
	public static final int STATE_PROGRESS = 2;
	public static final int STATE_END = 3;
	public static final int RESULT_CODE_FAILED = -1;
	public static final int RESULT_CODE_COMPLETE = 1;
	private int state = STATE_YET;
	private int resultCode = 0;
	private int readLength = 0;
	private int readTotal = 0;
	private int readPercentage = 0;
	private DownloadFileCallback callback;
	
	public void setCallback(DownloadFileCallback callback) {
		
		this.callback = callback;
		
	}
	
	public void download(String downloadUrl, String savePath) {
		
		execute(downloadUrl, savePath);
		
	}
	
	public int getReadTotal() {
		
		return readTotal;
		
	}
	
	public int getReadPercentage() {
		
		return readPercentage;
		
	}
	
	public int getReadLength() {
		
		return readLength;
		
	}
	
	public int getState() {
		
		return state;
		
	}
	
	public int getResultCode() {
		
		return resultCode;
		
	}
	
    @Override
    protected void onPreExecute() {
    	
        super.onPreExecute();
        
    }

    @Override
    protected String doInBackground(String... params) {

    	readLength = 0;
    	readTotal = 0;
    	readPercentage = 0;
    	state = STATE_START;
    	
    	if(callback != null) {
    		
    		callback.getResult(this);
    		
    	}
        
        try {
        	
            URL url = new URL(params[0]);
            String savePath = params[1];
            URLConnection conection = url.openConnection();
            conection.connect();

            readLength = conection.getContentLength();
            InputStream input = new BufferedInputStream(url.openStream(), 8192);
            OutputStream output = new FileOutputStream(savePath);

            byte data[] = new byte[1024];
            int count;
        	state = STATE_PROGRESS;

            while ((count = input.read(data)) != -1) {
            	
                readTotal += count;
                
                if(readLength > 0) {
                	
                	readPercentage = (int) ((readTotal * 100) / readLength);
                    
                } else {
                	
                	readPercentage = -1;
                	
                }
                
                publishProgress();
                output.write(data, 0, count);
                
            }

            output.flush();
            output.close();
            input.close();

            resultCode = RESULT_CODE_COMPLETE;
            
        } catch (Exception e) {
        	
        	resultCode = RESULT_CODE_FAILED;
        	
        }

        return null;
    }
    
    protected void onProgressUpdate(Integer... values) {
    	
    	if(callback != null) {
    		
    		callback.getResult(this);
    		
    	}
    	
    }

    @Override
    protected void onPostExecute(String file_url) {

    	state = STATE_END;
    	
    	if(callback != null) {
    		
    		callback.getResult(this);
    		
    	}
    	
    }
    
    public static class DownloadFileCallback {
    	
    	public void getResult(DownloadFile downloadFile) {}
        
    }

}
/*** Example

	String downloadUrl = "http://example.com/download.txt";
	String savePath = getFilesDir().getAbsolutePath() +"/save.txt";

	DownloadFile downloadFile = new DownloadFile();
	downloadFile.setCallback(new DownloadFileCallback(){
		
		@Override
		public void getResult(DownloadFile downloadFile) {
			
			switch (downloadFile.getState()) {

			case DownloadFile.STATE_START:
				
				break;
				
			case DownloadFile.STATE_PROGRESS:

				int readTotal = downloadFile.getReadTotal();
				int fileLength = downloadFile.getReadLength();			// If no set value, -1
				int readPercentage = downloadFile.getReadPercentage();	// If no set value, -1
				
				break;

			case DownloadFile.STATE_END:
				
				if(downloadFile.getResultCode() == DownloadFile.RESULT_CODE_COMPLETE) {
					
					// Success
					
				} else {
					
					// Fail..
					
				}
				
				break;
				
			}
			
		}
		
	});
	downloadFile.download(downloadUrl, savePath);

***/
