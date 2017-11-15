package im.wangchao.mrouter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static im.wangchao.mrouter.RouterServiceCenter.NAME;

/**
 * <p>Description  : ILoader Template.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/15.</p>
 * <p>Time         : 上午10:44.</p>
 */
public class ILoaderImpl implements ILoader{
    private Map<String, List<String>> interceptors = new HashMap<>();
    private Map<String, String> routerServices = new HashMap<>();
    private Map<String, Map<String, String>> routes = new HashMap<>();

    @Override public void loadInterceptors(Map<String, List<IInterceptor>> target) {
        target.put(NAME, new ArrayList<>());
        target.put("test", new ArrayList<>());
    }

    @Override public void loadRouterServices(Map<String, IRouterService> target) {
        target.put(NAME, new RouterServiceCenter(this));
        target.put("test", null);
    }

    @Override public List<IInterceptor> loadInterceptor(String name, Map<String, List<IInterceptor>> target) {
        List<IInterceptor> list = new ArrayList<>();
        target.put(name, list);
        return list;
    }

    @Override public IRouterService loadRouterService(String name, Map<String, IRouterService> target) {
        IRouterService service = null;
        target.put(name, service);
        return service;
    }

    @Override public String getTargetClass(String serviceName, String path) {
        return routes.get(serviceName).get(path);
    }
}
