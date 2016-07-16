/* This code is Copyright (c) 2008 Geoff Peters 
and is released open source under the GNU Public License, with the following modifications:

1) Attribution:
Any derived works of this code such as applications, web sites, or games must contain a text credit to Geoff Peters and a clickable link to his web site, www.gpeters.com, displayed on an "About" page or screen or equivalent. That is even if the code is re-written in a different language or for a different platform.

2) Revenue Sharing:
Applications making use of this source code or dervied works are "encouraged" but not required to donate a portion of their advertising revenue or music download referral revenue to Geoff Peters, via Paypal. Geoff Peters can be contacted at geoff@gpeters.com.

3) Any modifications to this code must be made available open source, and also contain this message.

4) This code is released as-is, with no warranty, and no guarantee that it will function correctly. It is not advised to use this code for mission critical or safety-related applications.
*/



package tapToCall;

import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.Display;
import java.util.Vector;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.io.ConnectionNotFoundException;
import tapToCall.TapCanvas;
import tapToCall.TapAlgorithm;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.Form;
import tapToCall.StringAlg;
import tapToCall.TapDatabase;
import javax.microedition.lcdui.TextField;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;

interface TapResult
{
     void tapDone(Vector keyDownList);
     void loadMenu();
}

public class TapToCall extends MIDlet implements CommandListener, TapResult
{
     TapCanvas tapCanvas;
     Command tapCommand;
     Command exitCommand;
     Command addSaveCommand;
     Command cancelCommand;
     Command callCommand;
     Command modifySongsCommand;
     Command registerCommand;
     Command trainCommandOK;
     Command trainCommandItem;
     Command editCommand;
     Command editSaveCommand;

     Form saveForm;
     String contourString;
     String longShortString;
     String phraseString;
     TextField phoneNumberField;
     TextField songNameField;
     TextField personNameField;
     
     TapDatabase tapDatabase;
     SongResult searchResult;
     
     public TapToCall()
     {
          tapCanvas = new TapCanvas(this);
          tapCommand = new Command("Tap Again", Command.ITEM, 1);
          callCommand = new Command("Call", Command.OK, 1);
          exitCommand = new Command("Quit", Command.ITEM, 2);
          addSaveCommand = new Command("Save", Command.OK, 1);
          cancelCommand = new Command("Cancel", Command.CANCEL, 1);
          modifySongsCommand = new Command("Song List", Command.ITEM, 1);
          registerCommand = new Command("Register", Command.ITEM, 1);
          trainCommandOK = new Command("Train", Command.OK, 1);
          trainCommandItem = new Command("Train", Command.ITEM, 1);
          editCommand = new Command("Edit This Song", Command.ITEM, 1);
          editSaveCommand = new Command("Save", Command.OK, 1);          
          
          tapCanvas.addCommand(tapCommand);
          tapCanvas.addCommand(exitCommand);
     }

     void showError(String errMsg)
     {    	 
		  Form form1 = new Form("Error");
		  form1.append(errMsg);
		  form1.addCommand(exitCommand);
		  Display display = Display.getDisplay(this);
		  display.setCurrent(form1);
     }
     
     public void startApp()
     {               
          if( ! loadDatabase() )
          {
        	  showError("Unable to load the database.");
          }
          else
          {
        	  Display display = Display.getDisplay(this);
	          display.setCurrent(tapCanvas);
	          tapCanvas.setFullScreenMode(true);
	          tapCanvas.repaint();
          }
     }
     
     public boolean loadDatabase()
     {    	 
    	 try
    	 {
    		 tapDatabase = new TapDatabase();
    		 return true;
    	 }
    	 catch (RecordStoreException e)
    	 {
    		 tapDatabase = null;
    	 }
    	 return false;
     }

     public void destroyApp(boolean b)
     {
     }

     public void pauseApp()
     {
     }

