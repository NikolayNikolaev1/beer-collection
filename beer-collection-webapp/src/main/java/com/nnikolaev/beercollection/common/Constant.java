package com.nnikolaev.beercollection.common;

public class Constant {
    public static class AppConfigValue {
        public static final String ADMIN_EMAIL = "${app.admin.email}";
        public static final String ADMIN_PASSWORD = "${app.admin.password}";
        public static final String JWT_SECRET = "${jwt.secret}";
        public static final String JWT_EXPIRATION_MS = "${jwt.expiration-in-ms}";
    }

    public static class Route {
        public static final String API_PREFIX = "/api";
        public static final String AUTH_PREFIX = API_PREFIX + "/auth";
        public static final String ADMIN_PREFIX = API_PREFIX + "/admin";
        public static final String COMPANY_PREFIX = API_PREFIX + "/company";
        public static final String LOGIN = "/login";
        public static final String LOGOUT = "/logout";
        public static final String REGISTER = "/register";

        public static class Company {
            public static final String CREATE = COMPANY_PREFIX + "/create";
        }
    }
}
