# Current feedback proposal:
My current proposal for feedback is giving two types of feedback. 
The first is the angle that the user makes with their squat. With that angle we can compare it to current public data/research to find a good/ideal angle, and give them feedback saying this like you lowered too less or too much. 
The second feedback is how fast the user lowered when doing their squat.We could use similar research as before to give feedback such as you lowered too fast or lowered too slow. 

## First feedback feature: lowering tempo/speed
We will use the timestamp measurements to estimate the time between the start of lowering and bottom of the squat, and the bottom of the squat and when they return back to standing.
We will use the data from our research as a comparision (research still undecided)
We will give the user feedback based on the comparision (ex. "Your estimated lowering phase was 1.0 s. Our research suggests 2.0 s. Try lowering more slowly to match that target.").
We will ensure to understand when feedback cannot be reliably given (such as when the gap between usable observations is too long). 

# Source 1: Effects of different lifting cadences on ground reaction forces during the squat exercise
Link: https://pubmed.ncbi.nlm.nih.gov/20386484/

Raising and lowering times based on condition:


| Condition | Prescribed Lowering Time | Prescribed Rising Time |
|:---------:|:------------------------:|:----------------------:|
|   Fast    |          1 sec           |         1 sec          |
|  Medium   |          3 sec           |         1 sec          | 
|   Slow    |          4 sec           |         2 sec          | 

The study was with six adult men doing loaded barbell squats.
Researchers measured forces and movement, and faster cadences produced higher peak forces. 
The study did not establish an ideal tempo. 

# Source 2: Acute Metabolic and Muscle Oxygenation Responses to Different Eccentric Tempos Under a Fixed Velocity-Loss Threshold in Squat
Link: https://pubmed.ncbi.nlm.nih.gov/40925585/

The participants were 12 healthy males performing loaded parallel squats. 
They had a prescribed 4-second lowering phase
They found that a 4-second lowering phase produced more changes in oxygenation measures compared to other conditions. 
Mean power did not differ significantly. 

# Source 3: Markerless Pixel-Based Pipeline for Quantifying 2D Lower Limb Kinematics During Squatting: A Preliminary Validation Study
Link: https://www.mdpi.com/2673-7078/6/1/1

10 healthy volunteers did squats while researchers compared a MediaPipe based 2D system with marker motion capture. 
The systems showed agreement, but the markerless system also showed systematic differences, especially at the hip.
The study had controlled recording environments and a sample of 10 people. 
This supports video based measurements, and does not validate the accuracy of FormCheck. 

## First feedback scope
Estimate lowering duration from lowering onset to the bottom.
Compare it with a user selected practice-target.
Explain whether the estimate was shorter, near, or longer than that target.
Don't give feedback if the observations don't support a reliable estimate.
