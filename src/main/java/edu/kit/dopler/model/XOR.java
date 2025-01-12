/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/.
 * <p>
 * Contributors: 
 *    @author Fabian Eger
 *    @author Kevin Feichtinger
 * <p>
 * Copyright 2024 Karlsruhe Institute of Technology (KIT)
 * KASTEL - Dependability of Software-intensive Systems
 * All rights reserved
 *******************************************************************************/
package edu.kit.dopler.model;

import edu.kit.dopler.exceptions.EvaluationException;

import java.util.stream.Stream;

public class XOR extends BinaryExpression {

    private static final String SYMBOL = "^";

    public XOR(IExpression leftExpression, IExpression rightExpression) {
        super(leftExpression, rightExpression);
    }

    @Override
    public boolean evaluate() throws EvaluationException {
        if (getLeftExpression() instanceof BooleanLiteralExpression
                && getRightExpression() instanceof DecisionValueCallExpression) {
            boolean left = ((BooleanLiteralExpression) getLeftExpression()).getLiteral();
            boolean right = (boolean) ((DecisionValueCallExpression) getRightExpression()).getValue().getValue();
            return left ^ right;
        } else if (getLeftExpression() instanceof DecisionValueCallExpression
                && getRightExpression() instanceof BooleanLiteralExpression) {
            boolean left = (boolean) ((DecisionValueCallExpression) getLeftExpression()).getValue().getValue();
            boolean right = ((BooleanLiteralExpression) getRightExpression()).getLiteral();
            return left ^ right;
        } else if (getLeftExpression() instanceof BooleanLiteralExpression
                && getRightExpression() instanceof BooleanLiteralExpression) {
            boolean right = ((BooleanLiteralExpression) getRightExpression()).getLiteral();
            boolean left = ((BooleanLiteralExpression) getLeftExpression()).getLiteral();
            return left ^ right;
        } else {
            throw new EvaluationException("Only Boolean Values Supported");
        }
    }

    @Override
    public void toSMTStream(Stream.Builder<String> builder, String callingDecision) {
        if (getLeftExpression() instanceof BooleanLiteralExpression
                || getLeftExpression() instanceof DecisionValueCallExpression
                || getRightExpression() instanceof DecisionValueCallExpression
                || getRightExpression() instanceof BooleanLiteralExpression) {
            builder.add("(xor ");
            getLeftExpression().toSMTStream(builder, callingDecision);
            getRightExpression().toSMTStream(builder, callingDecision);
            builder.add(")");
        }
    }

    @Override
    public String toString() {
        return String.format("(%s " + SYMBOL + " %s)", getLeftExpression(), getRightExpression());
    }
}
