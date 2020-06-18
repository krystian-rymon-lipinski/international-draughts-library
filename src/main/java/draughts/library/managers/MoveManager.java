package draughts.library.managers;

import java.util.ArrayList;
import java.util.Iterator;

import draughts.library.boardmodel.Piece;
import draughts.library.boardmodel.Tile;
import draughts.library.movemodel.Hop;
import draughts.library.movemodel.Move;

public class MoveManager {
	
	private ArrayList<Move<? extends Hop>> possibleMoves;
	
	//used for making move hop by hop
	private int hopsMadeInMove;
	private ArrayList<Hop> possibleHops;
	
	public MoveManager() {
		possibleMoves = new ArrayList<>();
		hopsMadeInMove = 0;
		possibleHops = new ArrayList<>();
	}
	
	public ArrayList<Move<? extends Hop>> getPossibleMoves() {
		return possibleMoves;
	}
	
	public int getHopsMadeInMove() {
		return hopsMadeInMove;
	}
	
	public ArrayList<Hop> getPossibleHops() {
		return possibleHops;
	}
	
	public void findAllCorrectMoves(BoardManager boardManager, boolean isWhiteToMove) {
		possibleMoves.addAll(boardManager.findCapturesForAllPieces(isWhiteToMove));
		if(possibleMoves.size() == 0)
			possibleMoves.addAll(boardManager.findMovesForAllPieces(isWhiteToMove));		
	}

	//methods for making move all hops at once
	
	public Move<? extends Hop> isMadeMoveCorrect(int source, int destination, ArrayList<Integer> takenPawns) {
		for(Move<? extends Hop> move : possibleMoves) {
			if(move.doesSourceMatch(source) &&
			   move.doesDestinationMatch(destination) &&
			   move.doesTakenPawnsMatch(takenPawns))
			   		return move;
		}
		return null;
	}
	
	//methods for making move hop by hop
	
	public ArrayList<Hop> findPossibleHops(Piece chosenPiece) {
		for(Move<? extends Hop> move : possibleMoves) {
			if(chosenPiece.getPosition().getIndex() == move.getHop(hopsMadeInMove).getSource().getIndex())
				possibleHops.add(move.getHop(hopsMadeInMove));
		}
		return possibleHops;
	}
	
	public Hop findHopByDestination(Tile tileDestination) {
		for(Hop hop : possibleHops) {
			if(tileDestination.getIndex() == hop.getDestination().getIndex()) return hop;
		}	
		return null;
	}
	
	public void hopFinished() {
		possibleHops.clear();
		hopsMadeInMove++;
	}
	
	public void updatePossibleMoves(Piece chosenPiece) {
		Iterator<Move<? extends Hop>> movesIterator = possibleMoves.iterator();
		
		while(movesIterator.hasNext()) {
			Move<? extends Hop> move = movesIterator.next();
			if(move.getHop(hopsMadeInMove).getSource().getIndex() != chosenPiece.getPosition().getIndex()) {
				movesIterator.remove();
				possibleMoves.remove(move);
			}
		}
	}
	
	public Move<? extends Hop> findMoveMade(Tile chosenTile) {
		for(Move<? extends Hop> move : possibleMoves) {
			if(chosenTile.getIndex() == move.getMoveDestination().getIndex()) return move;
		}
		return null;
	}
	
	public void moveDone() {
		possibleHops.clear();
		possibleMoves.clear();
		hopsMadeInMove = 0;
	}
	
	public boolean isMoveFinished() {
		return (hopsMadeInMove == possibleMoves.get(0).getNumberOfHops()) ? true : false;
	}
}