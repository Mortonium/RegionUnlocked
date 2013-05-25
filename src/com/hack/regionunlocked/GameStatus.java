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
	private String checkName;
	private GameRegion gameRegion;
	private HashMap<GameRegion, RegionSupportStatus> supportStatuses;
	private String scandItKey = "key=-rdsomoapvSlt5JjXpPNr0WfBpw-H7f5R9JJMnIbw5J";

	public GameStatus(String upcCode) {
		this.upcCode = upcCode;
		this.checkName = getScandItName(upcCode);
	}

	private String getScandItName(String upcCode) {
		String url = "https://api.scandit.com/v2/products/" + upcCode
				+ scandItKey;

		String content = getWebsiteContent(url);

		if (content.contains("name")) {

			String strip = content.substring(18);
			int check = strip.indexOf("\"");
			strip = strip.substring(0, check);
			check = strip.indexOf("0");
			strip = strip.substring(0, check - 9);
			System.out.println(strip);
			checkName = strip;
			return strip;
		} else {
			System.out.println("ERROR");
			return "";
		}
	}

	private String getUPCDatabaseName(String upcCode) {
		// 885370201215 = Gears of War 3
		String content = getWebsiteContent("http://www.upcdatabase.com/item/"
				+ upcCode);

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

			String regex = "(?i)<td>[\\s]*<a href=\"[^\"]*\"[^>]*>"
					+ this.name
					+ "</a>[\\s]*</td>[\\s]*"
					+ "<td>[\\s]*([\\w]+)[\\s]*</td>[\\s]*"
					+ // version
					"<td bgcolor=\"#[A-F0-9]*\">[\\s]*([\\w?]+)[\\s]*</td>[\\s]*"
					+ // NTSC/J compatibility
					"<td bgcolor=\"#[A-F0-9]*\">[\\s]*([\\w?]+)[\\s]*</td>[\\s]*"
					+ // NTSC/U compatibility
					"<td bgcolor=\"#[A-F0-9]*\">[\\s]*([\\w?]+)[\\s]*</td>"; // PAL
																				// compatibility

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
			BufferedReader br = new BufferedReader(new InputStreamReader(
					inStream));
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