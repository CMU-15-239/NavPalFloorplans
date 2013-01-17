package edu.cmu.ri.rcommerce;


interface LocalizationCallback{
	
	void locationUpdate(double x,double y,double theta, long time);
}