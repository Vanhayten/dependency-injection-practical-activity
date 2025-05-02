# Dependency Injection Framework Project

This project demonstrates various approaches to dependency injection (DI) in Java applications, and includes a custom implementation of a simple DI framework. It's divided into two main parts:

## Part 1: Dependency Injection Implementations

In this section, we demonstrate various approaches to dependency injection:

### 1. Basic Interfaces and Implementations

* `IDao`: An interface representing data access operations
    * `DaoImpl`: A concrete implementation that simulates database access

* `IMetier`: An interface representing business logic
    * `MetierImpl`: A concrete implementation that uses `IDao` to perform calculations

### 2. Static Instantiation

The most basic form of dependency injection, where dependencies are hard-coded:

```java
DaoImpl dao = new DaoImpl();
MetierImpl metier = new MetierImpl();
metier.setDao(dao);
```

### 3. Dynamic Instantiation

Uses reflection to create instances based on a configuration file:

```java
String daoClassName = scanner.nextLine();
Class<?> daoClass = Class.forName(daoClassName);
IDao dao = (IDao) daoClass.getDeclaredConstructor().newInstance();
// ...
Method setterMethod = metierClass.getMethod("setDao", IDao.class);
setterMethod.invoke(metier, dao);
```

### 4. Spring Framework (XML Configuration)

Uses Spring's XML-based configuration to define beans and their dependencies:

```xml
<bean id="dao" class="dao.DaoImpl"></bean>
<bean id="metier" class="metier.MetierImpl">
    <property name="dao" ref="dao"></property>
</bean>
```

### 5. Spring Framework (Annotation-based Configuration)

Uses Spring's annotation-based configuration:

```java
@Component("dao")
public class DaoImpl implements IDao {
    // ...
}

@Component("metier")
public class MetierImpl implements IMetier {
    private IDao dao;
    
    @Autowired
    @Qualifier("dao")
    public void setDao(IDao dao) {
        this.dao = dao;
    }
    // ...
}
```

## Part 2: Custom DI Framework

The second part of the project implements a custom dependency injection framework inspired by Spring.

### 1. Key Components

#### Annotations
* `@Component`: Marks a class as a component to be managed by the DI container
* `@Autowired`: Indicates that a dependency should be automatically injected
* `@Qualifier`: Specifies which implementation to inject when multiple are available

#### XML Configuration
* Uses JAXB for XML-to-object mapping
* Supports both setter injection and constructor injection

#### ApplicationContext
The central element of the framework that:
* Scans for annotated classes
* Instantiates objects
* Resolves and injects dependencies
* Provides access to managed objects

### 2. Supported Injection Types

The custom framework supports three types of dependency injection:

1. **Constructor Injection**: Dependencies are provided through a constructor
   ```java
   @Autowired
   public CustomMetierImpl(IDao dao) {
       this.dao = dao;
   }
   ```

2. **Setter Injection**: Dependencies are set through setter methods
   ```java
   @Autowired
   @Qualifier("customDao")
   public void setDao(IDao dao) {
       this.dao = dao;
   }
   ```

3. **Field Injection**: Dependencies are injected directly into fields
   ```java
   @Autowired
   @Qualifier("customDao")
   private IDao dao;
   ```

### 3. Configuration Methods

The framework supports two configuration approaches:

#### XML Configuration
```xml
<beans>
    <bean id="dao" className="dao.DaoImpl" />
    <bean id="metier" className="metier.MetierImpl">
        <property name="dao" ref="dao" />
    </bean>
</beans>
```

Usage:
```java
ApplicationContext context = new ApplicationContext();
context.initWithXML("src/main/resources/config.xml");
IMetier metier = (IMetier) context.getBean("metier");
```

#### Annotation-based Configuration
```java
@Component("customDao")
public class CustomDaoImpl implements IDao {
    // ...
}

@Component("customMetier")
public class CustomMetierImpl implements IMetier {
    @Autowired
    @Qualifier("customDao")
    private IDao dao;
    // ...
}
```

Usage:
```java
ApplicationContext context = new ApplicationContext();
context.initWithAnnotations("dao, metier");
IMetier metier = context.getBean(IMetier.class);
```

## Running the Project

### Prerequisites
- Java 11 or higher
- Maven

### Build
```
mvn clean package
```

### Run the examples

1. Static Instantiation:
```
java -cp target/dependency-injection-demo-1.0-SNAPSHOT.jar presentation.PresentationStatic
```

2. Dynamic Instantiation:
```
java -cp target/dependency-injection-demo-1.0-SNAPSHOT.jar presentation.PresentationDynamic
```

3. Spring with XML:
```
java -cp target/dependency-injection-demo-1.0-SNAPSHOT.jar presentation.PresentationSpringXML
```

4. Spring with Annotations:
```
java -cp target/dependency-injection-demo-1.0-SNAPSHOT.jar presentation.PresentationSpringAnnotations
```

5. Custom Framework with XML:
```
java -cp target/dependency-injection-demo-1.0-SNAPSHOT.jar framework.CustomFrameworkXmlTest
```

6. Custom Framework with Annotations:
```
java -cp target/dependency-injection-demo-1.0-SNAPSHOT.jar framework.CustomFrameworkAnnotationTest
```

## Conclusion

This project demonstrates the power and flexibility of dependency injection, from basic manual approaches to a fully-fledged custom framework. The implementation showcases how modern frameworks like Spring manage dependencies, improving code modularity, testability, and maintainability.

Key takeaways:
- Dependency injection promotes loose coupling between components
- It separates the responsibility of creating objects from using them
- It makes testing easier by allowing mock dependencies
- Various approaches (XML, annotations) offer flexibility to suit different needs