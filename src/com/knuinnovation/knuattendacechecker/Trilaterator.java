package com.knuinnovation.knuattendacechecker;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;

import android.util.Log;

import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

public class Trilaterator {
	public static final String TAG = "Trilaterator";

	public static Point calculatePosition(ArrayList<Point> data, ArrayList<Double> range) {
		
		Log.v(TAG, "Trilateration started\n Points: " + data.toString() + "\n Ranges: " + range.toString());
		
		int n = data.size();
		double[][] distanceMatrix = new double[n][n];
		double[][] intersectMatrix = {{1.0, 1.0, 1.0}, {1.0, 1.0, 1.0}, {1.0, 1.0, 1.0}};
		double[][] identityMatrix = {{1.0, 0.0, 0.0}, {0.0, 1.0, 0.0}, {0.0, 0.0, 1.0}};
		
		ArrayList<Point> intersectPoints = new ArrayList<Point>();
		
		// calculate pairwise distances
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				distanceMatrix[i][j] = distance(data.get(i).x, data.get(i).y, data.get(j).x, data.get(j).y);
			}
		}
		
		// Range correction until all circles intersect
		while (!Arrays.deepEquals(intersectMatrix, identityMatrix)) {
			for (int i = 0; i < n; i++) {
				for (int j = i+1; j < n; j++) {
					if (distanceMatrix[i][j] > range.get(i) + range.get(j)) {
						intersectMatrix[i][j] = 1.0;
						intersectMatrix[j][i] = 1.0;
					}
					else if (distanceMatrix[i][j] < Math.abs(range.get(i) - range.get(j))) {
						intersectMatrix[i][j] = -1.0;
						intersectMatrix[j][i] = -1.0;
					}
					else {
						intersectMatrix[i][j] = 0.0;
						intersectMatrix[j][i] = 0.0;
					}
				}
			}

			for (int i = 0; i < n/2 + 1; i++) {
				for (int j = i+1; j < n; j++) {

					int k = (range.get(i) > range.get(j)) ? i : j;

					if (intersectMatrix[i][j] == 1.0)
						range.set(k, range.get(k) + 0.1);
					else if (intersectMatrix[i][j] == -1.0)
						range.set(k, range.get(k) - 0.1);
				}
			}
		}
		
		// Calculate all intersections
		for (int i = 0; i < n/2 + 1; i++) {
			for (int j = i + 1; j < n; j++) {
				intersectPoints.addAll(circcirc(data.get(i).x, data.get(i).y, data.get(j).x, data.get(j).y, range.get(i), range.get(j)));
			}
		}
		
		// Build Kmeans clusterer
		SimpleKMeans kmeans = new SimpleKMeans();
		kmeans.setSeed(10);
		kmeans.setPreserveInstancesOrder(true);
		try {
			kmeans.setNumClusters(3);
		} catch (Exception e) {
			return new Point(0.0, 0.0);
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("@RELATION points\n\n@ATTRIBUTE x NUMERIC\n@ATTRIBUTE y NUMERIC\n\n@DATA\n");
		for (Point p : intersectPoints) {
			sb.append(p + "\n");
		}
		
		String s = sb.toString();
		//System.out.println(s);
		StringReader sr = new StringReader(s);
		BufferedReader br = new BufferedReader(sr);
		
		try {
			Instances inst = new Instances(br);
			kmeans.buildClusterer(inst);
			
			Instances clusterCentroids = kmeans.getClusterCentroids();
			//double[] clusterSizes = kmeans.getClusterSizes();
			int[] clusterSizes = kmeans.getClusterSizes();
			
			double max = clusterSizes[0];
			int maxindex = 0;
			for (int i = 0; i < clusterSizes.length; i++) {
				if (clusterSizes[i] > max) {
					max = clusterSizes[i];
					maxindex = i;
				}
			}
			
			
			//Point position = new Point(clusterCentroids.get(maxindex).value(0), clusterCentroids.get(maxindex).value(1));
			Point position = new Point(clusterCentroids.instance(maxindex).value(0), clusterCentroids.instance(maxindex).value(1));
			
			Log.v(TAG, "Trilateration finihed\n Position: " + position.toString());
			
			return position;
			
		} catch (Exception e) {
			return new Point(-1.0,-1.0);
		}
		
	}
			

	// Calculate distance of two points
	public static final double distance(double p1x, double p1y, double p2x, double p2y) {
		return Math.sqrt(Math.pow(p1x-p2x, 2.0) + Math.pow(p1y-p2y, 2.0));
	}

	// Calculate intersections of two circles
	public static final ArrayList<Point> circcirc(double x1, double y1, double x2, double y2, double r1, double r2) {

		double r3 = distance(x1, y1, x2, y2);
		double anought = Math.atan2(y2-y1, x2-x1);
		double aone = Math.acos( -( (Math.pow(r2, 2.0) - Math.pow(r1, 2.0) - Math.pow(r3, 2.0)) / (2 * r1 * r3)));
		double alpha1 = anought + aone;
		double alpha2 = anought - aone;

		ArrayList<Point> result = new ArrayList<Point>();

		Point out1 = new Point(x1 + r1 * Math.cos(alpha1), y1 + r1 * Math.sin(alpha1));
		Point out2 = new Point(x1 + r1 * Math.cos(alpha2), y1 + r1 * Math.sin(alpha2));

		result.add(out1);
		result.add(out2);

		return result;
	}

	
}
