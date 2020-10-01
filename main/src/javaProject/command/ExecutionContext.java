package javaProject.command;


import javaProject.CollectionController;
import javaProject.CollectionManager;
import javaProject.FileManager;

public interface ExecutionContext {
    CollectionManager collectionManager();
    CollectionController collectionController();
    FileManager fileManager();
}