package sc.laplace.test.hillstone.aop.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * @author jxwu
 */
@Slf4j
public class UserLogProxy {

    /**
     * get proxy.
     *
     * @return proxy target
     */
    public <T, U extends T> T getUserLogProxy(final U target, Class<T> interfaceType) {
        ClassLoader loader = interfaceType.getClassLoader();
        Class<?>[] interfaces = new Class[]{interfaceType};
        // proxy: 代理对象。 一般不使用该对象 method: 正在被调用的方法 args: 调用方法传入的参数
        InvocationHandler h = (proxy, method, args) -> {
            String methodName = method.getName();
            // log - before method
            log.info("[before] execute method: {}", methodName);
            // call method
            Object result = null;
            try {
                // 前置通知
                result = method.invoke(target, args);
                // 返回通知, 可以访问到方法的返回值
            } catch (NullPointerException e) {
                log.error(e.getMessage(), e);
                // 异常通知, 可以访问到方法出现的异常
            }
            // 后置通知. 因为方法可以能会出异常, 所以访问不到方法的返回值
            // log - after method
            log.info("[after] execute method: {}, return value: {}", methodName, result);
            return result;
        };
        // loader: 代理对象使用的类加载器.
        // interfaces: 指定代理对象的类型. 即代理代理对象中可以有哪些方法.
        // h: 当具体调用代理对象的方法时, 应该如何进行响应, 实际上就是调用 InvocationHandler 的 invoke 方法
        return interfaceType.cast(Proxy.newProxyInstance(loader, interfaces, h));
    }
}
