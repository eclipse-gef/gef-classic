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

package org.eclipse.zest.dot.core.internal.antlr4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;

/**
 * Custom ANTLR4 listener which collects all problems encountered while parsing
 * a DOT document and converts them to LSP4j diagnostic objects.
 */
public class DOTErrorListener extends BaseErrorListener {
	private final List<Diagnostic> problems = new ArrayList<>();

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
		int startLine = line - 1;
		int startCharacter = charPositionInLine;

		int endLine = startLine;
		int endCharacter = startCharacter + 1;

		if (offendingSymbol instanceof Token token) {
			startLine = token.getLine() - 1;
			startCharacter = token.getCharPositionInLine();

			// ANTLR's stopIndex is inclusive
			if (token.getStartIndex() >= 0 && token.getStopIndex() >= token.getStartIndex()) {
				endCharacter = startCharacter + (token.getStopIndex() - token.getStartIndex() + 1);
			}
		}

		Range range = new Range(new Position(startLine, startCharacter), new Position(endLine, endCharacter));

		Diagnostic diagnostic = new Diagnostic();
		diagnostic.setRange(range);
		diagnostic.setMessage(msg);
		diagnostic.setSeverity(DiagnosticSeverity.Error);
		diagnostic.setSource("antlr"); //$NON-NLS-1$
		problems.add(diagnostic);
	}

	/**
	 * This method should only be called after the document has been parsed.
	 *
	 * @return An unmodifiable list of all diagnostic problems.
	 */
	public List<Diagnostic> getProblems() {
		return Collections.unmodifiableList(problems);
	}
}
