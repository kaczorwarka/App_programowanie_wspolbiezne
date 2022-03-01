package com.example.projekt_10_finalny;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class Controller {

    @FXML
    private Rectangle pracownik_1 = new Rectangle();

    @FXML
    private Rectangle pracownik_2 = new Rectangle();

    @FXML
    private Rectangle pracownik_3 = new Rectangle();

    @FXML
    private Rectangle pracownik_4 = new Rectangle();

    @FXML
    private TextField number_threads = new TextField();
    @FXML
    private TextField number_repeats = new TextField();

    @FXML
    private AnchorPane animajce = new AnchorPane();

    @FXML
    private Line line1_1 = new Line();
    @FXML
    private Line line1_2 = new Line();
    @FXML
    private Line line1_3 = new Line();
    @FXML
    private Line line2 = new Line();
    @FXML
    private Line line3 = new Line();
    @FXML
    private Line line4 = new Line();

    @FXML
    private Slider zuzycie = new Slider();
    @FXML
    private Slider naprawa = new Slider();
    @FXML
    private Slider przesyl = new Slider();
    @FXML
    private Slider podpis = new Slider();
    @FXML
    private Slider odsylka = new Slider();

    @FXML
    private Button save = new Button();
    @FXML
    private Button change = new Button();
    @FXML
    private Button start = new Button();

    @FXML
    protected void onSaveButtonClick(){
        change.setDisable(false);
        start.setDisable(false);
        number_threads.setDisable(true);
        number_repeats.setDisable(true);
        save.setDisable(true);
    }

    @FXML
    protected void onChangeButtonClick(){
        start.setDisable(true);
        change.setDisable(true);
        number_threads.setDisable(false);
        number_repeats.setDisable(false);
        save.setDisable(false);
    }


    @FXML
    protected void onHelloButtonClick(){
        int ile_watkow;
        int liczba_powt = Integer.parseInt(number_repeats.getText());
        double roznica_wys = 100;
        int liczba_pracownikow_do = 3;
        int liczba_kurierow = 3;
        Pulka sem = new Pulka();
        if (Integer.parseInt(number_threads.getText()) > 0 && Integer.parseInt(number_threads.getText()) < 7) {
            ile_watkow = Integer.parseInt(number_threads.getText());

            //pobranie polozenia oraz rozmiaru polozenia pracownika odbierajacego zlecenia
            double p1_x = pracownik_1.getLayoutX();
            double p1_y = pracownik_1.getLayoutY();
            double p1_r = pracownik_1.getHeight() / 2;

            //okreslenie polozenia poczatkowego pierwszego klienta
            double circle_x = p1_x + p1_r;
            double circle_y = p1_y - roznica_wys;
            double circle_r = p1_r;

            //modyfikacja polozenia pierwszego klienta w zaleznosci od ilosci klientow
            circle_x -= (ile_watkow - 1) * circle_r;

            Klient[] klients = new Klient[]{
                    new Klient(0, liczba_powt, circle_x, circle_y, circle_r, p1_x, p1_y, animajce, sem, zuzycie, przesyl),
                    new Klient(1, liczba_powt, circle_x, circle_y, circle_r, p1_x, p1_y, animajce, sem, zuzycie, przesyl),
                    new Klient(2, liczba_powt, circle_x, circle_y, circle_r, p1_x, p1_y, animajce, sem, zuzycie, przesyl),
                    new Klient(3, liczba_powt, circle_x, circle_y, circle_r, p1_x, p1_y, animajce, sem, zuzycie, przesyl),
                    new Klient(4, liczba_powt, circle_x, circle_y, circle_r, p1_x, p1_y, animajce, sem, zuzycie, przesyl),
                    new Klient(5, liczba_powt, circle_x, circle_y, circle_r, p1_x, p1_y, animajce, sem, zuzycie, przesyl),
            };

            //laczna ilosc wszystkich zuzytych sprzetow wyslanych przez klientow (rozdzielona rownomiernie miedzy pracownikow naprawiajacych)
            liczba_powt *= ile_watkow;

            Pracownik pracownik = new Pracownik(0, liczba_powt, sem, 'g', p1_x, p1_y, p1_r, animajce, naprawa, przesyl, podpis);

            Pracownik pracownik1 = new Pracownik(1, liczba_powt / liczba_pracownikow_do + liczba_powt % liczba_pracownikow_do, sem, 'd',
                    pracownik_2.getLayoutX(), pracownik_2.getLayoutY(), pracownik_2.getHeight() / 2, animajce, naprawa, przesyl, podpis);

            Pracownik pracownik2 = new Pracownik(2, liczba_powt / liczba_pracownikow_do, sem, 'd',
                    pracownik_3.getLayoutX(), pracownik_3.getLayoutY(), pracownik_3.getHeight() / 2, animajce, naprawa, przesyl, podpis);

            Pracownik pracownik3 = new Pracownik(3, liczba_powt / liczba_pracownikow_do, sem, 'd',
                    pracownik_4.getLayoutX(), pracownik_4.getLayoutY(), pracownik_4.getHeight() / 2, animajce, naprawa, przesyl, podpis);

            pracownik.start();
            pracownik1.start();
            pracownik2.start();
            pracownik3.start();

            //zdefiniowane watkow kurierow odsylajacych naprawiony sprzet
            Kurier kurier1 = new Kurier(liczba_powt / liczba_kurierow + liczba_powt % liczba_kurierow, 0,
                    line1_1, line2, line3, line4, pracownik_2.getLayoutY() + pracownik_2.getHeight() / 2, 0,
                    animajce, sem, circle_x, circle_r, odsylka);

            Kurier kurier2 = new Kurier(liczba_powt / liczba_kurierow, 1,
                    line1_2, line2, line3, line4, pracownik_3.getLayoutY() + pracownik_3.getHeight() / 2, 0,
                    animajce, sem, circle_x, circle_r, odsylka);

            Kurier kurier3 = new Kurier(liczba_powt / liczba_kurierow, 2,
                    line1_3, line2, line3, line4, pracownik_4.getLayoutY() + pracownik_4.getHeight() / 2, 0,
                    animajce, sem, circle_x, circle_r, odsylka);

            kurier1.start();
            kurier2.start();
            kurier3.start();

            //wywolanie zadeklarowanej ilosci klientow
            for (int i = 0; i < ile_watkow; i++) {
                klients[i].start();
            }
        }
    }
}