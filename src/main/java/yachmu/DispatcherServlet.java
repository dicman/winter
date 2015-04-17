package yachmu;

import yachmu.annotations.Controller;
import yachmu.annotations.RequestMapping;
import org.reflections.Reflections;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class DispatcherServlet extends HttpServlet {
    private Map<Class<?>, Object> beans = new HashMap<>();
    private Map<String, Method> requestMappings = new HashMap<>();

    public DispatcherServlet() {
        super();

        Reflections reflections = new Reflections("yachmu");
        Set<Class<?>> types = reflections.getTypesAnnotatedWith(Controller.class);

        for (Class<?> type : types) {
            try {
                Object object = type.newInstance();
                beans.put(type, object);

                for (Method method : object.getClass().getMethods()) {
                    for (Annotation annotation : method.getDeclaredAnnotations()) {
                        if (annotation instanceof RequestMapping) {
                            requestMappings.put(((RequestMapping) annotation).value(), method);
                        }
                    }
                }
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) {
        try {
            for (String path : requestMappings.keySet()) {
                if (request.getRequestURI().equals(path)) {
                    Method method = requestMappings.get(path);
                    Object object = beans.get(method.getDeclaringClass());

                    try {
                        List<Object> params = new ArrayList<>();

                        for (Class<?> paramType : method.getParameterTypes()) {
                            if (paramType.equals(HttpServletRequest.class)) {
                                params.add(request);
                            } else if (paramType.equals(HttpServletResponse.class)) {
                                params.add(response);
                            } else {
                                params.add(null);
                            }
                        }

                        String responseBody = (String) method.invoke(object, params.toArray());

                        PrintWriter writer = response.getWriter();
                        writer.print(responseBody);
                        writer.close();

                        return;
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
