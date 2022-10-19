package at.jku.cps.travart.dopler.decision.model;

import at.jku.cps.travart.dopler.decision.exc.ActionExecutionException;

public interface IAction {

	void execute() throws ActionExecutionException;

	boolean isSatisfied() throws ActionExecutionException;

	@SuppressWarnings("rawtypes")
	IDecision getVariable();

	@SuppressWarnings("rawtypes")
	IValue getValue();
}
