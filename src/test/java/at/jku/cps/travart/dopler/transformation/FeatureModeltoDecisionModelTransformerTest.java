package at.jku.cps.travart.dopler.transformation;

import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import at.jku.cps.travart.core.common.IConfigurable;
import at.jku.cps.travart.core.common.ISampler;
import at.jku.cps.travart.core.exception.NotSupportedVariabilityTypeException;
import at.jku.cps.travart.core.sampling.FeatureModelSampler;
import at.jku.cps.travart.dopler.decision.IDecisionModel;
import at.jku.cps.travart.dopler.decision.factory.impl.DecisionModelFactory;
import at.jku.cps.travart.dopler.decision.impl.DecisionModel;
import at.jku.cps.travart.dopler.sampling.DecisionModelSampler;
import de.ovgu.featureide.fm.core.base.FeatureUtils;
import de.ovgu.featureide.fm.core.base.IConstraint;
import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.base.impl.DefaultFeatureModelFactory;
import de.ovgu.featureide.fm.core.base.impl.Feature;
import de.ovgu.featureide.fm.core.base.impl.FeatureModel;
import de.ovgu.featureide.fm.core.init.FMCoreLibrary;
import de.ovgu.featureide.fm.core.init.LibraryManager;

public class FeatureModeltoDecisionModelTransformerTest {
	private FeatureModel fm;
	private DecisionModel controlModel;

	static {
		LibraryManager.registerLibrary(FMCoreLibrary.getInstance());
	}

	@Before
	public void setUp() throws Exception {
		fm = new FeatureModel("TestModel");
		DecisionModelFactory factory = DecisionModelFactory.getInstance();
		controlModel = factory.create();
		controlModel.setName("TestModel");
	}

	@Test
	public void testTransformOrDecision() throws NotSupportedVariabilityTypeException {
//		EnumDecision ed1 = new EnumDecision("ed1");
//		StringValue sv1 = new StringValue("sv1");
//		StringValue sv2 = new StringValue("sv2");
//		StringValue sv3 = new StringValue("sv3");
//		ed1.getRange().add(sv1);
//		ed1.getRange().add(sv2);
//		ed1.getRange().add(sv3);
//		ed1.setCardinality(new Cardinality(1, 3));
//		controlModel.add(ed1);

		IFeature cmed1 = new Feature(fm, "ed1");
		FeatureUtils.setRoot(fm, cmed1);
		FeatureUtils.setOr(cmed1);

		IFeature cmsv1 = new Feature(fm, "sv1");
		IFeature cmsv2 = new Feature(fm, "sv2");
		IFeature cmsv3 = new Feature(fm, "sv3");

		FeatureUtils.addFeature(fm, cmed1);
		FeatureUtils.addFeature(fm, cmsv1);
		FeatureUtils.addFeature(fm, cmsv2);
		FeatureUtils.addFeature(fm, cmsv3);

		FeatureUtils.addChild(cmed1, cmsv1);
		FeatureUtils.addChild(cmed1, cmsv2);
		FeatureUtils.addChild(cmed1, cmsv3);

		ISampler<IDecisionModel> dsamp = new DecisionModelSampler();
		ISampler<IFeatureModel> fsamp = new FeatureModelSampler();
		FeatureModeltoDecisionModelTransformer conv = new FeatureModeltoDecisionModelTransformer();

		IDecisionModel dm = conv.transform(fm);
		Set<Map<IConfigurable, Boolean>> dSamples = dsamp.sampleValidConfigurations(dm);
		Set<Map<IConfigurable, Boolean>> fSamples = fsamp.sampleValidConfigurations(fm);

		assertTrue(equalValidSamples(dm, dSamples, fm, fSamples));
	}

	@Test
	public void testTransformAlternativeDecision() throws NotSupportedVariabilityTypeException {
		IFeature cmed1 = new Feature(fm, "ed1");
		FeatureUtils.setRoot(fm, cmed1);
		FeatureUtils.setAlternative(cmed1);

		IFeature cmsv1 = new Feature(fm, "sv1");
		IFeature cmsv2 = new Feature(fm, "sv2");
		IFeature cmsv3 = new Feature(fm, "sv3");

		FeatureUtils.addFeature(fm, cmed1);
		FeatureUtils.addFeature(fm, cmsv1);
		FeatureUtils.addFeature(fm, cmsv2);
		FeatureUtils.addFeature(fm, cmsv3);

		FeatureUtils.addChild(cmed1, cmsv1);
		FeatureUtils.addChild(cmed1, cmsv2);
		FeatureUtils.addChild(cmed1, cmsv3);

		ISampler<IDecisionModel> dsamp = new DecisionModelSampler();
		ISampler<IFeatureModel> fsamp = new FeatureModelSampler();
		FeatureModeltoDecisionModelTransformer conv = new FeatureModeltoDecisionModelTransformer();

		IDecisionModel dm = conv.transform(fm);
		Set<Map<IConfigurable, Boolean>> dSamples = dsamp.sampleValidConfigurations(dm);
		Set<Map<IConfigurable, Boolean>> fSamples = fsamp.sampleValidConfigurations(fm);

		assertTrue(equalValidSamples(dm, dSamples, fm, fSamples));
	}

