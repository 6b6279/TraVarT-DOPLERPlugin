package at.jku.cps.travart.dopler.transformation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import at.jku.cps.travart.core.common.Prop4JUtils;
import at.jku.cps.travart.core.common.exc.NotSupportedVariablityTypeException;
import at.jku.cps.travart.dopler.decision.factory.impl.DecisionModelFactory;
import at.jku.cps.travart.dopler.decision.impl.DecisionModel;
import at.jku.cps.travart.dopler.decision.model.ICondition;
import at.jku.cps.travart.dopler.decision.model.impl.And;
import at.jku.cps.travart.dopler.decision.model.impl.BooleanDecision;
import at.jku.cps.travart.dopler.decision.model.impl.BooleanValue;
import at.jku.cps.travart.dopler.decision.model.impl.Cardinality;
import at.jku.cps.travart.dopler.decision.model.impl.DecisionValueCondition;
import at.jku.cps.travart.dopler.decision.model.impl.DisAllowAction;
import at.jku.cps.travart.dopler.decision.model.impl.DoubleValue;
import at.jku.cps.travart.dopler.decision.model.impl.EnumDecision;
import at.jku.cps.travart.dopler.decision.model.impl.IsTakenFunction;
import at.jku.cps.travart.dopler.decision.model.impl.Not;
import at.jku.cps.travart.dopler.decision.model.impl.NumberDecision;
import at.jku.cps.travart.dopler.decision.model.impl.Rule;
import at.jku.cps.travart.dopler.decision.model.impl.SelectDecisionAction;
import at.jku.cps.travart.dopler.decision.model.impl.SetValueAction;
import at.jku.cps.travart.dopler.decision.model.impl.StringDecision;
import at.jku.cps.travart.dopler.decision.model.impl.StringValue;
import de.ovgu.featureide.fm.core.base.FeatureUtils;
import de.ovgu.featureide.fm.core.base.IConstraint;
import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.base.impl.DefaultFeatureModelFactory;
import de.ovgu.featureide.fm.core.base.impl.Feature;
import de.ovgu.featureide.fm.core.base.impl.FeatureModel;

public class DecisionModeltoFeatureModelTransformerTest {
	private DecisionModelFactory factory;
	private DecisionModel dm;

	@Before
	public void setUp() throws Exception {
		factory = DecisionModelFactory.getInstance();
		dm = factory.create();
		dm.setName("TestModel");
	}

	@Test
	public void testTransformOrDecision() throws NotSupportedVariablityTypeException {
		EnumDecision ed1 = new EnumDecision("ed1");
		StringValue sv1 = new StringValue("sv1");
		StringValue sv2 = new StringValue("sv2");
		StringValue sv3 = new StringValue("sv3");
		ed1.getRange().add(sv1);
		ed1.getRange().add(sv2);
		ed1.getRange().add(sv3);
		ed1.setCardinality(new Cardinality(1, 3));
		dm.add(ed1);
		DecisionModeltoFeatureModelTransformer conv = new DecisionModeltoFeatureModelTransformer();

		IFeatureModel fm = conv.transform(dm);
		Collection<IFeature> f = fm.getFeatures();
		IFeature theFeature = f.stream().filter(e -> e.getName().equals(ed1.getId().substring(2))).findAny().get();
		assertTrue("Multiple selections should be allowed for Or.", theFeature.getStructure().isMultiple());
		assertFalse("And should not be set for Or connection", theFeature.getStructure().isAnd());
		assertTrue(theFeature.getStructure().getChildren().size() == 3);
	}

	@Test
	public void testTransformAlternative() throws NotSupportedVariablityTypeException {
		EnumDecision ed1 = new EnumDecision("ed1");
		StringValue sv1 = new StringValue("sv1");
		StringValue sv2 = new StringValue("sv2");
		StringValue sv3 = new StringValue("sv3");
		ed1.getRange().add(sv1);
		ed1.getRange().add(sv2);
		ed1.getRange().add(sv3);
		ed1.setCardinality(new Cardinality(1, 1));
		dm.add(ed1);
		DecisionModeltoFeatureModelTransformer conv = new DecisionModeltoFeatureModelTransformer();

		IFeatureModel controlModel = new FeatureModel("TestModel");

		IFeature cmed1 = new Feature(controlModel, ed1.getName());
		FeatureUtils.setRoot(controlModel, cmed1);
		FeatureUtils.setAlternative(cmed1);
		IFeature cmsv1 = new Feature(controlModel, sv1.getValue());
		IFeature cmsv2 = new Feature(controlModel, sv2.getValue());
		IFeature cmsv3 = new Feature(controlModel, sv3.getValue());

		FeatureUtils.addChild(cmed1, cmsv1);
		FeatureUtils.addChild(cmed1, cmsv2);
		FeatureUtils.addChild(cmed1, cmsv3);

		FeatureUtils.addFeature(controlModel, cmed1);
		FeatureUtils.addFeature(controlModel, cmsv1);
		FeatureUtils.addFeature(controlModel, cmsv2);
		FeatureUtils.addFeature(controlModel, cmsv3);
		IFeatureModel fm = conv.transform(dm);
		System.out.println("fm Id: " + fm.getId());
		System.out.println("controlModel Id: " + controlModel.getId());

		assertTrue(areFMEqual(controlModel, fm));

	}

	@Test
	public void testTransformOrFullModelCheck() throws NotSupportedVariablityTypeException {
		EnumDecision ed1 = new EnumDecision("ed1");
		StringValue sv1 = new StringValue("sv1");
		StringValue sv2 = new StringValue("sv2");
		StringValue sv3 = new StringValue("sv3");
		ed1.getRange().add(sv1);
		ed1.getRange().add(sv2);
		ed1.getRange().add(sv3);
		ed1.setCardinality(new Cardinality(1, 3));
		dm.add(ed1);
		DecisionModeltoFeatureModelTransformer conv = new DecisionModeltoFeatureModelTransformer();

		IFeatureModel controlModel = new FeatureModel("TestModel");

		IFeature cmed1 = new Feature(controlModel, ed1.getName());
		FeatureUtils.setRoot(controlModel, cmed1);
		IFeature cmsv1 = new Feature(controlModel, sv1.getValue());
		IFeature cmsv2 = new Feature(controlModel, sv2.getValue());
		IFeature cmsv3 = new Feature(controlModel, sv3.getValue());

		FeatureUtils.addChild(cmed1, cmsv1);
		FeatureUtils.addChild(cmed1, cmsv2);
		FeatureUtils.addChild(cmed1, cmsv3);

		FeatureUtils.addFeature(controlModel, cmed1);
		FeatureUtils.addFeature(controlModel, cmsv1);
		FeatureUtils.addFeature(controlModel, cmsv2);
		FeatureUtils.addFeature(controlModel, cmsv3);
		FeatureUtils.setOr(cmed1);
		IFeatureModel fm = conv.transform(dm);

		assertTrue(areFMEqual(controlModel, fm));
	}

