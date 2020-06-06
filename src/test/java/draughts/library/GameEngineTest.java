package draughts.library;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import draughts.library.GameEngine.GameState;
import draughts.library.boardmodel.Piece;
import draughts.library.boardmodel.Tile;
import draughts.library.exceptions.NoCorrectMovesForSelectedPieceException;
import draughts.library.exceptions.NoPieceFoundInRequestedTileException;
import draughts.library.exceptions.WrongColorFoundInRequestedTileException;
import draughts.library.exceptions.WrongMoveException;

@RunWith(MockitoJUnitRunner.class)
public class GameEngineTest {
	
	@Spy
	GameEngine testObj;
	MoveManager moveManager;
	BoardManager boardManager;
	
	@Before
	public void setUp() {
		GameEngine gameEngine = new GameEngine();
		testObj = spy(gameEngine);
		testObj.setGameState(GameState.RUNNING);
		testObj.setIsWhiteToMove(true);
		moveManager = testObj.getMoveManager();
		boardManager = testObj.getBoardManager();
	}
	
	public void makeMove(int source, int destination) {
		try {
			testObj.tileClicked(source);
			testObj.tileClicked(destination);
		} catch(Exception ex) {}	
	}
	
	public Tile getTile(int index) {
		return boardManager.findTileByIndex(index);
	}
	
	@Test
	public void startGame_test() {
		testObj.startGame();
		
		assertTrue(testObj.getIsWhiteToMove());
		assertNull(testObj.getChosenPiece());
		assertEquals(9, testObj.getMoveManager().getPossibleMoves().size());
		assertEquals(GameState.RUNNING, testObj.getGameState());
		assertEquals(50, testObj.getDrawArbiter().getDrawCounter());
		assertEquals(DrawArbiter.DrawConditions.NONE, testObj.getDrawArbiter().getDrawConditions());
	}
	
	@Test
	public void checkForPawnPromotion_noPromotion_test() throws Exception {
		boardManager.createEmptyBoard();
		boardManager.addWhitePawn(12);
		boardManager.addBlackPawn(39);
		Piece whitePiece = boardManager.findPieceByIndex(12);
		Piece blackPiece = boardManager.findPieceByIndex(39);
		
		Move<? extends Hop> whiteMove = new Move<Hop>(whitePiece, new Hop(getTile(12), getTile(7)));
		Move<? extends Hop> blackMove = new Move<Hop>(blackPiece, new Hop(getTile(39), getTile(44)));
		
		testObj.checkForPawnPromotion(whiteMove);
		assertFalse(boardManager.getWhitePieces().get(0).isQueen());
		
		testObj.checkForPawnPromotion(blackMove);
		assertFalse(boardManager.getBlackPieces().get(0).isQueen());		
	}
	
	@Test
	public void checkForPawnPromotion_promotion_test() throws Exception {
		boardManager.createEmptyBoard();
		boardManager.addWhitePawn(7);
		boardManager.addBlackPawn(44);
		Piece whitePiece = boardManager.findPieceByIndex(7);
		Piece blackPiece = boardManager.findPieceByIndex(44);

		Move<? extends Hop> whiteMove = new Move<Hop>(whitePiece, new Hop(getTile(7), getTile(1)));
		Move<? extends Hop> blackMove = new Move<Hop>(blackPiece, new Hop(getTile(44), getTile(50)));
		
		testObj.checkForPawnPromotion(whiteMove);
		assertTrue(boardManager.getWhitePieces().get(0).isQueen());
		
		testObj.checkForPawnPromotion(blackMove);
		assertTrue(boardManager.getBlackPieces().get(0).isQueen());		
	}
	
	@Test
	public void changePlayer_test() {
		boardManager.createEmptyBoard();
		boardManager.addWhitePawn(40);
		boardManager.addBlackPawn(8);
		
		testObj.changePlayer();
		
		assertEquals(0, moveManager.getHopsMadeInMove());
		assertFalse(testObj.getIsWhiteToMove());
		assertEquals(2, moveManager.getPossibleMoves().size());
	}
	
