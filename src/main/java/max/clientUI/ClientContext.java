package max.clientUI;

import max.managers.CommandManager;
import max.network.ClientChannel;
import max.network.ClientResponseHandler;

public interface ClientContext {
    CommandManager commandManager();
    ClientChannel clientChannel();
    ClientResponseHandler responseHandler();
    LocalCollectionManager localCollection();
}