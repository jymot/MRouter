package im.wangchao.mrouter;

import java.util.List;
import java.util.Map;

/**
 * <p>Description  : ILoader.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/11.</p>
 * <p>Time         : 下午3:55.</p>
 */
public interface ILoader {
    // 加载 Interceptor
    // 加载 RouterService
    // 加载 Route
    // 加载 IProvider

    /**
     * 加载所有 IInterceptor 到目标集合
     * @param target 需要加载数据的数据集，key：RouterService Name
     */
    void loadInterceptors(Map<String, List<IInterceptor>> target);

    /**
     * 加载所有 IRouterService 到目标集合
     * @param target 需要加载数据的数据集，key：RouterService Name
     */
    void loadRouterServices(Map<String, IRouterService> target);

    /**
     * 加载所有 IProvider 到目标集合
     * @param target 需要加载数据的数据集，key：RouterService://Authority
     */
    void loadProviders(Map<String, IProvider> target);

    /**
     * 加载 IInterceptor 到目标集合
     * @param name Router Name
     * @param target 需要加载数据的数据集
     * @return 当前要加载的所有 IInterceptor
     */
    List<IInterceptor> loadInterceptor(String name, Map<String, List<IInterceptor>> target);

    /**
     * 加载 IRouterService 到目标集合
     * @param name Router Name
     * @param target 需要加载数据的数据集
     * @return 当前要加载的所有 IRouterService
     */
    IRouterService loadRouterService(String name, Map<String, IRouterService> target);

    /**
     * 获取路由目标类
     * @param serviceName RouterService 名称
     * @param path Path
     */
    String getTargetClass(String serviceName, String path);

    /**
     * 加载 IProvider 到目标集合
     * @param key RouterService://Authority
     * @param target 加载数据的数据家
     * @return 当前所要加载的 IProvider
     */
    IProvider loadProvider(String key, Map<String, IProvider> target);
}
