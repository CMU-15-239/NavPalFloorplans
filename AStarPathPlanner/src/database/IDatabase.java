package database;

import java.util.List;

public interface IDatabase
{
    public void establishDatabaseConnection();

    public void connectToDatabase(String dbName);

    public void closeDatabaseConnection(String dbName);

    public List<String> getAllDBNames();
}
