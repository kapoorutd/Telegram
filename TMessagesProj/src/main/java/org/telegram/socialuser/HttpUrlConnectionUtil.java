package org.telegram.socialuser;

import android.os.Build;

import org.telegram.socialuser.model.CZResponse;
import org.telegram.socialuser.model.CustomHttpParams;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * This class handle server request and response.
 * @author craterzone
 *
 */
public class HttpUrlConnectionUtil {
	
	
public static final String TAG = HttpUrlConnectionUtil.class.getName();
	
	public static final String HEADER_ACCEPT = "Accept";
	public static final String CONTENT_TYPE = "Content-type";
	public static final String PUT = "PUT";
	public static final String POST = "POST";
	public static final String GET = "GET";
	public static final int TIME_OUT = 2 * 60 * 1000;
	private static final int _4KB = 4 * 1024;
	
	
	/**
	 * Request to server using POST method
	 * @param urlString
	 * @param body
	 * @param contentType
	 * @param acceptType
	 * @param httpParams :can be null
	 * @return
	 */
	public static CZResponse post(String urlString, String body, String contentType, String acceptType, ArrayList<CustomHttpParams> httpParams) {
		return post(urlString, body, contentType, acceptType, httpParams, true);
	}
	
	private static CZResponse post(String urlString, String body, String contentType,
								   String acceptType, ArrayList<CustomHttpParams> httpParams, boolean retryOnEOF) {
		
		HttpURLConnection conn = null;
		try {
			URL url = new URL(urlString);
			System.setProperty("http.keepAlive", "false");
			if(checkHTTPS(urlString)){
				conn = (HttpsURLConnection) url.openConnection();
			} else{
				conn = (HttpURLConnection) url.openConnection();
			}
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setRequestMethod(POST);
			conn.setRequestProperty(CONTENT_TYPE,contentType);  
			conn.setRequestProperty(HEADER_ACCEPT,acceptType); 
			if(httpParams != null && httpParams.size() >0){
				for(CustomHttpParams params : httpParams){
					conn.setRequestProperty(params.getKey(), params.getValue());
				}
			}
			if ( Build.VERSION.SDK_INT > 13) {
				conn.setRequestProperty("Connection", "close");
			 }
			conn.setDoInput(true);
			conn.setDoOutput(true);
			body = (body != null) ? body : "";
			byte[] outputInBytes = body.getBytes("UTF-8");
			OutputStream os = conn.getOutputStream();
			os.write(outputInBytes);
			BufferedWriter writer = new BufferedWriter(
			        new OutputStreamWriter(os, "UTF-8"));
			
			writer.flush();
			writer.close();
			os.close();
			conn.connect();
			int statusCode = conn.getResponseCode();
			Logger.d(TAG, "Status Code is: " + statusCode);
	        switch (statusCode) {
	            case 200:
	            	String response = new String(readFullyBytes(conn.getInputStream(), 2 * _4KB));
	            	Logger.d(TAG, "Response String is : " + response);
	            	return new CZResponse(statusCode, response);
	            default:
	            	return new CZResponse(statusCode, "");
	            		
	        }
		} catch (Exception e) {
			Logger.e(TAG, "Error in getting response", e);
			if(retryOnEOF) {
				Logger.e(TAG, "EOF Exception while making request. Retrying ..");
				return put(urlString, body, contentType, acceptType, httpParams, false);
			} else {
				Logger.e(TAG, "EOF Exception while making request.", e);
			}
		} finally {
			if(conn!=null)
				conn.disconnect();
		}
		return null; 
	}
	
