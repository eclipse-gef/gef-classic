/*******************************************************************************
 * Copyright (c) 2024, 2025 Patrick Ziegler and others.
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

package org.eclipse.zest.tests.examples;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.internal.GraphLabel;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.zest.tests.examples.AbstractGraphTest.SWTBotExtension;
import org.eclipse.zest.tests.utils.ISWTBotGraphContainer;
import org.eclipse.zest.tests.utils.SWTBotGraph;
import org.eclipse.zest.tests.utils.SWTBotGraphConnection;
import org.eclipse.zest.tests.utils.SWTBotGraphNode;
import org.eclipse.zest.tests.utils.Snippet;

import org.eclipse.draw2d.EventDispatcher;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.PopUpHelper;
import org.eclipse.draw2d.SWTEventDispatcher;
import org.eclipse.draw2d.ToolTipHelper;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.InvocationInterceptor.Invocation;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

/**
 * Abstract base class for all tests related to the Zest examples.
 */
@ExtendWith(SWTBotExtension.class)
public abstract class AbstractGraphTest {
	private Graph graph;
	private Shell shell;
	protected SWTBot robot;
	protected SWTBotGraph graphRobot;

	public static class SWTBotExtension implements InvocationInterceptor {
		@Override
		public void interceptTestMethod(Invocation<Void> invocation,
				ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext)
				throws Throwable {
			Object target = invocationContext.getTarget().orElse(null);
			if (target instanceof AbstractGraphTest testObject) {
				Snippet annotation = invocationContext.getExecutable().getAnnotation(Snippet.class);
				Objects.requireNonNull(annotation, "Test is missing @Snippet annotation."); //$NON-NLS-1$
				testObject.doTest(annotation, invocation);
			}
		}
	}

	@AfterEach
	public void tearDown() {
		if (shell != null) {
			UIThreadRunnable.syncExec(shell::dispose);
		}
	}

	/**
	 * Wrapper method to handle the instantiation of the example class and the
	 * execution of the unit test. Each example must satisfy the following
	 * requirements:
	 * <ul>
	 * <li>It must have a static main(String[]) method.</li>
	 * <li>It must not create a display.</li>
	 * <li>It must store the created viewer in a static {@code viewer} variable.
	 * <ul>
	 *
	 * @param statement The test to execute once the example has been created.
	 * @throws Throwable If the example could not be instantiated.
	 */
	private void doTest(Snippet annotation, Invocation<Void> statement) throws Throwable {
		if (Display.getCurrent() != null) {
			fail("""
					SWTBot test needs to run in a non-UI thread.
					Make sure that "Run in UI thread" is unchecked in your launch configuration or that useUIThread is set to false in the pom.xml
					"""); //$NON-NLS-1$
		}

		Class<?> clazz = annotation.type();

		Semaphore lock = new Semaphore(0);
		AtomicReference<Throwable> throwable = new AtomicReference<>();

		Lookup lookup = MethodHandles.privateLookupIn(clazz, MethodHandles.lookup());
		MethodType type = MethodType.methodType(void.class, String[].class);
		MethodHandle methodHandle = lookup.findStatic(clazz, "main", type); //$NON-NLS-1$

		// Fail early, otherwise the example might block indefinitely
		Assert.isTrue(hasGraph(lookup, annotation), "Graph object not found for " + clazz); //$NON-NLS-1$

		// Create snippet
		Display.getDefault().asyncExec(() -> {
			// Non-Blocking! Executed after the widget has been created
			Display.getCurrent().asyncExec(() -> {
				try {
					graph = getGraph(lookup, annotation);

					// Make sure the layout is reproducible
					if (graph.getLayoutAlgorithm() instanceof SpringLayoutAlgorithm springLayout) {
						springLayout.setRandom(annotation.random());
					}

					graphRobot = new SWTBotGraph(graph);
					shell = graph.getShell();
					robot = new SWTBot(shell);
					// Wait for layout to be applied
					waitEventLoop(10);
				} catch (Throwable e) {
					throwable.set(e);
				} finally {
					lock.release();
				}
			});
			// Blocking! Creates the snippet
			try {
				methodHandle.invoke(null);
			} catch (Throwable e) {
				throwable.set(e);
			} finally {
				lock.release();
			}
		});

		// Wait for shell to be created
		lock.acquire();
		// Propagate any errors
		if (throwable.get() != null) {
			throw throwable.get();
		}

		// Run the actual test
		statement.proceed();
	}

	/**
	 * Checks whether the example class can be tested.
	 *
	 * @param lookup  The lookup object on the example class.
	 * @param snippet The annotation of the executed test.
	 * @return {@code true}, if the graph instance of the example is accessible.
	 * @throws ReflectiveOperationException If the graph could not
	 */
	protected abstract boolean hasGraph(Lookup lookup, Snippet snippet) throws ReflectiveOperationException;

	/**
	 * Returns the graph instance created by the current Zest example. This instance
	 * is expected to be stored in a static variable.
	 *
	 * @param lookup  The lookup object on the example class.
	 * @param snippet The annotation of the executed test.
	 * @return The graph object created by the example.
	 * @throws ReflectiveOperationException If the graph could not
	 */
	protected abstract Graph getGraph(Lookup lookup, Snippet snippet) throws ReflectiveOperationException;

	/**
	 * The distance is defined as {@code sqrt(x^2 + y^2) } where {@code x} and
	 * {@code y} are the coordinates of the vector pointing from the {@code source}
	 * to the {@code destination} node.
	 *
	 * @return The Euclidean length of the given {@code connection}.
	 */
	protected static double getLength(SWTBotGraphConnection connection) {
		Point c1 = getCenter(connection.getSource());
		Point c2 = getCenter(connection.getDestination());
		int x = c1.x - c2.x;
		int y = c1.y - c2.y;
		Point vec = new Point(x, y);
		return getLength(vec);
	}

