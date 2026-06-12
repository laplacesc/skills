package sc.laplace.test.hbasequery;

import org.apache.flink.api.java.utils.ParameterTool;
import sc.laplace.test.hbasequery.config.ConfigLoader;

import java.io.IOException;

/**
 * @author jxwu
 * <p>
 * tip-hadoop-hmaster:2181
 * hmaster.hillstonenet-cloudview.com:2181
 * <p>
 * java -jar HbaseQuery.jar -hbaseUrl tip-hadoop-hmaster:2181 -type ip -input input/ip.txt
 * java -jar HbaseQuery.jar -hbaseUrl tip-hadoop-hmaster:2181 -type domain -input input/domain.txt
 * java -jar HbaseQuery.jar -hbaseUrl tip-hadoop-hmaster:2181 -type url -input input/url.txt
 */
public class Main {

    public static void main(String[] args) throws IOException {
        new HbaseQueryJob(
                ParameterTool.fromMap(ConfigLoader.getInstance().getConfigMap())
                        .mergeWith(ParameterTool.fromArgs(args))
        ).run();
    }
}