	@Test
	public void testTransformMandatoryDecision() throws NotSupportedVariabilityTypeException {
		IFeature cmed1 = new Feature(fm, "ed1");
		FeatureUtils.setRoot(fm, cmed1);

		IFeature cmsv1 = new Feature(fm, "sv1");

		FeatureUtils.addFeature(fm, cmed1);
		FeatureUtils.addFeature(fm, cmsv1);

		FeatureUtils.addChild(cmed1, cmsv1);
		FeatureUtils.setMandatory(cmsv1, true);

		ISampler<IDecisionModel> dsamp = new DecisionModelSampler();
		ISampler<IFeatureModel> fsamp = new FeatureModelSampler();
		FeatureModeltoDecisionModelTransformer conv = new FeatureModeltoDecisionModelTransformer();

		IDecisionModel dm = conv.transform(fm);
		Set<Map<IConfigurable, Boolean>> dSamples = dsamp.sampleValidConfigurations(dm);
		Set<Map<IConfigurable, Boolean>> fSamples = fsamp.sampleValidConfigurations(fm);

		assertTrue(equalValidSamples(dm, dSamples, fm, fSamples));
	}

	@Test
	public void testTransformNumberDecision() throws NotSupportedVariabilityTypeException {
		IFeature cmnd1 = new Feature(fm, "nd1");
		FeatureUtils.setRoot(fm, cmnd1);

		IFeature cmdv1 = new Feature(fm, "dv1");

		FeatureUtils.addFeature(fm, cmnd1);
		FeatureUtils.addFeature(fm, cmdv1);

		FeatureUtils.addChild(cmnd1, cmdv1);

		ISampler<IDecisionModel> dsamp = new DecisionModelSampler();
		ISampler<IFeatureModel> fsamp = new FeatureModelSampler();
		FeatureModeltoDecisionModelTransformer conv = new FeatureModeltoDecisionModelTransformer();

		IDecisionModel dm = conv.transform(fm);
		Set<Map<IConfigurable, Boolean>> dSamples = dsamp.sampleValidConfigurations(dm);
		Set<Map<IConfigurable, Boolean>> fSamples = fsamp.sampleValidConfigurations(fm);

		assertTrue(equalValidSamples(dm, dSamples, fm, fSamples));
	}

	@Test
	public void testTransformNumberDecisionWithVirtualRoot() throws NotSupportedVariabilityTypeException {
		IFeature cmvr = new Feature(fm, "VIRTUAL_ROOT");
		IFeature cmnd1 = new Feature(fm, "nd1");
		FeatureUtils.setRoot(fm, cmvr);

		IFeature cmdv1 = new Feature(fm, "dv1");

		FeatureUtils.addFeature(fm, cmvr);
		FeatureUtils.addFeature(fm, cmnd1);
		FeatureUtils.addFeature(fm, cmdv1);

		FeatureUtils.addChild(cmvr, cmnd1);
		FeatureUtils.addChild(cmnd1, cmdv1);

		ISampler<IDecisionModel> dsamp = new DecisionModelSampler();
		ISampler<IFeatureModel> fsamp = new FeatureModelSampler();
		FeatureModeltoDecisionModelTransformer conv = new FeatureModeltoDecisionModelTransformer();

		IDecisionModel dm = conv.transform(fm);
		Set<Map<IConfigurable, Boolean>> dSamples = dsamp.sampleValidConfigurations(dm);
		Set<Map<IConfigurable, Boolean>> fSamples = fsamp.sampleValidConfigurations(fm);

		assertTrue(equalValidSamples(dm, dSamples, fm, fSamples));
	}

	@Test
	public void testTransformNumberDecisionWithVirtualRootAndConstraint() throws NotSupportedVariabilityTypeException {
		IFeature cmvr = new Feature(fm, "VIRTUAL_ROOT");
		IFeature cmnd1 = new Feature(fm, "nd1");
		IFeature cmsd1 = new Feature(fm, "sd1");
		FeatureUtils.setRoot(fm, cmvr);
		FeatureUtils.setAlternative(cmnd1);

		IFeature cmdv1 = new Feature(fm, "dv1");
		IFeature cmdv2 = new Feature(fm, "dv2");

		FeatureUtils.addFeature(fm, cmvr);
		FeatureUtils.addFeature(fm, cmnd1);
		FeatureUtils.addFeature(fm, cmsd1);
		FeatureUtils.addFeature(fm, cmdv1);
		FeatureUtils.addFeature(fm, cmdv2);

		FeatureUtils.addChild(cmvr, cmnd1);
		FeatureUtils.addChild(cmnd1, cmdv1);
		FeatureUtils.addChild(cmnd1, cmdv2);

		DefaultFeatureModelFactory factory = new DefaultFeatureModelFactory();
		IConstraint constr = factory.createConstraint(fm,
				Prop4JUtils.createImplies(Prop4JUtils.createLiteral(cmdv1), Prop4JUtils.createLiteral(cmsd1)));
		FeatureUtils.addConstraint(fm, constr);

		ISampler<IDecisionModel> dsamp = new DecisionModelSampler();
		ISampler<IFeatureModel> fsamp = new FeatureModelSampler();
		FeatureModeltoDecisionModelTransformer conv = new FeatureModeltoDecisionModelTransformer();

		IDecisionModel dm = conv.transform(fm);
		Set<Map<IConfigurable, Boolean>> dSamples = dsamp.sampleValidConfigurations(dm);
		Set<Map<IConfigurable, Boolean>> fSamples = fsamp.sampleValidConfigurations(fm);

		assertTrue(equalValidSamples(dm, dSamples, fm, fSamples));
	}

