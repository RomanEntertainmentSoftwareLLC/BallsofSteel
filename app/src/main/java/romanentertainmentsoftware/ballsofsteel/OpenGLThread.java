package romanentertainmentsoftware.ballsofsteel;

import android.content.Context;
import android.util.Log;

import math3D.Plane3D;
import math3D.Vector3D;
import math3D.Vertex3D;
import object3D.Object3D;
import primitive3D.Ellipsoid3D;
import primitive3D.Particle3D;

/**
 * Created by Roman Entertainment Software LLC on 6/16/2018.
 */
public class OpenGLThread implements Runnable{
    private final String TAG = "THREAD";
    Context context;
    public long threadID;
    public boolean running = true;
    public boolean loaded = false;

    OpenGLThread(Context context){
        this.context = context;
    }

    @Override
    public void run() {

        while(running) {
            if (loaded == false){
                //Log.d("OPENGLTHREAD", String.valueOf(context));
                Render.filmCamera = null;
                Render.filmCamera = new Object3D(context, "Object3D: Film Camera", Render.programTexturedDirectionalSpecularLit, R.raw.filmcamera, new Vertex3D(0f, 0f, 0f), new Vector3D(0f, 0f, 0f), new Vertex3D(0f, 0f, 20f), new Vector3D(0f, 0f, 0f), new Vector3D(0.5f, 0.5f, 0.5f), 1f, 1f, 1f, 1f, 1f, false);
                Render.filmCamera.loadFile();
                //Log.d("Load Screen", "Film Camera Loaded");

                Render.mario = null;
                Render.mario = new Object3D(context, "Object3D: Mario", Render.programTexturedDirectionalSpecularLit, R.raw.teapot, new Vertex3D(0f, 0f, 0f), new Vector3D(0f, 0f, 0f), new Vertex3D(0f, 0f, -20f), new Vector3D(0f, 0f, 0f), new Vector3D(2f, 2f, 2f), 0.75f, 0.75f, 0.75f, 1f, 1f, false);
                Render.mario.loadFile();
                //Log.d("Load Screen", "Mario Loaded");

                Render.cube = null;
                Render.cube = new Object3D(context, "Object3D: Cube Room", Render.programTexturedDirectionalSpecularLit, R.raw.cube, new Vertex3D(0f, 0f, 0f), new Vector3D(0f, 0f, 0f), new Vertex3D(0f, 0f, 0f), new Vector3D(0f, 0f, 0f), new Vector3D(150f, 150f, 150f), 1f, 1f, 1f, 1f, 1f, false);
                Render.cube.loadFile();

                Render.vectorSpaceSide[0] = new Plane3D();
                Render.vectorSpaceSide[1] = new Plane3D();
                Render.vectorSpaceSide[2] = new Plane3D();
                Render.vectorSpaceSide[3] = new Plane3D();
                Render.vectorSpaceSide[4] = new Plane3D();
                Render.vectorSpaceSide[5] = new Plane3D();

                Render.cubeReflection = null;
                Render.cubeReflection = new Object3D(context, "Object3D: Cube Reflection", Render.programTexturedDirectionalSpecularLit, R.raw.cube, new Vertex3D(0f, 0f, 0f), new Vector3D(0f, 0f, 0f), new Vertex3D(0f, 0f, 0f), new Vector3D(0f, 0f, 0f), new Vector3D(2f, 2f, 2f), 1f, 1f, 1f, 1f, 1f, false);
                Render.cubeReflection.loadFile();

                Render.simpleCube = null;
                Render.simpleCube = new Object3D(context, "Object3D: Simple Cube", Render.programSimple, R.raw.cube, new Vertex3D(0f, 0f, 0f), new Vector3D(0f, 0f, 0f), new Vertex3D(0f, 0f, 0f), new Vector3D(0f, 0f, 0f), new Vector3D(2f, 2f, 2f), 1f, 1f, 1f, 1f, 1f, false);
                Render.simpleCube.loadFile();

                //Log.d("Load Screen", "Cube Loaded");

                //Render.wormhole = null;
                //Render.wormhole = new Object3D(context, Render.programTexturedDirectionalSpecularLit, R.raw.wormhole, new Vertex3D(0f, 0f, 0f), new Vector3D(0f, 0f, 0f), 1f, 1f, 1f, 1f, 8f, false);
                //Render.wormhole.loadFile();
                ////Log.d("Load Screen", "Wormhole Loaded");

                Render.ship[0] = null;
                Render.ship[0] = new Object3D(context, "Object3D: Ship 0", Render.programTexturedDirectionalSpecularLit, R.raw.ship, new Vertex3D(0f, 0f, 0f), new Vector3D(0f, 0f, 0f), new Vertex3D(0f, 0f, -30f), new Vector3D(0f, 0f, 0f), new Vector3D(1f, 1f, 1f), 1f, 1f, 1f, 1f, 1f, false);
                Render.ship[0].loadFile();
                //Log.d("Load Screen", "Ship 0 Loaded");

                Render.ship[1] = null;
                Render.ship[1] = new Object3D(context, "Object3D: Ship 1", Render.programTexturedDirectionalSpecularLit, R.raw.shipsides, new Vertex3D(0f, 0f, 0f), new Vector3D(0f, 0f, 0f), new Vertex3D(Render.ship[0].world_position.x, Render.ship[0].world_position.y, Render.ship[0].world_position.z), new Vector3D(0f, 0f, 0f), new Vector3D(1f, 1f, 1f), 1f, 1f, 1f, 1f, 1f, false);
                Render.ship[1].loadFile();
                //Log.d("Load Screen", "Ship 1 Loaded");

                Render.ball = null;
                Render.ball = new Object3D(context, "Object3D: Ball", Render.programTexturedDirectionalSpecularLit, R.raw.ball, new Vertex3D(0f, 0f, 0f), new Vector3D(0f, 0f, 0f), new Vertex3D(0f, 0f, 0f), new Vector3D(0f, 0f, 0f), new Vector3D(25f, 25f, 25f), 1f, 1f, 1f, 1f, 1f, false);
                Render.ball.loadFile();
                ////Log.d("Load Screen", "Ball Loaded");

                Render.ellipsoid = new Ellipsoid3D();

                Object3D tempBrick = new Object3D(context, "Object3D: brick", Render.programTexturedDirectionalSpecularLit, R.raw.cube, new Vertex3D(0f, 0f, 0f), new Vector3D(0f, 0f, 0f), new Vertex3D(0f, 0f, 0f), new Vector3D(0f, 0f, 0f), new Vector3D(4f, 2f, 2f), 1f, 1f, 1f, 1f, 1f, false);
                tempBrick.loadFile();

                for (int z = 0; z < 1; z++) {
                    for (int y = 0; y < 10; y++) {
                        for (int x = 0; x < 10; x++) {
                            Render.brick[x][y][z] = null;
                            Render.brick[x][y][z] = tempBrick;
                        }
                    }
                }

                Log.d("Testing", "Did I make it here? o.O");

                //Render.ball2 = null;
                //Render.ball2 = new Object3D(context, Render.programTexturedDirectionalSpecularLit, Render.programDepth, R.raw.ball, new Vertex3D(-10f, 0f, -30f), new Vector3D(0f, 0f, 0f), 1f, 1f, 1f, 1f, 1f, false);
                //Render.ball2.loadFile();
                ////Log.d("Load Screen", "Ball2 Loaded");

                Render.particle = null;
                Render.particle = new Particle3D(context, Render.programParticle,
                        0f, 0f, 0f, 1f,
                        1f, 0f, 0f, 1f);

                Render.particlePointLight = null;
                Render.particlePointLight = new Particle3D(context, Render.programParticle,
                        0f, 0f, 0f, 1f,
                        1f, 1f, 1f, 1f);

                Render.collidablePointParticle[0] = null;
                Render.collidablePointParticle[0] = new Particle3D(context, Render.programParticle,
                        0f, 0f, 0f, 1f,
                        0f, 1f, 0f, 1f);

                Render.collidablePointParticle[1] = null;
                Render.collidablePointParticle[1] = new Particle3D(context, Render.programParticle,
                        0f, 0f, 0f, 1f,
                        0f, 1f, 0f, 1f);

                Render.collidablePointParticle[2] = null;
                Render.collidablePointParticle[2] = new Particle3D(context, Render.programParticle,
                        0f, 0f, 0f, 1f,
                        0f, 1f, 0f, 1f);

                Render.collidablePointParticle[3] = null;
                Render.collidablePointParticle[3] = new Particle3D(context, Render.programParticle,
                        0f, 0f, 0f, 1f,
                        0f, 1f, 0f, 1f);

                Render.collidablePointParticle[4] = null;
                Render.collidablePointParticle[4] = new Particle3D(context, Render.programParticle,
                        0f, 0f, 0f, 1f,
                        0f, 1f, 0f, 1f);

                Render.collidablePointParticle[5] = null;
                Render.collidablePointParticle[5] = new Particle3D(context, Render.programParticle,
                        0f, 0f, 0f, 1f,
                        0f, 1f, 0f, 1f);


                Render.particleCam[0] = null;
                Render.particleCam[0] = new Particle3D(context, Render.programParticle,
                        0f, 0f, 0f, 1f,
                        1f, 1f, 1f, 1f);

                Render.particleCam[1] = null;
                Render.particleCam[1] = new Particle3D(context, Render.programParticle,
                        0f, 0f, 0f, 1f,
                        1f, 1f, 1f, 1f);

                Render.particleCam[2] = null;
                Render.particleCam[2] = new Particle3D(context, Render.programParticle,
                        0f, 0f, 0f, 1f,
                        1f, 1f, 1f, 1f);

                Render.particleCam[3] = null;
                Render.particleCam[3] = new Particle3D(context, Render.programParticle,
                        0f, 0f, 0f, 1f,
                        1f, 1f, 1f, 1f);

                Render.particleCam[4] = null;
                Render.particleCam[4] = new Particle3D(context, Render.programParticle,
                        0f, 0f, 0f, 1f,
                        1f, 1f, 1f, 1f);

                Render.particleCam[5] = null;
                Render.particleCam[5] = new Particle3D(context, Render.programParticle,
                        0f, 0f, 0f, 1f,
                        1f, 1f, 1f, 1f);


                Render.particleCam[6] = null;
                Render.particleCam[6] = new Particle3D(context, Render.programParticle,
                        0f, 0f, 0f, 1f,
                        1f, 1f, 1f, 1f);

                Render.particleCam[7] = null;
                Render.particleCam[7] = new Particle3D(context, Render.programParticle,
                        0f, 0f, 0f, 1f,
                        1f, 1f, 1f, 1f);

                Render.particleCam[8] = null;
                Render.particleCam[8] = new Particle3D(context, Render.programParticle,
                        0f, 0f, 0f, 1f,
                        1f, 1f, 1f, 1f);

                Render.particleCam[9] = null;
                Render.particleCam[9] = new Particle3D(context, Render.programParticle,
                        0f, 0f, 0f, 1f,
                        1f, 1f, 1f, 1f);

                loaded = true;
                //Log.d("Load Screen", "Loaded = true");
            }


            // Slow down the thread a hair to keep it from working too hard
            try {
                Thread.sleep(100);
            } catch (InterruptedException ie) {

            }
        }

        // Exit when running = false;
        return;
    }
}
