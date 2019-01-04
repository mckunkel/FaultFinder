package spark.utils;

public class AbstractQuery {

	public void shutdown() {
		SparkManager.INSTANCE.shutdown();
	}
}
