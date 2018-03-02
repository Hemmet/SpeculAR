package com.mate.specular.util;

import com.mate.specular.model.Circle;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;

/**
 * Created by mert on 02.03.18.
 */

public class PointProcess {

    public static Mat fındHomography(Circle[] referenceArray, Circle[] currentArray){
        if(referenceArray.length != 4 || currentArray.length !=4) return null; //has to be 4 tuple points

        Mat homog = new Mat();

        Point ref1 = new Point(referenceArray[0].getX_coord(), referenceArray[0].getY_coord());
        Point ref2 = new Point(referenceArray[1].getX_coord(), referenceArray[1].getY_coord());
        Point ref3 = new Point(referenceArray[2].getX_coord(), referenceArray[2].getY_coord());
        Point ref4 = new Point(referenceArray[3].getX_coord(), referenceArray[3].getY_coord());

        Point cur1 = new Point(currentArray[0].getX_coord(), currentArray[0].getY_coord());
        Point cur2 = new Point(currentArray[1].getX_coord(), currentArray[1].getY_coord());
        Point cur3 = new Point(currentArray[2].getX_coord(), currentArray[2].getY_coord());
        Point cur4 = new Point(currentArray[3].getX_coord(), currentArray[3].getY_coord());

        MatOfPoint2f ref = new MatOfPoint2f(ref1,ref2,ref3,ref4);
        MatOfPoint2f cur = new MatOfPoint2f(cur1,cur2,cur3,cur4);
        homog = Calib3d.findHomography(ref,cur,Calib3d.RANSAC, 10);

        return homog;
    }

    public static MatOfPoint2f applyHomography(MatOfPoint2f ref, Mat homog){
       Mat src = new Mat();
       Mat dst = new Mat();
       src.push_back(ref);
       Core.perspectiveTransform(src,dst,homog);

       MatOfPoint2f ret = new MatOfPoint2f(dst);
       return ret;
    }


}
