package gov.nsa.dpacs.mikrokopter.app.ww;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.globes.Earth;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Cone;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.Path;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwindx.examples.ApplicationTemplate;
import java.util.ArrayList;

public class LobLayer extends RenderableLayer {

	ShapeAttributes coneAttributes;
	Cone lob;
	LineArrow arrow;

	public LobLayer() {
		this.setName("DPACS LOBs");
		this.init();
	}

	private void init() {
		this.coneAttributes = new BasicShapeAttributes();
		this.coneAttributes.setInteriorMaterial(Material.YELLOW);
		this.coneAttributes.setInteriorOpacity(0.5);
		this.coneAttributes.setEnableLighting(true);
		this.coneAttributes.setOutlineMaterial(Material.RED);
		this.coneAttributes.setOutlineWidth(2d);
		this.coneAttributes.setDrawInterior(true);
		this.coneAttributes.setDrawOutline(false);
	}

	public void createCone(double latitudeDeg, double longitudeDeg, double altitudeMeters, double headingDeg) {
		double heightAngle = .1;
		Position pos = Position.fromDegrees(latitudeDeg, longitudeDeg, altitudeMeters);
		LatLon pos1 = Position.greatCircleEndPosition(pos, Angle.fromDegrees(headingDeg),Angle.fromDegrees(heightAngle));
		LatLon posCenter = Position.greatCircleEndPosition(pos, Angle.fromDegrees(headingDeg),Angle.fromDegrees(heightAngle/2.0));
		double height = Position.ellipsoidalDistance(pos, pos1,Earth.WGS84_EQUATORIAL_RADIUS,Earth.WGS84_POLAR_RADIUS);
		Cone lob = new Cone(new Position(posCenter,altitudeMeters), height,1.0E+03);
		lob.setRoll(Angle.POS90);
            	lob.setTilt(Angle.POS90.add(Angle.POS180).add(Angle.fromDegrees(headingDeg)));
		lob.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
		lob.setAttributes(coneAttributes);
		lob.setValue(AVKey.DISPLAY_NAME, "Heading(deg): " + headingDeg + " Altitude(m): " + altitudeMeters);
		lob.setVisible(true);
		this.addRenderable(lob);
	}

	private void createLineArrow(double latitudeDeg, double longitudeDeg, double altitudeMeters, double headingDeg) {
		// Create and set an attribute bundle.
		ShapeAttributes attrs = new BasicShapeAttributes();
		attrs.setOutlineMaterial(Material.RED);
		attrs.setOutlineWidth(2d);

		// Create a path, set some of its properties and set its attributes.
		ArrayList<Position> pathPositions = new ArrayList<Position>();
		Position p0 = Position.fromDegrees(latitudeDeg, longitudeDeg, altitudeMeters);
		pathPositions.add(p0);
		LatLon ll = Position.greatCircleEndPosition(p0, Angle.fromDegrees(headingDeg), Angle.fromDegrees(.1));
		pathPositions.add(new Position(ll, altitudeMeters));

		Path path = new LineArrow(pathPositions);

		// To ensure that the arrowheads resize smoothly, refresh each time the path is drawn.
		path.setAttributes(attrs);
		path.setVisible(true);
		path.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
		path.setPathType(AVKey.GREAT_CIRCLE);
		path.setValue(AVKey.DISPLAY_NAME, "Heading(deg): " + headingDeg + " Altitude(m): " + altitudeMeters);
		this.addRenderable(path);
	}

	public void updateLob(double latitude, double longitude, double altitude, double heading) {
		if (this.lob == null) {
			this.createCone(latitude, longitude, altitude, heading);
		} else {
			this.lob.setHeading(Angle.fromDegrees(heading));
		}

	}

	public static class TestApp extends ApplicationTemplate {

		public static class AppFrame extends ApplicationTemplate.AppFrame {

			public AppFrame() {
				LobLayer lobLayer = new LobLayer();
				insertBeforePlacenames(getWwd(), lobLayer);
				this.getLayerPanel().update(this.getWwd());
				double lat = 30.432066;
				double lon = -97.834022;
				double alt = 300.0;
//				int heading = 45;
				for(int heading = 0; heading <= 360; heading=heading+45) {
					lobLayer.updateLob(lat,lon, alt,heading);
					lobLayer.createLineArrow(lat,lon, alt,heading);
				}
			}
		}
	}

	public static void main(String args[]) {
		ApplicationTemplate.start("LOB/Heading Test Application", TestApp.AppFrame.class);
	}
}