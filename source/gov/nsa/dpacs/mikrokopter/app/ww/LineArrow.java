/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nsa.dpacs.mikrokopter.app.ww;

import gov.nasa.worldwind.View;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwindx.examples.util.DirectedPath;
import java.nio.FloatBuffer;

/**
 *
 * @author Rob
 */
public class LineArrow extends DirectedPath {

	public LineArrow() {
	}

	public LineArrow(Iterable<? extends Position> positions) {
		super(positions);
	}

	@Override
	protected void computeArrowheadGeometry(DrawContext dc, Vec4 polePtA, Vec4 polePtB, Vec4 ptA, Vec4 ptB, double arrowLength, double arrowBase, FloatBuffer buffer, PathData pathData) {
		// Build a triangle to represent the arrowhead. The triangle is built from two vectors, one parallel to the
		// segment, and one perpendicular to it. The plane of the arrowhead will be parallel to the surface.
		double poleDistance = polePtA.distanceTo3(polePtB);
		// Compute parallel component
		Vec4 parallel = ptA.subtract3(ptB);
		Vec4 surfaceNormal = dc.getGlobe().computeSurfaceNormalAtPoint(ptB);
		// Compute perpendicular component
		Vec4 perpendicular = surfaceNormal.cross3(parallel);
		// Compute midpoint of segment
		Vec4 arrowHead = polePtB;
		if (!this.isArrowheadSmall(dc, arrowHead, 1)) {
			// Compute the size of the arrowhead in pixels to make ensure that the arrow does not exceed the maximum
			// screen size.
			View view = dc.getView();
			double arrowHeadDistance = view.getEyePoint().distanceTo3(arrowHead);
			double pixelSize = view.computePixelSizeAtDistance(arrowHeadDistance);
			if (arrowLength / pixelSize > this.maxScreenSize) {
				arrowLength = this.maxScreenSize * pixelSize;
				arrowBase = arrowLength * this.getArrowAngle().tanHalfAngle();
			}
			// Don't draw an arrowhead if the path segment is smaller than the arrow's base or length
			if (poleDistance <= arrowLength || poleDistance <= arrowBase) {
				return;
			}
			perpendicular = perpendicular.normalize3().multiply3(arrowBase);
			parallel = parallel.normalize3().multiply3(arrowLength);
			// Compute geometry of direction arrow
			Vec4 vertex1 = arrowHead.add3(parallel).add3(perpendicular);
			Vec4 vertex2 = arrowHead.add3(parallel).add3(perpendicular.multiply3(-1.0));
			// Add geometry to the buffer
			Vec4 referencePoint = pathData.getReferencePoint();
			buffer.put((float) (vertex1.x - referencePoint.x));
			buffer.put((float) (vertex1.y - referencePoint.y));
			buffer.put((float) (vertex1.z - referencePoint.z));
			buffer.put((float) (vertex2.x - referencePoint.x));
			buffer.put((float) (vertex2.y - referencePoint.y));
			buffer.put((float) (vertex2.z - referencePoint.z));
			buffer.put((float) (arrowHead.x - referencePoint.x));
			buffer.put((float) (arrowHead.y - referencePoint.y));
			buffer.put((float) (arrowHead.z - referencePoint.z));
		}
	}
	
}
