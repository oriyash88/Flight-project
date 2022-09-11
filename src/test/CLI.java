package test;

import java.util.ArrayList;
import java.util.Scanner;

import test.Commands.Command;
import test.Commands.DefaultIO;

public class CLI {

	ArrayList<Command> commands;
	DefaultIO dio;
	Commands c;

	public CLI(DefaultIO dio) {
		this.dio=dio;
		c=new Commands(dio); 
		commands=new ArrayList<>();
		// example: commands.add(c.new ExampleCommand());
		// implement

		//commands.add(c.new UploadCommand());
        commands.add(c.new UploadCommand());
		commands.add(c.new AlgorithmSettings());
		commands.add(c.new AnomalyDetectiomCommand());
		commands.add(c.new DisplayCommand());
		commands.add(c.new AnalyzeCommand());
		commands.add(c.new ExitCommand());


	}
	
	public void start() {

		int num=-1;
		while(num!=5){
			c.PrintMenu();
			for(int i=0;i<commands.size();i++){
				dio.write((i+1)+". "+commands.get(i).description+"\n");
			}
			num = (int)dio.readVal() -1;
			dio.readText();
			if(num>=0 && num<=6)
				commands.get(num).execute();
		}
		dio.write("bye");
	}

}

