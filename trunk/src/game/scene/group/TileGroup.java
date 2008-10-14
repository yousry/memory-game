/**
 * 
 */
package game.scene.group;

import game.control.ControlService;
import game.control.group.TileBehaviorFace;
import game.control.group.TileBehaviorHight;
import game.control.group.TileBehaviorRotation;
import game.control.group.TilePickBehavior;
import game.logic.stage.Board;
import game.logic.tile.TileLogic;
import game.logic.tile.TileLogic.Value;

import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Group;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

;

/**
 * @author Yousry Abdallah
 * 
 */
public class TileGroup extends BranchGroup implements AppGroup {

	private GroupUtilities utilities = new GroupUtilities();

	Board.TokenPosition position;

	TileLogic.Value tokenValue;

	private double boardPositionRad = 0;

	private Canvas3D canvas3D;

	/**
	 * 
	 */
	public TileGroup(Board.TokenPosition position, TileLogic.Value value, Canvas3D canvas3D) {
		super();
		this.position = position;
		boardPositionRad = utilities.translateBoardPosition(position);
		this.tokenValue = value;
		this.canvas3D = canvas3D;
		initialize();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see game.scene.group.AppGroup#initialize()
	 */
	@Override
	public void initialize() {

		Group actualGroup = this;

		// test global Rotation
		// TransformGroup objTransR = new TransformGroup();
		// objTransR.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		// objTransR.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		// BoundingSphere boundR = new BoundingSphere(new Point3d(0.0, 0.0,
		// 0.0), 100.0);
		// Transform3D spinR = new Transform3D();
		// spinR.rotX(Math.PI * 0.4);
		// Alpha rotationAlphaR = new Alpha(-1, Alpha.INCREASING_ENABLE, 0, 0,
		// 3000, 0, 0, 0, 0, 0);
		// RotationInterpolator rotatorR = new
		// RotationInterpolator(rotationAlphaR, objTransR, spinR, 0.0f,
		// (float) Math.PI * 2.0f);
		// rotatorR.setSchedulingBounds(boundR);
		// objTransR.addChild(rotatorR);
		// actualGroup.addChild(objTransR);
		// actualGroup = objTransR;

		// A: Position
		actualGroup = utilities.initPosition(actualGroup, new Vector3f(0f, 0f, 0.2f));

		// B: Rotation
		actualGroup = utilities.initRotation(actualGroup, new Vector3d(Math.PI / 2d, boardPositionRad, 0d));

		// Behavior Rotation
		TransformGroup transformGroup = new TransformGroup();
		transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

		TileBehaviorRotation behaviorRotation = new TileBehaviorRotation(transformGroup, boardPositionRad);
		behaviorRotation.setSchedulingBounds(new BoundingSphere());
		actualGroup.addChild(behaviorRotation);
		actualGroup.addChild(transformGroup);
		actualGroup = transformGroup;

		TileLogic logic = ControlService.getService().getTile(position);
		logic.getAction().setBehaviorRotation(behaviorRotation);

		// A*: Position correction
		actualGroup = utilities.initPosition(actualGroup, new Vector3f(0f, 0f, -0.27f));

		// Behavior Height
		transformGroup = new TransformGroup();
		transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		TileBehaviorHight behaviorHight = new TileBehaviorHight((TransformGroup) transformGroup);
		behaviorHight.setSchedulingBounds(new BoundingSphere());
		actualGroup.addChild(behaviorHight);
		actualGroup.addChild(transformGroup);
		actualGroup = transformGroup;

		logic = ControlService.getService().getTile(position);
		logic.getAction().setBehaviorHight(behaviorHight);

		// C: Scale
		actualGroup = utilities.initScale(actualGroup, 0.28);

		// Behavior Face
		transformGroup = new TransformGroup();
		transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		TileBehaviorFace behaviorFace = new TileBehaviorFace(transformGroup);
		behaviorFace.setSchedulingBounds(new BoundingSphere());
		actualGroup.addChild(behaviorFace);
		actualGroup.addChild(transformGroup);
		actualGroup = transformGroup;

		logic = ControlService.getService().getTile(position);
		
		logic.setPosition(position);
		logic.setValue(tokenValue);
		
		logic.getAction().setBehaviorFace(behaviorFace);

		// Behavior Pick
		transformGroup = new TransformGroup();
		transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		transformGroup.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
		
		TilePickBehavior pickBehavior = new TilePickBehavior(canvas3D,this,new BoundingSphere(), logic); 
		pickBehavior.setSchedulingBounds(new BoundingSphere());
		actualGroup.addChild(pickBehavior);
		actualGroup.addChild(transformGroup);
		actualGroup = transformGroup;

		// D: Load Object
		switch (tokenValue) {
		case FIRE:
			logic.setValue(Value.FIRE);
			actualGroup = utilities.loadObject(actualGroup, "/obj/fire-tile.obj");
			break;
		case WATER:
			logic.setValue(Value.WATER);
			actualGroup = utilities.loadObject(actualGroup, "/obj/water-tile.obj");
			break;
		case EARTH:
			logic.setValue(Value.EARTH);
			actualGroup = utilities.loadObject(actualGroup, "/obj/earth-tile.obj");
			break;
		case AIR:
			logic.setValue(Value.AIR);
			actualGroup = utilities.loadObject(actualGroup, "/obj/air-tile.obj");
			break;
		}

		compile();

	}
}
