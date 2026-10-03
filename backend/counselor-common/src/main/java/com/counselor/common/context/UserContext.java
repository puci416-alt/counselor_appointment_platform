package com.counselor.common.context;

/**
 * 当前登录用户上下文（基于 ThreadLocal）
 */
public class UserContext {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> USERNAME = new ThreadLocal<>();
    private static final ThreadLocal<Integer> USER_TYPE = new ThreadLocal<>();

    public static void set(Long userId, String username, Integer userType) {
        USER_ID.set(userId);
        USERNAME.set(username);
        USER_TYPE.set(userType);
    }

    public static Long getUserId() {
        return USER_ID.get();
    }

    public static String getUsername() {
        return USERNAME.get();
    }

    public static Integer getUserType() {
        return USER_TYPE.get();
    }

    public static void clear() {
        USER_ID.remove();
        USERNAME.remove();
        USER_TYPE.remove();
    }
}