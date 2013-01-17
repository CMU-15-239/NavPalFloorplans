package edu.cmu;

public interface Edge
{
    /**
     * Edge is the used in the same sense that it is used in most A* or D* papers. It is a connection between two places. Edges should be given costs in the future to make the path planning better.
     */
    public Vertex getFrom();

    public void setFrom(Vertex s);

    public Vertex getTo();

    public void setTo(Vertex s);

    public Vertex getToSub();

    public boolean isToHigherLevel();

    public Node toNode();

    public Node fromNode();
}
