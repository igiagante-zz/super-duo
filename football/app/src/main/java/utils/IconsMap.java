package utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;

import com.example.igiagante.football.R;

import java.util.HashMap;
import java.util.Map;

/**
 * @author igiagante on 12/9/15.
 *         It uses to provide the country flags for the seasons.
 */
public class IconsMap {

    private static Context mContext;
    private static Map<String, Drawable> icons;

    public IconsMap(Context context) {
        mContext = context;
        icons = new HashMap<>();
    }

    public void initIconsMap() {
        icons.put("394", getDrawable(mContext, R.drawable.ic_germany_flag));
        icons.put("395", getDrawable(mContext, R.drawable.ic_germany_flag));
        icons.put("398", getDrawable(mContext, R.drawable.ic_premier_league));
        icons.put("399", getDrawable(mContext, R.drawable.ic_espania));
        icons.put("405", getDrawable(mContext, R.drawable.ic_champions_league));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Drawable getDrawable(Context context, int resource) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getResources().getDrawable(resource, null);
        }
        return context.getResources().getDrawable(resource);
    }

    public Drawable getIcon(String league) {
        return icons.get(league);
    }
}
