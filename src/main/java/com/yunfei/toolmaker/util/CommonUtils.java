package com.yunfei.toolmaker.util;

import java.lang.reflect.Field;

public class CommonUtils {
    public static Boolean checkEntityExistNullField(Object obj) throws IllegalAccessException {
        Class<?> clazz = obj.getClass();

        // 获取类的所有字段，包括私有字段
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            // 设置字段可访问，即使是私有字段也可以访问
            field.setAccessible(true);

            try {
                Object value = field.get(obj);
                if (value == null) {
                    return true;
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw e;
            }
        }
        return false;
    }
}
