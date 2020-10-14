package max.command;


import java.io.Serializable;
import java.util.Arrays;

public abstract class Command implements ICommand, Serializable {
    private static final long serialVersionUID = 2901644046809010785L;
    protected String commandKey;
    protected String description = "No Description";
    protected String[] args;

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
     * Создает объект класса dragon для передачи его серверу
     *
     * @param obj in order to set the input required by the max.command called
     */
    public void addInput(Object obj) {
        //
    }

    /**
     *
     * Определяет, нужен ли данной команде дополнительный ввод объекта класса dragon
     *
     * @return true если ввод необходим, иначе false
     */
    public int requireInput() {
        return TYPE_NO_INPUT;
    }

    public Object getInput() {
        return null;
    }

    public boolean isNumeric(String strNum) {
        if (strNum == null)
            return false;
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() +
                "{" +
                "args=" + Arrays.toString(args) +
                "organization=" + getInput() +
                '}';
    }
}