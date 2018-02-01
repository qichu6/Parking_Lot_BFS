package project4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class BFS {

	// generate random numbers, seeds generate data
	public static ArrayList<Integer> generateTestData(int length) {
		return generateTestData(length, System.nanoTime(), 1000);
	}

	public static ArrayList<Integer> generateTestData(int length, long seed) {
		return generateTestData(length, seed, 1000);
	}

	public static ArrayList<Integer> generateTestData(int length, long seed, int size) {
		ArrayList<Integer> testDate = new ArrayList<Integer>();
		Random r = new Random(seed);
		for (int i = 0; i < length; i++) {
			testDate.add(r.nextInt(size));
		}
		return testDate;
	}

	// delate the repetitive layouts (it equals to judge whether the layout is inside one list)
	public static int inList(ArrayList<Area> areaList, Area area) {
		for (int i = 0; i < areaList.size(); i++) {
			if (areaList.get(i).same(area))
				return i;
		}
		return -1;
	}

	public static ArrayList<int[]> bfs(Area area) {
		ArrayList<Area> areaList = new ArrayList<Area>();
		areaList.add(area);
		ArrayList<Integer> areaQueue = new ArrayList<Integer>();
		areaQueue.add(0);
		int target = -1;
		// cycle the algorithm
		while (!areaQueue.isEmpty()) {
			int optArea = areaQueue.get(0);
			Area optarea = areaList.get(optArea);// find the corresponding layout 
			areaQueue.remove(0);
			// find all the possible layout in next step
			HashMap<Integer, ArrayList<Integer>> nextList = areaList.get(optArea).getNextList();
			for (int carID : nextList.keySet()) {
				// all possible directions 
				for (int direction : nextList.get(carID)) {
					Area nextArea = new Area(optarea);
					nextArea.lastArea = optArea;
					nextArea.lastOptVehicle = carID;
					nextArea.lastDriection = direction;
					if (nextArea.move(carID, direction)) {
						int areaID = inList(areaList, nextArea);
						// non-repetitive layout and then add the layout as the last one in the queue
						if (areaID == -1) {
							areaList.add(nextArea);
							areaQueue.add(areaList.size() - 1);
							if (nextArea.check()) {
								target = areaList.size() - 1;
								break;
							}
						}
					} else
						continue;
				}
				// every cycle should check whether it is out or not 
				if (target != -1)
					break;
			}
			if (target != -1)
				break;
		}
		// System.out.println(target);
		System.out.printf("searchCount:%d,", areaList.size());
		// System.out.println(areaList.size());
		if (target == -1) {
			System.out.println("no solution!");
			return null;
		}
		System.out.print("hasSolution,");
		ArrayList<int[]> result = new ArrayList<int[]>();
		int optArea = target;
		// trace back to find the changing process from back to the first
		while (optArea != 0) {
			int[] move = new int[2];
			move[0] = areaList.get(optArea).lastOptVehicle;
			move[1] = areaList.get(optArea).lastDriection;
			result.add(move);
			optArea = areaList.get(optArea).lastArea;
		}

		// the print process is in right order after it reverses.
		Collections.reverse(result);
		return result;
	}

	public static void main(String args[]) {
		
		ArrayList<Vehicle> carList = new ArrayList<Vehicle>();
		Random random = new Random(System.nanoTime());
		long starTime;
		long endTime;
		long runTime;

		// the first parameter is the car's length, the second is the direction of car's head
		carList.add(new Vehicle(1, 1));// to avoid the index 0 appearing 
		carList.add(new Vehicle(2, 4, true));
		carList.add(new Vehicle(2, 2));
		carList.add(new Vehicle(2, 4));
		carList.add(new Vehicle(2, 2));
		carList.add(new Vehicle(2, 2));
		carList.add(new Vehicle(2, 2));
		carList.add(new Vehicle(2, 4));
		for (int i = 1; i < carList.size(); i++) {
			System.out.printf("car:%d ", i);
			carList.get(i).print();
		}
		Area cap = new Area(carList, 6, 5);
		Area.carList = carList;
		Area.outDirection = 4;
		Area.outmax = 4;
		Area.outmin = 2;

		// the first parameter is the id of the cars, the second one is x coordinate and the third is y coordinate
		System.out.printf("set car 1 location: 4,2, %s\n", cap.setCarSite(1, 4, 3) ? "success" : "fail");
		System.out.printf("set car 2 location: 2,2, %s\n", cap.setCarSite(2, 2, 3) ? "success" : "fail");
		System.out.printf("set car 3 location: 4,2, %s\n", cap.setCarSite(3, 3, 5) ? "success" : "fail");
		System.out.printf("set car 4 location: 2,2, %s\n", cap.setCarSite(4, 4, 4) ? "success" : "fail");
		System.out.printf("set car 5 location: 2,2, %s\n", cap.setCarSite(5, 6, 3) ? "success" : "fail");
		System.out.printf("set car 6 location: 2,2, %s\n", cap.setCarSite(6, 6, 1) ? "success" : "fail");
		System.out.printf("set car 7 location: 4,2, %s\n", cap.setCarSite(7, 6, 5) ? "success" : "fail");
		// priint the layouts
		cap.print();
		System.out.println("target is :" + Area.target);
		System.out.println("target out: " + cap.check());
		ArrayList<int[]> result = bfs(cap);
		// System.out.println(bfs(cap));
		if (result != null)
			for (int[] r : result) {
				cap.move(r[0], r[1]);
				System.out.printf("car: %d, direction: %d\n", r[0], r[1]);
				cap.print();
				System.out.println();
			}
		starTime = System.nanoTime();
		endTime = System.nanoTime();
		runTime = endTime - starTime;
		System.out.printf("run time: %s ns\n", String.valueOf(runTime));
	}
}
