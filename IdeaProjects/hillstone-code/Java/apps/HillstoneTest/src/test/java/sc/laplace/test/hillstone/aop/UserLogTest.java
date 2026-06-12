package sc.laplace.test.hillstone.aop;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cglib.core.DebuggingClassWriter;
import sc.laplace.test.hillstone.aop.interceptor.UserLogInterceptor;
import sc.laplace.test.hillstone.aop.proxy.UserLogProxy;
import sc.laplace.test.hillstone.aop.service.UserService;
import sc.laplace.test.hillstone.aop.service.UserServiceImpl;

import javax.annotation.Resource;

@SpringBootTest
class UserLogTest {

    @Resource
    UserServiceImpl userServiceImpl;

    public static void main(String[] args) {
        new UserLogTest().testInterceptor();
        new UserLogTest().testProxy();
    }

    @Test
    void testInterceptor() {
        // 指定 CGLIB 将动态生成的代理类保存至指定的磁盘路径下
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "target");

        // proxy
        UserServiceImpl userService = new UserLogInterceptor().getUserLogProxy(UserServiceImpl.class);

        // call methods
        userService.findUserList();
        userService.addUser();
        userService.delUser();
    }

    @Test
    void testProxy() {
        // 将JDK动态代理生成的类保存为 .class文件
        System.setProperty("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");

        // proxy
        UserService userService = new UserLogProxy().getUserLogProxy(new UserServiceImpl(), UserService.class);

        // call methods
        userService.findUserList();
        userService.addUser();
    }

    @Test
    void testAspect() {
        userServiceImpl.addUser();
    }
}
