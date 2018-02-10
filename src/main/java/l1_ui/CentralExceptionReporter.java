package l1_ui;



import javafx.event.ActionEvent;
import javafx.scene.Node;
import l1_ui.multex.FxAlerter;

import java.util.Locale;
import java.util.ResourceBundle;

public class CentralExceptionReporter {

    /**The baseName for locating the exception message text resource bundle.*/
    public static final String BASE_NAME = "MessageResources";

    /**The bundle to be used for localizing message texts.*/
    private final ResourceBundle bundle;

    public CentralExceptionReporter() {
        final Locale defaultLocale = Locale.getDefault();
        this.bundle = ResourceBundle.getBundle(BASE_NAME, defaultLocale);
    }

    /**Reports the given exception with localized message text in a modal dialog, which blocks input to the root parent window of the UI Node, which generated the event.*/
    public void reportException(final ActionEvent ev, final Exception ex) {
        final Object evSource = ev.getSource();
        final Node sourceNode = evSource instanceof Node ? (Node) evSource : null;
        new FxAlerter(bundle).report(ex, sourceNode);
    }
}