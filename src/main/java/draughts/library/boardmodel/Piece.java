package draughts.library.boardmodel;

import java.util.ArrayList;
import java.util.Objects;

import draughts.library.exceptions.NoPieceFoundInRequestedTileException;
import draughts.library.movemodel.Capture;
import draughts.library.movemodel.Hop;
import draughts.library.movemodel.Move;

public abstract class Piece {
	
	protected Tile position;
	
	public Piece(Tile position) {
		this.position = position;
	}

	public abstract boolean isQueen();
	
	public abstract boolean isWhite();
	
	public abstract boolean isTileOccupiedBySameColor(Tile tile);
	
	public abstract boolean isTileOccupiedByOppositeColor(Tile tile);
	
	public abstract ArrayList<Move<Hop>> findAllMoves(Tile[][] board);

	public abstract ArrayList<Capture> findAllCaptures(Tile[][] board, ArrayList<Piece> oppositePieces);


	public Tile getPosition() {
		return position;
	}

	public void setPosition(Tile position) {
		this.position = position;
	}
	
	public void hop(Tile dst) {
		this.position = dst;
	}
	
	public void addMovesIfAny(ArrayList<Move<Hop>> mainList, ArrayList<Move<Hop>> candidateList) {
		if(candidateList != null && candidateList.size() > 0)
			mainList.addAll(candidateList);
	}
	
	public void addMoveIfNotNull(ArrayList<Move<Hop>> mainList, Move<Hop> candidateMove) {
		if(candidateMove != null)
			mainList.add(candidateMove);
	}
	
	public void addCapturesIfAny(ArrayList<Capture> mainList, ArrayList<Capture> candidateList) {
		if(candidateList != null && candidateList.size() > 0)
			mainList.addAll(candidateList);
	}

	public void addCaptureIfNotNull(ArrayList<Capture> mainList, Capture candidate) {
		if (candidate != null) mainList.add(candidate);
	}

	public boolean isCapturePossible(MoveDirection direction) {
		switch (direction) {
			case UP_LEFT:
				return position.getColumn() > 2 && position.getRow() > 2;
			case UP_RIGHT:
				return position.getColumn() < 9 && position.getRow() > 2;
			case DOWN_LEFT:
				return position.getColumn() > 2 && position.getRow() < 9;
			case DOWN_RIGHT:
				return position.getColumn() < 9 && position.getRow() < 9;
			default:
				return false;
		}
	}
	
	public Tile findTarget(MoveDirection moveDirection, Tile[][] board, int hopLength) {
		
		switch(moveDirection) {
			case UP_LEFT:
				return board[position.getRow()-1-hopLength][position.getColumn()-1-hopLength];
			case UP_RIGHT:
				return board[position.getRow()-1-hopLength][position.getColumn()-1+hopLength];
			case DOWN_LEFT:
				return board[position.getRow()-1+hopLength][position.getColumn()-1-hopLength];
			case DOWN_RIGHT:
				return board[position.getRow()-1+hopLength][position.getColumn()-1+hopLength];
			default:
				break;
		}
		return null;
	}
	
	public Piece findPieceBeingTaken(Tile position, ArrayList<Piece> allPieces) throws NoPieceFoundInRequestedTileException {
		for(Piece piece : allPieces) {
			if (piece.getPosition().equals(position)) return piece;
		}
		
		throw new NoPieceFoundInRequestedTileException("No piece on seemingly taken tile: " + position.getIndex());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Piece piece = (Piece) o;
		return position.equals(piece.position);
	}

	@Override
	public int hashCode() {
		return Objects.hash(position);
	}

	@Override
	public String toString() {
		return "Piece{" +
				"position=" + position +
				'}';
	}

	public enum MoveDirection {
		UP_LEFT,
		UP_RIGHT,
		DOWN_LEFT,
		DOWN_RIGHT;
	}	

}