	@Test
	public void testTransformConstraintNumberDecisionImpliesBoolean() throws NotSupportedVariabilityTypeException {
		IFeature cmvr = new Feature(fm, "VIRTUAL_ROOT");
		IFeature cmnd1 = new Feature(fm, "nd1");
		IFeature cmbd1 = new Feature(fm, "bd1");
		FeatureUtils.setRoot(fm, cmvr);
		FeatureUtils.setAlternative(cmnd1);

		IFeature cmdv1 = new Feature(fm, "dv1");
		IFeature cmdv2 = new Feature(fm, "dv2");

		FeatureUtils.addFeature(fm, cmvr);
		FeatureUtils.addFeature(fm, cmnd1);
		FeatureUtils.addFeature(fm, cmbd1);
		FeatureUtils.addFeature(fm, cmdv1);
		FeatureUtils.addFeature(fm, cmdv2);

		FeatureUtils.addChild(cmvr, cmnd1);
		FeatureUtils.addChild(cmvr, cmbd1);
		FeatureUtils.addChild(cmnd1, cmdv1);
		FeatureUtils.addChild(cmnd1, cmdv2);

		DefaultFeatureModelFactory factory = new DefaultFeatureModelFactory();
		IConstraint constr = factory.createConstraint(fm,
				Prop4JUtils.createImplies(Prop4JUtils.createLiteral(cmdv1), Prop4JUtils.createLiteral(cmbd1)));
		FeatureUtils.addConstraint(fm, constr);

		ISampler<IDecisionModel> dsamp = new DecisionModelSampler();
		ISampler<IFeatureModel> fsamp = new FeatureModelSampler();
		FeatureModeltoDecisionModelTransformer conv = new FeatureModeltoDecisionModelTransformer();

		IDecisionModel dm = conv.transform(fm);
		Set<Map<IConfigurable, Boolean>> dSamples = dsamp.sampleValidConfigurations(dm);
		Set<Map<IConfigurable, Boolean>> fSamples = fsamp.sampleValidConfigurations(fm);

		assertTrue(equalValidSamples(dm, dSamples, fm, fSamples));
	}

	@Test
	public void testTransformConstraintNumberDecisionExcludesBoolean() throws NotSupportedVariabilityTypeException {
		IFeature cmvr = new Feature(fm, "VIRTUAL_ROOT");
		IFeature cmnd1 = new Feature(fm, "nd1");
		IFeature cmbd1 = new Feature(fm, "bd1");
		FeatureUtils.setRoot(fm, cmvr);
		FeatureUtils.setAlternative(cmnd1);

		IFeature cmdv1 = new Feature(fm, "dv1");
		IFeature cmdv2 = new Feature(fm, "dv2");

		FeatureUtils.addFeature(fm, cmvr);
		FeatureUtils.addFeature(fm, cmnd1);
		FeatureUtils.addFeature(fm, cmbd1);
		FeatureUtils.addFeature(fm, cmdv1);
		FeatureUtils.addFeature(fm, cmdv2);

		FeatureUtils.addChild(cmvr, cmnd1);
		FeatureUtils.addChild(cmvr, cmbd1);
		FeatureUtils.addChild(cmnd1, cmdv1);
		FeatureUtils.addChild(cmnd1, cmdv2);

		DefaultFeatureModelFactory factory = new DefaultFeatureModelFactory();
		IConstraint constr = factory.createConstraint(fm, Prop4JUtils.createImplies(Prop4JUtils.createLiteral(cmdv1),
				Prop4JUtils.createNot(Prop4JUtils.createLiteral(cmbd1))));
		FeatureUtils.addConstraint(fm, constr);

		ISampler<IDecisionModel> dsamp = new DecisionModelSampler();
		ISampler<IFeatureModel> fsamp = new FeatureModelSampler();
		FeatureModeltoDecisionModelTransformer conv = new FeatureModeltoDecisionModelTransformer();

		IDecisionModel dm = conv.transform(fm);
		Set<Map<IConfigurable, Boolean>> dSamples = dsamp.sampleValidConfigurations(dm);
		Set<Map<IConfigurable, Boolean>> fSamples = fsamp.sampleValidConfigurations(fm);

		assertTrue(equalValidSamples(dm, dSamples, fm, fSamples));
	}

