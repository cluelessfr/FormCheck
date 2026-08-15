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
