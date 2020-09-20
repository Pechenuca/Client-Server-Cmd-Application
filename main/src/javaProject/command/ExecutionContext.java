package javaProject.command;


import javaProject.CollectionManager;
import javaProject.FileManager;

public interface ExecutionContext {
    CollectionManager collectionManager();
    FileManager fileManager();
    StringBuilder result();
}