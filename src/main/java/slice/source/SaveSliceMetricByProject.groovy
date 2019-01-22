package slice.source
import java.io.FileWriter;
import java.util.List

import au.com.bytecode.opencsv.CSVWriter;
import util.*;

class SaveSliceMetricByProject {
	
	def saveSliceMetricByProjectCSV(def projeto, String slicesResultsPath, String dataRubyPath){		
		CSVWriter writer = new CSVWriter(new FileWriter(dataRubyPath+projeto+".csv")); 
		String[] text = ["mergeID", "isConflicting","existsCommonSlices", "totalCommonSlices"]
		writer.writeNext(text);	
		
		def merges = new GeneralSearchManager().readCSVHeader(slicesResultsPath+projeto+"_MergeScenario_SliceDetails.csv")
		
		merges.each {
			def isConflicting = 0
			def existsCommonSlices = 0
			def totalCommonSlices = 0
			
			if (it[13].equals("true")){
				isConflicting = 1
			}
			if (!it[14].equals("[]")){
				existsCommonSlices = 1
			}			
			if (!it[14].equals("[]")){
				it[14].tokenize(',[]')*.trim().each {
					totalCommonSlices++
				}
			}
			
			String[] linha = [it[0],isConflicting,existsCommonSlices, totalCommonSlices]
			writer.writeNext(linha)
		}
		
		writer.close()
	}
	
	def execute(def project, String slicesResultsPath, String dataRubyPath){
		this.saveSliceMetricByProjectCSV(project, slicesResultsPath, dataRubyPath)
	}
	
	def execute(String slicesResultsPath, String dataRubyPath){
		def projects = GeneralSearchManager.readCSV("projectList.csv")
		projects.each {
			  def project = it[0]//.substring(0, it[0].indexOf("."))
			  this.saveSliceMetricByProjectCSV(project, slicesResultsPath, dataRubyPath)
		}
		  
	}
	
		static main(args) {
			//just testing			
			
		}

}
