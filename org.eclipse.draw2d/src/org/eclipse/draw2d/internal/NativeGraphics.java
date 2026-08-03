/*******************************************************************************
 * Copyright (c) 2000, 2026 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.draw2d.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.LineAttributes;
import org.eclipse.swt.graphics.Pattern;

import org.eclipse.pde.api.tools.annotations.NoExtend;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * The GC-agnostic default implementation of the {@link Graphics} class.
 *
 * @noextend This class is not intended to be subclassed by clients.
 */
@NoExtend
public abstract class NativeGraphics extends Graphics {

	/**
	 * An internal type used to represent and update the GC's clipping.
	 */
	protected static interface Clipping {
		/**
		 * Sets the clip's bounding rectangle into the provided argument and returns it
		 * for convenience.
		 *
		 * @param rect the rect
		 * @return the given rect
		 */
		Rectangle getBoundingBox(Rectangle rect);

		Clipping getCopy();

		void intersect(int left, int top, int right, int bottom);

		void scale(float horizontal, float vertical);

		void translate(float dx, float dy);
	}

	/**
	 * Any state stored in this class is only applied when it is needed by a
	 * specific graphics call.
	 */
	protected static class LazyState {
		public Color bgColor;
		public Color fgColor;
		public Font font;
		public int graphicHints;
		public LineAttributes lineAttributes;
		public Clipping relativeClip;
	}

	protected static abstract class RectangleClipping implements Clipping {

		public float top;
		public float left;
		public float bottom;
		public float right;

		protected RectangleClipping(float left, float top, float right, float bottom) {
			this.left = left;
			this.right = right;
			this.bottom = bottom;
			this.top = top;
		}

		protected RectangleClipping(Rectangle rect) {
			this(rect.x, rect.y, rect.right(), rect.bottom());
		}

		@Override
		public Rectangle getBoundingBox(Rectangle rect) {
			rect.x = (int) left;
			rect.y = (int) top;
			rect.width = (int) Math.ceil(right) - rect.x;
			rect.height = (int) Math.ceil(bottom) - rect.y;
			return rect;
		}

		@Override
		public void intersect(int left, int top, final int right, final int bottom) {
			this.left = Math.max(this.left, left);
			this.right = Math.min(this.right, right);
			this.top = Math.max(this.top, top);
			this.bottom = Math.min(this.bottom, bottom);
			// use left/top -1 to ensure ceiling function doesn't add a pixel
			if (this.right < this.left || this.bottom < this.top) {
				this.right = this.left - 1;
				this.bottom = this.top - 1;
			}
		}

		@Override
		public void scale(float horz, float vert) {
			left /= horz;
			right /= horz;
			top /= vert;
			bottom /= vert;
		}

		@Override
		public void translate(float dx, float dy) {
			left += dx;
			right += dx;
			top += dy;
			bottom += dy;
		}
	}

	/**
	 * Contains the entire state of the Graphics.
	 */
	protected static class State extends LazyState implements Cloneable {
		public float[] affineMatrix;
		public int alpha;
		public Pattern bgPattern;
		public int dx;
		public int dy;
		public float sx;
		public float sy;

		public Pattern fgPattern;

		@Override
		public State clone() {
			try {
				State clone = (State) super.clone();
				clone.lineAttributes = SWTGraphics.clone(clone.lineAttributes);
				return clone;
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			}
		}

		/**
		 * Copies all state information from the given State to this State
		 *
		 * @param state The State to copy from
		 */
		public void copyFrom(State state) {
			bgColor = state.bgColor;
			fgColor = state.fgColor;
			lineAttributes = SWTGraphics.clone(state.lineAttributes);
			dx = state.dx;
			dy = state.dy;
			sx = state.sx;
			sy = state.sy;
			bgPattern = state.bgPattern;
			fgPattern = state.fgPattern;
			font = state.font;
			graphicHints = state.graphicHints;
			affineMatrix = state.affineMatrix;
			relativeClip = state.relativeClip;
			alpha = state.alpha;
		}
	}

