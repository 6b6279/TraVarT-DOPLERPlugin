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

import edu.kit.dopler.exceptions.ActionExecutionException;
import edu.kit.dopler.exceptions.EvaluationException;

import java.util.Set;

public class Rule {

    private IExpression condition;
    private Set<IAction> actions;

    public Rule(IExpression condition, Set<IAction> actions) {
        this.condition = condition;
        this.actions = actions;
    }

    public IExpression getCondition() {
        return condition;
    }

    public void setCondition(IExpression condition) {
        this.condition = condition;
    }

    public Set<IAction> getActions() {
        return actions;
    }

    public void setActions(Set<IAction> actions) {
        this.actions = actions;
    }

    public void executeActions() throws ActionExecutionException, EvaluationException {

        if (condition.evaluate()) {
            for (IAction action : actions) {
                action.execute();
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("if (").append(condition).append(") {");
        for (IAction action : actions) {
            builder.append(action).append(";");
        }
        builder.append("}");
        return builder.toString();
    }
}
