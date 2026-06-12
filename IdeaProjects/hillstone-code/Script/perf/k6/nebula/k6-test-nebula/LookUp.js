import nebulaPool from 'k6/x/nebulagraph';
import {check} from 'k6';
import {Trend} from 'k6/metrics';

var latencyTrend = new Trend('latency', true);
var responseTrend = new Trend('responseTime', true);
var rowSizeTrend = new Trend('rowSize');

var graph_option = {
  address: "10.182.63.120:9669,10.182.63.121:9669,10.182.63.122:9669",
  space: "ldbc",
  csv_path: "target/data/test_data/social_network/dynamic/person.csv",
  pool_policy: "connection",
  csv_delimiter: "|",
  csv_with_header: true,
  output: "output/20241226203336/output500_LookUp.csv",
  username: "root",
  password: "nebula",
  max_size: 2000,
};

nebulaPool.setOption(graph_option);
var pool = nebulaPool.init();
// initial session for every vu
var session = pool.getSession()

String.prototype.format = function () {
  var formatted = this;
  var data = arguments[0];

  formatted = formatted.replace(/\{(\d+)\}/g, function(match, key){
    return data[key]
  });
  return formatted;
};


export default function (data) {
  // get csv data from csv file
  let d = session.getData();
  // {0} means the first column data in the csv file
  let ngql = "LOOKUP ON Person WHERE Person.firstName == \"{1}\" YIELD Person.firstName, Person.lastName, Person.gender, Person.birthday, Person.creationDate, Person.locationIP, Person.browserUsed".format(d);
  let response = session.execute(ngql);
  check(response, {
    "IsSucceed": (r) => r !== null && r.isSucceed() === true
  });
  // add trend
  if (response !== null) {
    latencyTrend.add(response.getLatency());
    responseTrend.add(response.getResponseTime());
    rowSizeTrend.add(response.getRowSize());
  }
};

export function teardown() {
  pool.close();
}