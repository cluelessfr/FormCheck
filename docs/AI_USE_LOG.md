# AI Use Log
## In this project I used AI (OpenAI Codex) in the following ways:
1. Planning
2. Debugging Guidance
3. Code Review
4. Explanation of unfamiliar concepts

## I did the following tasks myself:
1. Writing and understanding the code that was written
2. Reviewed Codex's suggestions instead of automatically implementing them
3. Recorded test results

# Dated Entries
## 08/05/2026
I created the FormCheck project inside of Android Studio, configured an Android Virtual Machine, and connected the project to GitHub. I decided that FormCheck would analyze prerecorded videos instead of requiring a live camera feed.

## 08/07/2026
I wrote the XML interface code for the app title, app description, "Select a video" button, and privacy notice. I also added text fields indicating the video selection status, which tells the user whether a file was selected. I was able to verify the opening of the app and the button click, but I could not verify the file selection because the emulator was slow and unreliable. 

## 08/08/2026
I wrote the code for calculating the angle between three points. The middle point would be the vertex (the knee), while the other two would be endpoints (ex. ankle and hip). I calculated the angle using the dot-product formula. I verified the 90 degree, 180 degree, and undefined angles with synthetic points. 

## 08/09/2026
I wrote the code which determines the quality of the landmarks (points). The visibility describes how well the body landmark is visible or obstructed. The presence describes if the body landmark is present within the frame itself. This passed the reliability threshold tests, but MediaPipe is not integrated yet and final "minimum thresholds" have not been selected yet.

I was also able to verify the file selection using the Firebase remote Android device on a Google Pixel 10. The status now changed from "No video selected" to "Video selected". This test does not confirm video decoding or MediaPipe analysis.

## 08/13/2026
I finally added the MediaPipe Tasks Vision dependency. Currently, I have only added it into the Gradle build, not yet implemented features like pose detection and video analysis.

I also wrote PoseLandmarkerManager to initialize the vision model. It was tested with the emulator and passed. This only verifies model initialization, not frame processing.

## 08/15/2026
I added a method that receives an already decoded video frame and timestamp and sends it to MediaPipe. It rejects null images and negative timestamps. All tests pass, but so far I have only tested with a synthetic image, not a real image.

## 09/07/2026
I wrote the VideoFrameDecoder function to be able to read a video's duration using Android's media metadata system. I also wrote an emulator test to go along with this that detected the duration of a 3-second video (it passed, and Codex used ffmpeg to crop the original video into the 3-second video). So far this can only do metadata reading, but in the future it will be able to do pose analysis and more.

## 09/10/2026
I wrote a function that extracts a bitmap when given a video timestamp. I then added a method that converts bitmaps into MPImages. Finally, I added a centralized class to connect decoding, conversion, and pose processing. The class returned a valid object, but I wasn't able to confirm that the pose landmarks were detected. I wrote instrumented tests for all of these, and using the Pixel 7 instrumented tests all 11/11 of them passed. Currently only one frame is processed.

## 09/19/2026
Now I added a few things. First I added a function that can sample frames at different intervals and stores the MediaPipe result with the timestamp of the frame. I also added hip/knee/ankle extraction for both sides and also confidence-based leg section. Codex wrote a few methods, and I wrote the rest. I then added timestamped knee angle measurements, and also connected the leg selector to the angle calculator. Finally, I added a sequence analyzer that keeps the valid measurements and skips the unclear frames. I added test for all these functions

## 09/23/2026
I added a function to detect the first squat repetition, with detector thresholds and added instrumented tests. Codex implemented the method to detect the first repetition given a list of TimestampedKneeAngles. The detector now recognizes standing, bottom, and returning to standing angles and returns empty for incomplete sequences. All 51 JVM tests passed. The thresholds used for the standing and bottom angles were for testing and not universal standards.
