package com.knuinnovation.knuattendacechecker;

public class Point {
	
	public double x;
	public double y;
	
	Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean equals(Object obj) {
		if (this.x == ((Point) obj).x && this.y == ((Point) obj).y)
			return true;
		else 
			return false;
	}

	@Override
	public String toString() {
		return x + "," + y;
	}
}
