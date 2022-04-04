# Eclipse Graphical Editing Framework (GEF) Classic

This repository contains the code base of the [Eclipse Graphical Editing Framework (GEF)](http://www.eclipse.org/gef/) project's classic components: 
 - [GEF (MVC) 3.x](https://www.eclipse.org/gef/gef_mvc/index.php)
 - [Draw2d 3.x](https://www.eclipse.org/gef/draw2d/index.php) 
 - [Zest 1.x](https://www.eclipse.org/gef/zest/index.php)
 
The code base of the GEF components implemented in JavaFX is located in the [eclipse/gef](https://github.com/eclipse/gef) repository instead.

## Getting started with the framework components ([adopters](https://www.eclipse.org/projects/dev_process/#2_3_3_Adopters))
In order to develop graphical applications with GEF Classic, you should first set up a proper development environment. The following sections shortly lay out how to set up an Eclipse IDE for this purpose. They conclude with running our deployed and undeployed examples to confirm everything is set up properly. 

Having accomplished that, you might want to browse our [developer documentation](https://github.com/eclipse/gef-classic/wiki#developer-documentation) to learn about the framework components in detail. At any time, if you get stuck, feel free to [contact us](https://projects.eclipse.org/projects/tools.gef/contact).

### Set up an Eclipse IDE
1. Install a recent **Java SE Development Kit 11** as a prerequisite.
2. Download an '[Eclipse IDE for Eclipse Committers ](http://www.eclipse.org/downloads/packages)' package and start it, pointing to an empty workspace folder. 

### Set up a Target Definition containing GEF Classic (development snapshot)

1. Import of the target-platform project from the Git repository
2. Open the 2022-03.target file with the *Target Editor* again, using the *Open With* context menu, let if fully resolve, then click *Set as Target Platform* (link in the upper right corner of the editor).

You can also create a new target file using https://github.com/eclipse/gef-classic/blob/master/target-platform/2022-03.target as template.olve, then click *Set as Target Platform* (link in the upper right corner of the editor).

### Run the examples
As the deployed 'Flow', 'Logic', 'Shapes', and 'WYSIWYG Document' GEF (MVC) examples are contained in the target definition, we only need to start a new Eclipse Runtime to run them: 

1. Go to *Run -> Run Configurations...* then create a new *Eclipse Application* launch configuration and *Run* it.
2. Go to *File -> New -> Project...* and select to create a new *General/Project*. Name it `gef-logic-example` or as you like.
3. Go to *File -> New -> Example...* and select to create a new *GEF (Graphical Editing Framework)/Logic Diagram*, choosing the *Four-bit Adder Model* from the *Logic Model Samples* section.

The undeployed Draw2d and Zest examples have to be checked out in source before. Using EGit this can easily be achieved as follows:

1. Go to *File -> Import...*, then select *Git/Projects from Git*, press *Next >*.
2. Select *Clone URI*, press *Next >*.
3. Paste `https://github.com/eclipse/gef-legacy.git` to the *URI* field , press *Next >*.
3. Select *master* branch, press *Next >*.
4. Confirm the local directory or change it as needed, press *Next >*.
5. Ensure *Import existing Eclipse projects* is checked, then select *Working Tree* and press *Next >*.
5. Select `org.eclipse.draw2d.examples` and `org.eclipse.zest.examples`, press *Finish*.
6. Select an arbitrary example class, e.g. `org.eclipse.zest.examples.jface.GraphJFaceSnippet1`, in the *Package Explorer* view and select *Run As -> Java Application* from the context menu.

## How to proceed from here?
The first thing you will probably want to consult is the developer documentation, which explains the different framework components in detail. It is bundled by the individual SDK features that are available for the framework components and can be accessed via *Help -> Help Contents* if these features are installed into the Eclipse IDE (its not sufficient to include them in a target definition for this purpose). It is further contributed to [help.eclipse.org](http://help.eclipse.org/) for each release, where it can be accessed online.<sup>6)</sup>

All further project information (forum, mailing list, issue tracker, update-site locations, release plans) can be retrieved from the project meta-data at [projects.eclipse.org](https://projects.eclipse.org/projects/tools.gef).

If you want to contribute, please consult the [contributor guide](https://github.com/eclipse/gef-legacy/blob/master/CONTRIBUTING.md#contributing-to-the-eclipse-graphical-editing-framework-gef).
