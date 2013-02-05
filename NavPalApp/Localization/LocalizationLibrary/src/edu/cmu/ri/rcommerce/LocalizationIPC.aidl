package edu.cmu.ri.rcommerce;
import edu.cmu.ri.rcommerce.LocalizationCallback;

interface LocalizationIPC {
    
	void registerCallback(LocalizationCallback cb);
    void unregisterCallback(LocalizationCallback cb);
    void setLocation(float x, float y);
    void setOrientationOffset(float rad);
    void setCoordinateSystemToRobot();
    void setCoordinateSystemToGlobal();
    void reset();
}