	protected static final int AA_MASK;
	protected static final int AA_SHIFT;
	protected static final int AA_WHOLE_NUMBER = 1;
	protected static final int ADVANCED_GRAPHICS_MASK;
	protected static final int ADVANCED_HINTS_DEFAULTS;
	protected static final int ADVANCED_HINTS_MASK;
	protected static final int ADVANCED_SHIFT;
	protected static final int FILL_RULE_MASK;
	protected static final int FILL_RULE_SHIFT;
	protected static final int FILL_RULE_WHOLE_NUMBER = -1;
	protected static final int INTERPOLATION_MASK;
	protected static final int INTERPOLATION_SHIFT;
	protected static final int INTERPOLATION_WHOLE_NUMBER = 1;

	protected static final int TEXT_AA_MASK;
	protected static final int TEXT_AA_SHIFT;
	protected static final int XOR_MASK;
	protected static final int XOR_SHIFT;

	static {
		XOR_SHIFT = 3;
		AA_SHIFT = 8;
		TEXT_AA_SHIFT = 10;
		INTERPOLATION_SHIFT = 12;
		FILL_RULE_SHIFT = 14;
		ADVANCED_SHIFT = 15;

		AA_MASK = 3 << AA_SHIFT;
		// If changed to more than 1-bit, check references!
		FILL_RULE_MASK = 1 << FILL_RULE_SHIFT;
		INTERPOLATION_MASK = 3 << INTERPOLATION_SHIFT;
		TEXT_AA_MASK = 3 << TEXT_AA_SHIFT;
		XOR_MASK = 1 << XOR_SHIFT;
		ADVANCED_GRAPHICS_MASK = 1 << ADVANCED_SHIFT;

		ADVANCED_HINTS_MASK = TEXT_AA_MASK | AA_MASK | INTERPOLATION_MASK;
		ADVANCED_HINTS_DEFAULTS = ((SWT.DEFAULT + AA_WHOLE_NUMBER) << TEXT_AA_SHIFT)
				| ((SWT.DEFAULT + AA_WHOLE_NUMBER) << AA_SHIFT)
				| ((SWT.DEFAULT + INTERPOLATION_WHOLE_NUMBER) << INTERPOLATION_SHIFT);
	}

	protected final LazyState appliedState = new LazyState();
	protected final State currentState = new State();
	private boolean sharedClipping;
	// TODO Make private once all references in SWTGraphics have been removed
	protected boolean elementsNeedUpdate;

	private final List<State> stack = new ArrayList<>();
	private int stackPointer;

	private void checkSharedClipping() {
		if (sharedClipping) {
			sharedClipping = false;

			boolean previouslyApplied = (appliedState == currentState.relativeClip);
			// Fix: currentState.relativeClip can be null and lead to NPE
			if (currentState.relativeClip != null) {
				currentState.relativeClip = currentState.relativeClip.getCopy();
			}
			if (previouslyApplied) {
				appliedState.relativeClip = currentState.relativeClip;
			}
		}
	}

	/**
	 * @see Graphics#clipRect(Rectangle)
	 */
	@Override
	public void clipRect(Rectangle rect) {
		if (currentState.relativeClip == null) {
			throw new IllegalStateException("The current clipping area does not " + //$NON-NLS-1$
					"support intersection."); //$NON-NLS-1$
		}

		checkSharedClipping();
		currentState.relativeClip.intersect(rect.x, rect.y, rect.right(), rect.bottom());
		appliedState.relativeClip = null;
	}

	/**
	 * @see Graphics#dispose()
	 */
	@Override
	public void dispose() {
		while (stackPointer > 0) {
			popState();
		}
	}

	@Override
	public boolean getAdvanced() {
		return (currentState.graphicHints & ADVANCED_GRAPHICS_MASK) != 0;
	}

	/**
	 * Retrieves the 6 specifiable values in the 3x3 affine transformation matrix
	 * and places them into an array of single precisions values.
	 *
	 * @param m the single precision array used to store the returned values.
	 */
	protected abstract void getAffineMatrix(float[] m);

