/*******************************************************************************
 * Copyright 2005, 2024 CHISEL Group, University of Victoria, Victoria,
 *                      BC, Canada and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: The Chisel Group, University of Victoria
 ******************************************************************************/
package org.eclipse.zest.layouts.exampleUses;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.BoxLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.GridLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.eclipse.zest.layouts.exampleStructures.SimpleGraph;
import org.eclipse.zest.layouts.exampleStructures.SimpleNode;
import org.eclipse.zest.layouts.exampleStructures.SimpleRelationship;

/**
 * @author Rob Lintern
 * @author Ian Bull A simple example of using layout algorithms with a SWT
 *         application.
 */
public class SimpleSWTExample {

	private static final Color BLACK = new Color(0, 0, 0);
	private static final Color NODE_NORMAL_COLOR = new Color(225, 225, 255);
	private static final Color NODE_SELECTED_COLOR = new Color(255, 125, 125);
	private static final Color NODE_ADJACENT_COLOR = new Color(255, 200, 125);
	private static final Color BORDER_NORMAL_COLOR = new Color(0, 0, 0);
	private static final Color BORDER_SELECTED_COLOR = new Color(255, 0, 0);
	private static final Color BORDER_ADJACENT_COLOR = new Color(255, 128, 0);
	private static final Color RELATIONSHIP_COLOR = new Color(192, 192, 225);
	private static final Color RELATIONSHIP_HIGHLIGHT_COLOR = new Color(255, 200, 125);

	@SuppressWarnings("nls")
	private static final String[] NAMES = { "Peggy", "Rob", "Ian", "Chris", "Simon", "Wendy", "Steven", "Kim", "Neil",
			"Dave", "John", "Suzanne", "Jody", "Casey", "Bjorn", "Peter", "Erin", "Lisa", "Jennie", "Liz", "Bert",
			"Ryan", "Nick", "Amy", "Lee", "Me", "You", "Max", "NCI", "OWL", "Ed", "Jamie", "Protege", "Matt", "Bryan",
			"Pete", "Sam", "Bob", "Katie", "Bill", "Josh", "Davor", "Ken", "Jacob", "Norm", "Jim", "Maya", "Jill",
			"Kit", "Jo", "Joe", "Andrew", "Charles", "Pat", "Patrick", "Jeremy", "Mike", "Michael", "Patricia", "Marg",
			"Terry", "Emily", "Ben", "Holly", "Joanna", "Joanne", "Evan", "Tom", "Dan", "Eric", "Corey", "Meghan",
			"Kevin", "Nina", "Ron", "Daniel", "David", "Jeff", "Nathan", "Amanda", "Phil", "Tricia", "Steph", "Stewart",
			"Stuart", "Bull", "Lintern", "Callendar", "Thompson", "Rigby", "Adam", "Judith", "Cynthia", "Sarah", "Sara",
			"Roger", "Andy", "Kris", "Mark", "Shane", "Spence", "Ivy", "Ivanna", "Julie", "Justin", "Emile", "Toby",
			"Robin", "Rich", "Kathy", "Cathy", "Nicky", "Ricky", "Danny", "Anne", "Ann", "Jen", "Robert", "Calvin",
			"Alvin", "Scott", "Kumar" };

	private static final int INITIAL_PANEL_WIDTH = 800;
	private static final int INITIAL_PANEL_HEIGHT = 600;
	private static final double INITIAL_NODE_WIDTH = 20;
	private static final double INITIAL_NODE_HEIGHT = 15;

	protected static List<LayoutAlgorithm> algorithms = List.of(new SpringLayoutAlgorithm(), //
			new TreeLayoutAlgorithm(), //
			new TreeLayoutAlgorithm(TreeLayoutAlgorithm.LEFT_RIGHT), //
			new RadialLayoutAlgorithm(), //
			new GridLayoutAlgorithm(), //
			new BoxLayoutAlgorithm(SWT.HORIZONTAL), //
			new BoxLayoutAlgorithm(SWT.VERTICAL));

	protected static List<String> algorithmNames = List.of(Messages.getString("SimpleSWTExample.SpringLayout"), //$NON-NLS-1$
			Messages.getString("SimpleSWTExample.TreeVLayout"), //$NON-NLS-1$
			Messages.getString("SimpleSWTExample.TreeHLayout"), //$NON-NLS-1$
			Messages.getString("SimpleSWTExample.RadialLayout"), //$NON-NLS-1$
			Messages.getString("SimpleSWTExample.GridLayout"), //$NON-NLS-1$
			Messages.getString("SimpleSWTExample.HorizontalLayout"), //$NON-NLS-1$
			Messages.getString("SimpleSWTExample.VerticalLayout")); //$NON-NLS-1$
	// private long updateGUICount = 0;

