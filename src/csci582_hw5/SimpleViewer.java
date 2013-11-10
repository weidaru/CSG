package csci582_hw5;
// SimpleViewer.java
// Ari Requicha
// December 28, 2000

import java.awt.GraphicsConfiguration;

import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Group;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JFrame;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseZoom;
import com.sun.j3d.utils.universe.SimpleUniverse;


public class SimpleViewer {

 // Instantiating an object of this class makes a JFrame in which to 
 // display graphic objects defined in Java3D. SimpleViewer uses SimpleUniverse
 // and other utilities to create a scene graph containing all the necessary
 // view nodes plus the beginnings of a scene branch. It creates
 // a BranchGroup, a TransformGroup, and an extendable Group under it.
 // It also attaches 3 mouse behaviors to the BranchGroup; these operate on the
 // TransformGroup. Pressing the left button and dragging generates a rotation
 // of the scene; dragging in the x direction creates a rotation around the y axis,
 // and dragging in an in-between direction creates a composite rotation. 
 // Dragging with the right button pressed translates along the drag
 // direction. Dragging with the middle button pressed (or with the left + Alt
 // for PCs) zooms, by translating along the z axis.

 // The actual scene is to be created elsewhere as a Branchgroup and attached 
 // to the SimpleViewer. This is done by first calling the method getSVGroup()
 // and adding BranchGroup children to the returned value.

 // SimpleViewer is not a fancy viewer! It works reasonably well for objects located
 // within a sphere of radius <1 centered at the origin. 

 // Group variable declaration. The actual Group is created by the
 // SimpleViewer constructor

	 private Group svGroup;
	 private TransformGroup transGroup;
	 private Sphere viewSphere;

 // Constructor

	 public SimpleViewer () {
		 JFrame displayFrame = new JFrame( "Simple Viewer" );
		 displayFrame.setSize( 500, 500 );
 
		  // To display we need a java3d Canvas3D. Note capital D
		  // A null argument in the constructor gives the default values
		  // We need to use a "heavy" (AWT) component (Canvas3D) to get 
		  // graphics acceleration. However, this implies that the canvas
		  // always gets written on top of any Swing lightweight widgets.
		  // This is not a problem if we keep the 3D display as a window
		  // used exclusively for display. All the GUI stuff built with
		  // Swing has to go in separate windows. 
		
		  // We need a graphics configuration for the canvas constructor
		  // and can get a default configuration by calling the method below 
		
		  GraphicsConfiguration config =
		   SimpleUniverse.getPreferredConfiguration();
		
		  Canvas3D canvas = new Canvas3D( config ); 
		  displayFrame.add( canvas );
		
		
		  // Use utilities to build all the superstructures and viewing
		  // info with reasonable defaults.
		  // This puts the viewpoint on the z axis and can display
		  // objects that fit in a sphere of radius ~ 0.8 centered at the 
		  // origin. Anything beyond that gets clipped.
		
		  SimpleUniverse univ = new SimpleUniverse( canvas );
		  univ.getViewingPlatform( ).setNominalViewingTransform( );
		
		  // Make a scene graph branch
		
		  BranchGroup branch = new BranchGroup( ); // The root
		  
		  // Put a TransformGroup below the root and make it readable 
		  // and writable. This is needed for the mouse behaviors to be able
		  // to modify it.
		
		  transGroup = new TransformGroup( ); // null transform
		  transGroup.setCapability( TransformGroup.ALLOW_TRANSFORM_READ );
		  transGroup.setCapability( TransformGroup.ALLOW_TRANSFORM_WRITE );
		  branch.addChild( transGroup );
		
		  // Put the SimpleViewer Group below the transform and make it extendable.
		  // This is the attachment point, where the user will link his/her own scene.
		  // Needs capabilities to be able to add the user scene after being live.
		  // Group is used here because it seems to be the only class that can be
		  // extended in this manner. The user scene must be defined as a BranchGroup.
		
		  svGroup = new Group( );
		  svGroup.setCapability( Group.ALLOW_CHILDREN_READ );
		  svGroup.setCapability( Group.ALLOW_CHILDREN_WRITE );
		  svGroup.setCapability( Group.ALLOW_CHILDREN_EXTEND );
		  transGroup.addChild( svGroup );
		  
		  // Add mouse behaviors for rotating, translating and zooming.
		  // This uses utility classes that do all the work.
		
		  MouseRotate mouseRot = new MouseRotate();
		  mouseRot.setTransformGroup( transGroup );
		  mouseRot.setSchedulingBounds( new BoundingSphere() );
		  branch.addChild( mouseRot );
		
		  MouseTranslate mouseTrans = new MouseTranslate();
		  mouseTrans.setTransformGroup( transGroup );
		  mouseTrans.setSchedulingBounds( new BoundingSphere() );
		  branch.addChild( mouseTrans );
		
		  MouseZoom mouseZ = new MouseZoom();
		  mouseZ.setTransformGroup( transGroup );
		  mouseZ.setSchedulingBounds( new BoundingSphere() );
		  branch.addChild( mouseZ );
		
		  // Now hang the BranchGraph from the universe, which makes it live,
		  // and make all visible. Therefore, when we return from the constructor
		  // the SimpleViewer frame is on the screen and the associated scene
		  // graph is live.
		
		  univ.addBranchGraph( branch );
		  displayFrame.setVisible( true );
		  
		  viewSphere = new Sphere();
	
	 } // End constructor
	
	 // Method to return the point of attachment, i.e., the SimpleViewer Group
	
	 public Group getSVGroup() { return svGroup; }
	 
	 public void setViewSphere(Sphere sphere) {
		 if(sphere == null || sphere.radius==0.0f)
			 return;
		 sphere.copyTo(viewSphere);
		 Point3f center = viewSphere.center;
		 float radius = viewSphere.radius;
		 
		 Transform3D xfm = new Transform3D();
		 Matrix4f translateMatrix = new Matrix4f();
		 translateMatrix.setIdentity();
		 float vec[] = {-center.x, -center.y, -center.z, 1.0f};
		 translateMatrix.setColumn(3, vec);
		 Matrix4f scaleMatrix = new Matrix4f();
		 scaleMatrix.setIdentity();
		 float scale  = 0.8f/radius;
		 scaleMatrix.m00 = scale;
		 scaleMatrix.m11 = scale;
		 scaleMatrix.m22 = scale;
		 scaleMatrix.mul(translateMatrix);
		 xfm.set(scaleMatrix);
		 
		 transGroup.setTransform(xfm);
	 }
	 
	 public Sphere getViewSphere() {
		 return viewSphere;
	 }

} // End SimpleViewer class
 


