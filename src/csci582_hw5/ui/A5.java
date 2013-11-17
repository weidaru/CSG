package csci582_hw5.ui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.LineArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;

import csci582_hw5.Line;
import csci582_hw5.Pair;
import csci582_hw5.SimpleViewer;
import csci582_hw5.Sphere;
import csci582_hw5.csg.CSGBuilder;
import csci582_hw5.csg.CSGCache;
import csci582_hw5.csg.CSGNode;
import csci582_hw5.csg.CSGOperation;
import csci582_hw5.pathplan.Node;
import csci582_hw5.pathplan.RoadMap;

public class A5 extends JFrame {
	//MenuBar	 		menu level 1
	private JMenuBar menuBar;
	//Menu 				menu level 2
	private JMenu fileMenu;
	private JMenu viewMenu;
	private JMenu transformMenu;
	private JMenu objectMenu;
	private JMenu planMenu;
	
	//MenuItem			menu level 3
	private JMenuItem exitMenuItem;
	private JMenuItem rotateXMenuItem;
	private JMenuItem rotateYMenuItem;
	private JMenuItem rotateZMenuItem;
	private JMenuItem translateMenuItem;
	private JMenuItem composeMenuItem;
	private JMenuItem doneMenuItem;
	private JMenuItem blockMenuItem;
	private JMenuItem moveMenuItem;
	private JMenuItem unionMenuItem;
	private JMenuItem differenceMenuItem;
	private JMenuItem intersectionMenuItem;
	private JMenuItem displayMenuItem;
	private JCheckBoxMenuItem roadmapMenuItem;
	private JMenuItem eraseMenuItem;
	private JMenuItem sceneMenuItem;
	private JMenuItem endpointsMenuItem;
	private JMenuItem numPointsMenuItem;
	
	private SimpleViewer display;
	private BranchGroup coordinateLabGroup;
	
	private Matrix4f matrix;
	private Map<String ,Matrix4f> matrixMap;
	
	private CSGCache csgCache;
	private RoadMap roadMap;
	private BranchGroup roadMapGroup;
	private BranchGroup endpointsGroup;
	
	private boolean setMatrix(String newName, Matrix4f m) {
		if(newName.length() > 16)
			return false;
		else {
			matrixMap.put(newName, m);
			return true;
		}
	}
	
	private Matrix4f getMatrix(String name) {
		if(matrixMap.containsKey(name))
			return matrixMap.get(name);
		else
			return null;
	}
	
	private void display(String name) {
		if(csgCache.contains(name)) {
			BranchGroup group = csgCache.getCachedGroup(name);
			Sphere sphere = CSGOperation.calculateBoundingSphere(csgCache.get(name));
			display(group, sphere);
		}
		else {
			System.out.println("Node " + name + " cannot be found.");
		}
	}
	
	private void display(BranchGroup scene, Sphere boundSphere) {
		Sphere sphere = null;
		if(boundSphere == null) {
			sphere = display.getViewSphere();
		}
		else {
			sphere = boundSphere.union(display.getViewSphere());
		}
		// Log info, comment out for now.
		//System.out.printf("Set new view Sphere (%.3f, %.3f, %.3f) %.3f\n",
		//				  sphere.center.x, sphere.center.y, sphere.center.z, sphere.radius);
		display.setViewSphere(sphere);
		display.getSVGroup().addChild(scene);
	}
	
	private void clear() {
		display.getSVGroup().removeAllChildren();
		display.getSVGroup().addChild(getCoordinateLab());
	}
	
	private void hide(javax.media.j3d.Node node) {
		display.getSVGroup().removeChild(node);
	}
	
	private void addCube(String name, float x, float y, float z) {
		if(name.length() > 16) {
			System.out.println("Name size exceeds 16.");
			return;
		}
		if(csgCache.contains(name)) {
			System.out.println("Cube " + name + " already exists.");
			return;
		}

		CSGNode node = CSGBuilder.buildCube(x, y, z);
		csgCache.insert(name, node);
		System.out.println("New cube " + name);
	}
	
