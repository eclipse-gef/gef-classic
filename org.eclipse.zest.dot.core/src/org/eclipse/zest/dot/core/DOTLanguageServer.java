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

package org.eclipse.zest.dot.core;

import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.DiagnosticRegistrationOptions;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.lsp4j.TextDocumentSyncKind;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;
import org.eclipse.zest.dot.core.internal.DOTTextDocumentService;
import org.eclipse.zest.dot.core.internal.DOTWorkspaceService;

/**
 * This class implements a language server for the DOT language.
 *
 * @see <a href="https://graphviz.org/doc/info/lang.html">DOT Language</a>
 * @noextend This class is not intended to be subclassed by clients.
 */
public class DOTLanguageServer implements LanguageServer {

	private final DOTTextDocumentService textDocumentService;
	private final DOTWorkspaceService workspaceService;
	private boolean shutdownRequestReceived;

	public DOTLanguageServer() {
		this(new DOTTextDocumentService(), new DOTWorkspaceService());
	}

	protected DOTLanguageServer(DOTTextDocumentService textDocumentService, DOTWorkspaceService workspaceService) {
		this.textDocumentService = textDocumentService;
		this.workspaceService = workspaceService;
	}

	@Override
	public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
		ServerCapabilities capabilities = new ServerCapabilities();
		capabilities.setTextDocumentSync(TextDocumentSyncKind.Full);
		capabilities.setDiagnosticProvider(new DiagnosticRegistrationOptions());
		return CompletableFuture.completedFuture(new InitializeResult(capabilities));
	}

	@Override
	public TextDocumentService getTextDocumentService() {
		return textDocumentService;
	}

	@Override
	public WorkspaceService getWorkspaceService() {
		return workspaceService;
	}

	@Override
	public CompletableFuture<Object> shutdown() {
		shutdownRequestReceived = true;
		return CompletableFuture.completedFuture(null);
	}

	@Override
	public void exit() {
		if (shutdownRequestReceived) {
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Sets the proxy for the language client registered to this server.
	 *
	 * @param proxyClient The language client.
	 */
	public void setRemoteProxy(LanguageClient proxyClient) {
		this.textDocumentService.setRemoteProxy(proxyClient);
	}
}
