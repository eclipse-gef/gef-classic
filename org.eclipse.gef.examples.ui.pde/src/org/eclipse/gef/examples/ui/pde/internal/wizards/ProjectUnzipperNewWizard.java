/*******************************************************************************
 * Copyright (c) 2006, 2010 IBM Corporation and others.
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

package org.eclipse.gef.examples.ui.pde.internal.wizards;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import org.eclipse.gef.examples.ui.pde.internal.GefExamplesPlugin;
import org.eclipse.gef.examples.ui.pde.internal.l10n.Messages;

/**
 * This abstract wizard serves as the base for our zipped project wizards. At
 * minimum, the wizard uses its constructor parameter to create a 1 page wizard.
 * The page lets users define the name and location of the project where to
 * unzip the project archive. The wizard performs: project creation, the unzip
 * operation, the classpath update, the progress monitoring
 *
 * @see Wizard
 */
abstract public class ProjectUnzipperNewWizard extends Wizard implements INewWizard, IExecutableExtension {

	/**
	 * Java Nature
	 */
	private static final String ORG_ECLIPSE_JDT_CORE_JAVANATURE = "org.eclipse.jdt.core.javanature"; //$NON-NLS-1$

	/**
	 * PDE Nature
	 */
	private static final String ORG_ECLIPSE_PDE_PLUGIN_NATURE = "org.eclipse.pde.PluginNature"; //$NON-NLS-1$

	/**
	 * The single page provided by this base implementation. It provides all the
	 * functionality required to capture the name and location of the target project
	 */
	private WizardNewProjectCreationPage wizardNewProjectCreationPage;

	/**
	 * The name of the project creation page
	 */
	private String pageName;

	/**
	 * The title of the project creation page
	 */
	private String pageTitle;

	/**
	 * The description of the project creation page
	 */
	private String pageDescription;

	/**
	 * The name of the project in the project creation page
	 */
	private String pageProjectName;

	/**
	 * The list of paths pointing to the location of the project archives
	 */
	private URL[] projectZipURL;

	/**
	 * The list of formats to be applied to the user supplied name
	 */
	private String[] nameFormats;

	/**
	 * The configuration element associated with this new project wizard
	 */
	private IConfigurationElement config;

	/**
	 * Constructor
	 *
	 * @param pageNameIn        The name of the project creation page
	 * @param pageTitleIn       The title of the project creation page
	 * @param pageDescriptionIn The description of the project creation page
	 * @param pageProjectNameIn The project name in the project creation page
	 * @param projectZipURLIn   The URL pointing to the location of the project
	 *                          archive
	 */
	public ProjectUnzipperNewWizard(String pageNameIn, String pageTitleIn, String pageDescriptionIn,
			String pageProjectNameIn, URL projectZipURLIn) {
		this(pageNameIn, pageTitleIn, pageDescriptionIn, pageProjectNameIn, new URL[] { projectZipURLIn },
				new String[] { "{0}" }); //$NON-NLS-1$
	}

	/**
	 * Constructor
	 *
	 * @param pageNameIn          The name of the project creation page
	 * @param pageTitleIn         The title of the project creation page
	 * @param pageDescriptionIn   The description of the project creation page
	 * @param pageProjectNameIn   The project name in the project creation page
	 * @param projectZipURLListIn The list of URL pointing to the location of the
	 *                            project archives
	 * @param formatsIn           The list of formats to be applied to the user
	 *                            supplied name. The {@link java.text.MessageFormat}
	 *                            class should be consulted to understand
	 *                            substitutions. The &quot;{0}&quot; substitution
	 *                            will be overridden with the user supplied name.
	 *                            Otherwise, the absolute strings may be passed in,
	 *                            which will completely ignore the user supplied
	 *                            name.
	 */
	public ProjectUnzipperNewWizard(String pageNameIn, String pageTitleIn, String pageDescriptionIn,
			String pageProjectNameIn, URL[] projectZipURLListIn, String[] nameFormatsIn) {
		super();

		// assert projectZipURLListIn.length > 0;
		// assert nameFormatsIn.length > 0;
		// assert projectZipURLListIn.length == nameFormatsIn.length;

		pageName = pageNameIn;
		pageTitle = pageTitleIn;
		pageDescription = pageDescriptionIn;
		pageProjectName = pageProjectNameIn;
		projectZipURL = projectZipURLListIn;
		nameFormats = nameFormatsIn;
		setNeedsProgressMonitor(true);
	}

