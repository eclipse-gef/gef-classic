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

package org.eclipse.zest.dot.ui.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.Future;

import org.eclipse.lsp4e.server.StreamConnectionProvider;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.zest.dot.core.DOTLanguageServer;

import org.eclipse.draw2d.internal.Logger;

public final class DOTConnectionProvider implements StreamConnectionProvider {
	private static final Logger LOGGER = Logger.getLogger(DOTConnectionProvider.class);
	private final DOTLanguageServer languageServer = new DOTLanguageServer();

	private PipedOutputStream clientOutput;
	private PipedInputStream serverInput;

	private PipedInputStream clientInput;
	private PipedOutputStream serverOutput;

	private Future<Void> listening;

	@Override
	public void start() throws IOException {
		clientOutput = new PipedOutputStream();
		serverInput = new PipedInputStream(clientOutput);

		serverOutput = new PipedOutputStream();
		clientInput = new PipedInputStream(serverOutput);

		// stop() is not called during normal shutdown and the streams never closed
		PlatformUI.getWorkbench().addWorkbenchListener(new IWorkbenchListener() {
			@Override
			public boolean preShutdown(IWorkbench workbench, boolean forced) {
				return true;
			}

			@Override
			public void postShutdown(IWorkbench workbench) {
				stop();
			}
		});

		Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(languageServer, serverInput, serverOutput);

		languageServer.setRemoteProxy(launcher.getRemoteProxy());

		listening = launcher.startListening();
	}

	@Override
	public InputStream getInputStream() {
		return clientInput;
	}

	@Override
	public OutputStream getOutputStream() {
		return clientOutput;
	}

	@Override
	public InputStream getErrorStream() {
		return null;
	}

	@Override
	public void stop() {
		listening.cancel(true);

		try {
			clientOutput.close();
			serverOutput.close();
			clientInput.close();
			serverInput.close();
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}

	}
}