	/**
	 * @see Graphics#getAlpha()
	 */
	@Override
	public int getAlpha() {
		return currentState.alpha;
	}

	/**
	 * @see Graphics#getAntialias()
	 */
	@Override
	public int getAntialias() {
		return ((currentState.graphicHints & AA_MASK) >> AA_SHIFT) - AA_WHOLE_NUMBER;
	}

	/**
	 * @see Graphics#getBackgroundColor()
	 */
	@Override
	public Color getBackgroundColor() {
		return currentState.bgColor;
	}

	/**
	 * @see Graphics#getClip(Rectangle)
	 */
	@Override
	public Rectangle getClip(Rectangle rect) {
		if (currentState.relativeClip != null) {
			currentState.relativeClip.getBoundingBox(rect);
			return rect;
		}
		throw new IllegalStateException("Clipping can no longer be queried due to transformations"); //$NON-NLS-1$
	}

	/**
	 * @see Graphics#getFillRule()
	 */
	@Override
	public int getFillRule() {
		return ((currentState.graphicHints & FILL_RULE_MASK) >> FILL_RULE_SHIFT) - FILL_RULE_WHOLE_NUMBER;
	}

	/**
	 * @see Graphics#getFont()
	 */
	@Override
	public Font getFont() {
		return currentState.font;
	}

	/**
	 * @see Graphics#getForegroundColor()
	 */
	@Override
	public Color getForegroundColor() {
		return currentState.fgColor;
	}

	/**
	 * @see Graphics#getInterpolation()
	 */
	@Override
	public int getInterpolation() {
		return ((currentState.graphicHints & INTERPOLATION_MASK) >> INTERPOLATION_SHIFT) - INTERPOLATION_WHOLE_NUMBER;
	}

	/**
	 * @see Graphics#getLineCap()
	 */
	@Override
	public int getLineCap() {
		return currentState.lineAttributes.cap;
	}

	/**
	 * @see Graphics#getLineJoin()
	 */
	@Override
	public int getLineJoin() {
		return currentState.lineAttributes.join;
	}

	/**
	 * @see Graphics#getLineMiterLimit()
	 */
	@Override
	public float getLineMiterLimit() {
		return currentState.lineAttributes.miterLimit;
	}

	/**
	 * @see Graphics#getLineStyle()
	 */
	@Override
	public int getLineStyle() {
		return currentState.lineAttributes.style;
	}

	/**
	 * @see Graphics#getLineWidth()
	 */
	@Override
	public int getLineWidth() {
		return (int) currentState.lineAttributes.width;
	}

	/**
	 * @see Graphics#getLineWidthFloat()
	 */
	@Override
	public float getLineWidthFloat() {
		return currentState.lineAttributes.width;
	}

	/**
	 * @see Graphics#getTextAntialias()
	 */
	@Override
	public int getTextAntialias() {
		return ((currentState.graphicHints & TEXT_AA_MASK) >> TEXT_AA_SHIFT) - AA_WHOLE_NUMBER;
	}

	/**
	 * @see Graphics#getXORMode()
	 */
	@Override
	public boolean getXORMode() {
		return (currentState.graphicHints & XOR_MASK) != 0;
	}

	/**
	 * Performs a rotation on the underlying transformation matrix.
	 *
	 * @param degrees the angle of rotation measured in degrees.
	 * @return {@code true}, if the transformation matrix has been changed.
	 */
	protected abstract boolean performRotate(float degrees);

	/**
	 * Performs a horizontal and vertical scaling on the underlying transformation
	 * matrix.
	 *
	 * @param horizontal the factor by which coordinates are scaled along the X axis
	 *                   direction.
	 * @param vertical   the factor by which coordinates are scaled along the Y axis
	 *                   direction.
	 * @return {@code true}, if the transformation matrix has been changed.
	 */
	protected abstract boolean performScale(float horizontal, float vertical);

