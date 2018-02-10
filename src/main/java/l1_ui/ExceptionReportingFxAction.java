package l1_ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;


/**An named action for JavaFX, which is allowed to throw any Exception.
 * If an exception occurs during execution of the handle method of the attribute {@link #eventHandler},
 * it will be reported to a new modal dialog blocking the causing UI component for further input, until the dialog is closed.
 * @author Christoph Knabe
 * @since 2016-08-17
 */
public class ExceptionReportingFxAction implements EventHandler<javafx.event.ActionEvent> {


    /**The name of this Action, as to be displayed on the user interface.*/
    public final String name;

    /**The event handler to be called, when method {@link #handle(ActionEvent)} is called.*/
        public final ExceptionTolerantEventHandler<ActionEvent> eventHandler;

    /**Initializes an ExceptionReportingSwingAction.
     * @param name how to name the button or menu item, which is associated with the action.
     * @param eventHandler handler to be called, when method {@link #handle(ActionEvent)} is called.
     */
    public ExceptionReportingFxAction(final String name, final ExceptionTolerantEventHandler<ActionEvent> eventHandler){
        this.name = name;
        this.eventHandler = eventHandler;
    }

    @Override
    public void handle(final ActionEvent ev) {
        try{
            eventHandler.handle(ev);
        }catch(Exception ex){
            new CentralExceptionReporter().reportException(ev, ex);
        }
    }

}