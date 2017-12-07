package im.wangchao.mrouter.exception;

import im.wangchao.mrouter.RouteIntent;

/**
 * <p>Description  : TargetClassNotFoundException.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2017/12/7.</p>
 * <p>Time         : 下午5:24.</p>
 */
public class TargetClassNotFoundException extends Exception{

    public TargetClassNotFoundException(RouteIntent route){
        super(String.format("Can not found target class with Uri(%s).", route.uri()));
    }
}
