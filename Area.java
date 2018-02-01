package project4;

import java.util.ArrayList;
import java.util.HashMap;

public class Area {
	static ArrayList<Vehicle> carList;
	static int target;
	static int outDirection;// exit direction
	// 1 2 3 4 : up, down, left, right
	static int outmax;// the location of exit
	static int outmin;
	ArrayList<Integer> xList;
	ArrayList<Integer> yList;
	static int areaHight;
	static int areaWidth;
	int[][] area;
	int lastOptVehicle;// store the changing process
	int lastDriection;
	int lastArea;

	// store corresponding x, y coordinates of current layout
	public Area() {
		xList = new ArrayList<Integer>();
		yList = new ArrayList<Integer>();
	}

	public Area(int width, int hight) {
		xList = new ArrayList<Integer>();
		yList = new ArrayList<Integer>();
		areaHight = hight;
		areaWidth = width;
		area = new int[width + 1][hight + 1];
	}

	// generate layout
	public Area(ArrayList<Vehicle> carList, int width, int hight) {
		xList = new ArrayList<Integer>();
		yList = new ArrayList<Integer>();
		Area.areaHight = hight;
		Area.areaWidth = width;
		area = new int[width + 1][hight + 1];
		// take up place of cars and place in order
		for (int i = 0; i < carList.size(); i++) {
			if (carList.get(i).target)
				target = i;
			xList.add(-1);
			yList.add(-1);
		}
	}

	/*
	 * public Area(ArrayList<Integer> xList, ArrayList<Integer> yList, int
	 * width, int hight, int lastOpeVehicle, int lastDriection) { super();
	 * this.xList = new ArrayList<Integer>(xList); this.yList = new
	 * ArrayList<Integer>(yList); Area.areaHight = hight; Area.areaWidth =
	 * width; area = new int[width + 1][hight + 1]; this.lastOptVehicle =
	 * lastOpeVehicle; this.lastDriection = lastDriection; }
	 */

	// the layout should be copied before next operation
	public Area(Area area) {
		
		this.xList = new ArrayList<Integer>(area.xList);
		this.yList = new ArrayList<Integer>(area.yList);
		this.area = new int[areaWidth + 1][];
		// copying method of two-dimension array
		for (int i = 0; i <= areaWidth; i++) {
			this.area[i] = area.area[i].clone();
		}
		this.lastOptVehicle = area.lastOptVehicle;
		this.lastDriection = area.lastDriection;
		this.lastArea = area.lastArea;
	}

	// trace back to see whether the car can be placed like this. if not, set zero as storage order
	public boolean resetArea(ArrayList<Integer> changedX, ArrayList<Integer> changedY) {
		for (int i = 0; i < changedX.size(); i++) {
			this.area[changedX.get(i)][changedY.get(i)] = 0;
		}
		return false;
	}

	public boolean setCarSite(int vehicle, int x, int y) {
		if (vehicle > carList.size())
			return false;
		// one car that need to set location
		Vehicle car = carList.get(vehicle);
		xList.set(vehicle, x);
		yList.set(vehicle, y);
		ArrayList<Integer> changedX = new ArrayList<Integer>();
		ArrayList<Integer> changedY = new ArrayList<Integer>();
		if (car.target)
			target = vehicle;
		// set one car and sign its coordinates and direction
		switch (car.direction) {
		default:
		case 1:
			// 1, the head of the car is up上，(x,y) is the coordinates
			for (int i = 0; i < car.length; i++)
				if (x <= areaWidth && x >= 0 && y - i >= 0 && y - i <= areaHight && area[x][y - i] == 0) {
					changedX.add(x);
					changedY.add(y - i);
					area[x][y - i] = vehicle;
				} else
					return resetArea(changedX, changedY);
			break;
		case 2:
			// 2, the head of the car is down
			for (int i = 0; i < car.length; i++)
				if (x <= areaWidth && x >= 0 && y + i >= 0 && y + i <= areaHight && area[x][y + i] == 0) {
					changedX.add(x);
					changedY.add(y + i);
					area[x][y + i] = vehicle;
				} else
					return resetArea(changedX, changedY);
			break;
		case 3:
			// 3, the head of the car is left
			for (int i = 0; i < car.length; i++)
				if (x + i <= areaWidth && x + i >= 0 && y >= 0 && y <= areaHight && area[x + i][y] == 0) {
					changedX.add(x + i);
					changedY.add(y);
					area[x + i][y] = vehicle;
				} else
					return resetArea(changedX, changedY);
			break;
		case 4:
			// 4, the head of the car is right 
			for (int i = 0; i < car.length; i++)
				if (x - i <= areaWidth && x - i >= 0 && y >= 0 && y <= areaHight && area[x - i][y] == 0) {
					changedX.add(x - i);
					changedY.add(y);
					area[x - i][y] = vehicle;
				} else
					return resetArea(changedX, changedY);
			break;
		}
		return true;
	}