	@Test
	public void testTransformOrFullModelCheck2Enums() throws NotSupportedVariablityTypeException {
		EnumDecision ed1 = new EnumDecision("ed1");
		EnumDecision ed2 = new EnumDecision("ed2");

		StringValue sv1 = new StringValue("sv1");
		StringValue sv2 = new StringValue("sv2");
		StringValue sv3 = new StringValue("sv3");
		ed2.getRange().add(sv1);
		ed2.getRange().add(ed2.getNoneOption());
		ed1.getRange().add(sv1);
		ed1.getRange().add(sv2);
		ed1.getRange().add(sv3);
		ed1.setCardinality(new Cardinality(1, 3));
		dm.add(ed1);
		dm.add(ed2);
		DecisionModeltoFeatureModelTransformer conv = new DecisionModeltoFeatureModelTransformer();

		IFeatureModel controlModel = new FeatureModel("TestModel");
		// more than one decision requires virtual root
		IFeature vr = new Feature(controlModel, "VIRTUAL_ROOT");
		FeatureUtils.setOr(vr);
		IFeature cmed1 = new Feature(controlModel, ed1.getName());
		IFeature cmed2 = new Feature(controlModel, ed2.getName());
		FeatureUtils.setRoot(controlModel, vr);
		IFeature cmsv1 = new Feature(controlModel, sv1.getValue());
		IFeature cmsv2 = new Feature(controlModel, sv2.getValue());
		IFeature cmsv3 = new Feature(controlModel, sv3.getValue());

		FeatureUtils.addChild(vr, cmed1);
		FeatureUtils.addChild(vr, cmed2);

		FeatureUtils.addChild(cmed1, cmsv1);
		FeatureUtils.addChild(cmed1, cmsv2);
		FeatureUtils.addChild(cmed1, cmsv3);

		FeatureUtils.addChild(cmed2, cmsv1);

		FeatureUtils.addFeature(controlModel, vr);
		FeatureUtils.addFeature(controlModel, cmed1);
		FeatureUtils.addFeature(controlModel, cmed2);
		FeatureUtils.addFeature(controlModel, cmsv1);
		FeatureUtils.addFeature(controlModel, cmsv2);
		FeatureUtils.addFeature(controlModel, cmsv3);
		FeatureUtils.setOr(cmed1);
		IFeatureModel fm = conv.transform(dm);

		assertTrue(areFMEqual(controlModel, fm));
	}

	@Test
	public void testTransformBooleanDecision() throws NotSupportedVariablityTypeException {
		IFeatureModel controlModel = new FeatureModel("TestModel");
		BooleanDecision bd1 = new BooleanDecision("bd1");
		dm.add(bd1);

		IFeature cmbd1 = new Feature(controlModel, "bd1");
		FeatureUtils.addFeature(controlModel, cmbd1);

		DecisionModeltoFeatureModelTransformer conv = new DecisionModeltoFeatureModelTransformer();

		IFeatureModel fm = conv.transform(dm);
		assertTrue(areFMEqual(controlModel, fm));
	}

	@Test
	public void testTransformNumberDecision() throws NotSupportedVariablityTypeException {
		IFeatureModel controlModel = new FeatureModel("TestModel");
		NumberDecision nd1 = new NumberDecision("nd1");
		DoubleValue dv1 = new DoubleValue(1);
		DoubleValue dv2 = new DoubleValue(2);
		DoubleValue dv3 = new DoubleValue(3);
		nd1.getRange().add(dv1);
		nd1.getRange().add(dv2);
		nd1.getRange().add(dv3);
		dm.add(nd1);

		IFeature cmbd1 = new Feature(controlModel, nd1.getName());

		IFeature cmdv1 = new Feature(controlModel, nd1.getName() + "_" + dv1.getValue().toString());
		IFeature cmdv2 = new Feature(controlModel, nd1.getName() + "_" + dv2.getValue().toString());
		IFeature cmdv3 = new Feature(controlModel, nd1.getName() + "_" + dv3.getValue().toString());

		FeatureUtils.addChild(cmbd1, cmdv1);
		FeatureUtils.addChild(cmbd1, cmdv2);
		FeatureUtils.addChild(cmbd1, cmdv3);
		FeatureUtils.addFeature(controlModel, cmdv1);
		FeatureUtils.addFeature(controlModel, cmdv2);
		FeatureUtils.addFeature(controlModel, cmdv3);

		FeatureUtils.addFeature(controlModel, cmbd1);
		// Numberdecisions are always alternative
		FeatureUtils.setAlternative(cmbd1);

		DecisionModeltoFeatureModelTransformer conv = new DecisionModeltoFeatureModelTransformer();

		IFeatureModel fm = conv.transform(dm);
		assertTrue(areFMEqual(controlModel, fm));
	}

	@Test
	public void testTransformStringDecision() throws NotSupportedVariablityTypeException {
		IFeatureModel controlModel = new FeatureModel("TestModel");
		StringDecision sd1 = new StringDecision("sd1");
		dm.add(sd1);
		IFeature cmsd1 = new Feature(controlModel, sd1.getName());
		FeatureUtils.addFeature(controlModel, cmsd1);

		DecisionModeltoFeatureModelTransformer conv = new DecisionModeltoFeatureModelTransformer();

		IFeatureModel fm = conv.transform(dm);
		assertTrue(areFMEqual(controlModel, fm));
	}

	@Test
	public void testTransformMandatoryVisibilityCondition() throws NotSupportedVariablityTypeException {
		EnumDecision ed1 = new EnumDecision("ed1");
		EnumDecision ed2 = new EnumDecision("ed2");

		StringValue sv1 = new StringValue("sv1");
		StringValue sv2 = new StringValue("sv2");
		StringValue sv3 = new StringValue("sv3");
		ed2.getRange().add(sv1);
		ed2.getRange().add(ed2.getNoneOption());
		ed1.getRange().add(sv1);
		ed1.getRange().add(sv2);
		ed1.getRange().add(sv3);
		ed1.setCardinality(new Cardinality(1, 3));
		And a = new And(ICondition.FALSE, new IsTakenFunction(ed2));
		ed1.setVisibility(a);
		dm.add(ed1);
		dm.add(ed2);
		DecisionModeltoFeatureModelTransformer conv = new DecisionModeltoFeatureModelTransformer();

		IFeatureModel controlModel = new FeatureModel("TestModel");
		IFeature cmed1 = new Feature(controlModel, ed1.getName());
		IFeature cmed2 = new Feature(controlModel, ed2.getName());
		IFeature cmsv1 = new Feature(controlModel, sv1.getValue());
		IFeature cmsv2 = new Feature(controlModel, sv2.getValue());
		IFeature cmsv3 = new Feature(controlModel, sv3.getValue());

		FeatureUtils.setMandatory(cmed1, true);

		FeatureUtils.addChild(cmed1, cmsv1);
		FeatureUtils.addChild(cmed1, cmsv2);
		FeatureUtils.addChild(cmed1, cmsv3);

		FeatureUtils.addChild(cmed2, cmsv1);

		FeatureUtils.addFeature(controlModel, cmed1);
		FeatureUtils.addFeature(controlModel, cmed2);
		FeatureUtils.addFeature(controlModel, cmsv1);
		FeatureUtils.addFeature(controlModel, cmsv2);
		FeatureUtils.addFeature(controlModel, cmsv3);
		FeatureUtils.setOr(cmed1);
		IFeatureModel fm = conv.transform(dm);

		assertTrue(areFMEqual(controlModel, fm));
	}

