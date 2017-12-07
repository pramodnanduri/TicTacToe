package game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TicTacToeServer {

	private static List<PrintWriter> clientOutputStreams = new ArrayList<PrintWriter>();
	private static Game game = new Game();

	public static void main(String[] args) {
		if (args == null || args.length < 1) {
			System.out.println("Server port needs to be specified!");
			System.exit(0);
		}
		int port = Integer.parseInt(args[0]);
		new TicTacToeServer().startServer(port);
	}

	private void startServer(int port) {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Http Server started on port " + port);
			while (true) {
				Socket clientSocket = serverSocket.accept();
				new RequestHandler(clientSocket).start();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			closeResource(serverSocket);
		}
	}

	private void closeResource(ServerSocket socket) {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	static class RequestHandler extends Thread {

		Socket socket;
		BufferedReader reader;
		PrintWriter response;
		private char id;

		public RequestHandler(Socket clientSocket) {
			// client = user;
			try {
				socket = clientSocket;
				InputStreamReader isReader = new InputStreamReader(socket.getInputStream());
				reader = new BufferedReader(isReader);
				response = new PrintWriter(socket.getOutputStream());
				clientOutputStreams.add(response);
				setClientId();
				sendWelcomeScreen();
			} catch (Exception ex) {
			}
		}

		private void setClientId() {
			if (clientOutputStreams.size() == 1) {
				this.id = Game.CIRCLE;
			} else {
				this.id = Game.CROSS;
			}
		}

		private void sendWelcomeScreen() {
			response.println("**** Welcome to TicTacToe! **** ");
			response.println(game.getBoard());
			response.println("Your symbol is: " + id);
			response.println("Move needs to be made by " + Game.CIRCLE);
			response.flush();
		}

		private void writeToClient(PrintWriter response, String data) {
			response.println(data);
			response.flush();
		}

		private void writeToClient(String data) {
			response.println(data);
			response.flush();
		}

		@Override
		public void run() {
			String message = null;
			try {
				while ((message = reader.readLine()) != null) {
					int markedPosition = -1;
					try {
						markedPosition = Integer.parseInt(message);
						String result = game.makeMove(id, markedPosition);
						if (result.startsWith("~~")) {
							writeToClient(result);
						} else {
							broadCastGame(result);
						}
					} catch (Exception ex) {
						writeToClient("Invalid Input! ");
					}
				}
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}

		private void broadCastGame(String data) {
			for (PrintWriter client : clientOutputStreams) {
				writeToClient(client, data);
				writeToClient(client, "Your symbol is " + id);
				writeToClient(client, "Next move to be made by " + getNextMove());
			}
		}

		private char getNextMove() {
			if (id == Game.CIRCLE) {
				return Game.CROSS;
			}
			return Game.CIRCLE;
		}

	}

}