	private final Shell mainShell;
	private Composite mainComposite;

	private final ToolBar toolBar;

	private LayoutAlgorithm currentLayoutAlgorithm;
	protected SimpleNode selectedEntity;
	protected SimpleNode hoverEntity;
	private SimpleGraph graph;

	protected Point mouseDownPoint;
	protected Point selectedEntityPositionAtMouseDown;
	private long idCount = 0;

	public SimpleSWTExample(Display display) {
		mainShell = new Shell(display);
		mainShell.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				mainShell.layout(true);
			}
		});
		mainShell.setLayout(new GridLayout());
		toolBar = new ToolBar(mainShell, SWT.HORIZONTAL);
		toolBar.setLayoutData(new GridData(SWT.CENTER, SWT.BEGINNING, true, false));
		toolBar.setLayout(new FillLayout(SWT.HORIZONTAL));

		for (int i = 0; i < algorithms.size(); i++) {
			final LayoutAlgorithm algorithm = algorithms.get(i);
			String algorithmName = algorithmNames.get(i);
			ToolItem algorithmButton = new ToolItem(toolBar, SWT.PUSH);
			algorithmButton.setText(algorithmName);

			new ToolItem(toolBar, SWT.SEPARATOR);

			algorithmButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
					currentLayoutAlgorithm = algorithm;
					performLayout(false);
				}
			});
		}

		ToolItem redrawButton = new ToolItem(toolBar, SWT.PUSH);
		redrawButton.setText(Messages.getString("SimpleSWTExample.Redraw")); //$NON-NLS-1$
		redrawButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mainComposite.redraw();
			}
		});

		createMainPanel();
		SimpleNode.setNodeColors(NODE_NORMAL_COLOR, BORDER_NORMAL_COLOR, NODE_SELECTED_COLOR, NODE_ADJACENT_COLOR,
				BORDER_SELECTED_COLOR, BORDER_ADJACENT_COLOR);
		SimpleRelationship.setDefaultColor(RELATIONSHIP_COLOR);
		SimpleRelationship.setDefaultHighlightColor(RELATIONSHIP_HIGHLIGHT_COLOR);
		createTreeGraph(4, 3, false);
		mainShell.pack();
		// mainShell.setSize(INITIAL_PANEL_WIDTH + 100, INITIAL_PANEL_HEIGHT + 200);
	}

	IProgressMonitor progressMonitor = null;
	ProgressMonitorDialog pmd = null;
	boolean GUI_UPDATING = false;

	private void performLayout(boolean placeRandomly) {
		if (placeRandomly) {
			placeRandomly();
		}

		Rectangle bounds = mainComposite.getClientArea();
		graph.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);

		currentLayoutAlgorithm.setLayoutContext(graph);
		currentLayoutAlgorithm.applyLayout(true);
		updateGUI();
	}

	private Shell getShell() {
		return mainShell;
	}

	private void createMainPanel() {
		mainComposite = new Canvas(mainShell, SWT.NO_BACKGROUND);
		GridData mainGridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		mainGridData.widthHint = INITIAL_PANEL_WIDTH;
		mainGridData.heightHint = INITIAL_PANEL_HEIGHT;
		mainComposite.setLayoutData(mainGridData);
		mainComposite.addPaintListener(new GraphPaintListener());

		mainComposite.setBackground(new Color(255, 255, 255));
		mainComposite.setLayout(null);

		mainComposite.addMouseMoveListener(e -> {

			if (selectedEntity == null) {
				// Nothing selected, lets use a mouse hover
				SimpleNode oldEntity = hoverEntity;
				hoverEntity = null;

				for (SimpleNode entity : graph.getNodes()) {
					double x = entity.getX();
					double y = entity.getY();
					double w = entity.getWidth();
					double h = entity.getHeight();
					Rectangle rect = new Rectangle((int) x, (int) y, (int) w, (int) h);
					if (rect.contains(e.x, e.y)) {
						hoverEntity = entity;
						hoverEntity.ignoreInLayout(true);
						hoverEntity.setSelected();
						break;
					}
				}
				if (oldEntity != null && oldEntity != hoverEntity) {
					oldEntity.ignoreInLayout(false);
					oldEntity.setUnSelected();
				}
			}

		});
		mainComposite.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				selectedEntity = null;
				hoverEntity = null;
				for (SimpleNode entity : graph.getNodes()) {
					double x = entity.getX();
					double y = entity.getY();
					double w = entity.getWidth();
					double h = entity.getHeight();
					Rectangle rect = new Rectangle((int) x, (int) y, (int) w, (int) h);
					if (rect.contains(e.x, e.y)) {
						selectedEntity = entity;
						break;
					}
				}
				if (selectedEntity != null) {
					mouseDownPoint = new Point(e.x, e.y);
					selectedEntityPositionAtMouseDown = new Point((int) selectedEntity.getX(),
							(int) selectedEntity.getY());
					selectedEntity.ignoreInLayout(true);
					selectedEntity.setSelected();
				} else {
					mouseDownPoint = null;
					selectedEntityPositionAtMouseDown = null;
				}
				mainComposite.redraw();
			}

			@Override
			public void mouseUp(MouseEvent e) {
				if (selectedEntity != null) {
					selectedEntity.ignoreInLayout(false);
					selectedEntity.setUnSelected();
					List<SimpleNode> relatedNodes = selectedEntity.getRelatedEntities();
					for (SimpleNode element : relatedNodes) {
						element.setUnSelected();
					}
					for (SimpleRelationship rel : selectedEntity.getOutgoingConnections()) {
						rel.resetLineWidth();
					}
					for (SimpleRelationship rel : selectedEntity.getIncomingConnections()) {
						rel.resetLineWidth();
					}
				}
				selectedEntity = null;
				mouseDownPoint = null;
				selectedEntityPositionAtMouseDown = null;
			}
		});

		mainComposite.addMouseMoveListener(e -> {
			if (selectedEntity != null && mouseDownPoint != null) {
				double dx = e.x - mouseDownPoint.x;
				double dy = e.y - mouseDownPoint.y;

				selectedEntity.ignoreInLayout(false);
				selectedEntity.setLocation(selectedEntityPositionAtMouseDown.x + dx,
						selectedEntityPositionAtMouseDown.y + dy);
				selectedEntity.ignoreInLayout(true);
				mainComposite.redraw();
			}
		});

		mainComposite.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				if (currentLayoutAlgorithm != null) {
					performLayout(false);
				}
			}
		});
	}

	static int lastUpdateCall = 0;

	/**
	 *
	 * @param maxLevels   Max number of levels wanted in tree
	 * @param maxChildren Max number of children for each node in the tree
	 * @param random      Whether or not to pick random number of levels (from 1 to
	 *                    maxLevels) and random number of children (from 1 to
	 *                    maxChildren)
	 */
	private void createTreeGraph(int maxLevels, int maxChildren, boolean random) {
		graph = new SimpleGraph();

		// ccallendar - testing out having 2 roots
		SimpleNode root = createSimpleNode(getNextID());
		graph.addEntity(root);
		SimpleNode root2 = createSimpleNode(getNextID());
		graph.addEntity(root2);
		// end

		SimpleNode currentParent = createSimpleNode(getNextID());
		graph.addEntity(currentParent);

		// ccallendar - adding relationships from the parent to the 2 roots
		SimpleRelationship rel = new SimpleRelationship(root, currentParent, false);
		graph.addRelationship(rel);
		rel = new SimpleRelationship(root2, currentParent, false);
		graph.addRelationship(rel);
		// end

		int levels = random ? (int) (Math.random() * maxLevels + 1) : maxLevels;
		createTreeGraphRecursive(currentParent, maxChildren, levels, 1, random);
	}

	private void createTreeGraphRecursive(SimpleNode currentParentNode, int maxChildren, int maxLevel, int level,
			boolean random) {
		if (level > maxLevel) {
			return;
		}

		int numChildren = random ? (int) (Math.random() * maxChildren + 1) : maxChildren;
		for (int child = 0; child < numChildren; child++) {
			SimpleNode childNode = createSimpleNode(getNextID());
			graph.addEntity(childNode);
			SimpleRelationship rel = new SimpleRelationship(currentParentNode, childNode, false);
			graph.addRelationship(rel);
			SimpleRelationship.setDefaultSize(2);
			createTreeGraphRecursive(childNode, maxChildren, maxLevel, level + 1, random);
		}
	}

	private int repeats = 0;

	/**
	 * Gets the next name from the names list. Once all the names have been used up
	 * the names are repeated with a '1' after the name.
	 *
	 * @return String name
	 */
	private String getNextID() {
		if (idCount >= NAMES.length) {
			idCount = 0;
			repeats++;
		}
		String id = NAMES[(int) idCount];
		if (repeats > 0) {
			id += "_" + repeats; //$NON-NLS-1$
		}
		idCount++;
		return id;
	}

	/** Places nodes randomly on the screen **/
	private void placeRandomly() {
		for (SimpleNode simpleNode : graph.getNodes()) {
			double x = Math.random() * INITIAL_PANEL_WIDTH - INITIAL_NODE_WIDTH;
			double y = Math.random() * INITIAL_PANEL_HEIGHT - INITIAL_NODE_HEIGHT;
			simpleNode.setLocation(x, y);
		}
	}

	/**
	 * Creates a SimpleNode
	 *
	 * @param name
	 * @return SimpleNode
	 */
	private SimpleNode createSimpleNode(String name) {
		SimpleNode simpleNode = new SimpleNode(name, graph);
		int w = name.length() * 8; // an initial approximation of the width
		simpleNode.setSize(Math.max(w, INITIAL_NODE_WIDTH), INITIAL_NODE_HEIGHT);
		return simpleNode;
	}

	private void updateGUI() {
		if (!mainComposite.isDisposed()) {
			mainComposite.redraw();
			// mainComposite.update();
		}
	}

	static Display display = null;

	public static void main(String[] args) {
		display = Display.getDefault();
		SimpleSWTExample simpleSWTExample = new SimpleSWTExample(display);
		Shell shell = simpleSWTExample.getShell();
		// shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

	/**
	 * Implements a paint listener to display nodes and edges
	 */
	private class GraphPaintListener implements PaintListener {

		@Override
		public void paintControl(PaintEvent e) {
			if (Display.getDefault() == null || e.width == 0 || e.height == 0) {
				return;
			}

			// do a bit of our own double-buffering to stop flickering
			Image imageBuffer;

			try {
				imageBuffer = new Image(Display.getDefault(), e.width, e.height);
			} catch (SWTError noMoreHandles) {
				imageBuffer = null;
				noMoreHandles.printStackTrace();
				return;
			} catch (IllegalArgumentException tooBig) {
				imageBuffer = null;
				tooBig.printStackTrace();
				return;
			}

			GC gcBuffer = new GC(imageBuffer);

			// paint the relationships
			for (SimpleRelationship rel : graph.getConnections()) {
				SimpleNode src = rel.getSource();
				SimpleNode dest = rel.getTarget();

				// highlight the adjacent nodes if one of the nodes is selected
				if (src.equals(selectedEntity)) {
					dest.setAdjacent();
					rel.setSelected();
				} else if (dest.equals(selectedEntity)) {
					src.setAdjacent();
					rel.setSelected();
				} else {
					rel.setUnSelected();
				}

				double srcX = src.getX() + src.getWidth() / 2.0;
				double srcY = src.getY() + src.getHeight() / 2.0;
				double destX = dest.getX() + dest.getWidth() / 2.0;
				double destY = dest.getY() + dest.getHeight() / 2.0;
				drawEdge(srcX, srcY, destX, destY, rel, gcBuffer);

			}

			// paint the nodes
			for (SimpleNode entity : graph.getNodes()) {
				String name = entity.toString();
				Point textSize = gcBuffer.stringExtent(name);
				int entityX = (int) entity.getX();
				int entityY = (int) entity.getY();
				// TODO: What about resize from the layout algorithm
				int entityWidth = Math.max((int) entity.getWidth(), textSize.x + 8);
				int entityHeight = Math.max((int) entity.getHeight(), textSize.y + 2);

				gcBuffer.setBackground((Color) entity.getColor());
				gcBuffer.fillRoundRectangle(entityX, entityY, entityWidth, entityHeight, 8, 8);

				// position the text in the middle of the node
				int x = (int) (entityX + (entityWidth / 2.0)) - (textSize.x / 2);
				gcBuffer.setForeground(BLACK);
				gcBuffer.drawString(name, x, entityY);
				gcBuffer.setForeground((Color) entity.getBorderColor());
				gcBuffer.setLineWidth(entity.getBorderWidth());
				gcBuffer.drawRoundRectangle(entityX, entityY, entityWidth, entityHeight, 8, 8);
			}

			e.gc.drawImage(imageBuffer, 0, 0);
			imageBuffer.dispose();
			gcBuffer.dispose();
		}

		/**
		 * Draw an edge
		 *
		 * @param gcBuffer
		 * @param srcX
		 * @param srcY
		 * @param destX
		 * @param destY
		 * @param rel
		 */
		private void drawEdge(double srcX, double srcY, double destX, double destY, SimpleRelationship rel,
				GC gcBuffer) {
			gcBuffer.setForeground((Color) rel.getColor());
			gcBuffer.setLineWidth(rel.getLineWidth());
			gcBuffer.drawLine((int) srcX, (int) srcY, (int) destX, (int) destY);
		}
	}

}
