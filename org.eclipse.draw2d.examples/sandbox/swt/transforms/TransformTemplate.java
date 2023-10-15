/*******************************************************************************
 * Copyright (c) 2005, 2023 IBM Corporation and others.
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
package swt.transforms;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Transform;

public class TransformTemplate extends AbstractSWTTransform {

	public static void main(String[] args) {
		new TransformTemplate().runTransformTest();
	}

	@Override
	protected void performPaint(PaintEvent e) {
		GC gc = e.gc;
		gc.drawRoundRectangle(15, 15, 100, 100, 20, 20);
		Transform transform = new Transform(gc.getDevice());
		transform.translate(5, 5);
		gc.setTransform(transform);
		transform.dispose();
		gc.setAlpha(120);
		gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_YELLOW));
		gc.drawRoundRectangle(10, 10, 100, 100, 20, 20);
	}

}