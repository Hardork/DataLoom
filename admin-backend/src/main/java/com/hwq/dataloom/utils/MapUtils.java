package com.hwq.dataloom.utils;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Map转对象工具类
 */
public class MapUtils {

    /**
     * Map对对象方法
     * @param map map集合
     * @param entityClass 对应转换目标对象类
     * @return 目标对象
     * @param <T> 目标对象对应的Class
     * @throws IllegalAccessException 异常
     * @throws InstantiationException 异常
     */
    public static <T> T mapToEntity(Map<String, Object> map, Class<T> entityClass) throws IllegalAccessException, InstantiationException {
        T entity = entityClass.newInstance();
        for (Field field : entityClass.getDeclaredFields()) {
            field.setAccessible(true);
            if (map.containsKey(field.getName())) {
                Object value = map.get(field.getName());
                if (value!= null && field.getType().isAssignableFrom(value.getClass())) {
                    field.set(entity, value);
                }
            }
        }
        return entity;
    }
}
