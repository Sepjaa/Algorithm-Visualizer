package fi.sepjaa.visualizer.pathfinding;

public class ConnectedNodePair {

	private final Node n1;
	private final Node n2;

	/**
	 * @throws IllegalArgumentException Throws if nodes are not connected.
	 */
	public ConnectedNodePair(Node n1, Node n2) throws IllegalArgumentException {
		super();
		this.n1 = n1;
		this.n2 = n2;
		if (!(n1.getConnections().contains(n2.getId())) && n2.getConnections().contains(n1.getId())) {
			throw new IllegalArgumentException();
		}
	}

	public Node getN1() {
		return n1;
	}

	public Node getN2() {
		return n2;
	}

	public float getDistance() {
		return n1.distanceTo(n2);
	}

	public boolean contains(int id) {
		return n1.getId() == id || n2.getId() == id;
	}

	public boolean is(int id, int otherId) {
		return contains(id) && contains(otherId) && id != otherId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((n1 == null) ? 0 : n1.hashCode());
		result = prime * result + ((n2 == null) ? 0 : n2.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ConnectedNodePair other = (ConnectedNodePair) obj;
		if (n1 == null) {
			if (other.n1 != null) {
				return false;
			}
		} else if (!n1.equals(other.n1)) {
			return false;
		}
		if (n2 == null) {
			if (other.n2 != null) {
				return false;
			}
		} else if (!n2.equals(other.n2)) {
			return false;
		}
		return true;
	}

}
