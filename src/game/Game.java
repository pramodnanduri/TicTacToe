package game;

import java.util.HashMap;
import java.util.Map;

public class Game {

	private static char[] BOARD = new char[9];
	private static int[][] WIN_CHANCES = { { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 }, { 0, 3, 6 }, { 1, 4, 7 }, { 2, 5, 8 },
			{ 0, 4, 8 }, { 2, 4, 6 } };
	public static final char CROSS = 'X';
	public static final char CIRCLE = 'O';
	private static char lastPlayed = 'X';
	private static Map<Integer, Integer> map = new HashMap<Integer, Integer>();

	Game() {
		for (int i = 0; i < BOARD.length; i++) {
			BOARD[i] = ' ';
		}

		map.put(11, 0);
		map.put(12, 1);
		map.put(13, 2);
		map.put(21, 3);
		map.put(22, 4);
		map.put(23, 5);
		map.put(31, 6);
		map.put(32, 7);
		map.put(33, 8);
	}

	public String getBoard() {
		int count = 0;
		String display = new String("\n ");
		for (int i = 0; i < BOARD.length; i++) {
			if (count == 3) {
				display += "\n--- --- --- \n ";
				count = 0;
			}
			display += BOARD[i] + " | ";
			count++;
		}
		return display;
	}

	private boolean validateMove(char move, int position) {
		if (move == CROSS || move == CIRCLE) {
			if (BOARD[position] == CIRCLE || BOARD[position] == CROSS) {
				return false;
			}
			return true;
		}
		return false;
	}

	private boolean isCurrentPlayerTurn(char move) {
		if (move == lastPlayed) {
			return false;
		}
		return true;
	}

	synchronized String  makeMove(char move, int markedPosition) {
		if (!isCurrentPlayerTurn(move)) {
			return "~~ Its not your turn now! ~~";
		}
		Integer position = map.get(markedPosition);
		String data = "~~ Invalid Move! ~~";
		if (position != null) {
			if (validateMove(move, position)) {
				BOARD[position] = move;
				lastPlayed = move;
				if (checkWinner(move)) {
					data = "$$$ Game Over! " + move + " is the winner... $$$";
				} else if (checkDraw(move)) {
					data = "~~ Game Draw!... ~~";
				} else {
					data = getBoard() + "\nMove Updated!";
				}
			}
		}
		return data;
	}

	private boolean checkWinner(char move) {
		for (int i = 0; i < WIN_CHANCES.length; i++) {
			int count = 0;
			for (int j = 0; j < WIN_CHANCES[i].length; j++) {
				if (BOARD[WIN_CHANCES[i][j]] == move) {
					count++;
				}
				if (count == 3) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean checkDraw(char move) {
		int count = 0;
		for (int i = 0; i < BOARD.length; i++) {
			if (BOARD[i] != ' ') {
				count++;
			}
		}
		if (count == 9) {
			return true;
		}
		return false;
	}

}
