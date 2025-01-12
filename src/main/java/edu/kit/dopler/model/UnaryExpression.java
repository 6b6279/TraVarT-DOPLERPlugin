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

public abstract class UnaryExpression extends Expression {

    private IExpression child;

    protected UnaryExpression(IExpression child) {
        this.child = child;
    }

    public IExpression getOperand() {
        return child;
    }

    public void setChild(IExpression child) {
        this.child = child;
    }
}
