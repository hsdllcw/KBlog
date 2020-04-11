'use strict';
const React = require('react');

class JsonUtils extends React.Component {


    static stringToJson(data){
        return JSON.parse(data);
    }

    static jsonToString(data){
        return JSON.stringify(data);
    }

    static mapToJson(map) {
        return JSON.stringify(JsonUtils.strMapToObj(map));
    }

    static jsonToMap(jsonStr){
        return  JsonUtils.objToStrMap(JSON.parse(jsonStr));
    }



    static strMapToObj(strMap){
        let obj= Object.create(null);
        for (let[k,v] of strMap) {
            obj[k] = v;
        }
        return obj;
    }


    static   objToStrMap(obj){
        let strMap = new Map();
        for (let k of Object.keys(obj)) {
            strMap.set(k,obj[k]);
        }
        return strMap;
    }


}

module.exports = JsonUtils;