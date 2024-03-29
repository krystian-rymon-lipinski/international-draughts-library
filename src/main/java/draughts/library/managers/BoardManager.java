package draughts.library.managers;

import java.util.ArrayList;
import draughts.library.boardmodel.BlackPawn;
import draughts.library.boardmodel.BlackQueen;
import draughts.library.boardmodel.Board;
import draughts.library.boardmodel.Piece;
import draughts.library.boardmodel.Tile;
import draughts.library.boardmodel.WhitePawn;
import draughts.library.boardmodel.WhiteQueen;
import draughts.library.exceptions.NoPieceFoundInRequestedTileException;
import draughts.library.movemodel.Capture;
import draughts.library.movemodel.Hop;
import draughts.library.movemodel.Move;

public class BoardManager {
	
	private Tile[][] board;
	private ArrayList<Piece> whitePieces;
	private ArrayList<Piece> blackPieces;
	private boolean isWhiteQueenOnBoard;
	private boolean isBlackQueenOnBoard;
	
	public BoardManager() {
		board = new Tile[Board.NUMBER_OF_ROWS][Board.TILES_IN_ROW];
		whitePieces = new ArrayList<>();
		blackPieces = new ArrayList<>();
		this.isWhiteQueenOnBoard = false;
		this.isBlackQueenOnBoard = false;
	}
	
	public BoardManager(BoardManager boardManager) {
		this.board = boardManager.board;
		this.whitePieces = boardManager.whitePieces;
		this.blackPieces = boardManager.blackPieces;
		this.isWhiteQueenOnBoard = boardManager.isWhiteQueenOnBoard;
		this.isBlackQueenOnBoard = boardManager.isBlackQueenOnBoard;
	}
	
	
	public Tile[][] getBoard() {
		return board;
	}
	
	public ArrayList<Piece> getWhitePieces() {
		return whitePieces;
	}
	
	public ArrayList<Piece> getBlackPieces() {
		return blackPieces;
	}
	
	public boolean getIsWhiteQueenOnBoard() {
		return isWhiteQueenOnBoard;
	}
	
	public void setIsWhiteQueenOnBoard(boolean isWhiteQueenOnBoard) {
		this.isWhiteQueenOnBoard = isWhiteQueenOnBoard;
	}
	
	public boolean getIsBlackQueenOnBoard() {
		return isBlackQueenOnBoard;
	}
	
	public void setIsBlackQueenOnBoard(boolean isBlackQueenOnBoard) {
		this.isBlackQueenOnBoard = isBlackQueenOnBoard;
	}
	
	public void createPiecesForStartingPosition() {
		
		for(int i=0; i<20; i++) {
			addWhitePawn(30+i+1); //start positions for white pieces are 31 through 50
			addBlackPawn(i+1); //start positions for black pieces are 1 through 20
		}
	}
	
	public void createEmptyBoard() {
		
		for(int i=0; i<board.length; i++) {
			for(int j=0; j<board[0].length; j++) {
				board[i][j] = new Tile(i+1, j+1);
				if(board[i][j].getIndex() > 0) board[i][j].setState(Tile.State.EMPTY);
				else board[i][j].setState(Tile.State.WHITE_TILE);
			}
		}
	}
	
	public void createStartingPosition() {
		createEmptyBoard();
		createPiecesForStartingPosition();
	}
	
	public WhitePawn addWhitePawn(int index) {
		Tile position = findTileByIndex(index);
		WhitePawn whitePawn = new WhitePawn(position);
		placePieceOnBoard(whitePawn);
		return whitePawn;
	}
	
	public BlackPawn addBlackPawn(int index) {
		Tile position = findTileByIndex(index);
		BlackPawn blackPawn = new BlackPawn(position);
		placePieceOnBoard(blackPawn);
		return blackPawn;
	}
	
	public WhiteQueen addWhiteQueen(int index) {
		Tile position = findTileByIndex(index);
		WhiteQueen whiteQueen = new WhiteQueen(position);
		isWhiteQueenOnBoard = true;
		placePieceOnBoard(whiteQueen);
		return whiteQueen;
	}
	
	public BlackQueen addBlackQueen(int index) {
		Tile position = findTileByIndex(index);
		BlackQueen blackQueen = new BlackQueen(position);
		isBlackQueenOnBoard = true;
		placePieceOnBoard(blackQueen);
		return blackQueen;
	}
	
