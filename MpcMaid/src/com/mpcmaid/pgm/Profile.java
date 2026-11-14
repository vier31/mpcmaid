package com.mpcmaid.pgm;

/**
 * Represents the profile of an MPC
 * 
 * @author cyrille martraire
 */
public final class Profile {

	private final String name;

	private final int rowNumber;

	private final int colNumber;

	private final int sliderNumber;

	private final int filterNumber;

	private final int outputNumber;

	public final static Profile MPC500 = new Profile("MPC500", 4, 3, 1, 1, 3);

	public final static Profile MPC1000 = new Profile("MPC1000", 4, 4, 2, 2, 3);

	public final static Profile MPC2500 = new Profile("MPC2500", 4, 4, 2, 2, 5);

	public final static Profile[] PROFILES = { MPC1000, MPC2500, MPC500 };

	public Profile(String name, int rowNumber, int colNumber, int sliderNumber, int filterNumber, int outputNumber) {
		this.name = name;
		this.rowNumber = rowNumber;
		this.colNumber = colNumber;
		this.sliderNumber = sliderNumber;
		this.filterNumber = filterNumber;
		this.outputNumber = outputNumber;
	}

	public String getName() {
		return name;
	}

	public int getPadNumber() {
		return rowNumber * colNumber;
	}

	public int getRowNumber() {
		return rowNumber;
	}

	public int getColNumber() {
		return colNumber;
	}

	public int getSliderNumber() {
		return sliderNumber;
	}

	public int getFilterNumber() {
		return filterNumber;
	}

	/**
	 * @return The number of choices for the pad mixer output (Stereo plus the
	 *         assignable stereo pairs)
	 */
	public int getOutputNumber() {
		return outputNumber;
	}

	public String toString() {
		return "Profile " + name;
	}

	public static Profile getProfile(String name) {
		for (int i = 0; i < PROFILES.length; i++) {
			if (PROFILES[i].getName().equalsIgnoreCase(name)) {
				return PROFILES[i];
			}
		}
		return MPC500;
	}

}
