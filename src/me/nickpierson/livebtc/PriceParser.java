package me.nickpierson.livebtc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

public class PriceParser {

	private static SimpleDateFormat format;

	public static ArrayList<Float> parse(String prices, int numPoints, int minuteInterval) {
		String[] pricesArray = prices.split("\n");

		ArrayList<Float> results = new ArrayList<Float>();
		Date now;
		format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
		try {
			now = format.parse(pricesArray[0].substring(0, 19));
		} catch (ParseException e) {
			Log.e("LiveBTC", "exception", e);
			return null;
		}

		for (int i = numPoints - 1; i >= 0; i--) {
			float val = findNearest(pricesArray, now, minuteInterval * i);

			if (val == 0) {
				return null;
			}
			results.add(val);
		}

		return results;
	}

	private static float findNearest(String[] pricesArray, Date now, int minutesAgo) {
		for (int i = 0; i < pricesArray.length - 1; i++) {
			Date currDate, nextDate;
			try {
				currDate = format.parse(pricesArray[i].substring(0, 19));
				nextDate = format.parse(pricesArray[i + 1].substring(0, 19));
			} catch (ParseException e) {
				Log.e("LiveBTC", "exception", e);
				return 0;
			}

			long currDiff = now.getTime() - (minutesAgo * 60 * 1000) - currDate.getTime();
			long nextDiff = now.getTime() - (minutesAgo * 60 * 1000) - nextDate.getTime();

			if (Math.abs(nextDiff) > Math.abs(currDiff)) {
				return Float.valueOf(pricesArray[i].substring(26));
			}
		}

		return 0;
	}
}
