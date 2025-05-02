package presentation;

import metier.IMetier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PresentationSpringXML {
    public static void main(String[] args) {
        // Load the Spring context from XML file
        ApplicationContext context = new ClassPathXmlApplicationContext("config.xml");

        // Get the Métier bean from Spring context
        IMetier metier = context.getBean(IMetier.class);

        // Use the business logic
        System.out.println("Spring XML DI Result: " + metier.calcul());
    }
}