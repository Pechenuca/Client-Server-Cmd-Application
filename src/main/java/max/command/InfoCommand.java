package max.command;


import max.database.Credentials;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;

public class InfoCommand extends Command {

    public InfoCommand() {
        commandKey = "info";
        description = "вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)";
    }

    @Override
    public Object execute(ExecutionContext context, Credentials credentials) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        return MessageFormat.format(
                context.resourcesBundle().getString("server.response.command.info"),
                context.collectionManager().getCollection().getClass(),
                dateFormatter.format(context.collectionManager().getColCreationDate()),
                context.collectionManager().getCollection().size());
    }
}