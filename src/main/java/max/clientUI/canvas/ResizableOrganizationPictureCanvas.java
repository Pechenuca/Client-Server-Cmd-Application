package max.clientUI.canvas;

import javafx.animation.RotateTransition;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import max.coreSources.Organization;
import max.util.OrganizationEntrySerializable;

public class ResizableOrganizationPictureCanvas extends AbsResizableCanvas {

    private Organization organization = null;

    public ResizableOrganizationPictureCanvas() {
        super();
    }

    @Override
    public Object findObj(double coordX, double coordY) {
        return null;
    }

    @Override
    public void setObj(Object obj) {
        organization = (Organization) obj;
    }

    @Override
    public Object getObj() {
        return organization;
    }

    @Override
    public void draw() {
        double width = getWidth();
        double height = getHeight();

        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);
        if (organization != null)
            drawOrganization(gc);
        else {
            gc.setStroke(Color.RED);
            gc.strokeLine(0, 0, width, height);
            gc.strokeLine(0, height, width, 0);
        }
    }

    private void drawOrganization(GraphicsContext gc) {
        gc.setFill(Color.valueOf(organization.getColor().toString()));
        drawShape(gc);

    }



    private void drawShape(GraphicsContext gc) {
        gc.strokePolygon(new double[]{70, 80, 110, 100, 115, 90, 105, 70}, new double[]{110, 70, 65, 75, 80, 88, 98, 110}, 8);
        gc.strokePolygon(new double[]{45, 35, 5, 15, 0, 25, 10, 45}, new double[]{110, 70, 65, 75, 80, 88, 98, 110}, 8);
        gc.strokePolygon(new double[]{45, 35, 5, 15, 0, 25, 10, 45}, new double[]{110, 70, 65, 75, 80, 88, 98, 110}, 8);
        gc.strokePolygon(new double[]{80, 80, 110, 140, 125}, new double[]{125, 115, 105, 80, 110}, 5);
        gc.strokePolygon(new double[]{40, 56, 48}, new double[]{48, 48, 33}, 3);
        gc.strokePolygon(new double[]{60, 76, 68}, new double[]{48, 48, 33}, 3);
        gc.strokeOval(32, 70, 50, 70);
        gc.strokeOval(32, 110, 20, 40);
        gc.strokeOval(65, 110, 20, 40);
        gc.strokeOval(32, 40, 50, 40);

        gc.fillOval(32, 40, 50, 40);
        gc.fillPolygon(new double[]{40, 56, 48}, new double[]{48, 48, 33}, 3);
        gc.fillPolygon(new double[]{60, 76, 68}, new double[]{48, 48, 33}, 3);
        gc.fillPolygon(new double[]{80, 80, 110, 140, 125}, new double[]{125, 115, 105, 80, 110}, 5);
        gc.fillOval(32, 70, 50, 70);
        gc.fillOval(32, 110, 20, 40);
        gc.fillOval(65, 110, 20, 40);
        gc.fillPolygon(new double[]{70, 80, 110, 100, 115, 90, 105, 70}, new double[]{110, 70, 65, 75, 80, 88, 98, 110}, 8);
        gc.fillPolygon(new double[]{45, 35, 5, 15, 0, 25, 10, 45}, new double[]{110, 70, 65, 75, 80, 88, 98, 110}, 8);
        gc.fillPolygon(new double[]{45, 35, 5, 15, 0, 25, 10, 45}, new double[]{110, 70, 65, 75, 80, 88, 98, 110}, 8);
    }

    @Override
    public void animateEntry(OrganizationEntrySerializable dragon) {
        RotateTransition rotateTransition = new RotateTransition();
        rotateTransition.setDuration(Duration.millis(200));
        rotateTransition.setNode(this);
        rotateTransition.setByAngle(360);
        rotateTransition.setCycleCount(1);
        rotateTransition.setAutoReverse(false);
        rotateTransition.play();
    }
}