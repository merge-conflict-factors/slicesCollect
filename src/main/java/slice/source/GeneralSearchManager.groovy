package slice.source

import java.io.FileWriter;
import java.util.List;
import au.com.bytecode.opencsv.CSVReader
import au.com.bytecode.opencsv.CSVWriter;



class GeneralSearchManager {

	public static List<String[]> readCSV(csv){
		//CSVReader reader = new CSVReader(new FileReader(Util.TASKS_FILE))
		CSVReader reader = new CSVReader(new FileReader(csv))
		List<String[]> entries = reader.readAll()
		reader.close()
		//entries.remove(0) //ignore header
		return entries
	}
	
	
	public static List<String[]> readCSVHeader(csv){
		//CSVReader reader = new CSVReader(new FileReader(Util.TASKS_FILE))
		CSVReader reader = new CSVReader(new FileReader(csv))
		List<String[]> entries = reader.readAll()
		reader.close()
		entries.remove(0) //ignore header
		return entries
	}


		
	public static List<MergeCommitDetail> getMergeCommitDetailsList(String pathCSVFile){
		List<String[]> entries = readCSVHeader(pathCSVFile)
		List<MergeCommitDetail> listMergeScenarioDetails = []
		entries.each { entry ->
			//println  entries.size()
			MergeCommitDetail mg = new MergeCommitDetail()			
			mg.MergecommitID = entry[0]
			mg.isMergeConflicting = entry[1]
			mg.filesConflicting = entry[2]
			mg.Parent1ID = entry[3]
			mg.Parent1Files = entry[4]
			mg.Parent2ID = entry[5]
			mg.Parent2Files = entry[6]
			listMergeScenarioDetails += mg
		}
		return listMergeScenarioDetails
	}
	

	static main(args) {
		
	}

}
