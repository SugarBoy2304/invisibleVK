package myvk.utils.exception;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.PrintWriter;
import java.io.StringWriter;

public class MyException {

    private String title;
    private Alert.AlertType levelError = Alert.AlertType.WARNING;
    private String msg = "null";
    private String error = "null";
    private Exception exception;


    public MyException(String title) {
        this.title = title;
    }

    /** Конструкторы */
    public static MyException generate() {
        return generate("Ошибка");
    }

    public static MyException generate(String title) {
        return new MyException(title);
    }

    /** Отображение **/

    public void show(boolean freez) {
        Alert alert;

        if (exception != null) {

            /** Отрисовка ошибки с логом **/
            exception.printStackTrace();

            alert = new Alert(levelError);
            alert.setTitle(title);
            alert.setHeaderText(msg);
            alert.setContentText(error);

            // Create expandable Exception.
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            exception.printStackTrace(pw);
            String exceptionText = sw.toString();

            Label label = new Label("Код ошибки:");

            TextArea textArea = new TextArea(exceptionText);
            textArea.setEditable(false);
            textArea.setWrapText(true);

            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(label, 0, 0);
            expContent.add(textArea, 0, 1);

            // Set expandable Exception into the dialog pane.
            alert.getDialogPane().setExpandableContent(expContent);

        } else {

            /** Отрисовка собственной ошибки **/

            alert = new Alert(levelError);
            alert.setTitle(title);
            alert.setHeaderText(msg);
            alert.setContentText(error);

        }

        if (freez) alert.showAndWait(); else alert.show();
    }

    /** Setters **/

    public MyException setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public MyException setLevelError(Alert.AlertType levelError) {
        this.levelError = levelError;
        return this;
    }

    public MyException setError(String error) {
        this.error = error;
        return this;
    }

    public MyException setError(StackTraceElement[] arr) {
        String out = "";
        for (StackTraceElement s : arr) out += s.toString() + "\n";
        this.error = out;
        return this;
    }

    public MyException setException(Exception ex) {
        this.exception = ex;
        return this;
    }


}