	private boolean csgOpCheck(String n1, String n2, String newName) {
		if(csgCache.contains(newName)) {
			System.out.println("Node " + newName +" already exists.");
			return false;
		}
		if(!csgCache.contains(n1)) {
			System.out.println("Node " + n1 + " cannot be found." );
			return false;
		}
		if(!csgCache.contains(n2)) {
			System.out.println("Node " + n2 +" cannot be found.");
			return false;
		}
		return true;
	}
	
	private void union(String n1, String n2, String newName) {
		if(!csgOpCheck(n1, n2, newName))
			return;
		CSGNode node = CSGBuilder.union(csgCache.get(n1), csgCache.get(n2));
		csgCache.insert(newName, node);
		System.out.println("New object "  + newName + " after union ");
	}
	
	private void difference(String n1, String n2, String newName) {
		if(!csgOpCheck(n1, n2, newName))
			return;
		CSGNode node = CSGBuilder.difference(csgCache.get(n1), csgCache.get(n2));
		csgCache.insert(newName, node);
		System.out.println("New object "  + newName + " after difference ");
	}
	
	private void intersection(String n1, String n2, String newName) {
		if(!csgOpCheck(n1, n2, newName))
			return;
		CSGNode node = CSGBuilder.intersection(csgCache.get(n1), csgCache.get(n2));
		csgCache.insert(newName, node);
		System.out.println("New object "  + newName + " after intersection ");
	}
	
	private void move(String name, String matrixName, String newName) {
		if(csgCache.contains(newName)) {
			System.out.println("Node " + newName +" already exists.");
			return;
		}
		if(!csgCache.contains(name)) {
			System.out.println("Node " + name + " cannot be found." );
			return;
		}
		if(!csgCache.contains(matrixName)) {
			System.out.println("Matrix " + matrixName +" cannot be found.");
			return;
		}
		CSGNode node = CSGBuilder.transform(csgCache.get(name), matrixMap.get(matrixName));
		csgCache.insert(newName, node);
		System.out.println("New object " + newName + " after move " + name + " with matrix " + matrixName);
	}
	
