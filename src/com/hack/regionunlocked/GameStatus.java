package com.hack.regionunlocked;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.Reader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.File;

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

	private String folderName = "RegionUnlocked";
	private File folder;
	
	private String scanCacheName = "scanCache.csv";
	private File scanCache;
	
	public GameStatus(String upcCode, GameStatusCompleteListener listener) {
		this.upcCode = upcCode;
		this.listener = listener;
		support = new ArrayList<RegionSupportStatusSet>();
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
				content += line + "\n";
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
		String regionSetting = GlobalVariables.getRegionString();
		regionSetting = regionSetting.replace("/C","").replace("-","_");
		boolean userSystemDefined = false;
		if (regionSetting != null){
			userSystemDefined = true;
		}

		if (!this.nameScandit.equals("")) {
			String query = "select `region`,`ntsc_j`,`ntsc_u`,`pal` from `swdata` where \"" + nameScandit.toLowerCase() + "\"=`title` limit 3";
			System.out.println("Start query:" + query);
			query = Uri.encode(query);
			System.out.println("Coded query:" + query);
			String csvSource = "https://api.scraperwiki.com/api/1.0/datastore/sqlite?format=csv&name=regionunlocked&query=" + query;
			System.out.println("Full query:" + csvSource);
			String csv = downloadUrl(csvSource);
			String[] csvLines = csv.split("\n");
			System.out.println("Sample: " + csvLines[1] + " and total result count: " + csvLines.length);
			for (int i = 1; i < csvLines.length; i++){
				System.out.println("HI!");
				String[] values = csvLines[i].split(",");
				GameRegion region = GameRegion.UNKNOWN;
				try{
					region = GameRegion.valueOf(values[0].replace("/","_").toUpperCase());
				}catch (Exception e){
					//Do nothing, handled by assigning unknown above.
				}
				
				RegionSupportStatusSet set = new RegionSupportStatusSet(
						region);
				if (values[1] != "?")
					set.supportStatuses.put(GameRegion.NTSC_J, RegionSupportStatus.valueOf(values[1]));
				else set.supportStatuses.put(GameRegion.NTSC_J, RegionSupportStatus.Unknown);
				if (values[2] != "?")
					set.supportStatuses.put(GameRegion.NTSC_U, RegionSupportStatus.valueOf(values[2]));
				else set.supportStatuses.put(GameRegion.NTSC_U, RegionSupportStatus.Unknown);
				if (values[3] != "?")
					set.supportStatuses.put(GameRegion.PAL, RegionSupportStatus.valueOf(values[3]));
				else set.supportStatuses.put(GameRegion.PAL, RegionSupportStatus.Unknown);
				if (userSystemDefined){
					set.supportStatuses.get(GameRegion.valueOf(regionSetting));
				}
				
				support.add(set);
				success = true;
			}
			if (success){
				return true;
			}else throw new GameStatusException("No Match found for status check");
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
	
	private boolean checkStorage() {
		
		String state = Environment.getExternalStorageState();
		
		if (Environment.MEDIA_MOUNTED.equals(state))
			return true;
		else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
			return false;
		else 
			return false;
		
	}
	private boolean makeFile() {
		
		System.out.println("Finding sdCard");
		File sdCard = new File(Environment.getExternalStorageDirectory().toString());
		System.out.println("sdCard: " + sdCard.toString());
		if (sdCard.isDirectory()) {
			System.out.println("sdCard exists");
			folder = new File(sdCard + "/" + folderName);
			System.out.println("folder: " + folder.toString());
			if (!folder.isDirectory()) {
				System.out.println("folder does not exist");
				if (folder.mkdir())
					System.out.println("folder made");
				else
					System.out.println("folder failed to make");
			}
			scanCache = new File(folder + "/" + scanCacheName);
			System.out.println("scanCache: " + scanCache.toString());
			if (!scanCache.isFile()) {
				System.out.println("scanCache does not exist");
				try {
					scanCache.createNewFile();
					System.out.println("scanCache made");
					return true;
				} catch (IOException e) {
					System.out.println("scanCache failed to make");
					e.printStackTrace();
					return false;
				}
			}
			return true;
		}
		return false;
		
	}
	
	/*
	Save order
	PAL
	NTSC/U
	NTSC/J
	*/
	
	public void read(String upcCode) {
		if (!checkStorage()) {
			return;
		} else if (!makeFile()) {
			return;
		} else {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(scanCache));
				String input = reader.readLine();
				while (input != null) {
					if (input.startsWith(upcCode)) {
						String[] splitStrings = input.split(",");
						this.upcCode = upcCode;
						this.nameScandit = splitStrings[1];
						
						GameRegion region = GameRegion.UNKNOWN;
						if (splitStrings[2].equals("NTSC/J"))
							region = GameRegion.NTSC_J;
						if (splitStrings[2].equals("NTSC/U"))
							region = GameRegion.NTSC_U;
						if (splitStrings[2].equals("PAL"))
							region = GameRegion.PAL;
						
						if (region != GameRegion.UNKNOWN) {
							
							RegionSupportStatusSet set = new RegionSupportStatusSet(
									region);

							if (splitStrings[3].equals("Yes"))
								set.supportStatuses.put(GameRegion.PAL,
										RegionSupportStatus.Yes);
							if (splitStrings[3].equals("No"))
								set.supportStatuses.put(GameRegion.PAL,
										RegionSupportStatus.No);
							if (splitStrings[3].equals("?"))
								set.supportStatuses.put(GameRegion.PAL,
										RegionSupportStatus.Unknown);

							if (splitStrings[4].equals("Yes"))
								set.supportStatuses.put(GameRegion.NTSC_U,
										RegionSupportStatus.Yes);
							if (splitStrings[4].equals("No"))
								set.supportStatuses.put(GameRegion.NTSC_U,
										RegionSupportStatus.No);
							if (splitStrings[4].equals("?"))
								set.supportStatuses.put(GameRegion.NTSC_U,
										RegionSupportStatus.Unknown);

							if (splitStrings[5].equals("Yes"))
								set.supportStatuses.put(GameRegion.NTSC_J,
										RegionSupportStatus.Yes);
							if (splitStrings[5].equals("No"))
								set.supportStatuses.put(GameRegion.NTSC_J,
										RegionSupportStatus.No);
							if (splitStrings[5].equals("?"))
								set.supportStatuses.put(GameRegion.NTSC_J,
										RegionSupportStatus.Unknown);

							support.add(set);
							
						}
						
					}
					
					input = reader.readLine();
				}
				reader.close();
			} catch (Exception ex) {
				
			}
		}
	}
	public void write() {
		if (!checkStorage()) {
			System.out.println("Storage is accessible");
			return;
		} else if (!makeFile()) {
			System.out.println("File made");
			return;
		} else {
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(scanCache, true));
				System.out.println("Writer made");
				for (int i = 0; i < support.size(); i++) {
					writer.write(upcCode + ",\"" + nameScandit + "\",");
					writer.write(GameRegionToString(support.get(i).gameRegion) + ",");
					writer.write(RegionSupportStatusToString(support.get(i).supportStatuses.get(GameRegion.PAL)) + ",");
					writer.write(RegionSupportStatusToString(support.get(i).supportStatuses.get(GameRegion.NTSC_U)) + ",");
					writer.write(RegionSupportStatusToString(support.get(i).supportStatuses.get(GameRegion.NTSC_J)) + "\n");
				}
				writer.close();
			} catch (Exception ex) {
				
			}
		}
	}
	public String getGameTitle(){
		return nameScandit;
	}
	
}