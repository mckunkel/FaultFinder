/**
 * 
 */
package clasDC.faults;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * @author m.c.kunkel
 *
 */
@Getter
public class FaultSlidingInformation {
	private int yInc;
	private int xLength;
	private int[] xStart;
	private int[] xEnd;

	@Getter(AccessLevel.NONE)
	private FaultNames faultName;
	@Getter(AccessLevel.NONE)
	private int xMin;
	@Getter(AccessLevel.NONE)
	private int xMax;

	public FaultSlidingInformation() {

	}

	public void setFaultName(FaultNames faultName) {
		this.faultName = faultName;
		setInformation();
	}

	private void setInformation() {

		if (this.faultName.getSaveName().equals("pin_small")) {
			this.xLength = 10;
			this.xMin = 0;
			this.xMax = 12;
			this.xStart = new int[this.xLength];
			this.xEnd = new int[this.xLength];
			for (int i = 0; i < this.xLength; i++) {
				xStart[i] = xMin;
				xEnd[i] = xMax;
				xMin += 8;
				xMax += 8;
			}
			this.yInc = 6;
		} else if (this.faultName.getSaveName().equals("pin_big")) {
			this.xLength = 2;
			this.xStart = new int[] { 80, 96 };
			this.xEnd = new int[] { 104, 112 };
			this.yInc = 6;
		} else if (this.faultName.getSaveName().equals("Channel_1")) {
			this.xLength = 4;
			this.xMin = 0;
			this.xMax = 12;
			this.xStart = new int[this.xLength];
			this.xEnd = new int[this.xLength];
			for (int i = 0; i < this.xLength; i++) {
				this.xStart[i] = xMin;
				this.xEnd[i] = xMax;
				xMin += 8;
				xMax += 8;
			}
			this.yInc = 0;
		} else if (this.faultName.getSaveName().equals("Channel_2")) {
			this.xLength = 3;
			this.xMin = 32;
			this.xMax = 56;
			this.xStart = new int[this.xLength];
			this.xEnd = new int[this.xLength];
			for (int i = 0; i < this.xLength; i++) {
				this.xStart[i] = xMin;
				this.xEnd[i] = xMax;
				xMin += 16;
				xMax += 16;
			}
			this.yInc = 0;
		} else if (this.faultName.getSaveName().equals("Channel_3")) {
			this.xLength = 1;
			this.xMin = 66;
			this.xMax = 112;
			this.xStart = new int[this.xLength];
			this.xEnd = new int[this.xLength];
			for (int i = 0; i < this.xLength; i++) {
				this.xStart[i] = xMin;
				this.xEnd[i] = xMax;
				xMin += 16;
				xMax += 16;
			}
			this.yInc = 0;
		} else if (this.faultName.getSaveName().equals("Connector_E")
				|| this.faultName.getSaveName().equals("Connector_Tree")
				|| this.faultName.getSaveName().equals("Connector_Three")) {
			this.xLength = 14;
			this.xMin = 0;
			this.xMax = 8;
			this.xStart = new int[this.xLength];
			this.xEnd = new int[this.xLength];
			for (int i = 0; i < this.xLength; i++) {
				this.xStart[i] = xMin;
				this.xEnd[i] = xMax;
				xMin += 8;
				xMax += 8;
			}
			this.yInc = 0;
		} else if (this.faultName.getSaveName().equals("Fuse_A") || this.faultName.getSaveName().equals("Fuse_B")
				|| this.faultName.getSaveName().equals("Fuse_C")) {
			this.xLength = 7;
			this.xMin = 0;
			this.xMax = 16;
			this.xStart = new int[this.xLength];
			this.xEnd = new int[this.xLength];
			for (int i = 0; i < this.xLength; i++) {
				this.xStart[i] = xMin;
				this.xEnd[i] = xMax;
				xMin += 16;
				xMax += 16;
			}
			this.yInc = 0;
		} else if (this.faultName.getSaveName().equals("Deadwire") || this.faultName.getSaveName().equals("Hotwire")) {
			this.xLength = 1;
			this.xMin = 0;
			this.xMax = 112;
			this.xStart = new int[this.xLength];
			this.xEnd = new int[this.xLength];
			for (int i = 0; i < this.xLength; i++) {
				this.xStart[i] = xMin;
				this.xEnd[i] = xMax;
				// System.out.println(xMin + " " + xMax);
				// xMin += 2;
				// xMax += 2;
			}
			this.yInc = 6;
		} else if (this.faultName.getSaveName().equals("No_Fault")) {
			this.xLength = 1;
			this.xMin = 0;
			this.xMax = 112;
			this.xStart = new int[this.xLength];
			this.xEnd = new int[this.xLength];
			for (int i = 0; i < this.xLength; i++) {
				this.xStart[i] = xMin;
				this.xEnd[i] = xMax;

			}
			this.yInc = 6;
		} else {
			throw new IllegalArgumentException("What are you inputting ILLEGAL " + this.faultName);
		}

	}

	public static void main(String[] args) {
		FaultSlidingInformation faultSlidingInformation = new FaultSlidingInformation();
		faultSlidingInformation.setFaultName(FaultNames.DEADWIRE);

		// System.out.println(faultSlidingInformation.getXEnd()[faultSlidingInformation.getXLength()
		// - 1]
		// - faultSlidingInformation.getXStart()[0] + " "
		// +
		// faultSlidingInformation.getXEnd()[faultSlidingInformation.getXLength()
		// - 1] + " "
		// + faultSlidingInformation.getXStart()[0]);

	}

}
