/*******************************************************************************
 * Copyright (c) 2011, 2025 Stephan Schwiebert and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Stephan Schwiebert - initial API and implementation
 *******************************************************************************/
package org.eclipse.zest.tests.cloudio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.zest.cloudio.TagCloud;
import org.eclipse.zest.cloudio.TagCloudViewer;
import org.eclipse.zest.cloudio.Word;
import org.eclipse.zest.cloudio.layout.DefaultLayouter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TagCloudViewerTests {

	private Display display;
	private boolean createdDisplay = false;
	private Composite composite;
	private TagCloud cloud;

	@Before
	public void setUp() throws Exception {
		display = Display.getCurrent();
		if (display == null) {
			display = new Display();
			createdDisplay = true;
		}
		composite = new Shell(display);
		composite.setLayout(new FillLayout());
		cloud = new TagCloud(composite, SWT.NONE);
	}

	@After
	public void tearDown() throws Exception {
		composite.dispose();
		if (createdDisplay) {
			display.dispose();
		}
	}

	@SuppressWarnings("static-method")
	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_NullCloud() {
		new TagCloudViewer(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_DisposedCloud() {
		cloud.dispose();
		new TagCloudViewer(cloud);
	}

	@Test
	public void testConstructor_ValidCloud() {
		TagCloudViewer viewer = new TagCloudViewer(cloud);
		TagCloud cloud = viewer.getCloud();
		assertNotNull(cloud);
		assertEquals(this.cloud, cloud);
		assertTrue(viewer.getSelection() != null);
		assertTrue(viewer.getSelection().isEmpty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidLabelProvider() {
		TagCloudViewer viewer = new TagCloudViewer(cloud);
		viewer.setLabelProvider(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidLabelProvider2() {
		TagCloudViewer viewer = new TagCloudViewer(cloud);
		viewer.setLabelProvider(new BaseLabelProvider());
	}

	@Test
	public void testValidLabelProvider() {
		TagCloudViewer viewer = new TagCloudViewer(cloud);
		TestLabelProvider labelProvider = new TestLabelProvider();
		viewer.setLabelProvider(labelProvider);
		assertEquals(labelProvider, viewer.getLabelProvider());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidContentProvider() {
		TagCloudViewer viewer = new TagCloudViewer(cloud);
		viewer.setContentProvider(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidContentProvider2() {
		TagCloudViewer viewer = new TagCloudViewer(cloud);
		viewer.setContentProvider(new IContentProvider() {
		});
	}

	private static class ListContentProvider implements ITreeContentProvider {

		@Override
		public Object[] getElements(Object inputElement) {
			return ((List<?>) inputElement).toArray();
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			return null;
		}

		@Override
		public Object getParent(Object element) {
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			return false;
		}

	}

	@Test
	public void testValidContentProvider() {
		TagCloudViewer viewer = new TagCloudViewer(cloud);
		ListContentProvider provider = new ListContentProvider();
		viewer.setContentProvider(provider);
		assertEquals(provider, viewer.getContentProvider());
	}

	@Test
	public void testValidLabelAsignment() {
		TagCloudViewer viewer = new TagCloudViewer(cloud);
		ListContentProvider provider = new ListContentProvider();
		viewer.setContentProvider(provider);
		TestLabelProvider labelProvider = new TestLabelProvider();
		viewer.setLabelProvider(labelProvider);
		List<String> data = new ArrayList<>();
		data.add("Hello"); //$NON-NLS-1$
		data.add("World"); //$NON-NLS-1$
		viewer.setInput(data);
		List<Word> words = viewer.getCloud().getWords();
		for (Word word : words) {
			assertEquals(TestLabelProvider.COLOR, word.getColor());
			for (int i = 0; i < TestLabelProvider.FONT_DATA.length; i++) {
				assertEquals(TestLabelProvider.FONT_DATA[i], word.getFontData()[i]);
			}
			assertEquals(TestLabelProvider.ANGLE, word.angle, 0.01);
			assertEquals(TestLabelProvider.WEIGHT, word.weight, 0.01);
			assertTrue(word.x != 0);
			assertTrue(word.y != 0);
			assertTrue(word.width != 0);
			assertTrue(word.height != 0);
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidLayouter() {
		TagCloudViewer viewer = new TagCloudViewer(cloud);
		viewer.setLayouter(null);
	}

	@Test
	public void testValidLayouter() {
		TagCloudViewer viewer = new TagCloudViewer(cloud);
		DefaultLayouter layouter = new DefaultLayouter(5, 5);
		viewer.setLayouter(layouter);
		assertEquals(layouter, viewer.getLayouter());
	}

}
