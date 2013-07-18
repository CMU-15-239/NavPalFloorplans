package importgraph;

public class Building
{
    public String name;
    public String id;
    public Floor[] floors;
    public FloorConnectionRef[] floorConnectionRefs;
    public EdgeWeight[] edgeWeights;
    
    public EdgeWeight getEdge(String id1, String id2) {
	int idx = indexOfEdge(id1, id2);
	if(idx != -1) {
	    return edgeWeights[idx];
	}

	return null;
    }
    
    public int indexOfEdge(String id1, String id2) {
	for(int e = 0; e < edgeWeights.length; e++) {
	    if(edgeWeights[e].equals(id1, id2)) {
		return e;
	    }
	}
	
	return -1;
    }
}
