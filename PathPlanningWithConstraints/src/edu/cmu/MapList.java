/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cmu;

/**
 * 
 * @author Chet
 */
public class MapList
{

    // The fields:
    // 0 The name of the map
    // 1 the floor number
    // 2 The type of map
    // 3 The id that the android uses to find the image
    // 4 The filename prefix used when saving any data associated with the plan
    // 5 C or I, Complete or Incomplete. Complete means that SVG files are included and
    // 6 data about the rooms and hallways that can be extracted if map is complete
    public static final String[][] MAPS =
    {
    { "Newell Simon 1st Floor", "1", "Floorplan", Integer.toString(R.raw.nsh_1_f), "nsh_1_f", "C", Integer.toString(R.raw.nsh_1_f_vec) },
    //{ "Newell Simon 2nd Floor", "2", "Floorplan", Integer.toString(R.raw.nsh_2_f), "nsh_2_f", "C", Integer.toString(R.raw.nsh_2_f_vec) },
    // { "Newell Simon 4th Floor", "3", "Floorplan", Integer.toString(R.raw.nsh_4_f), "nsh_4_f", "C", Integer.toString(R.raw.nsh_4_f_vec) },
    // { "Newell Simon 3nd Floor", "3", "Floorplan", Integer.toString(R.raw.nsh_3_f), "nsh_3_f", "C", Integer.toString(R.raw.nsh_3_f_vec_c) },
    // {"Gates 5th Floor", "5","Floorplan", Integer.toString(R.raw.gates_5_f),"gates_5_f","I",""},
    // {"Gates 6th Floor (Robot Map)", "6" , "Robot Map", Integer.toString(R.raw.gates_6_r),"gates_6_r","I",""},
    // {"Newell Simon 1st Floor (Robot Map)", "1" ,"Robot Map", Integer.toString(R.raw.nsh_1_r),"nsh_1_r","I",""},
    // {"Newell Simon Floor A (Robot Map)", "A" ,"Robot Map", Integer.toString(R.raw.nsh_a_r),"nsh_a_r","I",""}
    };

    /**
     * These ,matrices correspond to the translation, shift and scalings of between the robot maps and the floorplan's coordinate system. The shift, scalings and rotations can be found by using Photoshop or something. Overlay the images
     * with the origin of each map at the 0,0 marker. The scaling and rotation can be determined by stretching and rotating the overlay. The shift is done last.
     */
    public static final float[][][] mTrans =
    {
    {
    { -14.7636f, 1.2916f, 970.00f },
    { -1.2916f, -14.7636f, 580.0f },
    { 0, 0, 1.0f } },// /nsh_1_f
    {
    { 0, -14.76f, 980.0f },
    { -14.76f, 0, 620.0f },
    { 0, 0, 1.0f } },// /nsh_2_f
    // {{0, 1.0f, 0.0f}, {1.0f, 0, 0.0f}, {0, 0, 1.0f}},
    // {{0, 1.0f, 0.0f}, {1.0f, 0, 0.0f}, {0, 0, 1.0f}},
    // {{0, 1.0f, 0.0f}, {1.0f, 0, 0.0f}, {0, 0, 1.0f}},
    // {{0, 1.0f, 0.0f}, {1.0f, 0, 0.0f}, {0, 0, 1.0f}}
    };

    /**
     * The first two numbers in each array are the scalings. These correspond to the conversion between pixels and points. The second number is used to shift the map if it looks like it needs done (admin->showObstacles)
     */
    public static final float[][] mReadSvg =
    {
    { 1 / .795f, 1 / .8f, 0f, 0f },// /nsh_1_f
    { 1 / .795f, 1 / .8f, -4.0f, 0.0f }, // /nsh_2_f
    // { 1 / .795f, 1 / .8f, -4.0f, 0.0f }, // /nsh_3_f // Added by Gary. These numbers may need to be tweaked

    // {{0, 1.0f, 0.0f}, {1.0f, 0, 0.0f}, {0, 0, 1.0f}},
    // {{0, 1.0f, 0.0f}, {1.0f, 0, 0.0f}, {0, 0, 1.0f}},
    // {{0, 1.0f, 0.0f}, {1.0f, 0, 0.0f}, {0, 0, 1.0f}},
    // {{0, 1.0f, 0.0f}, {1.0f, 0, 0.0f}, {0, 0, 1.0f}}
    };
}
