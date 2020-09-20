package javaProject.command;


import javaProject.coreSources.Organization;
import java.io.Serializable;
import java.util.Arrays;

public abstract class Command implements ICommand, Serializable {

    private static final long serialVersionUID = 2901644046809010785L;
    protected String commandKey;
    protected String description = "No Description";
    protected String[] args;
    protected boolean requireInputs = false;

    public Command() {}
    public Command(String[] args) {
        this.args = args;
    }
    public String getCommandKey() {
        return commandKey;
    }
    public String getDescription() {
        return description;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    /**
     *
     * Создает объект класса Organization для передачи его серверу
     *
     *
     *
     */
    public void addOrgInput(Organization organization) {
        //
    }
    /**
     *
     * Определяет, нужен ли данной команде дополнительный ввод объекта класса dragon
     *
     *
     * @return true если ввод необходим, иначе false
     */
    public boolean requireOrgInput() {
        return requireInputs;
    }
    public Organization getOrganization() {
        return null;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() +
                "{" +
                "args=" + Arrays.toString(args) +
                '}';
    }
}