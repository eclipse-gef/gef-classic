/*******************************************************************************
 * Copyright (c) 2003, 2023 IBM Corporation and others.
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

package org.eclipse.draw2d.examples.cg;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.examples.AbstractExample;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.graph.Path;
import org.eclipse.draw2d.graph.ShortestPathRouter;

/**
 * @since 3.0
 */
public class ObstructionExample extends AbstractExample {

	static ShortestPathRouter router = new ShortestPathRouter();

	static Map<IFigure, Rectangle> obstacleMap = new HashMap<>();
	static List<Path> paths = new ArrayList<>();

	static class EllipseDragFigure extends Ellipse {

		protected EllipseDragFigure oFigure;
		protected boolean isSource;
		protected Path path;

		private static Dimension offset = new Dimension();

		public EllipseDragFigure(boolean isItSource) {
			this.isSource = isItSource;
			setBackgroundColor(ColorConstants.darkBlue);

			addMouseListener(new MouseListener.Stub() {
				@Override
				public void mousePressed(MouseEvent event) {
					event.consume();
					offset.setWidth(event.x - getLocation().x());
					offset.setHeight(event.y - getLocation().y());
				}

				@Override
				public void mouseReleased(MouseEvent event) {
					offset.setWidth(0);
					offset.setHeight(0);
					if (event.button == 2) {
						getParent().remove(EllipseDragFigure.this);
					}
				}
			});
			addMouseMotionListener(new MouseMotionListener.Stub() {
				@Override
				public void mouseDragged(MouseEvent event) {
					Rectangle rect = getBounds().getCopy();
					rect.setX(event.x - offset.width());
					rect.setY(event.y - offset.height());
					setBounds(rect);
					router.removePath(path);
					paths.remove(path);
					if (isSource) {
						path = new Path(getBounds().getCenter(), oFigure.getBounds().getCenter());
					} else {
						path = new Path(oFigure.getBounds().getCenter(), getBounds().getCenter());
					}
					router.addPath(path);
					paths.add(path);
					oFigure.path = path;

					getParent().repaint();

				}
			});

		}

		public void addOtherFigure(EllipseDragFigure figure) {
			this.oFigure = figure;
		}

		public EllipseDragFigure getOtherFigure() {
			return oFigure;
		}
	}

	static class DragFigure extends RectangleFigure {
		private static Dimension offset = new Dimension();

		public DragFigure() {

			setBackgroundColor(ColorConstants.green);

			addMouseListener(new MouseListener.Stub() {
				@Override
				public void mousePressed(MouseEvent event) {
					event.consume();
					offset.setWidth(event.x - getLocation().x());
					offset.setHeight(event.y - getLocation().y());
				}

				@Override
				public void mouseReleased(MouseEvent event) {
					offset.setWidth(0);
					offset.setHeight(0);
					if (event.button == 3) {
						getParent().remove(DragFigure.this);
					}
				}
			});
			addMouseMotionListener(new MouseMotionListener.Stub() {
				@Override
				public void mouseDragged(MouseEvent event) {
					Rectangle rect = getBounds().getCopy();
					rect.setX(event.x - offset.width());
					rect.setY(event.y - offset.height());
					router.updateObstacle(ObstructionExample.obstacleMap.get(DragFigure.this), rect);
					ObstructionExample.obstacleMap.put(DragFigure.this, rect);
					setBounds(rect);
					getParent().repaint();
				}
			});
		}