	private Pair<BranchGroup, Sphere> query(Point3f start, Point3f end) {
		Node n1 = new Node(start.x, start.y, start.z);
		Node n2 = new Node(end.x, end.y, end.z);
		
		ArrayList<Line> lineList =  roadMap.query(n1, n2);
		if(lineList.size() == 0)
			System.out.println("No path between " + n1.getPosition().toString() + " and " + n2.getPosition().toString());
		
		BranchGroup group = new BranchGroup();
		group.setCapability(BranchGroup.ALLOW_DETACH);
		Appearance a = null; {
			a= new Appearance();
			ColoringAttributes ca = new ColoringAttributes(new Color3f(0.0f, 1.0f, 0.0f),ColoringAttributes.SHADE_FLAT);
			a.setColoringAttributes(ca);
		}

		for(int i=0; i<lineList.size(); i++) {
			Shape3D lineShape = lineList.get(i).toShape3D();
			lineShape.setAppearance(a);
			group.addChild(lineShape);
		}
		
		a = null; {
			a = new Appearance();
			ColoringAttributes ca = new ColoringAttributes(new Color3f(1.0f, 0.0f, 0.0f),ColoringAttributes.SHADE_FLAT);
			a.setColoringAttributes(ca);
			float radius = Math.min(0.05f, display.getViewSphere().radius/50.0f);
			if(radius == 0.0f)
				radius = 0.005f;
			com.sun.j3d.utils.geometry.Sphere s = 
					new com.sun.j3d.utils.geometry.Sphere(radius);
			
			
			s.setAppearance(a);
			
			TransformGroup tg = new TransformGroup();
			Transform3D transform = new Transform3D();
			Matrix4f matrix = new Matrix4f();
			matrix.setIdentity();
			matrix.m03 = start.x;
			matrix.m13 = start.y;
			matrix.m23 = start.z;
			transform.set(matrix);
			tg.setTransform(transform);
			tg.addChild(s);
			
			group.addChild(tg);
		}
		
		a = null; {
			a = new Appearance();
			ColoringAttributes ca = new ColoringAttributes(new Color3f(0.0f, 1.0f, 0.0f),ColoringAttributes.SHADE_FLAT);
			a.setColoringAttributes(ca);
			float radius = Math.min(0.05f, display.getViewSphere().radius/50.0f);
			if(radius == 0.0f)
				radius = 0.005f;
			com.sun.j3d.utils.geometry.Sphere s = 
					new com.sun.j3d.utils.geometry.Sphere(radius);
			s.setAppearance(a);
			
			TransformGroup tg = new TransformGroup();
			Transform3D transform = new Transform3D();
			Matrix4f matrix = new Matrix4f();
			matrix.setIdentity();
			matrix.m03 = end.x;
			matrix.m13 = end.y;
			matrix.m23 = end.z;
			transform.set(matrix);
			tg.setTransform(transform);
			tg.addChild(s);
			
			group.addChild(tg);
		}
		
		Sphere sphere = new Sphere();
		sphere.radius = start.distance(end)/2.0f;
		sphere.center.x = (start.x + end.x)/2.0f;
		sphere.center.y = (start.y + end.y)/2.0f;
		sphere.center.z = (start.z + end.z)/2.0f;
		
		return new Pair<BranchGroup, Sphere>(group, sphere);
	}
	
	private void setRoadMapVisibility(boolean visible) {
		if(visible) {
			if(roadMapGroup == null) {
				roadMapGroup = roadMap.getNodeGroup();
			}
			
			assert(roadMapGroup != null);
			display(roadMapGroup, roadMap.getBoundSphere());
		}
		else {
			hide(roadMapGroup);
		}
	}
	
	private void test() {
		/*
		addCube("a", 0.2f, 0.2f, 0.3f);
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		matrix.m03 = 0.05f;
		matrix.m13 = 0.05f;
		matrix.m23 = 0.05f;
		setMatrix("a", matrix);
		move("a", "a", "b");
		move("b", "a", "c");
		
		difference("a","b","d");
		union("d", "c", "e");
		

		display("d");

		roadMap.load(csgCache.get("d"));
		setRoadMapVisibility(true);
		
		Point3f start = new Point3f(0.1f, 0.1f, 0.1f);
		Point3f end = new Point3f(0.3f, 0.3f, 0.3f);
		Pair<BranchGroup, Sphere> p =  query(start, end);
		display(p.first(), p.second());
		*/
	}
	
	public A5() {
		csgCache = new CSGCache();
		
		matrix = new Matrix4f();
		matrix.setIdentity();
		
		matrixMap = new TreeMap<String ,Matrix4f>();
		
		roadMap = new RoadMap();
		roadMapGroup = null;
		endpointsGroup = null;
		
		
		/***************UI initialization.***************/
		//Menu level 1
		menuBar = new JMenuBar();

		//Menu level 2
		initMenuLevel2();
		
		//Menu level 3
		initMenuLevel3();

		//Setup the menu hierarchy.
		fileMenu.add(exitMenuItem);
		
		transformMenu.add(rotateXMenuItem);
		transformMenu.add(rotateYMenuItem);
		transformMenu.add(rotateZMenuItem);
		transformMenu.add(translateMenuItem);
		transformMenu.add(composeMenuItem);
		transformMenu.add(doneMenuItem);
		
		viewMenu.add(displayMenuItem);
		viewMenu.add(eraseMenuItem);
		viewMenu.add(roadmapMenuItem);
		
		objectMenu.add(blockMenuItem);	
		objectMenu.add(moveMenuItem);
		objectMenu.add(unionMenuItem);
		objectMenu.add(differenceMenuItem);
		objectMenu.add(intersectionMenuItem);
		
		menuBar.add(fileMenu);
		menuBar.add(viewMenu);
		menuBar.add(transformMenu);
		menuBar.add(objectMenu);
		menuBar.add(planMenu);
		
		planMenu.add(sceneMenuItem);
		planMenu.add(endpointsMenuItem);
		
		setJMenuBar(menuBar);

		//Other stuffs
		pack();
		//Set it in the mid of screen.
		setLocation(getCenterLocation(getWidth(), getHeight()));
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		display = new SimpleViewer();
		coordinateLabGroup = null;
		display.getSVGroup().addChild(getCoordinateLab());
		
		test();
	}
	