	@Test
	public void testTransformConstraintEnumDecisionExcludesEnumdecision() throws NotSupportedVariabilityTypeException {
		IFeature cmvr = new Feature(fm, "VIRTUAL_ROOT");
		IFeature cmed1 = new Feature(fm, "ed1");
		IFeature cmed2 = new Feature(fm, "ed2");
		FeatureUtils.setRoot(fm, cmvr);
		FeatureUtils.setOr(cmed1);
		FeatureUtils.setOr(cmed2);

		IFeature cmsv1 = new Feature(fm, "sv1");
		IFeature cmsv2 = new Feature(fm, "sv2");
		IFeature cmsv3 = new Feature(fm, "sv3");
		IFeature cmsv4 = new Feature(fm, "sv4");

		FeatureUtils.addFeature(fm, cmvr);
		FeatureUtils.addFeature(fm, cmed1);
		FeatureUtils.addFeature(fm, cmed2);
		FeatureUtils.addFeature(fm, cmsv1);
		FeatureUtils.addFeature(fm, cmsv2);
		FeatureUtils.addFeature(fm, cmsv3);
		FeatureUtils.addFeature(fm, cmsv4);

		FeatureUtils.addChild(cmvr, cmed1);
		FeatureUtils.addChild(cmvr, cmed2);
		FeatureUtils.addChild(cmed1, cmsv1);
		FeatureUtils.addChild(cmed1, cmsv2);
		FeatureUtils.addChild(cmed2, cmsv3);
		FeatureUtils.addChild(cmed2, cmsv4);

		DefaultFeatureModelFactory factory = new DefaultFeatureModelFactory();
		IConstraint constr = factory.createConstraint(fm, Prop4JUtils.createImplies(Prop4JUtils.createLiteral(cmsv1),
				Prop4JUtils.createNot(Prop4JUtils.createLiteral(cmsv4))));
		FeatureUtils.addConstraint(fm, constr);

		ISampler<IDecisionModel> dsamp = new DecisionModelSampler();
		ISampler<IFeatureModel> fsamp = new FeatureModelSampler();
		FeatureModeltoDecisionModelTransformer conv = new FeatureModeltoDecisionModelTransformer();

		IDecisionModel dm = conv.transform(fm);
		Set<Map<IConfigurable, Boolean>> dSamples = dsamp.sampleValidConfigurations(dm);
		Set<Map<IConfigurable, Boolean>> fSamples = fsamp.sampleValidConfigurations(fm);

		assertTrue(equalValidSamples(dm, dSamples, fm, fSamples));
	}

	@Test
	public void testTransformConstraintEnumDecisionImpliesEnumDecision() throws NotSupportedVariabilityTypeException {
		IFeature cmvr = new Feature(fm, "VIRTUAL_ROOT");
		IFeature cmed1 = new Feature(fm, "ed1");
		IFeature cmed2 = new Feature(fm, "ed2");
		FeatureUtils.setRoot(fm, cmvr);
		FeatureUtils.setAlternative(cmed1);
		FeatureUtils.setOr(cmed2);

		IFeature cmsv1 = new Feature(fm, "dv1");
		IFeature cmsv2 = new Feature(fm, "dv2");

		FeatureUtils.addFeature(fm, cmvr);
		FeatureUtils.addFeature(fm, cmed1);
		FeatureUtils.addFeature(fm, cmed2);
		FeatureUtils.addFeature(fm, cmsv1);
		FeatureUtils.addFeature(fm, cmsv2);

		FeatureUtils.addChild(cmvr, cmed1);
		FeatureUtils.addChild(cmvr, cmed2);
		FeatureUtils.addChild(cmed1, cmsv1);
		FeatureUtils.addChild(cmed2, cmsv2);

		DefaultFeatureModelFactory factory = new DefaultFeatureModelFactory();
		IConstraint constr = factory.createConstraint(fm,
				Prop4JUtils.createImplies(Prop4JUtils.createLiteral(cmsv1), Prop4JUtils.createLiteral(cmsv2)));
		FeatureUtils.addConstraint(fm, constr);

		ISampler<IDecisionModel> dsamp = new DecisionModelSampler();
		ISampler<IFeatureModel> fsamp = new FeatureModelSampler();
		FeatureModeltoDecisionModelTransformer conv = new FeatureModeltoDecisionModelTransformer();

		IDecisionModel dm = conv.transform(fm);
		Set<Map<IConfigurable, Boolean>> dSamples = dsamp.sampleValidConfigurations(dm);
		Set<Map<IConfigurable, Boolean>> fSamples = fsamp.sampleValidConfigurations(fm);

		assertTrue(equalValidSamples(dm, dSamples, fm, fSamples));
	}

