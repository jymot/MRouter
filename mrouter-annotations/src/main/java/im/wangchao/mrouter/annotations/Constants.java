package im.wangchao.mrouter.annotations;

/**
 * <p>Description  : Contants.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/9/5.</p>
 * <p>Time         : 下午1:21.</p>
 */
public final class Constants {
    public static final String ROUTER_SERVICE_NAME = "router";

    public static final String APP_MODULE_NAME = "App";
    public static final String CLASSS_PACKAGE = "im.wangchao.mrouter.loaders";
    public static final String CLASS_ILOADER_SUFFIX = "_RouterLoader_AutoGeneration";

    public static final String CLASS_ILOADER = "im.wangchao.mrouter.ILoader";
    public static final String CLASS_IINTERCEPTOR = "im.wangchao.mrouter.IInterceptor";
    public static final String CLASS_IROUTERSERVICE = "im.wangchao.mrouter.IRouterService";
    public static final String CLASS_IPROVIDER = "im.wangchao.mrouter.IProvider";

    public static final int INTERCEPTOR_DEFAULT_PRIORITY = 999;

    public static String getClassName(String simpleName){
        return CLASSS_PACKAGE.concat(".").concat(simpleName);
    }

    public static String getLoaderClassName(String moduleName) {
        return moduleName.toUpperCase().concat(CLASS_ILOADER_SUFFIX);
    }

    public static String getLoaderClassPath(String moduleName) {
        return CLASSS_PACKAGE.concat(".").concat(getLoaderClassName(moduleName));
    }
}