	private BranchGroup getCoordinateLab() {
		if(coordinateLabGroup != null)
			return coordinateLabGroup;
		coordinateLabGroup = new BranchGroup();
		coordinateLabGroup.setCapability(BranchGroup.ALLOW_DETACH);
		
		{
			Appearance appearance = new Appearance();
			ColoringAttributes ca = new ColoringAttributes(new Color3f(1.0f, 0.0f, 0.0f),ColoringAttributes.SHADE_FLAT);
			appearance.setColoringAttributes(ca);
			
		    Point3f[] plaPts = new Point3f[2];
		    plaPts[0] = new Point3f(0.0f, 0.0f, 0.0f);
		    plaPts[1] = new Point3f(1.0f, 0.0f, 0.0f);
		    LineArray pla = new LineArray(2, LineArray.COORDINATES);
		    pla.setCoordinates(0, plaPts);
		    Shape3D plShape = new Shape3D(pla, appearance);
		    coordinateLabGroup.addChild(plShape);
		}
		
		{
			Appearance appearance = new Appearance();
			ColoringAttributes ca = new ColoringAttributes(new Color3f(0.0f, 1.0f, 0.0f),ColoringAttributes.SHADE_FLAT);
			appearance.setColoringAttributes(ca);
			
		    Point3f[] plaPts = new Point3f[2];
		    plaPts[0] = new Point3f(0.0f, 0.0f, 0.0f);
		    plaPts[1] = new Point3f(0.0f, 1.0f, 0.0f);
		    LineArray pla = new LineArray(2, LineArray.COORDINATES);
		    pla.setCoordinates(0, plaPts);
		    Shape3D plShape = new Shape3D(pla, appearance);
		    coordinateLabGroup.addChild(plShape);
		}
		
		{
			Appearance appearance = new Appearance();
			ColoringAttributes ca = new ColoringAttributes(new Color3f(0.0f, 0.0f, 1.0f),ColoringAttributes.SHADE_FLAT);
			appearance.setColoringAttributes(ca);
			
		    Point3f[] plaPts = new Point3f[2];
		    plaPts[0] = new Point3f(0.0f, 0.0f, 0.0f);
		    plaPts[1] = new Point3f(0.0f, 0.0f, 1.0f);
		    LineArray pla = new LineArray(2, LineArray.COORDINATES);
		    pla.setCoordinates(0, plaPts);
		    Shape3D plShape = new Shape3D(pla, appearance);
		    coordinateLabGroup.addChild(plShape);
		}

	    
	    return coordinateLabGroup;
	}
	
