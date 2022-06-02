package com.game.radaykin_vlad_201_hw5;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;


/*
 * Класс контроллер
 */
public class JigsawController {

    private final Group[] tilesField; // массив для фигур на доске
    private int[][] busyTiles; // массив для хранения расположенных на доске фигур
    private int[][] chosenFigure; // вабранная из списка фигура
    private int gameScore; // количество очков, набранных игроком
    private Text gameScoreText; // поля для вывода счета
    private Tile dragTile;
    private Timeline timeline; // переменная для хранения времени, затраченного на игру
    private Text timelineText; // поле для вывода текущего времени на экран
    private int minutes = 0, secs = 0, millis = 0; // счетчики для обновления минут, секунд и миллисекунд
    final int SIZE = 50; // размер одного поля
    private DBWorker db = new DBWorker();

    @FXML
    private Button resultButton;

    @FXML
    private AnchorPane mainWindow;

    @FXML
    private Button restartButton;

    @FXML
    private TextFlow gameScoreTextField;

    @FXML
    private TextFlow timelineTextField;

    @FXML
    void initialize() {
        timelineText = new Text("00:00:000");
        timelineText.setFont(Font.font(14));
        timeline = new Timeline(new KeyFrame(Duration.millis(1), event -> Change(timelineText)));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(false);
        timeline.play();

        gameScoreText = new Text("0");
        gameScoreText.setFont(Font.font(14));
        gameScoreTextField.getChildren().add(gameScoreText);

        timelineTextField.getChildren().add(timelineText);
    }

