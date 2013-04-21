package importgraph;

public class EdgeWeight
{
    public String node1Id;
    public String node2Id;
    public int weight;
    
    public EdgeWeight() {}
    
    public EdgeWeight(String node1Id, String node2Id, int weight) {
	this.node1Id = node1Id;
	this.node2Id = node2Id;
	this.weight = weight;
    }
    
    public String toString() {
	return "{ node1Id: " + node1Id + ", node2Id: " + node2Id
			+ ", weight: " + weight + "}";
    }
    
    public boolean equals(String id1, String id2) {
	return (node1Id.equals(id1) && node2Id.equals(id2))
		|| (node2Id.equals(id1) && node1Id.equals(id2));
    }
}
