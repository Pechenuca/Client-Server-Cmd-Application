package max.command;

import max.CollectionController;
import max.managers.CollectionManager;
import max.managers.FileManager;

public interface ExecutionContext {
    CollectionManager collectionManager();
    CollectionController collectionController();
    FileManager fileManager();
}