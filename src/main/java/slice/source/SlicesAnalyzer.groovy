package slice.source
import java.io.FileWriter;
import java.util.List

import au.com.bytecode.opencsv.CSVWriter;
import util.*;


class SlicesAnalyzer {
	
   	private def evaluateMergeScenarioDetails(def csv, String contribResultsPath, String slicesResultsPath){
		try {
			
			def mergesDetail = new GeneralSearchManager().getMergeCommitDetailsList(contribResultsPath+csv+"_MergeScenarioList.csv")
			
			String n4 = slicesResultsPath+csv+"_ConflictingFiles_SliceDetails.csv"; 
			CSVWriter writer4 = new CSVWriter(new FileWriter(n4)); 
			String[] text4 = ["Merge Commit ID", "countModel","countView","countController","countApp","countConfig","slicesList"]
			writer4.writeNext(text4);
		
			mergesDetail.each {merge->
				if (merge.isMergeConflicting.equals("true")){
					this.checkConflictingFilesSlice(merge.filesConflicting,merge.MergecommitID, writer4 )	
				}				
			}
	
			writer4.close()
			new SlicesExtractor().execute(csv, contribResultsPath, slicesResultsPath)
			
		} catch (Exception e) {
		    System.out.println("stopped when running project " + csv);
			e.printStackTrace()
		}
	}

	
	
	private def checkConflictingFilesSlice(def conflictingFiles, def mergeCommitID, CSVWriter writer4){
			def listConflictingFiles = conflictingFiles.tokenize('@[]')*.trim()//tokenize(',[]')*.trim()			
			String sm = "app/models"
			def countSM=0  
			String sv = "app/views"
			def countSV=0  
			String sc = "app/controllers"
			def countSC=0  
			String appShared = "app/"
			def countAppShared=0 
			def countConfig=0 
			
			def modelsSlices=[]
			def viewsSlices = []
			def controllersSlices=[]

			listConflictingFiles.each { file->
				if (file.startsWith(sm)){
					def nomeModelo = file.substring(11)
					def tmp = nomeModelo.substring(0, nomeModelo.indexOf("."))
					modelsSlices.add(tmp)
					countSM++
				} else 	if (file.startsWith(sv)){
					def nomeView = file.substring(10)
					if (nomeView.contains("/")){
						def tmp = nomeView.substring(0, nomeView.indexOf("/"))
						viewsSlices.add(tmp)
						countSV++
					}else {
						def tmp = nomeView.substring(0, nomeView.indexOf("."))
						viewsSlices.add(tmp)
						countSV++
					}

				}else if (file.startsWith(sc)){
					def nomeController = file.substring(16)
					def tmp = nomeController.substring(0, nomeController.indexOf("."))
					
					if (tmp.contains("/")){ //subfolders
						def tmp2 = tmp.substring(0, tmp.indexOf("/"))
						controllersSlices.add(tmp2)
						countSC++
					}else {
						def tmp2 = tmp.substring(0, tmp.indexOf("_"))
						controllersSlices.add(tmp2)
						countSC++
					}

								
				}else if (file.startsWith(appShared)){ 
					countAppShared++
				}else{ 
					countConfig++
				}
			}			
			
			def conflictingFilesSlices = (modelsSlices + viewsSlices + controllersSlices).unique()
						
			modelsSlices.each {model->
				viewsSlices.each {view->
					if (view.contains(model)){
						conflictingFilesSlices-=view
					}
				}
			}
			
			modelsSlices.each {model->
				controllersSlices.each {controller->
					if (controller.contains(model)){
						conflictingFilesSlices-=controller
					}
				}
			}
			
			String[] linhaCSV4 = [mergeCommitID, countSM,countSV,countSC,countAppShared,countConfig,conflictingFilesSlices]	
			writer4.writeNext(linhaCSV4)
		}

	
	   def executeDEPRECATED(String contribResultsPath, String slicesResultsPath){	  
		  def projetos = GeneralSearchManager.readCSV("projectList.csv")
		  projetos.each {
			 def nome = it[0]
			 this.evaluateMergeScenarioDetails(nome, contribResultsPath, slicesResultsPath)			  
		  }	
	  }

	   def execute(String projectName, String contribResultsPath, String slicesResultsPath){
		  this.evaluateMergeScenarioDetails(projectName, contribResultsPath, slicesResultsPath)		  
	   }
 

	static main(args) {	
		//Just Testing
		new SlicesAnalyzer().execute("", "")
		
	}

}
