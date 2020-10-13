package max.clientUI.canvas;

import javafx.animation.FadeTransition;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import max.util.OrganizationEntrySerializable;

import java.util.ArrayList;

public class ResizableMapCanvas extends AbsResizableCanvas {

    private static final int SCREEN_START_MARGIN_ERROR_X = 10;
    private static final int SCREEN_START_MARGIN_ERROR_Y = 90;

    public ArrayList<OrganizationEntrySerializable> organizationsList = new ArrayList<>();
    private double scale = 0;
    private GraphicsContext gc;
    private double min;
    private final Pane wrapperMapPane;

    public ResizableMapCanvas(ArrayList<OrganizationEntrySerializable> organizationsList, Pane wrapperMapPane) {
        super();
        this.organizationsList = organizationsList;
        this.wrapperMapPane = wrapperMapPane;
    }

    @Override
    public Object findObj(double coordX, double coordY) throws NullPointerException {
        double min = Math.min(getWidth(), getHeight());
        double finalCoordX = (coordX - SCREEN_START_MARGIN_ERROR_X) * (scale / min) - scale / 2.0;
        double finalCoordY = scale / 2.0 - (coordY - SCREEN_START_MARGIN_ERROR_Y) * (scale / min);

        return organizationsList.stream().filter(organization ->
                Math.abs(organization.getOrganization().getCoordinates().getX() - finalCoordX) < scale * 0.018)
                .filter(organization ->
                        Math.abs(organization.getOrganization().getCoordinates().getY() - finalCoordY) < scale * 0.018)
                .findAny().orElse(null);
    }

    @Override
    public void setObj(Object obj) {
        organizationsList = (ArrayList<OrganizationEntrySerializable>) obj;
    }

    @Override
    public Object getObj() {
        return organizationsList;
    }

    @Override
    public void draw() {
        double width = getWidth();
        double height = getHeight();
        min = Math.min(width, height);

        gc = getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);

        // scale the map
        double maxx = organizationsList.stream().mapToDouble(d -> d.getOrganization().getCoordinates().getX()).max().orElse(getWidth());
        double minx = organizationsList.stream().mapToDouble(d -> d.getOrganization().getCoordinates().getX()).min().orElse(getHeight());
        double maxy = organizationsList.stream().mapToDouble(d -> d.getOrganization().getCoordinates().getY()).max().orElse(0);
        double miny = organizationsList.stream().mapToDouble(d -> d.getOrganization().getCoordinates().getY()).min().orElse(0);
        scale = 2 * Math.max(maxx, Math.max(-Math.min(minx, miny), maxy));

        //draw background
        gc.setFill(Color.DARKGREY);
        gc.fillRect(0, 0, width, height);

        //draw axis
        gc.setFill(Color.BLACK);
        gc.strokeLine(0, min / 2, min, min / 2);
        gc.strokeLine(min / 2, 0, min / 2, min);
        gc.fillText("0.0", min / 2, min / 2 + 20);
        gc.fillText(String.valueOf((int)(-scale*2/2.2 / 4)), min / 4, min / 2 + 20);
        gc.fillText(String.valueOf((int)(scale*2/2.2 / 4)), min * 3.0 / 4.0, min / 2 + 20);

