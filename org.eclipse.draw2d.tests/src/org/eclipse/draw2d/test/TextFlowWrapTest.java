/*******************************************************************************
 * Copyright (c) 2000, 2025 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.draw2d.test;

import java.text.BreakIterator;
import java.text.spi.BreakIteratorProvider;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ServiceLoader;

import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.text.FlowBox;
import org.eclipse.draw2d.text.FlowPage;
import org.eclipse.draw2d.text.InlineFlow;
import org.eclipse.draw2d.text.ParagraphTextLayout;
import org.eclipse.draw2d.text.TextFlow;
import org.eclipse.draw2d.text.TextFragmentBox;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TextFlowWrapTest extends BaseTestCase {

	// @TODO:Pratik create similar test cases for bidi...where the fragments are
	// examined

	// used to ensure that there are no extra fragments
	static final String TERMINATE = "#@terminate@#!"; //$NON-NLS-1$
	// used to ensure that two consecutive fragments are on the same line
	static final String SAMELINE = "#@sameline@#"; //$NON-NLS-1$
	// used to ensure that two consecutive fragments are on different lines
	static final String NEWLINE = "#@newline@#"; //$NON-NLS-1$
	// used to ensure that a fragment is truncated (this mark is placed after
	// the fragment
	// that is supposed to be truncated)
	static final String TRUNCATED = "#@truncated@#"; //$NON-NLS-1$
	// used to ensure that a fragment is not truncated (this mark is placed
	// after the fragment
	// that is not supposed to be truncated)
	static final String NON_TRUNCATED = "#@non-truncated@#"; //$NON-NLS-1$

	FlowPage figure;
	TextFlow textFlow, textFlow2;
	ClassLoader classLoader;

	@BeforeEach
	public void setUp() {
		classLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
	}

	@AfterEach
	public void tearDown() {
		Thread.currentThread().setContextClassLoader(classLoader);
	}

	protected void doTest(String stringToTest, String widthString, String[] answers) {
		doTest2(stringToTest, "", widthString, answers); //$NON-NLS-1$
	}

	protected void doTest2(String string1, String string2, String widthString, String[] answers) {
		int width = -1;
		if (widthString != null) {
			width = FigureUtilities.getStringExtents(widthString, SERIF).width;
		}
		figure.setSize(width, 1000);
		textFlow.setText(string1);
		textFlow2.setText(string2);
		figure.validate();
		List<FlowBox> list = new ArrayList<>(textFlow.getFragments());
		if (string2.length() != 0) {
			list.addAll(textFlow2.getFragments());
		}
		Iterator<FlowBox> frags = list.iterator();

		int index = 0;
		TextFragmentBox previousFrag = null;
		for (; index < answers.length; index++) {
			String answer = answers[index];
			if (answer == TERMINATE) {
				// if (frags.hasNext()) {
				// TextFragmentBox box = (TextFragmentBox)frags.next();
				// assertTrue("Failed on: " + string1 + string2 +
				// " Found extra fragment: -"
				// + string1.substring(box.offset, box.offset + box.length) +
				// "-\n",
				// false);
				// }
				return;
			}
			if (answer == TRUNCATED) {
				assertTrue(previousFrag.isTruncated(),
						"Failed on: " + string1 + string2 + "Fragment is not truncated\n"); //$NON-NLS-1$//$NON-NLS-2$
				continue;
			}
			if (answer == NON_TRUNCATED) {
				assertFalse(previousFrag.isTruncated(), "Failed on: " + string1 + string2 + "Fragment is truncated\n"); //$NON-NLS-1$ //$NON-NLS-2$
				continue;
			}

			if (!frags.hasNext()) {
				break;
			}

			TextFragmentBox frag = (TextFragmentBox) frags.next();

			if (answer == SAMELINE) {
				assertTrue(previousFrag.getBaseline() == frag.getBaseline(),
						"Failed on: " + string1 + string2 + " Fragments are not on the same line\n"); //$NON-NLS-1$//$NON-NLS-2$
				index++;
				if (index >= answers.length) {
					return;
				}
				answer = answers[index];
			} else if (answer == NEWLINE) {
				assertTrue(previousFrag.getBaseline() != frag.getBaseline(),
						"Failed on: " + string1 + string2 + " Fragments are on the same line\n"); //$NON-NLS-1$ //$NON-NLS-2$
				index++;
				if (index >= answers.length) {
					return;
				}
				answer = answers[index];
			}
			previousFrag = frag;

			if (textFlow.getFragments().contains(frag)) {
				assertEquals(answer, string1.substring(frag.offset, frag.offset + frag.length),
						"Failed on: \"" + string1 + "\" + \"" + string2 + "\" Fragment expected: \"" + answer //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
								+ "\" Got: \"" + string1.substring(frag.offset, frag.offset + frag.length) + "\"\n"); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				assertEquals(answer, string2.substring(frag.offset, frag.offset + frag.length),
						"Failed on: \"" + string1 + "\" + \"" + string2 + "\" Fragment expected: \"" + answer //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
								+ "\" Got: \"" + string2.substring(frag.offset, frag.offset + frag.length) + "\"\n"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}

		if (index < answers.length) {
			// We put this in the if statement because the error message
			// accesses answers[index]
			// which would cause an index out of bounds exception if there were
			// no leftover
			// fragments
			fail("Failed on: \"" + string1 + "\" + \"" + string2 + "\" Fragment expected: -" + answers[index] //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					+ "- No corresponding fragment\n"); //$NON-NLS-1$
		}
	}

	protected void runGenericTests() {
		doTest("tester abc", "tester", new String[] { "tester", "abc", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		doTest("tester abc", "tester a", new String[] { "tester", "abc", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		doTest("tester abc", "tester ab", new String[] { "tester", "abc", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		doTest("tester ab", "tester", new String[] { "tester", "ab" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		doTest("tester ab c", "tester", new String[] { "tester", "ab c" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		doTest("test\r ab c", "test ab c", new String[] { "test", " ab c" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		doTest("test\n ab c", "test ab c", new String[] { "test", " ab c" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		doTest("test\r\n abc def", "test abc def", new String[] { "test", " abc def", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		doTest("\rtester abc def", "tester", new String[] { "", "tester" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		doTest("\r\ntester abc def", "tester", new String[] { "", "tester" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		doTest("\ntester abc def", "tester", new String[] { "", "tester" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		doTest("tester abc\n def", "tester", new String[] { "tester", "abc", " def", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		doTest("tester abc\r\n def", "tester", new String[] { "tester", "abc", " def", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		doTest("tester abc\r def", "tester", new String[] { "tester", "abc", " def", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		doTest("tester abc def\r\n", "tester", new String[] { "tester", "abc", "def", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		doTest("tester abc def\r", "tester", new String[] { "tester", "abc", "def", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		doTest("tester abc def\n", "tester", new String[] { "tester", "abc", "def", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		doTest("blah blah blah", "blah blah", new String[] { "blah blah", "blah", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		doTest("blah blah blah", "blah", new String[] { "blah", "blah" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		doTest("h hh h", "h hh", new String[] { "h hh", "h", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		doTest("h hh h", "h hh ", new String[] { "h hh", "h" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		doTest("x x x  x ", "x x x ", new String[] { "x x x ", "x ", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		doTest("x x x  x", "x x x", new String[] { "x x x", " x", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		doTest("\n\nbreak", "break", new String[] { "", "" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		doTest("\r\rbreak", "break", new String[] { "", "" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		doTest("\r\n\r\nbreak", "break", new String[] { "", "", "break", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		doTest("crow ", "crow", new String[] { "crow", NEWLINE, "", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

		doTest("abc - -moreango", "abc", new String[] { "abc", NEWLINE, "- -", NEWLINE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		doTest("abc def ghi", "abc def g", new String[] { "abc def", "ghi", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		doTest("blah blah ", "blah blah", new String[] { "blah blah", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		doTest("testers testers testers ab c", "testers testers test", //$NON-NLS-1$ //$NON-NLS-2$
				new String[] { "testers testers", "testers ab c", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$
		doTest("testers\r ab c", "testers", new String[] { "testers", " ab c", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		doTest("trailingSpace  \n  ", "trailingSpace", new String[] { "trailingSpace", " ", "  ", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		doTest("test \r b", "test", new String[] { "test", "", " b", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		doTest("   \n   \n   \n   ", "wwwwww", new String[] { "   ", "   ", "   ", "   ", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		doTest("   \n  \n   ", " ", new String[] { " ", " ", " ", "", " ", " ", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
		doTest("\r\r\n", "wwwwwww", new String[] { "", "", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		doTest("", "www", new String[] { "", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
		// empty string means availableWidth == 1
		doTest("", "", new String[] { "", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		doTest("a cow\naha", "a cow", new String[] { "a cow", "aha", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		// tests with two spaces after a period
		doTest("a one. two", "a one", new String[] { "a", NEWLINE, "one.", NEWLINE, "two" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		doTest("a one-two", "a one", new String[] { "a", NEWLINE, "one-", NEWLINE, "two" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		// chinese characters
		doTest("\u7325\u7334\u7329", "\u7325\u7334", new String[] { "\u7325\u7334", "\u7329", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		doTest("\u7325\u7334\u7329", "\u7325", new String[] { "\u7325", "\u7334", "\u7329", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

		// BiDi
		doTest2("\u0634", "foo-bar", "\u0634foo-", new String[] { "\u0634", SAMELINE, "foo-", NEWLINE, "bar" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

		// testing with multiple TextFlows will bring lookAhead into action
		doTest2("foo1", " bar1", null, new String[] { "foo1", SAMELINE, " bar1", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		doTest2("foo2", " bar2", "foo2 ", new String[] { "foo2", "", "bar2", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		doTest2("foo3", " ba3", "foo3", new String[] { "foo3", "", NEWLINE, "ba3", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		doTest2("foo4 ", " bar4", "foo4 ", new String[] { "foo4 ", "", "bar4", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		doTest2("wwww ", " bar", "wwww", new String[] { "wwww", "", " bar", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		doTest2("foo5 ", "bar5", "foo5 ", new String[] { "foo5", NEWLINE, "", "bar5", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		doTest2("foot bar", "xyz", "booxyz", new String[] { "foot", "bar", SAMELINE, "xyz", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		doTest2("foo\n", " bar6", null, new String[] { "foo", NEWLINE, "", SAMELINE, " bar6", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		// doTest2("foo7-bar7", "mo", "foo7-ba", new String[] {"foo7-", NEWLINE,
		// "bar7", SAMELINE, "mo", TERMINATE});
		doTest2("foo-bar", "abc", "foo-barab", new String[] { "foo-", NEWLINE, "bar", SAMELINE, "abc", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		// doTest2(" foobar", "abc", " foobarab", new String[] {"", NEWLINE,
		// "foobar"});
		doTest2("foo  bar", "abc", "foo  barab", new String[] { "foo ", NEWLINE, "bar", SAMELINE, "abc", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		// doTest2("abd", "\u7325", "abd", new String[] {"abd", NEWLINE,
		// "\u7325"});

		doTest2("a abc", "-def", "a abc", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				new String[] { "a", NEWLINE, "abc", SAMELINE, "-", NEWLINE, "def", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$

		doTest2("alpha\n", "bravo", null, new String[] { "alpha", NEWLINE, "", SAMELINE, "bravo", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	}

	protected void runHardWrappingTests() {
		doTest("ahahahah", "aha", new String[] { "ahahahah", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		doTest("Flow    Container  ", " ", new String[] { "Flow", " ", "", "Container", " ", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
		doTest("aha \nb \r c ", "", new String[] { "aha", "", "b", "", "", "c", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
		doTest2("one", "two", "onet", new String[] { "one", SAMELINE, "two", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		doTest2("one", "t ", "one", new String[] { "one", SAMELINE, "t", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		doTest("Flowing", "flow", new String[] { "Flowing", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		doTest2("foobar", "foobar", "foo", new String[] { "foobar", SAMELINE, "foobar" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		doTest2("home ", "alone", "home al", new String[] { "home", NEWLINE, "", SAMELINE, "alone", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		doTest2("more willing in t", "hemorning", "more willing in themorni", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				new String[] { "more willing in", NEWLINE, "t", SAMELINE, "hemorning", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	protected void runSoftWrappingTests() {
		doTest("tester ab", "teste", new String[] { "teste", NEWLINE, "r ab", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		doTest("aha \nb \r c ", "", new String[] { "a", "h", "a", "", "b", "", "", "c", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$
		doTest("\u0634abcd", "\u0634abc", new String[] { "\u0634", SAMELINE, "abc", NEWLINE, "d", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		// doTest2("foofoo", "foo", "foo", new String[] {"foo", NEWLINE, "foo",
		// NEWLINE, "foo", TERMINATE});
		// doTest2("foofo", "ofoo", "foo", new String[] {"foo", NEWLINE, "fo",
		// SAMELINE, "o", NEWLINE, "foo", TERMINATE});
	}

	protected void runTruncatedWrappingTests() {
		doTest("Flowing  Container", "Flo ...", new String[] { "Flo", NEWLINE, "Co", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		doTest("Flowing C", "Flo ...", new String[] { "Flo", "C", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		doTest("Fooooooo", "...", new String[] { "", TRUNCATED, TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		doTest("WWW", "|...", new String[] { "", TRUNCATED, TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		doTest(" Foo", "Foo", new String[] { "", "Foo", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		// doTest("aha \nb \r c ", "", new String[] {"", TRUNCATED, NEWLINE,
		// "b", NON_TRUNCATED, NEWLINE, "", NON_TRUNCATED, NEWLINE, "c",
		// NON_TRUNCATED});
		// doTest("aha \nb \r c", "", new String[] {"", TRUNCATED, "",
		// NON_TRUNCATED, "b", "", NON_TRUNCATED, "", "c", TERMINATE});
		// doTest("aha \nb \r w ", "..", new String[] {"", TRUNCATED, "",
		// NON_TRUNCATED, "b", "", NON_TRUNCATED, "", "w", TERMINATE});
		// truncation is not supported with BiDi and across figures (with
		// look-ahead), so we're
		// not testing it here
		// doTest2("foobar", "foobar", "foobar...", new String[] {"foobar",
		// SAMELINE, "", TERMINATE});
		// doTest2("foobar", "foobar", "fooba...", new String[] {"fooba",
		// TERMINATE});
		// doTest2("foobar", "foobar", "f...", new String[] {"f", TERMINATE});
	}

	@Test
	public void testHardWrapping() {
		figure = new FlowPage();
		textFlow = new TextFlow();
		textFlow.setLayoutManager(new ParagraphTextLayout(textFlow, ParagraphTextLayout.WORD_WRAP_HARD));
		textFlow.setFont(SERIF);
		figure.add(textFlow);
		textFlow2 = new TextFlow();
		textFlow2.setLayoutManager(new ParagraphTextLayout(textFlow2, ParagraphTextLayout.WORD_WRAP_HARD));
		textFlow2.setFont(SERIF);
		figure.add(textFlow2);

		runGenericTests();
		runHardWrappingTests();
	}

	@Test
	public void testSoftWrapping() {
		figure = new FlowPage();
		textFlow = new TextFlow();
		textFlow.setLayoutManager(new ParagraphTextLayout(textFlow, ParagraphTextLayout.WORD_WRAP_SOFT));
		textFlow.setFont(SERIF);
		figure.add(textFlow);
		textFlow2 = new TextFlow();
		textFlow2.setLayoutManager(new ParagraphTextLayout(textFlow2, ParagraphTextLayout.WORD_WRAP_SOFT));
		textFlow2.setFont(SERIF);
		figure.add(textFlow2);

		runGenericTests();
		runSoftWrappingTests();
	}

	@Test
	public void testTruncatedWrapping() {
		figure = new FlowPage();
		textFlow = new TextFlow();
		textFlow.setLayoutManager(new ParagraphTextLayout(textFlow, ParagraphTextLayout.WORD_WRAP_TRUNCATE));
		textFlow.setFont(SERIF);
		figure.add(textFlow);
		textFlow2 = new TextFlow();
		textFlow2.setLayoutManager(new ParagraphTextLayout(textFlow2, ParagraphTextLayout.WORD_WRAP_TRUNCATE));
		textFlow2.setFont(SERIF);
		figure.add(textFlow2);

		runGenericTests();
		runTruncatedWrappingTests();
	}

	@Test
	public void testInlineFlow() {
		figure = new FlowPage();
		InlineFlow inline = new InlineFlow();
		textFlow = new TextFlow();
		textFlow.setLayoutManager(new ParagraphTextLayout(textFlow, ParagraphTextLayout.WORD_WRAP_SOFT));
		textFlow.setFont(SERIF);
		inline.add(textFlow);
		figure.add(inline);
		textFlow2 = new TextFlow();
		textFlow2.setLayoutManager(new ParagraphTextLayout(textFlow2, ParagraphTextLayout.WORD_WRAP_SOFT));
		textFlow2.setFont(SERIF);
		figure.add(textFlow2);
		runGenericTests();
		runSoftWrappingTests();

		figure = new FlowPage();
		inline = new InlineFlow();
		textFlow = new TextFlow();
		textFlow.setLayoutManager(new ParagraphTextLayout(textFlow, ParagraphTextLayout.WORD_WRAP_HARD));
		textFlow.setFont(SERIF);
		inline.add(textFlow);
		figure.add(inline);
		textFlow2 = new TextFlow();
		textFlow2.setLayoutManager(new ParagraphTextLayout(textFlow2, ParagraphTextLayout.WORD_WRAP_HARD));
		textFlow2.setFont(SERIF);
		figure.add(textFlow2);
		runGenericTests();
		runHardWrappingTests();
		doTest2("def", "def", "defde", new String[] { "def", SAMELINE, "def", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	}

	@Test
	public void testNestedInlineFlows() {
		figure = new FlowPage();
		textFlow = new TextFlow();
		textFlow.setLayoutManager(new ParagraphTextLayout(textFlow, ParagraphTextLayout.WORD_WRAP_SOFT));
		textFlow.setFont(SERIF);
		figure.add(textFlow);
		InlineFlow inline = new InlineFlow();
		figure.add(inline);
		InlineFlow inline2 = new InlineFlow();
		inline.add(inline2);
		textFlow2 = new TextFlow();
		textFlow2.setLayoutManager(new ParagraphTextLayout(textFlow2, ParagraphTextLayout.WORD_WRAP_SOFT));
		textFlow2.setFont(SERIF);
		inline2.add(textFlow2);
		runGenericTests();
		runSoftWrappingTests();

		figure = new FlowPage();
		textFlow = new TextFlow();
		textFlow.setLayoutManager(new ParagraphTextLayout(textFlow, ParagraphTextLayout.WORD_WRAP_HARD));
		textFlow.setFont(SERIF);
		figure.add(textFlow);
		inline = new InlineFlow();
		figure.add(inline);
		inline2 = new InlineFlow();
		inline.add(inline2);
		textFlow2 = new TextFlow();
		textFlow2.setLayoutManager(new ParagraphTextLayout(textFlow2, ParagraphTextLayout.WORD_WRAP_HARD));
		textFlow2.setFont(SERIF);
		inline2.add(textFlow2);
		runGenericTests();
		runHardWrappingTests();
		doTest2("def", "def", "defde", new String[] { "def", SAMELINE, "def", TERMINATE }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	}

	@Test
	@SuppressWarnings("static-method")
	public void testSpi() {
		ServiceLoader<BreakIteratorProvider> serviceLoader = ServiceLoader.load(BreakIteratorProvider.class);
		BreakIteratorProvider service = serviceLoader.findFirst().orElse(null);

		assertNotNull(service);
		assertTrue(service instanceof BreakIteratorProviderMock);
	}

	public static class BreakIteratorProviderMock extends BreakIteratorProvider {
		@Override
		public BreakIterator getWordInstance(Locale locale) {
			return BreakIterator.getWordInstance(locale);
		}

		@Override
		public BreakIterator getLineInstance(Locale locale) {
			return BreakIterator.getLineInstance(locale);
		}

		@Override
		public BreakIterator getCharacterInstance(Locale locale) {
			return BreakIterator.getCharacterInstance(locale);
		}

		@Override
		public BreakIterator getSentenceInstance(Locale locale) {
			return BreakIterator.getSentenceInstance(locale);
		}

		@Override
		public Locale[] getAvailableLocales() {
			return BreakIterator.getAvailableLocales();
		}
	}
}