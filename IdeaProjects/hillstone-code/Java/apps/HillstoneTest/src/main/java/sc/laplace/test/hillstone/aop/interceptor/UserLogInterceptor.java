package sc.laplace.test.hillstone.aop.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author jxwu
 */
@Slf4j
public class UserLogInterceptor implements MethodInterceptor {

    public <T> T getUserLogProxy(Class<T> tClass) {
        //创建加强器，用来创建动态代理类
        Enhancer enhancer = new Enhancer();
        //为加强器指定要代理的业务类（即：为下面生成的代理类指定父类）
        enhancer.setSuperclass(tClass);
        //设置回调：对于代理类上所有方法的调用，都会调用CallBack，而Callback则需要实现intercept()方法进行拦
        enhancer.setCallback(this);
        // 创建动态代理类对象并返回
        return tClass.cast(enhancer.create());
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        // log - before method
        log.info("[before] execute method: {}", method.getName());
        // call method
        Object result = proxy.invokeSuper(obj, args);
        // log - after method
        log.info("[after] execute method: {}, return value: {}", method.getName(), result);
        return result;
    }
}
