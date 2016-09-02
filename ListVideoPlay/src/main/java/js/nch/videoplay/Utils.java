package js.nch.videoplay;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Formatter;
import java.util.Locale;

public class Utils {

  public static String stringForTime(int timeMs) {
    if (timeMs <= 0 || timeMs >= 24 * 60 * 60 * 1000) {
      return "00:00";
    }
    int totalSeconds = timeMs / 1000;
    int seconds = totalSeconds % 60;
    int minutes = (totalSeconds / 60) % 60;
    int hours = totalSeconds / 3600;
    StringBuilder stringBuilder = new StringBuilder();
    Formatter mFormatter = new Formatter(stringBuilder, Locale.getDefault());
    if (hours > 0) {
      return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
    } else {
      return mFormatter.format("%02d:%02d", minutes, seconds).toString();
    }
  }

  public static boolean isWifiConnected(Context context) {
    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    if (wifiNetworkInfo.isConnected()) {
      return true;
    }
    return false;
  }

  public static int dip2px(Context context ,int dpValue){
    // 获取密度比
    float density = context.getResources().getDisplayMetrics().density;
    int px = (int) (density * dpValue + 0.5f);
    return px;
  }

}
