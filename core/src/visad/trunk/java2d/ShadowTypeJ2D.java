
//
// ShadowTypeJ2D.java
//

/*
VisAD system for interactive analysis and visualization of numerical
data.  Copyright (C) 1996 - 1998 Bill Hibbard, Curtis Rueden, Tom
Rink and Dave Glowacki.
 
This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 1, or (at your option)
any later version.
 
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License in file NOTICE for more details.
 
You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
*/

package visad.java2d;
 
import visad.*;

import java.util.Vector;
import java.util.Enumeration;
import java.rmi.*;

/**
   The ShadowTypeJ2D hierarchy shadows the MathType hierarchy,
   within a DataDisplayLink, under Java2D.<P>
*/
public abstract class ShadowTypeJ2D extends ShadowType {

  /** basic information about this ShadowTypeJ2D */
  MathType Type; // MathType being shadowed
  transient DataDisplayLink Link;
  transient DisplayImplJ2D display;
  transient private Data data; // from Link.getData()
  private ShadowTypeJ2D Parent;

  ShadowType adaptedShadowType;

  public ShadowTypeJ2D(MathType type, DataDisplayLink link,
                       ShadowType parent)
         throws VisADException, RemoteException {
    super(type, link, getAdaptedParent(parent));
    Type = type;
    Link = link;
    display = (DisplayImplJ2D) link.getDisplay();
    Parent = (ShadowTypeJ2D) parent;
    data = link.getData();
  }

  public static ShadowType getAdaptedParent(ShadowType parent) {
    if (parent == null) return null;
    else return parent.getAdaptedShadowType();
  }

  public ShadowType getAdaptedShadowType() {
    return adaptedShadowType;
  }

  public ShadowRealType[] getComponents(ShadowType type, boolean doRef)
          throws VisADException {
    return adaptedShadowType.getComponents(type, doRef);
  }

  public Data getData() {
    return data;
  }

  public DisplayImpl getDisplay() {
    return display;
  }
 
  public MathType getType() {
    return Type;
  }

  public int getLevelOfDifficulty() {
    return adaptedShadowType.getLevelOfDifficulty();
  }

  public boolean getMultipleDisplayScalar() {
    return adaptedShadowType.getMultipleDisplayScalar();
  }

  public boolean getMappedDisplayScalar() {
    return adaptedShadowType.getMappedDisplayScalar();
  }

  public int[] getDisplayIndices() {
    return adaptedShadowType.getDisplayIndices();
  }

  public int[] getValueIndices() {
    return adaptedShadowType.getValueIndices();
  }

/*
  public int getDirectManifoldDimension() {
    return adaptedShadowType.getDirectManifoldDimension();
  }

  public boolean getIsDirectManipulation() {
    return adaptedShadowType.getIsDirectManipulation();
  }

  public String getWhyNotDirect() {
    return adaptedShadowType.getWhyNotDirect();
  }

  public int getAxisToComponent(int i) {
    return adaptedShadowType.getAxisToComponent(i);
  }

  public ScalarMap getDirectMap(int i) {
    return adaptedShadowType.getDirectMap(i);
  }

  public int getDomainAxis() {
    return adaptedShadowType.getDomainAxis();
  }
*/

  /** checkIndices: check for rendering difficulty, etc */
  public int checkIndices(int[] indices, int[] display_indices,
             int[] value_indices, boolean[] isTransform, int levelOfDifficulty)
      throws VisADException, RemoteException {
    return adaptedShadowType.checkIndices(indices, display_indices, value_indices,
                                          isTransform, levelOfDifficulty);
  }

  /** clear AccumulationVector */
  void preProcess() throws VisADException {
  }

  /** transform data into a VisADSceneGraphObject;
      add generated scene graph components as children of group;
      value_array are inherited valueArray values;
      default_values are defaults for each display.DisplayRealTypeVector;
      return true if need post-process;
      this is default (for ShadowTextType) */
  boolean doTransform(VisADGroup group, Data data, float[] value_array,
                      float[] default_values, DataRenderer renderer)
          throws VisADException, RemoteException {
    return false;
  }