	/**
	 * Performs a horizontal and vertical shift on the underlying transformation
	 * matrix.
	 *
	 * @param horz the multiplier by which coordinates are shifted along the X axis
	 *             direction.
	 * @param vert the multiplier by which coordinates are shifted along the Y axis
	 *             direction.
	 * @return {@code true}, if the transformation matrix has been changed.
	 */
	protected abstract boolean performShear(float horz, float vert);

	/**
	 * Performs a horizontal and vertical shift on the underlying transformation
	 * matrix.
	 *
	 * @param dx the distance by which coordinates are translated in the X axis
	 *           direction
	 * @param dy the distance by which coordinates are translated in the Y axis
	 *           direction
	 * @return {@code true}, if the transformation matrix has been changed.
	 */
	protected abstract boolean performTranslate(float dx, float dy);

	/**
	 * Performs a horizontal and vertical shift on the underlying transformation
	 * matrix.
	 *
	 * @param dx the distance by which coordinates are translated in the X axis
	 *           direction
	 * @param dy the distance by which coordinates are translated in the Y axis
	 *           direction
	 * @return {@code true}, if the transformation matrix has been changed.
	 */
	protected abstract boolean performTranslate(int dx, int dy);

	/**
	 * @see Graphics#popState()
	 */
	@Override
	public void popState() {
		stackPointer--;
		restoreState(stack.get(stackPointer));
	}

	/**
	 * @see Graphics#pushState()
	 */
	@Override
	public void pushState() {
		if (currentState.relativeClip == null) {
			throw new IllegalStateException(
					"The clipping has been modified in a way that cannot be saved and restored."); //$NON-NLS-1$
		}

		State s;

		if (elementsNeedUpdate) {
			elementsNeedUpdate = false;
			currentState.affineMatrix = new float[6];
			getAffineMatrix(currentState.affineMatrix);
		}
		if (stack.size() > stackPointer) {
			s = stack.get(stackPointer);
			s.copyFrom(currentState);
		} else {
			stack.add(currentState.clone());
		}
		sharedClipping = true;
		stackPointer++;
	}

	/**
	 * @see Graphics#restoreState()
	 */
	@Override
	public void restoreState() {
		restoreState(stack.get(stackPointer - 1));
	}

	/**
	 * Sets all State information to that of the given State, called by
	 * restoreState()
	 *
	 * @param s the State
	 * @since 3.24
	 */
	protected void restoreState(State s) {
		/*
		 * We must set the transformation matrix first since it affects things like
		 * clipping regions and patterns.
		 */
		if (elementsNeedUpdate || currentState.affineMatrix != s.affineMatrix) {
			setAffineMatrix(s.affineMatrix);
		}
		currentState.relativeClip = s.relativeClip;
		sharedClipping = true;

		setBackgroundColor(s.bgColor);
		setBackgroundPattern(s.bgPattern);

		setForegroundColor(s.fgColor);
		setForegroundPattern(s.fgPattern);

		setAlpha(s.alpha);
		setLineAttributes(s.lineAttributes);
		setFont(s.font);

		// This method must come last because above methods will incorrectly set
		// advanced state
		setGraphicHints(s.graphicHints);

		currentState.dx = s.dx;
		currentState.dy = s.dy;

		currentState.sx = s.sx;
		currentState.sy = s.sy;
	}

	/**
	 * This method requires advanced graphics support. A check should be made to
	 * ensure advanced graphics is supported in the user's environment before
	 * calling this method. See {@link GC#getAdvanced()}.
	 *
	 * @see Graphics#rotate(float)
	 */
	@Override
	public void rotate(float degrees) {
		elementsNeedUpdate = performRotate(degrees);

		// Can no longer operate or maintain clipping
		appliedState.relativeClip = currentState.relativeClip = null;
	}

	/**
	 * @see Graphics#scale(double)
	 */
	@Override
	public void scale(double factor) {
		scale((float) factor, (float) factor);
	}

	/**
	 * This method requires advanced graphics support. A check should be made to
	 * ensure advanced graphics is supported in the user's environment before
	 * calling this method. See {@link GC#getAdvanced()}.
	 *
	 * @see Graphics#scale(float, float)
	 */
	@Override
	public void scale(float horizontal, float vertical) {
		elementsNeedUpdate = performScale(horizontal, vertical);
		currentState.sx *= horizontal;
		currentState.sy *= vertical;

		checkSharedClipping();
		if (currentState.relativeClip != null) {
			currentState.relativeClip.scale(horizontal, vertical);
		}
	}

