package at.jku.cps.travart.dopler.decision.model;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import at.jku.cps.travart.dopler.decision.exc.CircleInConditionException;
import at.jku.cps.travart.dopler.decision.model.impl.And;
import at.jku.cps.travart.dopler.decision.model.impl.BooleanDecision;
import at.jku.cps.travart.dopler.decision.model.impl.EnumDecision;
import at.jku.cps.travart.dopler.decision.model.impl.IsTakenFunction;
import at.jku.cps.travart.dopler.decision.model.impl.Not;
import at.jku.cps.travart.dopler.decision.model.impl.NumberDecision;
import at.jku.cps.travart.dopler.decision.model.impl.Or;
import at.jku.cps.travart.dopler.decision.model.impl.StringDecision;

@RunWith(Parameterized.class)
public class IConditionTest {
	public ICondition cond;

	public IConditionTest(final ICondition myInterface) {
		cond = myInterface;
	}

	@Test
	public void evalTest() throws CircleInConditionException {
		cond.evaluate();
	}

	@Parameterized.Parameters
	public static Collection<ICondition> instancesToTestAsCondition() {
		StringDecision d = new StringDecision("test");
		return Arrays.<ICondition>asList(ICondition.FALSE, ICondition.FALSE, ICondition.TRUE,
				new IsTakenFunction(new BooleanDecision("test")), new IsTakenFunction(new EnumDecision("test")),
				new And(ICondition.TRUE, ICondition.TRUE), new Or(ICondition.TRUE, ICondition.TRUE),
				new IsTakenFunction(d), new Not(ICondition.TRUE));
	}

	@Parameterized.Parameters
	public static Collection<ICondition> instancesToTestAsVisibility() {
		StringDecision sd = new StringDecision("id");

		return Arrays.<ICondition>asList(new IsTakenFunction(new BooleanDecision("id")),
				new IsTakenFunction(new EnumDecision("id")), new IsTakenFunction(new NumberDecision("id")),
				new IsTakenFunction(new StringDecision("id")), new And(ICondition.TRUE, ICondition.TRUE),
				new Or(ICondition.TRUE, ICondition.TRUE), new IsTakenFunction(sd), new Not(ICondition.TRUE),
				ICondition.TRUE, ICondition.FALSE);
	}

}
