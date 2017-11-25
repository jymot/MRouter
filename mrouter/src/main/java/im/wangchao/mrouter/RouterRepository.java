package im.wangchao.mrouter;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
    private Map<String, Map<Integer, List<IInterceptor>>> mInterceptorsOrigin = new HashMap<>();
    private Map<String, IProvider> mProviders = new HashMap<>();

    private List<ILoader> mLoaders = new ArrayList<>();

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
            instance().mRouterServices.put(NAME, new RouterServiceCenter());
            List<ILoader> list = new ArrayList<>();
            ILoader APP_RouterLoader_AutoGeneration = (ILoader) Class.forName(Constants.getLoaderClassPath(Constants.APP_MODULE_NAME)).newInstance();
            initLoad(APP_RouterLoader_AutoGeneration);
            list.add(APP_RouterLoader_AutoGeneration);

            // other loaders
            List<String> loaderCls = APP_RouterLoader_AutoGeneration.loaderClass();
            if (loaderCls != null){
                ILoader itemLoader;
                for (String cls: loaderCls){
                    if (cls.endsWith(".class")){
                        cls = cls.substring(0, cls.length() - 6);
                    }
                    list.add(itemLoader = (ILoader) Class.forName(cls.replaceAll("/", ".")).newInstance());
                    initLoad(itemLoader);
                }
            }
            instance().mLoaders.addAll(list);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void initLoad(ILoader loader){
        loadInterceptors(loader, instance().mInterceptorsOrigin, instance().mInterceptors);
        loader.loadRouterServices(instance().mRouterServices);
        loader.loadProviders(instance().mProviders);
    }

    private static void loadInterceptors(ILoader loader,
                                         Map<String, Map<Integer, List<IInterceptor>>> origin,
                                         Map<String, List<IInterceptor>> interceptors){
        loader.loadInterceptors(origin);
        for (Map.Entry<String, Map<Integer, List<IInterceptor>>> entry: origin.entrySet()){
            List<IInterceptor> list = new ArrayList<>();

            Map<Integer, List<IInterceptor>> sortMap = new TreeMap<>((l, r) -> l - r);
            sortMap.putAll(entry.getValue());

            for (int key: sortMap.keySet()){
                list.addAll(sortMap.get(key));
            }

            interceptors.put(entry.getKey(), list);
        }
    }

    static RouterServiceCenter getRouterServiceCenter(){
        return (RouterServiceCenter) getRouterService(NAME);
    }

    @Nullable static List<IInterceptor> getInterceptors(String name){
        return instance().getInterceptorsImpl(name);
    }

    @Nullable static IRouterService getRouterService(String name){
        return instance().getRouterServiceImpl(name);
    }

    @Nullable static String getTargetClass(String routerName, String path){
        return instance().getTargetClassImpl(routerName, path);
    }

    static IProvider getProvider(String routerName, String provider){
        return instance().getProviderImpl(routerName, provider);
    }

    @Nullable private List<IInterceptor> getInterceptorsImpl(String name){
        List<IInterceptor> interceptors = mInterceptors.get(name);
        if (interceptors == null){
            for (ILoader loader: mLoaders){
                loadInterceptors(loader, mInterceptorsOrigin, mInterceptors);
            }
            interceptors = mInterceptors.get(name);
        }
        return interceptors;
    }

    @Nullable private IRouterService getRouterServiceImpl(String name){
        IRouterService service = mRouterServices.get(name);
        if (service == null){
            for (ILoader loader: mLoaders){
                service = loader.loadRouterService(name, mRouterServices);
                if (service != null){
                    return service;
                }
            }
        }
        return service;
    }

    private String getTargetClassImpl(String routerName, String path){
        String targetCls = null;
        for (ILoader loader : mLoaders) {
            try {
                targetCls = loader.getTargetClass(routerName, path);
            } catch (Exception ignore) {}
            if (targetCls != null && !targetCls.isEmpty()) {
                return targetCls;
            }
        }
        return null;
    }

    private IProvider getProviderImpl(String routerName, String providerName){
        final String key = routerName.concat("://").concat(providerName);
        IProvider provider = mProviders.get(key);
        if (provider == null){
            for (ILoader loader: mLoaders){
                provider = loader.loadProvider(key, mProviders);
                if (provider != null){
                    return provider;
                }
            }
        }
        return provider;
    }
}