	// generates all the possible movement of the cars in one layout and it will be used to find all possibilities of the next node in BFS algorithm
	public HashMap<Integer, ArrayList<Integer>> getNextList() {
		HashMap<Integer, ArrayList<Integer>> result = new HashMap<Integer, ArrayList<Integer>>();
		// generate cars(including direction) can move in the next step
		for (int i = 1; i < carList.size(); i++) {
			int x = xList.get(i);
			int y = yList.get(i);
			int length = carList.get(i).length;
			ArrayList<Integer> list = new ArrayList<Integer>();
			switch (carList.get(i).direction) {
			default:
			//case 1,2,3,4 represents the four directions of cars
			case 1:
				// can move upward
				if (x <= areaWidth && x > 0 && y + 1 <= areaHight && area[x][y + 1] == 0)
					list.add(1);
				// can move downward
				if (x <= areaWidth && x > 0 && y - length > 0 && area[x][y - length] == 0)
					list.add(2);
				// can move and the results put into result sets
				if (!list.isEmpty())
					result.put(i, list);
				break;
			case 2:
				if (x <= areaWidth && x > 0 && y + length <= areaHight && area[x][y + length] == 0)
					list.add(1);
				if (x <= areaWidth && x > 0 && y - 1 > 0 && area[x][y - 1] == 0)
					list.add(2);
				if (!list.isEmpty())
					result.put(i, list);
				break;
			case 3:
				if (y <= areaHight && y > 0 && x - 1 > 0 && area[x - 1][y] == 0)
					list.add(3);
				if (y <= areaHight && y > 0 && x + length <= areaWidth && area[x + length][y] == 0)
					list.add(4);
				if (!list.isEmpty())
					result.put(i, list);
				break;
			case 4:
				if (y <= areaHight && y > 0 && x - length > 0 && area[x - length][y] == 0)
					list.add(3);
				if (y <= areaHight && y > 0 && x + 1 <= areaWidth && area[x + 1][y] == 0)
					list.add(4);
				if (!list.isEmpty())
					result.put(i, list);
				break;
			}
		}
		return result;
	}