	//tests for making move all hops at once
	
	
	@Test(expected = NoPieceFoundInRequestedTileException.class)
	public void tileClicked_noPieceFound_test() throws NoPieceFoundInRequestedTileException, 
	 												   WrongColorFoundInRequestedTileException,
	 												   NoCorrectMovesForSelectedPieceException, 
	 												   WrongMoveException {
		testObj.startGame();
		testObj.tileClicked(25);
	}
	
	@Test(expected = WrongColorFoundInRequestedTileException.class)
	public void tileClicked_wrongPieceFound_test() throws NoPieceFoundInRequestedTileException, 
														  WrongColorFoundInRequestedTileException,
														  NoCorrectMovesForSelectedPieceException, 
														  WrongMoveException {
		testObj.startGame();
		testObj.tileClicked(12);
	}
	
	@Test(expected = NoCorrectMovesForSelectedPieceException.class)
	public void tileClicked_noMovesForPiece_test() throws NoPieceFoundInRequestedTileException, 
														  WrongColorFoundInRequestedTileException,
														  NoCorrectMovesForSelectedPieceException, 
														  WrongMoveException {
		testObj.startGame();
		testObj.tileClicked(43);
	}
	
	@Test
	public void tileClicked_numberOfPossibleDestinations_test() throws NoPieceFoundInRequestedTileException, 
																		WrongColorFoundInRequestedTileException, 
																		NoCorrectMovesForSelectedPieceException, 
																		WrongMoveException {
		testObj.startGame();
		testObj.tileClicked(33);

		assertEquals(33, testObj.getChosenPiece().getPosition().getIndex());
		assertEquals(2, moveManager.getPossibleHopDestinations().size());
		assertTrue(moveManager.getPossibleHopDestinations().get(0).getIndex() == 28 ||
				   moveManager.getPossibleHopDestinations().get(0).getIndex() == 29);
		assertTrue(moveManager.getPossibleHopDestinations().get(1).getIndex() == 28 ||
				   moveManager.getPossibleHopDestinations().get(1).getIndex() == 29);
	}
	
	@Test(expected = WrongMoveException.class)
	public void tileClicked_twice_wrongMoveMade_test() throws NoPieceFoundInRequestedTileException, 
														  WrongColorFoundInRequestedTileException,
														  NoCorrectMovesForSelectedPieceException, 
														  WrongMoveException {
		testObj.startGame();
		testObj.tileClicked(33);
		testObj.tileClicked(26);
	}
	
	@Test
	public void tileClicked_twice_changedMarkedPiece_forOneWithMoves_test() throws NoPieceFoundInRequestedTileException, 
																	WrongColorFoundInRequestedTileException,
																	NoCorrectMovesForSelectedPieceException, 
																	WrongMoveException {
		testObj.startGame();
		testObj.tileClicked(33);
		testObj.tileClicked(31);
		
		assertEquals(31, testObj.getChosenPiece().getPosition().getIndex());
		assertEquals(2, moveManager.getPossibleHopDestinations().size());
		assertTrue(moveManager.getPossibleHopDestinations().get(0).getIndex() == 26 ||
				   moveManager.getPossibleHopDestinations().get(0).getIndex() == 27);
		assertTrue(moveManager.getPossibleHopDestinations().get(1).getIndex() == 26 ||
				   moveManager.getPossibleHopDestinations().get(1).getIndex() == 27);
		
	}
	
	@Test(expected = NoCorrectMovesForSelectedPieceException.class)
	public void tileClicked_twice_changedMarkedPiece_forOneWithoutMoves_test() throws NoPieceFoundInRequestedTileException, 
																	WrongColorFoundInRequestedTileException,
																	NoCorrectMovesForSelectedPieceException, 
																	WrongMoveException {
		testObj.startGame();
		testObj.tileClicked(33);
		testObj.tileClicked(44);
	}
	
	@Test
	public void tileClicked_twice_properMoveMade_test() throws Exception {
		testObj.startGame();
		makeMove(33, 28);
		
		assertEquals(Tile.State.WHITE_PAWN, boardManager.findTileByIndex(28).getState());
		assertEquals(Tile.State.EMPTY, boardManager.findTileByIndex(33).getState());
		assertEquals(28, boardManager.findPieceByIndex(28).getPosition().getIndex());
	}
	
