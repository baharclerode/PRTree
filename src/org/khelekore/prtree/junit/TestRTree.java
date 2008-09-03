package org.khelekore.prtree.junit;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.khelekore.prtree.MBR;
import org.khelekore.prtree.MBRConverter;
import org.khelekore.prtree.PRTree;
import static org.junit.Assert.*;

public class TestRTree {
    private PRTree<Rectangle2D> tree;

    @Before
    public void setUp() {
	tree = new PRTree<Rectangle2D> (new Rectangle2DConverter (), 10);
    }

    private class Rectangle2DConverter implements MBRConverter<Rectangle2D> {
	public double getMin (Rectangle2D t, int ordinate) {
	    if (ordinate == 0)
		return t.getMinX ();
	    return t.getMinY ();
	}
	public double getMax (Rectangle2D t, int ordinate) {
	    if (ordinate == 0)
		return t.getMaxX ();
	    return t.getMaxY ();
	}
    }

    @Test
    public void testEmpty () {
	tree.load (Collections.<Rectangle2D>emptyList ());
	for (Rectangle2D r : tree.find (0, 0, 1, 1))
	    fail ("should not get any results");
	assertNull ("mbr of empty tress should be null", tree.getMBR ());
    }

    @Test
    public void testSingle () {
	Rectangle2D rx = new Rectangle2D.Double (0, 0, 1, 1);
	tree.load (Collections.singletonList (rx));
	MBR mbr = tree.getMBR ();
	assertEquals ("odd min for mbr", 0, mbr.getMin (0), 0);
	assertEquals ("odd min for mbr", 0, mbr.getMin (1), 0);
	assertEquals ("odd max for mbr", 1, mbr.getMax (0), 0);
	assertEquals ("odd max for mbr", 1, mbr.getMax (1), 0);
	int count = 0;
	for (Rectangle2D r : tree.find (0, 0, 1, 1)) {
	    assertEquals ("odd rectangle returned", rx, r);
	    count++;
	}
	assertEquals ("odd number of rectangles returned", 1, count);

	for (Rectangle2D r : tree.find (5, 5, 6, 7))
	    fail ("should not find any rectangle");

	for (Rectangle2D r : tree.find (-5, -5, -2, -4))
	    fail ("should not find any rectangle");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadQueryRectX () {
	tree.find (0, 0, -1, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadQueryRectY () {
	tree.find (0, 0, 1, -1);
    }

    @Test
    public void testMany () {
	List<Rectangle2D> rects = new ArrayList<Rectangle2D> (1000);
	for (int i = 0; i < 1000000; i++)
	    rects.add (new Rectangle2D.Double (i, i, 10, 10));
	tree.load (rects);
	int count = 0;
	
	// dx = 10, each rect is 10 so 20 in total
	for (Rectangle2D r : tree.find (495, 495, 504.9, 504.9))
	    count++;
	assertEquals ("should find some rectangles", 20, count);

	count = 0;
	for (Rectangle2D r : tree.find (1495, 495, 1504.9, 504.9))
	    count++;
	assertEquals ("should not find rectangles", 0, count);
    }

    public static void main (String args[]) {
	JUnitCore.main (TestRTree.class.getName ());
    }
}