	@Test
	public void testTransformConstraintEnumDecisionExcludesEnumDecision() throws NotSupportedVariabilityTypeException {
		IFeature cmvr = new Feature(fm, "VIRTUAL_ROOT");
		IFeature cmed1 = new Feature(fm, "ed1");
		IFeature cmed2 = new Feature(fm, "ed2");
		FeatureUtils.setRoot(fm, cmvr);
		FeatureUtils.setAlternative(cmed1);
		FeatureUtils.setOr(cmed2);

		IFeature cmsv1 = new Feature(fm, "dv1");
		IFeature cmsv2 = new Feature(fm, "dv2");

		FeatureUtils.addFeature(fm, cmvr);
		FeatureUtils.addFeature(fm, cmed1);
		FeatureUtils.addFeature(fm, cmed2);
		FeatureUtils.addFeature(fm, cmsv1);
		FeatureUtils.addFeature(fm, cmsv2);

		FeatureUtils.addChild(cmvr, cmed1);
		FeatureUtils.addChild(cmvr, cmed2);
		FeatureUtils.addChild(cmed1, cmsv1);
		FeatureUtils.addChild(cmed2, cmsv2);

		DefaultFeatureModelFactory factory = new DefaultFeatureModelFactory();
		IConstraint constr = factory.createConstraint(fm, Prop4JUtils.createNot(
				Prop4JUtils.createImplies(Prop4JUtils.createLiteral(cmsv1), Prop4JUtils.createLiteral(cmsv2))));
		FeatureUtils.addConstraint(fm, constr);

		ISampler<IDecisionModel> dsamp = new DecisionModelSampler();
		ISampler<IFeatureModel> fsamp = new FeatureModelSampler();
		FeatureModeltoDecisionModelTransformer conv = new FeatureModeltoDecisionModelTransformer();

		IDecisionModel dm = conv.transform(fm);
		Set<Map<IConfigurable, Boolean>> dSamples = dsamp.sampleValidConfigurations(dm);
		Set<Map<IConfigurable, Boolean>> fSamples = fsamp.sampleValidConfigurations(fm);

		assertTrue(equalValidSamples(dm, dSamples, fm, fSamples));
	}

	@Test
	public void testTransformConstraintEnumDecisionRequiresEnumDecisions() throws NotSupportedVariabilityTypeException {
		IFeature cmvr = new Feature(fm, "VIRTUAL_ROOT");
		IFeature cmed1 = new Feature(fm, "ed1");
		IFeature cmed2 = new Feature(fm, "ed2");
		FeatureUtils.setRoot(fm, cmvr);
		FeatureUtils.setAlternative(cmed1);
		FeatureUtils.setOr(cmed2);

		IFeature cmsv1 = new Feature(fm, "sv1");
		IFeature cmsv2 = new Feature(fm, "sv2");
		IFeature cmsv3 = new Feature(fm, "sv3");

		FeatureUtils.addFeature(fm, cmvr);
		FeatureUtils.addFeature(fm, cmed1);
		FeatureUtils.addFeature(fm, cmed2);
		FeatureUtils.addFeature(fm, cmsv1);
		FeatureUtils.addFeature(fm, cmsv2);
		FeatureUtils.addFeature(fm, cmsv3);

		FeatureUtils.addChild(cmvr, cmed1);
		FeatureUtils.addChild(cmvr, cmed2);
		FeatureUtils.addChild(cmed1, cmsv1);
		FeatureUtils.addChild(cmed2, cmsv2);
		FeatureUtils.addChild(cmed2, cmsv3);

		DefaultFeatureModelFactory factory = new DefaultFeatureModelFactory();
		IConstraint constr = factory.createConstraint(fm, Prop4JUtils.createImplies(Prop4JUtils.createLiteral(cmsv1),
				Prop4JUtils.createAnd(Prop4JUtils.createLiteral(cmsv2), Prop4JUtils.createLiteral(cmsv3))));
		FeatureUtils.addConstraint(fm, constr);

		ISampler<IDecisionModel> dsamp = new DecisionModelSampler();
		ISampler<IFeatureModel> fsamp = new FeatureModelSampler();
		FeatureModeltoDecisionModelTransformer conv = new FeatureModeltoDecisionModelTransformer();

		IDecisionModel dm = conv.transform(fm);
		Set<Map<IConfigurable, Boolean>> dSamples = dsamp.sampleValidConfigurations(dm);
		Set<Map<IConfigurable, Boolean>> fSamples = fsamp.sampleValidConfigurations(fm);

		assertTrue(equalValidSamples(dm, dSamples, fm, fSamples));
	}

	@Test
	public void testTransformConstraintEnumDecisionExcludesEnumDecisions() throws NotSupportedVariabilityTypeException {
		IFeature cmvr = new Feature(fm, "VIRTUAL_ROOT");
		IFeature cmed1 = new Feature(fm, "ed1");
		IFeature cmed2 = new Feature(fm, "ed2");
		FeatureUtils.setRoot(fm, cmvr);
		FeatureUtils.setAlternative(cmed1);
		FeatureUtils.setOr(cmed2);

		IFeature cmsv1 = new Feature(fm, "sv1");
		IFeature cmsv2 = new Feature(fm, "sv2");
		IFeature cmsv3 = new Feature(fm, "sv3");

		FeatureUtils.addFeature(fm, cmvr);
		FeatureUtils.addFeature(fm, cmed1);
		FeatureUtils.addFeature(fm, cmed2);
		FeatureUtils.addFeature(fm, cmsv1);
		FeatureUtils.addFeature(fm, cmsv2);
		FeatureUtils.addFeature(fm, cmsv3);

		FeatureUtils.addChild(cmvr, cmed1);
		FeatureUtils.addChild(cmvr, cmed2);
		FeatureUtils.addChild(cmed1, cmsv1);
		FeatureUtils.addChild(cmed2, cmsv2);
		FeatureUtils.addChild(cmed2, cmsv3);

		DefaultFeatureModelFactory factory = new DefaultFeatureModelFactory();
		IConstraint constr = factory.createConstraint(fm,
				Prop4JUtils.createImplies(Prop4JUtils.createLiteral(cmsv1),
						Prop4JUtils.createAnd(Prop4JUtils.createNot(Prop4JUtils.createLiteral(cmsv2)),
								Prop4JUtils.createNot(Prop4JUtils.createLiteral(cmsv3)))));
		FeatureUtils.addConstraint(fm, constr);

		ISampler<IDecisionModel> dsamp = new DecisionModelSampler();
		ISampler<IFeatureModel> fsamp = new FeatureModelSampler();
		FeatureModeltoDecisionModelTransformer conv = new FeatureModeltoDecisionModelTransformer();

		IDecisionModel dm = conv.transform(fm);
		Set<Map<IConfigurable, Boolean>> dSamples = dsamp.sampleValidConfigurations(dm);
		Set<Map<IConfigurable, Boolean>> fSamples = fsamp.sampleValidConfigurations(fm);

		assertTrue(equalValidSamples(dm, dSamples, fm, fSamples));
	}