	@Test
	public void tileClicked_twice_properCaptureMade_test() {		
		boardManager.createEmptyBoard();
		boardManager.addWhitePawn(33);
		boardManager.addBlackPawn(28);
		
		moveManager.findAllCorrectMoves(boardManager, testObj.getIsWhiteToMove());
		
		makeMove(33, 22);
		
		assertEquals(Tile.State.EMPTY, boardManager.findTileByIndex(28).getState());
		assertEquals(0, boardManager.getBlackPieces().size());
	}
	
	@Test 
	public void tileClicked_consecutiveCaptures_ultimate_test() throws NoPieceFoundInRequestedTileException, 
																	WrongColorFoundInRequestedTileException,
																	NoCorrectMovesForSelectedPieceException, 
																	WrongMoveException {
		
		boardManager.createEmptyBoard();
		boardManager.addWhitePawn(48);
		boardManager.addWhitePawn(44);
		boardManager.addBlackPawn(20);
		boardManager.addBlackPawn(30);
		boardManager.addBlackPawn(40);
		boardManager.addBlackPawn(21);
		boardManager.addBlackPawn(31);
		boardManager.addBlackPawn(32);
		boardManager.addBlackPawn(33);
		boardManager.addBlackPawn(42);
		
		moveManager.findAllCorrectMoves(boardManager, testObj.getIsWhiteToMove());
		
		testObj.tileClicked(48);
		try {
			testObj.tileClicked(47);
		} catch (WrongMoveException ex) {}
		assertEquals(48, testObj.getChosenPiece().getPosition().getIndex());
		
		testObj.tileClicked(37);
		
		assertEquals(37, testObj.getChosenPiece().getPosition().getIndex());
		assertEquals(1, moveManager.getHopsMadeInMove());
		assertEquals(2, moveManager.getPossibleHopDestinations().size());
		assertTrue(testObj.getIsWhiteToMove());
		
		try {
			testObj.tileClicked(44);
		} catch(NoCorrectMovesForSelectedPieceException ex) {}
		
		assertEquals(44, testObj.getChosenPiece().getPosition().getIndex());
		
		try {
			testObj.tileClicked(28);
		} catch(WrongMoveException ex) {}
		
		assertEquals(44, testObj.getChosenPiece().getPosition().getIndex());
		
		testObj.tileClicked(37);
		
		assertEquals(37, testObj.getChosenPiece().getPosition().getIndex());
		assertEquals(2, moveManager.getPossibleHopDestinations().size());
		
		testObj.tileClicked(28);
		
		assertEquals(28, testObj.getChosenPiece().getPosition().getIndex());
		assertEquals(1, moveManager.getPossibleHopDestinations().size());
		assertEquals(2, testObj.getMoveManager().getHopsMadeInMove());
		assertTrue(testObj.getIsWhiteToMove());
		
		testObj.tileClicked(39);
		
		assertEquals(0, testObj.getChosenPiece().getPosition().getIndex());
		assertEquals(0, moveManager.getHopsMadeInMove());
		assertFalse(testObj.getIsWhiteToMove());
		assertEquals(5, boardManager.getBlackPieces().size());
		
	}
	
	@Test
	public void checkGameState_whiteWon_byCapturingAllBlackPieces() {
		boardManager.createEmptyBoard();
		boardManager.addWhitePawn(48);
		boardManager.addBlackPawn(43);
		
		moveManager.findAllCorrectMoves(boardManager, testObj.getIsWhiteToMove());
		
		makeMove(48, 39);
		
		assertEquals(0, boardManager.getBlackPieces().size());
		assertEquals(0, testObj.getMoveManager().getPossibleMoves().size());
		assertEquals(GameState.WON_BY_WHITE, testObj.getGameState());
	}
	
	@Test
	public void checkGameState_whiteWon_byBlockingAllBlackPieces() {	
		boardManager.createEmptyBoard();
		boardManager.addBlackPawn(26);
		boardManager.addWhitePawn(31);
		boardManager.addWhitePawn(41);
		
		moveManager.findAllCorrectMoves(boardManager, testObj.getIsWhiteToMove());
		
		makeMove(41, 37);
		
		assertEquals(1, boardManager.getBlackPieces().size());
		assertEquals(0, testObj.getMoveManager().getPossibleMoves().size());
		assertEquals(GameState.WON_BY_WHITE, testObj.getGameState());
		
	}
	
