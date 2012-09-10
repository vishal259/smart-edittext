package com.arcsoft.sample.graphics;
import android.graphics.PointF;

public class Point2D
{
  public static double angle360(double angle)
  {
    double d;
    if (angle < 0.0D){
    	d = 360.0D + angle % -360.0D;
    }else{
    	 d = angle % 360.0D;
    }
    return d;
  }

  public static double angleBetweenPoints(float x1, float y1, float x2, float y2, float paramFloat5)
  {
    double res;
    if ((x1 == x2) && (y1 == y2)){
    	 res = 0.0D;
    }else{
      double d1 = Math.atan2(x1 - x2, y1 - y2);
      if (paramFloat5 > 0.0F)
      {
        res = paramFloat5 * (float)Math.round(d1 / paramFloat5);
      }
      res = angle360(degrees(d1));
    }
    
    return res;
  }

  public static double angleBetweenPoints(PointF paramPointF1, PointF paramPointF2)
  {
    return angleBetweenPoints(paramPointF1, paramPointF2, 0.0F);
  }

  public static double angleBetweenPoints(PointF paramPointF1, PointF paramPointF2, float paramFloat)
  {
    return angleBetweenPoints(paramPointF1.x, paramPointF1.y, paramPointF2.x, paramPointF2.y, paramFloat);
  }

  public static double angleBetweenPoints(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
  {
    return angleBetweenPoints(paramArrayOfFloat1[0], paramArrayOfFloat1[1], paramArrayOfFloat2[0], paramArrayOfFloat2[1], 0.0F);
  }

  public static double degrees(double paramDouble)
  {
    return 57.295779513082323D * paramDouble;
  }

  public static double distance(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    return Math.sqrt(Math.pow(paramFloat1 - paramFloat3, 2.0D) + Math.pow(paramFloat2 - paramFloat4, 2.0D));
  }

  public static double distance(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
  {
    return distance(paramArrayOfFloat1[0], paramArrayOfFloat1[1], paramArrayOfFloat2[0], paramArrayOfFloat2[1]);
  }

  public static PointF intersection(PointF[] paramArrayOfPointF1, PointF[] paramArrayOfPointF2)
  {
    float f1 = paramArrayOfPointF1[0].x;
    float f2 = paramArrayOfPointF1[0].y;
    float f3 = paramArrayOfPointF1[1].x;
    float f4 = paramArrayOfPointF1[1].y;
    float f5 = paramArrayOfPointF2[0].x;
    float f6 = paramArrayOfPointF2[0].y;
    float f7 = paramArrayOfPointF2[1].x;
    float f8 = paramArrayOfPointF2[1].y;
    return new PointF(((f1 * f4 - f2 * f3) * (f5 - f7) - (f1 - f3) * (f5 * f8 - f6 * f7)) / ((f1 - f3) * (f6 - f8) - (f2 - f4) * (f5 - f7)), ((f1 * f4 - f2 * f3) * (f6 - f8) - (f2 - f4) * (f5 * f8 - f6 * f7)) / ((f1 - f3) * (f6 - f8) - (f2 - f4) * (f5 - f7)));
  }

  public static double radians(double paramDouble)
  {
    return 0.0174532925199433D * paramDouble;
  }

  public static void rotate(PointF paramPointF, double paramDouble)
  {
    float f1 = paramPointF.x;
    float f2 = paramPointF.y;
    paramPointF.x = (float)(f1 * Math.cos(paramDouble) - f2 * Math.sin(paramDouble));
    paramPointF.y = (float)(f1 * Math.sin(paramDouble) + f2 * Math.cos(paramDouble));
  }

  public static void rotate(PointF[] paramArrayOfPointF, double paramDouble)
  {
    for (int i = 0; ; i++)
    {
      if (i >= paramArrayOfPointF.length)
        return;
      rotate(paramArrayOfPointF[i], paramDouble);
    }
  }

  public static PointF sizeOfRect(PointF[] paramArrayOfPointF)
  {
    return new PointF(paramArrayOfPointF[1].x - paramArrayOfPointF[0].x, paramArrayOfPointF[3].y - paramArrayOfPointF[0].y);
  }

  public static void translate(PointF paramPointF, float paramFloat1, float paramFloat2)
  {
    paramPointF.x = (paramFloat1 + paramPointF.x);
    paramPointF.y = (paramFloat2 + paramPointF.y);
  }

  public static void translate(PointF[] paramArrayOfPointF, float paramFloat1, float paramFloat2)
  {
    for (int i = 0; ; i++)
    {
      if (i >= paramArrayOfPointF.length)
        return;
      translate(paramArrayOfPointF[i], paramFloat1, paramFloat2);
    }
  }
}