
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class DNDTest {

	public static void main(String[] args) {
		Shell shell = new Shell();
		shell.setLayout(new FillLayout());

		Canvas dragCanvas = new Canvas(shell, SWT.NONE);
		dragCanvas.setBackground(new Color(255, 255, 255));
		dragCanvas.addMouseListener(new MouseListener() {
			@Override
			public void mouseDoubleClick(MouseEvent event) {
			}

			@Override
			public void mouseDown(MouseEvent event) {
				System.out.println("Mouse Down"); //$NON-NLS-1$
			}

			@Override
			public void mouseUp(MouseEvent event) {
				System.out.println("Mouse Up"); //$NON-NLS-1$
			}
		});
		DragSource ds = new DragSource(dragCanvas, DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK);
		ds.setTransfer(TextTransfer.getInstance());
		ds.addDragListener(new DragSourceListener() {
			@Override
			public void dragStart(DragSourceEvent event) {
				System.out.println("Drag Start"); //$NON-NLS-1$
			}

			@Override
			public void dragSetData(DragSourceEvent event) {
				event.data = "Now it works"; //$NON-NLS-1$
			}

			@Override
			public void dragFinished(DragSourceEvent event) {
				System.out.println("Drag Finished"); //$NON-NLS-1$
			}
		});

		Canvas dropCanvas = new Canvas(shell, SWT.NONE);
		dropCanvas.setBackground(new Color(128, 128, 128));
		DropTarget dt = new DropTarget(dropCanvas, DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK);
		dt.setTransfer(TextTransfer.getInstance());
		dt.addDropListener(new DropTargetListener() {
			@Override
			public void dragEnter(DropTargetEvent event) {
			}

			@Override
			public void dragLeave(DropTargetEvent event) {
			}

			@Override
			public void dragOperationChanged(DropTargetEvent event) {
			}

			@Override
			public void dragOver(DropTargetEvent event) {
			}

			@Override
			public void drop(DropTargetEvent event) {
				System.out.println("Drop"); //$NON-NLS-1$
			}

			@Override
			public void dropAccept(DropTargetEvent event) {
			}
		});

		shell.setSize(300, 200);
		shell.open();
		Display display = Display.getDefault();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

}
