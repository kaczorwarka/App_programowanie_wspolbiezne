package com.example.projekt_10_finalny;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.util.Duration;
import java.util.Random;
import static javafx.scene.paint.Color.BLACK;


public class Klient extends Thread{
    int N;
    private Pulka sem;
    int ktory;
    double szerokosc_klienta = 50;
    double pomniejszenie = 5;
    int powiekszenie = 3;

    //zmienne przechowujace pierwsze polozenie circle
    double circle_x;
    double circle_y;
    double circle_r;

    AnchorPane animajce;
    Slider zuzycie;
    Slider przesyl;

    //zmienne przechowujace polozenie pierwszego pracownika (odbierajacego zamuwienie)
    double p1_x;
    double p1_y;

    public Klient(int ktory, int N,double circle_x,double circle_y,double circle_r,double p1_x , double p1_y,AnchorPane animajce,Pulka sem,Slider zuzycie,Slider przesyl){
        super("K-"+ktory);
        this.ktory = ktory;
        this.sem = sem;
        this.N=N;
        this.circle_x = circle_x;
        this.circle_y = circle_y;
        this.circle_r = circle_r;
        this.p1_x = p1_x;
        this.p1_y = p1_y;
        this.animajce = animajce;
        this.zuzycie=zuzycie;
        this.przesyl=przesyl;
    }

    Random random = new Random();

    @Override
    public void run(){
        //wyliczenie polozena danego klienta
        circle_x += szerokosc_klienta * ktory;

        //zdefiniowanie reprezentacji klienta
        Rectangle rectangle = new Rectangle();
        rectangle.setX(circle_x-circle_r);
        rectangle.setY(circle_y-circle_r);
        rectangle.setHeight(circle_r * 2);
        rectangle.setWidth(circle_r * 2);

        Line line = new Line();
        line.setStartX(circle_x);
        line.setStartY(circle_y-circle_r);
        line.setEndX(circle_x);
        line.setEndY(10);

        //okreslenie adresu (koloru) klienta
        Paint adres = Pulka.adresy[ktory];
        rectangle.setFill(adres);
        rectangle.setStroke(BLACK);

        Platform.runLater(()->{
            animajce.getChildren().add(rectangle);
            animajce.getChildren().add(line);
        });

        for(int i=0; i<N; i++) {
            //sekcja lokalna

            //zdefiniowanie nowego circle (sprzetu) i okreslenie jego parametrow
            Circle circle = new Circle();
            circle.setRadius(circle_r/pomniejszenie);
            circle.setCenterX(circle_x);
            circle.setCenterY(circle_y);

            //zdefiniowanie funkcji powiekszajacej circle (zuzycie sprzetu)
            ScaleTransition scaleTransition = new ScaleTransition();
            scaleTransition.setDuration(Duration.millis(random.nextInt((int)zuzycie.getValue())+(int)zuzycie.getMin()));
            scaleTransition.setByX(powiekszenie);
            scaleTransition.setByY(powiekszenie);
            scaleTransition.setNode(circle);

            //zuzycie sprzetu
            scaleTransition.setOnFinished(e->{
                synchronized (this){
                    notify();
                }
            });

            Platform.runLater(()->{
                animajce.getChildren().add(circle);
                scaleTransition.play();
            });

            synchronized (this){
                try{
                    wait();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }

            Platform.runLater(()->{
                animajce.getChildren().remove(scaleTransition);
            });

            //sekcja krytyczna
            sem.wyslanie_k(getName(),ktory,circle,circle_r,p1_x,p1_y,animajce,przesyl);
        }

        //oczekiwanie na zwort wysztkich naprawionych urzadzen
        for(int i=0; i<N; i++){
            try {
                Pulka.skrzynka[ktory].acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //usuniecie reprezentacji klienta
        Platform.runLater(()->{
            animajce.getChildren().remove(rectangle);
            animajce.getChildren().remove(line);
        });
    }
}
