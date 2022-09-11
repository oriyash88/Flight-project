package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.*;


public class TimeSeries {
     public int rowCounter;
	String [] header = null;
	HashMap<String, Vector<Float>> arr = new HashMap<>();

	public TimeSeries(String csvFileName)
	{
		int numOfVectors = 0;
		rowCounter = 0;
		int j=0;
		String line = "";

		//static String [] header = null;
		String [] lines = null;
		Vector<Vector<Float>> vf = new Vector<Vector<Float>>();

		try
		{
			BufferedReader lineRead = new BufferedReader(new FileReader(csvFileName));
			while ((line = lineRead.readLine()) != null)
			{
				if (rowCounter == 0)
				{
					header = line.split(",");
					numOfVectors = header.length;
					//for(int i=0; i<numOfVectors; i++)



				}
				else
				{
					lines = line.split(",");
					for(int i=0; i<numOfVectors; i++)
					{
						if(rowCounter ==1) {
							Vector<Float> f = new Vector<>();
							vf.add(i, f);
						}

						vf.get(i).add(j, Float.valueOf(lines[i]));
					}
					j++;

				}
				rowCounter ++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		for(int i=0; i<numOfVectors;i++)
		{
			arr.put(header[i],vf.get(i));
		}

	}

	public Vector<Float> getv(String str)
	{
		return arr.get(str);
	}


	public int getRowSize() {
		return rowCounter;
	}

	public float[] swopToFloatArray(Vector<Float> v)
	{
		float [] f = new float[v.size()];
		for(int i=0; i< v.size(); i++)
		{
			f[i] = v.get(i);
		}
		return f;
	}



}

