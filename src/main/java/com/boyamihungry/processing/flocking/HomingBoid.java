package com.boyamihungry.processing.flocking;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

/**
 * Created by patwheaton on 4/17/16.
 */
public class HomingBoid {
    // location, acceleration, and velocity vectors
    PVector currentLocation;
    PVector originalLocation;
    PVector acc;
    PVector vel;

    int boidId;

    float maxspeed;               // maximum speed our boid can move per frame
    float maxforce;               // maximum steering force
    float wandertheta = 0.0f;     // wandering angle
    float desiredSeparation;      // we want to be 25 pixels apart from our neighbors
    float neighbourRange;         // anything within 50px is a neighbor
    float separationScale;

    int col;                    // boid colour
    float radius;                 // radius size of boid

    PApplet parent;
    BoidDraw drawer;

    // JPW todo: decouple this from PApplet, at least the color stuff
    public HomingBoid(int boidId, PVector location, PApplet parentApp, BoidDraw drawer)
    {
        this.parent = parentApp;
        this.boidId = boidId;
        this.currentLocation = location.copy();
        this.originalLocation = location.copy();
        this.drawer = drawer;
        this.acc = new PVector( 0, 0 );
        this.vel = new PVector( parent.random(-2, 2), parent.random(-2, 2) );

        maxspeed = 5.0f;
        maxforce = 0.75f;
        radius = 4;
        desiredSeparation = 25.0f;
        neighbourRange = 50.0f;
        separationScale = 2.0f;

        // random color using HSB (Hue, Saturation, Brightness)
        // HSB mimics a color wheel. Hue is 360 degrees (red is 0 degrees)
        parent.colorMode( parent.HSB, 360, 100, 100 );
        // pick a random red color (0 degrees) at 100% saturation, but with a random brightness of 40-100%
        col = parent.color( 0, 100, (int) parent.random( 40, 100 ) );
    }

    public void update( ArrayList boids )
    {
        // get the vectors for separation, cohesion, and alignment
        PVector sep = separation( boids );
        PVector coh = cohesion( boids );
        PVector align = alignment( boids );

        sep.mult( separationScale );   // scale up separation to make it more profound

        // apply them to our acceleration
        acc.add( align ).add( coh ).add( sep );

        vel.add( acc );                // add our acceleration vector to our velocity
        vel.limit( maxspeed );         // make sure our velocity is capped at maxspeed
        currentLocation.add( vel );                // add to our location to move us
        acc.mult( 0 );                 // zero out acceleration in preparation for next update

        borders();
    }

    public PVector separation( ArrayList boids )
    {
        // direction to steer in to keep away from other boids
        PVector steerVec = new PVector( 0, 0, 0 );

        // number of neighbours we're close to
        int count = 0;

        // loop through all the boids
        for ( int i=0; i<boids.size(); i++ )
        {
            // for each one, calculate our distance
            HomingBoid neighbor = (HomingBoid) boids.get(i);
            float d = PVector.dist(currentLocation, neighbor.currentLocation);

            // if we're more than 0px away and less than our desired separation (25px)
            if ( d > 0 && d < desiredSeparation )
            {
                // find vector between us and the neighbor
                // since we're subtracting neighbor position from currentLocation, the vector will point AWAY from the neighbor boid
                PVector diff = PVector.sub(currentLocation, neighbor.currentLocation);

                // normalize it so that it's a unit vector of 1
                diff.normalize();
                // divide it by the distance (will be larger the closer you are to the neighbour, smaller the further you are)
                // reason for this is that we need to steer sharply away if we're close to an object, but can steer gradually around it
                // if we're far away
                diff.div(d);
                // add it to our steering vector
                steerVec.add(diff);

                // increase the count so we can keep track of how many neighbors we're moving away from
                ++count;
            }
        }

        if ( count > 0 )
        {
            // calculate the average direction we should move towards to move away from all of the neighbor boids
            steerVec.div( (float)count );
        }

        // if we have to steer (magnitude is greater than zero)
        if ( steerVec.mag() > 0 )
        {
            // make the vector the length of maxspeed by normalizing it (making it 1 unit), then multiplying it by maxspeed
            steerVec.normalize();
            steerVec.mult( maxspeed );

            // find vector in between velocity and steer (like we did for steer() function)
            steerVec.sub( vel );
            // limit it by the maximum amount we can steer
            steerVec.limit( maxforce );
        }

        return steerVec;
    }

    public PVector cohesion( ArrayList boids )
    {
        // Cohesion behaviour finds the average position between all neighbours
        // and steers in that direction

        // sum of the neighbours positions (which we'll average out)
        PVector sum = new PVector( 0, 0 );

        // number of neighbours
        int count = 0;

        for ( int i=0; i<boids.size(); i++ )
        {
            // loop through all boids
            HomingBoid neighbor = (HomingBoid) boids.get(i);

            // are we close to this boid?
            float d = PVector.dist(currentLocation, neighbor.currentLocation);

            if ( d > 0 && d < neighbourRange )
            {
                // if so, add the neighbour's position to our sum
                sum.add( neighbor.currentLocation);
                // increase the count
                ++count;
            }
        }

        // do we have neighbors?
        if ( count > 0 )
        {
            // average out the sum to calculate the average position of our neighbours
            sum.div( (float)count );

            // return the vecter that steers in that direction
            return steer( sum, false );
        }
        else
        {
            // otherwise return zero vector (0, 0)
            return new PVector( 0, 0 );
        }
    }

