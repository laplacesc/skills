import http from 'k6/http';
import {check, sleep} from 'k6';
import {SharedArray} from 'k6/data';

const csv_path = "../../target/data/test_data/social_network/dynamic/person_knows_person.csv";

const PERSON_KNOWS_PERSON = new SharedArray('open csv', function () {
    const csv = open(csv_path);
    return csv
        .split('\n') // 按行拆分
        .filter(line => line.trim() !== '')
        .map(line => line);
});

const IP_PORT_LIST = ['10.182.63.120:8529', '10.182.63.121:8529', '10.182.63.122:8529'];

const AQL_QUERY_URL = `http://${IP_PORT_LIST[Math.floor(Math.random() * IP_PORT_LIST.length)]}/_db/ldbc/_api/cursor`;
const HEADERS = {
    'Content-Type': 'application/json'
};

export default function () {
    let know = PERSON_KNOWS_PERSON[Math.floor(Math.random() * PERSON_KNOWS_PERSON.length)];
    let person1 = know.split('|')[0];
    let person2 = know.split('|')[1];
    const query = {
        "query": "FOR edge IN person_knows_person FILTER edge._from == @person1 AND edge._to == @person2 RETURN edge.creationDate",
        "bindVars": {
            "person1": `person/${person1}`,
            "person2": `person/${person2}`
        },
        "count": false,
        "batchSize": 1
    };
    const res = http.post(AQL_QUERY_URL, JSON.stringify(query), {headers: HEADERS});
    check(res, {
        'status is 201': (r) => r.status === 201,
        'no error in response': (r) => r.json("error") === false,
    });
    sleep(1);
}
