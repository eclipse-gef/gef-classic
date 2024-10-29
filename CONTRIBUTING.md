# Contributing to the Eclipse Graphical Editing Framework (GEF) Classic

This guide provides all necessary information to enable [contributors and committers](https://www.eclipse.org/projects/dev_process/#2_3_1_Contributors_and_Committers) to contribute to the Eclipse Graphical Editing Framework (GEF) project's classic components. [Adopters](https://www.eclipse.org/projects/dev_process/#2_3_3_Adopters), who want to develop applications against GEF Classic, should rather consult our [README](https://github.com/eclipse-gef/gef-classic/blob/master/README.md#eclipse-graphical-editing-framework-gef).

## Set up a development environment

You can set up a pre-configured IDE for the development of GEF Classic using the following link:

[![Create Eclipse Development Environment for GEF Classic](https://download.eclipse.org/oomph/www/setups/svg/GEF_Classic.svg)](https://www.eclipse.org/setups/installer/?url=https://raw.githubusercontent.com/eclipse-gef/gef-classic/master/setup/GEFConfiguration.setup&show=true "Click to open Eclipse-Installer Auto Launch or drag onto your running installer's title area")

### Set up an Eclipse IDE
We recommend to download a recent '[Eclipse IDE for Eclipse Committers](http://www.eclipse.org/downloads/packages)' package and to prepare it as outlined below.

#### Install PDE API Tools Execution Environment Descriptions
All production bundles (i.e. excluding doc and test bundles) are configured to have PDE API tooling enabled. The API checks, which are configured on a project-specific basis, include checks for references that do not exist in the specified execution environment. As API tooling requires execution environment descriptions (see [Installing Execution Environment Descriptions](https://wiki.eclipse.org/Execution_Environments#Installing_Execution_Environment_Descriptions) for details) to perform the respective checks, these need to be made available, because otherwise you will see warnings for missing execution environment descriptions.

#### Install Maven Integration (m2e)
In case you want to run a headless build locally in your workspace, the [Maven Integration (m2e)](http://eclipse.org/m2e/) is needed, as the headless build is based on Maven/Tycho.

Please install *m2e - Maven Integration for Eclipse* in a version matching your IDE.

#### Install Eclipse RelEng Tools
To ensure that bundle versions and Maven pom.xml versions are in sync, the Eclipse RelEng Tools are very helpful.

It is recommended that you install the 'Eclipse RelEng Tools' into your IDE. Please make sure that you install it in a version matching the your IDE. For Neon, it can be installed in version 3.9.0 from [http://download.eclipse.org/eclipse/updates/4.6](http://download.eclipse.org/eclipse/updates/4.6).

#### Check out the code using EGit
Having set up the Eclipse IDE, next is to [fork the repository](https://help.github.com/articles/fork-a-repo/) and to check out the sources into the local workspace. Using EGit, this can be performed as follows:

1. Clone the forked repository:
    - Copy the URI of your repository fork to your clipboard.  
    - Open the *Git Repository Exploring Perspective* (provided by EGit) within Eclipse, and from the toolbar of the *Git Repositories Browser* view select to *Clone a Git Repository and add the clone to this view*. 
    - If you have copied the URL of your fork into the clipboard before, the upcoming *Clone Git Repository* dialog should already provide the necessary entries for *URI*, *Host*, and *Repository path*, so you may simple forward by pressing *Next >*.
    - Select the branches you want to clone from remote. The *master* branch is the one used for the current development stream. Development in maintenance releases is performed in respective maintenance branches. After having selected all branches of interest, press *Next >* to continue.
    - Choose a local directory to store the cloned repository (the default will be located under your home directory) and select the *Initial branch* to check out.
2. Checkout the projects into your IDE's workspace:
    - Right-click the *Working directory* entry, located under the repository within the *Git Repositories Browser* view and from the context menu select to *Import Projects...*.
    - In the upcoming *Import Projects from Git Repository* dialog, select to *Import existing projects* and press *Next >*.
    - Choose to import all projects and press *Finish* to conclude.

### Set Target Platform
A target definition file is provided by the `target-platform` project. To specify the target platform, simply open the respective target definition (e.g. *2022-03.target*) within the *Target Editor*, let it fully resolve (i.e. wait until the *Resolving Target Definition* background task has finished and the installable units are listed under the respective *Locations*), then choose to *Set as Target Platform*).

In case the target editor does not properly resolve the target definition, an invalid target cache may be the cause. In such cases, opening the target definition file with a text editor and incrementing the sequence number manually, will invalidate the cache when reopening it with the target editor again. After the cache has been invalidated, the sequence number can be restored.

### Define API Baseline
All production code bundles (i.e. excluding doc and test bundles) are configured to have PDE API tooling enabled. It is used to ensure compliance of sources to the specified execution environments, as well as to ensure proper semantic versioning. As PDE API tooling requires the definition of an API baseline you will see errors after having checked out the code ('An API baseline has not been set for the current workspace.').

API-baselines are provided by the `org.eclipse.gef.baseline` project. You may define them by going to *Preferences -> Plug-in Development -> API Baselines*, then choose to select "Add Baseline..." and point to the `plugins` sub-folder of an API baseline located in the baselines project (note that the dialog browses the file system instead of the workspace, so you will have to point into the respective folder in your local Git repository).

## Create and test a contribution
Having properly set up your development environment you can start working on the necessary changes. Please ensure to execute the *all* test-suite that exists for each bundle that was changed to be safe against regressions. In case your change is not trivial, it is recommended to add respective tests to demonstrate that the fix is adequate. You can either call the *AllTests* suite contained in the test bundles of all GEF components, or run a headless Maven build locally (which will also execute the tests).

## Run a headless build
GEF uses a Maven/Tycho-based build infrastructure. With the [Eclipse Maven Integration](http://eclipse.org/m2e/), a headless build can also be executed in the local workspace. Make sure you have checked out all projects of the respective git repository. Then easily run the build by right-clicking the `pom.xml` file located in the root directory of the GEF classic repository, and selecting 'Run As -> Maven build...'.

In the configuration wizard you have to specify the goals ("clean verify"). The build needs to be executed with a Java 17 VM, so if you have installed multiple JVMs, you should check the settings in the *JRE* tab.

As a result of the build, an update-site will be created in the `target` sub-folder of the `org.eclipse.gef.repository` project.

## Deliver a Contribution
Before any contribution can be accepted by the project, you need to electronically sign the [Eclipse Contributor Agreement (ECA)](https://www.eclipse.org/legal/ECA.php). Further, your changes should be comprised to a single commit (squash if necessary) and the commit needs to be properly [signed off](https://wiki.eclipse.org/Development_Resources/Contributing_via_Git#Signing_off_on_a_commit) to mark its CLA-compliance.

The commit message should follow the following structure: 

	'['(<bug-id>|'NONE')']' <one-line-summary-or-single-line-commit-message>
    
  	 (<wrapped-detailed-commit-message>)?
     
     Signed-off-by: <author-name> '<'<author-email>'>'
     Bug: <url-of-bugzilla-issue>

Example:

	[#493136]: Create DotSampleGraphs.xtend with sample graph definitions.
	
	- Extract the definitions of the sample dot graphs into a separate DotSampleGraphs.xtend file (utilize the xtend multi-line string functionality).
	- Reuse these sample graph definitions within the DotParser and DotInterpreter test cases.
	- Modify the org.eclipse.gef.dot.tests plugin to be able to handle the Xtend generated files (.classpath, .gitignore, .project, build.properties, pom.xml).
	- Implement additional DotParser test cases.

If your contribution does not exceed the contribution limit (currently 1000 LOC) this will allow us to directly accept a pull request. If your contribution was not developed from scratch or contains content from other authors, please indicate this on the related Github Issue entry. As in cases where the contribution exceeds the limit, we will have to open a contribution questionnaire (and may have to ask for additional information via the related Github Issue entry) to handle such cases in compliance to the [Eclipse Legal Process](http://eclipse.org/legal/EclipseLegalProcessPoster.pdf).

## Eclipse Development Process

This Eclipse Foundation open project is governed by the Eclipse Foundation
Development Process and operates under the terms of the Eclipse IP Policy.

* https://eclipse.org/projects/dev_process
* https://www.eclipse.org/org/documents/Eclipse_IP_Policy.pdf

## Eclipse Contributor Agreement

In order to be able to contribute to Eclipse Foundation projects you must
electronically sign the Eclipse Contributor Agreement (ECA).

* http://www.eclipse.org/legal/ECA.php

The ECA provides the Eclipse Foundation with a permanent record that you agree
that each of your contributions will comply with the commitments documented in
the Developer Certificate of Origin (DCO). Having an ECA on file associated with
the email address matching the "Author" field of your contribution's Git commits
fulfills the DCO's requirement that you sign-off on your contributions.

For more information, please see the Eclipse Committer Handbook:
https://www.eclipse.org/projects/handbook/#resources-commit


