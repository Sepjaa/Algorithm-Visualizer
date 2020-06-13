package fi.sepjaa.visualizer.pathfinding;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Node {
	private static final Logger LOG = LoggerFactory.getLogger(Node.class);
	private static final Random random = new Random();
	private static final float NODE_BUFFER_ZONE_WIDTH = 0.05f;

	private final int id;
	private final float x, y;
	private final List<Integer> connections;

	public Node(int id, float x, float y) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.connections = new LinkedList<>();
	}

	public static Node randomInstance(int id) {
		return new Node(id, NODE_BUFFER_ZONE_WIDTH + random.nextFloat() * (1 - 2 * NODE_BUFFER_ZONE_WIDTH),
				NODE_BUFFER_ZONE_WIDTH + random.nextFloat() * (1 - 2 * NODE_BUFFER_ZONE_WIDTH));
	}

	void addConnection(int id) {
		connections.add(id);
	}

	public float distanceTo(Node other) {
		return (float) Math.sqrt(Math.pow(getX() - other.getX(), 2) + Math.pow(getY() - other.getY(), 2));
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public int getId() {
		return id;
	}

	public List<Integer> getConnections() {
		return connections;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((connections == null) ? 0 : connections.hashCode());
		result = prime * result + id;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
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
		Node other = (Node) obj;
		if (connections == null) {
			if (other.connections != null) {
				return false;
			}
		} else if (!connections.equals(other.connections)) {
			return false;
		}
		if (id != other.id) {
			return false;
		}
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x)) {
			return false;
		}
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Node [id=" + id + ", x=" + x + ", y=" + y + ", connections=" + connections + "]";
	}
}