	@Test
	public void testTransformDecisionVisibilityCondition() throws NotSupportedVariablityTypeException {
		EnumDecision ed1 = new EnumDecision("ed1");
		EnumDecision ed2 = new EnumDecision("ed2");
		BooleanDecision bd1 = new BooleanDecision("bd1");

		StringValue sv1 = new StringValue("sv1");
		StringValue sv2 = new StringValue("sv2");
		StringValue sv3 = new StringValue("sv3");
		ed2.getRange().add(sv1);
		ed2.getRange().add(ed2.getNoneOption());
		ed1.getRange().add(sv1);
		ed1.getRange().add(sv2);
		ed1.getRange().add(sv3);
		ed1.setCardinality(new Cardinality(1, 3));
		ed1.setVisibility(new IsTakenFunction(bd1));
		dm.add(ed1);
		dm.add(ed2);
		dm.add(bd1);
		DecisionModeltoFeatureModelTransformer conv = new DecisionModeltoFeatureModelTransformer();

		IFeatureModel controlModel = new FeatureModel("TestModel");
		IFeature vr = new Feature(controlModel, "VIRTUAL_ROOT");
		FeatureUtils.setOr(vr);

		IFeature cmed1 = new Feature(controlModel, ed1.getName());
		IFeature cmed2 = new Feature(controlModel, ed2.getName());
		IFeature cmbd1 = new Feature(controlModel, bd1.getName());
		IFeature cmsv1 = new Feature(controlModel, sv1.getValue());
		IFeature cmsv2 = new Feature(controlModel, sv2.getValue());
		IFeature cmsv3 = new Feature(controlModel, sv3.getValue());

		FeatureUtils.setRoot(controlModel, vr);
		FeatureUtils.addChild(vr, cmed1);
		FeatureUtils.addChild(vr, cmed2);
		FeatureUtils.addChild(vr, cmbd1);

		FeatureUtils.addChild(cmed1, cmsv1);
		FeatureUtils.addChild(cmed1, cmsv2);
		FeatureUtils.addChild(cmed1, cmsv3);

		FeatureUtils.addChild(cmbd1, cmed1);

		FeatureUtils.addChild(cmed2, cmsv1);

		FeatureUtils.addFeature(controlModel, vr);
		FeatureUtils.addFeature(controlModel, cmbd1);
		FeatureUtils.addFeature(controlModel, cmed1);
		FeatureUtils.addFeature(controlModel, cmed2);
		FeatureUtils.addFeature(controlModel, cmsv1);
		FeatureUtils.addFeature(controlModel, cmsv2);
		FeatureUtils.addFeature(controlModel, cmsv3);
		FeatureUtils.setOr(cmed1);
		IFeatureModel fm = conv.transform(dm);

		assertTrue(areFMEqual(controlModel, fm));
	}

	@Test
	public void testTransformMandatoryConstraint() throws NotSupportedVariablityTypeException {
		EnumDecision ed1 = new EnumDecision("ed1");
		And a = new And(ICondition.FALSE, ICondition.FALSE);
		ed1.setVisibility(a);
		dm.add(ed1);

		IFeatureModel controlModel = new FeatureModel("TestModel");
		IFeature cmed1 = new Feature(controlModel, "ed1");
		FeatureUtils.setMandatory(cmed1, true);
		cmed1.getCustomProperties().set(DefaultDecisionModelTransformationProperties.PROPERTY_KEY_VISIBILITY,
				DefaultDecisionModelTransformationProperties.PROPERTY_KEY_VISIBILITY_TYPE,
				ed1.getVisiblity().toString());
		FeatureUtils.addFeature(controlModel, cmed1);

		DecisionModeltoFeatureModelTransformer conv = new DecisionModeltoFeatureModelTransformer();
		IFeatureModel fm = conv.transform(dm);

		assertTrue(areFMEqual(controlModel, fm));
	}

