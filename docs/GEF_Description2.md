

GEF Description2
================

Contents
--------

*   [1 Capturing user actions on the GUI and translating them into changes on the model](#Capturing-user-actions-on-the-GUI-and-translating-them-into-changes-on-the-model)
    *   [1.1 EditDomain](#EditDomain)
    *   [1.2 Global principle](#Global-principle)
    *   [1.3 Event flow](#Event-flow)
    *   [1.4 Tools, Requests, feedback](#Tools.2C-Requests.2C-feedback)
    *   [1.5 Commands](#Commands)
    *   [1.6 CommandStack](#CommandStack)
    *   [1.7 EditPolicies](#EditPolicies)
    *   [1.8 Tools, Requests, EditPolicies : How to get started with this ?](#Tools.2C-Requests.2C-EditPolicies-:-How-to-get-started-with-this-.3F)
    *   [1.9 Palette](#Palette)
*   [2 Connections](#Connections)
    *   [2.1 Draw2d part](#Draw2d-part)
        *   [2.1.1 Polyline class](#Polyline-class)
        *   [2.1.2 ConnectionRouter interface](#ConnectionRouter-interface)
        *   [2.1.3 ConnectionLayer class](#ConnectionLayer-class)
        *   [2.1.4 PolylineConnection class](#PolylineConnection-class)
        *   [2.1.5 Examples](#Examples)
    *   [2.2 GEF part](#GEF-part)
        *   [2.2.1 The view: A Connection implementation](#The-view:-A-Connection-implementation)
        *   [2.2.2 The controller: An AbstractConnectionEditPart implementation](#The-controller:-An-AbstractConnectionEditPart-implementation)
        *   [2.2.3 How to add the connections to the diagram](#How-to-add-the-connections-to-the-diagram)
        *   [2.2.4 What about ConnectionAnchors ? NodeEditPart interface](#What-about-ConnectionAnchors-.3F-NodeEditPart-interface)
        *   [2.2.5 What about the ConnectionRouter ?](#What-about-the-ConnectionRouter-.3F)
        *   [2.2.6 Some details...](#Some-details...)

Capturing user actions on the GUI and translating them into changes on the model
--------------------------------------------------------------------------------

Now the view is created when the editor is opened according to the state of the model at this time, and the view is updated when the model changes, whatever the way the model is modified, by GEF or by any other way. This is already something really useful that could be used like that to display an evolving model. But if your goal is to allow the user to modify your model graphically, this is not enough, you have to react on the user actions on the GUI and to modify the model accordingly :

![Gef desc2 im16.gif](https://raw.githubusercontent.com/eclipse-gef/gef-classic/master/docs/images/Gef_desc2_im16.gif)

This process involves a lot of new GEF classes, so before going further I will introduce them. Also, don't be confused by my picture : the EditParts play a central role in this process too but I can't detail it on this little drawing without making it look like a big knot.

### EditDomain

The EditDomain is the common denominator to all GEF objects which participate to an editing session of a model. It binds all the things together and provides all the components with some common features necessary to edit the model like a CommandStack and a PaletteViewer with Tools.

Here is a picture of the EditDomain and the different elements it binds together. The golden arrows show how the user actions are translated from mouse moves and key stroke events into modifications of the model. Don't be afraid by this picture, all this process is handled by GEF. In order to allow edition, you only need to implement two types of classes : Commands and EditPolicies.

![Gef desc2 im17.gif](https://raw.githubusercontent.com/eclipse-gef/gef-classic/master/docs/images/Gef_desc2_im17.gif)

You don't really have to worry about instanciating and putting all these things together. To begin, use a subclass of GraphicalEditorPart or GraphicalEditorPartWithPalette. These classes set all these things up for you. You only need to override some methods to configure the GraphicalViewer, the PaletteViewer and things like that. That's easy, just copy-paste some code from an existing gef plugin, don't waste your time on this, it isn't an important part of the explanations.

If you don't use these classes, then you will have to instanciate EditDomain and to give it the EditPartViewers you will use and the PaletteRoot.

\[ TODO : provide an example of this. \]

### Global principle

Here is an overview of the way GEF translates user actions on the gui into changes on the model. The comments follow the golden arrows on the preceding picture showing the EditDomain. This will be explained in a more detailed way in the next sections, just try to get the general idea.

1.  The user acts on the GUI with mouse and keyboard.
2.  User actions on the gui result in events caught by an EditPartViewer. The EditPartViewer forwards all these events to the active tool.
3.  The active tool interprets this sequence of events to build Requests. Requests are objects used by GEF to specify which operations have to be executed on the model without specifying how to do them.
4.  The active tool sends the Requests to EditParts through three main different methods : one to get Commands implementing the Requests, and two other ones to ask the EditPart to show feedback for the Requests. Commands are objects implementing the modifications of the model specified in the Requests. They have execute, undo and redo methods and these methods can be called to do concrete modifications of the model. EditParts forward the Requests they receive to their installed EditPolicies. EditPolicies are objects able to build Commands in return to some types of Requests, and to show feedbacks for these Requests.
5.  After obtaining a Command from an EditPart, the active tool can execute it through the CommandStack. The CommandStack is an implementation of a redo - undo stack.
6.  The CommandStack executes the Command and the model is modified, and this triggers an update of the view, as explained before.

You hook into this process by providing two things :

*   subclasses of Command which implement concrete modifications of your model for each request you are interested to catch,
*   subclasses of EditPolicies which translate the Requests sent by the active tool into Commands modifying your model and show appropriate feedback for these Requests.

### Event flow

Events occuring on Controls where EditPartViewers are installed are what triggers all the editing process. GEF doesn't use any listeners on draw2d figures to capture events. Here is a picture from the IBM redbook which illustrates the event flow :

![Gef desc2 im18.gif](https://raw.githubusercontent.com/eclipse-gef/gef-classic/master/docs/images/Gef_desc2_im18.gif)

All the events occuring on a SWT Control on the top of which an EditPartViewer is installed are forwarded to something called the "active tool" of the EditDomain. The active tool knows from which EditPartViewer the events it receives come.

But what if there are listeners installed on the draw2d Figures ? Who will receive the events in this case ? GEF, Draw2d or the two of them ?

The Draw2d LightWeightSystem installed by each GraphicalViewer on top of its Control uses a special EventDispatcher. First this EventDispatcher sends the events it receives to Draw2d. If an event is consumed by Draw2d (there is a method in the event class to check whether or not it has already been consumed) the process ends here. If it hasn't, it is sent to the EditDomain and is forwarded to the active tool.

In other words, Draw2d always gets a chance to catch the event before GEF does and at most one of them catch it.

### Tools, Requests, feedback

GEF needs a way to translate raw events coming from the actions of the user on the interface into high level editing operation orders with an associated contract ("move this element here", "add this element to this parent", "reconnect this connection to this node", "create a new child of this type for this parent",...). Requests are such orders.

Tools are objects able to receive events resulting from user actions on an EditPartViewer. They are finite state machines which track the sequence of the events they receive and depending on this sequence, build instances of subclasses of Request and send them to the appropriate EditPart(s).

Gef provides implementation for the most common Tools and Requests.

More about Tools :

To interpret the mouse events they receive from an EditPartViewer, Tools can use the EditPartViewer.findEditPartAt(Point p, ...) method of the source EditPartViewer to know which EditPart is at the mouse location, and the EditPartViewer.findHandleAt(...) method to know if there is a "handle" at this mouse location (in two words, handles are the little black squares that allow the SelectionTool to do some specific operation temporarily, like resizing an element...) :

*   The findEditPartAt() method can perform a conditional search in the figure tree of the EditPartViewer to find the top-most EditPart under the mouse location satisfying some conditions. Without taking into account the conditional aspects of the search it works like that (it isn't implemented like that but the result is the same) : it finds the topmost figure under the mouse location, then begins to climb along the parent chain of this figure to find the first parent figure which is directly associated with an EditPart and this EditPart is returned.
*   The findHandleAt() method finds the topmost figure in the "handle layer" implementing the Handle interface and returns that Handle.

The active tool sends Requests to EditParts to :

*   get a Command implementing this request (this will be explained later...),
*   show / erase source feedback for this request,
*   show / erase target feedback for this request.

Feedback : most of the time, as it receives the sequence of events, the active tool maintains a Request which is the one which would be "executed" if the user ended the current operation here. When this Request is updated by the active tool or when the EditPart(s) to which this request would apply change(s), the active tool sends the appropriate showSourceFeedback/showTargetFeedback and eraseTargetFeedback/eraseSourceFeedback messages with this Request as a parameter to the EditParts to which that Request would apply. This enables EditParts to show some preview of what the result of the current operation would be if the user ended the operation here.

source/target feedback : some operations involve two EditParts, like a child and his parent or future parent, a connection and one of its extremities,... These two EditParts are called the source and the target. In such cases, for one single request, two distinct kinds of feedback are required : the source feedback is the one shown on the source and the target feedback is the one shown on the target. That's why there are distinct methods in EditPart and EditPolicies to handle these two kinds of feedback. Think about what happens when you move a file in the Windows Explorer : ghost icon of the file moving with the mouse pointer = source (the file) feedback, black line indicating the drop location in the folder = target (the folder) feedback.

More about Requests :

Requests encapsulate information about a modification of the model the user wants to be done, but in terms of EditParts because the Tools don't know anything about your model. There are different subclasses of Request provided by GEF to encapsulate different kinds of information. Each Request has a type attribute which determines the "meaning" (= associated contract) of the Request, i.e., what should be done (= which Command should be returned) when this Request is received by an EditPart.

When looking at the code, you may ask yourself the following questions : "But why isn't there a Request subclass for each Request type ?", "What is the purpose of the type attribute ? ", "Why doesn't the class type plays the role of the type attribute ?". Well, that's because the information carried by a request and its meaning are two different things. The Request class hierarchy groups in super classes Requests which share common information. This has nothing to do with the meaning of the different requests, which is represented by the type attribute of the Requests. For example, two requests with different meaning but carrying the same information could share the same class but would have a different type attribute.

Here is a picture illustrating this process :

![Gef desc2 im19.gif](https://raw.githubusercontent.com/eclipse-gef/gef-classic/master/docs/images/Gef_desc2_im19.gif)

### Commands

GEF can be used with any model you like. This is good news, but on the other hand this means that GEF is unable to modify your model by itself because the way to do it depends entirely on the properties of your own model. So you will have to provide some objects to allow GEF to modify your model and to write classes for these objects. Such objects are Commands.

For each request your are interested to catch on an EditPart, you have to write a Command subclass and you have to provide an instance of this class when it is required by GEF through the EditPart.getCommand(Request) method (we will see how to achieve that goal later in the EditPolicies section).

![Gef desc2 im21.gif](https://raw.githubusercontent.com/eclipse-gef/gef-classic/master/docs/images/Gef_desc2_im21.gif)

Commands are instances of subclasses of the Command class. This class declares the methods :

*   void execute() : this method will be called by GEF the first time the execution of the command is needed.
*   void undo() : this method will be called by GEF when undoing of the model modification applied by the execute() method is needed.
*   void redo() : this method will be called by GEF when redoing of the model modification applied by the execute() method is needed.
*   boolean canExecute() : this method should return true if the Command is executable and false otherwise. GEF will call this method before trying to execute a Command, so if it returns false, the Command will not be executed. Depending on the return of this method, GEF could also display some feedback showing that the operation the user is trying to achieve is not allowed.

You will have to override these methods to define the Command's behaviour, i.e., the modification of your model it will implement.

Thanks to the CompoundCommand class (extends Command), Commands can be chained to make complex commands. The execute method of such a compound command calls one after the other the execute methods of the children commands. CompoundCommands are Commands so they can also be chained with any other Commands.

Here is an example of a Command class adding a fruit to the list of fruits a person likes eating. Such a Command could be returned when a req_add is sent to the PersonEditPart by GEF.

Example :

public class AddFruitCommand extends Command{

         private Fruit fruit2add;
         private Person person;
         public AddFruitCommand(Fruit fruit2add, Person person){
                 this.fruit2add = fruit2add;
                 this.person = person;
         }
         public void execute(){
                 person.addFruit(fruit);
         }
         public void redo(){
                 execute();
         }
         public void undo(){
                 person.removeFruit(fruit);
         }
 }

Here is a sequence diagram showing what will happen when such a Command is executed by the CommandStack :

![Gef desc2 im22.gif](https://raw.githubusercontent.com/eclipse-gef/gef-classic/master/docs/images/Gef_desc2_im22.gif)

### CommandStack

CommandStack is an implementation of a redo-undo stack. There is one CommandStack per EditDomain. A Command can be executed through the CommandStack by calling CommandStack.execute(Command). Instanciating a Command subclass and executing this Command through the CommandStack is the only way you should be modifying your model because :

*   only the modifications of the model made this way will be undo-redoable (by calling redo() and undo() on the CommandStack),
*   the dirty state of the editor ( = whether or not the model of the editor is in synch with its last saved state) should be based on the CommandStack.isDirty() method, as explained in the next paragraph, so all the operations likely to modify this dirty state must be made through the CommandStack.

There is a markSaveLocation() method in CommandStack. This method must be called when the model is saved. When it is called, the CommandStack marks the top of the undo stack as the last saved model state. If the top of the undo stack is different from that mark, the isDirty() method returns false because it means that the model is potentially in a different state from the last saved state. The isDirty method of your IEditorPart class should delegate to the isDirty() method of the CommandStack.

Most of the time you don't have to worry about the CommandStack because the Tools execute themselves the Commands you provide through the CommandStack. But you can also modify the model through customized JFace Actions triggered by a menu item or a button, and in this case you will have to get the CommandStack from your editor and use it to execute some Command to modify the model.

Here is a picture illustrating all this :

![Gef desc2 im23.gif](https://raw.githubusercontent.com/eclipse-gef/gef-classic/master/docs/images/Gef_desc2_im23.gif)

### EditPolicies

Requests are sent to EditParts through these methods :

*   EditPart.getCommand(Request) : called to get from an EditPart a Command implementing the given Request,
*   EditPart.showTargetFeedback() and EditPart.eraseTargetFeedback() : called to ask an EditPart to show/erase target feedback for the given Request, that is, a preview of what would occur on this EditPart if the Command implementing this Request got executed,
*   EditPart.showSourceFeedback() and EditPart.eraseSourceFeedback() : ...

You could override these methods directly. But while doing so, you would notice that a lot of the code handling a given request type in each EditPart class is almost the same. For example, when you move an EditPart, whatever its Figure is, the code to show feedback is probably always the same. That's one of the reasons why the default implementations of these methods in AbstractEditPart delegate the job to dedicated objects : these are EditPolicies. As you could expect, the EditPolicy interface declares the same methods as the 5 explainded above, along with some less important others. So you should not override the above methods of EditPart, use EditPolicies instead.

More precisely, the default implementations of the above EditPart methods work like that :

*   EditPart.getCommand(Request) returns a CompoundCommand resulting from the chaining of the Commands returned by the EditPolicy.getCommand(Request) method of each EditPolicy installed on the EditPart,
*   the show/erase methods iterate over all installed EditPolicies to ask them to show/erase the feedback.

"Roles" :

In each EditPart, EditPolicies are stored in a key-value map. The key objects are called "roles". Roles can be any object you want but predefined roles are available as static variables of the EditPolicy interface and you should use them. A role represents a particular editing capability which can be implemented by one EditPolicy on an EditPart. Roles provide you with a flexible mechanism allowing you to replace easily at runtime an EditPolicy implementing a particular editing capability by another or to override the EditPolicy defined for a particular role in a superclass with a new one in the subclass just by installing another EditPolicy for the same role.

Installing EditPolicies on an EditPart :

You install EditPolicies on an EditPart by overriding the createEditPolicies() method (it will be called by GEF when needed) and by putting into it some installEditPolicy(arole, anEditPolicy) calls, without forgetting the call to super.createEditPolicies() before the rest of the method (to have the right overriding behaviour), like this :

protected void createEditPolicies() {

         super.createEditPolicies();
         installEditPolicy(EditPolicy.CONTAINER_ROLE, new SomeContainerEditPolicy());
 }

How are EditPolicies implemented in GEF and how to use them ?

Each EditPolicy deals with some request types. Most of the time, the getCommand() method is factorized into a few other methods, one for each request type, and sometimes these factor methods themselves are factorized into simpler methods. You will have to override or implement some of these simple factor methods to return your Commands. Most of the time, their names and parameters are selfdescriptive. There will also most likely be some convenience methods available to help you. See next section for a bit more details...sorry if this is fuzzy but I'm doing my best...

TODO : explaining the effect of a getCommand() method returning null and the differences with an unexecutable command return. Basically GEF interprets this as a not allowed operation and displays a red cursor to show it to the user but I don't know the details. If you know them, please add a paragraph about it.

### Tools, Requests, EditPolicies : How to get started with this ?

In my opinion this is the most complicated thing about GEF.

There is an article called [standard user interactions in GEF](https://archive.eclipse.org/modeling/searchcvs/cvssrc/www-gef/www-gef/reference/interactions.htmlt). The most useful user interactions are described and which Tools, Requests and EditPolicies are involved in their achievement.

Once you know which Tool to use to achieve some user interaction, on which Request to react and with which EditPolicy :

*   add a palette entry for this Tool in your palette,
*   write Command subclasses to implement the Requests you are interested to catch,
*   for the right EditPolicy, find out in the javadoc which methods to override to react on the Requests you are interested in and override them (you will most likely have to return instances of your Command subclasses),
*   install your EditPolicy subclass on the EditParts which must support this interaction.

### Palette

Tools are displayed to the user with a PaletteViewer. With the PaletteViewer, the user can choose a particular Tool called the active Tool. All the events sent by the EditPartViewers to the EditDomain are forwarded to the active Tool.

\[ TODO : explaining how the palette works and how to build one...\]

Connections
-----------

Connections are model objects that are represented by a link between the visual representations of two other model objects called the source and the target. Model objects likely to be a source or a target are called nodes.

Connections are particular model objects because their representations :

*   can't belong to a particular content pane as they must be able to link any EditPart representation to any other (remember the clipping system of draw2d and the bounds...),
*   must always be on top of the other diagram elements,
*   always link the representations of the source and the target and can't exist without them.

I think these are the reasons why GEF doesn't deal with connections the same as it does with other model objects.

I will try to explain :

*   the Draw2d part of this topic, i.e., which Draw2d classes are used to render them on the view and how these classes work,
*   the GEF part of the connections, i.e., the model / controller part.

### Draw2d part

In this part, the terms "connection", "source" and "target" will refer to a visual link and to its two ends, not to model objects.

#### Polyline class

Polylines are Figures made to represent a list of points with lines between them ( p0 -- p1, p1 -- p2, p2 -- p3,...). You can set the points of a Polyline by calling Polyline.setPoints(PointList) and some other methods.

Here are some interesting properties of Polylines :

*   You cannot set the bounds of a Polyline to move it or to resize it as you would do with most other Figures. The bounds are automatically calculated in the getBounds() method to be the smallest rectangle which completely includes all the points (taking the line width into account) for efficiency purposes. This implies you can't add a Polyline as a child of a Figure with a LayoutManager that sets the bounds of all the children (?).
*   Polylines are Shapes so you can set the line style and line width used to draw them by calling setLineStyle(int) and setLineWidth(int) (unfortunately there was a bug there last time I checked, maybe it is fixed now).
*   The containsPoint(Point) method is overriden to return true iff the Point is on the polyline or cointained by a child figure and this has the implications you can guess on the hit-testing algorithm of Draw2d (findFigureAt()).
*   When you translate the parent figure of a Polyline, the Polyline doesn't get translated.

![Gef desc2 im25.gif](https://raw.githubusercontent.com/eclipse-gef/gef-classic/master/docs/images/Gef_desc2_im25.gif)

ConnectionAnchors track the movements of their owner Figure and when this Figure moves, they notify their listeners to tell them the geometrical location they define may have changed. The methods add / removeAnchorListener(AnchorListener) allow to add such listeners to a ConnectionAnchor.

Draw2d provides some ready to use ConnectionAnchors, you just have to choose the one that suits your needs, instanciate it and give it an owner Figure.

#### ConnectionRouter interface

A ConnectionRouter is an object able to route ( = call setPoints(PointList) on ) a Connection based on some associated constraint object, and on the locations provided by the source and target ConnectionAnchors of the Connection. The method ConnectionRouter.route(Connection) and the method ConnectionRouter.setConstraint(Connection, Object) serve that purpose.

There is not a one - one relation between ConnectionRouters and Connections : a ConnectionRouter is shared between multiple Connections and keeps track of their associated constraint objects. Thanks to this, when a ConnectionRouter routes a particular Connection, it can possibly take into account the positions of the other Connections, for example to avoid overlapping between them. So there is also a remove(Connection) method to remove a Connection and its associated constraint from a ConnectionRouter, and an invalidate(Connection) method to tell the ConnectionRouter to discard all caught information about a particular Connection.

I don't think you will ever have to call the methods of the ConnectionRouter interface by yourself, the Draw2d Connection implementation (PolylineConnection) should do all this for you so don't worry about it. ConnectionRouters are like LayoutManagers, you set them once, and you let them do the hard work for you.

As for ConnectionAnchors, Draw2d provides some ready to use ConnectionRouters.

#### ConnectionLayer class

A ConnectionLayer is a Layer (a FreeformLayer) with an associated ConnectionRouter automatically shared by all the children Figures implementing the Connection interface. You can set the ConnectionRouter by calling ConnectionLayer.setConnectionRouter(ConnectionRouter). Just as you can add a LayoutManager to a parent Figure, you can add a ConnectionRouter to a ConnectionLayer, the idea is the same. Such a ConnectionLayer is used by the RootEditParts of GEF as the layer containing all the Connections.

#### PolylineConnection class

PolylineConnection is a Polyline subclass that implements the Connection interface. PolylineConnections being Connections, you can set a ConnectionRouter, a source and a target ConnectionAnchor and a constraint object which will be used by the ConnectionRouter to route the Connection automatically.

However, PolylineConnection provides some additional features to the ones defined by Polyline and Connection :

*   there is a default LayoutManager installed on a PolylineConnection : DelegatingLayout (and I think you can't use another one because it is used to place the "decorations"). Such a LayoutManager takes constraint objects of type Locator which are able to relocate their associated child (Locator.relocate(IFigure)). Draw2d provides ready to use Locators for various puposes. For example, this can be used to place Labels at the end, at the middle or at the beginning (it depends on the type of Locator you choose) of the PolylineConnection. To do this, you add the Labels as children of the PolylineConnection with their associated Locator by calling IFigure.add(IFigure child, Object constraint (= the locator, in this case) ).
*   you can set a source and a target decoration (RotatableDecoration). Draw2d provides such decoration Figures. You can use this feature to add an arrow ending or beginning to your PolylineConnection, for example. These decorations will be added as children of the PolylineConnection with a Locator of the type ArrowLocator which will take care of setting their orientation as well.
*   bounds calculation now takes into account the presence of children, which was not the case in the Polyline class. So if children are outside the bounding box of the polyline points, there will still be visible.

Routing of PolylineConnections takes place in the validation process of Draw2d, so it can be triggered by calling revalidate() on a PolylineConnection : the PolylineConnection.layout() method overrides the Figure.layout() method and it first routes the polyline with the ConnectionRouter, then lays out the children Figures of the PolylineConnection with the LayoutManager. But you will most likely never have to call revalidate() by yourself as most of the time Draw2d does this kind of stuff for you when it is required.

A PolylineConnection listens to its source and target ConnectionAnchors and is automatically revalidated when they move ( = when their owner Figures move = when the source and the target of the connection move). So once you have created a PolylineConnection, you can move the source and the target figure without worrying about the PolylineConnection(s) between them, they will update automatically and all their decorations, children, etc. as well.

#### Examples

\[TODO: provide examples of all this\]

### GEF part

In this part, the terms "connection", "source node" and "target node" refer to model objects, not to their visual representation. The source node refers to the one with a visual representation that has to be associated to the source side of the Connection Figure (Connections in draw2d can be asymetrical, they have a source side and a target side to which you can add particular decorations, labels, etc.). (something bothers me with my definitions here but by now I haven't found better ones...)

#### The view: A Connection implementation

Everything that has been said before about views of model objects still applies here, the only change being : the Draw2d Figures used to represent connections must implement the Connection interface. So you should write a PolylineConnection subclass with public methods to access the graphical properties which represent the data held by the connection, and maybe you could group these public methods in an interface to allow easy replacement of a view implementation by another. Don't worry about setting a ConnectionRouter and ConnectionAnchors : this will be done by GEF later, based on information you will have to provide elsewhere.

Here is an example of a PolylineConnection with an arrow ending and displaying a label at its source end, and the interface to access the Label text used to display a property of the connection :

Interface :

public interface IMyConnection extends Connection, IFigure{

         public void setLabelText(String text);
 }

  
Figure :

public class MyConnection extends PolylineConnection implements IMyConnection{

         private Label label;
         
         public MyConnection(){
                 label = new Label("");
                 add(label, new ConnectionEndpointLocator(this, false));
                 
                 PolygonDecoration dec = new PolygonDecoration();
                 dec.setScale(20,5);
                 fig.setTargetDecoration(dec);
                 
                 setLineWidth(2);
         }
         
         public void setLabelText(String text){
                 label.setText(text);
         }
 }

#### The controller: An AbstractConnectionEditPart implementation

Like every other model object associated to a view that the user can individually interact with, connections are associated to their views by EditParts. So for every type of connection you want to represent, you have to write an EditPart class to link it to its view. For connections, you have to write a subclass of AbstractConnectionEditPart.

AbstractConnectionEditParts are AbstractGraphicalEditParts so just as you would do to implement an AbstractGraphicalEditPart subclass, you will have to override some of the following methods :

*   IFigure createFigure() : it must return the Figure used to display the connection and this Figure must implement the Connection interface. The returned figure will be added to the ConnectionLayer of the RootEditPart.
*   void activate() : the right place to add listeners to the connection model to listen to its changes.
*   void deactivate() : the right pace to remove the listeners introduced by activate.
*   void createEditPolicies() : the right place to install the EditPolicies.
*   void refreshVisuals() : to sync the view with the current model state. This is where the constraint (which must be stored in the model) used by the ConnectionRouter must be refreshed by calling setRoutingConstraint(Object) on the Connection.
*   IFigure getContentPane() : only if it is different from getFigure().
*   List getModelChildren() : must return the model objects that should be represented in the content pane of the connection. The default return is the empty List.

And also some kind of propertyChange(PropertyChangeEvent) method to react on the events fired by the model and to call the appropriate refreshing methods.

AbstractConnectionEditPart have two methods, getSource() and getTarget(), to get the "source and target EditParts" of this AbstractConnectionEditPart. The source / target EditPart is the one associated to the source / target node of the connection, and they will be set when the AbstractConnectionEditPart will be built by GEF.

#### How to add the connections to the diagram

After you have written the views and controllers for the connections you have to implement at least three things :

*   For each AbstractGraphicalEditPart which has a node as model object, you have to implement these two methods :
    *   List getModelSourceConnections() : must return the List of connections that have the model of this EditPart as their source node ( = the List of connections that should be represented with the Figure of the current EditPart as the source side of their Connection Figure ),
    *   List getModelTargetConnections() : must return the List of connections that have the model of this EditPart as their target node ( = the List of connections that should be represented with the Figure of the current EditPart as the target side of their Connection Figure ). Each connection has to appear for one EditPart in the getModelSourceConnections() list, and for one EditPart in the getModelTargetConnections() list.
*   For each type of connection, you have to write some lines in the createEditPart(EditPart context, Object model) method of the EditPartFactory to create an appropriate AbstractConnectionEditPart for the connection (an alternative to this is overriding the AbstractGraphicalEditPart.createConnection(Object model) method).
*   For each AbstractGraphicalEditPart which has a node as model object, you have to listen to the node and to react on the events fired by the node by calling refreshSourceConnections() / refreshTargetConnections() if these events describe changes in the model that affect the List returned by getModelSourceConnections() / getModelTargetConnection().

So by defining the getModel...Connections() methods, you define the connection structure of the view given a state of the model (i.e., which node representations have to be linked together by which connection representations) and by defining the EditParts created for each connection type by the EditPartFactory, you define the look (and the editing behaviour) of the view to be associated to these connections. With these informations, GEF has (almost : see next section ) everything it has to know to build the view associated to a state of your model and it will do it automatically the first time the model has to be shown in an EditPartViewer. After that, you will have to call the appropriate refreshing methods in reaction to events fired by the model, as mentioned above.

#### What about ConnectionAnchors ? NodeEditPart interface

By default, an AbstractConnectionEditPart uses ChopBoxAnchors to attach its Connection to the Figures of its source and target EditParts (at least in v3.0 of GEF). If this doesn't suit your needs, you will have to implement the NodeEditPart interface on the EditParts associated with node model objects. This interface is used by AbstractConnectionEditPart to obtain customized ConnectionAnchors from the source and target EditParts when the AbstractConnectionEditPart is refreshed (see details section).

#### What about the ConnectionRouter ?

As Connections are added to the ConnectionLayer of the RootEditPart, you will have to set the ConnectionRouter there. One convenient place to do that is in the configureGraphicalViewer() method of the graphical editor. The following code shows how to set the Manhattan connection router on the root edit part.

protected void configureGraphicalViewer() {

         super.configureGraphicalViewer();
         GraphicalViewer viewer = this.getGraphicalViewer();
         // … do the regular tasks such as setting edit part factory
         RootEditPart root = viewer.getRootEditPart();
 
         if (root instanceof LayerManager) {
             ((ConnectionLayer) ((LayerManager) root)
                     .getLayer(LayerConstants.CONNECTION_LAYER))
                     .setConnectionRouter(new ManhattanConnectionRouter());
         }
     }

See the logic example, or the HelloGef examples for alternative solutions. There the connection router is set in the refreshVisuals() method of the top, not root edit part. (Does this lead to connection router being set multiple times?)

#### Some details...

If you want to know in a more detailed way how GEF builds the EditParts for the connections, and when the ConnectionAnchors are set, see the notes here :

Note 1 : in the process of building the view, a connection will be discovered by GEF two times : one time at the source side, in the List returned by a getModelSourceConnection() method, and another time at the target side, in the List returned by a getModelTargetConnection(). An EditPart will be built for the connection the first time it is discovered and its source or target EditPart will be set. The second time the connection is discovered, the AbstractConnectionEditPart which is already built is found by using the \[model objects => EditParts\] map of the EditPartViewer and the remaining node EditPart (source or target) is set.

Note 2 : When the source or target EditPart of an AbstractConnectionEditPart changes, if both the target EditPart and the source EditPart are non-null, the AbstractConnectionEditPart is refreshed. In addition to the classic refresh() method of AbstractGraphicalEditPart, the refresh() method of AbstractConnectionEditPart also refreshes the ConnectionAnchors of the Connection Figure. Additional things...

TODOS : maybe implementing a very very simple example with no editing capabilities (maybe Labels layed out by flow layout in a diagram) explaining how to build a simple outline view with a JFace treeviewer and then with a GEF tree viewer, explaining how to contribute to the property sheet, explaining how to contribute to retargatable actions like undo, redo, delete..., explaining how to add a context menu to the editor, explaining how to react to key events...