	public void placePieceOnBoard(Piece piece) {
		Tile position = findTileByIndex(piece.getPosition().getIndex());
		if (piece.isWhite()) {
			whitePieces.add(piece);
			if (piece.isQueen()) {
				position.setState(Tile.State.WHITE_QUEEN);
				isWhiteQueenOnBoard = true;
			}
			else {
				position.setState(Tile.State.WHITE_PAWN);
			}
		}
		else {
			blackPieces.add(piece);
			if (piece.isQueen()) {
				position.setState(Tile.State.BLACK_QUEEN);
				isBlackQueenOnBoard = true;
			}
			else {
				position.setState(Tile.State.BLACK_PAWN);
			}
		}
	}


	public void removePieceFromBoard(Piece piece) {
		boolean wasQueen = piece.isQueen();
		ArrayList<Piece> pieces = piece.isWhite() ? whitePieces : blackPieces;

		piece.getPosition().setState(Tile.State.EMPTY);
		pieces.remove(piece);

		if(wasQueen) {
			for(Piece somePiece : pieces) {
				if (somePiece.isQueen()) return;
			}
			if (piece.isWhite()) isWhiteQueenOnBoard = false;
			else 				 isBlackQueenOnBoard = false;
		}
	}
	
	public void makeHop(Piece movedPiece, Tile destination) {
		movedPiece.getPosition().setState(Tile.State.EMPTY);	
		movedPiece.hop(destination);
		
	}
	
	public void makeCapture(Piece movedPiece, Tile destination, Piece takenPiece) {
		makeHop(movedPiece, destination);
		if(takenPiece.isWhite()) removePieceFromBoard(takenPiece);
		else                     removePieceFromBoard(takenPiece);
	}

	public void restoreCapturedPiece(Piece movingPiece, Capture capture) {
		placePieceOnBoard(capture.getTakenPiece());
	}
	
	public void makeWholeMove(Move<? extends Hop> move) {
		if (move.isPromotion()) {
			Piece queen;
			if (move.getOldMovingPiece() == null) { //making move for the first time
				queen = promotePawn(move.getMovingPiece());
			}
			else { //making move that has already been discovered and made (and reversed)
				queen = move.getOldMovingPiece();
				removePieceFromBoard(move.getMovingPiece());
				placePieceOnBoard(queen);
			}

			move.setOldMovingPiece(move.getMovingPiece());
			move.setMovingPiece(queen);
		}

		if(move.isCapture()) {
			for (Hop hop : move.getHops()) {
				Capture capture = (Capture) hop;
				makeCapture(move.getMovingPiece(), capture.getDestination(), capture.getTakenPiece());
			}
		}
		else {
			makeHop(move.getMovingPiece(), move.getHop(0).getDestination());
		}

	}
	
	public void reverseWholeMove(Move<? extends Hop> move) {
		if (move.isPromotion()) {
			Piece queen = move.getMovingPiece();
			move.setMovingPiece(move.getOldMovingPiece());
			move.setOldMovingPiece(queen);
			demoteQueen(queen, move.getMovingPiece());
		}

		for (int i=move.getNumberOfHops()-1; i>=0; i--) {
			makeHop(move.getMovingPiece(), move.getHop(i).getSource());
			if (move.isCapture()) {
				Capture capture = (Capture) move.getHop(i);
				restoreCapturedPiece(move.getMovingPiece(), capture);
			}
		}
	}
	
	public Piece promotePawn(Piece pawnToPromote) {
		Piece newQueen;
		removePieceFromBoard(pawnToPromote);
		if(pawnToPromote.isWhite()) {
			newQueen = addWhiteQueen(pawnToPromote.getPosition().getIndex());
		} else {
			newQueen = addBlackQueen(pawnToPromote.getPosition().getIndex());
		}
		return newQueen;
	}

	public void demoteQueen(Piece queenToDemote, Piece oldPawnToRestore) {
		removePieceFromBoard(queenToDemote);
		placePieceOnBoard(oldPawnToRestore);
	}
	
	public Tile findTileByIndex(int tileIndex) {
		for (int i=0; i<board.length; i++) {
			for(int j=0; j<board[0].length; j++) {
				if(board[i][j].getIndex() == tileIndex)
					return board[i][j];
			}
		}
		return null;
	}
	
	public Piece findPieceByIndex(int tileIndex) throws NoPieceFoundInRequestedTileException {
		ArrayList<Piece> pieces = new ArrayList<>();
		
		pieces.addAll(whitePieces);
		pieces.addAll(blackPieces);
		
		for(Piece piece : pieces) {
			if(piece.getPosition().getIndex() == tileIndex)
				return piece;
		}
		
		throw new NoPieceFoundInRequestedTileException("No piece found in tile: " + tileIndex);		
	}
	
	public ArrayList<Move<Hop>> findMovesForAllPieces(boolean isWhiteToMove) {
		ArrayList<Piece> pieces = isWhiteToMove ? whitePieces : blackPieces;
		
		ArrayList<Move<Hop>> allMoves = new ArrayList<>();
		ArrayList<Move<Hop>> pieceMoves;
		
		for(Piece piece : pieces) {
			pieceMoves = piece.findMoves(board);
			if(pieceMoves.size() > 0) allMoves.addAll(pieceMoves);
		}
		return allMoves;
	}