	// TODO: Commented tests need fix, after we split value conditions from
	// selection conditions
//	// If the Value of NumberDecision nd1 is Equal to the value of DoubleValue
//	// dv[0] (=0) then the StringValue sv1 should be disallowed for EnumDecision ed1
//	// Controlmodel has: propNode=d_nd1_0.0 => -sv1
//	// transformed model has: propNode=null => -sv1
//	// possibly impossible to reconstruct as a constraint in FeatureIDE because
//	// there is no
//	// way of checking the equality of 2 nodes. Current implementation creates
//	// ambiguity
//	@Test // TODO not sure if controlModel constraint is right. might need a fix
//	public void testTransformRulesCompareEqualDisAllow()
//			throws NotSupportedVariablityTypeException, RangeValueException {
//		EnumDecision ed1 = new EnumDecision("ed1");
//		StringValue sv1 = new StringValue("sv1");
//		ed1.getRange().add(sv1);
//		NumberDecision nd1 = new NumberDecision("nd1");
//		DoubleValue[] dv1 = new DoubleValue[] { new DoubleValue(0), new DoubleValue(1), new DoubleValue(2) };
//		Collections.addAll(nd1.getRange(), dv1);
//		nd1.setValue(dv1[1]);
//		NumberDecision nd2 = new NumberDecision("nd2");
//		DoubleValue[] dv2 = new DoubleValue[] { new DoubleValue(0), new DoubleValue(1), new DoubleValue(2) };
//		Collections.addAll(nd1.getRange(), dv2);
//		nd2.setValue(dv2[1]);
//		Equals e = new Equals(nd1, nd2);
//		Rule r = new Rule(e, new DisAllowAction(ed1, sv1));
//		nd1.addRule(r);
//		dm.add(ed1);
//		dm.add(nd1);
//		dm.add(nd2);
//
//		IFeatureModel controlModel = new FeatureModel("TestModel");
//		IFeature vr = new Feature(controlModel, "VIRTUAL_ROOT");
//		FeatureUtils.setRoot(controlModel, vr);
//		FeatureUtils.setOr(vr);
//		IFeature cmed1 = new Feature(controlModel, ed1.getName());
//		IFeature cmsv1 = new Feature(controlModel, sv1.getValue());
//		IFeature cmnd1 = new Feature(controlModel, nd1.getName());
//		IFeature cmnd2 = new Feature(controlModel, nd2.getName());
//		IFeature cmdv1 = new Feature(controlModel, nd1.getName() + "_" + dv1[0].getValue());
//		IFeature cmdv2 = new Feature(controlModel, nd1.getName() + "_" + dv1[1].getValue());
//		IFeature cmdv3 = new Feature(controlModel, nd1.getName() + "_" + dv1[2].getValue());
//		IFeature cmdv4 = new Feature(controlModel, nd2.getName() + "_" + dv2[0].getValue());
//		IFeature cmdv5 = new Feature(controlModel, nd2.getName() + "_" + dv2[1].getValue());
//		IFeature cmdv6 = new Feature(controlModel, nd2.getName() + "_" + dv2[2].getValue());
//
//		FeatureUtils.addChild(vr, cmed1);
//		FeatureUtils.addChild(vr, cmnd1);
//		FeatureUtils.addChild(vr, cmnd2);
//
//		FeatureUtils.addChild(cmed1, cmsv1);
//
//		FeatureUtils.addChild(cmnd1, cmdv1);
//		FeatureUtils.addChild(cmnd1, cmdv2);
//		FeatureUtils.addChild(cmnd1, cmdv3);
//
//		FeatureUtils.addChild(cmnd2, cmdv4);
//		FeatureUtils.addChild(cmnd2, cmdv5);
//		FeatureUtils.addChild(cmnd2, cmdv6);
//
//		FeatureUtils.addFeature(controlModel, vr);
//		FeatureUtils.addFeature(controlModel, cmnd1);
//		FeatureUtils.addFeature(controlModel, cmnd2);
//		FeatureUtils.addFeature(controlModel, cmed1);
//		FeatureUtils.addFeature(controlModel, cmsv1);
//
//		FeatureUtils.addFeature(controlModel, cmdv1);
//		FeatureUtils.addFeature(controlModel, cmdv2);
//		FeatureUtils.addFeature(controlModel, cmdv3);
//		FeatureUtils.addFeature(controlModel, cmdv4);
//		FeatureUtils.addFeature(controlModel, cmdv5);
//		FeatureUtils.addFeature(controlModel, cmdv6);
//		FeatureUtils.setMandatory(cmed1, true);
//		DefaultFeatureModelFactory factory = new DefaultFeatureModelFactory();
//		IConstraint constr = factory.createConstraint(controlModel, Prop4JUtils.createImplies(
//				Prop4JUtils.createLiteral(cmdv2), Prop4JUtils.createNot(Prop4JUtils.createLiteral(cmsv1))));
//		FeatureUtils.addConstraint(controlModel, constr);
//
//		DecisionModeltoFeatureModelTransformer conv = new DecisionModeltoFeatureModelTransformer();
//		IFeatureModel fm = conv.transform(dm);
//
//		assertTrue(areFMEqual(controlModel, fm));
//	}
//
//	// If the Value of NumberDecision nd1 is Greater than the value of DoubleValue
//	// dv[0] (=0) then the StringValue sv1 should be disallowed for EnumDecision ed1
//	@Test // TODO not sure if controlModel constraint is right. might need a fix
//	public void testTransformRulesCompareGreaterDisAllow() throws NotSupportedVariablityTypeException {
//		EnumDecision ed1 = new EnumDecision("ed1");
//		StringValue sv1 = new StringValue("sv1");
//		ed1.getRange().add(sv1);
//		NumberDecision nd1 = new NumberDecision("nd1");
//		DoubleValue[] dv = new DoubleValue[] { new DoubleValue(0), new DoubleValue(1), new DoubleValue(2) };
//		Collections.addAll(nd1.getRange(), dv);
//		Greater e = new Greater(nd1, dv[0]);
//		Rule r = new Rule(e, new DisAllowAction(ed1, sv1));
//		nd1.addRule(r);
//		dm.add(ed1);
//		dm.add(nd1);
//
//		IFeatureModel controlModel = new FeatureModel("TestModel");
//		IFeature vr = new Feature(controlModel, "VIRTUAL_ROOT");
//		FeatureUtils.setRoot(controlModel, vr);
//		FeatureUtils.setOr(vr);
//		IFeature cmed1 = new Feature(controlModel, ed1.getName());
//		IFeature cmsv1 = new Feature(controlModel, sv1.getValue());
//		IFeature cmnd1 = new Feature(controlModel, nd1.getName());
//		IFeature cmdv1 = new Feature(controlModel, nd1.getName() + "_" + dv[0].getValue());
//		IFeature cmdv2 = new Feature(controlModel, nd1.getName() + "_" + dv[1].getValue());
//		IFeature cmdv3 = new Feature(controlModel, nd1.getName() + "_" + dv[2].getValue());
//		FeatureUtils.addChild(vr, cmed1);
//		FeatureUtils.addChild(vr, cmnd1);
//
//		FeatureUtils.addChild(cmed1, cmsv1);
//
//		FeatureUtils.addChild(cmnd1, cmdv1);
//		FeatureUtils.addChild(cmnd1, cmdv2);
//		FeatureUtils.addChild(cmnd1, cmdv3);
//
//		FeatureUtils.addFeature(controlModel, vr);
//		FeatureUtils.addFeature(controlModel, cmnd1);
//		FeatureUtils.addFeature(controlModel, cmed1);
//		FeatureUtils.addFeature(controlModel, cmsv1);
//
//		FeatureUtils.addFeature(controlModel, cmdv1);
//		FeatureUtils.addFeature(controlModel, cmdv2);
//		FeatureUtils.addFeature(controlModel, cmdv3);
//		FeatureUtils.setMandatory(cmed1, true);
//		DefaultFeatureModelFactory factory = new DefaultFeatureModelFactory();
//		IConstraint constr1 = factory.createConstraint(controlModel, Prop4JUtils.createImplies(
//				Prop4JUtils.createLiteral(cmdv2), Prop4JUtils.createNot(Prop4JUtils.createLiteral(cmsv1))));
//		IConstraint constr2 = factory.createConstraint(controlModel, Prop4JUtils.createImplies(
//				Prop4JUtils.createLiteral(cmdv3), Prop4JUtils.createNot(Prop4JUtils.createLiteral(cmsv1))));
//		FeatureUtils.addConstraint(controlModel, constr1);
//		FeatureUtils.addConstraint(controlModel, constr2);
//
//		DecisionModeltoFeatureModelTransformer conv = new DecisionModeltoFeatureModelTransformer();
//		IFeatureModel fm = conv.transform(dm);
//
//		assertTrue(areFMEqual(controlModel, fm));
//	}
//
//	// If the Value of NumberDecision nd1 is GreaterEquals than the value of
//	// DoubleValue
//	// dv[0] (=0) then the StringValue sv1 should be disallowed for EnumDecision ed1
//	// controlModel= nd1_0.0 => -sv1, nd1_1.0 => -sv1, nd1_2.0 => -sv1
//	// transformed = null => -sv1,null => -sv1,null => -sv1
//	// reason is because right nodes can't be found because of d_ naming
//	@Test // TODO not sure if controlModel constraint is right. might need a fix
//	public void testTransformRulesCompareGreaterEqualDisAllow() throws NotSupportedVariablityTypeException {
//		EnumDecision ed1 = new EnumDecision("ed1");
//		StringValue sv1 = new StringValue("sv1");
//		ed1.getRange().add(sv1);
//		NumberDecision nd1 = new NumberDecision("nd1");
//		DoubleValue[] dv = new DoubleValue[] { new DoubleValue(0), new DoubleValue(1), new DoubleValue(2) };
//		Collections.addAll(nd1.getRange(), dv);
//		GreaterEquals e = new GreaterEquals(nd1, dv[0]);
//		Rule r = new Rule(e, new DisAllowAction(ed1, sv1));
//		nd1.addRule(r);
//		dm.add(ed1);
//		dm.add(nd1);
//
//		IFeatureModel controlModel = new FeatureModel("TestModel");
//		IFeature vr = new Feature(controlModel, "VIRTUAL_ROOT");
//		FeatureUtils.setRoot(controlModel, vr);
//		FeatureUtils.setOr(vr);
//		IFeature cmed1 = new Feature(controlModel, ed1.getName());
//		IFeature cmsv1 = new Feature(controlModel, sv1.getValue());
//		IFeature cmnd1 = new Feature(controlModel, nd1.getName());
//		IFeature cmdv1 = new Feature(controlModel, nd1.getName() + "_" + dv[0].getValue());
//		IFeature cmdv2 = new Feature(controlModel, nd1.getName() + "_" + dv[1].getValue());
//		IFeature cmdv3 = new Feature(controlModel, nd1.getName() + "_" + dv[2].getValue());
//		FeatureUtils.addChild(vr, cmed1);
//		FeatureUtils.addChild(vr, cmnd1);
//
//		FeatureUtils.addChild(cmed1, cmsv1);
//
//		FeatureUtils.addChild(cmnd1, cmdv1);
//		FeatureUtils.addChild(cmnd1, cmdv2);
//		FeatureUtils.addChild(cmnd1, cmdv3);
//
//		FeatureUtils.addFeature(controlModel, vr);
//		FeatureUtils.addFeature(controlModel, cmnd1);
//		FeatureUtils.addFeature(controlModel, cmed1);
//		FeatureUtils.addFeature(controlModel, cmsv1);
//
//		FeatureUtils.addFeature(controlModel, cmdv1);
//		FeatureUtils.addFeature(controlModel, cmdv2);
//		FeatureUtils.addFeature(controlModel, cmdv3);
//		FeatureUtils.setMandatory(cmed1, true);
//		DefaultFeatureModelFactory factory = new DefaultFeatureModelFactory();
//		IConstraint constr0 = factory.createConstraint(controlModel, Prop4JUtils.createImplies(
//				Prop4JUtils.createLiteral(cmdv1), Prop4JUtils.createNot(Prop4JUtils.createLiteral(cmsv1))));
//
//		IConstraint constr1 = factory.createConstraint(controlModel, Prop4JUtils.createImplies(
//				Prop4JUtils.createLiteral(cmdv2), Prop4JUtils.createNot(Prop4JUtils.createLiteral(cmsv1))));
//		IConstraint constr2 = factory.createConstraint(controlModel, Prop4JUtils.createImplies(
//				Prop4JUtils.createLiteral(cmdv3), Prop4JUtils.createNot(Prop4JUtils.createLiteral(cmsv1))));
//		FeatureUtils.addConstraint(controlModel, constr0);
//		FeatureUtils.addConstraint(controlModel, constr1);
//		FeatureUtils.addConstraint(controlModel, constr2);
//
//		DecisionModeltoFeatureModelTransformer conv = new DecisionModeltoFeatureModelTransformer();
//		IFeatureModel fm = conv.transform(dm);
//
//		assertTrue(areFMEqual(controlModel, fm));
//	}
//
//	// If the Value of NumberDecision nd1 is less than the value of DoubleValue
//	// dv[0] (=0) then the StringValue sv1 should be disallowed for EnumDecision ed1
//	// controlModel= propNode=d_nd1_0.0 => -sv1
//	// transformed= propNode=null => -sv1
//	// TODO not sure if controlModel constraint is right. might need a fix,
//	// couldn't find anything that would model a "Less" relation in feature model
//	@Test
//	public void testTransformRulesCompareLessDisAllow() throws NotSupportedVariablityTypeException {
//		EnumDecision ed1 = new EnumDecision("ed1");
//		StringValue sv1 = new StringValue("sv1");
//		ed1.getRange().add(sv1);
//		NumberDecision nd1 = new NumberDecision("nd1");
//		DoubleValue[] dv = new DoubleValue[] { new DoubleValue(0), new DoubleValue(1), new DoubleValue(2) };
//		Collections.addAll(nd1.getRange(), dv);
//		Less e = new Less(nd1, dv[2]);
//		Rule r = new Rule(e, new DisAllowAction(ed1, sv1));
//		nd1.addRule(r);
//		dm.add(ed1);
//		dm.add(nd1);
//
//		IFeatureModel controlModel = new FeatureModel("TestModel");
//		IFeature vr = new Feature(controlModel, "VIRTUAL_ROOT");
//		FeatureUtils.setRoot(controlModel, vr);
//		FeatureUtils.setOr(vr);
//		IFeature cmed1 = new Feature(controlModel, ed1.getName());
//		IFeature cmsv1 = new Feature(controlModel, sv1.getValue());
//		IFeature cmnd1 = new Feature(controlModel, nd1.getName());
//		IFeature cmdv1 = new Feature(controlModel, nd1.getName() + "_" + dv[0].getValue());
//		IFeature cmdv2 = new Feature(controlModel, nd1.getName() + "_" + dv[1].getValue());
//		IFeature cmdv3 = new Feature(controlModel, nd1.getName() + "_" + dv[2].getValue());
//		FeatureUtils.addChild(vr, cmed1);
//		FeatureUtils.addChild(vr, cmnd1);
//
//		FeatureUtils.addChild(cmed1, cmsv1);
//
//		FeatureUtils.addChild(cmnd1, cmdv1);
//		FeatureUtils.addChild(cmnd1, cmdv2);
//		FeatureUtils.addChild(cmnd1, cmdv3);
//
//		FeatureUtils.addFeature(controlModel, vr);
//		FeatureUtils.addFeature(controlModel, cmnd1);
//		FeatureUtils.addFeature(controlModel, cmed1);
//		FeatureUtils.addFeature(controlModel, cmsv1);
//
//		FeatureUtils.addFeature(controlModel, cmdv1);
//		FeatureUtils.addFeature(controlModel, cmdv2);
//		FeatureUtils.addFeature(controlModel, cmdv3);
//		FeatureUtils.setMandatory(cmed1, true);
//		DefaultFeatureModelFactory factory = new DefaultFeatureModelFactory();
//		IConstraint constr = factory.createConstraint(controlModel, Prop4JUtils.createImplies(
//				Prop4JUtils.createLiteral(cmdv1), Prop4JUtils.createNot(Prop4JUtils.createLiteral(cmsv1))));
//		FeatureUtils.addConstraint(controlModel, constr);
//
//		DecisionModeltoFeatureModelTransformer conv = new DecisionModeltoFeatureModelTransformer();
//		IFeatureModel fm = conv.transform(dm);
//
//		assertTrue(areFMEqual(controlModel, fm));
//	}
//
//	// If the Value of NumberDecision nd1 is lessEquals than the value of
//	// DoubleValue
//	// dv[0] (=0) then the StringValue sv1 should be disallowed for EnumDecision ed1
//	@Test // TODO not sure if controlModel constraint is right. might need a fix
//	public void testTransformRulesCompareLessEqualsDisAllow() throws NotSupportedVariablityTypeException {
//		EnumDecision ed1 = new EnumDecision("ed1");
//		StringValue sv1 = new StringValue("sv1");
//		ed1.getRange().add(sv1);
//		NumberDecision nd1 = new NumberDecision("nd1");
//		DoubleValue[] dv = new DoubleValue[] { new DoubleValue(0), new DoubleValue(1), new DoubleValue(2) };
//		Collections.addAll(nd1.getRange(), dv);
//		LessEquals e = new LessEquals(nd1, dv[0]);
//		Rule r = new Rule(e, new DisAllowAction(ed1, sv1));
//		nd1.addRule(r);
//		dm.add(ed1);
//		dm.add(nd1);
//
//		IFeatureModel controlModel = new FeatureModel("TestModel");
//		IFeature vr = new Feature(controlModel, "VIRTUAL_ROOT");
//		FeatureUtils.setRoot(controlModel, vr);
//		FeatureUtils.setOr(vr);
//		IFeature cmed1 = new Feature(controlModel, ed1.getName());
//		IFeature cmsv1 = new Feature(controlModel, sv1.getValue());
//		IFeature cmnd1 = new Feature(controlModel, nd1.getName());
//		IFeature cmdv1 = new Feature(controlModel, nd1.getName() + "_" + dv[0].getValue());
//		IFeature cmdv2 = new Feature(controlModel, nd1.getName() + "_" + dv[1].getValue());
//		IFeature cmdv3 = new Feature(controlModel, nd1.getName() + "_" + dv[2].getValue());
//		FeatureUtils.addChild(vr, cmed1);
//		FeatureUtils.addChild(vr, cmnd1);
//
//		FeatureUtils.addChild(cmed1, cmsv1);
//
//		FeatureUtils.addChild(cmnd1, cmdv1);
//		FeatureUtils.addChild(cmnd1, cmdv2);
//		FeatureUtils.addChild(cmnd1, cmdv3);
//
//		FeatureUtils.addFeature(controlModel, vr);
//		FeatureUtils.addFeature(controlModel, cmnd1);
//		FeatureUtils.addFeature(controlModel, cmed1);
//		FeatureUtils.addFeature(controlModel, cmsv1);
//
//		FeatureUtils.addFeature(controlModel, cmdv1);
//		FeatureUtils.addFeature(controlModel, cmdv2);
//		FeatureUtils.addFeature(controlModel, cmdv3);
//		FeatureUtils.setMandatory(cmed1, true);
//		DefaultFeatureModelFactory factory = new DefaultFeatureModelFactory();
//		IConstraint constr = factory.createConstraint(controlModel, Prop4JUtils.createImplies(
//				Prop4JUtils.createLiteral(cmdv1), Prop4JUtils.createNot(Prop4JUtils.createLiteral(cmsv1))));
//		FeatureUtils.addConstraint(controlModel, constr);
//
//		DecisionModeltoFeatureModelTransformer conv = new DecisionModeltoFeatureModelTransformer();
//		IFeatureModel fm = conv.transform(dm);
//
//		assertTrue(areFMEqual(controlModel, fm));
//	}

