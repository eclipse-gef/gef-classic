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
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DocumentDiagnosticParams;
import org.eclipse.lsp4j.DocumentDiagnosticReport;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.TextDocumentItem;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.zest.dot.core.DOTLanguageServer;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class DOTLanguageServerTest {
	static DOTLanguageServer languageServer;
	static Thread languageServerThread;

	static LanguageClient languageClient;
	static Launcher<DOTLanguageServer> languageClientLauncher;

	static final String URI = "test.dot"; //$NON-NLS-1$

	@BeforeAll
	public static void setUpAll() {
		CountDownLatch latch = new CountDownLatch(1);

		languageServer = new DOTLanguageServer();
		languageServerThread = new Thread(() -> {
			Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(languageServer, System.in, System.out);

			Future<Void> startListing = launcher.startListening();
			languageClient = launcher.getRemoteProxy();
			latch.countDown();
			try {
				startListing.get();
			} catch (ExecutionException e) {
				throw new RuntimeException(e);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		});
		languageServerThread.start();

		try {
			latch.await(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	@AfterAll
	public static void tearDown() {
		languageServerThread.interrupt();
	}

	@Test
	@SuppressWarnings("static-method")
	public void testGraphNoProblem() {
		List<Diagnostic> problems = getDiagnostics("""
				digraph G {
					A -> B;
				}"""); //$NON-NLS-1$
		assertEquals(Collections.emptyList(), problems);
	} 

	@Test
	@SuppressWarnings("static-method")
	public void testGraphProblem() {
		List<Diagnostic> problems = getDiagnostics("""
				digraph G {
					A => B;
				}"""); //$NON-NLS-1$
		assertNotEquals(Collections.emptyList(), problems);
	}

	private static List<Diagnostic> getDiagnostics(String content) {
		try {
			openDocument(content);
			return getDiagnosticReport().getRelatedFullDocumentDiagnosticReport().getItems();
		} finally {
			closeDocument();
		}
	}

	private static DocumentDiagnosticReport getDiagnosticReport() {
		TextDocumentIdentifier document = new TextDocumentIdentifier();
		document.setUri(URI);

		DocumentDiagnosticParams params = new DocumentDiagnosticParams();
		params.setTextDocument(document);

		try {
			return languageServer.getTextDocumentService().diagnostic(params).get(4, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new RuntimeException(e);
		}
	}

	private static void openDocument(String content) {
		TextDocumentItem document = new TextDocumentItem();
		document.setUri(URI);
		document.setText(content);

		DidOpenTextDocumentParams params = new DidOpenTextDocumentParams();
		params.setTextDocument(document);

		languageServer.getTextDocumentService().didOpen(params);
	}

	private static void closeDocument() {
		TextDocumentIdentifier document = new TextDocumentIdentifier();
		document.setUri(URI);

		DidCloseTextDocumentParams params = new DidCloseTextDocumentParams();
		params.setTextDocument(document);

		languageServer.getTextDocumentService().didClose(params);
	}
}
