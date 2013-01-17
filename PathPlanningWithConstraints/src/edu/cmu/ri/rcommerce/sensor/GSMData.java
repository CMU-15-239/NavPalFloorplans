package edu.cmu.ri.rcommerce.sensor;

class GSMData
{
	enum ChannelType { REGULAR,BCCH}
	enum CellStatus { CELL_SUITABLE, CELL_LOW_PRIORITY, CELL_FORBIDDEN,
		CELL_BARRED, CELL_LOW_LEVEL,CELL_OTHER};
	 int channel;
	 int signal_strength;
	 GSMData.ChannelType type;

	 GSMData.CellStatus status;	
	 int base_station_id;
	 int country_code;
	 int network_code;
	 int location_area_code;
	 int cellID;
	 int psc;
}