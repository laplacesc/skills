package sc.laplace.test.hbasequery.config;

import lombok.Getter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

import java.io.IOException;

/**
 * @author jxwu
 */
@Getter
public class HbaseConnection {
    private static HbaseConnection instance;
    private final Connection connection;

    private HbaseConnection(String hbaseUrl) throws IOException {
        Configuration config = HBaseConfiguration.create();
        config.set(HConstants.ZOOKEEPER_QUORUM, hbaseUrl);
        connection = ConnectionFactory.createConnection(config);
    }

    public static synchronized HbaseConnection getInstance(String hbaseUrl) throws IOException {
        if (instance == null) {
            instance = new HbaseConnection(hbaseUrl);
        }
        return instance;
    }
}
