package privatelibs.ogu.to.privatelibs.logger;

import android.util.Log;

class TOGLogger {

    static final String sTag = "APSdk";
    static boolean sEnabled = false;

    static void v(String tag, String msg) {
        log(Log.VERBOSE, tag, msg, null);
    }

    static void v(String msg) {
        log(Log.VERBOSE, null, msg, null);
    }

    static void d(String tag, String msg) {
        log(Log.DEBUG, tag, msg, null);
    }

    static void d(String msg) {
        log(Log.DEBUG, null, msg, null);
    }

    static void i(String tag, String msg) {
        log(Log.INFO, tag, msg, null);
    }

    static void i(String msg) {
        log(Log.INFO, null, msg, null);
    }

    static void w(String msg) {
        log(Log.WARN, null, msg, null);
    }

    static void w(String tag, Throwable throwable) {
        log(Log.WARN, tag, "warn:", throwable);
    }

    static void w(Throwable throwable) {
        log(Log.WARN, null, "warn:", throwable);
    }

    static void e(String tag, String msg) {
        log(Log.ERROR, tag, msg, null);
    }

    static void e(String msg) {
        log(Log.ERROR, null, msg, null);
    }

    static void e(String tag, Throwable throwable) {
        log(Log.ERROR, tag, "error:", throwable);
    }

    static void e(Throwable throwable) {
        log(Log.ERROR, null, "error:", throwable);
    }

    private static void log(int level, String tag, String msg,
            Throwable throwable) {

        if (!sEnabled) {
            return;
        }

        if (tag == null) {
            tag = sTag;
        } else if (tag.length() > 23) {
            tag = tag.substring(0, 23);
        }

        if (msg == null) {
            msg = "";
        }

        switch (level) {
        case Log.VERBOSE:
            if (throwable != null) {
                Log.v(tag, msg, throwable);
            } else {
                Log.v(tag, msg);
            }
            break;
        case Log.DEBUG:
            if (throwable != null) {
                Log.d(tag, msg, throwable);
            } else {
                Log.d(tag, msg);
            }
            break;
        case Log.INFO:
            if (throwable != null) {
                Log.i(tag, msg, throwable);
            } else {
                Log.i(tag, msg);
            }
            break;
        case Log.WARN:
            if (throwable != null) {
                Log.w(tag, msg, throwable);
            } else {
                Log.w(tag, msg);
            }
            break;
        case Log.ERROR:
            if (throwable != null) {
                Log.e(tag, msg, throwable);
            } else {
                Log.e(tag, msg);
            }
            break;
        }
    }
}
