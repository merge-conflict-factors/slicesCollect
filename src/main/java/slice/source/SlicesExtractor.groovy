package slice.source
import java.io.FileWriter;
import java.util.List

import au.com.bytecode.opencsv.CSVWriter;
import util.*;

class SlicesExtractor {	
	
	

	def extractSliceDetails(def nome, def leftFiles, def rigthFiles, def mergeCommitID, def isMergeConflicting, CSVWriter writer1){
		
		def countSMLeft=0  
		def countSMRight=0
		def countSVLeft=0  
		def countSVRight=0
		def countSCLeft=0  
		def countSCRight=0
		def countAppSharedLeft=0 
		def countAppSharedRight=0
		def countConfigLeft=0 
		def countConfigRight=0	
		def slicesLeft = []
		def slicesRight = []
		def commomSlices = []
		def list = this.getSlicesParent(leftFiles)	
		countSMLeft = list[0]
		countSVLeft = list[1]
		countSCLeft = list[2]
		countAppSharedLeft = list[3]
		countConfigLeft = list[4]
		slicesLeft = list[5]
			
		list = this.getSlicesParent(rigthFiles)
		countSMRight = list[0]
		countSVRight = list[1]
		countSCRight = list[2]
		countAppSharedRight = list[3]
		countConfigRight = list[4]
		slicesRight = list[5]
		
		def slicesLeftTmp = this.confirmSlicesParent(nome, slicesLeft) 
		def slicesRightTmp = this.confirmSlicesParent(nome, slicesRight)
		commomSlices = this.getCommomSlicesMergeScenarios(nome, slicesLeftTmp, slicesRightTmp)
		
		String[] linhaCSV1 = [mergeCommitID, countSMLeft,countSVLeft,countSCLeft,countAppSharedLeft,countConfigLeft,slicesLeftTmp,countSMRight,countSVRight,countSCRight,countAppSharedRight,countConfigRight,slicesRightTmp,isMergeConflicting,commomSlices]
		writer1.writeNext(linhaCSV1)		
		
	}	

	private def getSlicesParent(def filesParentList){
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
				
		filesParentList.each { file->
			if (file.startsWith(sm)){
				def nomeModelo = file.substring(11)
				def tmp = ""
				if(nomeModelo.contains("/")){ //i.e. app/models/subfolder/xx.erb
					tmp = nomeModelo.substring(0, nomeModelo.indexOf("/"))
					modelsSlices.add(tmp)
					countSM++					
				}else if (!nomeModelo.contains("/") & nomeModelo.contains(".")){ // i.e. app/models/xx.erb
					tmp = nomeModelo.substring(0, nomeModelo.indexOf("."))
					modelsSlices.add(tmp)
					countSM++
				}

			} else 	if (file.startsWith(sv)){
				def nomeView = file.substring(10)
				def tmp = ""
				if (nomeView.indexOf("/")!=-1){
				   tmp = nomeView.substring(0, nomeView.indexOf("/"))
				   viewsSlices.add(tmp)
				   countSV++
				}
			}else if (file.startsWith(sc)){
				def nomeController = file.substring(16)
				def tmp = nomeController.substring(0, nomeController.indexOf("."))
				if (tmp.contains("_")){
					def tmp2 = tmp.substring(0, tmp.indexOf("_"))
					controllersSlices.add(tmp2)
					countSC++
				}else{
					controllersSlices.add(tmp)
					countSC++
				}
			}else if (file.startsWith(appShared)){
				countAppShared++
			}else{
				countConfig++
			}
		}
		
		def slicesParent = (modelsSlices + viewsSlices + controllersSlices).unique()
		
		modelsSlices.each {model->
			viewsSlices.each {view->
				if (view.contains(model)){
					slicesParent-=view
				}
			}
		}
		
		modelsSlices.each {model->
			controllersSlices.each {controller->
				if (controller.contains(model)){
					slicesParent-=controller
				}
			}
		}
		
		String[] linhaCSV1 = [countSM,countSV,countSC,countAppShared,countConfig, slicesParent]
		return linhaCSV1
	}

