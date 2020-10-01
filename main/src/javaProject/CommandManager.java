package javaProject;

import javaProject.command.*;
import javaProject.command.collectionhandlers.*;
import javaProject.exception.NoSuchCommandException;


import java.util.*;

public class CommandManager {
    private final Map<String, Command> commands;
    /**
     * Конструктор - создает объект класса CommandManager
     */
    public CommandManager() {
        this.commands = new HashMap<>();
        initCommands();
    }

    public void initCommands() {
        commands.put("help", new HelpCommand(this.getKeysCommands()));
        commands.put("man", new DescriptorCommand(this.getCommands()));
        commands.put("info", new InfoCommand());
        commands.put("show", new ShowCommand());
        commands.put("update", new UpdateCommand());
        commands.put("remove_key", new RemoveKeyCommand());
        commands.put("clear", new ClearCommand());
        commands.put("remove_greater_key", new RemoveGreaterKeyCommand());
        commands.put("print_descending", new PrintDescendingCommand());
        commands.put("exit", new ExitCommand());
        commands.put("execute_script", new ExecuteScriptCommand(getCommandsValues()));;
        commands.put("login", new LoginCommand());
        commands.put("register", new RegisterCommand());
    }

    /**
     * Функция выполнения команды
     * @param commandStr - строка, содержащая ключ команды
     */
    public Command getCommand(String commandStr) throws NoSuchElementException {
        String[] cmd = getCommandFromStr(commandStr);
        Command command = this.getCommandFromMap(cmd[0]);
        command.setArgs(this.getCommandArgs(cmd));
        return command;
    }

    /**
     * Функция разделения строки на слова
     * @param s - строка входных данных
     * @return возвращает массив слов из входных данных
     */
    public String[] getCommandFromStr(String s) {
        return s.trim().split(" ");
    }

    /**
     * Функция получения аргументов команды из входных данных
     * @param fullStr - строка входных данных
     * @return возвращает массив аргументов команды
     */
    public String[] getCommandArgs(String[] fullStr) {
        String[] inputArgs = new String[2];
        inputArgs = Arrays.copyOfRange(fullStr, 1, fullStr.length);
        return inputArgs;
    }
    /*
     * Функция получения команды из коллекции команд по ключу
     * @param key - строка входных данных - ключ команды
     * @return возвращает объект класса Command
     */
    public Command getCommandFromMap(String key) throws NoSuchCommandException {
        if (!commands.containsKey(key)) {
            throw new NoSuchCommandException("What are u writing? type 'help' for the available commands. \nUnknown: '" + key + "'");
        }
        return commands.getOrDefault(key, null);
    }

    private List<Command> getCommandsValues(){
        List<Command> l = new ArrayList<>();
        commands.values().forEach(e -> {
            if(!(e.getCommandKey().equals("help")) && !(e.getCommandKey().equals("man")))
                l.add(e);
        });
        return l;
    }

    public Set<String> getKeysCommands() {
        return this.getCommands().keySet();
    }

    public Map<String, Command> getCommands() {
        return this.commands;
    }
}