package org.eclipse.draw2d.test;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

import org.junit.jupiter.api.Test;

public class PointTests extends BaseTestCase {

	@SuppressWarnings("static-method")
	@Test
	public void testMin() {
		assertTrue(Point.min(new Point(1, 3), new Point(2, 6)).equals(new Point(1, 3)));
		assertTrue(Point.min(new Point(4, 8), new Point(2, 6)).equals(new Point(2, 6)));
		assertTrue(Point.min(new Point(4, 8), new Point(2, 10)).equals(new Point(2, 8)));
		assertTrue(Point.min(new Point(4, 12), new Point(6, 10)).equals(new Point(4, 10)));
	}

	@SuppressWarnings("static-method")
	@Test
	public void testMax() {
		assertTrue(Point.max(new Point(1, 3), new Point(2, 6)).equals(new Point(2, 6)));
		assertTrue(Point.max(new Point(4, 8), new Point(2, 6)).equals(new Point(4, 8)));
		assertTrue(Point.max(new Point(4, 8), new Point(2, 10)).equals(new Point(4, 10)));
		assertTrue(Point.max(new Point(4, 12), new Point(6, 10)).equals(new Point(6, 12)));
	}

	@SuppressWarnings("static-method")
	@Test
	public void testEquals() {
		assertTrue(new Point(4, 7).equals(4, 7));
		assertFalse(new Point(3, 6).equals(3, 7));
		assertFalse(new Point(3, 6).equals(4, 6));
	}

	@SuppressWarnings("static-method")
	@Test
	public void testDifference() {
		Point p1 = new Point(4, 7);
		Point p2 = new Point(2, 4);
		// check returns expected result
		assertTrue(p1.getDifference(p2).equals(2, 3));
		// check is side-effect free
		assertTrue(p1.equals(4, 7));
		assertTrue(p2.equals(2, 4));
	}

	@SuppressWarnings("static-method")
	@Test
	public void testTranslate() {
		Point p1 = new Point(3, 6);
		p1.translate(new Dimension(4711, 567));
		assertTrue(p1.equals(4714, 573));

		p1.translate(0.4, 0.7);
		assertTrue(p1.equals(4714, 573));
	}

	@SuppressWarnings("static-method")
	@Test
	public void testSetX() {
		assertTrue(new Point(4711, 678).setX(3).equals(3, 678));
	}

	@SuppressWarnings("static-method")
	@Test
	public void testSetY() {
		assertTrue(new Point(4711, 678).setY(3).equals(4711, 3));
	}

	@SuppressWarnings("static-method")
	@Test
	public void testSetLocation() {
		assertTrue(new Point().setLocation(4711, 678).equals(4711, 678));
		assertTrue(new Point().setLocation(new Point(4711, 678)).equals(4711, 678));
	}

	@SuppressWarnings("static-method")
	@Test
	public void testConstructor() {
		assertEquals(0, 0, new Point());
	}

	@SuppressWarnings("static-method")
	@Test
	public void testConstructorIntInt() {
		assertEquals(-1, 2, new Point(-1, 2));
	}

	@SuppressWarnings("static-method")
	@Test
	public void testConstructorDoubleDouble() {
		assertEquals((int) Math.PI, (int) -Math.E, new Point(Math.PI, -Math.E));
	}

	@SuppressWarnings("static-method")
	@Test
	public void testConstructorPoint() {
		Point template = new Point(-1, 2);
		assertEquals(-1, 2, new Point(template));
		assertEquals(-1, 2, template); // assert read only argument point
	}

