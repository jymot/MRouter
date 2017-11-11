package im.wangchao.mrouter.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Description  : Route.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/11/11.</p>
 * <p>Time         : 下午2:00.</p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Route {

    String path();

    String routerName() default "";
}