	@Test
	public void testTransformConstraintUnidirectionalIncludesTwoFeatures() throws NotSupportedVariabilityTypeException {
		IFeature cmvr = new Feature(fm, "VIRTUAL_ROOT");
		IFeature cmed1 = new Feature(fm, "ed1");
		IFeature cmed2 = new Feature(fm, "ed2");
		FeatureUtils.setRoot(fm, cmvr);
		FeatureUtils.setAlternative(cmed1);
		FeatureUtils.setOr(cmed2);

		IFeature cmsv1 = new Feature(fm, "sv1");
		IFeature cmsv2 = new Feature(fm, "sv2");
		IFeature cmsv3 = new Feature(fm, "sv3");

		FeatureUtils.addFeature(fm, cmvr);
		FeatureUtils.addFeature(fm, cmed1);
		FeatureUtils.addFeature(fm, cmed2);
		FeatureUtils.addFeature(fm, cmsv1);
		FeatureUtils.addFeature(fm, cmsv2);
		FeatureUtils.addFeature(fm, cmsv3);

		FeatureUtils.addChild(cmvr, cmed1);
		FeatureUtils.addChild(cmvr, cmed2);
		FeatureUtils.addChild(cmed1, cmsv1);
		FeatureUtils.addChild(cmed2, cmsv2);
		FeatureUtils.addChild(cmed2, cmsv3);

		DefaultFeatureModelFactory factory = new DefaultFeatureModelFactory();
		IConstraint constr = factory.createConstraint(fm,
				Prop4JUtils.createNot(Prop4JUtils.createImplies(Prop4JUtils.createLiteral(cmsv1),
						Prop4JUtils.createOr(Prop4JUtils.createLiteral(cmsv2), Prop4JUtils.createLiteral(cmsv3)))));
		FeatureUtils.addConstraint(fm, constr);

		ISampler<IDecisionModel> dsamp = new DecisionModelSampler();
		ISampler<IFeatureModel> fsamp = new FeatureModelSampler();
		FeatureModeltoDecisionModelTransformer conv = new FeatureModeltoDecisionModelTransformer();

		IDecisionModel dm = conv.transform(fm);
		Set<Map<IConfigurable, Boolean>> dSamples = dsamp.sampleValidConfigurations(dm);
		Set<Map<IConfigurable, Boolean>> fSamples = fsamp.sampleValidConfigurations(fm);

		assertTrue(equalValidSamples(dm, dSamples, fm, fSamples));
	}

	@Test
	public void testTransformConstraintUnidirectionalIncludesOneFeature() throws NotSupportedVariabilityTypeException {
		IFeature cmvr = new Feature(fm, "VIRTUAL_ROOT");
		IFeature cmed1 = new Feature(fm, "ed1");
		IFeature cmed2 = new Feature(fm, "ed2");
		FeatureUtils.setRoot(fm, cmvr);
		FeatureUtils.setAlternative(cmed1);
		FeatureUtils.setOr(cmed2);

		IFeature cmsv1 = new Feature(fm, "sv1");
		IFeature cmsv2 = new Feature(fm, "sv2");
		IFeature cmsv3 = new Feature(fm, "sv3");

		FeatureUtils.addFeature(fm, cmvr);
		FeatureUtils.addFeature(fm, cmed1);
		FeatureUtils.addFeature(fm, cmed2);
		FeatureUtils.addFeature(fm, cmsv1);
		FeatureUtils.addFeature(fm, cmsv2);
		FeatureUtils.addFeature(fm, cmsv3);

		FeatureUtils.addChild(cmvr, cmed1);
		FeatureUtils.addChild(cmvr, cmed2);
		FeatureUtils.addChild(cmed1, cmsv1);
		FeatureUtils.addChild(cmed2, cmsv2);
		FeatureUtils.addChild(cmed2, cmsv3);

		DefaultFeatureModelFactory factory = new DefaultFeatureModelFactory();
		IConstraint constr = factory.createConstraint(fm,
				Prop4JUtils.createNot(Prop4JUtils.createImplies(Prop4JUtils.createLiteral(cmsv1),
						Prop4JUtils.createNot(Prop4JUtils.createLiteral(cmsv2)))));
		FeatureUtils.addConstraint(fm, constr);

		ISampler<IDecisionModel> dsamp = new DecisionModelSampler();
		ISampler<IFeatureModel> fsamp = new FeatureModelSampler();
		FeatureModeltoDecisionModelTransformer conv = new FeatureModeltoDecisionModelTransformer();

		IDecisionModel dm = conv.transform(fm);
		Set<Map<IConfigurable, Boolean>> dSamples = dsamp.sampleValidConfigurations(dm);
		Set<Map<IConfigurable, Boolean>> fSamples = fsamp.sampleValidConfigurations(fm);

		assertTrue(equalValidSamples(dm, dSamples, fm, fSamples));
	}

