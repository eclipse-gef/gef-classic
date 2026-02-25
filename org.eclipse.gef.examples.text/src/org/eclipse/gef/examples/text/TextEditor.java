/*******************************************************************************
 * Copyright (c) 2004, 2026 IBM Corporation and others.
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

package org.eclipse.gef.examples.text;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IKeyBindingService;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.PositionConstants;

import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.MouseWheelHandler;
import org.eclipse.gef.MouseWheelZoomHandler;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackEvent;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.tools.SelectionTool;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.gef.ui.parts.TreeViewer;

import org.eclipse.gef.examples.text.actions.BooleanStyleAction;
import org.eclipse.gef.examples.text.actions.MultiStyleAction;
import org.eclipse.gef.examples.text.actions.StyleService;
import org.eclipse.gef.examples.text.actions.TextActionConstants;
import org.eclipse.gef.examples.text.edit.BlockTextPart;
import org.eclipse.gef.examples.text.edit.ContainerTreePart;
import org.eclipse.gef.examples.text.edit.DocumentPart;
import org.eclipse.gef.examples.text.edit.ImportPart;
import org.eclipse.gef.examples.text.edit.ImportsPart;
import org.eclipse.gef.examples.text.edit.InlineTextPart;
import org.eclipse.gef.examples.text.edit.TextFlowPart;
import org.eclipse.gef.examples.text.edit.TextRunTreePart;
import org.eclipse.gef.examples.text.model.Block;
import org.eclipse.gef.examples.text.model.CanvasStyle;
import org.eclipse.gef.examples.text.model.Container;
import org.eclipse.gef.examples.text.model.Style;
import org.eclipse.gef.examples.text.model.TextRun;
import org.eclipse.gef.examples.text.tools.TextTool;

/**
 * @since 3.1
 */
public class TextEditor extends GraphicalEditor {

	private Container doc;
	private final StyleService styleService = new StyleService();

	private static final class TextEditorEditPartFactory implements EditPartFactory {
		@Override
		public EditPart createEditPart(EditPart context, Object model) {
			if (model instanceof Container cont) {
				return switch (cont.getType()) {
				case Container.TYPE_ROOT -> new DocumentPart(cont);
				case Container.TYPE_IMPORT_DECLARATIONS -> new ImportsPart(cont);
				case Container.TYPE_COMMENT, Container.TYPE_PARAGRAPH -> new BlockTextPart(cont);
				case Container.TYPE_INLINE -> new InlineTextPart(cont);
				default -> throw new RuntimeException("unknown model type"); //$NON-NLS-1$
				};
			}
			if (model instanceof TextRun textRun) {
				if (textRun.getType() == TextRun.TYPE_IMPORT) {
					return new ImportPart(textRun);
				}
				return new TextFlowPart(textRun);
			}
			throw new RuntimeException("unexpected model object"); //$NON-NLS-1$
		}
	}

	class TextOutlinePage extends ContentOutlinePage {

		public TextOutlinePage(EditPartViewer viewer) {
			super(new TreeViewer());
			EditDomain domain = new DefaultEditDomain(TextEditor.this);
			domain.setDefaultTool(new SelectionTool());
			domain.loadDefaultTool();
			EditPartViewer treeViewer = getViewer();
			treeViewer.setEditDomain(domain);
			getSelectionSynchronizer().addViewer(treeViewer);
			treeViewer.setEditPartFactory((context, model) -> {
				if (model instanceof Container cont) {
					return new ContainerTreePart(cont);
				}
				return new TextRunTreePart(model);
			});
		}

		@Override
		public void createControl(Composite parent) {
			super.createControl(parent);
			getViewer().setContents(doc);
		}
	}

	@Override
	public void stackChanged(CommandStackEvent event) {
		firePropertyChange(PROP_DIRTY);
		super.stackChanged(event);
	}

	/**
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#configureGraphicalViewer()
	 */
	@Override
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();
		doc.getStyle().setParentStyle(new CanvasStyle(getGraphicalViewer().getControl()));

		getEditDomain().setDefaultTool(new TextTool(styleService));
		getEditDomain().loadDefaultTool();

