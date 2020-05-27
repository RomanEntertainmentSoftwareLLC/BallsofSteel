package object3D;

/**
 * Created by Roman Entertainment Software LLC on 5/4/2018.
 */

import java.util.ArrayList;

public class Face3D {

    public ArrayList<Integer> indexNumberList = new ArrayList<Integer>();
    public ArrayList<Integer> textureCoordNumberList = new ArrayList<Integer>();
    public ArrayList<Integer> normalNumberList = new ArrayList<Integer>();

    public Face3D(){

    }

    public Face3D(int indexNumA, int textureCoordNumA, int normalNumA,
                  int indexNumB, int textureCoordNumB, int normalNumB,
                  int indexNumC, int textureCoordNumC, int normalNumC){
        indexNumberList.add(indexNumA);
        textureCoordNumberList.add(textureCoordNumA);
        normalNumberList.add(normalNumA);

        indexNumberList.add(indexNumB);
        textureCoordNumberList.add(textureCoordNumB);
        normalNumberList.add(normalNumB);

        indexNumberList.add(indexNumC);
        textureCoordNumberList.add(textureCoordNumC);
        normalNumberList.add(normalNumC);
    }
}
