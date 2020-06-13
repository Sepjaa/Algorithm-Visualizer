package fi.sepjaa.visualizer.sorting;

import fi.sepjaa.visualizer.common.CommonConstants;

/**
 * Helper class for saving operation indexes. Indexes 0-1 map to main array and
 * 2-3 to optional memory buffer.
 *
 * @author Jaakko
 *
 */
public class SortingOperationIndexes {
	private final int i0;
	private final int i1;
	private final int i2;
	private final int i3;

	private SortingOperationIndexes(int i0, int i1, int i2, int i3) {
		super();
		this.i0 = i0;
		this.i1 = i1;
		this.i2 = i2;
		this.i3 = i3;
	}

	public static SortingOperationIndexes noStatement() {
		return new SortingOperationIndexes(CommonConstants.NO_STATEMENT, CommonConstants.NO_STATEMENT,
				CommonConstants.NO_STATEMENT, CommonConstants.NO_STATEMENT);
	}

	public static SortingOperationIndexes instance(int i0, int i1, int i2, int i3) {
		return new SortingOperationIndexes(i0, i1, i2, i3);
	}

	public int getI0() {
		return i0;
	}

	public int getI1() {
		return i1;
	}

	public int getI2() {
		return i2;
	}

	public int getI3() {
		return i3;
	}

	public boolean inArray(int i) {
		return i0 == i || i1 == i;
	}

	public boolean inMemory(int i) {
		return i0 == i || i1 == i;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + i0;
		result = prime * result + i1;
		result = prime * result + i2;
		result = prime * result + i3;
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
		SortingOperationIndexes other = (SortingOperationIndexes) obj;
		if (i0 != other.i0) {
			return false;
		}
		if (i1 != other.i1) {
			return false;
		}
		if (i2 != other.i2) {
			return false;
		}
		if (i3 != other.i3) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "SortingOperationIndexes [i0=" + i0 + ", i1=" + i1 + ", i2=" + i2 + ", i3=" + i3 + "]";
	}

}
