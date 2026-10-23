package com.taran.formcheck.feedback;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.taran.formcheck.analysis.LandmarkQualityGate;
import com.taran.formcheck.pose.LoweringTiming;
import com.taran.formcheck.pose.LoweringTimingAnalyzer;
import com.taran.formcheck.pose.PoseLandmarkerManager;
import com.taran.formcheck.pose.SquatAnalysisOutcome;
import com.taran.formcheck.pose.SquatAnalysisResult;
import com.taran.formcheck.pose.SquatRepetitionDetector;
import com.taran.formcheck.pose.TimestampedPoseResult;
import com.taran.formcheck.pose.VideoKneeAngleSequenceAnalyzer;
import com.taran.formcheck.pose.VideoPoseSequenceProcessor;
import com.taran.formcheck.pose.VideoSquatSequenceAnalyzer;
import com.taran.formcheck.video.VideoFrameDecoder;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@RunWith(AndroidJUnit4.class)
public class FeedbackEvaluationInstrumentedTest {
    @Test
    public void testSideViewEvaluation() throws IOException {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        File cachedVideoFile = copyAssetToCache("feedback-evaluation/side-view-complete-squat.mp4", "evaluation-side-view.mp4");

        Uri uri = Uri.fromFile(cachedVideoFile);

        try (VideoFrameDecoder decoder = new VideoFrameDecoder(appContext, uri);
             PoseLandmarkerManager manager = new PoseLandmarkerManager(appContext)) {
            List<TimestampedPoseResult> timestampedPoseResults = VideoPoseSequenceProcessor.processVideoPoseSequence(decoder, manager, 250);

            LandmarkQualityGate gate = new LandmarkQualityGate(0.5, 0.5);
            VideoKneeAngleSequenceAnalyzer analyzer = new VideoKneeAngleSequenceAnalyzer(gate);
            SquatRepetitionDetector repetitionDetector = new SquatRepetitionDetector(160, 120);
            LoweringTimingAnalyzer timingAnalyzer =  new LoweringTimingAnalyzer(5, 3, 500);
            VideoSquatSequenceAnalyzer squatSequenceAnalyzer = new VideoSquatSequenceAnalyzer(analyzer, repetitionDetector, timingAnalyzer);

            SquatAnalysisResult result = squatSequenceAnalyzer.analyze(timestampedPoseResults);

            LoweringPracticeTarget target = new LoweringPracticeTarget(3000, 500);
            LoweringFeedbackOutcome outcome = LoweringFeedbackAnalyzer.analyze(result, target, RecordingSuitability.CONFIRMED_SIDE_VIEW);

            String TAG = "FeedbackEvaluation";
            Log.d(TAG, "Clip Name: side-view-complete-squat; Repetition-detection outcome: " + result.getOutcome() + "; Feedback outcome: " + outcome);

            if (result.getLoweringTiming().isPresent()) {
                LoweringTiming timing = result.getLoweringTiming().get();
                Log.d(TAG, "Start Timing: " + timing.getLoweringStart() + " Ms; Bottom Timing: " + timing.getBottomReached() + " Ms; Duration: " + timing.getDurationMilliseconds() + " Ms; Sampling Allowance: " + timing.getBoundarySamplingAllowanceMS() + " Ms");
                Optional<LoweringDurationRange> range  = LoweringDurationRange.fromTiming(timing);

                if (range.isPresent()) {
                    Log.d(TAG, "Minimum: " + range.get().getMinimumDurationMs() + " Ms; Maximum: " + range.get().getMaximumDurationMs() + " Ms");
                }
                else {
                    Log.d(TAG, "Duration range unavailable");
                }
            }
            else {
                Log.d(TAG, "Lowering timing unavailable");
            }

            Assert.assertEquals(SquatAnalysisOutcome.COMPLETE_REPETITION_DETECTED, result.getOutcome());
        }

        finally {
            cachedVideoFile.delete();
        }
    }

    @Test
    public void testFrontViewEvaluation() throws IOException {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        File cachedVideoFile = copyAssetToCache("feedback-evaluation/front-view-squat.mp4", "evaluation-front-view.mp4");

        Uri uri = Uri.fromFile(cachedVideoFile);

        try (VideoFrameDecoder decoder = new VideoFrameDecoder(appContext, uri);
             PoseLandmarkerManager manager = new PoseLandmarkerManager(appContext)) {
            List<TimestampedPoseResult> timestampedPoseResults = VideoPoseSequenceProcessor.processVideoPoseSequence(decoder, manager, 250);

            LandmarkQualityGate gate = new LandmarkQualityGate(0.5, 0.5);
            VideoKneeAngleSequenceAnalyzer analyzer = new VideoKneeAngleSequenceAnalyzer(gate);
            SquatRepetitionDetector repetitionDetector = new SquatRepetitionDetector(160, 120);
            LoweringTimingAnalyzer timingAnalyzer =  new LoweringTimingAnalyzer(5, 3, 500);
            VideoSquatSequenceAnalyzer squatSequenceAnalyzer = new VideoSquatSequenceAnalyzer(analyzer, repetitionDetector, timingAnalyzer);

            SquatAnalysisResult result = squatSequenceAnalyzer.analyze(timestampedPoseResults);

            LoweringPracticeTarget target = new LoweringPracticeTarget(3000, 500);
            LoweringFeedbackOutcome outcome = LoweringFeedbackAnalyzer.analyze(result, target, RecordingSuitability.UNSUPPORTED_VIEW);

            String TAG = "FeedbackEvaluation";
            Log.d(TAG, "Clip Name: front-view-squat; Repetition-detection outcome: " + result.getOutcome() + "; Feedback outcome: " + outcome);

            if (result.getLoweringTiming().isPresent()) {
                LoweringTiming timing = result.getLoweringTiming().get();
                Log.d(TAG, "Start Timing: " + timing.getLoweringStart() + " Ms; Bottom Timing: " + timing.getBottomReached() + " Ms; Duration: " + timing.getDurationMilliseconds() + " Ms; Sampling Allowance: " + timing.getBoundarySamplingAllowanceMS() + " Ms");
                Optional<LoweringDurationRange> range  = LoweringDurationRange.fromTiming(timing);

                if (range.isPresent()) {
                    Log.d(TAG, "Minimum: " + range.get().getMinimumDurationMs() + " Ms; Maximum: " + range.get().getMaximumDurationMs() + " Ms");
                }
                else {
                    Log.d(TAG, "Duration range unavailable");
                }
            }
            else {
                Log.d(TAG, "Lowering timing unavailable");
            }

            Assert.assertEquals(LoweringFeedbackOutcome.UNAVAILABLE, outcome);
        }

        finally {
            cachedVideoFile.delete();
        }
    }

    private File copyAssetToCache(String assetPath, String cacheFilename) throws IOException{
        Context testContext = InstrumentationRegistry.getInstrumentation().getContext();
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        File destinationFile = new File(appContext.getCacheDir(), cacheFilename);

        try (InputStream file = testContext.getAssets().open(assetPath);
             FileOutputStream outputStream = new FileOutputStream(destinationFile)) {
            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = file.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        return destinationFile;
    }
}
