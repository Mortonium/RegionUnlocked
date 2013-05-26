package com.hack.regionunlocked;

import android.os.AsyncTask;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.Reader;
import java.net.URL;
import java.net.HttpURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class GameStatus extends AsyncTask<Void, Void, Boolean> {

	private String upcCode;
	private String nameUPCDatabase;
	private String nameScandit;
	private List<RegionSupportStatusSet> support;
	private String scandItKey = "key=-rdsomoapvSlt5JjXpPNr0WfBpw-H7f5R9JJMnIbw5J";
	private boolean found = false;
	
	private GameStatusCompleteListener listener;
	private boolean success = false;
	
	private Exception ex;

	public GameStatus(String upcCode, GameStatusCompleteListener listener) {
		this.upcCode = upcCode;
		this.listener = listener;
	}
	public boolean wasSuccessful() {
		return success;
	}
	
	@Override
	protected Boolean doInBackground(Void... lolwut) {
		  
		// params comes from the execute() call: params[0] is the url.
		try {
			this.success = false;
			if (upcCode.equals("")) {
				throw new GameStatusException("No UPC code specified");
			} else {

				this.nameUPCDatabase = getUPCDatabaseName(upcCode);
				this.nameScandit = getScandItName(upcCode);
				//found = checkNames();
				found = true;
				
				if (found == true) {
					return checkStatusWikia();
				}
				
				throw new GameStatusException("Name not found");
				
			}
		} catch (Exception e) {
			this.ex = e;
			this.success = false;
			return false;
		}
	}
	// onPostExecute displays the results of the AsyncTask.
	@Override
	protected void onPostExecute(Boolean success) {
		listener.setString("test end");
		if (success)
			listener.onGameStatusComplete();
		else{
			if (ex != null) listener.onGameStatusError(ex);
			else listener.onGameStatusError(new GameStatusException("Unknown Error"));
		}
		
	}
	private String downloadUrl(String myurl) throws GameStatusException {
		InputStream is = null;
		// Only display the first 500 characters of the retrieved
		// web page content.
		int len = 500;
			
		try {
			URL url = new URL(myurl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(30000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("GET");
			//conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:5.0) Gecko/20100101 Firefox/5.0");
			conn.setDoInput(true);
			// Starts the query
			conn.connect();
			int response = conn.getResponseCode();
			is = conn.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					is));
			String content = "";
			String line;
			while ((line = br.readLine()) != null) {
				content += line;
			}
			return content;
			/*
			// Convert the InputStream into a string
			String contentAsString = readIt(is, len);
			return contentAsString;
			*/
			
		// Makes sure that the InputStream is closed after the app is
		// finished using it.
		} catch (Exception ex) {
			throw new GameStatusException("downloadUrl fail: " + (ex.getMessage() == null ? "" : ex.getMessage()));
		} finally {
			if (is != null) {
				try{
					is.close();
				}catch (Exception e){
					throw new GameStatusException("failed closing input stream :(");
				}
			} 
		}
	}
	
	private String getScandItName(String upcCode) throws Exception {
		String url = "https://api.scandit.com/v2/products/" + upcCode + "?" + scandItKey;

		// String content = getWebsiteContent(url);
		String content = downloadUrl(url);

		try{
			if (content.contains("name")) {
				String strip = content.substring(18);
				int check = strip.indexOf("\"");
				strip = strip.substring(0, check);
				check = strip.indexOf("0");
				strip = strip.substring(0, check - 9);
				return strip;
			} else {
				return "";
			}
		}catch(Exception e){
			throw new GameStatusException("Couldn't find Game name in scandit");
		}
	}

	private String getUPCDatabaseName(String upcCode) throws Exception {
		// 885370201215 = Gears of War 3
		// String content = getWebsiteContent("http://www.upcdatabase.com/item/" + upcCode);
		String content = downloadUrl("http://www.upcdatabase.com/item/" + upcCode);
		
		try {
			String regex = "<td>Description</td><td></td><td>(.*?)</td>";

			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(content);

			if (!matcher.find())
				throw new GameStatusException("getUPCDatabaseName fail (No regex match).");
			return matcher.group(1);

		} catch (Exception ex) {
			throw new GameStatusException("getUPCDatabaseName fail:\n\n" + ex.getMessage());
		}

	}

	private boolean checkNames() throws GameStatusException {
		if ((nameScandit.toLowerCase().contains(nameUPCDatabase.toLowerCase())) || (nameUPCDatabase.toLowerCase().contains(nameScandit.toLowerCase())))
			return true;
		else
			return false;
	}

	private boolean checkStatusWikia() throws GameStatusException {

		if (!this.nameScandit.equals("")) {
			String content = downloadUrl("http://gaming.wikia.com/wiki/Region_Free_Xbox_360_Games");
			String regex = "(?i)<td>[\\s]*<a href=\"[^\"]*\"[^>]*>"
					+ this.nameScandit
					+ "</a>[\\s]*</td>[\\s]*"
					+ "<td>[\\s]*([\\w]+)[\\s]*</td>[\\s]*"
					+ // version
					"<td[^>]*>[\\s]*([\\w?]+)[\\s]*</td>[\\s]*"
					+ // NTSC/J compatibility
					"<td[^>]*>[\\s]*([\\w?]+)[\\s]*</td>[\\s]*"
					+ // NTSC/U compatibility
					"<td[^>]*>[\\s]*([\\w?]+)[\\s]*</td>"; // PAL
																				// compatibility

			//regex = "<td>[\\s]*<a href=\"[^\"]*\"[^>]*>Halo 4</a>[\\s]*</td>[\\s]*<td>[\\s]*([\\w]+)[\\s]*</td>[\\s]*<td[^>]*>[\\s]*([\\w]+)[\\s]*</td>[\\s]*<td[^>]*>[\\s]*([\\w]+)[\\s]*</td>[\\s]*<td[^>]*>[\\s]*([\\w]+)[\\s]*</td>";
			
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(content);
			
			System.out.println("|MARK1|");
			
			boolean matchFound = false;
			while (matcher.find()) {
				
				matchFound = true;
				success = true;

				GameRegion region = GameRegion.UNKNOWN;
				if (matcher.group(1).equals("NTSC/J"))
					region = GameRegion.NTSC_J;
				if (matcher.group(1).equals("NTSC/U")
						|| matcher.group(1).equals("US"))
					region = GameRegion.NTSC_U;
				if (matcher.group(1).equals("PAL"))
					region = GameRegion.PAL;
				if (region != GameRegion.UNKNOWN) {
					RegionSupportStatusSet set = new RegionSupportStatusSet(
							region);

					if (matcher.group(2).equals("Yes"))
						set.supportStatuses.put(GameRegion.NTSC_J,
								RegionSupportStatus.Yes);
					if (matcher.group(2).equals("No"))
						set.supportStatuses.put(GameRegion.NTSC_J,
								RegionSupportStatus.No);
					if (matcher.group(2).equals("?"))
						set.supportStatuses.put(GameRegion.NTSC_J,
								RegionSupportStatus.Unknown);

					if (matcher.group(3).equals("Yes"))
						set.supportStatuses.put(GameRegion.NTSC_U,
								RegionSupportStatus.Yes);
					if (matcher.group(3).equals("No"))
						set.supportStatuses.put(GameRegion.NTSC_U,
								RegionSupportStatus.No);
					if (matcher.group(3).equals("?"))
						set.supportStatuses.put(GameRegion.NTSC_U,
								RegionSupportStatus.Unknown);

					if (matcher.group(4).equals("Yes"))
						set.supportStatuses.put(GameRegion.PAL,
								RegionSupportStatus.Yes);
					if (matcher.group(4).equals("No"))
						set.supportStatuses.put(GameRegion.PAL,
								RegionSupportStatus.No);
					if (matcher.group(4).equals("?"))
						set.supportStatuses.put(GameRegion.PAL,
								RegionSupportStatus.Unknown);

					support.add(set);
				}
			}
			System.out.println("|MARK2|");
			if (matchFound)
				return true;
			throw new GameStatusException("No Match found for status check");
			
		} else {
			throw new GameStatusException("No name for status checking");
		}
	}

	public String getSupportAsText() {
		if ((support == null) || (support.size() == 0)) {
			return "No Results";
		} else {
			String result = "";
			for (int i = 0; i < support.size(); i++) {
				result += "Version: "
						+ GameRegionToString(support.get(i).gameRegion) + "\n";
				result += "\tNTSC/J: "
						+ RegionSupportStatusToString(support.get(i).supportStatuses
								.get(GameRegion.NTSC_J)) + "\n";
				result += "\tNTSC/U: "
						+ RegionSupportStatusToString(support.get(i).supportStatuses
								.get(GameRegion.NTSC_U)) + "\n";
				result += "\tPAL:    "
						+ RegionSupportStatusToString(support.get(i).supportStatuses
								.get(GameRegion.PAL)) + "\n";
				result += "\n";
			}
			result += "\n";
			return result;
		}
	}

	public List<RegionSupportStatusSet> getSupport() {
		return support;
	}

	private String GameRegionToString(GameRegion region) {
		switch (region) {
		case NTSC_J:
			return "NTSC/J";
		case NTSC_U:
			return "NTSC/U";
		case PAL:
			return "PAL";
		default:
			return "Unknown";
		}
	}

	private String RegionSupportStatusToString(RegionSupportStatus status) {
		switch (status) {
		case Yes:
			return "Yes";
		case No:
			return "No";
		default:
			return "Unknown";
		}
	}
	
}