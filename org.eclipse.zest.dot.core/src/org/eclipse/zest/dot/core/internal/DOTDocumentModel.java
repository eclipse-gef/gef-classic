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
 *     Patrick Ziegler - initial API and implementation
 *******************************************************************************/

package org.eclipse.zest.dot.core.internal;

import java.util.Collections;
import java.util.List;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.zest.dot.core.internal.antlr4.DOTErrorListener;
import org.eclipse.zest.dot.core.internal.antlr4.DOTLexer;
import org.eclipse.zest.dot.core.internal.antlr4.DOTParser;
import org.eclipse.zest.dot.core.internal.antlr4.DOTParser.GraphContext;
import org.eclipse.zest.dot.core.internal.antlr4.DOTZestModelVisitor;

import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStreams;

/**
 * Internal representation of a DOT file in the language server.
 */
public class DOTDocumentModel {
	private final List<Diagnostic> problems;
	private final DOTZestGraphModel graph;

	/**
	 * Creates a new document model.
	 *
	 * @param text The content of a DOT file.
	 */
	public DOTDocumentModel(String text) {
		final DOTErrorListener listener = new DOTErrorListener();

		DOTLexer lexer = new DOTLexer(CharStreams.fromString(text));
		lexer.addErrorListener(listener);

		DOTParser parser = new DOTParser(new BufferedTokenStream(lexer));
		parser.addErrorListener(listener);

		final GraphContext graphContext = parser.graph();

		problems = listener.getProblems();

		final DOTZestModelVisitor visitor = new DOTZestModelVisitor();

		graphContext.accept(visitor);

		graph = visitor.getGraphModel();
	}

	/**
	 * Returns all problems that were encountered while parsing the source code.
	 *
	 * @return An unmodifiable list of lexer/parser problems.
	 */
	public List<Diagnostic> getProblems() {
		return Collections.unmodifiableList(problems);
	}

	/**
	 * Returns the Zest model for this graph.
	 *
	 * @return The graph model.
	 */
	public DOTZestGraphModel getGraph() {
		return graph;
	}
}
