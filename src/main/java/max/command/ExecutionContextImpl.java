package max.command;

import max.database.DBRequestManager;
import max.managers.CollectionManager;
import max.managers.FileManager;

import java.util.Locale;
import java.util.ResourceBundle;

public class ExecutionContextImpl implements ExecutionContext {

    private final CollectionManager collectionManager;
    private final DBRequestManager controller;
    private final FileManager fileManager;
    private ResourceBundle resourcesBundle;

    public ExecutionContextImpl(CollectionManager collectionManager, DBRequestManager controller, FileManager fileManager, ResourceBundle bundle) {
        this.collectionManager = collectionManager;
        this.controller = controller;
        this.fileManager = fileManager;
        this.resourcesBundle = bundle;
    }

    @Override
    public CollectionManager collectionManager() {
        return collectionManager;
    }
    @Override
    public DBRequestManager DBRequestManager() {
        return controller;
    }
    @Override
    public FileManager fileManager() {
        return fileManager;
    }
    @Override
    public ResourceBundle resourcesBundle() {
        synchronized (this) {
            return resourcesBundle;
        }
    }
    @Override
    public void setResourcesBundle(Locale locale) {
        synchronized (this) {
            if (!resourcesBundle.getLocale().equals(locale))
                resourcesBundle = ResourceBundle.getBundle("bundles.LangBundle", locale);
        }
    }
}