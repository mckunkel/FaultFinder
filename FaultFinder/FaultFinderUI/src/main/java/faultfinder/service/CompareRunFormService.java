package faultfinder.service;

import java.util.List;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public interface CompareRunFormService {

	public List<String> getAllRuns();

	// public Dataset<Row> compareRun(String str);

	public void compareRun(String str);

	public Dataset<Row> getAllRunsDataset();

	public List<String> getAllProblems();

	public Dataset<Row> getAllProblemsDataset();

}
