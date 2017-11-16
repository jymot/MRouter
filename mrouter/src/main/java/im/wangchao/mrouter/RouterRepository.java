package im.wangchao.mrouter;

import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import im.wangchao.mrouter.annotations.Constants;

import static im.wangchao.mrouter.RouterServiceCenter.NAME;

/**
 * <p>Description  : RouterRepository.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/15.</p>
 * <p>Time         : 下午3:45.</p>
 */
/*package*/ class RouterRepository {
    private static volatile RouterRepository sInstance;

    private Map<String, IRouterService> mRouterServices = new HashMap<>();
    private Map<String, List<IInterceptor>> mInterceptors = new HashMap<>();

    private ILoader mLoader;

    private static RouterRepository instance(){
        if (sInstance == null){
            synchronized (RouterRepository.class){
                if (sInstance == null){
                    sInstance = new RouterRepository();
                }
            }
        }

        return sInstance;
    }

    static void init(){
        try {
            ILoader loader = (ILoader) Class.forName(Constants.getClassName(Constants.CLASS_ILOADER_NAME)).newInstance();
            loader.loadInterceptors(instance().mInterceptors);
            loader.loadRouterServices(instance().mRouterServices);
            instance().mLoader = loader;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    static IRouterService getRouterServiceCenter(){
        return getRouterService(NAME);
    }

    @Nullable static List<IInterceptor> getInterceptors(String name){
        return instance().getInterceptorsImpl(name);
    }

    @Nullable static IRouterService getRouterService(String name){
        return instance().getRouterServiceImpl(name);
    }

    static String getTargetClass(String routerName, String path){
        return instance().getTargetClassImpl(routerName, path);
    }

    @Nullable private List<IInterceptor> getInterceptorsImpl(String name){
        List<IInterceptor> interceptors = mInterceptors.get(name);
        if (interceptors == null){
            interceptors = mLoader.loadInterceptor(name, mInterceptors);
        }
        return interceptors;
    }

    @Nullable private IRouterService getRouterServiceImpl(String name){
        IRouterService service = mRouterServices.get(name);
        if (service == null){
            service = mLoader.loadRouterService(name, mRouterServices);
        }
        return service;
    }

    private String getTargetClassImpl(String routerName, String path){
        return mLoader.getTargetClass(routerName, path);
    }
}
