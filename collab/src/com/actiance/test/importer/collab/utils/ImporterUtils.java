package com.actiance.test.importer.collab.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ImporterUtils {
	
	public String[] getAllFileNames(String location) {
		String[] fileNames = null;
		try {
			File file = new File(location);
			System.out.println(file.getAbsolutePath());
			fileNames = file.list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileNames;
	}
	
	
//	verify the file presences in folder on the basic of isProcessed (if isProcessed=true than it should be in processedFiles Folder else in failedFiles or skippedFiles Folder)
	
	public boolean verifyFilePresence(String location, String fileName, boolean isProccessed){
		
		boolean proccessed = false;
		boolean failed = false;
		boolean skipped = false;
		ArrayList<String> directories = new ArrayList<String>();
		ArrayList<String> files = new ArrayList<String>();
		
		try{
			File parentDirectory = new File(location);
			String[] allNames = parentDirectory.list();
			for(String name : allNames){
				if(new File(location+"\\"+name).isDirectory()){
					directories.add(name);
				}else{
					files.add(name);
				}
			}
			
//			verify files presences in parent location after finished the Importer 
			
			/*if(files.size() != 0){
				System.out.println("The following files have not been proccessed");
				System.out.println(files);
				if(isProccessed){
				     for(String filename : files){
				      if(filename.equals(filename)){
				       System.out.println("File: "+filename+" has been processed but exists in the parent folder");
				       return false;
				      }
				     }
				    }else{
				     for(String filename : files){
				      if(filename.equals(filename)){
				       System.out.println("File: "+filename+" has not been processed but exists in the parent folder");
				       return false;
				      }
				     }
				    }
			}*/
			
			switch (directories.size()) {
//			 If in parent Folder have 3 Directories
			case 3:
				proccessed = checkFile(location+"\\processedFiles", fileName);
				failed = checkFile(location+"\\failedFiles", fileName);
				skipped = checkFile(location+"\\skippedFiles", fileName);
				
				if(isProccessed){
					if(proccessed & failed & skipped){
						System.out.println("File: "+fileName+" exists in Proccessed, Failed and Skipped folders");
						return false;
						
					}else if(proccessed & failed){
						System.out.println("File: "+fileName+" exists in both Proccessed and Failed folders");
						return false;
						
					}else if(proccessed & skipped){
						System.out.println("File: "+fileName+" exists in both Proccessed and Skipped folders");
						return false;
						
					}else if(failed & skipped){
						System.out.println("File: "+fileName+" exists in both Failed and Skipped folders");
						return false;
						
					}else if(isProccessed & failed){
						System.out.println("File: "+fileName+" has been proccessed but exist in Failed folder");
						return false;
						
					}else if(isProccessed & skipped){
						System.out.println("File: "+fileName+" has been proccessed but exist in Skipped folder");
						return false;
						
					}else if(isProccessed & proccessed){
						return true;
					}
					
				}else{
					if(proccessed & failed & skipped){
						System.out.println("File: "+fileName+" has not been proccessed but exists in Proccessed, Failed and Skipped folders");
						return false;
						
					}else if(proccessed & failed){
						System.out.println("File: "+fileName+" has not been proccessed but exists in both Proccessed and Failed folders");
						return false;
						
					}else if(proccessed & skipped){
						System.out.println("File: "+fileName+" has not been proccessed but exists in both Proccessed and Skipped folders");
						return false;
						
					}else if(failed & skipped){
						System.out.println("File: "+fileName+" has not been proccessed but exists in both Failed and Skipped folders");
						return false;
						
					}else if(proccessed){
						System.out.println("File: "+fileName+" has not been proccessed but exists in Proccessed folder");
						return false;
						
					}else if(failed){
						System.out.println("File: "+fileName+" has not been proccessed and exists in Failed folder");
						return true;
						
					}else if(skipped){
						System.out.println("File: "+fileName+" has not been proccessed and exist in Skipped folder");
						return true;
						
					}
				}
				
				break;
//			If In Parent Folder have 2 Folders
			case 2:
				
				Collections.sort(directories);
				if(directories.get(0).equalsIgnoreCase("failedFiles") && directories.get(1).equalsIgnoreCase("processedFiles")){
					
					proccessed = checkFile(location+"\\processedFiles", fileName);
					failed = checkFile(location+"\\failedFiles", fileName);
					
					if(isProccessed){
						if(proccessed & failed){
							System.out.println("File: "+fileName+" has been proccessed but exists in both Proccessed and Failed folder");
							return false;
							
						}else if(isProccessed & failed){
							System.out.println("File: "+fileName+" has been proccessed but exists in Failed folder");
							return false;
							
						}else if(proccessed){
							return true;
							
						}else{
							System.out.println("File: "+fileName+" is not present in any of the Importer folders");
							return false;
						}
						
					}else{
						if(proccessed & failed){
							System.out.println("File: "+fileName+" has not been proccessed but exists in both Proccessed and Failed folder");
							return false;
							
						}else if(proccessed){
							System.out.println("File: "+fileName+" has not been proccessed but exists in Proccessed folder");
							return false;
							
						}else if(failed){
							return true;
						}else{
							System.out.println("File: "+fileName+" is not present in any of the Importer folders");
							return false;
						}
					}
						
				}else if(directories.get(0).equalsIgnoreCase("failedFiles") && directories.get(1).equalsIgnoreCase("skippedFiles")){
					
					failed = checkFile(location+"\\failedFiles", fileName);
					skipped = checkFile(location+"\\skippedFiles", fileName);
					
					if(isProccessed){
						if(skipped & failed){
							System.out.println("File: "+fileName+" has been proccessed but exists in both Skipped and Failed folder");
							return false;
							
						}else if(failed){
							System.out.println("File: "+fileName+" has been proccessed but exists in Failed folder");
							return false;
							
						}else if(skipped){
							System.out.println("File: "+fileName+" has been proccessed but exists in Skipped folder");
							return false;
							
						}else{
							System.out.println("File: "+fileName+" is not present in any of the Importer folders");
							return false;
						}
						
					}else{
						if(skipped & failed){
							System.out.println("File: "+fileName+" has not been proccessed but exists in both Skipped and Failed folder");
							return false;
							
						}else if(failed){
							System.out.println("File: "+fileName+" has not been proccessed and found in Failed folder");
							return true;
							
						}else if(skipped){
							System.out.println("File: "+fileName+" has not been proccessed and found in Skipped folder");
							return true;
						}else{
							System.out.println("File: "+fileName+" is not present in any of the Importer folders");
							return false;
						}
					}
					
				}else if(directories.get(0).equalsIgnoreCase("processedFiles") && directories.get(1).equalsIgnoreCase("skippedFiles")){
					
					proccessed = checkFile(location+"\\processedFiles", fileName);
					skipped = checkFile(location+"\\skippedFiles", fileName);
					
					if(isProccessed){
						if(skipped & proccessed){
							System.out.println("File: "+fileName+" has been proccessed but exists in both Skipped and Proccessed folder");
							return false;
							
						}else if(skipped){
							System.out.println("File: "+fileName+" has been proccessed but exists in Skipped folder");
							return false;
							
						}else if(proccessed){
							return true;
							
						}else{
							System.out.println("File: "+fileName+" is not present in any of the Importer folders");
							return false;
						}
						
					}else{
						if(skipped & proccessed){
							System.out.println("File: "+fileName+" has not been proccessed but exists in both Skipped and Proccessed folder");
							return false;
							
						}else if(proccessed){
							System.out.println("File: "+fileName+" has not been proccessed but exists in Proccessed folder");
							return true;
							
						}else if(skipped){
							System.out.println("File: "+fileName+" has not been proccessed and found in Skipped folder");
							return true;
						}else{
							System.out.println("File: "+fileName+" is not present in any of the Importer folders");
							return false;
						}
					}
					
				}else{
					System.out.println("Irrrelevant folders found: "+directories.get(0)+", "+directories.get(1));
				}
				
				break;
				
			case 1:
				if(isProccessed){
					if(directories.get(0).equalsIgnoreCase("processedFiles")){
							proccessed = checkFile(location, fileName);
							return true;
								
					}
					else if(directories.get(0).equalsIgnoreCase("failedFiles")){
						//failed = checkFile(location, fileName);
						System.out.println("The file is Processed but only failed Folder is exist");
						return false;
					}
					else if(directories.get(0).equalsIgnoreCase("skippedFiles")){
						//skipped = checkFile(location, fileName);
						System.out.println("The file is Processed but only Skipped Folder is exist");
						return false;
					}
					else{
						System.out.println("Invalid Directory Found....");
						}
				}
				else{
					if(directories.get(0).equalsIgnoreCase("processedFiles")){
						System.out.println("file is not processed but Processed directory is exist");
						return false;
					}
					else if(directories.get(0).equalsIgnoreCase("failedFiles"))
						return true;
					
					else if(directories.get(0).equalsIgnoreCase("skippedFiles"))
						return true;
					else
						System.out.println("Invalid Directory is Found");
					
				}
			
				break;

			default:
				System.out.println("Invalid number of folders");
				break;
			}
			
		}catch (Exception e){
			System.out.println("Error while checking checking File Presence");
			e.printStackTrace();
			return false;
		}
		return false;
	}
	
//	Check the File presence in giving location 
	public boolean checkFile(String location, String fileName){
		if(new File(location+"/"+fileName).exists()){
			return true;
		}else{
			return false;
		}
	}
	
	/*
	 * boolean presence = false;
				
				if(checkFile(location+"\\proccessedFolder", fileName)){
					presence = true;
				}
				
				if(presence){
					if(checkFile(location+"\\failedFolder", fileName)){
						System.out.println("File: "+fileName+" exists in both Proccessed and Failed folders");
						flag1 = false;
					}else{
						flag1 = true;
					}
				}else{
					if(checkFile(location+"\\failedFolder", fileName)){
						presence = true;
						flag2 = true;
					}
				}
				
				if(presence & !flag1){
					if(checkFile(location+"\\skippedFolder", fileName)){
						System.out.println("File: "+fileName+" exists in Proccessed, Failed and Skipped folders");
						flag2 = false;
					}else{
						flag2 = true;
					}
				}else if(presence & flag1){
					if(checkFile(location+"\\skippedFolder", fileName)){
						System.out.println("File: "+fileName+" exists in both Proccessed and Skipped folders");
						flag1 = false;
					}
				}else if(flag2){
					if(checkFile(location+"\\skippedFolder", fileName)){
						System.out.println("File: "+fileName+" exists in both Failed and Skipped folders");
						flag1 = false;
					}
				}
	 */
}