	/**
	 * The length of a vector is defined as {@code sqrt(x^2 + y^2) }.
	 *
	 * @return The Euclidean length of the given {@code vector}.
	 */
	protected static double getLength(Point vec) {
		return Math.sqrt(vec.x * vec.x + vec.y * vec.y);
	}

	/**
	 * The center is defined as {@code (x + width / 2, y + height / 2)}.
	 *
	 * @return The center of the given {@code node}.
	 */
	protected static Point getCenter(SWTBotGraphNode node) {
		Point location = node.getLocation();
		Dimension size = node.getSize();
		return new Rectangle(location, size).getCenter();
	}

	/**
	 * Returns the tooltip that is shown for the given figure. The tooltip is
	 * accessed via reflection by first going through the {@link EventDispatcher},
	 * followed by the {@link ToolTipHelper}.
	 *
	 * @param figure The node figure beneath the mouse cursor.
	 * @return The tooltip of the node figure that is currently being shown.
	 */
	protected static IFigure getToolTip(IFigure figure) throws Throwable {
		EventDispatcher eventDispatcher = figure.internalGetEventDispatcher();

		Lookup lookup1 = MethodHandles.privateLookupIn(SWTEventDispatcher.class, MethodHandles.lookup());
		MethodHandle getter1 = lookup1.findGetter(SWTEventDispatcher.class, "toolTipHelper", ToolTipHelper.class); //$NON-NLS-1$
		ToolTipHelper toolTipHelper = (ToolTipHelper) getter1.invoke(eventDispatcher);

		Lookup lookup2 = MethodHandles.privateLookupIn(PopUpHelper.class, MethodHandles.lookup());
		MethodHandle getter2 = lookup2.findGetter(PopUpHelper.class, "lws", LightweightSystem.class); //$NON-NLS-1$
		LightweightSystem lws = (LightweightSystem) getter2.invoke(toolTipHelper);

		return lws.getRootFigure().getChildren().get(0);
	}

	/**
	 * Returns the fish-eye figure at the given coordinates. Note that those figures
	 * are on a separate layer.
	 *
	 * @param x The x coordinate of the fish-eye figure.
	 * @param y The x coordinate of the fish-eye figure.
	 */
	protected GraphLabel getFishEyeFigure(int x, int y) {
		IFigure fishEyeLayer = graph.getRootLayer().getChildren().get(1);
		return (GraphLabel) fishEyeLayer.findFigureAt(x, y);
	}

	/**
	 * Asserts that the given graph node has the expected name.
	 *
	 * @param node The graph node to validate.
	 * @param text The expected name of the graph node.
	 */
	protected static void assertNode(GraphNode node, String text) {
		assertEquals(node.getText(), text);
	}

	/**
	 * Asserts that the given {@code connection} has the expected source and
	 * destination nodes.
	 *
	 * @param connection  The graph connection to validate.
	 * @param source      The name of the expected source node.
	 * @param destination The name of the expected destination node.
	 */
	protected static void assertConnection(GraphConnection connection, String source, String destination) {
		assertEquals(connection.getSource().getText(), source);
		assertEquals(connection.getDestination().getText(), destination);
	}

	/**
	 * Asserts that the given {@code connection} uses a {@link PolylineConnection}
	 * with given {@code curveDepth}.
	 *
	 * @param bot        The graph connection to validate.
	 * @param curveDepth The expected curveDepth of the connection
	 */
	protected static void assertCurve(SWTBotGraphConnection bot, int curveDepth) throws ReflectiveOperationException {
		GraphConnection connection = bot.widget;
		MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(connection.getClass(), MethodHandles.lookup());
		VarHandle field = lookup.findVarHandle(connection.getClass(), "curveDepth", int.class); //$NON-NLS-1$
		assertEquals(curveDepth, field.get(connection), "Unexpected connection curve"); //$NON-NLS-1$
	}

	/**
	 * Asserts that no graph nodes in the given {@link ISWTBotGraphContainer}
	 * intersect with one another. This method doesn't check nested containers.
	 *
	 * @param container The {@link ISWTBotGraphContainer} to validate.
	 */
	protected static void assertNoOverlap(ISWTBotGraphContainer container) {
		List<SWTBotGraphNode> nodes = container.getNodes();
		for (int i = 0; i < nodes.size(); ++i) {
			for (int j = i + 1; j < nodes.size(); ++j) {
				SWTBotGraphNode node1 = nodes.get(i);
				Rectangle bounds1 = new Rectangle(node1.getLocation(), node1.getSize());
				SWTBotGraphNode node2 = nodes.get(j);
				Rectangle bounds2 = new Rectangle(node2.getLocation(), node2.getSize());
				assertFalse(bounds1.intersects(bounds2));
			}
		}
	}

	/**
	 * Asserts that the given {@code object} is of type {@code class}. Subclasses
	 * are allowed.
	 *
	 * @param object The object to validate.
	 * @param clazz  The expected class of the describing {@link IFigure}.
	 */
	protected static void assertInstanceOf(Object object, Class<?> clazz) {
		assertTrue(clazz.isAssignableFrom(object.getClass()));
	}

	/**
	 * Pumps the event loop for the given number of milliseconds. At least one
	 * events loop will be executed.
	 */
	private static void waitEventLoop(int time) {
		long start = System.currentTimeMillis();
		do {
			while (Display.getCurrent().readAndDispatch()) {
				Thread.yield();
			}
		} while (System.currentTimeMillis() - start < time);
	}
}
