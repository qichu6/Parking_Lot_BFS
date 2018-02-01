package project4;

public class Vehicle {
	int number;
	int length;
	int direction;
	// 1 2 3 4 : up down left right
	boolean target;

	// target car
	public Vehicle(int length, int direction, boolean target) {
		this.length = length;
		this.direction = direction;
		this.target = target;
	}

	// not target car
	public Vehicle(int len, int dire) {
		target = false;
		length = len;
		direction = dire;
	}

	public void print() {
		System.out.printf("length: %d, direction: %d%s\n", this.length, this.direction, this.target ? ",target" : "");
	}

}
