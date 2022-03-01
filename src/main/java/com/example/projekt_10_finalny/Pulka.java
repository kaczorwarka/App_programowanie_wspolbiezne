package com.example.projekt_10_finalny;


import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.util.Duration;
import java.util.Random;
import java.util.concurrent.Semaphore;
import static javafx.scene.paint.Color.*;

public class Pulka {
    public static int j = 0;
    public static int k = 0;
    public static int R = 10;
    //buf - pulka na ktora odkladane sa urzadzenia do naprawy
    public static String[] buf = new String[R];
    //dane klienta w postaci stringa modyfikowane przez pracownika pryzjmujacego
    public static String wysylka;
    public static Semaphore wolne = new Semaphore(R);
    public static Semaphore zajete = new Semaphore(0);
    public static Semaphore chron_j = new Semaphore(1);
    public static Semaphore chron_k = new Semaphore(1);
    public static Semaphore Swysylka_k = new Semaphore(1);
    public static Semaphore Swysylka_p = new Semaphore(0);
    public static Semaphore[] wysylka_p = new Semaphore[]{
            new Semaphore(1),
            new Semaphore(1),
            new Semaphore(1)
    };
    public static Semaphore[] wysylka_k = new Semaphore[]{
            new Semaphore(0),
            new Semaphore(0),
            new Semaphore(0)
    };
    public static Semaphore[] skrzynka = new Semaphore[]{
            new Semaphore(0),
            new Semaphore(0),
            new Semaphore(0),
            new Semaphore(0),
            new Semaphore(0),
            new Semaphore(0),
    };
    //miejsce gdzie pracownik nadaje adres danemu urzadzeniu
    public static Circle p1;
    //pulka na ktora odkladane sa urzadzenia do naprawy
    public static Circle[] buf_circle = new Circle[R];
    //skrzynka w ktorej urzadzenie oczekuje na odebranie przez kuriera
    public static Circle[] do_circle = new Circle[3];
    //skrzynka w ktorej dane urzadzenia oczekuja na odebranie przez kuriera
    public static String[] do_dana = new String[3];
    //lista adresow
    public static Paint[] adresy = new Paint[] {BLUE,GREEN,BROWN,RED,ORANGE,PURPLE};
    //adres jaki nadaje pracownik przyjmujace zamuwienia
    public static Paint kolor;
    //poczatek pulki
    double buf_x = 45;

    Random random = new Random();

