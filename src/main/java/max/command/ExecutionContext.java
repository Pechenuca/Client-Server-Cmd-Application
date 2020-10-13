package max.command;

import max.CollectionController;
import max.database.DBRequestManager;
import max.managers.CollectionManager;
import max.managers.FileManager;

import java.util.Locale;
import java.util.ResourceBundle;

public interface ExecutionContext {
    CollectionManager collectionManager();
    DBRequestManager DBRequestManager();
    FileManager fileManager();
    ResourceBundle resourcesBundle();
    void setResourcesBundle(Locale locale);
}