	// Within the rule a BooleanDecision bd1 DisAllows a StringValue sv1 for
	// EnumDecision ed1
	@Test
	public void testTransformRulesBooleanConditionDisAllow() throws NotSupportedVariablityTypeException {
		EnumDecision ed1 = new EnumDecision("ed1");
		StringValue sv1 = new StringValue("sv1");
		BooleanDecision bd1 = new BooleanDecision("bd1");
		ed1.getRange().add(sv1);
		Rule r = new Rule(new IsTakenFunction(bd1), new DisAllowAction(ed1, sv1));
		bd1.addRule(r);
		dm.add(ed1);
		dm.add(bd1);

		IFeatureModel controlModel = new FeatureModel("TestModel");
		IFeature vr = new Feature(controlModel, "VIRTUAL_ROOT");
		FeatureUtils.setRoot(controlModel, vr);
		FeatureUtils.setOr(vr);
		IFeature cmed1 = new Feature(controlModel, ed1.getName());
		IFeature cmbd1 = new Feature(controlModel, bd1.getName());
		IFeature cmsv1 = new Feature(controlModel, sv1.getValue());
		FeatureUtils.addChild(vr, cmed1);
		FeatureUtils.addChild(vr, cmbd1);

		FeatureUtils.addChild(cmed1, cmsv1);

		FeatureUtils.addFeature(controlModel, vr);
		FeatureUtils.addFeature(controlModel, cmbd1);
		FeatureUtils.addFeature(controlModel, cmed1);
		FeatureUtils.addFeature(controlModel, cmsv1);

		FeatureUtils.setMandatory(cmed1, true);
		DefaultFeatureModelFactory factory = new DefaultFeatureModelFactory();
		IConstraint constr = factory.createConstraint(controlModel, Prop4JUtils.createImplies(
				Prop4JUtils.createLiteral(cmbd1), Prop4JUtils.createNot(Prop4JUtils.createLiteral(cmsv1))));
		FeatureUtils.addConstraint(controlModel, constr);

		DecisionModeltoFeatureModelTransformer conv = new DecisionModeltoFeatureModelTransformer();
		IFeatureModel fm = conv.transform(dm);

		assertTrue(areFMEqual(controlModel, fm));
	}

