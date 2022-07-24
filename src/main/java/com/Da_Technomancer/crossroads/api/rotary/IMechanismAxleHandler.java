package com.Da_Technomancer.crossroads.api.rotary;

public interface IMechanismAxleHandler extends IAxleHandler{

	byte getUpdateKey();

	void setUpdateKey(byte keyIn);

	boolean renderOffset();

	void setRenderOffset(boolean newOffset);

	void setRotRatio(double newRotRatio);
}
