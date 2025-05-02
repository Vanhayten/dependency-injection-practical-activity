package framework;

import metier.IMetier;

public class CustomFrameworkAnnotationTest {
    public static void main(String[] args) {
        // Create application context with annotation configuration
        ApplicationContext context = new ApplicationContext();
        context.initWithAnnotations("dao, metier");

        // Get the business logic bean
        IMetier metier = context.getBean(IMetier.class);

        // Test the calculation
        System.out.println("Custom Framework Annotation DI Result: " + metier.calcul());
    }
}