  /** render accumulated Vector of value_array-s to
      and add to group; then clear AccumulationVector */
  void postProcess(VisADGroup group) throws VisADException {
  }


  /* helpers for doTransform */

  /** map values into display_values according to ScalarMap-s in reals */
  public  static void mapValues(float[][] display_values, double[][] values,
                               ShadowRealType[] reals) throws VisADException {
    ShadowType.mapValues(display_values, values, reals);
  }

  /** map values into display_values according to ScalarMap-s in reals */
  public static void mapValues(float[][] display_values, float[][] values,
                               ShadowRealType[] reals) throws VisADException {
    ShadowType.mapValues(display_values, values, reals);
  }

  public static VisADGeometryArray makePointGeometry(float[][] spatial_values,
                float[][] color_values) throws VisADException {
    return ShadowType.makePointGeometry(spatial_values, color_values);
  }

  /** construct an VisADAppearance object */
  static VisADAppearance makeAppearance(GraphicsModeControl mode,
                      float constant_alpha,
                      float[] constant_color,
                      VisADGeometryArray array) {
    VisADAppearance appearance = new VisADAppearance();

// LineWidth and PointSize not needed - in GraphicsModeControl

    appearance.alpha = constant_alpha; // may be Float.NaN
    if (constant_color != null && constant_color.length == 3) {
      appearance.color_flag = true;
      appearance.red = constant_color[0];
      appearance.green = constant_color[1];
      appearance.blue = constant_color[2];
    }
    appearance.array = array; // may be null
    return appearance;
  }

  /** collect and transform spatial DisplayRealType values from display_values;
      add spatial offset DisplayRealType values;
      adjust flow1_values and flow2_values for any coordinate transform;
      if needed, return a spatial Set from spatial_values, with the same topology
      as domain_set (or an appropriate Irregular topology);
      domain_set = null and allSpatial = false if not called from
      ShadowFunctionType */
  public static Set assembleSpatial(float[][] spatial_values,
                float[][] display_values, int valueArrayLength,
                int[] valueToScalar, DisplayImpl display,
                float[] default_values, int[] inherited_values,
                Set domain_set, boolean allSpatial, boolean set_for_shape,
                int[] spatialDimensions, float[][] range_select,
                float[][] flow1_values, float[][] flow2_values,
                float[] flowScale)
         throws VisADException, RemoteException {
    return ShadowType.assembleSpatial(spatial_values, display_values,
           valueArrayLength, valueToScalar, display, default_values,
           inherited_values, domain_set, allSpatial, set_for_shape,
           spatialDimensions, range_select, flow1_values, flow2_values,
           flowScale);
  }

  /** assemble Flow components;
      Flow components are 'single', so no compositing is required */
  public static void assembleFlow(float[][] flow1_values,
                float[][] flow2_values, float[] flowScale,
                float[][] display_values, int valueArrayLength,
                int[] valueToScalar, DisplayImpl display,
                float[] default_values, float[][] range_select)
         throws VisADException, RemoteException {
    ShadowType.assembleFlow(flow1_values, flow2_values, flowScale,
                      display_values, valueArrayLength, valueToScalar,
                      display, default_values, range_select);
  }

  public static VisADGeometryArray makeFlow(float[][] flow_values,
                float flowScale, float[][] spatial_values,
                float[][] color_values, float[][] range_select)
         throws VisADException { // J2D
    return ShadowType.makeFlow(flow_values, flowScale, spatial_values,
           color_values, range_select);
  }

  /** composite and transform color and Alpha DisplayRealType values
      from display_values, and return as (Red, Green, Blue, Alpha) */
  public static float[][] assembleColor(float[][] display_values,
                int valueArrayLength, int[] valueToScalar,
                DisplayImpl display, float[] default_values,
                float[][] range_select)
         throws VisADException, RemoteException {
    return ShadowType.assembleColor(display_values, valueArrayLength,
           valueToScalar, display, default_values, range_select);
  }

  /** return a composite of SelectRange DisplayRealType values from
      display_values, as 0.0 for select and Double.Nan for no select
      (these values can be added to other DisplayRealType values) */
  public static float[][] assembleSelect(float[][] display_values, int domain_length,
                                        int valueArrayLength, int[] valueToScalar,
                                        DisplayImpl display) throws VisADException {
    return ShadowType.assembleSelect(display_values, domain_length,
           valueArrayLength, valueToScalar, display);
  }

