package draughts.library.boardmodel;

import java.util.ArrayList;

import draughts.library.exceptions.NoPieceFoundInRequestedTileException;
import draughts.library.movemodel.Capture;
import draughts.library.movemodel.Hop;
import draughts.library.movemodel.Move;

public abstract class Queen extends Piece {

	public Queen(Tile position) {
		super(position);
	}
	
	public boolean isQueen() {
		return true;
	}
	
	public ArrayList<Move<Hop>> findAllMoves(Tile[][] board) {
		
		ArrayList<Move<Hop>> moves = new ArrayList<>();
				
		addMovesIfAny(moves, findMovesInDirection(MoveDirection.UP_LEFT, board));
		addMovesIfAny(moves, findMovesInDirection(MoveDirection.UP_RIGHT, board));
		addMovesIfAny(moves, findMovesInDirection(MoveDirection.DOWN_LEFT, board));
		addMovesIfAny(moves, findMovesInDirection(MoveDirection.DOWN_RIGHT, board));

		return moves;
	}
	
	public ArrayList<Move<Hop>> findMovesInDirection(MoveDirection moveDirection, Tile[][] board) {
		ArrayList<Move<Hop>> moves = new ArrayList<>();
		Move<Hop> move;
		int hopLength = 1;
		
		while( (move = findMove(moveDirection, hopLength, board)) != null) {
			moves.add(move);
			hopLength++;
		}
		return moves;
	}

	public Move<Hop> findMove(MoveDirection moveDirection, int hopLength, Tile[][] board) {
		if (isMovePossible(moveDirection, hopLength)) {
			Tile target = findTarget(moveDirection, board, hopLength);
			if (target.getState() == Tile.State.EMPTY) {
				return new Move<>(this, new Hop(position, target));
			} else {
				return null;
			}
		}
		else {
			return null;
		}
	}

	public boolean isMovePossible(MoveDirection moveDirection, int hopLength) {
		switch(moveDirection) {
			case UP_LEFT:
				return (position.getRow()-hopLength > 0 && position.getColumn()-hopLength > 0);
			case UP_RIGHT:
				return (position.getRow()-hopLength > 0 && position.getColumn()+hopLength < 11);
			case DOWN_LEFT:
				return (position.getRow()+hopLength < 11 && position.getColumn()-hopLength > 0);
			case DOWN_RIGHT:
				return (position.getRow()+hopLength < 11 && position.getColumn()+hopLength < 11);
			default:
				return false;
		}
	}

	public ArrayList<Capture> findAllCaptures(Tile[][] board, ArrayList<Piece> oppositePieces) {
		ArrayList<Capture> captures = new ArrayList<>();

		addCapturesIfAny(captures, findCapturesInDirection(MoveDirection.UP_LEFT, board, oppositePieces));
		addCapturesIfAny(captures, findCapturesInDirection(MoveDirection.UP_RIGHT, board, oppositePieces));
		addCapturesIfAny(captures, findCapturesInDirection(MoveDirection.DOWN_LEFT, board, oppositePieces));
		addCapturesIfAny(captures, findCapturesInDirection(MoveDirection.DOWN_RIGHT, board, oppositePieces));

		return captures;
	}
	
	public ArrayList<Capture> findCapturesInDirection
		(MoveDirection moveDirection, Tile[][] board, ArrayList<Piece> allPieces)
	{
		ArrayList<Capture> moves = new ArrayList<>();
		int hopLength = 1;
		Piece foundPawnToTake = null;
		
		while(isMovePossible(moveDirection, hopLength)) {
			Tile target = findTarget(moveDirection, board, hopLength);
			
			if(target.getState() == Tile.State.EMPTY) {
				if(foundPawnToTake != null) {
					moves.add(new Capture(position, target, foundPawnToTake));
				}
				hopLength++;
			} 
			else if(isTileOccupiedBySameColor(target)) {
				break;
			}
			else if(isTileOccupiedByOppositeColor(target)){
				if(foundPawnToTake == null) {
					try {
						foundPawnToTake = findPieceBeingTaken(target, allPieces);
					} catch(NoPieceFoundInRequestedTileException ex) {
						ex.printStackTrace();
					} finally {
						hopLength++;
					}
				}
				else break;
			}
		}
		
		return moves;
	}
	
}