     public void commandAction(Command com, Displayable dis)
     {
          if ( com == tapCommand || com == cancelCommand)
          {
               tapCanvas.resetState();
               tapCanvas.setFullScreenMode(true);
               tapCanvas.setCommandListener(null);
               
               Display display = Display.getDisplay(this);
               display.setCurrent(tapCanvas);
               
               tapCanvas.repaint();
          }
          else if ( com == exitCommand )
		   {
        	  notifyDestroyed();
		   }
          else if( com == addSaveCommand )
          {
        	  int phoneId;
        	  try
        	  {
        		  phoneId = tapDatabase.addNewPhoneNumber(personNameField.getString(), phoneNumberField.getString()); 
            	  tapDatabase.addNewSong(songNameField.getString(), phoneId, contourString, longShortString, phraseString);  
        	  }
        	  catch (RecordStoreFullException e)
        	  {
        		  showError("Sorry, unable to save. The record store is full.");
        		  return;
        	  }
        	  catch (RecordStoreException e)
        	  {
        		  showError("Unable to save, sorry.");
        		  return;
        	  }
        	          	  
        	  Form songAddedForm = new Form("Song Added");
        	  songAddedForm.append("Thank you, your song was added. You can now tap " + songNameField.getString() + 
        			  " to call " + personNameField.getString() + " " + phoneNumberField.getString());
        	  songAddedForm.addCommand(tapCommand);
        	  songAddedForm.addCommand(exitCommand);
        	  songAddedForm.setCommandListener(this);
              
              Display display = Display.getDisplay(this);
              display.setCurrent(songAddedForm);            
          }
          else if( com == callCommand)
          {
    		 if( searchResult != null && searchResult.phoneRecord.phoneNumber != null)
    		 {
				   // determine the number and call it
				   boolean needToQuit = false;
				   try
				   {
				        needToQuit = platformRequest("tel:" + searchResult.phoneRecord.phoneNumber);
				   }
				   catch (ConnectionNotFoundException e)
				   {
				   }
				   if ( needToQuit )
				   {
					   notifyDestroyed();
				   }
    		 }
          }
          else if( com == trainCommandOK || com == trainCommandItem )
          {
        	  // display training form
        	  saveForm = new Form("Add Song");
        	  songNameField = new TextField("Song Name", "", 30, TextField.ANY);        	  
        	  phoneNumberField = new TextField("Phone No.", "", 30, TextField.PHONENUMBER);
        	  personNameField = new TextField("Person Name", "", 30, TextField.ANY);
        	  saveForm.append(songNameField);        	  
        	  saveForm.append(phoneNumberField);
        	  saveForm.append(personNameField);
        	  saveForm.addCommand(addSaveCommand);
        	  saveForm.setCommandListener(this);
              
              Display display = Display.getDisplay(this);
              display.setCurrent(saveForm);    
          }
     }
     
     public void loadMenu()
     {
		  Form mainMenuForm = new Form("Main Menu");
		  mainMenuForm.append("Welcome to TapToCall 1.0, created by Geoff Peters. This evaluation copy will expire in 30 days. Please visit www.gpeters.com and register using code 1923232.");
		  mainMenuForm.addCommand(tapCommand);
		  mainMenuForm.addCommand(modifySongsCommand);
		  mainMenuForm.addCommand(registerCommand);
		  mainMenuForm.addCommand(exitCommand);
		  mainMenuForm.setCommandListener(this);
		  
		  Display display = Display.getDisplay(this);
		  display.setCurrent(mainMenuForm);        
     }
     
     public class SongResult {
    	 TapDatabase.PhoneRecord phoneRecord;
    	 String songName;
    	 public SongResult(TapDatabase.PhoneRecord _phoneRecord, String _songName )
    	 {
    		 phoneRecord = _phoneRecord;
    		 songName = _songName;
    	 }
     }
     
