package com.boyamihungry.processing.flocking;

/**
 * Created by patwheaton on 4/17/16.
 */
public class BoidController {

//    ArrayList mBoids;
//    int mMaxNumBoids;
//    PApplet parent;
//
//    //constructor
//    BoidController(int maxNumBoids, PApplet parentApp) {
//        this.parent = parentApp;
//        mMaxNumBoids = maxNumBoids;
//        mBoids = new ArrayList();
//    }
//
//    //update
//    public void update() {
//        for (int i = 0; i < mBoids.size(); i++) {
//            HomingBoid currBoid = (HomingBoid) mBoids.get(i);
//            currBoid.update(mBoids); //need to pass other boids for separation. cohesion, and alignment calculations
//        }
//    }
//
//    public void render(PApplet parentApp) {
//        update();
//
//        for (int i = 0; i < mBoids.size(); i++) {
//            HomingBoid currBoid = (HomingBoid) mBoids.get(i);
//
//            if (parentApp.mousePressed) currBoid.seek(new PVector(parentApp.mouseX, parentApp.mouseY));
//
//            currBoid.render(parentApp);
//        }
//    }
//
//    public void addBoids(int numBoids, float xPos, float yPos) {
//        for (int i = 0; i < numBoids; i++) {
//            if (mBoids.size() < mMaxNumBoids) {
//                mBoids.add(new HomingBoid(new PVector(xPos, yPos), parent));
//            }
//        }
//    }
//
//    public void removeBoids(int numBoids) {
//        for (int i = 0; i < numBoids; i++) {
//            if (mBoids.size() != 0) mBoids.remove(i);
//        }
//    }
//
//    public void initSeeking(float xPos, float yPos) {
//        for (int i = 0; i < mBoids.size(); i++) {
//            HomingBoid currBoid = (HomingBoid) mBoids.get(i);
//            currBoid.seek(new PVector(parent.mouseX, parent.mouseY));
//        }
//    }
//
//    public int getMaxNumBoids() {
//        return mMaxNumBoids;
//    }
//
//    public int getNumBoids() {
//        return mBoids.size();
//    }
}
