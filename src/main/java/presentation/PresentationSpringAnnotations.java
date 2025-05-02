package presentation;

import metier.IMetier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"dao", "metier"})
public class PresentationSpringAnnotations {
    public static void main(String[] args) {
        // Create Spring application context using annotations
        ApplicationContext context = new AnnotationConfigApplicationContext(PresentationSpringAnnotations.class);

        // Get the Métier bean from Spring context
        IMetier metier = context.getBean(IMetier.class);

        // Use the business logic
        System.out.println("Spring Annotations DI Result: " + metier.calcul());
    }
}