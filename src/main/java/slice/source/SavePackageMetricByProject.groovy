package slice.source
import java.io.FileWriter;
import java.util.List

import au.com.bytecode.opencsv.CSVWriter;
import util.*;


/*
 * Exploring package structure effect as we do with slices: both common packages occurrence and total
 */

class SavePackageMetricByProject {
	
	def savePackageMetricByProjectCSV(def projeto, String slicesResultsPath, String dataRubyPath){	
	
		CSVWriter writer = new CSVWriter(new FileWriter(dataRubyPath+projeto+"_Packages.csv")); 
		String[] text = ["mergeID", "isConflicting","existsCommonPackages", "totalCommonPackages"]
		writer.writeNext(text);	
		
		def merges = new GeneralSearchManager().readCSVHeader(slicesResultsPath+projeto+"_MergeScenario_SliceDetails.csv")
		

		merges.each {
			def isConflicting = 0
			def existsCommonPackages = 0
			def totalCommonPackages = 0
			
			def leftPackages = []
			def rightPackages = []
			def commomPackages = []
			
			if(!it[1].equals("0")){
				leftPackages.add("model")
			}
			if(!it[2].equals("0")){
				leftPackages.add("view")
			}
			if(!it[3].equals("0")){
				leftPackages.add("controller")
			}
			if(!it[4].equals("0")){
				leftPackages.add("app")
			}
			if(!it[5].equals("0")){
				leftPackages.add("config")
			}
			
			
			if(!it[7].equals("0")){
				rightPackages.add("model")
			}
			if(!it[8].equals("0")){
				rightPackages.add("view")
			}
			if(!it[9].equals("0")){
				rightPackages.add("controller")
			}
			if(!it[10].equals("0")){
				rightPackages.add("app")
			}
			if(!it[11].equals("0")){
				rightPackages.add("config")
			}
			
			
			commomPackages = leftPackages.intersect(rightPackages)
			
			if (it[13].equals("true")){
				isConflicting = 1
			}
			if (!commomPackages.isEmpty()){
				existsCommonPackages = 1
				totalCommonPackages = commomPackages.size()//tokenize(',[]')*.trim().size()
			}			
			
			
			
			String[] linha = [it[0],isConflicting,existsCommonPackages,totalCommonPackages]
			writer.writeNext(linha)
		}
		
		writer.close()
	}
	
	def execute(def project, String slicesResultsPath, String dataRubyPath){
		this.savePackageMetricByProjectCSV(project, slicesResultsPath, dataRubyPath)
	}
	
	def execute(String slicesResultsPath, String dataRubyPath){
		def projects = GeneralSearchManager.readCSV("projectList.csv")
		projects.each {
			  def project = it[0]//.substring(0, it[0].indexOf("."))
			  this.savePackageMetricByProjectCSV(project, slicesResultsPath, dataRubyPath)
		}
		  
	}
	
		static main(args) {
			//just testing	
			//new SavePackageMetricByProject().execute("slicesExtractorResults/", "rubyData/")		
			
		}

}
