package sc.laplace.test.hillstone.spel;


import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.HashMap;
import java.util.Map;

public class SpELTest {

    private static final Logger log = LoggerFactory.getLogger(SpELTest.class);
    public Map<String, Integer> resultMap = new HashMap() {{
        put("IocType", 3);
        put("SubType", 0);
        put("BlackCount", 3);
        put("SandboxConfidence", 3);
        put("Tencent", 3);
        put("360", 3);
        put("ManualReview", 3);
        put("WhiteList", 3);
        put("Sandbox", 3);
        put("Virustotal", 3);
    }};

    @Test
    public void testSpEL() {
        SpelExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext(resultMap);
        resultMap.forEach(context::setVariable);
        // 仅出库Virustotal结果和Tencent结果都为恶意的数据
        Boolean b = parser.parseExpression("#Virustotal == 3 && #Tencent == 3").getValue(context, Boolean.class);
        System.out.println(b);
        // 去除只有360源单独判黑的数据
        Boolean bb = parser.parseExpression("!(#root['360'] == 3 && #BlackCount == 1)").getValue(context, Boolean.class);
        System.out.println(bb);
        // 去除只有360源单独判黑的数据
        Boolean bbb = parser.parseExpression("!(#IocType == 3 && #SubType == 0 && #ManualReview != 3 && #WhiteList != 3 && #Sandbox != 3 && #Virustotal != 3)").getValue(context, Boolean.class);
        System.out.println(bbb);
    }

    @Test
    public void testBinaryString() {
        String str = "11000,11001,1010,1111011";
        String[] arr = str.split(",");
        for (String s : arr) {
            int iocType = ((0xff) >> (8 - 3)) & Integer.parseInt(s, 2);
            log.info("ioc type: {}", iocType);
            int subTypes = Integer.parseInt(s, 2) >> 3;
            log.info("ioc subtypes: {}", subTypes);
            log.info("to binary string: {}", Integer.toBinaryString((subTypes << 3) + iocType));
        }
    }
}