	// if BooleanDecision bd1 is true, set also bd2 to true
	@Test
	public void testTransformBooleanImpliesOtherBooleanRule() throws NotSupportedVariablityTypeException {
		BooleanDecision bd1 = new BooleanDecision("bd1");
		BooleanDecision bd2 = new BooleanDecision("bd2");

		Rule r = new Rule(new IsTakenFunction(bd1), new SetValueAction(bd2, BooleanValue.getTrue()));
		bd1.addRule(r);
		dm.add(bd1);

		IFeatureModel controlModel = new FeatureModel("TestModel");
		IFeature vr = new Feature(controlModel, "VIRTUAL_ROOT");
		FeatureUtils.setRoot(controlModel, vr);
		FeatureUtils.setOr(vr);
		IFeature cmbd1 = new Feature(controlModel, bd1.getName());
		IFeature cmbd2 = new Feature(controlModel, bd2.getName());
		FeatureUtils.addChild(vr, cmbd1);
		FeatureUtils.addChild(vr, cmbd2);

		FeatureUtils.addFeature(controlModel, vr);
		FeatureUtils.addFeature(controlModel, cmbd1);
		FeatureUtils.addFeature(controlModel, cmbd2);
		DefaultFeatureModelFactory factory = new DefaultFeatureModelFactory();
		IConstraint constr = factory.createConstraint(controlModel,
				Prop4JUtils.createImplies(Prop4JUtils.createLiteral(cmbd1), Prop4JUtils.createLiteral(cmbd2)));
		FeatureUtils.addConstraint(controlModel, constr);

		DecisionModeltoFeatureModelTransformer conv = new DecisionModeltoFeatureModelTransformer();
		IFeatureModel fm = conv.transform(dm);

		assertTrue(areFMEqual(controlModel, fm));
	}

	// if BooleanDecision bd1 is true, set also bd2 to False
	@Test
	public void testTransformBooleanImpliesNotOtherBooleanRule() throws NotSupportedVariablityTypeException {
		BooleanDecision bd1 = new BooleanDecision("bd1");
		BooleanDecision bd2 = new BooleanDecision("bd2");

		Rule r = new Rule(new IsTakenFunction(bd1), new SetValueAction(bd2, BooleanValue.getFalse()));
		bd1.addRule(r);
		dm.add(bd1);
		dm.add(bd2);

		IFeatureModel controlModel = new FeatureModel("TestModel");
		IFeature vr = new Feature(controlModel, "VIRTUAL_ROOT");
		FeatureUtils.setRoot(controlModel, vr);
		FeatureUtils.setOr(vr);
		IFeature cmbd1 = new Feature(controlModel, bd1.getName());
		IFeature cmbd2 = new Feature(controlModel, bd2.getName());
		FeatureUtils.addChild(vr, cmbd1);
		FeatureUtils.addChild(vr, cmbd2);

		FeatureUtils.addFeature(controlModel, vr);
		FeatureUtils.addFeature(controlModel, cmbd1);
		FeatureUtils.addFeature(controlModel, cmbd2);
		DefaultFeatureModelFactory factory = new DefaultFeatureModelFactory();
		IConstraint constr = factory.createConstraint(controlModel, Prop4JUtils.createImplies(
				Prop4JUtils.createLiteral(cmbd1), Prop4JUtils.createNot(Prop4JUtils.createLiteral(cmbd2))));
		FeatureUtils.addConstraint(controlModel, constr);

		DecisionModeltoFeatureModelTransformer conv = new DecisionModeltoFeatureModelTransformer();
		IFeatureModel fm = conv.transform(dm);

		assertTrue(areFMEqual(controlModel, fm));
	}

	// if BooleanDecision bd1 is set to true, set EnumDecision ed1 to NoneValue
	@Test
	public void testTransformRulesBooleanConditionSetEnumNone() throws NotSupportedVariablityTypeException {
		EnumDecision ed1 = new EnumDecision("ed1");
		StringValue sv1 = new StringValue("sv1");
		StringValue sv2 = (StringValue) ed1.getNoneOption();
		BooleanDecision bd1 = new BooleanDecision("bd1");
		ed1.getRange().add(sv1);
		ed1.getRange().add(sv2);
		Rule r = new Rule(new IsTakenFunction(bd1), new SetValueAction(ed1, sv2));
		bd1.addRule(r);
		dm.add(ed1);
		dm.add(bd1);

		IFeatureModel controlModel = new FeatureModel("TestModel");
		IFeature vr = new Feature(controlModel, "VIRTUAL_ROOT");
		FeatureUtils.setRoot(controlModel, vr);
		FeatureUtils.setOr(vr);
		IFeature cmed1 = new Feature(controlModel, ed1.getName());
		IFeature cmbd1 = new Feature(controlModel, bd1.getName());
		IFeature cmsv1 = new Feature(controlModel, sv1.getValue());
		IFeature cmsv2 = new Feature(controlModel, sv2.getValue());
		FeatureUtils.addChild(vr, cmed1);
		FeatureUtils.addChild(vr, cmbd1);

		FeatureUtils.addChild(cmed1, cmsv1);
		FeatureUtils.addChild(cmed1, cmsv2);

		FeatureUtils.addFeature(controlModel, vr);
		FeatureUtils.addFeature(controlModel, cmbd1);
		FeatureUtils.addFeature(controlModel, cmed1);
		FeatureUtils.addFeature(controlModel, cmsv1);
		FeatureUtils.addFeature(controlModel, cmsv2);
		DefaultFeatureModelFactory factory = new DefaultFeatureModelFactory();
		IConstraint constr = factory.createConstraint(controlModel, Prop4JUtils.createImplies(
				Prop4JUtils.createLiteral(cmbd1), Prop4JUtils.createNot(Prop4JUtils.createLiteral(cmed1))));
		FeatureUtils.addConstraint(controlModel, constr);

		DecisionModeltoFeatureModelTransformer conv = new DecisionModeltoFeatureModelTransformer();
		IFeatureModel fm = conv.transform(dm);

		assertTrue(areFMEqual(controlModel, fm));
	}

