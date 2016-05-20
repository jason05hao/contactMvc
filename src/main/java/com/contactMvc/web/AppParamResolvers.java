package com.contactMvc.web;

import com.britesnow.snow.util.AnnotationMap;
import com.britesnow.snow.web.RequestContext;
import com.britesnow.snow.web.param.resolver.annotation.WebParamResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.google.inject.Singleton;
import com.contactMvc.web.annotation.JsonParam;

import java.io.IOException;
import java.util.Map;

import static com.britesnow.snow.util.MapUtil.mapIt;

/**
 * Created by jeremychone on 2/8/14.
 */
@Singleton
public class AppParamResolvers {

    ObjectMapper mapper = new ObjectMapper();

    /**
     * <p>This is a simple json param resolver. I uses the Snow simple JsonUtil but could use
     * Jackson or any other json processor.</p>
     *
     * @return The map for the JSON
     */
    @WebParamResolver(annotatedWith = JsonParam.class)
    public Map resolveJsonParam(AnnotationMap annotationMap, Class paramType, RequestContext rc) {
        JsonParam jsonParam = annotationMap.get(JsonParam.class);
        String paramName = jsonParam.value();
        String value = rc.getParam(paramName);
        Map r = null;
        if (value != null){
            if (!value.startsWith("{")){
                throw new RuntimeException("Web Param " + paramName + " does not have a valid JSON value (must start with '{'): " + value);
            }
            //r = JsonUtil.toMapAndList(value);
            r = parseJson(value);
        }
        return r;
    }
    private Map parseJson(String valueStr){
        Map r = null;
        try {
            r = mapper.readValue(valueStr,Map.class);
        } catch (IOException e) {
            Throwables.propagate(e);
        }
        return r;
    }

}