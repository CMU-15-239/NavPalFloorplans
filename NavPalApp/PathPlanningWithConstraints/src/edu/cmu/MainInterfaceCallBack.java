package edu.cmu;

/**
 * This interface will allow the Plan Recognizer to inform the Main Interface of a change in the predicted destination. This interface will be useful to add more pushing/pulling between Main Interface and the plan recognizer
 * 
 * @author piotr
 * 
 */
public interface MainInterfaceCallBack
{
    void placeDest(Room R);

}