        // Draw organizations
        organizationsList.forEach(this::drawOrganizations);
    }

    private void drawOrganizations(OrganizationEntrySerializable organization) {
        drawOrganization(((organization.getOrganization().getCoordinates().getX() + scale / 2.0) * (min / scale)),
                ((scale / 2.0 - organization.getOrganization().getCoordinates().getY()) * (min / scale)), gc,  organization);
    }

    public void drawOrganization(double x, double y, GraphicsContext gc, OrganizationEntrySerializable organization) {
        double size = setSize(organization);
        x = x - size*120/2D;
        y = y - size*120/2D;
        gc.setFill(Color.valueOf(organization.getOfficialAddress().toString()));
        drawShape(gc, organization, size, x, y);
        drawOfficialAddress(gc, organization, size, x, y);
    }

    private double setSize(OrganizationEntrySerializable organization) {
        if (organization.getAnnualTurnover()<50) return 0.05D*min/400;
        if (organization.getAnnualTurnover() > 1000) {
            return 1D*min/400;
        }
        return organization.getAnnualTurnover()*min/400000D;
    }


    private void drawOfficialAddress(GraphicsContext gc, OrganizationEntrySerializable organization, double size, double x, double y) {
        String officialAddress = organization.getOfficialAddress().toString();
        switch (officialAddress) {
            case "VILLAGE":
                gc.strokePolyline(new double[]{50*size + x, 65*size + x}, new double[]{74*size + y, 74*size + y}, 2);
                break;
            case "TOWN":
                gc.strokePolyline(new double[]{50*size + x, 55*size + x, 60*size + x, 65*size + x}, new double[]{74*size + y, 70*size + y, 74*size + y, 70*size + y}, 4);
                break;
            case "CITY":
                gc.strokePolyline(new double[]{50*size + x, 57*size + x, 65*size + x}, new double[]{72*size + y, 78*size + y, 72*size + y}, 3);
                break;

        }
    }

    private void drawShape(GraphicsContext gc, OrganizationEntrySerializable organization, double size, double x, double y) {
        gc.strokePolygon(new double[]{70 * size + x, 80 * size + x, 110 * size + x, 100 * size + x, 115 * size + x, 90 * size + x, 105 * size + x, 70 * size + x}, new double[]{110 * size + y, 70 * size + y, 65 * size + y, 75 * size + y, 80 * size + y, 88 * size + y, 98 * size + y, 110 * size + y}, 8);
        gc.strokePolygon(new double[]{45 * size + x, 35 * size + x, 5 * size + x, 15 * size + x, 0 * size + x, 25 * size + x, 10 * size + x, 45 * size + x}, new double[]{110 * size + y, 70 * size + y, 65 * size + y, 75 * size + y, 80 * size + y, 88 *size  + y, 98 * size + y, 110 * size + y}, 8);
        gc.strokePolygon(new double[]{80 * size + x, 80 * size + x, 110 * size + x, 140 * size + x, 125 * size + x}, new double[]{125 * size + y, 115 * size + y, 105 * size + y, 80 * size + y, 110 * size + y}, 5);
        gc.strokePolygon(new double[]{40 * size + x, 56 * size + x, 48 * size + x}, new double[]{48 * size + y, 48 * size + y, 33 * size + y}, 3);
        gc.strokePolygon(new double[]{60 * size + x, 76 * size + x, 68 * size + x}, new double[]{48 * size + y, 48 * size + y, 33 * size + y}, 3);
        gc.strokeOval(32 * size + x, 70 * size + y, 50 * size, 70 * size);
        gc.strokeOval(32 * size + x, 110 * size + y, 20 * size, 40 * size);
        gc.strokeOval(65 * size + x, 110 * size + y, 20 * size, 40 * size);
        gc.strokeOval(32 * size + x, 40 * size + y, 50 * size, 40 * size);

        gc.fillOval(32 * size + x, 40 * size + y, 50 * size, 40 * size);
        gc.fillPolygon(new double[]{40 * size + x, 56 * size + x, 48 * size + x}, new double[]{48 * size + y, 48 * size + y, 33 * size + y}, 3);
        gc.fillPolygon(new double[]{60 * size + x, 76 * size + x, 68 * size + x}, new double[]{48 * size + y, 48 * size + y, 33 * size + y}, 3);
        gc.fillPolygon(new double[]{80 * size + x, 80 * size + x, 110 * size + x, 140 * size + x, 125 * size + x}, new double[]{125 * size + y, 115 * size + y, 105 * size + y, 80 * size + y, 110 * size + y}, 5);
        gc.fillOval(32 * size + x, 70 * size + y, 50 * size, 70 * size);
        gc.fillOval(32 * size + x, 110 * size + y, 20 * size, 40 * size);
        gc.fillOval(65 * size + x, 110 * size + y, 20 * size, 40 * size);
        gc.fillPolygon(new double[]{70 * size + x, 80 * size + x, 110 * size + x, 100 * size + x, 115 * size + x, 90 * size + x, 105 * size + x, 70 * size + x}, new double[]{110 * size + y, 70 * size + y, 65 * size + y, 75 * size + y, 80 * size + y, 88 * size + y, 98 * size + y, 110 * size + y}, 8);
        gc.fillPolygon(new double[]{45 * size + x, 35 * size + x, 5 * size + x, 15 * size + x, 0 * size + x, 25 * size + x, 10 * size + x, 45 * size + x}, new double[]{110 * size + y, 70 * size + y, 65 * size + y, 75 * size + y, 80 * size + y, 88 * size + y, 98 * size + y, 110 * size + y}, 8);
    }

    @Override
    public void animateEntry(OrganizationEntrySerializable organization) {
        double x = ((organization.getOrganization().getCoordinates().getX() + scale / 2.0) * (min / scale));
        double y = ((scale / 2.0 - organization.getOrganization().getCoordinates().getY()) * (min / scale));
        Circle circle = new Circle(x, y, setSize(organization) * 120, Color.valueOf(organization.getOfficialAddress().toString()));
        wrapperMapPane.getChildren().add(circle);
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(4), circle);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setCycleCount(1);
        fadeOut.play();

        fadeOut.setOnFinished(e -> {
            wrapperMapPane.getChildren().remove(circle);
            drawOrganizations(organization);
        });
    }
}