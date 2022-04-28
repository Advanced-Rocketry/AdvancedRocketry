package zmaster587.advancedRocketry.backwardCompat;

import java.io.IOException;

public class ModelFormatException extends Exception {

	public ModelFormatException(String format, IOException e) {
		super(format, e);
	}

	public ModelFormatException(String format, NumberFormatException e) {
		super(format, e);
	}

	public ModelFormatException(String msg) {
		super(msg);
	}

}