	/**
	 * @see Graphics#setAdvanced(boolean)
	 */
	@Override
	public void setAdvanced(boolean value) {
		if (value) {
			currentState.graphicHints |= ADVANCED_GRAPHICS_MASK;
		} else {
			currentState.graphicHints &= ~ADVANCED_GRAPHICS_MASK;
		}
	}

	/**
	 *
	 * Sets this transform to the matrix specified by the 6 single precision values.
	 *
	 * @param m The new affine matrix. May be {@code null}.
	 */
	protected abstract void setAffineMatrix(float[] m);

	/**
	 * This method requires advanced graphics support. A check should be made to
	 * ensure advanced graphics is supported in the user's environment before
	 * calling this method. See {@link GC#getAdvanced()}.
	 *
	 * @see Graphics#setAntialias(int)
	 */
	@Override
	public void setAntialias(int value) {
		currentState.graphicHints &= ~AA_MASK;
		currentState.graphicHints |= ADVANCED_GRAPHICS_MASK | (value + AA_WHOLE_NUMBER) << AA_SHIFT;
	}

	/**
	 * @see Graphics#setBackgroundColor(Color)
	 */
	@Override
	public void setBackgroundColor(Color color) {
		currentState.bgColor = color;
		if (currentState.bgPattern != null) {
			currentState.bgPattern = null;
			// Force the BG color to be stale
			appliedState.bgColor = null;
		}
	}

	/**
	 * @see Graphics#setFillRule(int)
	 */
	@Override
	public void setFillRule(int rule) {
		currentState.graphicHints &= ~FILL_RULE_MASK;
		currentState.graphicHints |= (rule + FILL_RULE_WHOLE_NUMBER) << FILL_RULE_SHIFT;
	}

	/**
	 * @see Graphics#setFont(Font)
	 */
	@Override
	public void setFont(Font f) {
		currentState.font = f;
	}

	/**
	 * @see Graphics#setForegroundColor(Color)
	 */
	@Override
	public void setForegroundColor(Color color) {
		currentState.fgColor = color;
		if (currentState.fgPattern != null) {
			currentState.fgPattern = null;
			// Force fgColor to be stale
			appliedState.fgColor = null;
		}
	}

	private void setGraphicHints(int hints) {
		currentState.graphicHints = hints;
	}

	/**
	 * This method requires advanced graphics support. A check should be made to
	 * ensure advanced graphics is supported in the user's environment before
	 * calling this method. See {@link GC#getAdvanced()}.
	 *
	 * @see Graphics#setInterpolation(int)
	 */
	@Override
	public void setInterpolation(int interpolation) {
		// values range [-1, 3]
		currentState.graphicHints &= ~INTERPOLATION_MASK;
		currentState.graphicHints |= ADVANCED_GRAPHICS_MASK
				| (interpolation + INTERPOLATION_WHOLE_NUMBER) << INTERPOLATION_SHIFT;
	}

	/**
	 * @see Graphics#setLineAttributes(LineAttributes)
	 */
	@Override
	public void setLineAttributes(LineAttributes lineAttributes) {
		SWTGraphics.copyLineAttributes(currentState.lineAttributes, lineAttributes);
	}

	/**
	 * @see Graphics#setLineCap(int)
	 */
	@Override
	public void setLineCap(int value) {
		currentState.lineAttributes.cap = value;
	}

	/**
	 * @see Graphics#setLineDash(float[])
	 */
	@Override
	public void setLineDash(float[] value) {
		if (value != null) {
			// validate dash values
			for (float element : value) {
				if (element <= 0) {
					SWT.error(SWT.ERROR_INVALID_ARGUMENT);
				}
			}

			currentState.lineAttributes.dash = value.clone();
			currentState.lineAttributes.style = SWT.LINE_CUSTOM;
		} else {
			currentState.lineAttributes.dash = null;
			currentState.lineAttributes.style = SWT.LINE_SOLID;
		}
	}

