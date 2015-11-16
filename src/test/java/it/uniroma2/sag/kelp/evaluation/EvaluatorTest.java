/*
 * Copyright 2014 Simone Filice and Giuseppe Castellucci and Danilo Croce and Roberto Basili
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.uniroma2.sag.kelp.evaluation;

import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.data.label.StringLabel;
import it.uniroma2.sag.kelp.utils.evaluation.BinaryClassificationEvaluator;
import it.uniroma2.sag.kelp.utils.evaluation.MulticlassClassificationEvaluator;
import it.uniroma2.sag.kelp.utils.exception.NoSuchPerformanceMeasureException;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class EvaluatorTest {
	private static List<Label> labels =null;
	
	@BeforeClass
	public static void createObjects() {
		labels = new ArrayList<Label>();
		labels.add(new StringLabel("one"));
		labels.add(new StringLabel("two"));
		labels.add(new StringLabel("three"));
	}
	
	@Test
	public void multiclassEvaluatorMethodCallingTest() {
		MulticlassClassificationEvaluator clEv = new MulticlassClassificationEvaluator(labels);
		try {
			clEv.getPerformanceMeasure("Accuracy");
			clEv.getPerformanceMeasure("accuracy");

			clEv.getPerformanceMeasure("MeanF1");
			clEv.getPerformanceMeasure("meanF1");
			
			clEv.getPerformanceMeasure("OverallPrecision");
			clEv.getPerformanceMeasure("overallPrecision");
			
			clEv.getPerformanceMeasure("OverallRecall");
			clEv.getPerformanceMeasure("overallRecall");
		} catch (NoSuchPerformanceMeasureException e) {
			Assert.assertTrue(false);
		}
		Assert.assertTrue(true);
	}
	
	@Test
	public void binaryEvaluatorMethodCallingTest() {
		BinaryClassificationEvaluator ev = new BinaryClassificationEvaluator(new StringLabel("one"));
		try {
			ev.getPerformanceMeasure("Accuracy");
			ev.getPerformanceMeasure("accuracy");
			
			ev.getPerformanceMeasure("F1");
			ev.getPerformanceMeasure("f1");
			
			ev.getPerformanceMeasure("Precision");
			ev.getPerformanceMeasure("precision");
			
			ev.getPerformanceMeasure("Recall");
			ev.getPerformanceMeasure("recall");
		} catch (NoSuchPerformanceMeasureException e) {
			Assert.assertTrue(false);
		}
		Assert.assertTrue(true);
	}
	
	@Test
	public void directMethodCallingOnBinaryTest() {
		BinaryClassificationEvaluator ev = new BinaryClassificationEvaluator(new StringLabel("one"));
		ev.getAccuracy();
		ev.getF1();
		ev.getPrecision();
		ev.getRecall();
	}
	
	@Test
	public void directMethodCallingOnMulticlassTest() {
		MulticlassClassificationEvaluator ev = new MulticlassClassificationEvaluator(labels);
		ev.getAccuracy();
		ev.getMeanF1();
		Label l = new StringLabel("one");
		ev.getPrecisionFor(l);
		ev.getRecallFor(l);
		ev.getF1For(l);
		ev.getOverallPrecision();
		ev.getOverallRecall();
		ev.getOverallF1();
		ev.getRecalls();
		ev.getPrecisions();
		ev.getF1s();
	}
}
