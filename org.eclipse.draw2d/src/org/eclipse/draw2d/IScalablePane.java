/*******************************************************************************
 * Copyright (c) 2022, 2025 Johannes Kepler University Linz and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alois Zoitl - initial API and implementation
 *******************************************************************************/

package org.eclipse.draw2d;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import org.eclipse.swt.SWT;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.geometry.Translatable;

/**
 * Interface for scaleable panes which provides the default functionality for
 * deciding if a scaled graphics or the given graphics should be used for
 * drawing.
 *
 * @since 3.13
 */
public interface IScalablePane extends ScalableFigure {

	boolean useScaledGraphics();

	boolean optimizeClip();

	public default Rectangle getScaledRect(Rectangle rect) {
		double scale = getScale();
		rect.scale(1 / scale);
		return rect;
	}

	public static final class IScalablePaneHelper {

		private static Graphics prepareScaledGraphics(final Graphics graphics, IScalablePane figurePane) {
			Graphics graphicsToUse = (figurePane.useScaledGraphics()) ? new ScaledGraphics(graphics) : graphics;
			if (!figurePane.optimizeClip()) {
				graphicsToUse.clipRect(figurePane.getBounds().getShrinked(figurePane.getInsets()));
			}
			graphicsToUse.scale(figurePane.getScale());
			graphicsToUse.pushState();
			return graphicsToUse;
		}

		private static void cleanupScaledGraphics(final Graphics graphics, final Graphics graphicsUsed) {
			graphicsUsed.popState();

			if (graphicsUsed != graphics) {
				graphicsUsed.dispose();
			}
			graphics.restoreState();
		}

		static Rectangle getClientArea(IScalablePane figurePane, Function<Rectangle, Rectangle> superMethod,
				Rectangle rect) {
			return figurePane.getScaledRect(superMethod.apply(rect));
		}

		static Dimension getMinimumSize(IScalablePane figurePane, BiFunction<Integer, Integer, Dimension> superMethod,
				int wHint, int hHint) {
			Dimension d = superMethod.apply(getScaledHint(figurePane, wHint), getScaledHint(figurePane, hHint));
			int w = figurePane.getInsets().getWidth();
			int h = figurePane.getInsets().getHeight();
			return d.getExpanded(-w, -h).scale(figurePane.getScale()).expand(w, h);
		}

		static Dimension getPreferredSize(IScalablePane figurePane, BiFunction<Integer, Integer, Dimension> superMethod,
				int wHint, int hHint) {
			Dimension d = superMethod.apply(getScaledHint(figurePane, wHint), getScaledHint(figurePane, hHint));
			int w = figurePane.getInsets().getWidth();
			int h = figurePane.getInsets().getHeight();
			return d.getExpanded(-w, -h).scale(figurePane.getScale()).expand(w, h);
		}

		private static int getScaledHint(IScalablePane figurePane, int hint) {
			return hint != SWT.DEFAULT ? (int) (hint / figurePane.getScale()) : SWT.DEFAULT;
		}

		static void paintClientArea(IScalablePane figurePane, Consumer<Graphics> superMethod, Graphics graphics) {
			if (figurePane.getChildren().isEmpty()) {
				return;
			}

			if (figurePane.getScale() == 1.0) {
				superMethod.accept(graphics);
			} else {
				Graphics graphicsToUse = prepareScaledGraphics(graphics, figurePane);
				// Even though paintChildren() is defined in the Figure class, this cast is
				// still safe because this method is package-private and only called from
				// IFigures extending Figure.
				((Figure) figurePane).paintChildren(graphicsToUse);
				cleanupScaledGraphics(graphics, graphicsToUse);
			}
		}

		static void translateToParent(IScalablePane figurePane, Translatable t) {
			t.performScale(figurePane.getScale());
		}

		static void translateFromParent(IScalablePane figurePane, Translatable t) {
			t.performScale(1 / figurePane.getScale());
		}

		private IScalablePaneHelper() {
			throw new UnsupportedOperationException("Helper class IScalablePaneHelper should not be instantiated"); //$NON-NLS-1$
		}
	}
}
