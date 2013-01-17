How to set up development environment:
1. Install eclipse, set your workspace to the directory you extracted the files, and configure it for android development (see http://developer.android.com/sdk/installing.html)
2. Import the projects into eclipse (File -> Import -> General -> Existing projects into workspace)
3. Wait a little while for everything to build. There may transient errors reported for each project
	as their dependencies get built.
	
The generated javadoc documentation can be found in "doc/index.html"

Description of the Projects:
AndroidDataCollector - Used for logging all sensor data from a phone during an experiment (signal strengths, accelerometer, magnetometer, etc.)
HumanRobotInterface - Visual display of location tracking, also used to hook in to trading system to bid on and recieve tasks
LocalizationLibrary - Contains the main research components, including the pedestrian localization algorithms and particle filter implementation.
LogProcessor - Has many desktop programs for analysis of log data, including a particle filter visualizer and several programs to extract particular data streams from a log for further analysis in Matlab
Matlab - Matlab code primarily for analysis of Wifi and GSM data

Main Entry Points:
AndroidDataCollector
	-DataCollector.java: The activity that does all the logging
HumanRobotInterface
	-MapActivity.java: The screen the user interacts with 95% of the time
LocalizationLibrary
	-filter
		-PedestrianLocalization_Gyro.java: The new localization method which uses a gyroscope. Currently unfinished and under active development.
		-PedestrianLocalization_StandardDeviation.java: localization method that tracks movement by looking at the variance in acceleration values.
		-PedestrianLocalization_StepDetection.java: localization method that uses a peak detector (similar to a pedometer) to track movement.
	 -particleFilter
		-ParticleFilter.java: Generic implementation of the particle filter algorithm.
	 -sensor
		-RSSIMeasurer.java: Observer for particle filter based localization using Wifi or GSM Signal strengths.