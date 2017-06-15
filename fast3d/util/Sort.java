package fast3d.util;

import fast3d.Renderable;
import fast3d.math.Vector3d;

/**
 * in a 3d-environment some objects may cover others, so it is important to
 * detect the order (far to near) in which to render objects<br>
 * the nested classes offer static methods to sort like described by the
 * algorithms they are named like<br>
 * users should normally use Sort._Algorithm_.Renderables.sort(Vector3d
 * cameraPosition, Renderable... array); or
 * Sort._Algorithm_.Vectors.sort(Vector3d cameraPosition, Vector3d... array);
 * 
 * @author Tim Trense
 */
public abstract class Sort {

	/**
	 * Quicksort is generally the best, because fastest, way of sorting an
	 * array, but is very slow if the array is pre-sorted
	 * 
	 * @author Tim Trense
	 */
	public static class Quicksort {

		/**
		 * a static class giving methods to sort vectors with quicksort
		 * 
		 * @author Tim Trense
		 */
		public static class Vectors {
			/**
			 * sorts all the vectors in relation to their distance to the campos
			 * 
			 * @param campos
			 *            the distance-relation-vector
			 * @param vecs
			 *            the array to sort
			 */
			public static void sort(final Vector3d campos,
					final Vector3d... vecs) {
				sort(campos, 0, vecs.length - 1, vecs);
			}

			/**
			 * sorts the vectors in the specified range of the given array in
			 * relation to their distance to the campos
			 * 
			 * @param campos
			 *            the distance-relation-vector
			 * @param left
			 *            the lower border (inclusive)
			 * @param right
			 *            the upper border (inclusive)
			 * @param vecs
			 *            the array to sort
			 */
			public static void sort(Vector3d campos, int left,
					int right, Vector3d... vecs) {
				if (left < right) {
					int split = sortPartition(campos, vecs, left,
							right);
					sort(campos, left, split - 1, vecs);
					sort(campos, split + 1, right, vecs);
				}
			}

			private static int sortPartition(Vector3d campos,
					Vector3d[] vecs, int left, int right) {
				int lower, upper;
				lower = left;
				upper = right - 1;
				Vector3d pivot, help;
				pivot = vecs[right];
				double pivotdistance = campos.distanceTo(pivot);
				while (lower <= upper) {
					if (campos.distanceTo(
							vecs[lower]) < pivotdistance) {
						help = vecs[upper];
						vecs[upper] = vecs[lower];
						vecs[lower] = help;
						upper--;
					} else
						lower++;
				}
				help = vecs[right];
				vecs[right] = vecs[lower];
				vecs[lower] = help;
				return lower;
			}

		}

		/**
		 * a static class giving methods to sort renderables using quicksort
		 * 
		 * @author Tim Trense
		 */
		public static class Renderables {
			/**
			 * sorts all the renderables in relation to their distance to the
			 * campos
			 * 
			 * @param campos
			 *            the distance-relation-vector
			 * @param r
			 *            the array to sort
			 */
			public static void sort(final Vector3d campos,
					final Renderable... r) {
				sort(campos, 0, r.length - 1, r);
			}

			/**
			 * sorts the renderables in the specified range of the given array
			 * in relation to their distance to the campos
			 * 
			 * @param campos
			 *            the distance-relation-vector
			 * @param left
			 *            the lower border (inclusive)
			 * @param right
			 *            the upper border (inclusive)
			 * @param r
			 *            the array to sort
			 */
			public static void sort(Vector3d campos, int left,
					int right, Renderable... r) {
				if (left < right) {
					int split = sortPartition(campos, r, left, right);
					sort(campos, left, split - 1, r);
					sort(campos, split + 1, right, r);
				}
			}

			private static int sortPartition(Vector3d campos,
					Renderable[] r, int left, int right) {
				int lower, upper;
				lower = left;
				upper = right - 1;
				Renderable pivot, help;
				pivot = r[right];
				double pivotdistance = campos
						.distanceTo(pivot.getPos());
				while (lower <= upper) {
					if (campos.distanceTo(
							r[lower].getPos()) < pivotdistance) {
						help = r[upper];
						r[upper] = r[lower];
						r[lower] = help;
						upper--;
					} else
						lower++;
				}
				help = r[right];
				r[right] = r[lower];
				r[lower] = help;
				return lower;
			}
		}

	}

	/**
	 * Bubblesort is usually quite slow, but very fast if the array is
	 * pre-sorted
	 * 
	 * @author Tim Trense
	 */
	public static class Bubblesort {

		/**
		 * a static class giving methods to sort renderables using bubblesort
		 * 
		 * @author Tim Trense
		 */
		public static class Renderables {

			/**
			 * sorts the given renderable-array in relation to the renderables
			 * distance to the camera considering the painters algorithm
			 * 
			 * @param campos the position of the camera
			 * @param r the array to sort
			 */
			public static void sort(final Vector3d campos,
					final Renderable... r) {
				Renderable tempR;
				double tempD;
				boolean swapped = false;
				final double[] distances = new double[r.length];
				do {
					swapped = false;
					for (int i = 0; i < distances.length - 1; i++) {
						if (Double.isNaN(distances[i]))
							distances[i] = r[i].getPos()
									.distanceTo(campos);
						if (distances[i] > distances[i + 1]) {
							tempD = distances[i];
							distances[i] = distances[i + 1];
							distances[i + 1] = tempD;

							tempR = r[i];
							r[i] = r[i + 1];
							r[i + 1] = tempR;

							swapped = true;
						}
					}
				} while (swapped);
			}
		}
	}
}