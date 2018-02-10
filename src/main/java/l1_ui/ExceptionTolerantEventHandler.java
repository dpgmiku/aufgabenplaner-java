package l1_ui;

/**
 * Created by miku on 09.01.17.
 */
import javafx.event.Event;

import java.util.EventListener;


/**Exception tolerant handler for events of a specific class or type.
 * @author Christoph Knabe
 * @since 2016-08-17
 */
@FunctionalInterface
public interface ExceptionTolerantEventHandler<E extends Event> extends EventListener {

    /**This method is to be implemented by client event handlers. It may throw any Exception.*/
    void handle(final E ev) throws Exception;


}