	@Test
	public void testTransformConstraintNegatedImpliesWithOrAllPositiveLiterals()
			throws NotSupportedVariabilityTypeException {
		IFeature cmvr = new Feature(fm, "VIRTUAL_ROOT");
		IFeature cmed1 = new Feature(fm, "ed1");
		IFeature cmed2 = new Feature(fm, "ed2");
		FeatureUtils.setRoot(fm, cmvr);
		FeatureUtils.setAlternative(cmed1);
		FeatureUtils.setOr(cmed2);

		IFeature cmsv1 = new Feature(fm, "sv1");
		IFeature cmsv2 = new Feature(fm, "sv2");
		IFeature cmsv3 = new Feature(fm, "sv3");

		FeatureUtils.addFeature(fm, cmvr);
		FeatureUtils.addFeature(fm, cmed1);
		FeatureUtils.addFeature(fm, cmed2);
		FeatureUtils.addFeature(fm, cmsv1);
		FeatureUtils.addFeature(fm, cmsv2);
		FeatureUtils.addFeature(fm, cmsv3);

		FeatureUtils.addChild(cmvr, cmed1);
		FeatureUtils.addChild(cmvr, cmed2);
		FeatureUtils.addChild(cmed1, cmsv1);
		FeatureUtils.addChild(cmed2, cmsv2);
		FeatureUtils.addChild(cmed2, cmsv3);

		DefaultFeatureModelFactory factory = new DefaultFeatureModelFactory();
		IConstraint constr = factory.createConstraint(fm,
				Prop4JUtils.createNot(Prop4JUtils.createImplies(Prop4JUtils.createLiteral(cmsv1),
						Prop4JUtils.createAnd(Prop4JUtils.createNot(Prop4JUtils.createLiteral(cmsv3)),
								Prop4JUtils.createNot(Prop4JUtils.createLiteral(cmsv2))))));
		FeatureUtils.addConstraint(fm, constr);

		ISampler<IDecisionModel> dsamp = new DecisionModelSampler();
		ISampler<IFeatureModel> fsamp = new FeatureModelSampler();
		FeatureModeltoDecisionModelTransformer conv = new FeatureModeltoDecisionModelTransformer();

		IDecisionModel dm = conv.transform(fm);
		Set<Map<IConfigurable, Boolean>> dSamples = dsamp.sampleValidConfigurations(dm);
		Set<Map<IConfigurable, Boolean>> fSamples = fsamp.sampleValidConfigurations(fm);

		assertTrue(equalValidSamples(dm, dSamples, fm, fSamples));
	}

	@Test
	public void testTransformConstraintBooleanDecisionExcludesBoolean() throws NotSupportedVariabilityTypeException {
		IFeature cmvr = new Feature(fm, "VIRTUAL_ROOT");
		IFeature cmbd1 = new Feature(fm, "bd1");
		IFeature cmbd2 = new Feature(fm, "bd2");
		FeatureUtils.setRoot(fm, cmvr);

		FeatureUtils.addFeature(fm, cmvr);
		FeatureUtils.addFeature(fm, cmbd1);
		FeatureUtils.addFeature(fm, cmbd2);

		FeatureUtils.addChild(cmvr, cmbd1);
		FeatureUtils.addChild(cmvr, cmbd2);

		DefaultFeatureModelFactory factory = new DefaultFeatureModelFactory();
		IConstraint constr = factory.createConstraint(fm, Prop4JUtils.createImplies(Prop4JUtils.createLiteral(cmbd1),
				Prop4JUtils.createNot(Prop4JUtils.createLiteral(cmbd2))));
		FeatureUtils.addConstraint(fm, constr);

		ISampler<IDecisionModel> dsamp = new DecisionModelSampler();
		ISampler<IFeatureModel> fsamp = new FeatureModelSampler();
		FeatureModeltoDecisionModelTransformer conv = new FeatureModeltoDecisionModelTransformer();

		IDecisionModel dm = conv.transform(fm);
		Set<Map<IConfigurable, Boolean>> dSamples = dsamp.sampleValidConfigurations(dm);
		Set<Map<IConfigurable, Boolean>> fSamples = fsamp.sampleValidConfigurations(fm);

		assertTrue(equalValidSamples(dm, dSamples, fm, fSamples));
	}

