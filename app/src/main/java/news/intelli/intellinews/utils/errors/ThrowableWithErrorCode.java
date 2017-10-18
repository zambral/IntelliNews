package news.intelli.intellinews.utils.errors;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * Created by llefoulon on 01/11/2016.
 */

public class ThrowableWithErrorCode extends Throwable {

    @IntDef({NO_NETWORK,GENERIC_ERROR,DATABASE_ERROR,WRONG_EMAIL_FORMAT,WRONG_PASSWORD,NO_ACCOUNT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ErrorCode {}

    public static final int NO_NETWORK = 0;
    public static final int GENERIC_ERROR = 1;
    public static final int DATABASE_ERROR = 2;
    public static final int WRONG_EMAIL_FORMAT = 3;
    public static final int WRONG_PASSWORD = 4;
    public static final int NO_ACCOUNT = 5;


    private int errorCode = GENERIC_ERROR;

    public ThrowableWithErrorCode(@ErrorCode int code) {
        super();
        errorCode = code;
    }

    @ErrorCode
    public int getErrorCode(){
        return errorCode;
    }
}