	// if EnumDecision ed1 is not None, select BooleanDecision bd1
	// controlModel = d_ed1 => d_bd1
	// transformed = bd1 => bd1
	@Test
	public void testTransformRulesEnumNotNoneSelectBool() throws NotSupportedVariablityTypeException {
		EnumDecision ed1 = new EnumDecision("ed1");
		StringValue sv1 = new StringValue("sv1");
		StringValue sv2 = (StringValue) ed1.getNoneOption();
		BooleanDecision bd1 = new BooleanDecision("bd1");
		ed1.getRange().add(sv1);
		ed1.getRange().add(sv2);
		DecisionValueCondition dvc1 = new DecisionValueCondition(ed1, sv2);
		Not n = new Not(dvc1);
		Rule r = new Rule(n, new SelectDecisionAction(bd1));
		ed1.addRule(r);
		dm.add(ed1);
		dm.add(bd1);

		IFeatureModel controlModel = new FeatureModel("TestModel");
		IFeature vr = new Feature(controlModel, "VIRTUAL_ROOT");
		FeatureUtils.setRoot(controlModel, vr);
		FeatureUtils.setOr(vr);
		IFeature cmed1 = new Feature(controlModel, ed1.getName());
		IFeature cmbd1 = new Feature(controlModel, bd1.getName());
		IFeature cmsv1 = new Feature(controlModel, sv1.getValue());
		IFeature cmsv2 = new Feature(controlModel, sv2.getValue());
		FeatureUtils.addChild(vr, cmed1);
		FeatureUtils.addChild(vr, cmbd1);

		FeatureUtils.addChild(cmed1, cmsv1);
		FeatureUtils.addChild(cmed1, cmsv2);

		FeatureUtils.addFeature(controlModel, vr);
		FeatureUtils.addFeature(controlModel, cmbd1);
		FeatureUtils.addFeature(controlModel, cmed1);
		FeatureUtils.addFeature(controlModel, cmsv1);
		FeatureUtils.addFeature(controlModel, cmsv2);
		DefaultFeatureModelFactory factory = new DefaultFeatureModelFactory();
		IConstraint constr = factory.createConstraint(controlModel,
				Prop4JUtils.createImplies(Prop4JUtils.createLiteral(cmed1), Prop4JUtils.createLiteral(cmbd1)));
		FeatureUtils.addConstraint(controlModel, constr);

		DecisionModeltoFeatureModelTransformer conv = new DecisionModeltoFeatureModelTransformer();
		IFeatureModel fm = conv.transform(dm);

		assertTrue(areFMEqual(controlModel, fm));
	}

	// If EnumDecision ed1 value is sv1, then set EnumDecision ed2 to NoneOption
	// ControlModel produces: propNode=sv1 => -d_ed2
	// transformation produces: propNode=sv1 => ed2
	@Test
	public void testTransformRulesEnumValueSetsEnumDecNone() throws NotSupportedVariablityTypeException {
		EnumDecision ed1 = new EnumDecision("ed1");
		EnumDecision ed2 = new EnumDecision("ed2");
		StringValue sv1 = new StringValue("sv1");
		StringValue sv2 = new StringValue("sv2");
		StringValue sv3 = (StringValue) ed2.getNoneOption();
		ed1.getRange().add(sv1);
		ed2.getRange().add(sv2);
		ed2.getRange().add(sv3);

		DecisionValueCondition dvc1 = new DecisionValueCondition(ed1, sv1);

		Rule r = new Rule(dvc1, new SetValueAction(ed2, sv3));
		ed1.addRule(r);
		dm.add(ed1);
		dm.add(ed2);

		IFeatureModel controlModel = new FeatureModel("TestModel");
		IFeature vr = new Feature(controlModel, "VIRTUAL_ROOT");
		FeatureUtils.setRoot(controlModel, vr);
		FeatureUtils.setOr(vr);
		IFeature cmed1 = new Feature(controlModel, ed1.getName());
		IFeature cmed2 = new Feature(controlModel, ed2.getName());
		IFeature cmsv1 = new Feature(controlModel, sv1.getValue());
		IFeature cmsv2 = new Feature(controlModel, sv2.getValue());
		IFeature cmsv3 = new Feature(controlModel, sv3.getValue());
		FeatureUtils.addChild(vr, cmed1);
		FeatureUtils.addChild(vr, cmed2);

		FeatureUtils.addChild(cmed1, cmsv1);
		FeatureUtils.addChild(cmed2, cmsv2);
		FeatureUtils.addChild(cmed2, cmsv3);

		FeatureUtils.addFeature(controlModel, vr);
		FeatureUtils.addFeature(controlModel, cmed1);
		FeatureUtils.addFeature(controlModel, cmed2);
		FeatureUtils.addFeature(controlModel, cmsv1);
		FeatureUtils.addFeature(controlModel, cmsv2);
		FeatureUtils.addFeature(controlModel, cmsv3);
		DefaultFeatureModelFactory factory = new DefaultFeatureModelFactory();
		IConstraint constr = factory.createConstraint(controlModel, Prop4JUtils.createImplies(
				Prop4JUtils.createLiteral(cmsv1), Prop4JUtils.createNot(Prop4JUtils.createLiteral(cmed2))));
		FeatureUtils.addConstraint(controlModel, constr);

		DecisionModeltoFeatureModelTransformer conv = new DecisionModeltoFeatureModelTransformer();
		IFeatureModel fm = conv.transform(dm);

		assertTrue(areFMEqual(controlModel, fm));
	}

	// If EnumDecision ed1 value is not sv1, then select BooleanDecision bd2
	// controlModel= -d_bd1 => d_bd2
	// transformed= bd1 | bd2
	// @Ignore
	@Test
	public void testTransformRulesComplexConditionSelectDecisionAction() throws NotSupportedVariablityTypeException {
		EnumDecision ed1 = new EnumDecision("ed1");
		BooleanDecision bd1 = new BooleanDecision("bd1");
		BooleanDecision bd2 = new BooleanDecision("bd2");
		StringValue sv1 = new StringValue("sv1");
		ed1.getRange().add(sv1);
		Not n = new Not(new IsTakenFunction(bd1));
		Rule r = new Rule(n, new SelectDecisionAction(bd2));
		ed1.addRule(r);
		dm.add(ed1);
		dm.add(bd1);
		dm.add(bd2);

		IFeatureModel controlModel = new FeatureModel("TestModel");
		IFeature vr = new Feature(controlModel, "VIRTUAL_ROOT");
		FeatureUtils.setRoot(controlModel, vr);
		FeatureUtils.setOr(vr);
		IFeature cmed1 = new Feature(controlModel, ed1.getName());
		IFeature cmbd1 = new Feature(controlModel, bd1.getName());
		IFeature cmbd2 = new Feature(controlModel, bd2.getName());
		IFeature cmsv1 = new Feature(controlModel, sv1.getValue());
		FeatureUtils.addChild(vr, cmed1);
		FeatureUtils.addChild(vr, cmbd1);
		FeatureUtils.addChild(vr, cmbd2);

		FeatureUtils.addChild(cmed1, cmsv1);

		FeatureUtils.addFeature(controlModel, vr);

		FeatureUtils.addFeature(controlModel, cmbd1);
		FeatureUtils.addFeature(controlModel, cmbd2);
		FeatureUtils.addFeature(controlModel, cmed1);
		FeatureUtils.addFeature(controlModel, cmsv1);
		DefaultFeatureModelFactory factory = new DefaultFeatureModelFactory();
		IConstraint constr = factory.createConstraint(controlModel, Prop4JUtils.createImplies(
				Prop4JUtils.createNot(Prop4JUtils.createLiteral(cmbd1)), Prop4JUtils.createLiteral(cmbd2)));
		FeatureUtils.addConstraint(controlModel, constr);

		DecisionModeltoFeatureModelTransformer conv = new DecisionModeltoFeatureModelTransformer();
		IFeatureModel fm = conv.transform(dm);

		assertTrue(areFMEqual(controlModel, fm));
	}

