import http from 'k6/http';
import {check, sleep} from 'k6';
import {SharedArray} from 'k6/data';

const csv_path = "../../target/data/test_data/social_network/dynamic/person.csv";

const PERSON_FIRST_NAME = new SharedArray('open csv', function () {
    const csv = open(csv_path);
    return csv
        .split('\n') // 按行拆分
        .filter(line => line.trim() !== '')
        .map(line => line.split('|')[1]);
});

const IP_PORT_LIST = ['10.182.63.120:8529', '10.182.63.121:8529', '10.182.63.122:8529'];

const AQL_QUERY_URL = `http://${IP_PORT_LIST[Math.floor(Math.random() * IP_PORT_LIST.length)]}/_db/ldbc/_api/cursor`;
const HEADERS = {
    'Content-Type': 'application/json'
};

export default function () {
    let firstName = PERSON_FIRST_NAME[Math.floor(Math.random() * PERSON_FIRST_NAME.length)];
    const query = {
        "query": "FOR vertex IN person FILTER vertex.firstName == @firstName RETURN { 'firstName': vertex.firstName, 'lastName': vertex.lastName, 'gender': vertex.gender, 'birthday': vertex.birthday, 'creationDate': vertex.creationDate, 'locationIP': vertex.locationIP, 'browserUsed': vertex.browserUsed }",
        "bindVars": {
            "firstName": `${firstName}`
        },
        "count": false,
        "batchSize": 1
    };
    const res = http.post(AQL_QUERY_URL, JSON.stringify(query), {headers: HEADERS});
    check(res, {
        'status is 201': (r) => r.status === 201,
        'no error in response': (r) => r.json("error") === false
    });
    sleep(1);
}
