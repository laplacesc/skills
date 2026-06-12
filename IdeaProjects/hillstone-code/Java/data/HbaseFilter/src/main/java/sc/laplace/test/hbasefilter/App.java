package sc.laplace.test.hbasefilter;

import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * Hello world!
 *
 */
public class App {
    private static final String HBASE_ZOOKEEPER_QUORUM = "tip-hadoop-hmaster:2181";
    private static final String HBASE_ZOOKEEPER_PROPERTY_CLIENT_PORT = "2181";

    public static void main(String[] args) {
        // 连接HBase
        org.apache.hadoop.conf.Configuration config = org.apache.hadoop.hbase.HBaseConfiguration.create();
        // 设置HBase的Zookeeper地址和端口（请根据实际情况修改）
        config.set("hbase.zookeeper.quorum", HBASE_ZOOKEEPER_QUORUM);
        config.set("hbase.zookeeper.property.clientPort", HBASE_ZOOKEEPER_PROPERTY_CLIENT_PORT);

        org.apache.hadoop.hbase.client.Connection connection = null;
        try {
            connection = org.apache.hadoop.hbase.client.ConnectionFactory.createConnection(config);
            System.out.println("成功连接到HBase！");
            Table table = connection.getTable(TableName.valueOf("ti_source_intelligence_ip"));
            // 0.3.5.7 column=cf:result, timestamp=1747966959632, value=\x00\x00\x00\x00
            // 0.3.5.7 column=cf:result_tags, timestamp=1747966959632,
            // value=[{"name_cn":"\xE4\xBF\x9D\xE7\x95\x99ip","name_en":"Reserved","severity":1,"type":0,"subtype":1,"id":91}]
            // 同时过滤result列等于3，且result_tags列中包含id为11和12的数据
            // 构造result列的过滤器
            SingleColumnValueFilter resultValueFilter = new SingleColumnValueFilter(
                    Bytes.toBytes("cf"),
                    Bytes.toBytes("result"),
                    CompareOperator.EQUAL,
                    Bytes.toBytes(0));
            resultValueFilter.setFilterIfMissing(true);

            // 构造result_tags列包含id为11和12的过滤器
            // 这里假设result_tags是JSON字符串，需要用SubstringComparator判断是否包含"id":11和"id":12
            org.apache.hadoop.hbase.filter.SingleColumnValueFilter tagId11Filter = new SingleColumnValueFilter(
                    Bytes.toBytes("cf"),
                    Bytes.toBytes("result_tags"),
                    CompareOperator.EQUAL,
                    new org.apache.hadoop.hbase.filter.SubstringComparator("\"id\":91"));
            tagId11Filter.setFilterIfMissing(true);

            org.apache.hadoop.hbase.filter.SingleColumnValueFilter tagId12Filter = new SingleColumnValueFilter(
                    Bytes.toBytes("cf"),
                    Bytes.toBytes("result_tags"),
                    CompareOperator.EQUAL,
                    new org.apache.hadoop.hbase.filter.SubstringComparator("\"id\":11741"));
            tagId12Filter.setFilterIfMissing(true);

            // // 组合过滤器，要求全部满足
            // org.apache.hadoop.hbase.filter.FilterList filterList = new
            // org.apache.hadoop.hbase.filter.FilterList(
            // org.apache.hadoop.hbase.filter.FilterList.Operator.MUST_PASS_ALL);
            // filterList.addFilter(resultValueFilter);
            // filterList.addFilter(tagId11Filter);
            // filterList.addFilter(tagId12Filter);

            // result_tag的过滤器只需要匹配上一个就可以，result结果必须匹配
            // 所以resultValueFilter和(tagId11Filter或tagId12Filter)的组合
            org.apache.hadoop.hbase.filter.FilterList tagOrFilterList = new org.apache.hadoop.hbase.filter.FilterList(
                    org.apache.hadoop.hbase.filter.FilterList.Operator.MUST_PASS_ONE);
            tagOrFilterList.addFilter(tagId11Filter);
            tagOrFilterList.addFilter(tagId12Filter);

            org.apache.hadoop.hbase.filter.FilterList finalFilterList = new org.apache.hadoop.hbase.filter.FilterList(
                    org.apache.hadoop.hbase.filter.FilterList.Operator.MUST_PASS_ALL);
            finalFilterList.addFilter(resultValueFilter);
            finalFilterList.addFilter(tagOrFilterList);

            // 应用过滤器到Scan
            Scan scan = new Scan();
            scan.setFilter(finalFilterList);

            // 执行查询
            org.apache.hadoop.hbase.client.ResultScanner scanner = table.getScanner(scan);
            System.out.println("查询结果：");
            for (org.apache.hadoop.hbase.client.Result result : scanner) {
                System.out.println(Bytes.toString(result.getRow()) + " "
                        + Bytes.toInt(result.getValue(Bytes.toBytes("cf"), Bytes.toBytes("result"))) + " "
                        + Bytes.toString(result.getValue(Bytes.toBytes("cf"), Bytes.toBytes("result_tags"))));
            }
            scanner.close();
        } catch (Exception e) {
            System.out.println("连接HBase失败: " + e.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    System.out.println("关闭HBase连接时出错: " + e.getMessage());
                }
            }
        }
    }
}
