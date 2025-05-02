package framework;

import metier.IMetier;

public class CustomFrameworkXmlTest {
    public static void main(String[] args) {
        // Create application context with XML configuration
        ApplicationContext context = new ApplicationContext();
        context.initWithXML("src/main/resources/config.xml");

        // Get the business logic bean
        IMetier metier = (IMetier) context.getBean("metier");

        // Test the calculation
        System.out.println("Custom Framework XML DI Result: " + metier.calcul());
    }
}