package com.boyamihungry.processing;

import com.boyamihungry.processing.flocking.HomingBoid;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.video.Capture;

public class Faces extends PApplet {

    public static final int BOID_SPACING = 10;
    public static final int BOID_SIZE = 3;
    Capture cam;
    PImage camFrame = new PImage(width,height);


    boolean currentlyBooming = false;
    boolean startBoom = false;
    int boomFrame = 0;

   // Map<Integer,Integer> boidToPixelMap = new HashMap<>();

    HomingBoid[] boids;


    public static final int DELTA=19;

    public void setup() {

        this.frameRate(30f);

        PApplet app = (PApplet)this;

        boids = new HomingBoid[(width * height) / (BOID_SPACING * BOID_SPACING)];

        resetBoids();

        cam = new Capture(this,width,height);
        cam.start();
    }

    void resetBoids() {
        int boidId;
        int index = 0;
        for (int y = BOID_SPACING/2; y < height; y += BOID_SPACING) {
            for (int x = BOID_SPACING/2; x < width; x += BOID_SPACING) {
                boidId = y*width + x;
                //index++;
                //System.out.println("x=" + x + " y=" + y + " " + index );

                boids[index++] = new HomingBoid(
                        boidId,
                        new PVector(x,y),
                        this,
                        (id,applet) -> {
                            applet.pushStyle();
                            applet.fill(getRepresentativeColor(id, BOID_SPACING));
                            applet.ellipse(id % width, id % height, BOID_SIZE, BOID_SIZE);
                        }
                    );
            }
        }

        //System.out.println(index);

    }


    public void draw() {

        if ( startBoom ) {
            image(camFrame, 0, 0);
            startBoom = false;
            boomFrame = 0;
            currentlyBooming = true;

            // init boids
            resetBoids();
        }

        if (!currentlyBooming) {
            image(camFrame, 0, 0);
        } else {


            for ( HomingBoid boid : boids ) {
                if ( null != boid ) {
                    boid.render(this);
                }
            }

            boomFrame++;

        }
    }

//        if ( camFrame.isLoaded() ) {
//            camFrame.loadPixels();
//        }

//            if ( cam.available() ) {
//                cam.read();
//
//                cam.
//
//
//
//                cam.loadPixels();
//                image(cam,0,0);
//    }

    public void settings() {
        size(1024,768);
    }



    public void captureEvent(Capture c){
        c.read();
        camFrame = c.get(0,0,width,height);

//        for (int i = DELTA; i < c.pixels.length - DELTA; i++) {
//            c.pixels[i - ( (int)random(DELTA))] = c.pixels[i];
//        }
//
//        camFrame = c.get(0,0,width,height);

//        for ( int i=0; i<c.width; i++) {
//            for (int j = 0; j < c.height; j++) {
//            }
//        }


    }


    int getRepresentativeColor(int x, int y, int radius) {

        return 128;
    }

    int getRepresentativeColor(int pixelId, int radius) {

            return 128;
        }


    @Override
    public void keyTyped(KeyEvent event) {
        super.keyTyped(event);
        System.out.println("Boom");
        startBoom = true;
    }

    static public void main(String[] passedArgs) {
        String[] appletArgs = new String[]{"--window-color=#666666", "--stop-color=#cccccc", "com.boyamihungry.processing.Faces"};
        if (passedArgs != null) {
            PApplet.main(concat(appletArgs, passedArgs));
        } else {
            PApplet.main(appletArgs);
        }
    }
}