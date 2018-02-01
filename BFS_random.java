package project4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class BFS_random {

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
		int searchCount = 1;
		while (!areaQueue.isEmpty()) {
			int opeArea = areaQueue.get(0);
			Area opearea = areaList.get(opeArea);
			areaQueue.remove(0);
			HashMap<Integer, ArrayList<Integer>> nextList = areaList.get(opeArea).getNextList();
			for (int carID : nextList.keySet()) {
				for (int direction : nextList.get(carID)) {
					searchCount++;
					Area nextArea = new Area(opearea);
					nextArea.lastArea = opeArea;
					nextArea.lastOptVehicle = carID;
					nextArea.lastDriection = direction;
					if (nextArea.move(carID, direction)) {
						int areaID = inList(areaList, nextArea);
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
				if (target != -1)
					break;
			}
			if (target != -1)
				break;
		}
		// System.out.println(target);
		System.out.printf("nodeCount:%d, searchCount:%d ", areaList.size(), searchCount);
		if (target == -1) {
			System.out.print(" noSolution,");
			return null;
		}
		System.out.print(" hasSolution,");
		ArrayList<int[]> result = new ArrayList<int[]>();
		int opeArea = target;
		while (opeArea != 0) {
			int[] move = new int[2];
			move[0] = areaList.get(opeArea).lastOptVehicle;
			move[1] = areaList.get(opeArea).lastDriection;
			result.add(move);
			opeArea = areaList.get(opeArea).lastArea;
		}
		Collections.reverse(result);
		return result;
	}

	// initialize in different situation
	public static void init(Area area, int carCount, int areaWidth, int areaHight, int maxLength, int seed) {
		int maxSetTimes = 10;// the times of placing the car without changing the length
		int maxShortenCount = 5;// after 10 times placing repeatly, the length of the car will decrease by 1
		// set random seed
		Random r = new Random(seed);
		// set target，the id of target
		int target = r.nextInt(carCount) + 1;
		// set static variables
		Area.target = target;
		Area.areaHight = areaHight;
		Area.areaWidth = areaWidth;
		Area.carList.clear();
		// clean area's data
		// area.clear();
		// clear the layout and set the layout again according to height and width
		area.xList.clear();
		area.yList.clear();
		area.area = null;
		area.area = new int[Area.areaWidth + 1][Area.areaHight + 1];
		area.lastArea = 0;
		area.lastDriection = 0;
		area.lastOptVehicle = 0;
		// set direction and length of cars，initialize the length and direction of the cars
		for (int i = 0; i <= carCount; i++) {
			Vehicle car = new Vehicle(r.nextInt(maxLength) + 1, r.nextInt(4) + 1);
			Area.carList.add(car);
			area.xList.add(0);
			area.yList.add(0);
		}
		// set target car
		Area.carList.get(target).target = true;
		// set x,y of cars，two counters
		for (int i = 1; i < carCount + 1; i++) {
			int resetCount = 0;
			int shortenCount = 0;
			while (true) {
				//System.out.println(1);
				if (resetCount >= maxSetTimes) {
					shortenCount++;
					resetCount = 0;
					// if reassignment of x, y for woo many times, the length of cars will be decreased and coordinates will be reset
					if (Area.carList.get(i).length > 2)
						Area.carList.get(i).length--;
					if (shortenCount >= maxShortenCount) {
						Area.carList.get(i).length = 1;
					}
				}
				if (area.setCarSite(i, r.nextInt(areaWidth) + 1, r.nextInt(areaHight) + 1)) {
					break;
				}
				resetCount++;
			}
		}
		// set out direction same as target
		Area.outDirection = Area.carList.get(target).direction;
		// set out things，outmin和outmax
		int targetX = area.xList.get(target);
		int targetY = area.yList.get(target);
		// set x or y according to direction
		if (Area.outDirection < 3) {
			Area.outmax = targetX;
			Area.outmin = targetX;
		} else {
			Area.outmax = targetY;
			Area.outmin = targetY;
		}
	}

	// run the program in random layout随机场景运行程序
	public static void algorithmPerformanceTest(int hight, int width, int carSize, int maxLength, int seed) {
		long starTime;
		long endTime;
		ArrayList<Vehicle> carList = new ArrayList<Vehicle>();
		Area cap = new Area(carList, width, hight);
		Area.carList = carList;
		// the number of seed: 1000，generate the layout
		ArrayList<Integer> seeds = generateTestData(1000, seed);
		// the number of cycling times：5000
		for (int i = 0; i < 5000; i++) {
			
			init(cap, carSize, width, hight, maxLength, seeds.get(i));
			System.out.println(i);
			System.out.printf(" areaWidth:%d, areaHight:%d, carCount:%d, maxCarLength:%d,", width, hight, carSize,
					maxLength);
			starTime = System.nanoTime();
			bfs(cap);
			endTime = System.nanoTime();
			System.out.printf(" runTime:%d ns\n", endTime - starTime);
		}
	}

	public static void main(String args[]) {
		int hight = 6;
		int width = 5;
		int carSize = 6;
		int maxLength = 3;
		ArrayList<Integer> seeds = generateTestData(1000, 5);
		algorithmPerformanceTest(hight, width, carSize, maxLength, seeds.get(0));
	}
}