    public PVector alignment( ArrayList boids )
    {
        // similar to cohesion, Alignment calculates the average velocity of all neighbours
        // and steers towards it

        // sum of the neighbours velocities (which we'll average out)
        PVector sum = new PVector( 0, 0, 0 );

        // number of neighbours
        int count = 0;

        for ( int i=0; i<boids.size(); i++ )
        {
            // loop through all boids
            HomingBoid neighbor = (HomingBoid) boids.get(i);

            // are we close to this boid?
            float d = PVector.dist(currentLocation, neighbor.currentLocation);

            if ( (d > 0) && (d < neighbourRange) )
            {
                // if so, add the neighbour's position to our sum
                sum.add( neighbor.vel );
                // increase the count
                ++count;
            }
        }

        // do we have neighbors?
        if ( count > 0 )
        {
            // average out the sum to calculate the average velocity of our neighbours
            sum.div( (float)count );

            // return the vector that steers in that direction
            return sum;
        }
        else
        {
            return new PVector( 0, 0 );
        }
    }

    public void seek( PVector target )
    {
        // get the steering vector that will adjust us towards the target
        // false is passed in to not slowdown
        PVector steerVec = steer( target, false );

        // add steering vector
        acc.add( steerVec );
    }

    public void arrive( PVector target )
    {
        // get the steering vector that will adjust us towards the target
        // true is passed in to slowdown
        PVector steerVec = steer( target, true );

        // add steering vector
        acc.add( steerVec );
    }

    public void wander()
    {
        // the radius of our wander circle
        float wanderRadius = 16.0f;

        // distance of the wander circle from the boid
        float wanderDiameter = 60.0f;

        // how much we can change direction by each frame
        float change = 0.5f;

        // adjust our wandering angle
        wandertheta += parent.random( -change, change );

        // calculate the center of our wander circle based on our position, direction, and wanderDiameter
        PVector circleloc = vel.get();
        circleloc.normalize();
        circleloc.mult( wanderDiameter );
        circleloc.add(currentLocation);

        // calculate position on the wander circle based on the random angle we calculated (wandertheta)
        PVector circleOffset = new PVector( wanderRadius * parent.cos(wandertheta), wanderRadius * parent.sin(wandertheta) );

        // calculate the target by adding the circleOffset to the circleLocation
        PVector target = PVector.add( circleloc, circleOffset );

        // steer towards this position on the circle
        PVector steerVec = steer( target, false );
        acc.add( steerVec );
    }

    public PVector steer( PVector target, Boolean slowdown)
    {
        // vector that we'll add to our acceleration to steer towards target
        PVector steering;

        // calculate our desired velocity vector (would bring us to target)
        PVector desired = PVector.sub( target, currentLocation);

        // how far are we from target (magnitude of the desired vector)
        float d = desired.mag();

        // is our distance greater than zero?
        if ( d > 0 )
        {
            // normalize the desired vector to get a vector of length (magnitude) 1 that points in the proper direction
            desired.normalize();

            // if we're close to our target (within 100px) and we're to slowdown
            if ( slowdown && d < 100.0f )
            {
                // slow us down based on the distance
                desired.mult( maxspeed * (d/100.0f) );
            }
            else
            {
                // make the desired vector the length of our maxspeed
                desired.mult( maxspeed );
            }

            // calculate steering vector
            steering = PVector.sub( desired, vel );
            // limit it our maximum steering force so that we don't turn to face our target 100% all at once
            steering.limit( maxforce );
        }
        else
        {
            // distance is zero - we're at the target, no steering necessary
            steering = new PVector( 0, 0 );
        }

        // return the steering vector
        return steering;
    }


    public void borders()
    {
        if (currentLocation.x < -radius) currentLocation.x = parent.width+radius;
        if (currentLocation.y < -radius) currentLocation.y = parent.height+radius;
        if (currentLocation.x > parent.width+radius) currentLocation.x = -radius;
        if (currentLocation.y > parent.height+radius) currentLocation.y = -radius;
    }

    public void render(PApplet parentApp )
    {
        parentApp.pushMatrix();
        parentApp.fill( col );
        parentApp.noStroke();
        parentApp.translate( currentLocation.x, currentLocation.y );

        float theta = vel.heading2D() + parentApp.radians(90);
        parentApp.rotate(theta);

        drawer.drawBoid(this.boidId, parentApp);

        parentApp.popMatrix();
    }

    public interface BoidDraw {
        public void drawBoid(int boidId, PApplet applet);
    }

}