	private Point getCenterLocation(int w, int h) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		return new Point((screenSize.width - w)/2, (screenSize.height - h)/2);
	}
	
	private void initMenuLevel2() {
		fileMenu = new JMenu("File");
		viewMenu = new JMenu("View");
		transformMenu = new JMenu("Transform");
		objectMenu = new JMenu("Object");
		planMenu = new JMenu("Plan");
	}
	
	private void initMenuLevel3() {
		exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});

		
		rotateXMenuItem = new JMenuItem("RotateX");
		rotateXMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				TextDialog dialog = new TextDialog(A5.this, "Angle in Degrees");
				try {
					int angle_int= Integer.parseInt(dialog.getText());
					if(angle_int/90*90 != angle_int) {
						System.out.println("Angele must be multiple of 90.");
						return;
					}
					
					float angle = angle_int / 180.0f * (float)Math.PI;
					
					Matrix4f rot = new Matrix4f();
					rot.setIdentity();
					rot.rotX(angle);
					rot.mul(matrix);
					matrix.set(rot);
				}
				catch(IllegalArgumentException e) {
					System.out.println("Bad input for a angle. " + dialog.getText());
				}
			}
		});
		
		
		rotateYMenuItem = new JMenuItem("RotateY");
		rotateYMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				TextDialog dialog = new TextDialog(A5.this, "Angle in Degrees");
				try {
					int angle_int= Integer.parseInt(dialog.getText());
					if(angle_int/90*90 != angle_int) {
						System.out.println("Angele must be multiple of 90.");
						return;
					}
					
					float angle = angle_int / 180.0f * (float)Math.PI;
					Matrix4f rot = new Matrix4f();
					rot.setIdentity();
					rot.rotY(angle);
					rot.mul(matrix);
					matrix.set(rot);
				}
				catch(IllegalArgumentException e) {
					System.out.println("Bad input for a angle. " + dialog.getText());
				}
			}
		});
		
		rotateZMenuItem = new JMenuItem("RotateZ");
		rotateZMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				TextDialog dialog = new TextDialog(A5.this, "Angle in Degrees");
				try {
					int angle_int= Integer.parseInt(dialog.getText());
					if(angle_int/90*90 != angle_int) {
						System.out.println("Angele must be multiple of 90.");
						return;
					}
					
					float angle = angle_int / 180.0f * (float)Math.PI;
					Matrix4f rot = new Matrix4f();
					rot.setIdentity();
					rot.rotZ(angle);
					rot.mul(matrix);
					matrix.set(rot);
				}
				catch(IllegalArgumentException e) {
					System.out.println("Bad input for a angle. " + dialog.getText());
				}
				
			}
		});
		
		translateMenuItem = new JMenuItem("Translate");
		translateMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					TranslateDialog dialog = new TranslateDialog(A5.this);
					float[] vec = {dialog.getDeltaX(),  dialog.getDeltaY(), dialog.getDeltaZ(), 1.0f};
					
					Matrix4f translate = new Matrix4f();
					translate.setIdentity();
					translate.setColumn(3, vec);
					translate.mul(matrix);
					matrix.set(translate);
				}
				catch(IllegalArgumentException e){
					System.out.println(e.getMessage());
				}
			}
		});
		
		composeMenuItem = new JMenuItem("Compose");
		composeMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TextDialog dialog = new TextDialog(A5.this, "Name");
				String name = dialog.getText();
				Matrix4f n = getMatrix(name);
				if(n != null) {
					Matrix4f temp = new Matrix4f();
					temp.set(n);
					temp.mul(matrix);
					matrix.set(temp);
				}
				else 
					System.out.println("Invalid matrix name " + name);
			}
		});
		
		doneMenuItem = new JMenuItem("Done");
		doneMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TextDialog dialog = new TextDialog(A5.this, "Name");
				String name = dialog.getText();
				
				if(name == "") {
					System.out.println("String length must be greater than 0.");
					return;
				}
				
				if(matrixMap.containsKey(name)) {
					System.out.println("Reassignment of matrix is not allowed. " + name);
					return;
				}
				if(!setMatrix(name, matrix)) {
					System.out.println("Invalid matrix name, it must be shorter than 16 characters. " + name);
					return;
				}
				else {
					System.out.println("New matrix " + name);
				}
				matrix = new Matrix4f();
				matrix.setIdentity();
			}
		});
		
		displayMenuItem = new JMenuItem("Display");
		displayMenuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				TextDialog dialog = new TextDialog(A5.this, "Name");
				String name = dialog.getText();
				display(name);
			}
		});
		
		roadmapMenuItem = new JCheckBoxMenuItem("RoadMap");
		roadmapMenuItem.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if(roadmapMenuItem.isSelected()) {
					setRoadMapVisibility(true);
				}
				else {
					setRoadMapVisibility(false);
				}
			}
		});
		
		
		eraseMenuItem = new JMenuItem("Erase");
		eraseMenuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				clear();
			}
		});
		
		blockMenuItem = new JMenuItem("Block");
		blockMenuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				BlockDialog dialog = new BlockDialog(A5.this);
				
				String name = dialog.getBlockName();

				
				try {
					float x = dialog.getXSize();
					float y = dialog.getYSize();
					float z = dialog.getZSize();
					//generate block
					addCube(name, x, y, z);
				}catch(IllegalArgumentException exp) {
					System.out.println(exp.getMessage());
				}
			}
		});
		
		moveMenuItem = new JMenuItem("Move");
		moveMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MoveDialog dialog = new MoveDialog(A5.this);
				
				String name = dialog.getObjName();
				String transformName = dialog.getTransformName();
				String newName = dialog.getNewObjectName();

				move(name, transformName, newName);
			}
		});
		
		unionMenuItem = new JMenuItem("Union");
		unionMenuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				CSGOpDialog dialog = new CSGOpDialog(A5.this);
				String n1 = dialog.getFirstObjectName();
				String n2 = dialog.getSecondObjectName();
				String newName = dialog.getNewObjectName();
				
				union(n1, n2, newName);
			}
		});
		
		differenceMenuItem = new JMenuItem("Difference");
		differenceMenuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				CSGOpDialog dialog = new CSGOpDialog(A5.this);
				String n1 = dialog.getFirstObjectName();
				String n2 = dialog.getSecondObjectName();
				String newName = dialog.getNewObjectName();
				
				difference(n1, n2, newName);
			}
		});
		
		intersectionMenuItem = new JMenuItem("Intersection");
		intersectionMenuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				CSGOpDialog dialog = new CSGOpDialog(A5.this);
				String n1 = dialog.getFirstObjectName();
				String n2 = dialog.getSecondObjectName();
				String newName = dialog.getNewObjectName();
				
				intersection(n1, n2, newName);
			}
		});
		
		sceneMenuItem = new JMenuItem("Scene");
		sceneMenuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				TextDialog dialog = new TextDialog(A5.this, "ObjectName");
				String name = dialog.getText();
				CSGNode scene = csgCache.get(name);
				if(csgCache.get(name) == null) {
					System.out.println("Cannot find object " + name);
					return;
				}
				roadMap.load(scene);
				roadMapGroup = null;
				roadmapMenuItem.setSelected(true);
			}
		});
		
		endpointsMenuItem = new JMenuItem("EndPoints");
		endpointsMenuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				EndpointsDialog dialog = new EndpointsDialog(A5.this);
				Point3f start = dialog.getStartPoint();
				Point3f end = dialog.getEndPoint();
				if(start == null || end == null) {
					System.out.println("Invalid coordinates.");
					return;
				}
				Pair<BranchGroup, Sphere> p = query(start, end);
				if(endpointsGroup != null)
					hide(endpointsGroup);
				endpointsGroup = p.first();
				display(p.first(), p.second());
			}
		});
		
		numPointsMenuItem = new JMenuItem("NumPts");
		numPointsMenuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				TextDialog dialog = new TextDialog(A5.this, "Number of Points");
				String text = dialog.getText();
				try {
					int num = Integer.parseInt(text);
					roadMap.maxNode = num;
				}
				catch(IllegalArgumentException ex) {
					System.out.println("Bad input + " + text);
				}
			}
		});
	}
	
	public static void main(String[] argv) {
		new A5();
	}
}
