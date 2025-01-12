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

import java.util.Objects;

public class StringValue extends AbstractValue<String> {

    public StringValue(String value) {
        super(Objects.requireNonNull(value));
    }

    @Override
    public String getSMTValue() {
        return " \"" + getValue() + "\"";
    }

    @Override
    public String toString() {
        return getValue();
    }
}