		getGraphicalViewer().setRootEditPart(new ScalableRootEditPart());
		((FigureCanvas) getGraphicalViewer().getControl()).getViewport().setContentsTracksWidth(true);

		// Scroll-wheel Zoom
		getGraphicalViewer().setProperty(MouseWheelHandler.KeyGenerator.getKey(SWT.MOD1),
				MouseWheelZoomHandler.SINGLETON);
	}

	/**
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#createActions()
	 */
	@Override
	protected void createActions() {
		super.createActions();
		IKeyBindingService service = getSite().getKeyBindingService();
		ActionRegistry registry = getActionRegistry();
		IAction action;

		action = new BooleanStyleAction(styleService, TextActionConstants.STYLE_BOLD, Style.PROPERTY_BOLD);
		registry.registerAction(action);
		service.registerAction(action);

		action = new BooleanStyleAction(styleService, TextActionConstants.STYLE_ITALIC, Style.PROPERTY_ITALIC);
		registry.registerAction(action);
		service.registerAction(action);

		action = new BooleanStyleAction(styleService, TextActionConstants.STYLE_UNDERLINE, Style.PROPERTY_UNDERLINE);
		registry.registerAction(action);
		service.registerAction(action);

		action = new MultiStyleAction(styleService, TextActionConstants.BLOCK_ALIGN_LEFT, Style.PROPERTY_ALIGNMENT,
				Integer.valueOf(PositionConstants.ALWAYS_LEFT));
		registry.registerAction(action);

		action = new MultiStyleAction(styleService, TextActionConstants.BLOCK_ALIGN_CENTER, Style.PROPERTY_ALIGNMENT,
				Integer.valueOf(PositionConstants.CENTER));
		registry.registerAction(action);

		action = new MultiStyleAction(styleService, TextActionConstants.BLOCK_ALIGN_RIGHT, Style.PROPERTY_ALIGNMENT,
				Integer.valueOf(PositionConstants.ALWAYS_RIGHT));
		registry.registerAction(action);

		action = new MultiStyleAction(styleService, TextActionConstants.BLOCK_LTR, Style.PROPERTY_ORIENTATION,
				Integer.valueOf(SWT.LEFT_TO_RIGHT));
		registry.registerAction(action);

		action = new MultiStyleAction(styleService, TextActionConstants.BLOCK_RTL, Style.PROPERTY_ORIENTATION,
				Integer.valueOf(SWT.RIGHT_TO_LEFT));
		registry.registerAction(action);
	}

	/**
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#createGraphicalViewer(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createGraphicalViewer(Composite parent) {
		GraphicalViewer viewer = new GraphicalTextViewer();
		viewer.createControl(parent);
		setGraphicalViewer(viewer);
		configureGraphicalViewer();
		hookGraphicalViewer();
		initializeGraphicalViewer();
	}

	@Override
	public <T> T getAdapter(final Class<T> type) {
		if (type == IContentOutlinePage.class) {
			return type.cast(createOutlinePage());
		}
		if (type == StyleService.class) {
			return type.cast(styleService);
		}
		return super.getAdapter(type);
	}

	private IContentOutlinePage createOutlinePage() {
		return new TextOutlinePage(null);
	}

	/**
	 * @see GraphicalEditor#initializeGraphicalViewer()
	 */
	@Override
	protected void initializeGraphicalViewer() {
		getGraphicalViewer().setEditPartFactory(new TextEditorEditPartFactory());

		getGraphicalViewer().setContents(doc);
		// Initial layout is required for Caret calculation
		getGraphicalViewer().flush();
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				ObjectOutputStream objStream = new ObjectOutputStream(outputStream);) {
			objStream.writeObject(doc);
			IFile file = ((IFileEditorInput) getEditorInput()).getFile();
			file.setContents(new ByteArrayInputStream(outputStream.toByteArray()), true, false, monitor);
			getCommandStack().markSaveLocation();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
		// currently not implemented
	}

	/**
	 * @see GraphicalEditor#init(IEditorSite, IEditorInput)
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setEditDomain(new DefaultEditDomain(this));
		getCommandStack().addCommandStackEventListener(event -> {
			TextCommand command = (TextCommand) event.getCommand();
			if (command != null) {
				GraphicalTextViewer textViewer = (GraphicalTextViewer) getGraphicalViewer();
				if (event.getDetail() == CommandStack.POST_EXECUTE) {
					textViewer.setSelectionRange(command.getExecuteSelectionRange(textViewer));
				} else if (event.getDetail() == CommandStack.POST_REDO) {
					textViewer.setSelectionRange(command.getRedoSelectionRange(textViewer));
				} else if (event.getDetail() == CommandStack.POST_UNDO) {
					textViewer.setSelectionRange(command.getUndoSelectionRange(textViewer));
				}
			}
		});

		super.init(site, input);

		site.getKeyBindingService().setScopes(new String[] { GEFActionConstants.CONTEXT_TEXT });
		site.getActionBars().setGlobalActionHandler(ActionFactory.UNDO.getId(),
				getActionRegistry().getAction(ActionFactory.UNDO.getId()));
		site.getActionBars().setGlobalActionHandler(ActionFactory.REDO.getId(),
				getActionRegistry().getAction(ActionFactory.REDO.getId()));
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);

		IFile file = ((IFileEditorInput) input).getFile();
		try (InputStream is = file.getContents(false); ObjectInputStream ois = new ObjectInputStream(is);) {
			doc = (Container) ois.readObject();
		} catch (EOFException eofe) {
			// file was empty (as in the case of a new file); do nothing
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (doc == null) {
			doc = new Block(Container.TYPE_ROOT);
			Container preface = new Block(Container.TYPE_PARAGRAPH);
			preface.add(new TextRun("package org.eclipse.gef.examples.text")); //$NON-NLS-1$
			doc.add(preface);
			Container imports = new Block(Container.TYPE_IMPORT_DECLARATIONS);
			doc.add(imports);
			imports.add(new TextRun("org.eclipse.draw2d", TextRun.TYPE_IMPORT)); //$NON-NLS-1$
			imports.add(new TextRun("org.eclipse.gef", TextRun.TYPE_IMPORT)); //$NON-NLS-1$
			Container block = new Block(Container.TYPE_COMMENT);
			block.add(new TextRun(
					"""
							Copyright (c) 2005 IBM Corporation and others. All rights reserved. This program and \
							the accompanying materials are made available under the terms of the Eclipse Public \
							License v1.0 which accompanies this distribution, and is available at \
							http://www.eclipse.org/legal/epl-v10.html (\u7325\u7334\u7329\u7325\u7334\u7329\u7325\u7334\u7329\u7325\u7334\u7329\u7325\u7334\u7329\u7325\u7334\u7329\u7325\u7334\u7329\u7325\u7334\u7329\u7325\u7334\u7329\u7325\u7334\u7329\u7325\u7334\u7329\u7325\u7334\u7329\u7325\u7334\u7329\u7325\u7334\u7329\u7325\u7334\u7329\u7325\u7334\u7329\u7325\u7334\u7329\u7325\u7334\u7329\u7325\u7334\u7329\u7325\u7334\u7329\u7325\u7334\u7329\u7325\u7334\u7329\u7325\u7334\u7329\u7325\u7334\u7329\u7325\u7334\u7329\u7325\u7334\u7329)\r
							Contributors:
							    IBM Corporation - initial API and implementation
							\u0630\u0628\u063a \u0634\u0635\u062c\u062d (Saeed Anwar) - \u0634\u0635\u062c\u062d \
							\u0638\u0635\u0634\u0637\u0635\u0639\u0633 \u0635\u0639\u0633\u0640 \u0630\u0628\u063a (Bug 113700)""")); //$NON-NLS-1$
			doc.add(block);

			Container code = new Block(Container.TYPE_PARAGRAPH);
			code.getStyle().setFontFamily("Courier New"); //$NON-NLS-1$
			doc.add(code);
			code.add(new TextRun("""
					public void countToANumber(int limit) {
					    for (int i = 0; i < limit; i++)
					        System.out.println("Counting: " + i); //$NON-NLS-1$

					}""", //$NON-NLS-1$
					TextRun.TYPE_CODE));
		}

		setPartName(file.getName());
	}

}