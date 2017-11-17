package im.wangchao.mrouter.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static im.wangchao.mrouter.annotations.Constants.ROUTER_SERVICE_NAME;

/**
 * <p>Description  : Provider.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/17.</p>
 * <p>Time         : 上午8:23.</p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Provider {

    /**
     * The name for this Provider.
     */
    String name();

    /**
     * The interceptor that belongs to this RouterService.
     */
    String routerName() default ROUTER_SERVICE_NAME;
}
