package com.taran.formcheck;

import android.content.Intent;
import android.net.Uri;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.util.concurrent.FutureTask;

@RunWith(AndroidJUnit4.class)
public class ActivityLifecycleInstrumentedTest {
    private void setField(MainActivity activity, String name, Object value) {
        try {
            Field field = MainActivity.class.getDeclaredField(name);
            field.setAccessible(true);
            field.set(activity, value);
        }
        catch (ReflectiveOperationException exception) {
            throw new AssertionError(exception);
        }
    }

    private void waitForFrames(ActivityScenario<? extends android.app.Activity> scenario) {
        java.util.concurrent.CountDownLatch drawn = new java.util.concurrent.CountDownLatch(1);
        scenario.onActivity(activity -> {
            android.view.View root = activity.getWindow().getDecorView();
            root.postOnAnimation(() -> root.postOnAnimation(drawn::countDown));
        });
        try {
            Assert.assertTrue("Layout did not draw", drawn.await(5, java.util.concurrent.TimeUnit.SECONDS));
        }
        catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new AssertionError(exception);
        }
    }

    @Test
    public void testRecreationPreservesSelectedInputs() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                setField(activity, "selectedVideoUri", Uri.parse("content://formcheck-test/video/1"));
                ((Spinner) activity.findViewById(R.id.loweringTargetSpinner)).setSelection(4);
                ((CheckBox) activity.findViewById(R.id.recordingSuitabilityCheckBox)).setChecked(true);
            });
            scenario.recreate();
            scenario.onActivity(activity -> {
                Assert.assertEquals(activity.getString(R.string.video_selected), ((TextView) activity.findViewById(R.id.selectionStatusText)).getText().toString());
                Assert.assertTrue(((Button) activity.findViewById(R.id.analyzeVideoButton)).isEnabled());
                Assert.assertEquals(4, ((Spinner) activity.findViewById(R.id.loweringTargetSpinner)).getSelectedItemPosition());
                Assert.assertTrue(((CheckBox) activity.findViewById(R.id.recordingSuitabilityCheckBox)).isChecked());
            });
        }
    }

    @Test
    public void testBackgroundCancelsAnalysisAndAllowsRetry() {
        FutureTask<Void> task = new FutureTask<>(() -> null);
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                setField(activity, "selectedVideoUri", Uri.parse("content://formcheck-test/video/1"));
                setField(activity, "analysisInProgress", true);
                setField(activity, "analysisTask", task);
                activity.findViewById(R.id.selectVideoButton).setEnabled(false);
                activity.findViewById(R.id.analyzeVideoButton).setEnabled(false);
            });
            scenario.moveToState(Lifecycle.State.CREATED);
            Assert.assertTrue(task.isCancelled());
            scenario.moveToState(Lifecycle.State.RESUMED);
            scenario.onActivity(activity -> {
                Assert.assertTrue(activity.findViewById(R.id.selectVideoButton).isEnabled());
                Assert.assertTrue(activity.findViewById(R.id.analyzeVideoButton).isEnabled());
                Assert.assertTrue(activity.findViewById(R.id.loweringTargetSpinner).isEnabled());
                Assert.assertTrue(activity.findViewById(R.id.recordingSuitabilityCheckBox).isEnabled());
                Assert.assertTrue(((TextView) activity.findViewById(R.id.analysisResultText)).getText().toString().contains("interrupted"));
            });
            scenario.recreate();
            scenario.onActivity(activity -> Assert.assertTrue(((TextView) activity.findViewById(R.id.analysisResultText)).getText().toString().contains("interrupted")));
        }
    }

    @Test
    public void testCancelThenRepeatedVideoAnalysisAndReturn() throws java.io.IOException {
        android.app.Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        java.io.File video = new java.io.File(instrumentation.getTargetContext().getCacheDir(), "lifecycle-test-video.mp4");
        try (java.io.InputStream input = instrumentation.getContext().getAssets().open("test_video.mp4");
             java.io.FileOutputStream output = new java.io.FileOutputStream(video)) {
            byte[] buffer = new byte[8192];
            int count;
            while ((count = input.read(buffer)) != -1) {
                output.write(buffer, 0, count);
            }
        }
        android.app.Instrumentation.ActivityMonitor monitor = new android.app.Instrumentation.ActivityMonitor(ResultsActivity.class.getName(), null, false);
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            instrumentation.addMonitor(monitor);
            scenario.onActivity(activity -> {
                Assert.assertEquals(android.view.View.GONE, activity.findViewById(R.id.cancelAnalysisButton).getVisibility());
                setField(activity, "selectedVideoUri", Uri.fromFile(video));
                ((Spinner) activity.findViewById(R.id.loweringTargetSpinner)).setSelection(4);
                ((CheckBox) activity.findViewById(R.id.recordingSuitabilityCheckBox)).setChecked(true);
                activity.findViewById(R.id.analyzeVideoButton).setEnabled(true);
                activity.findViewById(R.id.analyzeVideoButton).performClick();
                Assert.assertEquals(android.view.View.VISIBLE, activity.findViewById(R.id.cancelAnalysisButton).getVisibility());
                activity.findViewById(R.id.cancelAnalysisButton).performClick();
                Assert.assertEquals(android.view.View.GONE, activity.findViewById(R.id.cancelAnalysisButton).getVisibility());
                Assert.assertTrue(activity.findViewById(R.id.analyzeVideoButton).isEnabled());
                Assert.assertTrue(activity.findViewById(R.id.selectVideoButton).isEnabled());
                Assert.assertTrue(activity.findViewById(R.id.loweringTargetSpinner).isEnabled());
                Assert.assertTrue(activity.findViewById(R.id.recordingSuitabilityCheckBox).isEnabled());
                Assert.assertEquals(activity.getString(R.string.analysis_cancelled), ((TextView) activity.findViewById(R.id.analysisResultText)).getText().toString());
            });
            Assert.assertNull("Cancelled analysis opened results", instrumentation.waitForMonitorWithTimeout(monitor, 1500));
            scenario.recreate();
            scenario.onActivity(activity -> {
                Assert.assertEquals(activity.getString(R.string.analysis_cancelled), ((TextView) activity.findViewById(R.id.analysisResultText)).getText().toString());
                Assert.assertEquals(4, ((Spinner) activity.findViewById(R.id.loweringTargetSpinner)).getSelectedItemPosition());
                Assert.assertTrue(((CheckBox) activity.findViewById(R.id.recordingSuitabilityCheckBox)).isChecked());
                Assert.assertEquals(android.view.View.GONE, activity.findViewById(R.id.cancelAnalysisButton).getVisibility());
            });
            for (int run = 0; run < 2; run++) {
                instrumentation.addMonitor(monitor);
                scenario.onActivity(activity -> {
                    setField(activity, "selectedVideoUri", Uri.fromFile(video));
                    activity.findViewById(R.id.analyzeVideoButton).setEnabled(true);
                    activity.findViewById(R.id.analyzeVideoButton).performClick();
                });
                android.app.Activity results = instrumentation.waitForMonitorWithTimeout(monitor, 60000);
                Assert.assertNotNull("Analysis did not open the results page", results);
                instrumentation.runOnMainSync(() -> {
                    Assert.assertFalse(((TextView) results.findViewById(R.id.loweringFeedbackText)).getText().toString().isEmpty());
                    results.finish();
                });
                instrumentation.waitForIdleSync();
                scenario.onActivity(activity -> {
                    Assert.assertTrue(activity.findViewById(R.id.analyzeVideoButton).isEnabled());
                    Assert.assertTrue(activity.findViewById(R.id.selectVideoButton).isEnabled());
                    Assert.assertEquals(android.view.View.GONE, activity.findViewById(R.id.cancelAnalysisButton).getVisibility());
                    Assert.assertEquals(activity.getString(R.string.analysis_complete), ((TextView) activity.findViewById(R.id.analysisResultText)).getText().toString());
                });
            }
        }
        finally {
            instrumentation.removeMonitor(monitor);
            video.delete();
        }
    }

    @Test
    public void testScrollableLayoutsAndRepeatedNavigation() {
        for (int run = 0; run < 2; run++) {
            try (ActivityScenario<MainActivity> main = ActivityScenario.launch(MainActivity.class)) {
                main.onActivity(activity -> {
                    android.widget.ScrollView scroll = activity.findViewById(R.id.main);
                    scroll.setSmoothScrollingEnabled(false);
                    scroll.fullScroll(android.view.View.FOCUS_DOWN);
                });
                waitForFrames(main);
                InstrumentationRegistry.getInstrumentation().waitForIdleSync();
                main.onActivity(activity -> {
                    android.graphics.Rect visible = new android.graphics.Rect();
                    Assert.assertTrue("Last main field must be reachable; scrollY=" + ((android.widget.ScrollView) activity.findViewById(R.id.main)).getScrollY() + "; contentHeight=" + activity.findViewById(R.id.mainContent).getHeight(), activity.findViewById(R.id.analysisLimitationsText).getGlobalVisibleRect(visible));
                    Assert.assertEquals(0, ((android.widget.TextView) activity.findViewById(R.id.analysisLimitationsText)).getLayout().getEllipsisCount(0));
                });
            }

            Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), ResultsActivity.class);
            intent.putExtra(ResultsActivity.EXTRA_FEEDBACK_OUTCOME, "SHORTER_THAN_TARGET");
            intent.putExtra(ResultsActivity.EXTRA_ANALYSIS_OUTCOME, "COMPLETE_REPETITION_DETECTED");
            intent.putExtra(ResultsActivity.EXTRA_RECORDING_SUITABILITY, "CONFIRMED_SIDE_VIEW");
            intent.putExtra(ResultsActivity.EXTRA_STANDING_START_MS, 4000L);
            intent.putExtra(ResultsActivity.EXTRA_LOWEST_ANGLE_MS, 6000L);
            intent.putExtra(ResultsActivity.EXTRA_RETURN_TO_STANDING_MS, 7500L);
            intent.putExtra(ResultsActivity.EXTRA_LOWERING_START_MS, 4000L);
            intent.putExtra(ResultsActivity.EXTRA_LOWERING_BOTTOM_MS, 5750L);
            intent.putExtra(ResultsActivity.EXTRA_MINIMUM_DURATION_MS, 1250L);
            intent.putExtra(ResultsActivity.EXTRA_MAXIMUM_DURATION_MS, 2250L);
            intent.putExtra(ResultsActivity.EXTRA_TARGET_DURATION_MS, 4000L);
            intent.putExtra(ResultsActivity.EXTRA_TARGET_TOLERANCE_MS, 500L);
            intent.putExtra(ResultsActivity.EXTRA_STANDING_ANGLE_DEGREES, 166.9);
            intent.putExtra(ResultsActivity.EXTRA_LOWEST_ANGLE_DEGREES, 57.5);
            intent.putExtra(ResultsActivity.EXTRA_RETURN_ANGLE_DEGREES, 167.9);
            try (ActivityScenario<ResultsActivity> results = ActivityScenario.launch(intent)) {
                results.onActivity(activity -> {
                    android.widget.ScrollView scroll = activity.findViewById(R.id.resultsScrollView);
                    scroll.setSmoothScrollingEnabled(false);
                    scroll.fullScroll(android.view.View.FOCUS_DOWN);
                });
                waitForFrames(results);
                InstrumentationRegistry.getInstrumentation().waitForIdleSync();
                results.onActivity(activity -> {
                    android.graphics.Rect visible = new android.graphics.Rect();
                    android.view.View last = activity.findViewById(R.id.measuredAnglesText);
                    Assert.assertTrue(last.getGlobalVisibleRect(visible));
                    Assert.assertEquals(last.getHeight(), visible.height());
                    android.view.View toolbar = activity.findViewById(R.id.resultsToolbar);
                    android.view.View scroll = activity.findViewById(R.id.resultsScrollView);
                    Assert.assertTrue(toolbar.getBottom() <= scroll.getTop());
                    Assert.assertTrue(toolbar.getGlobalVisibleRect(new android.graphics.Rect()));
                    int[] fields = {R.id.loweringFeedbackText, R.id.detectionOutcomeText, R.id.sampledTimesText, R.id.practiceTargetText, R.id.recordingStatusText, R.id.loweringTimingText, R.id.loweringDurationText, R.id.measuredAnglesText};
                    int previousBottom = 0;
                    for (int id : fields) {
                        TextView field = activity.findViewById(id);
                        Assert.assertTrue(field.getTop() >= previousBottom);
                        previousBottom = field.getBottom();
                        for (int line = 0; line < field.getLineCount(); line++) {
                            Assert.assertEquals(0, field.getLayout().getEllipsisCount(line));
                        }
                    }
                });
                android.graphics.Bitmap screenshot = InstrumentationRegistry.getInstrumentation().getUiAutomation().takeScreenshot();
                try (java.io.FileOutputStream output = new java.io.FileOutputStream(new java.io.File(InstrumentationRegistry.getInstrumentation().getTargetContext().getExternalFilesDir(null), "reliability-results.png"))) {
                    screenshot.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, output);
                }
                catch (java.io.IOException exception) {
                    throw new AssertionError(exception);
                }
                finally {
                    screenshot.recycle();
                }
            }
        }
    }

    @Test
    public void testResultsSurviveRecreationAndBackArrow() {
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), ResultsActivity.class);
        intent.putExtra(ResultsActivity.EXTRA_FEEDBACK_OUTCOME, "SHORTER_THAN_TARGET");
        intent.putExtra(ResultsActivity.EXTRA_ANALYSIS_OUTCOME, "COMPLETE_REPETITION_DETECTED");
        intent.putExtra(ResultsActivity.EXTRA_STANDING_ANGLE_DEGREES, 166.9);
        intent.putExtra(ResultsActivity.EXTRA_LOWEST_ANGLE_DEGREES, 57.5);
        intent.putExtra(ResultsActivity.EXTRA_RETURN_ANGLE_DEGREES, 167.9);
        try (ActivityScenario<ResultsActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.recreate();
            scenario.onActivity(activity -> {
                Assert.assertEquals(activity.getString(R.string.measured_angles_valid, 166.9, 57.5, 167.9), ((TextView) activity.findViewById(R.id.measuredAnglesText)).getText().toString());
                Assert.assertEquals(activity.getString(R.string.lowering_feedback_shorter), ((TextView) activity.findViewById(R.id.loweringFeedbackText)).getText().toString());
                com.google.android.material.appbar.MaterialToolbar toolbar = activity.findViewById(R.id.resultsToolbar);
                for (int index = 0; index < toolbar.getChildCount(); index++) {
                    android.view.View child = toolbar.getChildAt(index);
                    if (activity.getString(R.string.back_to_main).contentEquals(child.getContentDescription() == null ? "" : child.getContentDescription())) {
                        child.performClick();
                        Assert.assertTrue(activity.isFinishing());
                        return;
                    }
                }
                Assert.fail("Back arrow was not found");
            });
        }
    }
}
