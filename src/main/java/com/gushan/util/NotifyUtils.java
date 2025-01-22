package com.gushan.util;

import com.gushan.annotation.NotifyIgnore;
import com.gushan.properties.NotifyProperties;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * 通知工具类
 * <p>
 * 提供与通知相关的通用工具方法
 */
public class NotifyUtils {
    
    /**
     * 判断是否应该忽略指定的异常
     * <p>
     * 根据以下条件判断是否忽略异常：
     * 1. 异常类上是否有 @NotifyIgnore 注解
     * 2. 异常类名是否在排除列表中
     * 
     * @param exception 需要判断的异常对象
     * @param properties 通知配置属性
     * @return 如果应该忽略该异常则返回true，否则返回false
     */
    private static final Pattern TEMPLATE_PATTERN = Pattern.compile("\\{(.*?)\\}");

    /**
     * 格式化消息模板
     * @param template 消息模板
     * @param params 参数键值对
     * @return 格式化后的消息
     */
    public static String formatMessage(String template, String... params) {
        if (template == null) {
            return null;
        }
        
        if (params == null || params.length % 2 != 0) {
            return template;
        }

        String result = template;
        for (int i = 0; i < params.length; i += 2) {
            String key = params[i];
            String value = params[i + 1];
            result = result.replaceAll("\\{" + key + "\\}", value);
        }
        return result;
    }

    /**
     * 判断是否应该忽略指定的异常
     * <p>
     * 根据以下条件判断是否忽略异常：
     * 1. 异常类上是否有 @NotifyIgnore 注解
     * 2. 异常类名是否在排除列表中
     * 
     * @param exception 需要判断的异常对象
     * @param properties 通知配置属性
     * @return 如果应该忽略该异常则返回true，否则返回false
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{8,15}$");

    /**
     * 验证邮箱格式
     * @param email 待验证的邮箱地址
     * @return 如果邮箱格式正确返回true，否则返回false
     */
    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * 验证手机号格式
     * @param phone 待验证的手机号
     * @return 如果手机号格式正确返回true，否则返回false
     */
    public static boolean isValidPhoneNumber(String phone) {
        if (phone == null) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * 判断是否应该忽略指定的异常
     * <p>
     * 根据以下条件判断是否忽略异常：
     * 1. 异常类上是否有 @NotifyIgnore 注解
     * 2. 异常类名是否在排除列表中
     * 
     * @param exception 需要判断的异常对象
     * @param properties 通知配置属性
     * @return 如果应该忽略该异常则返回true，否则返回false
     */
    public static boolean shouldIgnore(Exception exception, NotifyProperties properties) {
        // 检查异常类上是否有 @NotifyIgnore 注解
        if (exception.getClass().isAnnotationPresent(NotifyIgnore.class)) {
            return true;
        }
        
        // 检查是否在排除列表中
        List<String> excludeExceptions = properties.getExcludeExceptions();
        if (excludeExceptions != null) {
            String exceptionClassName = exception.getClass().getName();
            return excludeExceptions.contains(exceptionClassName);
        }
        
        return false;
    }
}
