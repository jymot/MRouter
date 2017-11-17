package im.wangchao.mrouter.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static im.wangchao.mrouter.annotations.Constants.INTERCEPTOR_DEFAULT_PRIORITY;
import static im.wangchao.mrouter.annotations.Constants.ROUTER_SERVICE_NAME;

/**
 * <p>Description  : Interceptor.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/10.</p>
 * <p>Time         : 下午3:43.</p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Interceptor {

    /**
     * The interceptor that belongs to this RouterService.
     */
    String routerName() default ROUTER_SERVICE_NAME;

    /**
     * The priority of interceptor. The smaller the more priority.
     */
    int priority() default INTERCEPTOR_DEFAULT_PRIORITY;
}
