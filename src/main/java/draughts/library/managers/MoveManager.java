package draughts.library.managers;

import java.util.ArrayList;
import java.util.Iterator;

import draughts.library.boardmodel.Piece;
import draughts.library.boardmodel.Tile;
import draughts.library.exceptions.WrongMoveException;
import draughts.library.movemodel.Hop;
import draughts.library.movemodel.Move;

public class MoveManager {
	
	private ArrayList<Move<? extends Hop>> possibleMoves;
	
	public MoveManager() {
		possibleMoves = new ArrayList<>();
	}
	
	public ArrayList<Move<? extends Hop>> getPossibleMoves() {
		return possibleMoves;
	}
	
	public ArrayList<Move<? extends Hop>> findAllCorrectMoves(BoardManager boardManager, boolean isWhiteToMove) {
		possibleMoves.addAll(boardManager.findCapturesForAllPieces(isWhiteToMove));
		if(possibleMoves.size() == 0)
			possibleMoves.addAll(boardManager.findMovesForAllPieces(isWhiteToMove));
		for (Move<? extends Hop> move : possibleMoves) {
			move.classify();
		}
		return possibleMoves;
	}

	public Move<? extends Hop> convertToMove(int source, int destination, ArrayList<Integer> takenPawns)
	throws WrongMoveException {
		for(Move<? extends Hop> move : possibleMoves) {
			if(move.doesSourceMatch(source) &&
			   move.doesDestinationMatch(destination) &&
			   move.doesTakenPawnsMatch(takenPawns))
			   		return move;
		}
		throw new WrongMoveException("Chosen move (" + source + " -> " + destination +
									 " " + takenPawns + " is not allowed!");
	}

}
