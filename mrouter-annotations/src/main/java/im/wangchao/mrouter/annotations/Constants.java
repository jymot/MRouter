package im.wangchao.mrouter.annotations;

/**
 * <p>Description  : Contants.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/9/5.</p>
 * <p>Time         : 下午1:21.</p>
 */
public final class Constants {
    public static final String ROUTER_SERVICE_NAME = "router";

    public static final String CLASSS_PACKAGE = "im.wangchao.mrouter";
    public static final String CLASS_ILOADER_NAME = "RouterLoaderImpl_AutoGeneration";

    public static final String CLASS_ILOADER = "im.wangchao.mrouter.ILoader";
    public static final String CLASS_IINTERCEPTOR = "im.wangchao.mrouter.IInterceptor";
    public static final String CLASS_IROUTERSERVICE = "im.wangchao.mrouter.IRouterService";

    public static final int INTERCEPTOR_DEFAULT_PRIORITY = 999;

    public static String getClassName(String simpleName){
        return CLASSS_PACKAGE.concat(".").concat(simpleName);
    }
}
