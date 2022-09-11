package test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.HashMap;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Commands {
	
	// Default IO interface
	public interface DefaultIO{
		public String readText();
		public void write(String text);
		public float readVal();
		public void write(float val);

		// you may add default methods here

		public default void readAndFile(String fileName, String doneStr) {
			try {
				PrintWriter out = new PrintWriter(new FileWriter(fileName));
				String line;
				while(!(line=readText()).equals(doneStr)) {
					out.println(line);
				}
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public void PrintMenu() {
		dio.write(("Welcome to the Anomaly Detection Server.\n" +
				"Please choose an option:\n"));
	}

	
	// the default IO to be used in all commands
	DefaultIO dio;
	public Commands(DefaultIO dio) {
		this.dio=dio;
	}

	// you may add other helper classes here

	private class Anomal{
		public long start,end;
		public String description;
		public boolean tp;
	};



	private class SharedState{
		//Data Member:
		public float correlatedFeatures;
		public int rowfilesize;
		List<test.AnomalyReport> reports;
		ArrayList<Anomal> newAnomal=new ArrayList<>();
		HashMap <Integer,Command> commands = new HashMap<>();

		//CTOR:
		public SharedState(){
			correlatedFeatures= 0.9f;
			rowfilesize = 0;
		}
		//Methods:
		public void SetCommand(Integer index, Command cmd) {commands.put(index, cmd);}
	}

	private  SharedState sharedState=new SharedState();

	
	// Command abstract class
	public abstract class Command{
		protected String description;
		
		public Command(String description) {
			this.description=description;
		}
		
		public abstract void execute();
	}
	
	// Command class for example:
	public class ExampleCommand extends Command{

		public ExampleCommand() {
			super("this is an example of command");
		}

		@Override
		public void execute() {
			dio.write(description);
		}		
	}

///////////////////////////////////////////// 1 /////////////////////////////////////////////////

	public class UploadCommand extends Command{

		public UploadCommand()
		{
			super("upload a time series csv file");
			sharedState.SetCommand(1, this);
		}

		@Override
		public void execute() {
			dio.write("Please upload your local train CSV file.\n");
			dio.readAndFile("anomalyTrain.csv", "done");
			dio.write("Upload complete.\n");
			dio.write("Please upload your local test CSV file.\n");
			dio.readAndFile("anomalyTest.csv", "done");
			dio.write("Upload complete.\n");
		}
	}
//////////////////////////////////////////// 2 /////////////////////////////////////////////////

	public class AlgorithmSettings extends Command{

		public AlgorithmSettings() {
			super("algorithm settings");
		}

		@Override
		public void execute() {
			boolean flag = true;
			while (flag) {
				dio.write("The current correlation threshold is "+sharedState.correlatedFeatures+"\n");
				dio.write("Type a new threshold\n");
				float threshold = dio.readVal();
				if (threshold > 0 && threshold < 1) {
					sharedState.correlatedFeatures = threshold;
					flag = false;
				}
				else
					dio.write("please choose a value between 0 and 1.\n");
			}
		}
	}
///////////////////////////////////////////// 3 /////////////////////////////////////////////////

	public class AnomalyDetectiomCommand extends Command{

		public AnomalyDetectiomCommand() {
			super("detect anomalies");
		}

		@Override
		public void execute() {
			TimeSeries train=new TimeSeries("anomalyTrain.csv");
			TimeSeries test=new TimeSeries("anomalyTest.csv");
			sharedState.rowfilesize = test.getRowSize();
			SimpleAnomalyDetector sl = new SimpleAnomalyDetector();
			sl.setCorrelationThreshold(sharedState.correlatedFeatures);
			sl.learnNormal(train);
			sharedState.reports = sl.detect(test);

			Anomal al = new Anomal();
			al.start=0;
			al.end=0;
			al.description="";
			al.tp=false;
			for(AnomalyReport ar : sharedState.reports) {
				if(ar.timeStep==al.end+1 && ar.description.equals(al.description))
					al.end++;
				else{
					sharedState.newAnomal.add(al);
					al=new Anomal();
					al.start=ar.timeStep;
					al.end=al.start;
					al.description=ar.description;
				}

			};
			sharedState.newAnomal.add(al);
			sharedState.newAnomal.remove(0);

			dio.write("anomaly detection complete.\n");
		}
	}
///////////////////////////////////////////// 4 /////////////////////////////////////////////////

	public class DisplayCommand extends Command {
		public DisplayCommand() {
			super("display results");
		}

		@Override
		public void execute() {
			int size = sharedState.reports.size();
			for(int i=0; i<size; i++)
			{
				String str = sharedState.reports.get(i).description;
				long ts = sharedState.reports.get(i).timeStep;
				dio.write(""+ts);
				dio.write("\t ");
				dio.write(str+"\n");

			}
			dio.write("Done.\n");

		}
	}

///////////////////////////////////////////// 5 /////////////////////////////////////////////////


	public class AnalyzeCommand extends Command {
		public AnalyzeCommand() {
			super("upload anomalies and analyze results");
		}

		boolean crossSection(long as,long ae,long bs, long be){
			return (ae>=bs && be>=as);
		}

		boolean isTP(long start, long end){
			for( Anomal fr : sharedState.newAnomal){
				if(crossSection(start,end,fr.start,fr.end)){
					fr.tp=true;
					return true;
				}
			}
			return false;
		}


		@Override
		public void execute() {
			dio.write("Please upload your local anomalies file.\n");
			String s;
			float TP=0, sum=0, P=0, FP=0;; //TP = true positive, P = positive, FP=false positive ,sum = the amount of anomalies
			while(!(s=dio.readText()).equals("done")){
				long startRange = Long.parseLong(s.split(",")[0]);
				long endRange = Long.parseLong(s.split(",")[1]);
				if(isTP(startRange,endRange))
					TP++;
				sum+=endRange+1-startRange;
				P++;
			}
			dio.write("Upload complete.\n");
			for(Anomal nar : sharedState.newAnomal) {
				if (!nar.tp)
					FP++;
			}
			float N=sharedState.rowfilesize - sum;
			float TPR = ((int)(1000.0*TP/P))/1000.0f;
			float FPR = ((int)(1000.0*FP/N))/1000.0f;
			dio.write("True Positive Rate: ");
			dio.write(TPR);
			dio.write("\nFalse Positive Rate: ");
			dio.write(FPR);
			dio.write("\n");
		}


	}

///////////////////////////////////////////// 6 /////////////////////////////////////////////////

	public class ExitCommand extends Command {
		public ExitCommand() {
			super("exit");
		}

		@Override
		public void execute() {
		}
	}

//////////////////////////////////////////////////////////////////////////////////////////////////

}
