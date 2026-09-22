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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.DocumentDiagnosticParams;
import org.eclipse.lsp4j.DocumentDiagnosticReport;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.RelatedFullDocumentDiagnosticReport;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.TextDocumentService;

/**
 * Document service for managing DOT files.
 */
public class DOTTextDocumentService implements TextDocumentService {
	private final Map<String, DOTDocumentModel> models = new HashMap<>();
	private LanguageClient proxyClient;

	public void setRemoteProxy(LanguageClient proxyClient) {
		this.proxyClient = proxyClient;
	}

	@Override
	public CompletableFuture<DocumentDiagnosticReport> diagnostic(DocumentDiagnosticParams params) {
		DOTDocumentModel model = models.get(params.getTextDocument().getUri());

		if (model == null) {
			RelatedFullDocumentDiagnosticReport report = new RelatedFullDocumentDiagnosticReport();
			return CompletableFuture.completedFuture(new DocumentDiagnosticReport(report));
		}

		return CompletableFuture.supplyAsync(() -> {
			RelatedFullDocumentDiagnosticReport report = new RelatedFullDocumentDiagnosticReport(model.getProblems());
			return new DocumentDiagnosticReport(report);
		});
	}

	@Override
	public void didOpen(DidOpenTextDocumentParams params) {
		updateModel(params.getTextDocument().getUri(), params.getTextDocument().getText());
	}

	@Override
	public void didChange(DidChangeTextDocumentParams params) {
		updateModel(params.getTextDocument().getUri(), params.getContentChanges().get(0).getText());
	}

	@Override
	public void didClose(DidCloseTextDocumentParams params) {
		models.remove(params.getTextDocument().getUri());
	}

	@Override
	public void didSave(DidSaveTextDocumentParams params) {
		// nothing to do
	}

	/**
	 * Called whenever a document is opend and/or updated. It creates an internal
	 * representation of the document and publishes all detect problems.
	 *
	 * @param uri  The document location.
	 * @param text The document content.
	 */
	private void updateModel(String uri, String text) {
		DOTDocumentModel model = new DOTDocumentModel(text);
		models.put(uri, model);

		if (proxyClient != null) {
			PublishDiagnosticsParams params = new PublishDiagnosticsParams();
			params.setDiagnostics(model.getProblems());
			params.setUri(uri);
			proxyClient.publishDiagnostics(params);
		}
	}
}