	// If EnumDecision ed1 value is not sv1, then set EnumDecision ed2 to NoneOption
	// controlModel: propNode=sv1 & -bd1 => bd2
	// transformed: propNode=null | ed1 => null
	// @Ignore
	@Test
	public void testTransformRulesComplexConditionWithAndNodeAndNotNodeSelectDecisionAction()
			throws NotSupportedVariablityTypeException {
		EnumDecision ed1 = new EnumDecision("ed1");
		BooleanDecision bd1 = new BooleanDecision("bd1");
		BooleanDecision bd2 = new BooleanDecision("bd2");
		StringValue sv1 = new StringValue("sv1");
		ed1.getRange().add(sv1);
		DecisionValueCondition dvc = new DecisionValueCondition(ed1, sv1);
		Not n = new Not(new IsTakenFunction(bd1));
		And a = new And(dvc, n);
		Rule r = new Rule(a, new SelectDecisionAction(bd2));
		ed1.addRule(r);
		dm.add(ed1);
		dm.add(bd2);
		dm.add(bd1);

		IFeatureModel controlModel = new FeatureModel("TestModel");
		IFeature vr = new Feature(controlModel, "VIRTUAL_ROOT");
		FeatureUtils.setRoot(controlModel, vr);
		FeatureUtils.setOr(vr);
		IFeature cmed1 = new Feature(controlModel, ed1.getName());
		IFeature cmbd1 = new Feature(controlModel, bd1.getName());
		IFeature cmbd2 = new Feature(controlModel, bd2.getName());
		IFeature cmsv1 = new Feature(controlModel, sv1.getValue());
		FeatureUtils.addChild(vr, cmed1);
		FeatureUtils.addChild(vr, cmbd1);
		FeatureUtils.addChild(vr, cmbd2);

		FeatureUtils.addChild(cmed1, cmsv1);

		FeatureUtils.addFeature(controlModel, vr);

		FeatureUtils.addFeature(controlModel, cmbd1);
		FeatureUtils.addFeature(controlModel, cmbd2);
		FeatureUtils.addFeature(controlModel, cmed1);
		FeatureUtils.addFeature(controlModel, cmsv1);
		DefaultFeatureModelFactory factory = new DefaultFeatureModelFactory();
		// FIXME still needs to be changed
		IConstraint constr = factory.createConstraint(controlModel,
				Prop4JUtils.createImplies(
						Prop4JUtils.createAnd(Prop4JUtils.createLiteral(cmsv1),
								Prop4JUtils.createNot(Prop4JUtils.createLiteral(cmbd1))),
						Prop4JUtils.createLiteral(cmbd2)));
		FeatureUtils.addConstraint(controlModel, constr);

		DecisionModeltoFeatureModelTransformer conv = new DecisionModeltoFeatureModelTransformer();
		IFeatureModel fm = conv.transform(dm);

		assertTrue(areFMEqual(controlModel, fm));
	}

	// If EnumDecision ed1 value is not sv1, then set EnumDecision ed2 to
	// NoneOption+
	// controlModel = propNode=sv1 & bd1 => bd2
	// transformed model = propNode=ed1 & null => null
	// @Ignore
	@Test
	public void testTransformRulesComplexConditionWithAndNodeSelectDecisionAction()
			throws NotSupportedVariablityTypeException {
		EnumDecision ed1 = new EnumDecision("ed1");
		BooleanDecision bd1 = new BooleanDecision("bd1");
		BooleanDecision bd2 = new BooleanDecision("bd2");
		StringValue sv1 = new StringValue("sv1");
		ed1.getRange().add(sv1);
		DecisionValueCondition dvc = new DecisionValueCondition(ed1, sv1);
		And a = new And(dvc, new IsTakenFunction(bd1));
		Rule r = new Rule(a, new SelectDecisionAction(bd2));
		ed1.addRule(r);
		dm.add(ed1);

		IFeatureModel controlModel = new FeatureModel("TestModel");
		IFeature vr = new Feature(controlModel, "VIRTUAL_ROOT");
		FeatureUtils.setRoot(controlModel, vr);
		FeatureUtils.setOr(vr);
		IFeature cmed1 = new Feature(controlModel, ed1.getName());
		IFeature cmbd1 = new Feature(controlModel, bd1.getName());
		IFeature cmbd2 = new Feature(controlModel, bd2.getName());
		IFeature cmsv1 = new Feature(controlModel, sv1.getValue());
		FeatureUtils.addChild(vr, cmed1);
		FeatureUtils.addChild(vr, cmbd1);
		FeatureUtils.addChild(vr, cmbd2);

		FeatureUtils.addChild(cmed1, cmsv1);

		FeatureUtils.addFeature(controlModel, vr);

		FeatureUtils.addFeature(controlModel, cmbd1);
		FeatureUtils.addFeature(controlModel, cmbd2);
		FeatureUtils.addFeature(controlModel, cmed1);
		FeatureUtils.addFeature(controlModel, cmsv1);
		DefaultFeatureModelFactory factory = new DefaultFeatureModelFactory();
		// FIXME still needs to be changed
		IConstraint constr = factory.createConstraint(controlModel,
				Prop4JUtils.createImplies(
						Prop4JUtils.createAnd(Prop4JUtils.createLiteral(cmsv1), Prop4JUtils.createLiteral(cmbd1)),
						Prop4JUtils.createLiteral(cmbd2)));
		FeatureUtils.addConstraint(controlModel, constr);

		DecisionModeltoFeatureModelTransformer conv = new DecisionModeltoFeatureModelTransformer();
		IFeatureModel fm = conv.transform(dm);

		assertTrue(areFMEqual(controlModel, fm));
	}

	private boolean areFMEqual(final IFeatureModel fm1, final IFeatureModel fm2) {
		if (!areConstraintListsEqual(fm1.getConstraints(), fm2.getConstraints())) {
			return false;
		}
		if (!fm1.isFeatureOrderUserDefined() == fm2.isFeatureOrderUserDefined()) {
			return false;
		}
		if (!areFeatureTablesEqual(fm1.getFeatureTable(), fm2.getFeatureTable())) {
			return false;
		}
		if (!fm1.getProperty().equals(fm2.getProperty())) {
			return false;
		}
		if (!FeatureUtils.getConstraints(fm1).equals(FeatureUtils.getConstraints(fm2))) {
			return false;
		}
		return true;
	}

	private boolean areConstraintListsEqual(final List<IConstraint> cl1, final List<IConstraint> cl2) {
		if (cl1 == cl2) {
			return true;
		}
		if (cl1 == null || cl2 == null) {
			return false;
		}
		if (cl1.size() != cl2.size()) {
			return false;
		}
		for (IConstraint c1 : cl1) {
			if (cl1 == null) {
				continue;
			}
			IConstraint c2 = cl2.stream().filter(c -> c1.getNode().equals(c.getNode())).findFirst().get();
			if (c2 == null || !areConstraintsEqual(c1, c2)) {
				return false;
			}
		}

		return true;
	}

	private boolean areConstraintsEqual(final IConstraint c1, final IConstraint c2) {
		if (c1 == c2) {
			return true;
		}
		if (!c1.getNode().equals(c2.getNode())) {
			return false;
		}
		if (!c1.getCustomProperties().equals(c2.getCustomProperties())) {
			return false;
		}
		if (!c1.getContainedFeatures().equals(c2.getContainedFeatures())) {
			return false;
		}
		return true;
	}

	private boolean areFeatureTablesEqual(final Map<String, IFeature> m1, final Map<String, IFeature> m2) {
		if (m1.keySet().size() != m2.keySet().size()) {
			return false;
		}
		if (!m1.keySet().containsAll(m2.keySet())) {
			return false;
		}
		for (String k : m1.keySet()) {
			if (!areIFeaturesEqual(m1.get(k), m2.get(k))) {
				return false;
			}
		}
		return true;
	}

	private boolean areIFeaturesEqual(final IFeature f1, final IFeature f2) {
		if (!f1.getName().equals(f2.getName())) {
			return false;
		}
		if (FeatureUtils.isOr(f1) != FeatureUtils.isOr(f2)) {
			return false;
		}
		if (FeatureUtils.isAlternative(f1) != FeatureUtils.isAlternative(f2)) {
			return false;
		}
		if (!f1.getCustomProperties().equals(f2.getCustomProperties())) {
			return false;
		}
//		if(!f1.getProperty().equals(f2.getProperty()))return false;
		return true;
	}

}
