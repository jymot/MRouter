package im.wangchao.mrouter;

import android.content.Context;

/**
 * <p>Description  : Interceptor.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/10.</p>
 * <p>Time         : 上午11:27.</p>
 */
public interface IInterceptor {

    Route pushProceed(Context context, Route route, int requestCode);

    Route popProceed(Context context, Route route, int resultCode);

}
