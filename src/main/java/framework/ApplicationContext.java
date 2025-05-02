package framework;

import framework.annotations.Autowired;
import framework.annotations.Component;
import framework.annotations.Qualifier;
import framework.xml.BeanDefinition;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.reflections.Reflections;

public class ApplicationContext {
    private Map<String, Object> beans = new HashMap<>();

    /**
     * Initialize the context with XML-based configuration
     * @param configFile the XML configuration file
     */
    public void initWithXML(String configFile) {
        try {
            // Parse XML file using JAXB
            JAXBContext jaxbContext = JAXBContext.newInstance(BeanDefinition.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            BeanDefinition beanDefinition = (BeanDefinition) unmarshaller.unmarshal(new File(configFile));

            // Create beans from XML definitions
            for (BeanDefinition.Bean bean : beanDefinition.getBeans()) {
                String id = bean.getId();
                String className = bean.getClassName();

                // Instantiate bean
                Class<?> clazz = Class.forName(className);
                Object instance = clazz.getDeclaredConstructor().newInstance();
                beans.put(id, instance);
            }

            // Resolve dependencies
            for (BeanDefinition.Bean bean : beanDefinition.getBeans()) {
                String id = bean.getId();
                Object instance = beans.get(id);

                // Process property dependencies (setter injection)
                for (BeanDefinition.Property property : bean.getProperties()) {
                    String propertyName = property.getName();
                    String ref = property.getRef();

                    if (ref != null && !ref.isEmpty()) {
                        Object dependency = beans.get(ref);
                        if (dependency != null) {
                            // Find setter method
                            String setterName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
                            Method setter = findSetter(instance.getClass(), setterName);
                            if (setter != null) {
                                setter.invoke(instance, dependency);
                            }
                        }
                    }
                }

                // Process constructor dependencies
                if (!bean.getConstructorArgs().isEmpty()) {
                    // This is simplified - would need to handle multiple constructor args properly
                    for (BeanDefinition.ConstructorArg arg : bean.getConstructorArgs()) {
                        String ref = arg.getRef();
                        if (ref != null && !ref.isEmpty()) {
                            Object dependency = beans.get(ref);
                            if (dependency != null) {
                                // Find constructor with matching parameter
                                Constructor<?>[] constructors = instance.getClass().getConstructors();
                                for (Constructor<?> constructor : constructors) {
                                    if (constructor.getParameterCount() == 1 &&
                                            constructor.getParameterTypes()[0].isAssignableFrom(dependency.getClass())) {
                                        // Create a new instance with the constructor
                                        Object newInstance = constructor.newInstance(dependency);
                                        // Replace the existing instance
                                        beans.put(id, newInstance);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize the context with annotation-based configuration
     * @param basePackage the base package to scan for components
     */
    public void initWithAnnotations(String basePackage) {
        try {
            // Scan for @Component classes
            Reflections reflections = new Reflections(basePackage);
            Set<Class<?>> componentClasses = reflections.getTypesAnnotatedWith(Component.class);

            // First pass: instantiate all components
            for (Class<?> clazz : componentClasses) {
                Component componentAnnotation = clazz.getAnnotation(Component.class);
                String id = componentAnnotation.value();

                // If no ID is specified, use the class name with first letter lowercase
                if (id.isEmpty()) {
                    String className = clazz.getSimpleName();
                    id = className.substring(0, 1).toLowerCase() + className.substring(1);
                }

                // Create instance
                Object instance = clazz.getDeclaredConstructor().newInstance();
                beans.put(id, instance);
            }

            // Second pass: inject dependencies
            for (String id : beans.keySet()) {
                Object instance = beans.get(id);
                Class<?> clazz = instance.getClass();

                // Field injection
                for (Field field : clazz.getDeclaredFields()) {
                    if (field.isAnnotationPresent(Autowired.class)) {
                        field.setAccessible(true);

                        // Find the dependency
                        Object dependency = null;

                        // Check if there's a qualifier
                        if (field.isAnnotationPresent(Qualifier.class)) {
                            Qualifier qualifier = field.getAnnotation(Qualifier.class);
                            dependency = beans.get(qualifier.value());
                        } else {
                            // Find by type
                            Class<?> fieldType = field.getType();
                            List<Object> matchingBeans = beans.values().stream()
                                    .filter(bean -> fieldType.isAssignableFrom(bean.getClass()))
                                    .collect(Collectors.toList());

                            if (matchingBeans.size() == 1) {
                                dependency = matchingBeans.get(0);
                            } else if (matchingBeans.size() > 1) {
                                throw new RuntimeException("Multiple beans found for type " + fieldType.getName());
                            }
                        }

                        if (dependency != null) {
                            field.set(instance, dependency);
                        }
                    }
                }

                // Method injection (for setters)
                for (Method method : clazz.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(Autowired.class)) {
                        // Check if it's a setter method
                        if (method.getName().startsWith("set") && method.getParameterCount() == 1) {
                            // Find the dependency
                            Object dependency = null;
                            Class<?> paramType = method.getParameterTypes()[0];

                            // Check if there's a qualifier
                            Qualifier qualifier = null;
                            for (Annotation annotation : method.getAnnotations()) {
                                if (annotation instanceof Qualifier) {
                                    qualifier = (Qualifier) annotation;
                                    break;
                                }
                            }

                            if (qualifier != null) {
                                dependency = beans.get(qualifier.value());
                            } else {
                                // Find by type
                                List<Object> matchingBeans = beans.values().stream()
                                        .filter(bean -> paramType.isAssignableFrom(bean.getClass()))
                                        .collect(Collectors.toList());

                                if (matchingBeans.size() == 1) {
                                    dependency = matchingBeans.get(0);
                                } else if (matchingBeans.size() > 1) {
                                    throw new RuntimeException("Multiple beans found for type " + paramType.getName());
                                }
                            }

                            if (dependency != null) {
                                method.invoke(instance, dependency);
                            }
                        }
                    }
                }

                // Constructor injection
                Constructor<?>[] constructors = clazz.getDeclaredConstructors();
                for (Constructor<?> constructor : constructors) {
                    if (constructor.isAnnotationPresent(Autowired.class) && constructor.getParameterCount() > 0) {
                        Object[] dependencies = new Object[constructor.getParameterCount()];
                        Class<?>[] paramTypes = constructor.getParameterTypes();

                        // Get dependencies for each parameter
                        for (int i = 0; i < paramTypes.length; i++) {
                            Class<?> paramType = paramTypes[i];

                            // Find dependency by type
                            List<Object> matchingBeans = beans.values().stream()
                                    .filter(bean -> paramType.isAssignableFrom(bean.getClass()))
                                    .collect(Collectors.toList());

                            if (matchingBeans.size() == 1) {
                                dependencies[i] = matchingBeans.get(0);
                            }
                        }

                        // Create a new instance with dependencies
                        Object newInstance = constructor.newInstance(dependencies);
                        beans.put(id, newInstance);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a bean by type
     * @param type the bean type
     * @return the bean instance
     */
    public <T> T getBean(Class<T> type) {
        List<Object> matchingBeans = beans.values().stream()
                .filter(bean -> type.isAssignableFrom(bean.getClass()))
                .collect(Collectors.toList());

        if (matchingBeans.isEmpty()) {
            return null;
        } else if (matchingBeans.size() == 1) {
            return type.cast(matchingBeans.get(0));
        } else {
            throw new RuntimeException("Multiple beans found for type " + type.getName());
        }
    }

    /**
     * Get a bean by id
     * @param id the bean id
     * @return the bean instance
     */
    public Object getBean(String id) {
        return beans.get(id);
    }

    /**
     * Find a setter method by name
     * @param clazz the class to search
     * @param setterName the setter name
     * @return the setter method or null if not found
     */
    private Method findSetter(Class<?> clazz, String setterName) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(setterName) && method.getParameterCount() == 1) {
                return method;
            }
        }
        return null;
    }
}