package spark.utils;

import static org.apache.spark.sql.functions.col;

import org.apache.spark.sql.Dataset;

import faultfinder.objects.StatusChangeDB;

public class MainFrameQuery extends AbstractQuery {
	private Dataset<StatusChangeDB> queryDF = null;

	public MainFrameQuery() {

	}

	public void setDataset(Dataset<StatusChangeDB> queryDF) {
		this.queryDF = queryDF;
	}

	public Dataset<StatusChangeDB> getBySector(int sector) {
		return queryDF.filter(col("sector").equalTo(sector));
	}

	public Dataset<StatusChangeDB> getBySuperLayer(int superLayer) {
		return queryDF.filter(col("superlayer").equalTo(superLayer));
	}

	public Dataset<StatusChangeDB> getByLayer(int layer) {
		return queryDF.filter(col("loclayer").equalTo(layer));
	}

	public Dataset<StatusChangeDB> getBySectorAndSuperLayer(int sector, int superLayer) {
		return this.getBySector(sector).filter(col("superlayer").equalTo(superLayer));
	}

	public Dataset<StatusChangeDB> getBySectorAndSuperLayerAndLayer(int sector, int superLayer, int layer) {
		return this.getBySectorAndSuperLayer(sector, superLayer).filter(col("loclayer").equalTo(layer));
	}

	public void prepareMYDQLQuery() {

	}
}
