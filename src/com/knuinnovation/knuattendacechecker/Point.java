package com.knuinnovation.knuattendacechecker;

/**
 * This class represents a 2D point, used by the Trilaterator algorithm
 * @author Gábor Proksa
 *
 */
public class Point {
	
	public double x;
	public double y;
	
	Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Two points are equal if both coordinates are equal to each other
	 */
	@Override
	public boolean equals(Object obj) {
		if (this.x == ((Point) obj).x && this.y == ((Point) obj).y)
			return true;
		else 
			return false;
	}

	/**
	 * For displaying in logs
	 */
	@Override
	public String toString() {
		return x + "," + y;
	}
}
