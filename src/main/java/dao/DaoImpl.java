package dao;

public class DaoImpl implements IDao {
    @Override
    public double getData() {
        System.out.println("Getting data from database...");
        return Math.random() * 100;
    }
}