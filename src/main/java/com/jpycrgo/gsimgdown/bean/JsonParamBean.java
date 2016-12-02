package com.jpycrgo.gsimgdown.bean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author mengzx
 * @date 2016/4/28
 * @since 1.0.0
 */
public class JsonParamBean {

    private Map<String, Object> params;

    public JsonParamBean() {
        params = new HashMap<String, Object>();
        params.put("type", "updatenodelabel");
        params.put("isCache", true);
        params.put("cacheTime", 60);
        params.put("nodeId", "20117");
        params.put("isNodeId", "true");
        params.put("page", 1);
    }

    public String parseParams() throws JsonProcessingException, UnsupportedEncodingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String paramsOfJsonStr =  objectMapper.writeValueAsString(params);
        String paramsOfStr = URLEncoder.encode(paramsOfJsonStr, "UTF-8");

        return paramsOfStr;
    }

    public void setPageIndex(int pageIndex) {
        params.put("page", pageIndex);
    }

}