		@Override
		public void paint(Graphics graphics) {
			super.paint(graphics);
			Rectangle b = getBounds();
			Point p = b.getCenter().translate(-22, -15);
			graphics.drawString("y:" + b.y() + "h:" + b.height(), p); //$NON-NLS-1$ //$NON-NLS-2$
			p.setY(p.y() + 12);
			graphics.drawString("x:" + b.x() + "w:" + b.width(), p); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	class TestFigure extends Figure {

		private static final int INITAL_OBSTACLE_COUNT = 5;

		boolean showSegs = false;

		public TestFigure() {
			router = new ShortestPathRouter();

			addMouseListener(new MouseListener.Stub() {
				private EllipseDragFigure figure;
				private Point pPoint;

				@Override
				public void mousePressed(MouseEvent event) {
					event.consume();
					pPoint = event.getLocation();
				}

				@Override
				public void mouseReleased(MouseEvent event) {
					if (event.button == 1) {
						if (Math.abs(pPoint.x() - event.getLocation().x()) > 10) {
							DragFigure f = new DragFigure();
							f.setBounds(new Rectangle(pPoint, event.getLocation()));
							add(f);
						}
					} else if (event.button == 3) {
						if (figure != null) {
							// source already there, create target
							EllipseDragFigure eFigure = new EllipseDragFigure(false);
							eFigure.setBounds(
									new Rectangle(event.getLocation().x() - 10, event.getLocation().y() - 10, 20, 20));
							figure.addOtherFigure(eFigure);
							eFigure.addOtherFigure(figure);
							add(eFigure);
							// start with a new path
							figure = null;
						} else {
							EllipseDragFigure eFigure = new EllipseDragFigure(true);
							eFigure.setBounds(
									new Rectangle(event.getLocation().x() - 10, event.getLocation().y() - 10, 20, 20));
							add(eFigure);
							figure = eFigure;
						}
					} else {
						showSegs = !showSegs;
					}
					getParent().repaint();
				}
			});

			createInitialObstacles();
		}

		private final void createInitialObstacles() {
			Random r = new SecureRandom();
			int rowSize = (int) Math.sqrt(INITAL_OBSTACLE_COUNT);
			for (int i = 0; i < INITAL_OBSTACLE_COUNT; i++) {
				DragFigure f = new DragFigure();
				Rectangle bounds = new Rectangle(((i / rowSize) * 101) + (((i) % 3) * 10) + 100,
						((i % rowSize) * 101) + ((i % 7) * 6), 50, 80 + (int) (r.nextDouble() * 10));
				f.setBounds(bounds);

				add(f);
			}
		}

		@Override
		protected void paintBorder(Graphics g) {
			router.solve();
			g.setLineWidth(1);

			g.setForegroundColor(ColorConstants.blue);
			g.setBackgroundColor(ColorConstants.button);

			// draw paths
			paths.forEach(p -> {
				PointList pList = p.getPoints();
				for (int i = 1; i < pList.size(); i++) {
					g.drawLine(pList.getPoint(i - 1), pList.getPoint(i));
				}
			});
		}

		@Override
		public void add(IFigure figure, Object constraint, int index) {
			if (figure instanceof DragFigure) {
				ObstructionExample.obstacleMap.put(figure, figure.getBounds());
				router.addObstacle(figure.getBounds());
			}
			if (figure instanceof EllipseDragFigure) {
				handleAddEllipseDragFigure((EllipseDragFigure) figure);
			}

			super.add(figure, constraint, index);
		}

		private void handleAddEllipseDragFigure(EllipseDragFigure figure) {
			EllipseDragFigure otherFigure = figure.getOtherFigure();
			if (otherFigure != null) {
				// this is the target figure add a path
				Path path = new Path(otherFigure.getBounds().getCenter(), figure.getBounds().getCenter());
				router.addPath(path);
				paths.add(path);
				otherFigure.path = path;
				figure.path = path;
			}
		}

		@Override
		public void remove(IFigure figure) {
			if (figure instanceof DragFigure) {
				router.removeObstacle(ObstructionExample.obstacleMap.remove(figure));
			}
			if (figure instanceof EllipseDragFigure) {
				handleRemoveEllipseDragFigure((EllipseDragFigure) figure);

			}
			super.remove(figure);
			repaint();
		}

		private void handleRemoveEllipseDragFigure(EllipseDragFigure figure) {
			EllipseDragFigure otherFigure = figure.getOtherFigure();
			if (otherFigure != null) {
				getParent().remove(otherFigure);
				router.removePath(figure.path);
				paths.remove(figure.path);
			}
		}
	}

	/**
	 * @see org.eclipse.draw2d.examples.AbstractExample#createContents()
	 */
	@Override
	protected IFigure createContents() {
		Figure f = new TestFigure();
		f.setPreferredSize(900, 700);
		return f;
	}

	public static void main(String[] args) {
		new ObstructionExample().run();
	}

}
