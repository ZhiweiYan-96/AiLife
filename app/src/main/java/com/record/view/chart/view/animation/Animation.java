package com.record.view.chart.view.animation;

import android.graphics.Path;
import android.graphics.PathMeasure;
import com.record.view.chart.model.ChartSet;
import com.record.view.chart.view.ChartView;
import com.record.view.chart.view.ChartView.Orientation;
import com.record.view.chart.view.animation.easing.BaseEasingMethod;
import com.record.view.chart.view.animation.easing.quint.QuintEaseOut;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Animation {
    private static final int DEFAULT_ALPHA_OFF = -1;
    private static final int DEFAULT_DURATION = 1000;
    private static final float DEFAULT_OVERLAP_FACTOR = 1.0f;
    private static final long DELAY_BETWEEN_UPDATES = 20;
    private int mAlphaSpeed;
    private Runnable mAnimator = new Runnable() {
        public void run() {
            if (Animation.this.mChartView.canIPleaseAskYouToDraw()) {
                Animation.this.mChartView.addData(Animation.this.getUpdate(Animation.this.mChartView.getData()));
                Animation.this.mChartView.postInvalidate();
            }
        }
    };
    private ChartView mChartView;
    private long[] mCurrentDuration;
    private long mCurrentGlobalDuration;
    private int mDuration;
    private BaseEasingMethod mEasing;
    private long mGlobalDuration;
    private long mGlobalInitTime;
    private long[] mInitTime;
    private boolean mIsExiting;
    private int[] mOrder;
    private float mOverlapingFactor;
    private PathMeasure[][] mPathMeasures;
    private boolean mPlaying;
    private Runnable mRunnable;
    private float mStartXFactor;
    private float mStartYFactor;

    public Animation() {
        init(1000);
    }

    public Animation(int duration) {
        init(duration);
    }

    private void init(int duration) {
        this.mGlobalDuration = (long) duration;
        this.mOverlapingFactor = DEFAULT_OVERLAP_FACTOR;
        this.mAlphaSpeed = -1;
        this.mEasing = new QuintEaseOut();
        this.mStartXFactor = -1.0f;
        this.mStartYFactor = -1.0f;
        this.mPlaying = false;
        this.mCurrentGlobalDuration = 0;
        this.mGlobalInitTime = 0;
    }

    public ArrayList<ChartSet> prepareAnimation(ChartView chartView, ArrayList<float[][]> start, ArrayList<float[][]> end) {
        int i;
        int nSets = start.size();
        int nEntries = ((float[][]) start.get(0)).length;
        this.mChartView = chartView;
        this.mCurrentDuration = new long[nEntries];
        if (this.mOrder == null) {
            this.mOrder = new int[nEntries];
            for (i = 0; i < this.mOrder.length; i++) {
                this.mOrder[i] = i;
            }
        }
        float noOverlapDuration = (float) (this.mGlobalDuration / ((long) nEntries));
        this.mDuration = (int) (((((float) this.mGlobalDuration) - noOverlapDuration) * this.mOverlapingFactor) + noOverlapDuration);
        this.mPathMeasures = (PathMeasure[][]) Array.newInstance(PathMeasure.class, new int[]{nSets, nEntries});
        for (i = 0; i < nSets; i++) {
            for (int j = 0; j < nEntries; j++) {
                Path path = new Path();
                path.moveTo(((float[][]) start.get(i))[j][0], ((float[][]) start.get(i))[j][1]);
                path.lineTo(((float[][]) end.get(i))[j][0], ((float[][]) end.get(i))[j][1]);
                this.mPathMeasures[i][j] = new PathMeasure(path, false);
            }
        }
        this.mInitTime = new long[nEntries];
        this.mGlobalInitTime = System.currentTimeMillis();
        for (i = 0; i < nEntries; i++) {
            long noOverlapInitTime = this.mGlobalInitTime + (((long) i) * (this.mGlobalDuration / ((long) nEntries)));
            this.mInitTime[this.mOrder[i]] = noOverlapInitTime - ((long) (this.mOverlapingFactor * ((float) (noOverlapInitTime - this.mGlobalInitTime))));
        }
        this.mPlaying = true;
        return getUpdate(this.mChartView.getData());
    }

    private ArrayList<ChartSet> prepareAnimation(ChartView chartView) {
        float x;
        float y;
        ArrayList<ChartSet> sets = chartView.getData();
        if (this.mStartXFactor != -1.0f) {
            x = chartView.getInnerChartLeft() + ((chartView.getInnerChartRight() - chartView.getInnerChartLeft()) * this.mStartXFactor);
        } else {
            x = chartView.getZeroPosition();
        }
        if (this.mStartYFactor != -1.0f) {
            y = chartView.getInnerChartBottom() - ((chartView.getInnerChartBottom() - chartView.getInnerChartTop()) * this.mStartYFactor);
        } else {
            y = chartView.getZeroPosition();
        }
        int nSets = sets.size();
        int nEntries = ((ChartSet) sets.get(0)).size();
        ArrayList<float[][]> startValues = new ArrayList(nSets);
        ArrayList<float[][]> endValues = new ArrayList(nSets);
        for (int i = 0; i < nSets; i++) {
            float[][] startSet = (float[][]) Array.newInstance(Float.TYPE, new int[]{nEntries, 2});
            float[][] endSet = (float[][]) Array.newInstance(Float.TYPE, new int[]{nEntries, 2});
            for (int j = 0; j < nEntries; j++) {
                if (this.mStartXFactor == -1.0f && chartView.getOrientation() == Orientation.VERTICAL) {
                    startSet[j][0] = ((ChartSet) sets.get(i)).getEntry(j).getX();
                } else {
                    startSet[j][0] = x;
                }
                if (this.mStartYFactor == -1.0f && chartView.getOrientation() == Orientation.HORIZONTAL) {
                    startSet[j][1] = ((ChartSet) sets.get(i)).getEntry(j).getY();
                } else {
                    startSet[j][1] = y;
                }
                endSet[j][0] = ((ChartSet) sets.get(i)).getEntry(j).getX();
                endSet[j][1] = ((ChartSet) sets.get(i)).getEntry(j).getY();
            }
            startValues.add(startSet);
            endValues.add(endSet);
        }
        return prepareAnimation(chartView, startValues, endValues);
    }

    public ArrayList<ChartSet> prepareEnterAnimation(ChartView chartView) {
        this.mIsExiting = false;
        return prepareAnimation(chartView);
    }

    public ArrayList<ChartSet> prepareExitAnimation(ChartView chartView) {
        this.mIsExiting = true;
        return prepareAnimation(chartView);
    }

    private ArrayList<ChartSet> getUpdate(ArrayList<ChartSet> data) {
        int i;
        int nSets = data.size();
        int nEntries = ((ChartSet) data.get(0)).size();
        long currentTime = System.currentTimeMillis();
        this.mCurrentGlobalDuration = currentTime - this.mGlobalInitTime;
        for (i = 0; i < nEntries; i++) {
            long diff = currentTime - this.mInitTime[i];
            if (diff < 0) {
                this.mCurrentDuration[i] = 0;
            } else {
                this.mCurrentDuration[i] = diff;
            }
        }
        if (this.mCurrentGlobalDuration > this.mGlobalDuration) {
            this.mCurrentGlobalDuration = this.mGlobalDuration;
        }
        float[] posUpdate = new float[2];
        for (i = 0; i < nSets; i++) {
            for (int j = 0; j < nEntries; j++) {
                float timeNormalized = normalizeTime(j);
                if (this.mAlphaSpeed != -1) {
                    ((ChartSet) data.get(i)).setAlpha(((float) this.mAlphaSpeed) * timeNormalized);
                }
                if (!getEntryUpdate(i, j, timeNormalized, posUpdate)) {
                    posUpdate[0] = ((ChartSet) data.get(i)).getEntry(j).getX();
                    posUpdate[1] = ((ChartSet) data.get(i)).getEntry(j).getY();
                }
                ((ChartSet) data.get(i)).getEntry(j).setCoordinates(posUpdate[0], posUpdate[1]);
            }
        }
        if (this.mCurrentGlobalDuration < this.mGlobalDuration) {
            this.mChartView.postDelayed(this.mAnimator, DELAY_BETWEEN_UPDATES);
            this.mCurrentGlobalDuration += DELAY_BETWEEN_UPDATES;
        } else {
            this.mCurrentGlobalDuration = 0;
            this.mGlobalInitTime = 0;
            if (this.mRunnable != null) {
                this.mRunnable.run();
            }
            this.mPlaying = false;
            this.mAlphaSpeed = -1;
        }
        return data;
    }

    private float normalizeTime(int index) {
        if (this.mIsExiting) {
            return DEFAULT_OVERLAP_FACTOR - (((float) this.mCurrentDuration[index]) / ((float) this.mDuration));
        }
        return ((float) this.mCurrentDuration[index]) / ((float) this.mDuration);
    }

    private boolean getEntryUpdate(int i, int j, float normalizedTime, float[] pos) {
        return this.mPathMeasures[i][j].getPosTan(this.mPathMeasures[i][j].getLength() * this.mEasing.next(normalizedTime), pos, null);
    }

    public boolean isPlaying() {
        return this.mPlaying;
    }

    public Runnable getEndAction() {
        return this.mRunnable;
    }

    public Animation setEasing(BaseEasingMethod easing) {
        this.mEasing = easing;
        return this;
    }

    public Animation setDuration(int duration) {
        this.mGlobalDuration = (long) duration;
        return this;
    }

    public Animation setOverlap(float factor) {
        this.mOverlapingFactor = factor;
        return this;
    }

    public Animation setOverlap(float factor, int[] order) {
        this.mOverlapingFactor = factor;
        this.mOrder = order;
        return this;
    }

    public Animation setEndAction(Runnable runnable) {
        this.mRunnable = runnable;
        return this;
    }

    public Animation setStartPoint(float xFactor, float yFactor) {
        this.mStartXFactor = xFactor;
        this.mStartYFactor = yFactor;
        return this;
    }

    public Animation setAlpha(int speed) {
        this.mAlphaSpeed = speed;
        return this;
    }
}
