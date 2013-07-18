package database;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class FloorPlanCreatorMongoDatabaseHandle implements IFloorplanCreatorDatabase
{
    // Class Member Variables
    int _dbPort;
    String _dbName;
    String _dbHostname;
    
    Mongo _mongo;
    DB _db;
    
    boolean _verbosity;

    public FloorPlanCreatorMongoDatabaseHandle()
    {
	this("localhost", 27017);
    }

    public FloorPlanCreatorMongoDatabaseHandle(String hostname, int port)
    {
	_dbPort     = port;
	_dbHostname = hostname;
	_dbName = null;
	_mongo = null;
	_verbosity = false;
    }

    public void setDBName(String dbName)
    {
	_dbName = dbName;
    }

    @Override
    public void establishDatabaseConnection()
    {
	try
	{
	    _mongo = new Mongo(_dbHostname, _dbPort);
	}
	catch (UnknownHostException e)
	{
	    System.out.println("Unknown host: " + _dbHostname);
	    System.out.println("Please make sure the specified host '" + _dbHostname + "' is available and that the MongoDB is up and running.\n");
	    e.printStackTrace();
	    System.exit(-1);
	}
    }

    public void connectToDatabase(String dbName)
    {
	setDBName(dbName);

	try
	{
	    if (_mongo == null)
	    {
		throw new NullPointerException("Need to call method establishDatabaseConnection() before calling this method.");
	    }
	    
	    // Check if the specified DB already exists 
	    if (!doesDBExist(_dbName))
	    {
		throw new Exception();
	    }
	    
	    _db = _mongo.getDB(_dbName);	    
	}
	catch (NullPointerException e)
	{
	    e.printStackTrace();
	}
	catch (Exception e)
	{
	    System.out.println("The specified db name '" + _dbName + "' does not exist in the current MongoDB. Exiting!\n\n");
	    _mongo.close();
	    System.exit(-1);
	}
    }

    /**
     * NOTE: The queries used in this method were taken from http://www.mkyong.com/mongodb/java-mongodb-query-document/
     */
    @Override
    public String getJSONBuildingRepresentation(String buildingName)
    {
	String building = null;

	try
	{
	    if (_db == null)
	    {
		throw new NullPointerException("Need to call method connectToDatabase(String <dbName>) before calling this method.");
	    }

	    // Build the query string
	    BasicDBObject andQuery = new BasicDBObject();
	    List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
	    obj.add(new BasicDBObject("userBuildingName", buildingName));
	    obj.add(new BasicDBObject("graph", new BasicDBObject("$ne", "")));
	    andQuery.put("$and", obj);

	    // Query the data
	    DBCollection table = _db.getCollection("buildings");
	    DBCursor cursor = table.find(andQuery);

	    // Check if at least one document exists
	    if (cursor.count() > 0)
	    {
		building = cursor.next().get("graph").toString();
	    }
	    
	    if (_verbosity)
	    {
		System.out.println(andQuery.toString());
		System.out.println(building);
	    }
	}
	catch (NullPointerException e)
	{
	    e.printStackTrace();
	}

	return building;
    }

    @Override
    public void closeDatabaseConnection(String dbName)
    {
	// TODO Auto-generated method stub
    }

    public List<String> getAllDBNames()
    {
	return _mongo.getDatabaseNames();
    }
    
    public void toggleVerbosity()
    {
	_verbosity = !_verbosity;
    }
    
    // Quick and dirty way to see if DB exists. This can probably be done more efficiently, but I needed to get something working quickly.
    // I don't know the runtime complexity of the getDatabaseNames() method in the Mongo class, but the search in the list of DB naems is linear,
    // which is still pretty good. I am sure this could be made Log N or even constant, but is it worth it given that this function is only called
    // once per DB connection.
    private boolean doesDBExist(String dbName)
    {
	boolean dbExists = false;
	List<String> dbNames = getAllDBNames();

	for(String currentDBName : dbNames)
	{
	    if (currentDBName.equals(dbName))
	    {
		dbExists = true;
		break;
	    }
	}

	return dbExists;
    }
}
