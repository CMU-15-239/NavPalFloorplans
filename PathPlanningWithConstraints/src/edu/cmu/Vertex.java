package edu.cmu;

import java.util.Vector;

public interface Vertex
{
    /**
     * This is used in the same way that it is in the D* papers.
     */

    Vector<? extends Edge> getEdges();

    Vertex getSubVertex();

    int getHierarchy();

    float getCumulativeCost();

    void setCumulativeCost(float a);

    float getCost();

    boolean getEnroute();

    void setEnroute(boolean a);

    Vertex getParent();

    void setParent(Vertex a);

    Vertex raise();

    @Override
    String toString();
}