	@Test
	public void testTransformConstraintEnumDecisionWithNoneSelectsBoolean() throws NotSupportedVariabilityTypeException {
		IFeature cmvr = new Feature(fm, "VIRTUAL_ROOT");
		IFeature cmed1 = new Feature(fm, "ed1");
		IFeature cmbd1 = new Feature(fm, "bd1");
		FeatureUtils.setRoot(fm, cmvr);
		FeatureUtils.setOr(cmed1);

		IFeature cmsv1 = new Feature(fm, "sv1");
		IFeature cmsv2 = new Feature(fm, "None");

		FeatureUtils.addFeature(fm, cmvr);
		FeatureUtils.addFeature(fm, cmed1);
		FeatureUtils.addFeature(fm, cmbd1);
		FeatureUtils.addFeature(fm, cmsv1);
		FeatureUtils.addFeature(fm, cmsv2);

		FeatureUtils.addChild(cmvr, cmed1);
		FeatureUtils.addChild(cmvr, cmbd1);
		FeatureUtils.addChild(cmed1, cmsv1);
		FeatureUtils.addChild(cmed1, cmsv2);

		DefaultFeatureModelFactory factory = new DefaultFeatureModelFactory();
		IConstraint constr = factory.createConstraint(fm,
				Prop4JUtils.createImplies(Prop4JUtils.createLiteral(cmed1), Prop4JUtils.createLiteral(cmbd1)));
		FeatureUtils.addConstraint(fm, constr);

		ISampler<IDecisionModel> dsamp = new DecisionModelSampler();
		ISampler<IFeatureModel> fsamp = new FeatureModelSampler();
		FeatureModeltoDecisionModelTransformer conv = new FeatureModeltoDecisionModelTransformer();

		IDecisionModel dm = conv.transform(fm);
		Set<Map<IConfigurable, Boolean>> dSamples = dsamp.sampleValidConfigurations(dm);
		Set<Map<IConfigurable, Boolean>> fSamples = fsamp.sampleValidConfigurations(fm);

		assertTrue(equalValidSamples(dm, dSamples, fm, fSamples));
	}

	@Test
	public void testTransformConstraintBooleanSelectsEnumDecisionWithNone() throws NotSupportedVariabilityTypeException {
		IFeature cmvr = new Feature(fm, "VIRTUAL_ROOT");
		IFeature cmed1 = new Feature(fm, "ed1");
		IFeature cmbd1 = new Feature(fm, "bd1");
		FeatureUtils.setRoot(fm, cmvr);
		FeatureUtils.setOr(cmed1);

		IFeature cmsv1 = new Feature(fm, "sv1");
		IFeature cmsv2 = new Feature(fm, "None");

		FeatureUtils.addFeature(fm, cmvr);
		FeatureUtils.addFeature(fm, cmed1);
		FeatureUtils.addFeature(fm, cmbd1);
		FeatureUtils.addFeature(fm, cmsv1);
		FeatureUtils.addFeature(fm, cmsv2);

		FeatureUtils.addChild(cmvr, cmed1);
		FeatureUtils.addChild(cmvr, cmbd1);
		FeatureUtils.addChild(cmed1, cmsv1);
		FeatureUtils.addChild(cmed1, cmsv2);

		DefaultFeatureModelFactory factory = new DefaultFeatureModelFactory();
		IConstraint constr = factory.createConstraint(fm,
				Prop4JUtils.createImplies(Prop4JUtils.createLiteral(cmbd1), Prop4JUtils.createLiteral(cmsv2)));
		FeatureUtils.addConstraint(fm, constr);

		ISampler<IDecisionModel> dsamp = new DecisionModelSampler();
		ISampler<IFeatureModel> fsamp = new FeatureModelSampler();
		FeatureModeltoDecisionModelTransformer conv = new FeatureModeltoDecisionModelTransformer();

		IDecisionModel dm = conv.transform(fm);
		Set<Map<IConfigurable, Boolean>> dSamples = dsamp.sampleValidConfigurations(dm);
		Set<Map<IConfigurable, Boolean>> fSamples = fsamp.sampleValidConfigurations(fm);

		assertTrue(equalValidSamples(dm, dSamples, fm, fSamples));
	}

	private boolean equalValidSamples(final IDecisionModel dm, final Set<Map<IConfigurable, Boolean>> d,
			final IFeatureModel fm, final Set<Map<IConfigurable, Boolean>> f)
			throws NotSupportedVariabilityTypeException {
		ISampler<IDecisionModel> dsamp = new DecisionModelSampler();
		ISampler<IFeatureModel> fsamp = new FeatureModelSampler();
		long count1 = 0;
		for (Map<IConfigurable, Boolean> sample : f) {
			boolean also = dsamp.verifySampleAs(dm, sample);
			if (also) {
				count1++;
			}
		}
		boolean bFSamps = count1 == f.size();
		int count2 = 0;

		for (Map<IConfigurable, Boolean> sample : d) {
			boolean also = fsamp.verifySampleAs(fm, sample);
			if (also) {
				count2++;
			}
		}
		boolean bDSamps = count2 == d.size();

		return bFSamps && bDSamps;
	}

}
