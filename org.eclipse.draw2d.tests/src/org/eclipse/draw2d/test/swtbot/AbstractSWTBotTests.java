/*******************************************************************************
 * Copyright (c) 2024 Patrick Ziegler and others.
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

package org.eclipse.draw2d.test.swtbot;

import static org.junit.jupiter.api.Assertions.fail;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCanvas;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.test.swtbot.AbstractSWTBotTests.SWTBotExtension;
import org.eclipse.draw2d.test.utils.Snippet;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.InvocationInterceptor.Invocation;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

/**
 * To be able to execute these tests in the Eclipse IDE, the tests must
 * <b>NOT</b> be run in the UI thread.
 */
@SuppressWarnings("nls")
@ExtendWith(SWTBotExtension.class)
public abstract class AbstractSWTBotTests {
	protected SWTBotCanvas bot;
	protected FigureCanvas canvas;
	protected IFigure root;

	public static class SWTBotExtension implements InvocationInterceptor {
		@Override
		public void interceptTestMethod(Invocation<Void> invocation,
				ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext)
				throws Throwable {
			Object target = invocationContext.getTarget().orElse(null);
			if (target instanceof AbstractSWTBotTests testObject) {
				Snippet annotation = invocationContext.getExecutable().getAnnotation(Snippet.class);
				Objects.requireNonNull(annotation, "Test is missing @Snippet annotation."); //$NON-NLS-1$
				testObject.doTest(annotation, invocation);
			}
		}
	}

	/**
	 * Wrapper method to handle the instantiation of the example class and the
	 * execution of the unit test. Each example must satisfy the following
	 * requirements:
	 * <ul>
	 * <li>It must have a static main(String[]) method.</li>
	 * <li>It must not create a display.</li>
	 * <li>It must store the created {@link Shell} in a static variable.
	 * <ul>
	 *
	 * @param annotation The annotation containing the class to test.
	 * @param statement  The test to execute once the example has been created.
	 * @throws Throwable If the example could not be instantiated.
	 */
	protected void doTest(Snippet annotation, Invocation<Void> statement) throws Throwable {
		if (Display.getCurrent() != null) {
			fail("""
					SWTBot test needs to run in a non-UI thread.
					Make sure that "Run in UI thread" is unchecked in your launch configuration or that useUIThread is set to false in the pom.xml
					""");
		}

		Class<?> clazz = annotation.type();
		Semaphore lock = new Semaphore(0);
		AtomicReference<Throwable> throwable = new AtomicReference<>();
		Lookup lookup = MethodHandles.privateLookupIn(clazz, MethodHandles.lookup());

		// Fail early, otherwise the example might block indefinitely
		Assert.isTrue(hasShell(lookup, annotation), "Shell not found for " + clazz); //$NON-NLS-1$

		// The widget needs to be explicitly created in the UI thread. Note that the
		// lock is released by the test once it invokes "readAndDispatch()".
		Display.getDefault().asyncExec(() -> {
			try {
				Display.getCurrent().asyncExec(lock::release);
				createInstance(lookup, annotation);
			} catch (Throwable e) {
				throwable.set(e);
			}
		});

		// Run the actual test
		Shell shell = null;
		try {
			// Wait for example to initialize
			lock.acquire();

			shell = getShell(lookup, annotation);

			// Propagate any errors
			if (throwable.get() != null) {
				throw throwable.get();
			}

			bot = new SWTBot(shell).canvas(1); // 0 is the shell itself
			canvas = (FigureCanvas) bot.widget;
			root = canvas.getLightweightSystem().getRootFigure();
			// Run the actual test
			statement.proceed();
		} finally {
			// Close the snippet
			if (shell != null) {
				Display.getDefault().syncExec(shell::dispose);
			}
			lock.release();
		}
	}

	private static boolean hasShell(Lookup lookup, Snippet snippet) throws ReflectiveOperationException {
		try {
			getShell(lookup, snippet);
			return true;
		} catch (NoSuchFieldException ignore) {
			return false;
		}
	}

	private static Shell getShell(Lookup lookup, Snippet snippet) throws ReflectiveOperationException {
		VarHandle shell = lookup.findStaticVarHandle(snippet.type(), snippet.field(), Shell.class);
		return (Shell) shell.get();
	}

	private static void createInstance(Lookup lookup, Snippet snippet) throws Throwable {
		MethodType type = MethodType.methodType(void.class, String[].class);
		MethodHandle methodHandle = lookup.findStatic(snippet.type(), "main", type); //$NON-NLS-1$
		methodHandle.invoke(null);
	}

}
