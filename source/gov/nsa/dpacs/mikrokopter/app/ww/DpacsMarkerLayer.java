package gov.nsa.dpacs.mikrokopter.app.ww;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.MarkerLayer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.render.markers.BasicMarker;
import gov.nasa.worldwind.render.markers.BasicMarkerAttributes;
import gov.nasa.worldwind.render.markers.BasicMarkerShape;
import gov.nasa.worldwind.render.markers.Marker;
import gov.nasa.worldwindx.examples.ApplicationTemplate;
import java.util.ArrayList;
import java.util.List;

public class DpacsMarkerLayer extends MarkerLayer {
	ShapeAttributes coneAttributes;
	Marker arrow;

	public DpacsMarkerLayer() {
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

	private void createArrow(double latitudeDeg, double longitudeDeg, double altitudeMeters, double headingDeg) {
		BasicMarkerAttributes markerAttributes = new BasicMarkerAttributes(Material.RED, BasicMarkerShape.HEADING_ARROW, 1d, 10, 5);
//                markerAttributes = new BasicMarkerAttributes(Material.RED, BasicMarkerShape.ORIENTED_CONE_LINE, 0.7);
		Angle heading = Angle.fromDegrees(headingDeg);
		arrow = new BasicMarker(Position.fromDegrees(latitudeDeg,longitudeDeg, altitudeMeters),markerAttributes,heading);
		List<Marker> markers = new ArrayList<Marker>();
		markers.add(arrow);
		this.setMarkers(markers);
	}

	public void updateArrow(double latitude, double longitude, double altitude, double heading) {
		if (this.arrow == null) {
			this.createArrow(latitude, longitude, altitude, heading);
		} else {
			this.arrow.setHeading(Angle.fromDegrees(heading));
			List<Marker> markers = new ArrayList<Marker>();
			markers.add(arrow);
			this.setMarkers(markers);
		}
	}

	public static class TestApp extends ApplicationTemplate {

		public static class AppFrame extends ApplicationTemplate.AppFrame {

			public AppFrame() {
				DpacsMarkerLayer lobLayer = new DpacsMarkerLayer();
				insertBeforePlacenames(getWwd(), lobLayer);
				this.getLayerPanel().update(this.getWwd());
				lobLayer.updateArrow(30.1, -98.0, 500, 45.0);
			}
		}
	}

	public static void main(String args[]) {
		ApplicationTemplate.start("World Wind Rigid Shapes", TestApp.AppFrame.class);
	}
}