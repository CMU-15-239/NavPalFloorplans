Overview of the LocalizationLibrary components

edu.cmu.ri.rcommerce.filter
	Provides several simple filters as well as three different pedestrian localization implementations

edu.cmu.ri.rcommerce.particleFilter
	Provides a complete, generic, particle filter implementation
	
edu.cmu.rcommerce.render
	Provides Android-specific map rendering libraries

edu.cmu.rcommerce.sensor
	Provides an implementation of a particle filter for localization using wifi or gsm signal strengths.
	The main class of note is RSSIMeasurer, which is a component of the particle filter algorithm.
	RSSIMeasurer is initialized with a RuntimeProvider and a CalibrateProvider.
	
	The CalibrateProvider is responsible for giving an expected signal strength reading from
	 the location of a given particle.
	The RuntimeProvider gives a sequence of measured readings over time. It could provide them as they
	 are being captured, or as playback from a log.
	
	RSSIMeasurer uses these two to adjust the weights of all the particles in the state space, using
	 a particular distance metric.