package test;


public class StatLib {

	// simple average
	public static float avg(float[] x)
	{
		float sum =0;
		for(int i=0; i<x.length; i++)
		{
			sum = sum + x[i];
		}
		return (sum/x.length);
	}

	// returns the variance of X and Y
	public static float var(float[] x)
	{
		float xi = 0, xi2 =0;
		float Var;
		for(int i=0; i< x.length; i++)
		{
			xi = xi + x[i];
		}
		float u = ((float)1/x.length) * xi;

		for(int j=0; j<x.length; j++)
		{
			xi2 = xi2 + x[j]*x[j];
		}
		Var = ((float)xi2/x.length) - u*u;
		return Var;
	}

	// returns the covariance of X and Y
	public static float cov(float[] x, float[] y)
	{
		float avg_x, avg_y, avg_xy;

		avg_x = avg(x);
		avg_y = avg(y);

		float sum=0;
		float [] new_arr = new float[x.length];
		for(int i=0; i<x.length; i++)
		{
			new_arr[i] = (x[i]*y[i]);
		}
		avg_xy = avg(new_arr);
		return (avg_xy - (avg_x*avg_y));
	}


	// returns the Pearson correlation coefficient of X and Y
	public static float pearson(float[] x, float[] y)
	{
		float Cov = cov(x,y);
		float st_x, st_y;
		st_x = (var(x));
		st_x =(float)Math.sqrt(st_x);
		st_y = (var(y));
		st_y =(float)Math.sqrt(st_y);

		return Cov/(st_y*st_x);
	}

	// performs a linear regression and returns the line equation
	public static Line linear_reg(Point[] points)
	{
		float a,b;
		float [] x_arr = new float[points.length];
		float [] y_arr = new float[points.length];
		for(int i=0; i<points.length; i++)
		{
			x_arr[i] = points[i].x;
			y_arr[i] = points[i].y;
		}
		a = cov(x_arr, y_arr)/var(x_arr);
		float b_x = avg(x_arr);
		float b_y = avg(y_arr);

		b = b_y - (a*b_x);

		Line l = new Line(a,b);
		return  l;

	}

	// returns the deviation between point p and the line equation of the points
	public static float dev(Point p,Point[] points)
	{
		Line l = linear_reg(points);
		float Dev = dev(p,l);
		return Dev;
	}

	// returns the deviation between point p and the line
	public static float dev(Point p,Line l)
	{
		float Devi = Math.abs(l.f(p.x)-p.y);
		return Devi;
	}

}
