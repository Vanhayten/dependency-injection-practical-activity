package presentation;

import dao.IDao;
import metier.IMetier;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Scanner;

public class PresentationDynamic {
    public static void main(String[] args) {
        try {
            // Read configuration from a file
            Scanner scanner = new Scanner(new File("config.txt"));

            // Get the DAO class name from the config file
            String daoClassName = scanner.nextLine();
            // Dynamically load the DAO class
            Class<?> daoClass = Class.forName(daoClassName);
            // Create an instance of the DAO class
            IDao dao = (IDao) daoClass.getDeclaredConstructor().newInstance();

            // Get the Metier class name from the config file
            String metierClassName = scanner.nextLine();
            // Dynamically load the Metier class
            Class<?> metierClass = Class.forName(metierClassName);
            // Create an instance of the Metier class
            IMetier metier = (IMetier) metierClass.getDeclaredConstructor().newInstance();

            // Get the setter method for DAO in the Metier class
            Method setterMethod = metierClass.getMethod("setDao", IDao.class);
            // Invoke the setter method to inject the DAO
            setterMethod.invoke(metier, dao);

            // Use the business logic
            System.out.println("Dynamic DI Result: " + metier.calcul());

            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}