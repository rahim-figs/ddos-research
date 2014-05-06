package simulation;

public class Detection {

	private int mTime;
	private Router mRouter;
	private Rule mRule;
	private float mConfidence;
	
	public Detection(int time, Router router, Rule rule, float confidence) {
		mTime = time;
		mRouter = router;
		mRule = rule;
		mConfidence = confidence;
	}

	public int getTime() {
		return mTime;
	}

	public Router getRouter() {
		return mRouter;
	}

	public Rule getRule() {
		return mRule;
	}

	public float getConfidence() {
		return mConfidence;
	}

}
