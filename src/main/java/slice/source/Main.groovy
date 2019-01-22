package slice.source

import java.util.ArrayList;
import java.util.List;

class Main {

	private String contributionsExtractResultsPath = "";
    private String slicesExtractorPath = "";
	private String dataRubyPath = "";
	
	public String getContributionsExtractResultsPath(){
		return this.contributionsExtractResultsPath
	}
	public String getSlicesExtractorPath(){
		return this.slicesExtractorPath
	}
	public String getDataRubyPath(){
		return this.dataRubyPath
	}

	public Main(){
		List<String[]> paths = new GeneralSearchManager().readCSV("configuration.properties");
		ArrayList<String> dir = new ArrayList<String>();
		for(String[] s : paths) {
			   for (String path : s) {
				   dir.add(path.substring(path.indexOf("=")+1, path.length()).trim());
			}
		}
		this.contributionsExtractResultsPath = dir.get(0); 
		this.slicesExtractorPath = dir.get(1); 
		this.dataRubyPath = dir.get(2); 
	}
	
	def execute(String contribResultsPath, String slicesResultsPath, String rubyDataPath){
		def projetos = GeneralSearchManager.readCSV("projectList.csv")
		projetos.each {
		   def nome = it[0]
		   new SlicesAnalyzer().execute(nome, contribResultsPath, slicesResultsPath)
		   //slice metrics
		   new SaveSliceMetricByProject().execute(nome, slicesResultsPath, rubyDataPath)
		   //package metrics
		   new SavePackageMetricByProject().execute(nome, slicesResultsPath, rubyDataPath)
		}
	}
	static main(args) {
		Main m = new Main();
		m.execute(m.getContributionsExtractResultsPath(), m.getSlicesExtractorPath(), m.getDataRubyPath() )
		println "done.."
	
	}

}
