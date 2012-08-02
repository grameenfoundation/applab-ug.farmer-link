package applab.client.farmerlink;

import java.io.File;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

public class MarketLinkApplication extends Application {

    // Storage paths
    public static final String MARKETLINK_ROOT = Environment.getExternalStorageDirectory() + "/marketlink";
    public static final String CACHE_PATH = MARKETLINK_ROOT + "/.cache";
    public static final String METADATA_PATH = MARKETLINK_ROOT + "/databases";
    
	private static MarketLinkApplication singleton = null;


    public static MarketLinkApplication getInstance() {
        return singleton;
    }
    
    /**
     * Creates required directories on the SDCard (or other external storage)
     * @throws RuntimeException if there is no SDCard or the directory exists as a non directory
     */
    public static void createMarketLinkDirectories() throws RuntimeException {

        String cardstatus = Environment.getExternalStorageState();
        if (cardstatus.equals(Environment.MEDIA_REMOVED)
                || cardstatus.equals(Environment.MEDIA_UNMOUNTABLE)
                || cardstatus.equals(Environment.MEDIA_UNMOUNTED)
                || cardstatus.equals(Environment.MEDIA_MOUNTED_READ_ONLY)
                || cardstatus.equals(Environment.MEDIA_SHARED)) {
            RuntimeException e =
                new RuntimeException("MarketLink reports :: SDCard error: "
                        + Environment.getExternalStorageState());
            throw e;
        }

        String[] dirs = {
        		MARKETLINK_ROOT, CACHE_PATH, METADATA_PATH
        };

        for (String dirName : dirs) {
            File dir = new File(dirName);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    RuntimeException e =
                        new RuntimeException("MarketLink reports :: Cannot create directory: " + dirName);
                    throw e;
                }
            } else {
                if (!dir.isDirectory()) {
                    RuntimeException e =
                        new RuntimeException("MarketLink reports :: " + dirName
                                + " exists, but is not a directory");
                    throw e;
                }
            }
        }
    }
    
    @Override
    public void onCreate() {
        singleton = this;
        super.onCreate();
    }
}