	@SuppressWarnings("static-method")
	@Test
	public void testConstructorSwtPoint() {
		org.eclipse.swt.graphics.Point template = new org.eclipse.swt.graphics.Point(-1, 2);
		assertEquals(-1, 2, new Point(template));
		// assert read only argument point
		assertEquals(-1, template.x);
		assertEquals(2, template.y);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testConstructorDimension() {
		Dimension dimension = new Dimension(100, 200);
		assertEquals(100, 200, new Point(dimension));
		assertEquals(100, 200, dimension);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testEqualsObject() {
		Point testPoint = new Point(-1, 2);
		assertNotNull(testPoint);
		assertFalse(testPoint.equals(new Object()));
		assertTrue(testPoint.equals(testPoint));
		assertTrue(testPoint.equals(new Point(testPoint)));
		assertFalse(testPoint.equals(new Point()));
	}

	@SuppressWarnings("static-method")
	@Test
	public void testHashCodeToString() {
		assertEquals(0, new Point().hashCode());
		assertEquals((1 * 2) ^ (1 + 2), new Point(1, 2).hashCode());
		//
		assertNotNull(new Point().toString());
		assertNotNull(new Point(1, 2).toString());
	}

	@SuppressWarnings("static-method")
	@Test
	public void testGetCopy() {
		Point template = new Point(-1, 2);
		Point testPoint = template.getCopy();
		assertNotNull(testPoint);
		assertNotSame(template, testPoint);
		assertEquals(template, testPoint);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testGetSwtPoint() {
		org.eclipse.swt.graphics.Point testPoint = new Point(-1, 2).getSWTPoint();
		assertNotNull(testPoint);
		assertEquals(-1, testPoint.x);
		assertEquals(2, testPoint.y);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testSetLocationIntInt() {
		Point testPoint = new Point();
		assertSame(testPoint, testPoint.setLocation(-1, 2));
		assertEquals(-1, 2, testPoint);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testSetLocationPoint() {
		Point template = new Point(-1, 2);
		Point testPoint = new Point();
		assertSame(testPoint, testPoint.setLocation(template));
		assertEquals(template, testPoint);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testGetDifferencePoint() {
		Dimension dimension = new Point(5, -5).getDifference(new Point(4, -4));
		assertEquals(1, dimension.width);
		assertEquals(-1, dimension.height);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testGetDistanceSquaredPoint() {
		assertEquals(25, new Point(4, 7).getDistanceSquared(new Point(1, 3)));
		assertEquals(25, new Point(-1, -2).getDistanceSquared(new Point(-5, 1)));
	}

	@SuppressWarnings("static-method")
	@Test
	public void testGetDistancePoint() {
		assertEquals(5, new Point(4, 7).getDistance(new Point(1, 3)), 0);
		assertEquals(5, new Point(-1, -2).getDistance(new Point(-5, 1)), 0);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testGetDistanceOrthogonal() {
		assertEquals(53, new Point(10, 20).getDistanceOrthogonal(new Point(51, 32)));
		assertEquals(53, new Point(51, 32).getDistanceOrthogonal(new Point(10, 20)));
		//
		assertEquals(60, new Point(-10, -20).getDistanceOrthogonal(new Point(10, 20)));
		assertEquals(60, new Point(10, 20).getDistanceOrthogonal(new Point(-10, -20)));
	}

	@SuppressWarnings("static-method")
	@Test
	public void testNegate() {
		Point testPoint = new Point(1, 2);
		assertSame(testPoint, testPoint.negate());
		assertEquals(-1, -2, testPoint);
		//
		assertSame(testPoint, testPoint.negate());
		assertEquals(1, 2, testPoint);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testScale() {
		// check work scal(double)
		Point testPoint = new Point(10, 20);
		assertSame(testPoint, testPoint.scale(0.5));
		assertEquals(5, 10, testPoint);
		//
		// check work scal(double, double)
		assertSame(testPoint, testPoint.scale(20, 10));
		assertEquals(100, 100, testPoint);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testTranspose() {
		Point testPoint = new Point(3, 5);
		assertSame(testPoint, testPoint.transpose());
		assertEquals(5, 3, testPoint);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testGetNegated() {
		Point template = new Point(1, 2);
		Point testPoint = template.getNegated();
		assertNotSame(template, testPoint);
		assertEquals(-1, -2, testPoint);
		//
		template = testPoint;
		testPoint = template.getNegated();
		assertNotSame(template, testPoint);
		assertEquals(1, 2, testPoint);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testGetScaled() {
		Point template = new Point(10, 20);
		Point testPoint = template.getScaled(0.5);
		assertNotSame(template, testPoint);
		assertEquals(5, 10, testPoint);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testGetTranslated() {
		// check work getTranslated(int, int)
		Point template = new Point(3, 5);
		Point testPoint = template.getTranslated(1, -1);
		assertNotSame(template, testPoint);
		assertEquals(4, 4, testPoint);
		//
		// check work getTranslated(Dimension)
		template = testPoint;
		testPoint = template.getTranslated(new Dimension(-5, -5));
		assertNotSame(template, testPoint);
		assertEquals(-1, -1, testPoint);
		//
		// check work getTranslated(Point)
		template = testPoint;
		testPoint = template.getTranslated(new Point(1, 1));
		assertNotSame(template, testPoint);
		assertEquals(0, 0, testPoint);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testGetTransposed() {
		Point template = new Point(3, 5);
		Point testPoint = template.getTransposed();
		assertNotSame(template, testPoint);
		assertEquals(5, 3, testPoint);
	}
}
