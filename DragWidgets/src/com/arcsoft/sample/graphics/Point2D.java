package com.arcsoft.sample.graphics;

import android.graphics.PointF;

public class Point2D {
	public static double angle360(double angle) {
		double d;
		if (angle < 0.0D) {
			d = 360.0D + angle % -360.0D;
		} else {
			d = angle % 360.0D;
		}
		return d;
	}

	public static double angleBetweenPoints(float x1, float y1, float x2,
			float y2, float snapAngle) {
		double res;
		if ((x1 == x2) && (y1 == y2)) {
			res = 0.0D;
		} else {
			double d1 = Math.atan2(x1 - x2, y1 - y2);
			if (snapAngle > 0.0F) {
				res = snapAngle * (float) Math.round(d1 / snapAngle);
				return res;
			}
			res = angle360(degrees(d1));
		}

		return res;
	}

	public static double angleBetweenPoints(PointF paramPointF1,
			PointF paramPointF2) {
		return angleBetweenPoints(paramPointF1, paramPointF2, 0.0F);
	}

	public static double angleBetweenPoints(PointF pointA, PointF pointB,
			float paramFloat) {
		return angleBetweenPoints(pointA.x, pointA.y, pointB.x, pointB.y,
				paramFloat);
	}

	public static double angleBetweenPoints(float[] p1, float[] p2) {
		return angleBetweenPoints(p1[0], p1[1], p2[0], p2[1], 0.0F);
	}

	public static double degrees(double radians) {
		return 57.295779513082323D * radians;
	}

	public static double distance(float x1, float y1,
			float x2, float y2) {
		return Math.sqrt(Math.pow(x1 - x2, 2.0D)
				+ Math.pow(y1 - y2, 2.0D));
	}

	public static double distance(float[] pointA,
			float[] pointB) {
		return distance(pointA[0], pointA[1],
				pointB[0], pointB[1]);
	}

	public static PointF intersection(PointF[] points1, PointF[] points2) {
		float aX = points1[0].x;
		float aY = points1[0].y;
		float bX = points1[1].x;
		float bY = points1[1].y;

		float cX = points2[0].x;
		float cY = points2[0].y;
		float dX = points2[1].x;
		float dY = points2[1].y;
		return new PointF(((aX * bY - aY * bX) * (cX - dX) - (aX - bX)
				* (cX * dY - cY * dX))
				/ ((aX - bX) * (cY - dY) - (aY - bY) * (cX - dX)),
				((aX * bY - aY * bX) * (cY - dY) - (aY - bY)
						* (cX * dY - cY * dX))
						/ ((aX - bX) * (cY - dY) - (aY - bY) * (cX - dX)));
	}

	public static double radians(double degree) {
		return 0.0174532925199433D * degree;
	}

	public static void rotate(PointF point, double degress) {
		float f1 = point.x;
		float f2 = point.y;
		point.x = (float) (f1 * Math.cos(degress) - f2
				* Math.sin(degress));
		point.y = (float) (f1 * Math.sin(degress) + f2
				* Math.cos(degress));
	}

	public static void rotate(PointF[] points, double degree) {
		for (int i = 0;; i++) {
			if (i >= points.length)
				return;
			rotate(points[i], degree);
		}
	}

	public static void translate(PointF point, float x,
			float y) {
		point.x = (x + point.x);
		point.y = (y + point.y);
	}

	public static void translate(PointF[] points,
			float x, float y) {
		for (int i = 0;; i++) {
			if (i >= points.length)
				return;
			translate(points[i], x, y);
		}
	}
}