	private def  confirmSlicesParent(def nome, def slicesParent){
		def list = []
		def newList = []
		def generalAppUseDir = ["layouts", "sessions", "application", "shared", "partials","devise","kaminari"]
		if (slicesParent.size()>0) {
			list = slicesParent.tokenize(',[]')*.trim()
			newList = list - generalAppUseDir
		}
		return newList.unique()
	}
	
	private def  getCommomSlicesMergeScenarios(def nome, def leftSlices, def rightSlices){
		def commomList = []
		if (leftSlices.size() > 0 && rightSlices.size() >0) {
			leftSlices.each { l->
				rightSlices.each { r ->
					if (l.equals(r)){
						commomList.add(l)
					}
					if (!l.equals(r) & !( l.contains("/") | r.contains("/")  |  l.contains("_") | r.contains("_")) ){
						if (l.contains(r)){
							commomList.add(r)
						} else if (r.contains(l)){
							commomList.add(l)
						}
					}
				}
			}
		}
		return commomList.unique()
	}

	def execute(def nome, String contributionResultsPath, String sliceResultsPath){
	   def mergesDetail = new GeneralSearchManager().getMergeCommitDetailsList(contributionResultsPath+nome+"_MergeScenarioList.csv")
	   String nAllMerges = sliceResultsPath+nome+"_MergeScenario_SliceDetails.csv";
	   CSVWriter writernAllMerges = new CSVWriter(new FileWriter(nAllMerges));
	   String[] textnAllMerges = ["Merge Commit ID", "countModelLeft", "countViewLeft", "countControllerLeft", "countAppLeft", "countConfigLeft", "slicesListLeft", "countModelRight", "countViewRight", "countControllerRight", "countAppRight", "countConfigRight", "slicesListRight", "isConflicting", "commomSlices"]
	   writernAllMerges.writeNext(textnAllMerges);
   
	   mergesDetail.each {merge->
		   def listLeft = merge.Parent1Files.tokenize('@[]')*.trim()//tokenize(',[]')*.trim()
		   def listRight = merge.Parent2Files.tokenize('@[]')*.trim()//tokenize(',[]')*.trim()
		   this.extractSliceDetails(nome, listLeft, listRight,merge.MergecommitID, merge.isMergeConflicting, writernAllMerges)
	   }
	   writernAllMerges.close()
  
	}

	def execute(String contributionResultsPath, String sliceResultsPath){
		def projetos = GeneralSearchManager.readCSV("projectList.csv")
		projetos.each {
		   def nome = it[0]//.substring(0, it[0].indexOf("."))
		   def mergesDetail = new GeneralSearchManager().getMergeCommitDetailsList(contributionResultsPath+nome+"_MergeScenarioList.csv")
		   String nAllMerges = sliceResultsPath+nome+"_MergeScenario_SliceDetails.csv";
		   CSVWriter writernAllMerges = new CSVWriter(new FileWriter(nAllMerges));
		   String[] textnAllMerges = ["Merge Commit ID", "countModelLeft", "countViewLeft", "countControllerLeft", "countAppLeft", "countConfigLeft", "slicesListLeft", "countModelRight", "countViewRight", "countControllerRight", "countAppRight", "countConfigRight", "slicesListRight", "isConflicting", "commomSlices"]
		   writernAllMerges.writeNext(textnAllMerges);
	   
		   mergesDetail.each {merge->
			  /* def listLeft = merge.Parent1Files.tokenize(',[]')*.trim()
			   def listRight = merge.Parent2Files.tokenize(',[]')*.trim()
			   */
			   def listLeft = merge.Parent1Files.tokenize('@[]')*.trim()
			   def listRight = merge.Parent2Files.tokenize('@[]')*.trim()
			   this.extractSliceDetails(nome, listLeft, listRight,merge.MergecommitID, merge.isMergeConflicting, writernAllMerges)
		   }
		   writernAllMerges.close()
		}
  
	}
  
  
	static main(args) {
		//Just Testing

	}

}