	/**
	 * Request to server using PUT method
	 * @param urlString
	 * @param body
	 * @param contentType
	 * @param acceptType
	 * @param httpParams :can be null
	 * @return
	 */
	public static CZResponse put(String urlString, String body, String contentType, String acceptType, ArrayList<CustomHttpParams> httpParams) {
		return put(urlString, body, contentType, acceptType, httpParams, true);
	}
	private static CZResponse put(String urlString, String body, String contentType,
								  String acceptType, ArrayList<CustomHttpParams> httpParams, boolean retryOnEOF) {
		HttpURLConnection conn = null;
		try { 
			URL url = new URL(urlString);
			System.setProperty("http.keepAlive", "false");
			if(checkHTTPS(urlString)){
				conn = (HttpsURLConnection) url.openConnection();
			} else{
				conn = (HttpURLConnection) url.openConnection();
			}
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setRequestMethod(PUT);
			conn.setRequestProperty(CONTENT_TYPE,contentType);  
			conn.setRequestProperty(HEADER_ACCEPT,acceptType);  
			if(httpParams != null && httpParams.size() >0){
				for(CustomHttpParams params : httpParams){
					conn.setRequestProperty(params.getKey(), params.getValue());
				}
			}
			if ( Build.VERSION.SDK_INT > 13) {
				conn.setRequestProperty("Connection", "close");
			 }
			conn.setDoInput(true);
			conn.setDoOutput(true);
			body = (body != null) ? body : "";
			byte[] outputInBytes = body.getBytes("UTF-8");
			OutputStream os = conn.getOutputStream();
			os.write(outputInBytes);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
			writer.flush();
			writer.close();
			os.close();
			conn.connect();
			int statusCode = conn.getResponseCode();
			Logger.d(TAG, "Status code is : " + statusCode);
	        switch (statusCode) {
	            case 200:
	            	String response = new String(readFullyBytes(conn.getInputStream(), 2 * _4KB));
	            	Logger.d(TAG, "Response String is : " + response);
	            	return new CZResponse(statusCode, response);
	            default:
	            	return new CZResponse(statusCode, "");
	        }
		
		}catch (Exception e) {
			Logger.e(TAG, "Error in getting response", e);
			if(retryOnEOF) {
				Logger.e(TAG, "EOF Exception while making request. Retrying ..");
				return put(urlString, body, contentType, acceptType, httpParams, false);
			} else {
				Logger.e(TAG, "EOF Exception while making request.", e);
			}
		} finally {
			if(conn!=null)
				conn.disconnect();
		}
		return null; 
	}
	
	
	/**
	 * Request to server using GET method
	 * @param urlString
	 * @param acceptType
	 * @return
	 */
	public static CZResponse get(String urlString, String acceptType) {
		HttpURLConnection conn = null;
		try {
			URL url = new URL(urlString);
			System.setProperty("http.keepAlive", "false");
			if(checkHTTPS(urlString)){
				conn = (HttpsURLConnection) url.openConnection();
			} else{
				conn = (HttpURLConnection) url.openConnection();
			}
			conn.setRequestMethod(GET);
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.connect();
			int statusCode = conn.getResponseCode();
			Logger.d(TAG, "Status is" + statusCode);
	        switch (statusCode) {
	            case 200:
	            	String response = new String(readFullyBytes(conn.getInputStream(), 2 * _4KB));
	            	Logger.d(TAG, "Response String is : " + response);
	            	return new CZResponse(statusCode, response);
	            default:
	            	return new CZResponse(statusCode, "");
	        }
		} catch (Exception e) {
			Logger.e(TAG, "Error in getting response", e);
		} finally {
			if(conn!=null)
				conn.disconnect();
		}
		return null; 
	}
	
	
	/**
	 * Request to server using GET method accept custom parameter
	 * @param urlString
	 * @param acceptType
	 * @return
	 */
	public static CZResponse get(String urlString, String acceptType, ArrayList<CustomHttpParams> httpParams) {
		HttpURLConnection conn = null;
		try {
			URL url = new URL(urlString);
			if(checkHTTPS(urlString)){
				conn = (HttpsURLConnection) url.openConnection();
			} else{
				conn = (HttpURLConnection) url.openConnection();
			}
			conn.setRequestMethod(GET);
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setRequestProperty(HEADER_ACCEPT,acceptType); 
			if(httpParams != null && httpParams.size() >0){
				for(CustomHttpParams params : httpParams){
					conn.setRequestProperty(params.getKey(), params.getValue());
				}
			}
			conn.connect();
			int statusCode = conn.getResponseCode();
			Logger.d(TAG, "Status is" + statusCode);
	        switch (statusCode) {
	            case 200:
	            	String response = new String(readFullyBytes(conn.getInputStream(), 2 * _4KB));
	            	Logger.d(TAG, "Response String is : " + response);
	            	return new CZResponse(statusCode, response);
	            default:
	            	return new CZResponse(statusCode, "");
	        }
		} catch (Exception e) {
			Logger.e(TAG, "Error in getting response", e);
		} finally {
			if(conn!=null)
				conn.disconnect();
		}
		return null; 
	}
	
	
	public static String convertStreamToString(InputStream is) {
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			Logger.e(TAG, "Error in converting stream to string", e);
			return null;
		} catch (IllegalStateException e) {
			Logger.e(TAG, "Error in converting stream to string", e);
			return null;
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				Logger.e(TAG, "Error in closing Input Stream", e);
			}
		}
		return sb.toString();
	}
	
	public static boolean checkHTTPS(String url){
		if(url.contains("https")){
			return true;
		}
		return false;
		
	}
	
	  /**
     * Read bytes from InputStream efficiently. All data will be read from
     * stream. This method return the bytes or null. This method will not close
     * the stream.
     */
    public static byte[] readFullyBytes(InputStream is, int blockSize) {
        byte[] bytes = null;
        if (is != null) {
            try {
                int readed = 0;
                byte[] buffer = new byte[blockSize];
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                while ((readed = is.read(buffer)) >= 0) {
                    bos.write(buffer, 0, readed);
                }
                bos.flush();
                bytes = bos.toByteArray();
            } catch (IOException e) {
                Logger.e(TAG, " : readFullyBytes: ", e);
            }
        }
        return bytes;
    }
  
    /**
     * This method upload mutipart file.
     * @param urlString
     * @param filePath
     * @return
     */
    public static CZResponse postMultiPartFileUpload(String urlString, String filePath) {

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(filePath);
         try {
	         URL url = new URL(urlString);
	 		 if(checkHTTPS(urlString)){
	 			conn = (HttpsURLConnection) url.openConnection();
	 		 } else{
	 			conn = (HttpURLConnection) url.openConnection();
	 		 }
             FileInputStream fileInputStream = new FileInputStream(sourceFile);
             conn.setDoInput(true); // Allow Inputs
             conn.setDoOutput(true); // Allow Outputs
             conn.setUseCaches(false); // Don't use a Cached Copy
             conn.setRequestMethod("POST");
             conn.setRequestProperty("Connection", "Keep-Alive");
             conn.setRequestProperty("ENCTYPE", "multipart/form-data");
             conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
             conn.setRequestProperty("file", filePath);
             dos = new DataOutputStream(conn.getOutputStream());
             dos.writeBytes(twoHyphens + boundary + lineEnd);
             dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\""
                                           + filePath + "\"" + lineEnd);
             dos.writeBytes(lineEnd);
             // create a buffer of  maximum size
             bytesAvailable = fileInputStream.available();
             bufferSize = Math.min(bytesAvailable, maxBufferSize);
             buffer = new byte[bufferSize];
             // read file and write it into form...
             bytesRead = fileInputStream.read(buffer, 0, bufferSize); 
             while (bytesRead > 0) {
               dos.write(buffer, 0, bufferSize);
               bytesAvailable = fileInputStream.available();
               bufferSize = Math.min(bytesAvailable, maxBufferSize);
               bytesRead = fileInputStream.read(buffer, 0, bufferSize);  
              }
             dos.writeBytes(lineEnd);
             dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
             fileInputStream.close();
             dos.flush();
             dos.close();
             int  statusCode = conn.getResponseCode();
             Logger.d(TAG, "Status is" + statusCode);
 	         switch (statusCode) {
 	            case 200:
 	            	String response = new String(readFullyBytes(conn.getInputStream(), 2 * _4KB));
 	            	Logger.d(TAG, "Response String is : " + response);
 	            	return new CZResponse(statusCode, response);
 	            default:
 	            	return new CZResponse(statusCode, "");
 	            }
         } catch (MalformedURLException ex) {
          Logger.e(TAG, "error: " + ex.getMessage(), ex); 
        } catch (Exception e) {
            Logger.e(TAG, "Exception : "  + e.getMessage(), e); 
        }
        return null;
  }
    /**
	 * Request to server using GET method accept custom parameter
	 * @param urlString
	 * @param acceptType
	 * @return
	 */
	public static CZResponse get(String urlString, String acceptType, String contentType, ArrayList<CustomHttpParams> httpParams) {
		HttpURLConnection conn = null;
		try {
			URL url = new URL(urlString);
			if(checkHTTPS(urlString)){
				conn = (HttpsURLConnection) url.openConnection();
			} else{
				conn = (HttpURLConnection) url.openConnection();
			}
			conn.setRequestMethod(GET);
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setRequestProperty(CONTENT_TYPE,contentType);  
			conn.setRequestProperty(HEADER_ACCEPT,acceptType); 
			if(httpParams != null && httpParams.size() >0){
				for(CustomHttpParams params : httpParams){
					conn.setRequestProperty(params.getKey(), params.getValue());
				}
			}
			conn.connect();
			int statusCode = conn.getResponseCode();
			Logger.d(TAG, "Status is" + statusCode);
	        switch (statusCode) {
	            case 200:
	            	String response = new String(readFullyBytes(conn.getInputStream(), 2 * _4KB));
	            	Logger.d(TAG, "Response String is : " + response);
	            	return new CZResponse(statusCode, response);
	            default:
	            	return new CZResponse(statusCode, "");
	        }
		} catch (Exception e) {
			Logger.e(TAG, "Error in getting response", e);
		} finally {
			if(conn!=null)
				conn.disconnect();
		}
		return null; 
	}
    
       
}
