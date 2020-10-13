package max.clientUI.controls;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class TitledBorderedPanel extends StackPane
{
    private final Label titleLabel = new Label();
    private final StackPane contentPane = new StackPane();
    private Node content;

    public void setContent(Node content) {
        content.getStyleClass().add("bordered-titled-content");
        contentPane.getChildren().add(content);
    }

    public Node getContent() {
        return content;
    }

    public void setTitle(String title) {
        titleLabel.setText(" " + title + " ");
    }

    public String getTitle() {
        return titleLabel.getText();
    }



    public TitledBorderedPanel() {
        titleLabel.setText("default title");
        titleLabel.getStyleClass().add("bordered-titled-title");
        StackPane.setAlignment(titleLabel, Pos.TOP_LEFT);

        getStyleClass().add("bordered-titled-border");
        getChildren().addAll(titleLabel, contentPane);
    }

}