	@Test
	public void checkGameState_blackWon_byCapturingAllWhitePieces() {		
		boardManager.createEmptyBoard();
		boardManager.addBlackPawn(5);
		boardManager.addWhitePawn(10);
		
		testObj.setIsWhiteToMove(false);
		moveManager.findAllCorrectMoves(boardManager, testObj.getIsWhiteToMove());
		
		makeMove(5, 14);
		
		assertEquals(0, boardManager.getWhitePieces().size());
		assertEquals(0, testObj.getMoveManager().getPossibleMoves().size());
		assertEquals(GameState.WON_BY_BLACK, testObj.getGameState());
	}
	
	@Test
	public void checkGameState_blackWon_byBlockingAllWhitePieces() {		
		boardManager.createEmptyBoard();
		boardManager.addWhitePawn(25);
		boardManager.addBlackPawn(20);
		boardManager.addBlackPawn(10);
		
		testObj.setIsWhiteToMove(false);	
		moveManager.findAllCorrectMoves(boardManager, testObj.getIsWhiteToMove());
		makeMove(10, 14);
		
		assertEquals(1, boardManager.getWhitePieces().size());
		assertEquals(0, testObj.getMoveManager().getPossibleMoves().size());
		assertEquals(GameState.WON_BY_BLACK, testObj.getGameState());	
	}
	
	
	@Test
	public void checkGameState_drawn_normalConditions_test() {				
		boardManager.createEmptyBoard();
		boardManager.addWhiteQueen(34);
		boardManager.addBlackQueen(20);
		boardManager.addWhitePawn(50);
		boardManager.addBlackPawn(5);
		boardManager.setIsWhiteQueenOnBoard(true);
		boardManager.setIsBlackQueenOnBoard(true);
		
		moveManager.findAllCorrectMoves(boardManager, testObj.getIsWhiteToMove());
		testObj.getDrawArbiter().updateState(true, 2, 2);
		assertEquals(DrawArbiter.DrawConditions.NORMAL, testObj.getDrawArbiter().getDrawConditions());

		for(int i=0; i<12; i++) {
			makeMove(34, 45);
			makeMove(20, 3);
			makeMove(45, 34);
			makeMove(3, 20);
		}
		
		makeMove(34, 45);
		makeMove(20, 3);
		
		assertEquals(0, testObj.getDrawArbiter().getDrawCounter());
		assertEquals(GameEngine.GameState.DRAWN, testObj.getGameState());
	}
	
	@Test
	public void checkGameState_drawn_3vs1Conditions_test() {		
		boardManager.createEmptyBoard();
		boardManager.addWhiteQueen(16);
		boardManager.addBlackQueen(5);
		boardManager.addBlackPawn(3);
		boardManager.addBlackPawn(21);
		boardManager.setIsWhiteQueenOnBoard(true);
		boardManager.setIsBlackQueenOnBoard(true);
		
		moveManager.findAllCorrectMoves(boardManager, testObj.getIsWhiteToMove());
		testObj.getDrawArbiter().updateState(true, 1, 3);
		
		for(int i=0; i<8; i++) {
			makeMove(16, 49);
			makeMove(5, 46);
			makeMove(49, 16);
			makeMove(46, 5);
		}
		
		assertEquals(GameEngine.GameState.DRAWN, testObj.getGameState());	
	}
	
	@Test
	public void checkGameState_drawn_2v1Conditions_test() {		
		boardManager.createEmptyBoard();
		boardManager.addWhiteQueen(33);
		boardManager.addBlackQueen(5);
		boardManager.addBlackQueen(2);
		boardManager.setIsWhiteQueenOnBoard(true);
		boardManager.setIsBlackQueenOnBoard(true);
		
		moveManager.findAllCorrectMoves(boardManager, testObj.getIsWhiteToMove());
		testObj.getDrawArbiter().updateState(true, 1, 2);
		
		for(int i=0; i<2; i++) {
			makeMove(33, 50);
			makeMove(2, 35);
			makeMove(50, 33);
			makeMove(35, 2);
		}
		
		makeMove(33, 50);
		makeMove(2, 35);
		
		assertEquals(GameEngine.GameState.DRAWN, testObj.getGameState());
	}
	
}
