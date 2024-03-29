package main.java.task0.gui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.util.Optional;


public abstract class SimpleDialog<T> extends Dialog<T> {
    public SimpleDialog(String title, String header) {
        // Create the custom dialog.
        this.setTitle(title);
        this.setHeaderText(header);

        // Set the button types.
        ButtonType confirmButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        this.addComponents(grid);

        this.getDialogPane().setContent(grid);

        // Convert the result to a username-password-pair when the login button is clicked.
        this.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType)
                return this.getValue();
            return null;
        });
    }

    public abstract void addComponents(GridPane grid);
    public abstract T getValue();

    public static class MarkDialog extends SimpleDialog<Integer> {
        private Spinner<Integer> markspinner;

        public MarkDialog() {
            super("Mark Dialog", "Insert Mark");
        }

        public void addComponents(GridPane grid) {
            markspinner = new Spinner<Integer>();
            SpinnerValueFactory<Integer> valueFactory = //
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 30, 18);
            markspinner.setValueFactory(valueFactory);

            // Request focus on the markspinner field by default.
            Platform.runLater(markspinner::requestFocus);

            grid.add(new Label("Mark:"), 0, 0);
            grid.add(markspinner, 1, 0);
        }

        public Integer getValue() {
            return markspinner.getValue();
        }

        public static Integer showDialog() {
            MarkDialog dialog = new MarkDialog();
            Optional<Integer> result = dialog.showAndWait();
            return result.orElse(null);
        }
    }

    public static class DateDialog extends SimpleDialog<LocalDate> {
        private DatePicker datepicker;

        public DateDialog() {
            super("Date Dialog", "Insert Date");
        }

        public void addComponents(GridPane grid) {
            datepicker = new DatePicker();
            datepicker.setValue(LocalDate.now());

            // Request focus on the markspinner field by default.
            Platform.runLater(datepicker::requestFocus);

            grid.add(new Label("Date:"), 0, 0);
            grid.add(datepicker, 1, 0);
        }

        public LocalDate getValue() {
            return datepicker.getValue();
        }

        public static LocalDate showDialog() {
            DateDialog dialog = new DateDialog();
            Optional<LocalDate> result = dialog.showAndWait();
            return result.orElse(null);
        }
    }

    public static void showErrorDialog(String error) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("Error");
        alert.setContentText(error);

        alert.showAndWait();
    }

    public static void showConfirmDialog(String info) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Operation completed successfully");
        alert.setContentText(info);

        alert.showAndWait();
    }
}
