package com.example.projekt_10_finalny;

import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.*;
import javafx.util.Duration;

public class Kurier extends Thread{

    Pulka sem;
    Line line1;
    Line line2;
    Line line3;
    Line line4;
    double start_y;
    double end_y;
    int N;
    int nr;
    AnchorPane animacje;
    Slider odsylka;
    double circle_x;
    double circle_r;

    public Kurier(int N, int nr, Line line1, Line line2, Line line3, Line line4, double start_y, double end_y, AnchorPane animacje, Pulka sem, double circle_x, double circle_r, Slider odsylka){
        super("L-"+nr);
        this.sem = sem;
        this.N = N;
        this.nr = nr;
        this.animacje = animacje;
        this.line1 = line1;
        this.line2 = line2;
        this.line3 = line3;
        this.line4 = line4;
        this.start_y = start_y;
        this.end_y = end_y;
        this.circle_x = circle_x;
        this.circle_r = circle_r;
        this.odsylka=odsylka;

    }

    @Override
    public void run(){
        double end_x = 0;
        int ktory = 0;
        for(int i = 0; i < N; i++){

            try {
                Pulka.wysylka_k[nr].acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //odebranie przez kuriera naprawionego urzadzenia
            Circle p = Pulka.do_circle[nr];
            String dana = Pulka.do_dana[nr];
            System.out.println(getName()+" przyjolem "+dana);
            //wyszukanie adresu klienta
            for(int j=0;j<6;j++){
                if(Pulka.adresy[j] == p.getFill()){
                    ktory = j;
                    end_x = circle_x + circle_r * j * 2;
                }
            }
            //zdefiniowanie funckji transporujacej naprawione urzadzenie
            Path path = new Path();
            MoveTo moveTo = new MoveTo();
            LineTo lineTo1 = new LineTo();
            LineTo lineTo2 = new LineTo();
            LineTo lineTo3 = new LineTo();
            LineTo lineTo4 = new LineTo();
            LineTo lineTo5 = new LineTo();
            moveTo.setX(line1.getStartX());
            moveTo.setY(start_y);
            lineTo1.setX(line1.getEndX());
            lineTo1.setY(line1.getEndY());
            lineTo2.setX(line2.getEndX());
            lineTo2.setY(line2.getEndY());
            lineTo3.setX(line3.getEndX());
            lineTo3.setY(line3.getEndY());
            lineTo4.setX(end_x);
            lineTo4.setY(line4.getEndY());
            lineTo5.setX(end_x);
            lineTo5.setY(line4.getEndY()+25);
            path.getElements().addAll(moveTo,lineTo1,lineTo2,lineTo3,lineTo4,lineTo5);
            PathTransition pathTransition = new PathTransition(Duration.millis((int)odsylka.getValue()),path,p);

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
                animacje.getChildren().remove(p);
            });

            System.out.println(getName()+" dostarczylem "+dana);
            Pulka.skrzynka[ktory].release();
            Pulka.wysylka_p[nr].release();
        }
    }
}