	public boolean isAnyMovePossible(boolean isWhiteToMove) {
		ArrayList<Piece> pieces;
		ArrayList<Piece> oppositePieces;
		if (isWhiteToMove) {
			pieces = whitePieces;
			oppositePieces = blackPieces;
		}
		else 			   {
			pieces = blackPieces;
			oppositePieces = whitePieces;
		}

		ArrayList<Move<Hop>> pieceMoves;
		ArrayList<Capture> pieceCaptures;
		for(Piece piece : pieces) {
			pieceCaptures = piece.findCaptures(board, oppositePieces);
			if (pieceCaptures.size() > 0) return true;
			else {
				pieceMoves = piece.findMoves(board);
				if (pieceMoves.size() > 0) return true;
			}
		}

		return false;
	}
	
	public ArrayList<Move<Capture>> findCapturesForAllPieces(boolean isWhiteToMove) {
		ArrayList<Piece> pieces = isWhiteToMove ? whitePieces : blackPieces;

		ArrayList<Move<Capture>> allMoves = new ArrayList<>();
		ArrayList<Move<Capture>> pieceMoves;
		int longestConsecutiveCapture = 1;
		
		for(Piece piece : pieces) {
			pieceMoves = findLongestConsecutiveCaptures(piece);
			if(pieceMoves.size() > 0)
				if(pieceMoves.get(0).getNumberOfHops() > longestConsecutiveCapture) {
					allMoves.clear();
					allMoves.addAll(pieceMoves);
					longestConsecutiveCapture = pieceMoves.get(0).getNumberOfHops();
				}
				else if(pieceMoves.get(0).getNumberOfHops() == longestConsecutiveCapture)
					allMoves.addAll(pieceMoves);
		}
			
		return allMoves;
	}
	
	public ArrayList<Move<Capture>> findLongestConsecutiveCaptures(Piece piece) {		
		ArrayList<Capture> captures;
		ArrayList<Move<Capture>> moves = new ArrayList<>();
		ArrayList<Move<Capture>> newMoves = new ArrayList<>();
		
		ArrayList<Piece> oppositePieces;
		if (piece.isWhite()) oppositePieces = blackPieces;
		else 				 oppositePieces = whitePieces;
		
		do  {
						
			if(moves.size() == 0) { //first capture 
				captures = piece.findCaptures(board, oppositePieces);
				if(captures.size() == 0) break; //no captures available for piece
				else {
					for(Capture capture : captures) {
						newMoves.add(new Move<>(piece, capture));
					}
				}
			}
			else { //consecutive captures
				newMoves.clear();

				for(int i=0; i<moves.size(); i++) {
					for(int j=0; j<moves.get(i).getNumberOfHops(); j++) {
						makeHop(piece, moves.get(i).getHop(j).getDestination());
					}
					captures = piece.findCaptures(board, oppositePieces);
					for(Capture capture: captures) {
						if(!isPawnAlreadyTaken(moves.get(i), capture)) { //cannot take the same pawn twice
							newMoves.add(new Move<>(moves.get(i)));
							newMoves.get(newMoves.size()-1).addHop(capture);
						}			
					}
					for(int j=moves.get(i).getNumberOfHops()-1; j>=0; j--) {
						makeHop(piece, moves.get(i).getHop(j).getSource());
					}
				}
			}		
			
			if(newMoves.size() > 0) {
				moves.clear();
				moves.addAll(newMoves);
			}
			
		} while (newMoves.size() != 0 );
	
	return moves;
	}
	
	
	private boolean isPawnAlreadyTaken(Move<Capture> move, Capture capture) {
		for(int i=0; i<move.getNumberOfHops(); i++) {
			if(move.getHop(i).getTakenPiece() == capture.getTakenPiece())
				return true;
		}
		return false;
	}

	public void printBoard() {
		for (int i=0; i<board.length; i++) {
			for (int j=0; j<board[i].length; j++) {
				System.out.print("|");
				switch (board[i][j].getState()) {
					case WHITE_PAWN: System.out.print("o");
					break;
					case BLACK_PAWN: System.out.print("x");
					break;
					case WHITE_QUEEN: System.out.print("O");
					break;
					case BLACK_QUEEN: System.out.println("X");
					break;
					default: System.out.print(" ");
				}
			}
			System.out.print("|");
			System.out.print("\n");
		}

		System.out.println("White pieces: " + whitePieces);
		System.out.println("Black pieces: " + blackPieces);
	}

}