	/**
	 * @see Graphics#setLineDash(int[])
	 */
	@Override
	public void setLineDash(int[] dashes) {
		float[] fArray = null;
		if (dashes != null) {
			fArray = new float[dashes.length];
			for (int i = 0; i < dashes.length; i++) {
				fArray[i] = dashes[i];
			}
		}
		setLineDash(fArray);
	}

	/**
	 * @see Graphics#setLineDashOffset(float)
	 */
	@Override
	public void setLineDashOffset(float value) {
		currentState.lineAttributes.dashOffset = value;
	}

	/**
	 * @see Graphics#setLineJoin(int)
	 */
	@Override
	public void setLineJoin(int value) {
		currentState.lineAttributes.join = value;
	}

	/**
	 * @see Graphics#setLineMiterLimit(float)
	 */
	@Override
	public void setLineMiterLimit(float value) {
		currentState.lineAttributes.miterLimit = value;
	}

	/**
	 * @see Graphics#setLineStyle(int)
	 */
	@Override
	public void setLineStyle(int value) {
		currentState.lineAttributes.style = value;
	}

	/**
	 * @see Graphics#setLineWidth(int)
	 */
	@Override
	public void setLineWidth(int width) {
		currentState.lineAttributes.width = width;
	}

	/**
	 * @see Graphics#setLineWidthFloat(float)
	 */
	@Override
	public void setLineWidthFloat(float value) {
		currentState.lineAttributes.width = value;
	}

	/**
	 * This method requires advanced graphics support. A check should be made to
	 * ensure advanced graphics is supported in the user's environment before
	 * calling this method. See {@link GC#getAdvanced()}.
	 *
	 * @see Graphics#setTextAntialias(int)
	 */
	@Override
	public void setTextAntialias(int value) {
		currentState.graphicHints &= ~TEXT_AA_MASK;
		currentState.graphicHints |= ADVANCED_GRAPHICS_MASK | (value + AA_WHOLE_NUMBER) << TEXT_AA_SHIFT;
	}

	/**
	 * @see Graphics#setXORMode(boolean)
	 */
	@Override
	public void setXORMode(boolean xor) {
		currentState.graphicHints &= ~XOR_MASK;
		if (xor) {
			currentState.graphicHints |= XOR_MASK;
		}
	}

	/**
	 * This method requires advanced graphics support. A check should be made to
	 * ensure advanced graphics is supported in the user's environment before
	 * calling this method. See {@link GC#getAdvanced()}.
	 *
	 * @see Graphics#shear(float, float)
	 */
	@Override
	public void shear(float horz, float vert) {
		elementsNeedUpdate = performShear(horz, vert);
		// Can no longer track clipping changes
		appliedState.relativeClip = currentState.relativeClip = null;
	}

	/**
	 * This method requires advanced graphics support. A check should be made to
	 * ensure advanced graphics is supported in the user's environment before
	 * calling this method. See {@link GC#getAdvanced()}.
	 *
	 * @see Graphics#translate(float, float)
	 */
	@Override
	public void translate(float dx, float dy) {
		elementsNeedUpdate = performTranslate(dx, dy);
		checkSharedClipping();
		if (currentState.relativeClip != null) {
			currentState.relativeClip.translate(-dx, -dy);
		}
	}

	/**
	 * This method may require advanced graphics support if using a transform, in
	 * this case, a check should be made to ensure advanced graphics is supported in
	 * the user's environment before calling this method. See
	 * {@link GC#getAdvanced()}.
	 *
	 * @see Graphics#translate(int, int)
	 */
	@Override
	public void translate(int dx, int dy) {
		if (dx == 0 && dy == 0) {
			return;
		}
		elementsNeedUpdate = performTranslate(dx, dy);
		checkSharedClipping();
		if (currentState.relativeClip != null) {
			currentState.relativeClip.translate(-dx, -dy);
		}
	}
}
