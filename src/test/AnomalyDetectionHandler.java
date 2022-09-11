package test;


import test.Commands.DefaultIO;
import test.Server.ClientHandler;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class AnomalyDetectionHandler implements ClientHandler{

	@Override
	public void handle(InputStream infromClient, OutputStream outToClient) {
		//in = new Scanner(infromClient);
		///out = new PrintWriter(outToClient, true);
		SocketIO sio = new SocketIO(infromClient, outToClient);
		CLI c = new CLI(sio);
		c.start();
		sio.in.close();
		sio.out.close();
	}


	public class SocketIO implements DefaultIO {
		Scanner in;
		PrintWriter out;

		public SocketIO(InputStream infromClient, OutputStream outToClient)
		{
			in = new Scanner(infromClient);
			out = new PrintWriter(outToClient, true);
		}
		@Override
		public String readText() {
			String s = null;
			s = in.nextLine();
			return s;
		}

		@Override
		public void write(String text) {
			out.print(text);
			out.flush();
		}

		@Override
		public float readVal() {
			float f = 0;
			f = (float) in.nextFloat();
			return f;
		}

		@Override
		public void write(float val) {
			out.print(val);
			out.flush();
		}
	}

}

