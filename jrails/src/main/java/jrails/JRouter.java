package jrails;

import java.util.Map;

public class JRouter {

    public void addRoute(String verb, String path, Class<?> clazz, String method) {
        // Implement me!
    }

    // Returns "clazz#method" corresponding to verb+URN
    // Null if no such route
    public String getRoute(String verb, String path) {
        throw new UnsupportedOperationException();
    }

    // Call the appropriate controller method and
    // return the result
    public Html route(String verb, String path, Map<String, String> params) {
        throw new UnsupportedOperationException();
    }
}
