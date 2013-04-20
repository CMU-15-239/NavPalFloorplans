package AStar.Utils;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import Common.Tuple;

public class AStarPriorityQueue<T, V>
{
    PriorityQueue<Tuple<T, V>> _priorityQueue;
    
    // Stores a corresponding set of node indices that represents the current node indices in the _openList.
    // Since _openList is a priority queue, there is no convenient way to test if an index already exists below 
    // the top given the current ADT implementation. Therefore, we trade more resource (i.e., more memory) for 
    // increased speed (i.e., HashSet with O(1) time for add, remove, contains operations) when checking if the 
    // current node index already exists.
    
    // NOTE: To cleanup this implementation, the priority queue _openList and _openListIndices could be abstracted
    //	     into another class that encapsulates both of these data structures with their respective methods. This
    //	     would not reduce the amount of resources used, but will make the implementation of this method better
    //	     to understand.
    private Set<T> _set = new HashSet<T>();

    public AStarPriorityQueue(Integer size, Comparator<Tuple<T, V>> comparator)
    {
	_priorityQueue = new PriorityQueue<Tuple<T, V>>(size, comparator);
    }

    public Tuple<T, V> remove()
    {
	Tuple<T, V> nextLowestNode = _priorityQueue.remove();
	_set.remove(nextLowestNode.getFirst());
	return nextLowestNode;
    }

    public void add(T indice, V value)
    {
	_priorityQueue.add(new Tuple<T, V>(indice, value));
	_set.add(indice);
    }
    
    public boolean contains(T key)
    {
	return _set.contains(key);
    }
    
    public boolean isEmpty()
    {
	return _priorityQueue.isEmpty();
    }
    
    public int size()
    {
	return _priorityQueue.size();
    }
}
