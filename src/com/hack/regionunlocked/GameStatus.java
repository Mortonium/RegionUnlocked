package com.hack.regionunlocked;

import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.URL;

public class GameStatus {
	
	private String upcCode;
	private String name;
	private GameRegion gameRegion;
	private HashMap<GameRegion,RegionSupportStatus> supportStatuses;
	
	public GameStatus(String upcCode) {
		this.upcCode = upcCode;
	}
	
	private String getUPCDatabaseName(String upcCode) {
		//885370201215 = Gears of War 3
		String content = getWebsiteContent("http://www.upcdatabase.com/item/" + upcCode);
		
		String regex = "<td>Description</td><td></td><td>(.*?)</td>";
		
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(content);
		
		try {
			
			matcher.find();
			return matcher.group(1);
			
		} catch (Exception ex) {
			return "";
		}
		
	}
	private void checkStatus() {
		if (!this.name.equals("")) {
			String content = getWebsiteContent("http://gaming.wikia.com/wiki/Region_Free_Xbox_360_Games");
			
			String regexName = this.name;
			for (int i = this.name.length() - 1; i >= 0; i--) {
				
			}
			String regex = "<td>[\\s]*<a href=\"[^\"]*\"[^>]*>Alan Wake</a>[\\s]*</td>[\\s]*(.*?)</tr>";
			
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(content);

			boolean found = false;
			while (matcher.find()) {
				console.format("I found the text" +
					" \"%s\" starting at " +
					"index %d and ending at index %d.%n",
					matcher.group(),
					matcher.start(),
					matcher.end());
				found = true;
			}
		}
	}
	private String getWebsiteContent(String urlString) {
		try {
			URL url = new URL(urlString);
			InputStream inStream = url.openStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
			String content = "";
			String line;
			while ((line = br.readLine()) != null) {
				content += line;
			}
			return content;
		} catch (Exception ex) {
			return "";
		}
	}
	
}