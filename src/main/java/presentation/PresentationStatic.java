package presentation;

import dao.DaoImpl;
import metier.MetierImpl;

public class PresentationStatic {
    public static void main(String[] args) {
        /*
         * Static Dependency Injection
         */

        // Create an instance of the DAO implementation
        DaoImpl dao = new DaoImpl();

        // Create an instance of the business logic implementation
        MetierImpl metier = new MetierImpl();

        // Inject the DAO into the business logic
        metier.setDao(dao);

        // Use the business logic
        System.out.println("Static DI Result: " + metier.calcul());
    }
}