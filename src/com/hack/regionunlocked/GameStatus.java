package com.hack.regionunlocked;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.URL;

public class GameStatus {
	
	private String upcCode;
	private String name;
	private List<RegionSupportStatusSet> support;
	
	public GameStatus(String upcCode) {
		this.upcCode = upcCode;
		this.name = getUPCDatabaseName(upcCode);
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
	private void checkStatusWikia() {
		if (!this.name.equals("")) {
			String content = getWebsiteContent("http://gaming.wikia.com/wiki/Region_Free_Xbox_360_Games");
			
			String regex = "(?i)<td>[\\s]*<a href=\"[^\"]*\"[^>]*>" + this.name + "</a>[\\s]*</td>[\\s]*" +
			"<td>[\\s]*([\\w]+)[\\s]*</td>[\\s]*" + //version
			"<td bgcolor=\"#[A-F0-9]*\">[\\s]*([\\w?]+)[\\s]*</td>[\\s]*" + //NTSC/J compatibility
			"<td bgcolor=\"#[A-F0-9]*\">[\\s]*([\\w?]+)[\\s]*</td>[\\s]*" + //NTSC/U compatibility
			"<td bgcolor=\"#[A-F0-9]*\">[\\s]*([\\w?]+)[\\s]*</td>"; //PAL compatibility
			
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(content);

			while (matcher.find()) {
				
				GameRegion region = GameRegion.UNKNOWN;
				switch (matcher.group(1)) {
					case "NTSC/J":
						region = GameRegion.NTSCJ;
						break;
					case "NTSC/U": case "US":
						region = GameRegion.NTSCU;
						break;
					case "PAL":
						region = GameRegion.PAL;
						break;
				}
				if (region != GameRegion.UNKNOWN) {
					RegionSupportStatusSet set = new RegionSupportStatusSet(region);
					switch (matcher.group(2)) {
						case "Yes":
							set.supportStatuses.put(GameRegion.NTSCJ, RegionSupportStatus.Yes);
							break;
						case "No":
							set.supportStatuses.put(GameRegion.NTSCJ, RegionSupportStatus.No);
							break;
						case "?":
							set.supportStatuses.put(GameRegion.NTSCJ, RegionSupportStatus.Unknown);
							break;
					}
					switch (matcher.group(3)) {
						case "Yes":
							set.supportStatuses.put(GameRegion.NTSCU, RegionSupportStatus.Yes);
							break;
						case "No":
							set.supportStatuses.put(GameRegion.NTSCU, RegionSupportStatus.No);
							break;
						case "?":
							set.supportStatuses.put(GameRegion.NTSCU, RegionSupportStatus.Unknown);
							break;
					}
					switch (matcher.group(4)) {
						case "Yes":
							set.supportStatuses.put(GameRegion.PAL, RegionSupportStatus.Yes);
							break;
						case "No":
							set.supportStatuses.put(GameRegion.PAL, RegionSupportStatus.No);
							break;
						case "?":
							set.supportStatuses.put(GameRegion.PAL, RegionSupportStatus.Unknown);
							break;
					}
					support.add(set);
				}
				
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