	// how to move one certain car
	public boolean move(int car, int direction) {
		// judge which direction one car moves to
		if ((carList.get(car).direction > 2 && direction <= 2) || (carList.get(car).direction <= 2 && direction > 2))
			return false;
		int x = xList.get(car);
		int y = yList.get(car);
		int length = carList.get(car).length;
		switch (direction) {
		default:
		case 1:
			if (carList.get(car).direction == 1) {
				if (y + 1 <= Area.areaHight && area[x][y + 1] == 0) {
					area[x][y + 1] = car;
					area[x][y - length + 1] = 0;
					yList.set(car, y + 1);
				} else
					return false;
			} else {
				if (y + length <= Area.areaHight && area[x][y + length] == 0) {
					area[x][y + length] = car;
					area[x][y] = 0;
					yList.set(car, y + 1);
				} else
					return false;
			}
			break;
		case 2:
			if (carList.get(car).direction == 2) {
				if (y - 1 > 0 && area[x][y - 1] == 0) {
					area[x][y - 1] = car;
					area[x][y + length - 1] = 0;
					yList.set(car, y - 1);
				} else
					return false;
			} else {
				if (y - length > 0 && area[x][y - length] == 0) {
					area[x][y - length] = car;
					area[x][y] = 0;
					yList.set(car, y - 1);
				} else
					return false;
			}
			break;
		case 3:
			if (carList.get(car).direction == 3) {
				if (x - 1 > 0 && area[x - 1][y] == 0) {
					area[x - 1][y] = car;
					area[x + length - 1][y] = 0;
					xList.set(car, x - 1);
				} else
					return false;
			} else {
				if (x - length > 0 && area[x - length][y] == 0) {
					area[x - length][y] = car;
					area[x][y] = 0;
					xList.set(car, x - 1);
				} else
					return false;
			}
			break;
		case 4:
			if (carList.get(car).direction == 4) {
				if (x + 1 <= Area.areaWidth && area[x + 1][y] == 0) {
					area[x + 1][y] = car;
					area[x - length + 1][y] = 0;
					xList.set(car, x + 1);
				} else
					return false;
			} else {
				if (x + length <= Area.areaWidth && area[x + length][y] == 0) {
					area[x + length][y] = car;
					area[x][y] = 0;
					xList.set(car, x + 1);
				} else
					return false;
			}
			break;
		}
		return true;
	}

	// check whether the target car is out or not
	public boolean check() {
		// check whether setting the target car
		if (target == 0)
			for (int i = 1; i < carList.size(); i++) {
				if (carList.get(i).target)
					target = i;
			}
		// can't move out
		if ((carList.get(target).direction > 2 && Area.outDirection <= 2)
				|| (carList.get(target).direction <= 2 && Area.outDirection > 2))
			return false;
		switch (Area.outDirection) {
		default:
		case 1:
			for (int i = Area.outmin; i <= Area.outmax; i++) {
				if (area[i][Area.areaHight] == target)
					return true;
			}
			break;
		case 2:
			for (int i = Area.outmin; i <= Area.outmax; i++) {
				if (area[i][1] == target)
					return true;
			}
			break;
		case 3:
			for (int i = Area.outmin; i <= Area.outmax; i++) {
				if (area[1][i] == target)
					return true;
			}
			break;
		case 4:
			for (int i = Area.outmin; i <= Area.outmax; i++) {
				if (area[Area.areaWidth][i] == target)
					return true;
			}
			break;
		}
		return false;
	}

	// print according to coordinates, from top left corner to the bottom right corner
	public void print() {
		System.out.printf("= ");
		if (Area.outDirection == 1) {
			for (int j = 1; j <= Area.areaWidth; j++) {
				if (j <= Area.outmax && j >= Area.outmin)
					System.out.printf("* ");
				else
					System.out.printf("= ");
			}
		} else
			for (int j = 1; j <= Area.areaWidth; j++)
				System.out.printf("= ");
		System.out.printf("= ");
		System.out.println();
		for (int i = Area.areaHight; i > 0; i--) {
			if (Area.outDirection == 3 && i <= Area.outmax && i >= Area.outmin)
				System.out.printf("* ");
			else
				System.out.printf("| ");
			for (int j = 1; j <= Area.areaWidth; j++) {
				System.out.printf("%d ", area[j][i]);
			}
			if (Area.outDirection == 4 && i <= Area.outmax && i >= Area.outmin)
				System.out.printf("* ");
			else
				System.out.printf("| ");
			System.out.println();
		}
		System.out.printf("= ");
		if (Area.outDirection == 2) {
			for (int j = 1; j <= Area.areaWidth; j++) {
				if (j <= Area.outmax && j >= Area.outmin)
					System.out.printf("* ");
				else
					System.out.printf("= ");
			}
		} else
			for (int j = 1; j <= Area.areaWidth; j++)
				System.out.printf("= ");
		System.out.printf("= ");
		System.out.println();
	}

	// delete the repetitive ones
	public boolean same(Area area) {
		if (this.xList.equals(area.xList) && this.yList.equals(area.yList))
			return true;
		return false;
	}
}
