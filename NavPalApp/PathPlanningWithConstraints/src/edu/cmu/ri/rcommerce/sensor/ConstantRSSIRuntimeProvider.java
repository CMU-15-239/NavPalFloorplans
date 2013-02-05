package edu.cmu.ri.rcommerce.sensor;

/** dummy provider used for testing particle filter algorithm */
class ConstantRSSIRuntimeProvider implements RSSIRuntimeProvider {

	int resampleFrequency;
	int currentResampleState = 0;
	
	public ConstantRSSIRuntimeProvider(int invResampleFrequency)
	{
		this.resampleFrequency = invResampleFrequency;
	}
	@Override
	public boolean newReadingAvailable() {
		return ((currentResampleState++) % resampleFrequency == 0);
	}

	@Override
	public RSSIReading getCurrentReading() {
		return new RSSIReading(System.currentTimeMillis(), new long[]{5}, new float[]{0,-40}, RSSIReading.WIFI_RSSI);
	}

}