	/**
	 * Performs the bulk of the wizard functionality: project creation, the unzip
	 * operation and classpath update
	 *
	 * @see Wizard#performFinish
	 */
	@Override
	public boolean performFinish() {

		try {
			IRunnableWithProgress operation = new WorkspaceModifyOperation() {

				@Override
				public void execute(IProgressMonitor monitor) throws InterruptedException {
					try {
						monitor.beginTask(Messages.monitor_creatingProject, 120);

						/*
						 * Create the project folder
						 */
						IPath projectPath = wizardNewProjectCreationPage.getLocationPath();

						for (int i = 0; i < nameFormats.length; i++) {
							String projectName = MessageFormat.format(nameFormats[i],
									new Object[] { wizardNewProjectCreationPage.getProjectName() });
							String projectFolder = projectPath.toOSString() + File.separator + projectName;
							File projectFolderFile = new File(projectFolder);

							IWorkspace workspace = ResourcesPlugin.getWorkspace();
							IProject project = workspace.getRoot().getProject(projectName);

							// If the project does not exist, we will create it
							// and populate it.
							if (!project.exists()) {
								projectFolderFile.mkdirs();
								monitor.worked(10);

								/*
								 * Copy plug-in project code
								 */
								extractProject(projectFolderFile, getProjectZipURL()[i],
										new SubProgressMonitor(monitor, 100));

								if (monitor.isCanceled()) {
									throw new InterruptedException();
								}

								if (projectPath.equals(workspace.getRoot().getLocation())) {
									project.create(monitor);
								} else {
									IProjectDescription desc = workspace.newProjectDescription(project.getName());
									desc.setLocation(new Path(projectFolder));

									project.create(desc, monitor);
								}
							}

							// Now, we ensure that the project is open.
							project.open(monitor);

							renameProject(project, projectName);

							// Add Java and PDE natures
							IProjectDescription desc = workspace.newProjectDescription(project.getName());
							desc.setNatureIds(
									new String[] { ORG_ECLIPSE_PDE_PLUGIN_NATURE, ORG_ECLIPSE_JDT_CORE_JAVANATURE });
							project.setDescription(desc, monitor);

							monitor.worked(10);
							if (monitor.isCanceled()) {
								throw new InterruptedException();
							}
						} // end for

					} catch (IOException e) {
						throw new RuntimeException(e);
					} catch (CoreException e) {
						throw new RuntimeException(e);
					} finally {
						monitor.done();
					}
				}
			};

			getContainer().run(false, true, operation);

			/* Set perspective */
			BasicNewProjectResourceWizard.updatePerspective(getConfigurationElement());

		} catch (InterruptedException e) {
			return false;

		} catch (Exception e) {

			GefExamplesPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, "org.eclipse.gef.examples.ui.pde", //$NON-NLS-1$
					IStatus.OK, e.getMessage(), e));
			return false;
		}

		return true;
	}

	/**
	 * Unzip the project archive to the specified folder
	 *
	 * @param projectFolderFile The folder where to unzip the project archive
	 * @param monitor           Monitor to display progress and/or cancel operation
	 * @throws IOException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws FileNotFoundException
	 *
	 * @throws FileNotFoundException
	 * @throws InterruptedException
	 */
	private void extractProject(File projectFolderFile, URL url, IProgressMonitor monitor)
			throws FileNotFoundException, IOException, InterruptedException {

		/*
		 * Get project archive
		 */
		// URL urlZip = PdeUiPlugin.getDefault().find();
		// URL urlZipLocal = Platform.asLocalURL(urlZip);
		URL urlZipLocal = FileLocator.toFileURL(url);

		/*
		 * Walk each element and unzip
		 */
		ZipFile zipFile = new ZipFile(urlZipLocal.getPath());

		try {
			/*
			 * Allow for a hundred work units
			 */
			monitor.beginTask(Messages.monitor_unzippingProject, zipFile.size());

			unzip(zipFile, projectFolderFile, monitor);
		} finally {
			zipFile.close();
			monitor.done();
		}
	}

	/**
	 * Unzips the platform formatted zip file to specified folder
	 *
	 * @param zipFile           The platform formatted zip file
	 * @param projectFolderFile The folder where to unzip the project archive
	 * @param monitor           Monitor to display progress and/or cancel operation
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws InterruptedException
	 */
	private void unzip(ZipFile zipFile, File projectFolderFile, IProgressMonitor monitor)
			throws IOException, FileNotFoundException, InterruptedException {

		Enumeration e = zipFile.entries();

		while (e.hasMoreElements()) {
			ZipEntry zipEntry = (ZipEntry) e.nextElement();
			File file = new File(projectFolderFile, zipEntry.getName());
			if (!file.toPath().normalize().startsWith(projectFolderFile.toPath().normalize())) {
				throw new IOException("Bad zip entry");
			}

			if (false == zipEntry.isDirectory()) {

				/*
				 * Copy files (and make sure parent directory exist)
				 */
				File parentFile = file.getParentFile();
				if (null != parentFile && false == parentFile.exists()) {
					parentFile.mkdirs();
				}

				Path path = new Path(file.getPath());
				if (path.getFileExtension().equals("java")) { //$NON-NLS-1$
					InputStreamReader is = null;
					OutputStreamWriter os = null;

					try {
						is = new InputStreamReader(zipFile.getInputStream(zipEntry), "ISO-8859-1"); //$NON-NLS-1$
						os = new OutputStreamWriter(new FileOutputStream(file), ResourcesPlugin.getEncoding());
						char[] buffer = new char[102400];
						while (true) {
							int len = is.read(buffer);
							if (len < 0) {
								break;
							}
							os.write(buffer, 0, len);
						}
					} finally {
						if (null != is) {
							is.close();
						}
						if (null != os) {
							os.close();
						}
					}
				} else {
					InputStream is = null;
					OutputStream os = null;

					try {
						is = zipFile.getInputStream(zipEntry);
						os = new FileOutputStream(file);

						byte[] buffer = new byte[102400];
						while (true) {
							int len = is.read(buffer);
							if (len < 0) {
								break;
							}
							os.write(buffer, 0, len);
						}
					} finally {
						if (null != is) {
							is.close();
						}
						if (null != os) {
							os.close();
						}
					}
				}
			}

			monitor.worked(1);

			if (monitor.isCanceled()) {
				throw new InterruptedException();
			}
		}
	}

	/**
	 * Renames the specified project to the specified name
	 *
	 * @param project     Project to rename
	 * @param projectName New name for the project
	 * @throws CoreException
	 */
	private void renameProject(IProject project, String projectName) throws CoreException {
		IProjectDescription description = project.getDescription();
		description.setName(projectName);
		project.move(description, IResource.FORCE | IResource.SHALLOW, null);
	}

	/**
	 * Creates the sole wizard page contributed by this base implementation; the
	 * standard Eclipse WizardNewProjectCreationPage.
	 *
	 * @see WizardNewProjectCreationPage#WizardNewProjectCreationPage(String)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {

		wizardNewProjectCreationPage = new WizardNewProjectCreationPage(getPageName());

		wizardNewProjectCreationPage.setTitle(getPageTitle());

		wizardNewProjectCreationPage.setDescription(getPageDescription());

		wizardNewProjectCreationPage.setInitialProjectName(getPageProjectName());

		this.addPage(wizardNewProjectCreationPage);
	}

	/**
	 * Accessor to the pageName field
	 *
	 * @return The pageName field value
	 */
	private String getPageName() {
		return pageName;
	}

	/**
	 * Accessor to the pageTitle field
	 *
	 * @return The pageTitle field value
	 */
	private String getPageTitle() {
		return pageTitle;
	}

	/**
	 * Accessor to the pageDescription field
	 *
	 * @return The pageDescription field value
	 */
	private String getPageDescription() {
		return pageDescription;
	}

	/**
	 * Accessor to the PageProjectName field
	 *
	 * @return The PageProjectName field value
	 */
	private String getPageProjectName() {
		return pageProjectName;
	}

	/**
	 * Accessor to the ProjectZipURL field
	 *
	 * @return The projectZipURL field value
	 */
	private URL[] getProjectZipURL() {
		return projectZipURL;
	}

	/**
	 * Accessor to the config field
	 *
	 * @return The config field value
	 */
	private IConfigurationElement getConfigurationElement() {
		return config;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org
	 * .eclipse.core.runtime.IConfigurationElement, java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public void setInitializationData(IConfigurationElement configIn, String propertyName, Object data)
			throws CoreException {
		config = configIn;
	}

}