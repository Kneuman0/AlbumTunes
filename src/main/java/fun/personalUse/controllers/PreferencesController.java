package fun.personalUse.controllers;

import fun.personalUse.dataModel.PreferencesBean;
import fun.personalUse.mainAlbumTunesApp.AlbumTunesController;
import fun.personalUse.utilities.XmlUtilities;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class PreferencesController {
	


    @FXML
    private TextField 
    	imageLocTextField,
    	storageDirectoryTextField;

    @FXML
    private Button 
    	dataStoreageChangeButton,
    	imageLocChangeButton,
    	backupPlaylistDataButton;
    
    private PreferencesBean defaultPrefBean;
    
    private AlbumTunesController parentController;
    private Stage currentStage;
    
    
    public void initialize(){
    	defaultPrefBean = readInDefaultPrefBean();
    	System.out.println(defaultPrefBean.toString());
    }
    
    public void dataStoreageChangeButtonListener(){
    	
    }
    
    public void imageLocChangeButtonListener(){
    	
    }
    
    public void backupPlaylistButtonListener(){
    	
    }
    
    private PreferencesBean readInDefaultPrefBean(){
    	return XmlUtilities.readInPreferencesBean(getClass().getResourceAsStream("/resources/prefs.xml"));
    }
    
    private void writeDefaultPrefsBean(){
    	
    }
    
    public void setParentController(AlbumTunesController controller){
    	parentController = controller;
    }
    
    public void setCurrentStage(Stage currentStage){
    	this.currentStage = currentStage;
    }
    
    public ExitListener getOnExit(){
    	return new ExitListener();
    }
    
    private class ExitListener implements EventHandler<WindowEvent>{

		@Override
		public void handle(WindowEvent event) {
					
			currentStage.close();
			
		}
		
	}
    
    

}
