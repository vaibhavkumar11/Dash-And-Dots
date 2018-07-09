/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.gms.samples.vision.face.dashanddots;

import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.samples.vision.face.dashanddots.ui.camera.GraphicOverlay;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


class DashFaceTracker extends Tracker<Face> {
    private static final float EYE_CLOSED_THRESHOLD = 0.4f;
        private FirebaseFirestore firestore;
    private GraphicOverlay mOverlay;
    private DashGraphic mEyesGraphic;


    private Map<Integer, PointF> mPreviousProportions = new HashMap<>();


    private boolean mPreviousIsLeftOpen = true;
    private boolean mPreviousIsRightOpen = true;



    DashFaceTracker(GraphicOverlay overlay) {
        mOverlay = overlay;
    }

    @Override
    public void onNewItem(int id, Face face) {
        mEyesGraphic = new DashGraphic(mOverlay);
    }


    @Override
    public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {


        updatePreviousProportions(face);

        PointF leftPosition = getLandmarkPosition(face, Landmark.LEFT_EYE);
        PointF rightPosition = getLandmarkPosition(face, Landmark.RIGHT_EYE);

        float leftOpenScore = face.getIsLeftEyeOpenProbability();
        float rightOpenScore = face.getIsRightEyeOpenProbability();



        boolean isLeftOpen;
        if (leftOpenScore == Face.UNCOMPUTED_PROBABILITY) {
            isLeftOpen = mPreviousIsLeftOpen;
        } else {
            isLeftOpen = (leftOpenScore > EYE_CLOSED_THRESHOLD);
            mPreviousIsLeftOpen = isLeftOpen;
        }
        int temp=Integer.MIN_VALUE;



        boolean isRightOpen;
        if (rightOpenScore == Face.UNCOMPUTED_PROBABILITY) {
            isRightOpen = mPreviousIsRightOpen;
        } else {
            isRightOpen = (rightOpenScore > EYE_CLOSED_THRESHOLD);
            mPreviousIsRightOpen = isRightOpen;
        }

        if(DashAndDots.morseCode.size()<6) {
            if (!isRightOpen && !isLeftOpen) {
                Log.v("Main", DashAndDots.counter.size() + "Morse");
                DashAndDots.counter.add(0);

            } else if (isRightOpen && isLeftOpen) {
                if (DashAndDots.counter.size() < 10 && DashAndDots.counter.size() > 0) {
                    DashAndDots.morseCode.add(0);
                } else if (DashAndDots.counter.size() > 10) {
                    DashAndDots.morseCode.add(1);
                }
                DashAndDots.counter.clear();
            }
            Log.v("Code", DashAndDots.morseCode.size() + "");
        }
        if(DashAndDots.morseCode.size()==2)
        {
            Log.v("Original", DashAndDots.morseCode.get(0)+" "+ DashAndDots.morseCode.get(1));
            checkMorseCode();
        }



        //  mEyesGraphic.updateEyes(leftPosition, isLeftOpen, rightPosition, isRightOpen);
    }


    @Override
    public void onMissing(FaceDetector.Detections<Face> detectionResults) {
        mOverlay.remove(mEyesGraphic);
    }


    @Override
    public void onDone() {
        mOverlay.remove(mEyesGraphic);
    }


    private void updatePreviousProportions(Face face) {
        for (Landmark landmark : face.getLandmarks()) {
            PointF position = landmark.getPosition();
            float xProp = (position.x - face.getPosition().x) / face.getWidth();
            float yProp = (position.y - face.getPosition().y) / face.getHeight();
            mPreviousProportions.put(landmark.getType(), new PointF(xProp, yProp));
        }
    }


    private PointF getLandmarkPosition(Face face, int landmarkId) {
        for (Landmark landmark : face.getLandmarks()) {
            if (landmark.getType() == landmarkId) {
                return landmark.getPosition();
            }
        }

        PointF prop = mPreviousProportions.get(landmarkId);
        if (prop == null) {
            return null;
        }

        float x = face.getPosition().x + (prop.x * face.getWidth());
        float y = face.getPosition().y + (prop.y * face.getHeight());
        return new PointF(x, y);
    }
    public void checkMorseCode()
    {
        /**int morseCodeInteger= DashAndDots.morseCode.get(0) *10000+ DashAndDots.morseCode.get(1) *1000+ DashAndDots.morseCode.get(2) *100+ DashAndDots.morseCode.get(3) *10+ DashAndDots.morseCode.get(4);
        Log.v("Integer",morseCodeInteger+"");
        int num=0;
        int num1=1111;
        int num2=111;
        int num3=11;
        int num4=1;

        if(morseCodeInteger==num1)
        {
            num=1;
        }
        else if(morseCodeInteger==num2)
        {
            num=2;
        }
        else if(morseCodeInteger==num3)
        {
            num=3;
        }
        else if(morseCodeInteger==num4)
        {
            num=4;
        }
        **/
        int morseCodeInteger= DashAndDots.morseCode.get(0)*10+ DashAndDots.morseCode.get(1);
        Log.v("Integer",morseCodeInteger+"");
        int num=4;
        int num1=0;
        int num2=1;
        int num3=11;
        if(morseCodeInteger==num1)
        {
            num=1;
        }
        else if(morseCodeInteger==num2)
        {
            num=2;
        }
        else if(morseCodeInteger==num3)
        {
            num=3;
        }

        Log.e("MorseCode",num+"");
       firestore = FirebaseFirestore.getInstance();
        HashMap<String,String> maps=new HashMap<>();
        maps.put("channel",Integer.toString(num));
        DashAndDots.morseCode.clear();
        DashAndDots.counter.clear();
        firestore.collection("videos").document("vid").set(maps)
                .addOnSuccessListener(new OnSuccessListener< Void >() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Toast.makeText(,"Successfully sent to database",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d("TAG", e.toString());
                    }
                });


    }
}