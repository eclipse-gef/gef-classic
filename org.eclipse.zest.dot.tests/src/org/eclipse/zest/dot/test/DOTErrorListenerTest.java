/*******************************************************************************
 * Copyright (c) 2026 Patrick Ziegler and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *	 Patrick Ziegler - initial API and implementation
 *******************************************************************************/

package org.eclipse.zest.dot.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.List;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.zest.dot.core.internal.DOTDocumentModel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class DOTErrorListenerTest {

	@ParameterizedTest
	@ValueSource(strings = { """
			digraph G {
				A -> B;
				B -> C;
				C -> D;
				D -> A;
			}""", """
			graph G {
				Alice -- Bob;
				Bob -- Charlie;
				Charlie -- David;
				David -- Alice;
				Alice -- Charlie;
			}""", """
			digraph Workflow {
				Start -> Process [label="begin"];
				Process -> Decision;
				Decision -> Success [label="yes"];
				Decision -> Failure [label="no"];
				Failure -> Process [label="retry"];
			}""", """
			digraph Architecture {
				rankdir=LR;

				subgraph cluster_frontend {
					label="Frontend";
					color=blue;

					Browser;
					WebApp;
				}

				subgraph cluster_backend {
					label="Backend";
					color=green;

					API;
					Database;
				}

				Browser -> WebApp;
				WebApp -> API;
				API -> Database;
			}""", """
			digraph BinaryTree {
				node [shape=circle];

				Root -> Left;
				Root -> Right;

				Left -> LeftLeft;
				Left -> LeftRight;

				Right -> RightLeft;
				Right -> RightRight;
			}""", """
			digraph StateMachine {
				rankdir=LR;

				Idle [shape=doublecircle];
				Running;
				Paused;
				Finished [shape=doublecircle];

				Idle -> Running [label="start"];
				Running -> Paused [label="pause"];
				Paused -> Running [label="resume"];
				Running -> Finished [label="complete"];
				Paused -> Finished [label="cancel"];
			}""", """
			digraph Dependencies {
				"Application" -> "Authentication";
				"Application" -> "Database";
				"Application" -> "Logging";

				"Authentication" -> "User Service";
				"Database" -> "PostgreSQL";
				"Logging" -> "Monitoring";

				"User Service" -> "Redis";
			}""" })
	public void testGraphNoProblem(String content) {
		List<Diagnostic> problems = getDiagnostics(content);
		assertEquals(Collections.emptyList(), problems);
	}

	@Test
	public void testGraphUnclosedGraph() {
		List<Diagnostic> problems = getDiagnostics("""
				digraph G {
					A -> B;
					B -> C;"""); //$NON-NLS-1$
		assertEquals(1, problems.size());

		Diagnostic problem = problems.get(0);
		assertEquals("missing '}' at '<EOF>'", getMessage(problem)); //$NON-NLS-1$
		assertEquals(new Position(2, 8), problem.getRange().getStart());
		assertEquals(new Position(2, 9), problem.getRange().getEnd());
	}

	@Test
	public void testGraphUnclosedQuotedString() {
		List<Diagnostic> problems = getDiagnostics("""
				digraph G {
					A [label="Start];
					B;
				}"""); //$NON-NLS-1$
		assertEquals(2, problems.size());

		{
			Diagnostic problem = problems.get(0);
			assertEquals("token recognition error at: '\"Start];\\n\\tB;\\n}'", getMessage(problem)); //$NON-NLS-1$
			assertEquals(new Position(1, 10), problem.getRange().getStart());
			assertEquals(new Position(1, 11), problem.getRange().getEnd());
		}

		{
			Diagnostic problem = problems.get(1);
			assertEquals("mismatched input '<EOF>' expecting {NUMBER, STRING, ID, HTML_STRING}", getMessage(problem)); //$NON-NLS-1$
			assertEquals(new Position(3, 1), problem.getRange().getStart());
			assertEquals(new Position(3, 2), problem.getRange().getEnd());
		}
	}

	@Test
	public void testGraphUnclosedAttribute() {
		List<Diagnostic> problems = getDiagnostics("""
				digraph G {
					A [color=red;
					B -> C;
				}"""); //$NON-NLS-1$
		assertEquals(1, problems.size());

		Diagnostic problem = problems.get(0);
		assertEquals("mismatched input '->' expecting {';', '=', ']', ',', NUMBER, STRING, ID, HTML_STRING}", getMessage(problem)); //$NON-NLS-1$
		assertEquals(new Position(2, 3), problem.getRange().getStart());
		assertEquals(new Position(2, 5), problem.getRange().getEnd());
	}

	@Test
	public void testGraphUnclosedComment() {
		List<Diagnostic> problems = getDiagnostics("""
				digraph G {
					A -> B;
					/* This comment never ends
					B -> C;
				}"""); //$NON-NLS-1$
		assertEquals(2, problems.size());

		{
			Diagnostic problem = problems.get(0);
			assertEquals("token recognition error at: '/* This comment never ends\\n\\tB -> C;\\n}'", getMessage(problem)); //$NON-NLS-1$
			assertEquals(new Position(2, 1), problem.getRange().getStart());
			assertEquals(new Position(2, 2), problem.getRange().getEnd());
		}

		{
			Diagnostic problem = problems.get(1);
			assertEquals("missing '}' at '<EOF>'", getMessage(problem)); //$NON-NLS-1$
			assertEquals(new Position(4, 1), problem.getRange().getStart());
			assertEquals(new Position(4, 2), problem.getRange().getEnd());
		}
	}

	@Test
	public void testGraphUnclosedSubgraph() {
		List<Diagnostic> problems = getDiagnostics("""
				digraph G {
					subgraph cluster_0 {
						A;
						B;
					A -> B;
				}"""); //$NON-NLS-1$
		assertEquals(1, problems.size());

		Diagnostic problem = problems.get(0);
		assertEquals("missing '}' at '<EOF>'", getMessage(problem)); //$NON-NLS-1$
		assertEquals(new Position(5, 1), problem.getRange().getStart());
		assertEquals(new Position(5, 2), problem.getRange().getEnd());
	}

	@Test
	public void testGraphInvalidEdgeOperator() {
		List<Diagnostic> problems = getDiagnostics("""
				digraph G {
					A -> B;
					B => C;
				}"""); //$NON-NLS-1$
		assertEquals(1, problems.size());

		Diagnostic problem = problems.get(0);
		assertEquals("token recognition error at: '>'", getMessage(problem)); //$NON-NLS-1$
		assertEquals(new Position(2, 4), problem.getRange().getStart());
		assertEquals(new Position(2, 5), problem.getRange().getEnd());
	}

	@Test
	public void testGraphInvalidAttributeAssignment() {
		List<Diagnostic> problems = getDiagnostics("""
				digraph G {
					A [color: red];
					B -> C;
				}"""); //$NON-NLS-1$
		assertEquals(2, problems.size());

		{
			Diagnostic problem = problems.get(0);
			assertEquals("mismatched input ':' expecting {';', '=', ']', ',', NUMBER, STRING, ID, HTML_STRING}", getMessage(problem)); //$NON-NLS-1$
			assertEquals(new Position(1, 9), problem.getRange().getStart());
			assertEquals(new Position(1, 10), problem.getRange().getEnd());
		}

		{
			Diagnostic problem = problems.get(1);
			assertEquals("mismatched input ']' expecting {';', '=', ']', ',', NUMBER, STRING, ID, HTML_STRING}", getMessage(problem)); //$NON-NLS-1$
			assertEquals(new Position(1, 14), problem.getRange().getStart());
			assertEquals(new Position(1, 15), problem.getRange().getEnd());
		}
	}

	@Test
	public void testGraphInvalidAttributeBracket() {
		List<Diagnostic> problems = getDiagnostics("""
				digraph G {
					A [color=red);
					B -> C;
				}"""); //$NON-NLS-1$
		assertEquals(2, problems.size());

		{
			Diagnostic problem = problems.get(0);
			assertEquals("token recognition error at: ')'", getMessage(problem)); //$NON-NLS-1$
			assertEquals(new Position(1, 13), problem.getRange().getStart());
			assertEquals(new Position(1, 14), problem.getRange().getEnd());
		}

		{
			Diagnostic problem = problems.get(1);
			assertEquals("mismatched input '->' expecting {';', '=', ']', ',', NUMBER, STRING, ID, HTML_STRING}", getMessage(problem)); //$NON-NLS-1$
			assertEquals(new Position(2, 3), problem.getRange().getStart());
			assertEquals(new Position(2, 5), problem.getRange().getEnd());
		}
	}

	@Test
	public void testGraphInvalidDirectEdgeSyntax() {
		List<Diagnostic> problems = getDiagnostics("""
				digraph G {
					A -> -> B;
					C -> D;
				}"""); //$NON-NLS-1$
		assertEquals(1, problems.size());

		Diagnostic problem = problems.get(0);
		assertEquals("extraneous input '->' expecting {'{', 'subgraph', NUMBER, STRING, ID, HTML_STRING}", getMessage(problem)); //$NON-NLS-1$
		assertEquals(new Position(1, 6), problem.getRange().getStart());
		assertEquals(new Position(1, 8), problem.getRange().getEnd());
	}

	@Test
	public void testGraphInvalidPortSyntax() {
		List<Diagnostic> problems = getDiagnostics("""
				digraph G {
					A: -> B;
					C -> D;
				}"""); //$NON-NLS-1$
		assertEquals(1, problems.size());

		Diagnostic problem = problems.get(0);
		assertEquals("no viable alternative at input 'A:->'", getMessage(problem)); //$NON-NLS-1$
		assertEquals(new Position(1, 4), problem.getRange().getStart());
		assertEquals(new Position(1, 6), problem.getRange().getEnd());
	}

	private static String getMessage(Diagnostic problem) {
		Object o = problem.getMessage();
		if (o instanceof String string) { // LSP4J 0.x
			return string;
		}
		if (o instanceof Either either) { // LSP4j 1.x
			return (String) either.getLeft();
		}
		return null;
	}

	@SuppressWarnings("static-method")
	private List<Diagnostic> getDiagnostics(String content) {
		return new DOTDocumentModel(content).getProblems();
	}
}