  boolean terminalTupleOrReal(VisADGroup group, float[][] display_values,
                              int valueArrayLength, int[] valueToScalar,
                              float[] default_values, int[] inherited_values,
                              DataRenderer renderer)
          throws VisADException, RemoteException {
 
    GraphicsModeControl mode = display.getGraphicsModeControl();
 
    float[][] flow1_values = new float[3][];
    float[][] flow2_values = new float[3][];
    float[] flowScale = new float[2];
    float[][] range_select = new float[1][];
    assembleFlow(flow1_values, flow2_values, flowScale,
                 display_values, valueArrayLength, valueToScalar,
                 display, default_values, range_select);
 
    if (range_select[0] != null && range_select[0][0] != range_select[0][0]) {
      // data not selected
      return false;
    }

    int[] spatialDimensions = new int[2];
    float[][] spatial_values = new float[3][];
    assembleSpatial(spatial_values, display_values, valueArrayLength,
                    valueToScalar, display, default_values,
                    inherited_values, null, false, false,
                    spatialDimensions, range_select,
                    flow1_values, flow2_values, flowScale);

    if (range_select[0] != null && range_select[0][0] != range_select[0][0]) {
      // data not selected
      return false;
    }
 
    float[][] color_values =
      assembleColor(display_values, valueArrayLength, valueToScalar,
                    display, default_values, range_select);
 
    if (range_select[0] != null && range_select[0][0] != range_select[0][0]) {
      // data not selected
      return false;
    }
 
    int LevelOfDifficulty = adaptedShadowType.getLevelOfDifficulty();
    if (LevelOfDifficulty == SIMPLE_TUPLE) {
      // only manage Spatial, Color and Alpha here
      // i.e., the 'dots'
 
      if (color_values[0][0] != color_values[0][0] ||
          color_values[1][0] != color_values[1][0] ||
          color_values[2][0] != color_values[2][0]) {
        // System.out.println("single missing alpha");
        // a single missing color value, so render nothing
        return false;
      }
      // put single color in appearance
      float[] constant_color = {color_values[0][0], color_values[1][0],
                                color_values[2][0]};
      float constant_alpha = Float.NaN;

      VisADGeometryArray array;
      VisADAppearance appearance;

      boolean anyFlowCreated = false;
      // try Flow1
      array = makeFlow(flow1_values, flowScale[0], spatial_values,
                       color_values, range_select);
      if (array != null) {
        appearance = makeAppearance(mode, constant_alpha,
                                    constant_color, array);
        group.addChild(appearance);
        anyFlowCreated = true;
      }
      // try Flow2
      array = makeFlow(flow2_values, flowScale[1], spatial_values,
                       color_values, range_select);
      if (array != null) {
        appearance = makeAppearance(mode, constant_alpha,
                                    constant_color, array);
        group.addChild(appearance);
        anyFlowCreated = true;
      }

      if (!anyFlowCreated) {
        array = makePointGeometry(spatial_values, null);
        if (array != null) {
          appearance = makeAppearance(mode, constant_alpha,
                                      constant_color, array);
          group.addChild(appearance);
          if (renderer instanceof DirectManipulationRendererJ2D) {
            ((DirectManipulationRendererJ2D) renderer).setSpatialValues(spatial_values);
          }
        }
      }
      return false;
    }
    else { // if (!(LevelOfDifficulty == SIMPLE_TUPLE))
      // must be LevelOfDifficulty == LEGAL
      // add values to value_array according to SelectedMapVector-s
      // of RealType-s in components (including Reference)
      //
      // accumulate Vector of value_array-s at this ShadowTypeJ2D,
 
      // to be rendered in a post-process to scanning data
/*
      return true;
*/
      throw new UnimplementedException("ShadowTypeJ2D.terminalTupleOrReal: " +
                                       "terminal LEGAL");
    }
  }

  public String toString() {
    return adaptedShadowType.toString();
  }

}

