package it.uniroma2.sag.kelp.data.representation.vector.exception;

import it.uniroma2.sag.kelp.data.representation.Vector;

public class VectorOperationException extends Exception {
	private static final long serialVersionUID = -7611734518801109831L;
	private Vector second;
	private Vector first;

	public VectorOperationException(String message, Vector first, Vector second) {
		super(message);
		this.first = first;
		this.second = second;
	}

	public Vector getSecond() {
		return second;
	}

	public void setSecond(Vector second) {
		this.second = second;
	}

	public Vector getFirst() {
		return first;
	}

	public void setFirst(Vector first) {
		this.first = first;
	}
}
