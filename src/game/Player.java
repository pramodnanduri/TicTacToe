package game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Player extends Thread {

	String serverAddress = "localhost";
	int port = 5999;
	Socket socket;
	BufferedReader reader;
	PrintWriter writer;

	Player() {
		try {
			socket = new Socket(serverAddress, port);
			InputStreamReader inputstream = new InputStreamReader(socket.getInputStream());
			reader = new BufferedReader(inputstream);
			writer = new PrintWriter(socket.getOutputStream());
			this.start();
			takeInput();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void takeInput() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String move = null;
			while ((move = br.readLine()) != null) {
				sendDataToServer(move);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendDataToServer(String data) {
		writer.println(data);
		writer.flush();
	}

	@Override
	public void run() {
		String stream;
		try {
			while ((stream = reader.readLine()) != null) {
				if (stream.startsWith("$")) {
					System.exit(0);
				}
				System.out.println(stream);// Server response
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Player();
	}
}