    //Wysalenie przez klienta popsutego przedmotu
    void wyslanie_k(String adres,int ktory, Circle circle, double circle_r, double p1_x, double p1_y, AnchorPane animacje, Slider przesyl){
        try {
            Swysylka_k.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        wysylka = adres;
        //zadeklarowanie funckji przemieszczenia
        Path path = new Path();
        MoveTo moveTo = new MoveTo();
        LineTo lineTo = new LineTo();
        moveTo.setX(circle.getCenterX());
        moveTo.setY(circle.getCenterY());
        lineTo.setX(p1_x + circle_r);
        lineTo.setY(p1_y + circle_r);
        path.getElements().addAll(moveTo,lineTo);
        PathTransition pathTransition = new PathTransition(Duration.millis((int)przesyl.getValue()),path,circle);

        //wyslanie circle do pracownika odbieracjacego zamuwienia
        pathTransition.setOnFinished(e->{
            synchronized (this){
                notify();
            }
        });

        Platform.runLater(()->{
            pathTransition.play();
        });

        synchronized (this){
            try{
                wait();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        //usuniecie niepotrzebnych funckji
        Platform.runLater(()->{
            animacje.getChildren().remove(pathTransition);
        });
        p1 = circle;
        kolor = adresy[ktory];
        System.out.println("wyslalem - "+wysylka);
        Swysylka_p.release();
    }

    //odbior przez pracownika przyjmujacego danego adresu i wygenerowanie id sprzetu do naprawy
    void wyslanie_p(Slider podpis,String nazwa){
        int id;
        try {
            Swysylka_p.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //odebranie urzadzenia do naprawy
        System.out.println(nazwa + " otrzymalem " + wysylka);
        try {
            Thread.sleep((int)podpis.getValue());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        id = random.nextInt(99);
        wysylka = id + " " + wysylka;
        //wpisanie adresu (koloru) danego klienta
        p1.setFill(kolor);
    }

    //Polozenie na pulke danego produktu do naprawy prezez pracownika przyjmujacego
    public void pracownik_get(String id,double p1_x, double p1_y, double p1_r, AnchorPane animacje,Slider przesyl){
        double roz_wys = 100;
        try {
            wolne.acquire();
            chron_j.acquire();
            buf[j] = wysylka;
        }catch(InterruptedException e) {
            e.printStackTrace();
        }

        //obliczenie polozenia danego miejsca na pulce
        double buf_x_c = buf_x + j * 2 * p1_r + p1_r;
        double buf_y_c = p1_y + roz_wys + p1_r;
        //zadeklarowanie parametrow funckji odkladajacej na pulke
        Path path = new Path();
        MoveTo moveTo = new MoveTo();
        LineTo lineTo = new LineTo();
        moveTo.setX(p1_x + p1_r);
        moveTo.setY(p1_y + p1_r);
        lineTo.setX(buf_x_c);
        lineTo.setY(buf_y_c);
        path.getElements().addAll(moveTo,lineTo);
        PathTransition pathTransition = new PathTransition(Duration.millis((int)przesyl.getValue()),path,p1);

        pathTransition.setOnFinished(e->{
            synchronized (this){
                notify();
            }
        });

        Platform.runLater(()->{
            pathTransition.play();
        });

        synchronized (this){
            try{
                wait();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }

        Platform.runLater(()->{
            animacje.getChildren().remove(pathTransition);
        });

        buf_circle[j] = p1;
        zajete.release();
        j = (j+1) % R;
        System.out.println(id + " odlozylem na polke - "+wysylka);
        chron_j.release();
        Swysylka_k.release();
    }
    //Po wykonaniu naprawy zdjecie z pulki danego przedmiotu
    public Circle pracownik_do(String id,int nr,double p_x,double p_y,double p_r,AnchorPane animacje,Slider przesyl){
        String dana = null;
        double roz_wys = 100;
        double circle_x;
        double circle_y;
        Circle p;
        try {
            zajete.acquire();
            chron_k.acquire();
            dana = buf[k];
        }catch(InterruptedException e) {
            e.printStackTrace();
        }
        //okreslenie nastenego wolnego miejsca na pulce
        circle_x = buf_x + k*2*p_r + p_r;
        circle_y = p_y - roz_wys + p_r;
        p = buf_circle[k];

        //zadeklarowanie parametrow funkcji zdjemujacej z pulki
        Path path = new Path();
        MoveTo moveTo = new MoveTo();
        LineTo lineTo = new LineTo();
        moveTo.setX(circle_x);
        moveTo.setY(circle_y);
        lineTo.setX(p_x + p_r);
        lineTo.setY(p_y + p_r);
        path.getElements().addAll(moveTo,lineTo);
        PathTransition pathTransition = new PathTransition(Duration.millis((int)przesyl.getValue()),path,p);

        pathTransition.setOnFinished(e->{
            synchronized (this){
                notify();
            }
        });

        Platform.runLater(()->{
            pathTransition.play();
        });

        synchronized (this){
            try{
                wait();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }

        Platform.runLater(()->{
            animacje.getChildren().remove(pathTransition);
        });;

        wolne.release();
        k = (k+1) % R;
        System.out.println(id+" zdiolem z polki "+dana);

        do_dana[nr] = dana;
        chron_k.release();
        return p;
    }

    public void odsylka(int nr,String id){

        try {
            wysylka_p[nr].acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(id+" odeslalem "+do_dana[nr]);
        wysylka_k[nr].release();
    }
}
