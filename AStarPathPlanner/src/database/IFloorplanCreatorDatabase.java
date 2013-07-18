package database;

public interface IFloorplanCreatorDatabase extends IDatabase
{
    public String getJSONBuildingRepresentation(String buildingName);
}
