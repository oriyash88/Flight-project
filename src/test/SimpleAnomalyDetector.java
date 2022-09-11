package test;

import java.util.*;

import static java.lang.Math.abs;
import static test.StatLib.*;

public class SimpleAnomalyDetector implements TimeSeriesAnomalyDetector {

	List<CorrelatedFeatures> coorList = new ArrayList<>();
	private float correlationThreshold;
	@Override
	public void learnNormal(TimeSeries ts)
	{
		//ArrayList<CorrelatedFeatures> coorList = new ArrayList<>();
		int size = ts.arr.size(), flag=0;
        float corrnormal = (float) 0.9;
		for(int i=0; i<size; i++)
		{
			float m=0;
			int c=-1;
			for(int j=i+1; j<size; j++) {
				float p = abs(pearson(ts.swopToFloatArray(ts.arr.get(ts.header[i])),
						ts.swopToFloatArray(ts.arr.get(ts.header[j]))));
				if (p > m) {
					m = p;
					c = j;
				}
			}
			if((c != -1)&&(m>corrnormal))
			{
				String featuer1 = ts.header[i].toString();
				String featuer2 = ts.header[c].toString();
				float corrlation = m;
				int sizeof = ts.arr.get(ts.header[i]).size();
				Point[] points = new Point[sizeof];
				for(int s =0; s<sizeof; s++)
				{
					float x,y;
					x = ts.arr.get(ts.header[i]).get(s);
					y = ts.arr.get(ts.header[c]).get(s);
					points[s] = new Point(x,y);
				}
				Line lin_reg = linear_reg(points);
				float maxdec=0;
				for(int z=0; z<sizeof;z++)
				{
					float t = dev(points[z],lin_reg);
					if (t > maxdec)
						maxdec =t;
				}
				float threshold = (float) (maxdec*1.1);
				coorList.add(new CorrelatedFeatures(featuer1, featuer2, corrlation, lin_reg, threshold));
			}

			}
	}


	@Override
	public List<AnomalyReport> detect(TimeSeries ts) {
		List<AnomalyReport> la= new ArrayList<>();
		String des;
		float [] x;
		float [] y;
		for(CorrelatedFeatures cor:coorList) {
			x=ts.swopToFloatArray(ts.getv(cor.feature1));
			y=ts.swopToFloatArray(ts.getv(cor.feature2));

			for (int i=0;i< x.length;i++) {
				if (abs(y[i] - cor.lin_reg.f(x[i])) > cor.threshold) {
					des = cor.feature1 + "-" + cor.feature2;
					la.add(new AnomalyReport(des, i + 1));
				}
			}
		}
		return la;

	}

	public void setCorrelationThreshold(float threshold) {
		correlationThreshold=threshold;
	}


	public List<CorrelatedFeatures> getNormalModel(){
		return coorList;
		}
	}

