package at.jku.cps.travart.dopler.decision.model.impl;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

import at.jku.cps.travart.dopler.decision.exc.ActionExecutionException;
import at.jku.cps.travart.dopler.decision.exc.RangeValueException;
import at.jku.cps.travart.dopler.decision.exc.UnsatisfiedCardinalityException;
import at.jku.cps.travart.dopler.decision.model.ADecision;
import at.jku.cps.travart.dopler.decision.model.ARangeValue;
import at.jku.cps.travart.dopler.decision.model.IAction;
import at.jku.cps.travart.dopler.decision.model.IDecision;
import at.jku.cps.travart.dopler.decision.model.IValue;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class SetValueAction implements IAction {

	private final IDecision decision;
	private final ARangeValue value;

	public SetValueAction(final IDecision decision, final ARangeValue value) {
		this.decision = Objects.requireNonNull(decision);
		this.value = Objects.requireNonNull(value);
	}

	@Override
	public void execute() throws ActionExecutionException {
		try {
			if (decision.getType() == ADecision.DecisionType.ENUM) {
				EnumerationDecision enumDecision = (EnumerationDecision) decision;
				if (enumDecision.getCardinality().isAlternative()) {
					enumDecision.setValue((String) value.getValue());
				} else {
					Set<ARangeValue<String>> values = new HashSet<>(enumDecision.getValues());
					values.add(value);
					enumDecision.setValues(values);
				}
			} else {
				decision.setValue(value.getValue());
			}
		} catch (RangeValueException | UnsatisfiedCardinalityException e) {
			throw new ActionExecutionException(e);
		}
	}

	@Override
	public boolean isSatisfied() {
		try {
			if (!(decision.getType() == ADecision.DecisionType.ENUM)) {
				return decision.getValue().equals(value);
			}
			EnumerationDecision enumDecision = (EnumerationDecision) decision;
			if (enumDecision.getCardinality().isAlternative()) {
				return enumDecision.getValue().equals(value);
			}
			Set<ARangeValue<String>> values = new HashSet<>(enumDecision.getValues());
			return values.contains(value);
		} catch (NoSuchElementException e) {
			// if the value cannot be found in the list of values of the decision,
			// it is clearly not set, doesn't satisfy the action.
			return false;
		}
	}

	@Override
	public IDecision getVariable() {
		return decision;
	}

	@Override
	public IValue getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		return Objects.hash(decision, value);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SetValueAction other = (SetValueAction) obj;
		return Objects.equals(decision, other.decision) && Objects.equals(value, other.value);
	}

	@Override
	public String toString() {
		return decision + " = " + value + ";";
	}
}
