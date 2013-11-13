package csci582_hw5.ui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.TreeMap;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Group;
import javax.media.j3d.LineArray;
import javax.media.j3d.Shape3D;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;

import csci582_hw5.SimpleViewer;
import csci582_hw5.Sphere;
import csci582_hw5.csg.CSGBuilder;
import csci582_hw5.csg.CSGCache;
import csci582_hw5.csg.CSGNode;
import csci582_hw5.csg.CSGOperation;

public class A5 extends JFrame {
	//MenuBar	 		menu level 1
	private JMenuBar menuBar;
	//Menu 				menu level 2
	private JMenu fileMenu;
	private JMenu viewMenu;
	private JMenu transformMenu;
	private JMenu objectMenu;
	
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
	private JMenuItem eraseMenuItem;
	
	private SimpleViewer display;
	
	private Matrix4f matrix;
	private Map<String ,Matrix4f> matrixMap;
	
	private CSGCache csgCache;
	
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
			BranchGroup scene = new BranchGroup();
			scene.setCapability(BranchGroup.ALLOW_DETACH);
			Group tg = (Group) csgCache.getCachedGroup(name).cloneTree(true);
			scene.addChild(tg);
			
			Sphere sphere = CSGOperation.calculateBoundingSphere(csgCache.get(name));
			sphere = sphere.union(display.getViewSphere());
			System.out.printf("Set new view Sphere (%.3f, %.3f, %.3f) %.3f\n",
							  sphere.center.x, sphere.center.y, sphere.center.z, sphere.radius);
			display.setViewSphere(sphere);
			display.getSVGroup().addChild(scene);
		}
		else {
			System.out.println("Node " + name + " cannot be found.");
		}
	}
	
	private void clear() {
		display.getSVGroup().removeAllChildren();
		display.getSVGroup().addChild(getCoordinateLab());
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
	}
	
	private void difference(String n1, String n2, String newName) {
		if(!csgOpCheck(n1, n2, newName))
			return;
		CSGNode node = CSGBuilder.difference(csgCache.get(n1), csgCache.get(n2));
		csgCache.insert(newName, node);
	}
	
	private void intersection(String n1, String n2, String newName) {
		if(!csgOpCheck(n1, n2, newName))
			return;
		CSGNode node = CSGBuilder.intersection(csgCache.get(n1), csgCache.get(n2));
		csgCache.insert(newName, node);
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
	}
	
	private void test() {
		addCube("a", 0.1f, 0.1f, 0.1f);
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		matrix.m03 = 0.05f;
		matrix.m13 = 0.05f;
		matrix.m23 = 0.05f;
		setMatrix("a", matrix);
		move("a", "a", "b");
		move("b", "a", "c");
		
		union("a","b","d");
		union("d", "c", "e");
		
		//display("b");
		//display("c");

		display("e");

	}
	
	public A5() {
		csgCache = new CSGCache();
		
		matrix = new Matrix4f();
		matrix.setIdentity();
		
		matrixMap = new TreeMap<String ,Matrix4f>();
		
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
		objectMenu.add(blockMenuItem);	
		objectMenu.add(moveMenuItem);
		objectMenu.add(unionMenuItem);
		objectMenu.add(differenceMenuItem);
		objectMenu.add(intersectionMenuItem);
		
		menuBar.add(fileMenu);
		menuBar.add(viewMenu);
		menuBar.add(transformMenu);
		menuBar.add(objectMenu);
		
		setJMenuBar(menuBar);

		//Other stuffs
		pack();
		//Set it in the mid of screen.
		setLocation(getCenterLocation(getWidth(), getHeight()));
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		display = new SimpleViewer();
		display.getSVGroup().addChild(getCoordinateLab());
		
		test();
	}
	
	private BranchGroup getCoordinateLab() {
		BranchGroup lineGroup = new BranchGroup();
		lineGroup.setCapability(BranchGroup.ALLOW_DETACH);
		
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
		    lineGroup.addChild(plShape);
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
		    lineGroup.addChild(plShape);
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
		    lineGroup.addChild(plShape);
		}

	    
	    return lineGroup;
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
	}
	
	private void initMenuLevel3() {
		exitMenuItem = new JMenuItem("Exit");

		
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
		
	}
	
	public static void main(String[] argv) {
		new A5();
	}
}
