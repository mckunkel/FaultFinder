/*  +__^_________,_________,_____,________^-.-------------------,
 *  | |||||||||   `--------'     |          |                   O
 *  `+-------------USMC----------^----------|___________________|
 *    `\_,---------,---------,--------------'
 *      / X MK X /'|       /'
 *     / X MK X /  `\    /'
 *    / X MK X /`-------'
 *   / X MK X /
 *  / X MK X /
 * (________(                @author m.c.kunkel
 *  `------'
*/
package faultfinder.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import spark.utils.SparkManager;

public class EmptyDataPoint {
	private int superLayer;
	private int sector;
	private int wire;
	private int layer;
	private int counts;

	public EmptyDataPoint(int superLayer, int sector, int wire, int layer) {
		this.superLayer = superLayer;
		this.sector = sector;
		this.wire = wire;
		this.layer = layer;
	}

	public int getSuperLayer() {
		return superLayer;
	}

	public void setSuperLayer(int superLayer) {
		this.superLayer = superLayer;
	}

	public int getSector() {
		return sector;
	}

	public void setSector(int sector) {
		this.sector = sector;
	}

	public int getWire() {
		return wire;
	}

	public void setWire(int wire) {
		this.wire = wire;
	}

	public int getLayer() {
		return layer;
	}

	public void setLayer(int layer) {
		this.layer = layer;
	}

	public int getCounts() {
		return counts;
	}

	public void setCounts(int counts) {
		this.counts = counts;
	}

	public void increment() {
		counts++;
	}

	public static Dataset<Row> getEmptyDCData() {
		SparkSession spSession = SparkManager.getSession();
		List<EmptyDataPoint> emptyList = new ArrayList<EmptyDataPoint>();
		for (int superLayer = 1; superLayer <= 6; superLayer++) {
			for (int sector = 2; sector <= 2; sector++) {
				for (int wire = 1; wire <= 112; wire++) {
					for (int layer = 1; layer <= 6; layer++) {
						emptyList.add(new EmptyDataPoint(superLayer, sector, wire, layer));
					}
				}
			}
		}
		Encoder<EmptyDataPoint> multiDataEncoder = Encoders.bean(EmptyDataPoint.class);
		Dataset<Row> df = spSession.createDataset(emptyList, multiDataEncoder).toDF();
		return df;
	}
}
