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
			
			String regex = "(?i)<td>[\\s]*<a href=\"[^\"]*\"[^>]*>" + this.name + "</a>[\\s]*</td>[\\s]*" +
			"<td>[\\s]*([\\w]+)[\\s]*</td>[\\s]*" + //version
			"<td bgcolor=\"#[A-F0-9]*\">[\\s]*([\\w?]+)[\\s]*</td>[\\s]*" + //NTSC/J compatibility
			"<td bgcolor=\"#[A-F0-9]*\">[\\s]*([\\w?]+)[\\s]*</td>[\\s]*" + //NTSC/U compatibility
			"<td bgcolor=\"#[A-F0-9]*\">[\\s]*([\\w?]+)[\\s]*</td>"; //PAL compatibility
			
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(content);

			matcher.find();
			// System.out.println("\n\n");
			// System.out.println(matcher.group(1));
			// System.out.println(matcher.group(2));
			// System.out.println(matcher.group(3));
			// System.out.println(matcher.group(4));
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