    /*
     * Конструктор класса
     */
    public JigsawController() {

        gameScore = 0;
        tilesField = new Group[82];

        for (int i = 0; i < 82; ++i) {
            tilesField[i] = new Group();
        }
        busyTiles = new int[9][9];

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                busyTiles[i][j] = 0;
            }
        }
    }

    /*
     *Метод для завершения игры
     */
    @FXML
    public void resultButtonAction() {

        Alert message_box = new Alert(Alert.AlertType.INFORMATION);

        String login = "player" + (int)(Math.random() * 1000);
        db.addInfo(login, minutes * 60 + secs, gameScore);

        message_box.setTitle("Результат");
        timeline.pause();
        minutes = 0;
        secs = 0;
        millis = 0;

        db.getInfo();
        message_box.setHeaderText("Результат: " + (gameScore == 0 ? 0:(gameScore - 1))
                + "\n" + "Игровое время: " + timelineText.getText());
        message_box.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK) {
                System.out.println("Pressed OK.");
            }
        });
        Stage stage = (Stage) resultButton.getScene().getWindow();
        stage.close();
    }

    public EventHandler<WindowEvent> getResultEventHandler(){
        return windowEvent -> resultButtonAction();
    }

    /*
     * Метод для перезапуска игры
     */
    @FXML
    public void restartButtonAction() {

        restartButton.setText("Обновить");
        gameScore = 0;
        busyTiles = new int[9][9];

        for (Group shape : tilesField) {
            if (!shape.getChildren().isEmpty()) {
                shape.getChildren().clear();
            }
        }
        for (int i = 0; i < 82; ++i) {
            tilesField[i] = new Group();
        }
        createFigure();
    }

    /*
     * Метод отображения фигуры
     */
    private void showFigure() {

        // номер фигуры в списке
        int figureId = (int) (Math.random() * (Figures.get_figures_length()));
        chosenFigure = Figures.getChosenFigure(figureId);

        for (int i = 0; i < chosenFigure.length; ++i) {
            for (int j = 0; j < chosenFigure[i].length; ++j) {
                if (chosenFigure[i][j] == 1) {
                    Rectangle square = new Rectangle();
                    square.setX(275 + i * SIZE);
                    square.setY(42 + j * SIZE);
                    square.setHeight(SIZE);
                    square.setWidth(SIZE);
                    square.setStroke(Color.BLACK);
                    square.setStrokeWidth(2);
                    square.setFill(Color.BLUE);
                    tilesField[gameScore].getChildren().add(square);
                }
            }
        }
    }

    /*
     * Метод для создания фигуры
     */
    private void createFigure() {

        gameScoreText.setText(String.valueOf(gameScore));
        showFigure();
        mainWindow.getChildren().add(tilesField[gameScore++]);
        dragTile = new Tile();
        mainWindow.setOnMousePressed(this::handleStart);
        mainWindow.setOnMouseReleased(this::handleDrop);
        mainWindow.setOnMouseDragged(this::handleMove);
        mainWindow.setOnMouseEntered(this::handleStop);
    }

    /*
     * Метод для проверки возможности постановки фигуры на выбранное место
     */
    private boolean tryToTakePlace() {

        int old_x = (int) tilesField[gameScore - 1].getLayoutX();
        int old_y = (int) tilesField[gameScore - 1].getLayoutY();

        for (int x = -150; x <= 250; x+=SIZE) {
            for (int y = 200; y <= 600; y+=SIZE) {
                if (x == old_x && y == old_y) {
                    int new_x = (x + 150) / SIZE;
                    int new_y = (y - 200) / SIZE;

                    for (int i = 0; i < chosenFigure.length && new_x < 9; ++new_x, ++i) {
                        for (int j = 0; j < chosenFigure[i].length && new_y < 9; ++j) {
                            if (chosenFigure[i][j] == 1 && busyTiles[new_x][j + new_y] == 1) {
                                return false;
                            }
                        }
                    }
                    new_x = (x + 150) / SIZE;
                    new_y = (y - 200) / SIZE;

                    for (int i = 0; i < chosenFigure.length && new_x < 9; ++new_x, ++i) {
                        for (int j = 0; j < chosenFigure[i].length && new_y < 9; ++j) {
                            if (chosenFigure[i][j] == 1 && busyTiles[new_x][j + new_y] == 0) {
                                busyTiles[new_x][new_y +j] = 1;
                            }
                        }
                    }
                    return true;
                }
            }
        }
        return true;
    }

    /*
     * Метод для старта движения фигуры
     */
    private void handleStart(MouseEvent mouseEvent) {
        dragTile.setX(tilesField[gameScore - 1].getLayoutX() - mouseEvent.getSceneX());
        dragTile.setY(tilesField[gameScore - 1].getLayoutY() - mouseEvent.getSceneY());
        tilesField[gameScore - 1].setCursor(Cursor.MOVE);

    }

    /*
     * Метод для помещения фигуры на доску
     */
    private void handleDrop(MouseEvent mouseEvent) {

        Bounds bounds = tilesField[gameScore - 1].getLayoutBounds();
        tilesField[gameScore - 1].setCursor(Cursor.HAND);
        double x =  tilesField[gameScore - 1].getLayoutX();
        double y =  tilesField[gameScore - 1].getLayoutY();

        if (bounds.getHeight() == 52) {
            if (bounds.getWidth() == 52 && isInsideField(-174, 274, 176, 615)) {
                tilesField[gameScore - 1].setLayoutX(0);
                tilesField[gameScore - 1].setLayoutY(0);
                return;
            } else if (bounds.getWidth() == 102 && isInsideField(-174, 224, 176, 615)) {
                tilesField[gameScore - 1].setLayoutX(0);
                tilesField[gameScore - 1].setLayoutY(0);
                return;
            } else if (bounds.getWidth() == 152 && isInsideField(-174, 176, 176, 615)) {
                tilesField[gameScore - 1].setLayoutX(0);
                tilesField[gameScore - 1].setLayoutY(0);
                return;
            }
        } else if (bounds.getHeight() == 102) {
            if (bounds.getWidth() == 52 && isInsideField(-174, 274, 176, 574)) {
                tilesField[gameScore - 1].setLayoutX(0);
                tilesField[gameScore - 1].setLayoutY(0);
                return;
            } else if (bounds.getWidth() == 102 && isInsideField(-174, 224, 176, 574)) {
                tilesField[gameScore - 1].setLayoutX(0);
                tilesField[gameScore - 1].setLayoutY(0);
                return;
            } else if (bounds.getWidth() == 152 && isInsideField(-174, 176, 176, 574)) {
                tilesField[gameScore - 1].setLayoutX(0);
                tilesField[gameScore - 1].setLayoutY(0);
                return;
            }
        } else  if (bounds.getHeight() == 152) {
            if (bounds.getWidth() == 52 && isInsideField(-174, 274, 176, 524)) {
                tilesField[gameScore - 1].setLayoutX(0);
                tilesField[gameScore - 1].setLayoutY(0);
                return;
            } else if (bounds.getWidth() == 102 && isInsideField(-174, 224, 176, 524)) {
                tilesField[gameScore - 1].setLayoutX(0);
                tilesField[gameScore - 1].setLayoutY(0);
                return;
            } else if (bounds.getWidth() == 152 && isInsideField(-174, 176, 176, 524)) {
                tilesField[gameScore - 1].setLayoutX(0);
                tilesField[gameScore - 1].setLayoutY(0);
                return;
            }
        }
        if (y % SIZE > 25) {
            tilesField[gameScore - 1].setLayoutY(tilesField[gameScore - 1].getLayoutY() + (SIZE - y % SIZE));
        } else if (y % SIZE <= 25) {
            tilesField[gameScore - 1].setLayoutY(tilesField[gameScore - 1].getLayoutY() - (y % SIZE));
        }
        if (x % SIZE < 25 && x > 0) {
            tilesField[gameScore - 1].setLayoutX(tilesField[gameScore - 1].getLayoutX() - (x % SIZE));
        } else if (Math.abs(x % SIZE) >= 25 && x < 0) {
            tilesField[gameScore - 1].setLayoutX(tilesField[gameScore - 1].getLayoutX() - (SIZE + x % SIZE));
        } else if (Math.abs(x % SIZE) < 25 && x < 0) {
            tilesField[gameScore - 1].setLayoutX(tilesField[gameScore - 1].getLayoutX() - ((x % SIZE)));
        } else if (x % SIZE >= 25 && x > 0) {
            tilesField[gameScore - 1].setLayoutX(tilesField[gameScore - 1].getLayoutX() + (SIZE - x % SIZE));
        }
        if (!tryToTakePlace()) {
            tilesField[gameScore - 1].setLayoutX(0);
            tilesField[gameScore - 1].setLayoutY(0);
        } else {
            gameScoreText.setText(String.valueOf(gameScore));
            createFigure();
        }
    }

    /*
     * Метод для проверки нахождения фигуры внутри поля
     */
    private boolean isInsideField(int r_x, int l_x, int up_y, int down_y) {

        return (tilesField[gameScore - 1].getLayoutY() < up_y || tilesField[gameScore - 1].getLayoutY() > down_y)
                || tilesField[gameScore - 1].getLayoutX() > l_x || (tilesField[gameScore - 1].getLayoutX() < r_x);
    }

    /*
     * Метод для обработки перемещения фигуры
     */
    private void handleMove(MouseEvent mouseEvent) {
        tilesField[gameScore - 1].setLayoutX(mouseEvent.getSceneX() + dragTile.getX());
        tilesField[gameScore - 1].setLayoutY(mouseEvent.getSceneY() + dragTile.getY());
    }

    private void handleStop(MouseEvent mouseEvent) {
        tilesField[gameScore - 1].setCursor(Cursor.HAND);
    }

    /*
     * Метод для изменения времени в секундомере
     * */
    void Change(Text text) {
        if (millis == 1000) {
            secs++;
            millis = 0;
        }
        if (secs == 60) {
            minutes++;
            secs = 0;
        }
        text.setText((((minutes / 10) == 0) ? "0" : "") + minutes + ":"
                + (((secs / 10) == 0) ? "0" : "") + secs + ":"
                + (((millis / 10) == 0) ? "00" : (((millis / 100) == 0) ? "0" : "")) + millis++);
    }
}
