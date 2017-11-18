package im.wangchao.mrouter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>Description  : ILoader Template.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/15.</p>
 * <p>Time         : 上午10:44.</p>
 */
public class ILoaderImpl implements ILoader{
    private Map<String, List<String>> mInterceptors = new HashMap<>();
    private Map<String, String> mRouterServices = new HashMap<>();
    private Map<String, Map<String, String>> mRoutes = new HashMap<>();
    private Map<String, String> mProviders = new HashMap<>();

    @Override public void loadInterceptors(Map<String, List<IInterceptor>> target) {
        Set<String> names = mInterceptors.keySet();
        for (String name: names){
            loadInterceptor(name, target);
        }
    }

    @Override public void loadRouterServices(Map<String, IRouterService> target) {
        Set<String> names = mRouterServices.keySet();
        for (String name : names) {
            loadRouterService(name, target);
        }
    }

    @Override public void loadProviders(Map<String, IProvider> target) {
        Set<String> names = mProviders.keySet();
        for (String name: names){
            loadProvider(name, target);
        }
    }

    @Override public List<IInterceptor> loadInterceptor(String name, Map<String, List<IInterceptor>> target) {
        List<IInterceptor> list = new ArrayList<>();

        List<String> interceptorClassName = mInterceptors.get(name);
        if (interceptorClassName == null || interceptorClassName.size() == 0){
            return list;
        }
        try {
            for (String cls: interceptorClassName){
                list.add((IInterceptor) Class.forName(cls).newInstance());
            }
            target.put(name, list);
        } catch (Exception ignore){}

        return list;
    }

    @Override public IRouterService loadRouterService(String name, Map<String, IRouterService> target) {
        try {
            IRouterService service = (IRouterService) Class.forName(mRouterServices.get(name)).newInstance();
            target.put(name, service);
            return service;
        } catch (Exception e) {
            return null;
        }
    }

    @Override public String getTargetClass(String serviceName, String path) {
        return mRoutes.get(serviceName).get(path);
    }

    @Override public IProvider loadProvider(String key, Map<String, IProvider> target) {
        try {
            IProvider provider = (IProvider) Class.forName(mProviders.get(key)).newInstance();
            target.put(key, provider);
            return provider;
        } catch (Exception ignore) {
            return null;
        }
    }
}
