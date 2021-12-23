import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class Map {

	class Building {
		private String name;
		private List<Road> roads; // adjacency list
		private boolean encountered;
		private Building parent;

		public Building(String name) { // building constructor
			this.name = name;
			roads = new ArrayList<>();
			parent = null;
		}
	}

	class Road {
		private Building destination; // building where the road leads
		private int length;

		public Road(Building destination, int length) { // road constructor
			this.destination = destination;
			this.length = length;
		}
	}

	private HashMap<Building, List<Road>> map = new HashMap<>(); // HashMap to store Buildings and Roads

	public final boolean addBuilding(String name) {
		if (name == null)
			return false;
		for (Entry<Building, List<Road>> mapElement : map.entrySet()) { // iterate through map
			if (mapElement.getKey().name.equals(name)) { // check if building already exists
				return false;
			}
		}
		map.put(new Building(name), new ArrayList<Road>()); // add new building to map
		return true; // return true if successful
	}

	public final boolean addRoad(String fromBuilding, String toBuilding, int length) {
		if (length <= 0)
			return false;
		Building from = getBuilding(fromBuilding);
		Building to = getBuilding(toBuilding);

		if (from == null || to == null) // check if both buildings exists
			return false;
		for (Road road : from.roads) { // check if road already exists in from
			if (road.destination == to)
				return false; // return false if fails
		}
		for (Road road : to.roads) { // check if road already exists in to
			if (road.destination == from)
				return false; // return false if fails
		}
		from.roads.add(new Road(to, length)); // add to road to fromBuilding road list
		to.roads.add(new Road(from, length)); // add from road to toBuilding road list
		return true; // return true if successful
	}

	public final boolean addRoads(String fromBuilding, Collection<String> toBuildings, int length) {
		if (length <= 0 || toBuildings == null)
			return false;
		boolean complete = true;
		for (String building : toBuildings) {
			if (!addRoad(fromBuilding, building, length)) { // add each road from collection
				complete = false; // return false if fails
			}
		}
		return complete;
	}

	@SuppressWarnings("unlikely-arg-type")
	public final boolean removeBuilding(String name) {
		Building remBuild = null;
		for (Building building : map.keySet()) { // iterate through map
			if (building.name.equals(name)) { // check if building already exists
				remBuild = building;
			}
		}
		if (remBuild == null) // check if building exists
			return false; // return false if fails

		for (Building building : map.keySet()) { // iterate through map
			if (building.roads.contains(remBuild)) { // remove building from each road list
				building.roads.remove(remBuild);
			}
		}
		map.remove(remBuild); // remove building from map
		return true; // return true if succeeds
	}

	public final boolean removeRoad(String fromBuilding, String toBuilding) {
		boolean completeFrom = false;
		boolean completeTo = false;
		for (Building building : map.keySet()) { // iterate through map
			if (building.name.equals(fromBuilding)) { // remove road from fromBuilding list
				for (int i = 0; i < building.roads.size(); i++) {
					if (building.roads.get(i).destination.name.equals(toBuilding)) {
						building.roads.remove(building.roads.get(i));
						completeFrom = true; // succeeded in removing from fromBuilding road list
					}
				}
			}
			if (building.name.equals(toBuilding)) { // remove road from toBuilding list
				for (int i = 0; i < building.roads.size(); i++) {
					if (building.roads.get(i).destination.name.equals(fromBuilding)) {
						building.roads.remove(building.roads.get(i));
						completeTo = true; // succeeded in removing from toBuilding road list
					}
				}
			}
		}
		return completeFrom && completeTo;
	}

	public final int shortestLength(String source, String destination) {
		if (getBuilding(source) != null && getBuilding(destination) != null) { // check for valid inputs
			if (getBuilding(source) == getBuilding(destination)) {
				return 0;
			}
			try {
				return dijsktra(getBuilding(source)).get(getBuilding(destination)); // use dijsktra's algorithm to
																					// calculate length of shortest path
			} catch (NullPointerException e) {
				return -1;
			}
		} else
			return -1; // returns -1 if no such path exists
	}

	public final List<String> shortestPath(String source, String destination) {
		LinkedList<String> buildings = new LinkedList<>(); // initialize list to populate with path
		Building start = getBuilding(source);
		Building end = getBuilding(destination);
		if (start != null && end != null) { // check valid inputs (will return empty list if no path exists)
			dijsktra(start); // calculate using dijsktra's algorithm
			Building addLst = end;
			while (addLst != start && addLst.parent != null) {
				buildings.addFirst(addLst.name);
				addLst = addLst.parent; // iterate through vertex predecessor and add to list
			}
			if (addLst == start) {
				buildings.addFirst(addLst.name); // add start to list if reached
			}
		}
		return buildings;
	}

	public final int minimumTotalLength() {
		int total = 0; // counter
		HashMap<Building, Integer> MST = prim();
		for (Entry<Building, Integer> mapElement : MST.entrySet()) { // iterate through map
			total += mapElement.getValue(); // increment counter by length
		}
		return total;
	}

	public final int secondShortestPath(String source, String destination) {
		int secondShort = -1; // change this value
		Iterator<String> it = shortestPath(source, destination).iterator();
		if (!it.hasNext()) // no path exists
			return -1;
		int len = 0;
		String prev;
		String current = it.next();
		while (it.hasNext()) { // iterate through each road on shortest path, remove and check new shortest
			prev = current;
			current = (String) it.next(); // increment current
			for (Road r : getBuilding(prev).roads) { // iterate through road list
				if (r.destination.name.equals(current)) { // find correct road
					len = r.length; // save length of road to be deleted
				}
			}
			removeRoad(prev, current); // remove road from map
			int newShortest = shortestLength(source, destination);
			if (newShortest < secondShort || secondShort < 0) { // if path shorter than previously calculated paths
				secondShort = newShortest; // save the new second shortest path
			}
			addRoad(prev, current, len); // add road back into map
		}
		return secondShort; // return calculated second shortest map
	}

	// helper methods
	private Building getBuilding(String name) { // helper method to get building object from name of building
		Building building = null;
		for (Building b : map.keySet()) { // find building in map
			if (b.name.equals(name)) {
				building = b; // set building equal to building place holder
			}
		}
		return building; // return the building
	}

	private HashMap<Building, Integer> dijsktra(Building source) { // implementation of dikstra's algorithm
		ArrayList<Building> N = new ArrayList<>(); // holds already checked nodes
		HashMap<Building, Integer> D = new HashMap<>(); // holds buildings and respective path lengths
		if (map.isEmpty())
			return D;
		for (Entry<Building, List<Road>> mapElement : map.entrySet()) { // reset variables
			mapElement.getKey().encountered = false;
			mapElement.getKey().parent = null;
		}
		N.add(source);
		source.encountered = true;
		for (Road road : source.roads) { // for all nodes adjacent to source
			D.put(road.destination, road.length); // add roads and road length to d
			road.destination.parent = source; // mark source as parent
		}
		while (N.size() <= map.size() && D.keySet().iterator().hasNext()) { // loop until all nodes in N
			Building closest = D.keySet().iterator().next();
			for (Building building : D.keySet()) { // get closest not encountered building
				if (closest.encountered && !building.encountered) 
					closest = building;
				if (D.get(building) < D.get(closest) && !building.encountered) 
					closest = building;
			}
			N.add(closest); // add closest building to N
			closest.encountered = true;
			for (Road road : closest.roads) { // mark closest as parent to all adjacent roads
				if (!road.destination.encountered) {
					if (D.get(road.destination) == null || D.get(road.destination) > D.get(closest) + road.length) {
						D.put(road.destination, D.get(closest) + road.length);
						road.destination.parent = closest;
					}
				}
			}
		}
		return D; //return hash map of buildings and respective path lengths
	}

	private HashMap<Building, Integer> prim() { // implementation of prim's algorithm
		ArrayList<Building> N = new ArrayList<>(); // holds already checked nodes
		HashMap<Building, Integer> D = new HashMap<>(); // holds buildings and respective path lengths
		if (map.isEmpty())
			return D;
		for (Entry<Building, List<Road>> mapElement : map.entrySet()) {
			mapElement.getKey().encountered = false;
			mapElement.getKey().parent = null;
		}
		Building source = map.keySet().iterator().next();
		N.add(source);
		source.encountered = true;
		for (Road road : source.roads) { // for all nodes adjacent to source
			D.put(road.destination, road.length); // add roads and road length to d
			road.destination.parent = source; // mark source as parent
		}
		while (N.size() <= map.size() && D.keySet().iterator().hasNext()) { // loop until all nodes in N
			Building closest = D.keySet().iterator().next();
			for (Building building : D.keySet()) { // get closest not encountered building
				if (closest.encountered && !building.encountered)
					closest = building;
				if (D.get(building) < D.get(closest) && !building.encountered)
					closest = building;
			}
			N.add(closest); // add closest building to N
			closest.encountered = true;
			for (Road road : closest.roads) {
				if (!road.destination.encountered) { // mark closest as parent to all adjacent roads
					if (D.get(road.destination) == null || D.get(road.destination) > road.length) {
						D.put(road.destination, road.length);
						road.destination.parent = closest;
					}
				}
			}
		}
		return D; //return hash map of buildings and respective path lengths
	}
}
