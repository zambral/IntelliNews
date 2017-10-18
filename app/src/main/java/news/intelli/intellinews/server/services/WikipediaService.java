package news.intelli.intellinews.server.services;

import android.util.ArrayMap;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by llefoulon on 13/11/2016.
 */

public interface WikipediaService {

    @GET("w/api.php")
    Observable<ResponseBody> getPages(@Query("action") String action,@Query("search") String search);
}
