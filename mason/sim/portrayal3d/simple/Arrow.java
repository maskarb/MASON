/*
  Copyright 2006 by Sean Luke and George Mason University
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package sim.portrayal3d.simple;

import com.sun.j3d.utils.geometry.*;

import java.awt.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import sim.util.*;

import sim.portrayal3d.SimplePortrayal3D;

/**
 * 
 * @author Gabriel Balan
 * 
 * This file was initially taken from http://www.j3d.org/downloads/Arrow.java
 * 
 * and heavily modified
 */
public class Arrow extends TransformGroup
    {
    static final Color DEFAULT_ARROW_COLOR = Color.gray;
    static final Font3D DEFAULT_FONT3D = new Font3D(new Font(null, Font.PLAIN, 1), null);

    Cone arrowHead;
    Cylinder arrowTail;
    public Cylinder getArrowTail() { return arrowTail; }
    public Cone getArrowHead() { return arrowHead; }

    /**
     * Creates a 3D arrow between points <code>startPoint</code> and
     * <code>endPoint</code> if either label is not null, it adds a Text2D object
     * at the appropriate end.
     */
    public Arrow(double arrowTailRadius, Double3D startPoint, Double3D endPoint, String stLabel,
        String endLabel, Appearance appearance)
        {
        Vector3d stPt = new Vector3d(startPoint.x, startPoint.y, startPoint.z);
        Vector3d endPt = new Vector3d(endPoint.x, endPoint.y, endPoint.z);
        Vector3d v = new Vector3d(stPt);
        v.negate();
        v.add(new Vector3d(endPt));
        // v= start -> end

        float arrowLen = (float) v.length();
        float arrowHeadLen = 5.0f * (float)arrowTailRadius;
        float arrowHeadMaxRadius = 3.0f * (float)arrowTailRadius;
        float cylinderLen = arrowLen - arrowHeadLen;
        
        if(cylinderLen<0)
            {
            //this is a short arrow, 
            //I need a different formula
            arrowHeadLen = arrowLen/16;
            cylinderLen = arrowLen - arrowHeadLen;
            }

        // Apperance for the arrow
        Appearance caAppearance = appearance; 
        if(caAppearance==null)
            {
            caAppearance = SimplePortrayal3D.appearanceForColors(DEFAULT_ARROW_COLOR, null, DEFAULT_ARROW_COLOR, DEFAULT_ARROW_COLOR, 1.0f, 1.0f);
            }

        // Rotation Matrix for whole arrow (cylinder + cone)
        Transform3D caTransform = new Transform3D();
        caTransform.setTranslation(stPt);

        Vector3d oy = new Vector3d(0, 1, 0);

        Vector3d axis = new Vector3d();
        axis.cross(oy, v);
        //if v lies ofn Oy, then axis =[0,0,0]. No rotation is needed
        if(axis.length()!=0)
            {
            //TODO I should use rodrigues formula here
            caTransform.setRotation(new AxisAngle4d(axis, Math.asin(axis.length()
                        / v.length())));
            // axis.length() must be v.length() both doubles or floats, otherwise
            // you might get something bigger than 1.
            }
        caTransform.setTranslation(stPt);

        this.setTransform(caTransform);

        this.arrowTail = new Cylinder((float)arrowTailRadius, cylinderLen, caAppearance);
        Transform3D arrowCylinderTransform = new Transform3D();
        arrowCylinderTransform.set(new Vector3f(0, cylinderLen / 2, 0));
        TransformGroup arrowCylinderTransformGroup = new TransformGroup(
            arrowCylinderTransform);
        arrowCylinderTransformGroup.addChild(this.arrowTail);
        addChild(arrowCylinderTransformGroup);

        Transform3D arrowHeadTransform = new Transform3D();
        arrowHeadTransform.set(new Vector3f(0, cylinderLen, 0));
        TransformGroup arrowHeadTransformGroup = new TransformGroup(
            arrowHeadTransform);
        this.arrowHead = new Cone(arrowHeadMaxRadius, arrowHeadLen, 1, caAppearance);
        arrowHeadTransformGroup.addChild(this.arrowHead);
        this.addChild(arrowHeadTransformGroup);

        if (stLabel != null)
            {
            Text3D txt = new Text3D(DEFAULT_FONT3D, stLabel);
            OrientedShape3D os3d = new OrientedShape3D(txt, caAppearance,
                OrientedShape3D.ROTATE_ABOUT_POINT, new Point3f(0, 0, 0));

            Transform3D t = new Transform3D();
            t.setScale(5 * (float)arrowTailRadius);
            t.setTranslation(new Vector3f(0, -.1f, 0));
            TransformGroup stLabelTG = new TransformGroup(t);

            stLabelTG.addChild(os3d);
            this.addChild(stLabelTG);
            }

        if (endLabel != null)
            {
            Text3D txt = new Text3D(DEFAULT_FONT3D, endLabel);
            OrientedShape3D os3d = new OrientedShape3D(txt, caAppearance,
                OrientedShape3D.ROTATE_ABOUT_POINT, new Point3f(0,
                    arrowLen, 0));

            Transform3D t = new Transform3D();
            t.setScale(5 * (float)arrowTailRadius);
            t.setTranslation(new Vector3f(0, arrowLen + .1f, 0));
            TransformGroup endLabelTG = new TransformGroup(t);

            endLabelTG.addChild(os3d);
            this.addChild(endLabelTG);
            }
        }
    }
