package com.ossprj.transmission.task.model.component;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

public abstract class AbstractComponent<T> {

    protected final Map<String, Object> configMap;
    protected final T configuration;

    protected AbstractComponent(final Map<String, Object> configMap) {
        try {
            this.configMap = configMap;
            final ParameterizedType parameterizedType = (ParameterizedType) this.getClass().getGenericSuperclass();
            final Type actualType = parameterizedType.getActualTypeArguments()[0];
            //System.out.println("actualType: " + actualType);
            this.configuration = (T) new ObjectMapper().convertValue(configMap, Class.forName(actualType.getTypeName()));
        } catch (IllegalArgumentException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
