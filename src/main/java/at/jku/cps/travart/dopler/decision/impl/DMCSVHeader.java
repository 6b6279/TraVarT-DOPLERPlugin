/*******************************************************************************
 * TODO: explanation what the class does
 *
 *  @author Kevin Feichtinger
 *
 * Copyright 2023 Johannes Kepler University Linz
 * LIT Cyber-Physical Systems Lab
 * All rights reserved
 *******************************************************************************/
package at.jku.cps.travart.dopler.decision.impl;

import java.util.Arrays;

public enum DMCSVHeader {
    ID("ID"), QUESTION("Question"), TYPE("Type"), RANGE("Range"), CARDINALITY("Cardinality"), RULES("Constraint/Rule"),
    VISIBLITY("Visible/relevant if");

    private String header;

    public static String[] stringArray() {
        return Arrays.stream(DMCSVHeader.values()).map(DMCSVHeader::toString).toArray(String[]::new);
    }

    DMCSVHeader(String header) {
        this.header = header;
    }

    @Override
    public String toString() {
        return header;
    }
}
