/*******************************************************************************
 * TODO: explanation what the class does
 *
 *  @author Kevin Feichtinger
 *
 * Copyright 2023 Johannes Kepler University Linz
 * LIT Cyber-Physical Systems Lab
 * All rights reserved
 *******************************************************************************/
package at.jku.cps.travart.dopler.decision.model;

import at.jku.cps.travart.dopler.decision.model.impl.Cardinality;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CardinalityTest {

    @Test
    public void invalidParametersTest() {
        List<Executable> list = new LinkedList<>();
        list.add(new TestThrowRunnable(-1, 0));
        list.add(new TestThrowRunnable(0, -1));
        list.add(new TestThrowRunnable(-1, -1));
        list.add(new TestThrowRunnable(5, 3));

        for (Executable t : list) {
            assertThrows(IllegalArgumentException.class, t);
        }
    }

    @Test
    public void validInitializationTest() {
        Cardinality c1 = new Cardinality(0, 0);
        Cardinality c2 = new Cardinality(0, 1);
        Cardinality c3 = new Cardinality(0, 100);
        Cardinality c4 = new Cardinality(40, 60);
        assertEquals(0, c1.getMin());
        assertEquals(0, c2.getMin());
        assertEquals(0, c3.getMin());
        assertEquals(40, c4.getMin());
        assertEquals(0, c1.getMax());
        assertEquals(1, c2.getMax());
        assertEquals(100, c3.getMax());
        assertEquals(60, c4.getMax());
    }

    @Test
    public void isAlternativeTest() {
        Cardinality c1 = new Cardinality(1, 1);
        assertTrue(c1.isAlternative());
        c1 = new Cardinality(0, 1);
        assertFalse(c1.isAlternative());
        c1 = new Cardinality(1, 3);
        assertFalse(c1.isAlternative());
        c1 = new Cardinality(3, 3);
        assertFalse(c1.isAlternative());
    }

    @Test
    public void isOrTest() {
        Cardinality c1 = new Cardinality(1, 2);
        assertTrue(c1.isOr());
        c1 = new Cardinality(0, 1);
        assertFalse(c1.isOr());
        c1 = new Cardinality(3, 3);
        assertFalse(c1.isOr());
    }

    @Test
    public void isMutex() {
        Cardinality c1 = new Cardinality(2, 2);
        assertTrue(c1.isMutex());
        c1 = new Cardinality(0, 1);
        assertFalse(c1.isMutex());
        c1 = new Cardinality(0, 2);
        assertFalse(c1.isMutex());
        c1 = new Cardinality(1, 1);
        assertFalse(c1.isMutex());
        c1 = new Cardinality(1, 2);
        assertFalse(c1.isMutex());
    }

    @Test
    public void isWithinCardinalityTest() {
        Cardinality c1 = new Cardinality(1, 1);
        assertTrue(c1.isWithinCardinality(1));
        c1 = new Cardinality(0, 1);
        assertTrue(c1.isWithinCardinality(0));
        assertTrue(c1.isWithinCardinality(1));
        c1 = new Cardinality(3, 10);
        assertTrue(c1.isWithinCardinality(3));
        assertTrue(c1.isWithinCardinality(6));
        assertTrue(c1.isWithinCardinality(10));
        assertFalse(c1.isWithinCardinality(2));
        assertFalse(c1.isWithinCardinality(11));
        assertFalse(c1.isWithinCardinality(Integer.MAX_VALUE));
        assertFalse(c1.isWithinCardinality(Integer.MIN_VALUE));
    }

    @Test
    public void testIsNoCardinality() {
        Cardinality c1 = new Cardinality(0, 0);
        assertTrue(c1.isNoCardinality());
        c1 = new Cardinality(0, 1);
        assertFalse(c1.isNoCardinality());
        c1 = new Cardinality(1, 1);
        assertFalse(c1.isNoCardinality());
    }

    @Test
    public void testToString() {
        Cardinality c1 = new Cardinality(0, 0);
        assertEquals("", c1.toString());
        c1 = new Cardinality(0, 1);
        assertEquals(c1.getMin() + ":" + c1.getMax(), c1.toString());
    }

    private static class TestThrowRunnable implements Executable {

        private final int min;
        private final int max;

        public TestThrowRunnable(final int mi, final int ma) {
            min = mi;
            max = ma;
        }

        @Override
        public void execute() throws Throwable {
            Cardinality allow = new Cardinality(min, max);
        }
    }
}
