package Sandbox;

import importgraph.Building;

import com.google.gson.Gson;

public class GsonTest
{
    private int value1 = 1;
    private String value2 = "abc";
    private transient int value3 = 3;
    public GsonTest() {
	// no-args constructor
    }
    
    public int getValue1() {
	return value1;
    }

    public static void main(String[] args)
    {
	Gson gson = new Gson();
	/*
	GsonTest obj = new GsonTest();
	String json = gson.toJson(obj);  
	//==> json is {\\"value1\\":1,\\"value2\\":\\"abc\\"}

	//Note that you can not serialize objects with circular references since that will result in infinite recursion. 

	GsonTest obj2 = gson.fromJson(json, GsonTest.class);   

	int[] a = gson.fromJson(\\"[1,3,4,5]\\", int[].class);
	System.out.println(a[0]);
	System.out.println(obj2 == null);
	System.out.println(obj2.value1);
	*/
	
	String json2 = "{\"name\":\"buiding1\",\"id\":\"buildingGraph_h333446877\",\"floors\":[{\"name\":\"1\",\"imageId\":\"safdsa\",\"imageScale\":1,\"spaces\":[{\"type\":\"space\",\"edges\":[\"psw_h-260381518\",\"psw_h-260381518\",\"landmark_h1159858036\",\"floorConnection_h839404524\"],\"id\":\"space_h-479755197\",\"spaceType\":\"room\",\"label\":\"s1\",\"walls\":[{\"p1\":{\"x\":0,\"y\":0},\"p2\":{\"x\":0,\"y\":10},\"isDoor\":false},{\"p1\":{\"x\":0,\"y\":10},\"p2\":{\"x\":10,\"y\":10},\"isDoor\":false},{\"p1\":{\"x\":10,\"y\":0},\"p2\":{\"x\":10,\"y\":1},\"isDoor\":false},{\"p1\":{\"x\":10,\"y\":1},\"p2\":{\"x\":10,\"y\":5},\"isDoor\":true},{\"p1\":{\"x\":10,\"y\":5},\"p2\":{\"x\":10,\"y\":10},\"isDoor\":false},{\"p1\":{\"x\":0,\"y\":0},\"p2\":{\"x\":10,\"y\":0},\"isDoor\":false}]},{\"type\":\"space\",\"edges\":[\"psw_h-260381518\",\"psw_h-260381518\",\"landmark_h-737275099\",\"floorConnection_h1756936803\"],\"id\":\"space_h1728531433\",\"spaceType\":\"room\",\"label\":\"s2\",\"walls\":[{\"p1\":{\"x\":10,\"y\":0},\"p2\":{\"x\":20,\"y\":0},\"isDoor\":false},{\"p1\":{\"x\":20,\"y\":0},\"p2\":{\"x\":20,\"y\":25},\"isDoor\":false},{\"p1\":{\"x\":10,\"y\":10},\"p2\":{\"x\":20,\"y\":25},\"isDoor\":false},{\"p1\":{\"x\":10,\"y\":5},\"p2\":{\"x\":10,\"y\":10},\"isDoor\":false},{\"p1\":{\"x\":10,\"y\":1},\"p2\":{\"x\":10,\"y\":5},\"isDoor\":true},{\"p1\":{\"x\":10,\"y\":0},\"p2\":{\"x\":10,\"y\":1},\"isDoor\":false}]}],\"psws\":[{\"type\":\"psw\",\"edges\":[],\"id\":\"psw_h-260381518\",\"pswType\":\"door\",\"lineRep\":{\"p1\":{\"x\":10,\"y\":1},\"p2\":{\"x\":10,\"y\":5},\"isDoor\":true}}],\"floorConnections\":[{\"type\":\"floorConnection\",\"edges\":[],\"id\":\"floorConnection_h839404524\",\"label\":\"evil stairs\",\"pointRep\":{\"x\":10,\"y\":10},\"floorConnectionType\":\"stairs\"},{\"type\":\"floorConnection\",\"edges\":[],\"id\":\"floorConnection_h1756936803\",\"label\":\"good stairs\",\"pointRep\":{\"x\":20,\"y\":20},\"floorConnectionType\":\"stairs\"}],\"landmarks\":[{\"type\":\"landmark\",\"edges\":[],\"id\":\"landmark_h1159858036\",\"label\":\"candyman\",\"description\":\"Asdfafa\",\"pointRep\":{\"x\":3,\"y\":3}},{\"type\":\"landmark\",\"edges\":[],\"id\":\"landmark_h-737275099\",\"label\":\"42\",\"description\":\"1as\",\"pointRep\":{\"x\":20,\"y\":25}}]},{\"name\":\"2\",\"imageId\":\"safdsa2\",\"imageScale\":1,\"spaces\":[{\"type\":\"space\",\"edges\":[\"psw_h-260381518\",\"psw_h-260381518\",\"landmark_h1159858036\",\"floorConnection_h1756936803\"],\"id\":\"space_h622244805\",\"spaceType\":\"room\",\"label\":\"s3\",\"walls\":[{\"p1\":{\"x\":0,\"y\":0},\"p2\":{\"x\":0,\"y\":10},\"isDoor\":false},{\"p1\":{\"x\":0,\"y\":10},\"p2\":{\"x\":10,\"y\":10},\"isDoor\":false},{\"p1\":{\"x\":10,\"y\":0},\"p2\":{\"x\":10,\"y\":1},\"isDoor\":false},{\"p1\":{\"x\":10,\"y\":1},\"p2\":{\"x\":10,\"y\":5},\"isDoor\":true},{\"p1\":{\"x\":10,\"y\":5},\"p2\":{\"x\":10,\"y\":10},\"isDoor\":false},{\"p1\":{\"x\":0,\"y\":0},\"p2\":{\"x\":10,\"y\":0},\"isDoor\":false}]},{\"type\":\"space\",\"edges\":[\"psw_h-260381518\",\"psw_h-260381518\",\"landmark_h-737275099\"],\"id\":\"space_h1942556139\",\"spaceType\":\"room\",\"label\":\"s4\",\"walls\":[{\"p1\":{\"x\":10,\"y\":0},\"p2\":{\"x\":20,\"y\":0},\"isDoor\":false},{\"p1\":{\"x\":20,\"y\":0},\"p2\":{\"x\":20,\"y\":25},\"isDoor\":false},{\"p1\":{\"x\":10,\"y\":10},\"p2\":{\"x\":20,\"y\":25},\"isDoor\":false},{\"p1\":{\"x\":10,\"y\":5},\"p2\":{\"x\":10,\"y\":10},\"isDoor\":false},{\"p1\":{\"x\":10,\"y\":1},\"p2\":{\"x\":10,\"y\":5},\"isDoor\":true},{\"p1\":{\"x\":10,\"y\":0},\"p2\":{\"x\":10,\"y\":1},\"isDoor\":false}]}],\"psws\":[{\"type\":\"psw\",\"edges\":[],\"id\":\"psw_h-260381518\",\"pswType\":\"door\",\"lineRep\":{\"p1\":{\"x\":10,\"y\":1},\"p2\":{\"x\":10,\"y\":5},\"isDoor\":true}}],\"floorConnections\":[{\"type\":\"floorConnection\",\"edges\":[],\"id\":\"floorConnection_h839404524\",\"label\":\"evil stairs\",\"pointRep\":{\"x\":10,\"y\":15},\"floorConnectionType\":\"stairs\"},{\"type\":\"floorConnection\",\"edges\":[],\"id\":\"floorConnection_h1756936803\",\"label\":\"good stairs\",\"pointRep\":{\"x\":10,\"y\":10},\"floorConnectionType\":\"stairs\"}],\"landmarks\":[{\"type\":\"landmark\",\"edges\":[],\"id\":\"landmark_h1159858036\",\"label\":\"candyman\",\"description\":\"Asdfafa\",\"pointRep\":{\"x\":3,\"y\":3}},{\"type\":\"landmark\",\"edges\":[],\"id\":\"landmark_h-737275099\",\"label\":\"42\",\"description\":\"1as\",\"pointRep\":{\"x\":20,\"y\":25}}]}],\"floorConnectionRefs\":[{\"type\":\"floorConnection\",\"edges\":[\"floorGraph_h-1593009461\",\"floorGraph_h335141358\"],\"id\":\"floorConnection_h839404524\",\"label\":\"evil stairs\",\"floorConnectionType\":\"stairs\"},{\"type\":\"floorConnection\",\"edges\":[\"floorGraph_h-1593009461\",\"floorGraph_h335141358\"],\"id\":\"floorConnection_h1756936803\",\"label\":\"good stairs\",\"floorConnectionType\":\"stairs\"}],\"edgeWeights\":[{\"node1Id\":\"psw_h-260381518\",\"node2Id\":\"space_h-479755197\",\"weight\":1},{\"node1Id\":\"psw_h-260381518\",\"node2Id\":\"space_h1728531433\",\"weight\":1},{\"node1Id\":\"landmark_h1159858036\",\"node2Id\":\"space_h-479755197\",\"weight\":1},{\"node1Id\":\"landmark_h-737275099\",\"node2Id\":\"space_h1728531433\",\"weight\":1},{\"node1Id\":\"floorConnection_h839404524\",\"node2Id\":\"space_h-479755197\",\"weight\":1},{\"node1Id\":\"floorConnection_h1756936803\",\"node2Id\":\"space_h1728531433\",\"weight\":1},{\"node1Id\":\"psw_h-260381518\",\"node2Id\":\"space_h622244805\",\"weight\":1},{\"node1Id\":\"psw_h-260381518\",\"node2Id\":\"space_h1942556139\",\"weight\":1},{\"node1Id\":\"landmark_h1159858036\",\"node2Id\":\"space_h622244805\",\"weight\":1},{\"node1Id\":\"landmark_h-737275099\",\"node2Id\":\"space_h1942556139\",\"weight\":1},{\"node1Id\":\"floorConnection_h1756936803\",\"node2Id\":\"space_h622244805\",\"weight\":1},{\"node1Id\":\"psw_h-1472817065\",\"node2Id\":\"space_h-707577949\",\"weight\":1},{\"node1Id\":\"psw_h-1135339993\",\"node2Id\":\"space_h-1261985011\",\"weight\":1},{\"node1Id\":\"psw_h-1581827855\",\"node2Id\":\"space_h1884492357\",\"weight\":1},{\"node1Id\":\"psw_h-1594477077\",\"node2Id\":\"space_h1904949550\",\"weight\":1},{\"node1Id\":\"psw_h1662901615\",\"node2Id\":\"space_h1665103229\",\"weight\":1},{\"node1Id\":\"psw_h-1525086990\",\"node2Id\":\"space_h-1573634466\",\"weight\":1},{\"node1Id\":\"psw_h-1525086990\",\"node2Id\":\"space_h803929414\",\"weight\":1},{\"node1Id\":\"psw_h-388977348\",\"node2Id\":\"space_h803929414\",\"weight\":1},{\"node1Id\":\"psw_h493845650\",\"node2Id\":\"space_h803929414\",\"weight\":1},{\"node1Id\":\"psw_h493845650\",\"node2Id\":\"space_h731986991\",\"weight\":1},{\"node1Id\":\"psw_h-848114432\",\"node2Id\":\"space_h731986991\",\"weight\":1},{\"node1Id\":\"psw_h45102571\",\"node2Id\":\"space_h1830634552\",\"weight\":1},{\"node1Id\":\"psw_h-1472817065\",\"node2Id\":\"space_h1739663174\",\"weight\":1},{\"node1Id\":\"psw_h-1135339993\",\"node2Id\":\"space_h1739663174\",\"weight\":1},{\"node1Id\":\"psw_h-1581827855\",\"node2Id\":\"space_h1739663174\",\"weight\":1},{\"node1Id\":\"psw_h-1594477077\",\"node2Id\":\"space_h1739663174\",\"weight\":1},{\"node1Id\":\"psw_h1662901615\",\"node2Id\":\"space_h1739663174\",\"weight\":1},{\"node1Id\":\"psw_h-388977348\",\"node2Id\":\"space_h1739663174\",\"weight\":1},{\"node1Id\":\"psw_h-848114432\",\"node2Id\":\"space_h1739663174\",\"weight\":1},{\"node1Id\":\"psw_h45102571\",\"node2Id\":\"space_h1739663174\",\"weight\":1},{\"node1Id\":\"landmark_h1159858036\",\"node2Id\":\"space_h-707577949\",\"weight\":1},{\"node1Id\":\"landmark_h-737275099\",\"node2Id\":\"space_h1830634552\",\"weight\":1}]}";
	Building b = gson.fromJson(json2, Building.class);
	System.out.println(b.floors[0].spaces[1].walls[0].p1.x);
	
	
    }

}
