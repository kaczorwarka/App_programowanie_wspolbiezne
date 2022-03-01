package com.example.projekt_10_finalny;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import java.util.Random;


public class Pracownik extends Thread {
    int nr;
    int N;
    private final Pulka sem;
    char tryb;
    AnchorPane animacje;
    Slider naprawa,przesyl,podpis;
    double p_x;
    double p_y;
    double p_r;

    public Pracownik(int nr, int N, Pulka sem, char tryb, double p_x, double p_y, double p_r, AnchorPane animacje, Slider naprawa,Slider przesyl,Slider podpis){
        super("P-"+nr);
        this.nr = nr;
        this.sem = sem;
        this.N=N;
        this.tryb=tryb;
        this.animacje = animacje;
        this.p_x = p_x;
        this.p_y = p_y;
        this.p_r = p_r;
        this.przesyl=przesyl;
        this.naprawa=naprawa;
        this.podpis=podpis;
    }



    Random random = new Random();
    @Override
    public void run(){
        //g - pracownik odbierajacy zamowienia, d - pracownik naprawiajacy
        if (tryb=='g') {
            for (int i = 0; i < N; i++) {
                //sekcja krytyczna
                sem.wyslanie_p(podpis,getName());

                //sekcja krytyczna
                sem.pracownik_get(getName(),p_x, p_y, p_r, animacje,przesyl);
            }
        }
        else if(tryb=='d') {
            nr -= 1;
            int pomniejszenie = -3;
            for (int i = 0; i < N; i++){

                //sekcja krytyczna
                Circle p = sem.pracownik_do(getName(),nr,p_x,p_y,p_r,animacje,przesyl);
                //sekcja lokalna

                //zdefiniowanie parametrow funkcji naprawiajacej (pomniejszajacej) rzadzenie
                ScaleTransition scaleTransition = new ScaleTransition();
                scaleTransition.setByY(pomniejszenie);
                scaleTransition.setByX(pomniejszenie);
                scaleTransition.setDuration(Duration.millis(random.nextInt((int)naprawa.getValue())+(int)naprawa.getMin()));
                scaleTransition.setNode(p);

                scaleTransition.setOnFinished(e->{
                    synchronized (this){
                        notify();
                    }
                });

                Platform.runLater(()->{
                    scaleTransition.play();
                });

                synchronized (this){
                    try{
                        wait();
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
                System.out.println(getName() + " naprawilem " + Pulka.do_dana[nr]);

                Platform.runLater(()->{
                    animacje.getChildren().remove(scaleTransition);
                });
                //przekazanie naprawionego sprzetu do odbioru przez kuriera
                Pulka.do_circle[nr] = p;
                sem.odsylka(nr,getName());
            }
        }
    }
}
