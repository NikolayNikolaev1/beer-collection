package com.nnikolaev.beercollection.common;

public class Constant {
    public static final String ADMIN_AUTHORIZATION = "hasRole('ADMIN')";

    public static class AppConfigValue {
        public static final String ADMIN_EMAIL = "${app.admin.email}";
        public static final String ADMIN_PASSWORD = "${app.admin.password}";
        public static final String JWT_SECRET = "${jwt.secret}";
        public static final String JWT_EXPIRATION_MS = "${jwt.expiration-in-ms}";
    }

    public static class ExceptionMessage {
        public static class Beer {
            public static final String NOT_FOUND = "Beer with provided ID does not exist.";
        }

        public static class User {
            public static final String CONFLICT_ROLE = "User already with provided role.";
            public static final String CONFLICT_DELETED = "User already deleted.";
            public static final String CONFLICT_NOT_DELETED = "User is not deleted.";
        }
    }

    public static class Route {
        public static final String API_PREFIX = "/api";
        public static final String AUTH_PREFIX = API_PREFIX + "/auth";
        public static final String ADMIN_PREFIX = API_PREFIX + "/admin";
        public static final String LOGIN = "/login";
        public static final String LOGOUT = "/logout";
        public static final String REGISTER = "/register";
        public static final String METHOD_CREATE = "/create";
        public static final String METHOD_UPDATE = "/update/{id}";
        public static final String METHOD_DELETE = "/delete";

        public static class Beer {
            public static final String PREFIX = API_PREFIX + "/beers";
            public static final String CREATE = PREFIX + METHOD_CREATE;
            public static final String READ_ALL = PREFIX;
            public static final String READ_ONE = PREFIX + "/{id}";
            public static final String UPDATE = PREFIX + METHOD_UPDATE;
            public static final String DELETE = PREFIX + METHOD_DELETE;
            public static final String DELETE_ONE = PREFIX + METHOD_DELETE + "/{id}";
        }

        public static class Company {
            public static final String PREFIX = API_PREFIX + "/companies";
            public static final String CREATE = PREFIX + METHOD_CREATE;
            public static final String READ_ALL = PREFIX;
            public static final String READ_ONE = PREFIX + "/{id}";
            public static final String UPDATE = PREFIX + METHOD_UPDATE;
            public static final String DELETE = PREFIX + METHOD_DELETE;
            public static final String DELETE_ONE = PREFIX + METHOD_DELETE + "/{id}";
        }

        public static class User {
            public static final String PREFIX = API_PREFIX + "/users";
            public static final String CREATE = PREFIX + METHOD_CREATE;
            public static final String READ_ALL = PREFIX;
            public static final String READ_ONE = PREFIX + "/{id}";
            public static final String UPDATE = PREFIX + METHOD_UPDATE;

        }
    }
}
