package plugin.apsalar;

import android.content.Context;
import android.util.Log;

import com.apsalar.sdk.Apsalar;

/**
 * Created by lakshmana on 31/12/15.
 */
public class CoronaApsalar {

    static private  Context mContext;
    static private String API_KEY,APP_SECRET;
    static CoronaApsalar sInstance;

    private CoronaApsalar(Context context, String appkey, String appsecret) {
        mContext = context.getApplicationContext();
        API_KEY = appkey;
        APP_SECRET =appsecret;
    }

    public static synchronized void init(Context context, String appkey, String appsecret) {
        if (sInstance != null) {
            throw new IllegalStateException("Extra call to initialize analytics trackers");
        }
        sInstance = new CoronaApsalar(context, appkey,appsecret);
        Apsalar.startSession(context, API_KEY,
                APP_SECRET);
    }

    public static synchronized void sendevent(String event) {
        Apsalar.event(event);
    }
}
