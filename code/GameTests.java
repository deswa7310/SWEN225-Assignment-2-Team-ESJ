import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import org.junit.Test;

/**
 * GameTests contains a list of tests that cover the Murder Madness program.
 * 
 * @author johnh
 *
 */
public class GameTests {

	/**
	 * Tests that the right number of each Card type is initialized.
	 */
	@Test public void test_01() {
		Game game = new Game();
		game.initCards();
		
		// Check size of each Collection of Cards:
		assertEquals(4, game.getCharacters().size());
		assertEquals(5, game.getEstates().size());
		assertEquals(5, game.getWeapons().size());
	}
	
	/**
	 * Tests that every Square is initialized during Board initialization.
	 */
	@Test public void test_02() {
		Game game = new Game();
		game.initCards();
		game.initBoard();
		Board board = game.getBoard();
		
		// Iterate through every Square:
		for (int row = 0; row < Board.ROWS; row++) {
			for (int col = 0; col < Board.COLS; col++) {
				Square s = board.getSquare(row, col);
				if (s == null) fail(); // if Square not initialized, fail
			}
		}
	}
	
	/**
	 * Tests that Players are successfully initialized, configured, and set.
	 */
	@Test public void test_03() {
		Game game = new Game();
		
		// Create dummy Players for testing:
		Player[] players = createDummyPlayers(game);
		
		// Confirm configuration of Players:
		game.confirmConfigurations(players);
		
		// Check that Players have been set:
		assertEquals(4, game.getPlayers().length);
	}
	
	/**
	 * Tests that Cards are dealt to all Players successfully and evenly.
	 */
	@Test public void test_04() {
		Game game = new Game();
		
		// Create and set dummy Players:
		Player[] players = createDummyPlayers(game);
		game.confirmConfigurations(players);
		
		// Create solution and deal Cards to Players:
		game.dealCards();
		
		// Check each Player has Cards:
		for (Player p : players) {
			assertNotEquals(0, p.getHand().size());
		}
	}
	
	/**
	 * Tests that the solution contains a Card of each type (GameCharacter, Estate, and Weapon).
	 */
	@Test public void test_05() {
		Game game = new Game();
		
		// Create and set dummy Players:
		Player[] players = createDummyPlayers(game);
		game.confirmConfigurations(players);
		
		// Create solution and deal Cards to Players:
		game.dealCards();
		
		// Check the solution contains a Card of each type:
		boolean[] results = new boolean[3];
		for (Card c : game.getSolution()) {
			if (c instanceof GameCharacter) results[0] = true;
			if (c instanceof Estate) results[1] = true;
			if (c instanceof Weapon) results[2] = true;
		}
		
		for (boolean result : results) {
			assertTrue(result);
		}
	}
	
	
	/**
	 * Tests that the program successfully determines when all Players have been eliminated.
	 */
	@Test public void test_06() {
		Game game = new Game();
		
		// Create and set dummy Players and Cards:
		Player[] players = createDummyPlayers(game);
		game.confirmConfigurations(players);
		game.dealCards();
		
		// Test when none are eliminate:
		assertFalse(game.allPlayersEliminated());
		
		// Eliminate half of the Players:
		players[0].setSolveAttempted();
		players[1].setSolveAttempted();
		
		// Test when some are eliminated, but not all:
		assertFalse(game.allPlayersEliminated());
		
		// Eliminate the rest:
		players[2].setSolveAttempted();
		players[3].setSolveAttempted();
		assertTrue(game.allPlayersEliminated());
	}
	
	/**
	 * Tests that Player movement works:
	 */
	@Test public void test_07() {
		Game game = new Game();
		
		// Create and set dummy Players and Cards:
		Player[] players = createDummyPlayers(game);
		game.confirmConfigurations(players);
		
		// Check that the Player's Square is occupied:
		Square square = players[0].character.getSquare();
		assertTrue(square.isBlocked());
		
		// Move Player up:
		KeyEvent upEvent = new KeyEvent(new JFrame(), 0, 0, 0, KeyEvent.VK_UP, 'u');
		players[0].rollDice();
		players[0].move(upEvent, game, null);
		
		// Check that the Square is now clear:
		assertFalse(square.isBlocked());
	}
	
	
	/**
	 * Helper method that creates returns an array of fake Players.
	 * 
	 * @param game game being tested
	 * @return fake Players
	 */
	public Player[] createDummyPlayers(Game game) {
		
		// Set up character cards:
		game.initCards();
		game.initBoard();
		List<GameCharacter> characters = new ArrayList<>(game.getCharacters());
		
		// Creates a dummy Player for each character:
		Player[] players = new Player[4];
		for (int i = 0; i < 4; i++) {
			players[i] = new Player(i+1, "dummy", characters.get(i));
		}
		return players;
	}
}