     // returns phone number
     protected SongResult searchSongDatabase( String contourString, String longShortString, String phraseString)
     {

         StringAlg contourAlg = new StringAlg();
         contourAlg.setPattern(contourString, 's', 'u', 'd');
         
         StringAlg longShortAlg = new StringAlg();
         longShortAlg.setPattern(longShortString, 'L', 'A', 'S');         
         
         StringAlg phraseAlg = new StringAlg();
         phraseAlg.setPattern(phraseString, '1', 'p', 'Q');  
         
         int minContour = StringAlg.MATCH_NOT_FOUND;
         int minLongShort = StringAlg.MATCH_NOT_FOUND;
         int minPhrase = StringAlg.MATCH_NOT_FOUND;
         String minSongName = null;
         int minPhoneId = 0;
               
         TapDatabase.SongRecord songRecord = tapDatabase.getFirstSong();
         while(songRecord != null)
         {
             int contDistance = contourAlg.matchPatternWithText(songRecord.contour, true);
             if( contDistance < minContour )
             {
            	 int longShortDistance = longShortAlg.matchPatternWithText(songRecord.longShort, true);
            	 int phraseDistance = phraseAlg.matchPatternWithText(songRecord.phrase, false);
            	 minSongName = songRecord.songName;
            	 minPhoneId = songRecord.phoneId;
            	 minContour = contDistance;
            	 minLongShort = longShortDistance;
            	 minPhrase = phraseDistance;
             }
             else if( contDistance == minContour )
             {
            	 int longShortDistance = longShortAlg.matchPatternWithText(songRecord.longShort, true);
            	 if( longShortDistance < minLongShort )
            	 {
            		 int phraseDistance = phraseAlg.matchPatternWithText(songRecord.phrase, false);
                	 minSongName = songRecord.songName;
                	 minPhoneId = songRecord.phoneId;
                	 minContour = contDistance;
                	 minLongShort = longShortDistance;
                	 minPhrase = phraseDistance;
            	 }
            	 else if( longShortDistance == minLongShort)
            	 {
            		 int phraseDistance = phraseAlg.matchPatternWithText(songRecord.phrase, false);
                	 if( phraseDistance < minPhrase )
                	 {
                		 minSongName = songRecord.songName;
                    	 minPhoneId = songRecord.phoneId;
                    	 minContour = contDistance;
                    	 minLongShort = longShortDistance;
                    	 minPhrase = phraseDistance;		 
                	 }
            	 }
             }
             songRecord = tapDatabase.getNextSong();
         }
         if( minSongName != null)
         {
        	 TapDatabase.PhoneRecord phone = tapDatabase.getPhoneRecord(minPhoneId);
        	 return new SongResult(phone, minSongName);
         }        
         return null;
     }
     
     public void tapDone(Vector keyDownList)
     {
    	 Vector durationValues = TapAlgorithm.normalize_duration_values(keyDownList);
         contourString = TapAlgorithm.determine_contour_string(durationValues);
         longShortString = TapAlgorithm.determine_long_short_string(durationValues);
         phraseString = TapAlgorithm.determine_phrase_string(durationValues);
         
		 searchResult = searchSongDatabase( contourString, longShortString, phraseString);
		 
		 if( searchResult == null)
		 {
			 Form noSongForm = new Form("No Song Found");
			 noSongForm.append("Sorry, no matching song was found. You may need to train a new song first. There are " 
            		 + tapDatabase.countSongs() + " songs in the database.");
			 noSongForm.setCommandListener(this);
			 noSongForm.addCommand(trainCommandOK);
			 noSongForm.addCommand(tapCommand);    			 
			 noSongForm.addCommand(exitCommand);
             
             Display display = Display.getDisplay(this);
             display.setCurrent(noSongForm);
		 }
		 else
		 {
			 // a song was found
             StringItem outStrItem = new StringItem("Song", searchResult.songName);
             StringItem outStrItem2 = new StringItem("Person", searchResult.phoneRecord.personName);
             StringItem outStrItem3 = new StringItem("Number", searchResult.phoneRecord.phoneNumber);
             
             Form foundForm = new Form("Found Song");
             foundForm.append( outStrItem );
             foundForm.append( outStrItem2 );
             foundForm.append( outStrItem3 );
             foundForm.setCommandListener(this);
             foundForm.addCommand(callCommand);
             foundForm.addCommand(trainCommandItem);
             foundForm.addCommand(tapCommand);
             foundForm.addCommand(editCommand);
             foundForm.addCommand(exitCommand);
             
             Display display = Display.getDisplay(this);
             display.setCurrent(foundForm);
             
          }
     }
}