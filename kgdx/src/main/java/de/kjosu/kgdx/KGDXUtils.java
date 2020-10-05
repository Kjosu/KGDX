package de.kjosu.kgdx;

public class KGDXUtils {

	private KGDXUtils() {

	}

	public static boolean isNullOrEmpty(String s) {
		return s == null || s.isEmpty();
	}

	public static int uniqueIdentifier(int column, int row, int maxColumns) {
		if (maxColumns <= 0 || column >= maxColumns) {
			throw new IllegalArgumentException();
		}

		return row * maxColumns + column;
	}
}
