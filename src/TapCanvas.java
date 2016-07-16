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

import java.util.Date;
import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

public class TapCanvas extends Canvas
{
     TapResult parent;
     static final int TAP_WAIT_START = 0;
     static final int TAP_IN_PROGRESS = 1;
     static final int TAP_DONE = 2;
     
     // maximum number of keystrokes to allow a second button to be selected for tapping
     static final int TWO_BUTTON_THRESHOLD = 5; 
     
     int state = TAP_WAIT_START;
     boolean isTapPressed = false;
     long tapStartTime;
     public Vector keyDownList = new Vector();
     
     int tapKeyCode;
     int tapSecondButtonKeyCode;
     
     boolean trainingMode = false;
     
     boolean twoButtons = false;
     int numKeystrokes = 0;

     int numberOfKeysDown = 0;
     
     public TapCanvas(TapResult theparent)
     {
          parent = theparent;
     }

     public void resetState()
     {
          state = TAP_WAIT_START;
          trainingMode = false;
     }

     public void paint(Graphics g)
     {
          // clear the screen first
          g.setColor(0xffffff);
          g.fillRect(0, 0, getWidth(), getHeight());
          g.setColor(0x000000);

          if ( state == TAP_WAIT_START )
          {
               Font f = g.getFont();
               int spacing = (int)(f.getHeight() * 1.2);
               String str1;
               String str2;
               String str3;
               String str4;
               String str5;
               String str6;
               String str7;
               String str8;
               str1 = "Start tapping";
               str2 = "the song's";
               str3 = "rhythm with one";
               str4 = "or two buttons.";
               str5 = "";
               str6 = "";
               str7 = "Press 8";
               str8 = "for menu.";
               
               int currypos = 10;
               g.drawString(str1, getWidth() / 2, currypos, Graphics.TOP | Graphics.HCENTER);
               currypos += spacing;
               g.drawString(str2, getWidth() / 2, currypos, Graphics.TOP | Graphics.HCENTER);
               currypos += spacing;
               g.drawString(str3, getWidth() / 2, currypos, Graphics.TOP | Graphics.HCENTER);
               currypos += spacing;
               g.drawString(str4, getWidth() / 2, currypos, Graphics.TOP | Graphics.HCENTER);
               currypos += (int)(f.getHeight() * 1.6);
               g.drawString(str5, getWidth() / 2, currypos, Graphics.TOP | Graphics.HCENTER);
               currypos += spacing;
               g.drawString(str6, getWidth() / 2, currypos, Graphics.TOP | Graphics.HCENTER);
               if( str7 != "" )
               {
            	   currypos += (int)(f.getHeight() * 1.6);
	               g.drawString(str7, getWidth() / 2, currypos, Graphics.TOP | Graphics.HCENTER);
	               currypos += spacing;
	               g.drawString(str8, getWidth() / 2, currypos, Graphics.TOP | Graphics.HCENTER);
               }
          }
          else
               if ( state == TAP_IN_PROGRESS )
               {
                    Font f = g.getFont();
                    int spacing = (int)(f.getHeight() * 1.2);
                    String str1;
                    String str2;
                    String str3;
                    if( numKeystrokes < TWO_BUTTON_THRESHOLD )
                    {
                    	if( ! twoButtons)
                    	{
		                    str1 = "You can tap";
		                    str2 = "with one or two";
		                    str3 = "buttons.";
                    	}
                    	else
                    	{
		                    str1 = "Tapping with";
		                    str2 = "two buttons.";
		                    str3 = "";                    		
                    	}
                    }
                    else
                    {
	                    str1 = "Press any";
	                    str2 = "other key";
	                    str3 = "when finished.";
                    }
                    int currypos = 10;
                    g.drawString(str1, getWidth() / 2, currypos, Graphics.TOP | Graphics.HCENTER);
                    currypos += spacing;
                    g.drawString(str2, getWidth() / 2, currypos, Graphics.TOP | Graphics.HCENTER);
                    currypos += spacing;
                    g.drawString(str3, getWidth() / 2, currypos, Graphics.TOP | Graphics.HCENTER);
                    if ( isTapPressed )
                    {
                         g.drawString("Tap", getWidth() / 2, getHeight() - 10, Graphics.HCENTER | Graphics.BOTTOM);
                    }
               }
               else
                    if ( state == TAP_DONE )
                    {
                         Font f = g.getFont();
                         int spacing = (int)(f.getHeight() * 1.2);
                         String str1 = "Thank you.";
                         String str2 = "Please wait.";
                         int currypos = 10;
                         g.drawString(str1, getWidth() / 2, currypos, Graphics.TOP | Graphics.HCENTER);
                         currypos += spacing;
                         g.drawString(str2, getWidth() / 2, currypos, Graphics.TOP | Graphics.HCENTER);
                    }
     }

     public void keyPressed(int keyCode)
     {
    	 numberOfKeysDown++;
    	 
          Date date = new Date();
          long currentTime = date.getTime();
          date = null;
          if ( state == TAP_WAIT_START )
          {
        	  if(keyCode == KEY_NUM8)
        	  {
        		  parent.loadMenu();
        		  return;
        	  }
        	  
               
            tapStartTime = currentTime;
            state = TAP_IN_PROGRESS;
            isTapPressed = true;
            tapKeyCode = keyCode;
            twoButtons = false;
            numKeystrokes = 1;
            keyDownList.removeAllElements();

          }
          else if ( state == TAP_IN_PROGRESS )
          {
        	  if( numKeystrokes < TWO_BUTTON_THRESHOLD && keyCode != tapKeyCode )
        	  {
        		  twoButtons = true;
        		  tapSecondButtonKeyCode = keyCode;            		
        	  }
        	  
        	  if ( tapKeyCode == keyCode || (twoButtons && tapSecondButtonKeyCode == keyCode))
        	  {
        		  long tapTime = currentTime - tapStartTime;
        		  Long tapObj = new Long(tapTime);
        		  keyDownList.addElement(tapObj);
        		  tapStartTime = currentTime;
        		  isTapPressed = true;
        		  numKeystrokes++;
        	  }
        	  else if( numKeystrokes >= TWO_BUTTON_THRESHOLD)
        	  {
        		  state = TAP_DONE;
        		  long tapTime = currentTime - tapStartTime;
        		  Long tapObj = new Long(tapTime);
        		  keyDownList.addElement(tapObj);
        		  isTapPressed = false;
        		  parent.tapDone(keyDownList);
        	  }
          }
          repaint();
     }

     public void keyReleased(int keyCode)
     {
    	 numberOfKeysDown--;
          if ( tapKeyCode == keyCode || (twoButtons && tapSecondButtonKeyCode == keyCode) )
          {
               if ( state == TAP_IN_PROGRESS )
               {
                    if( numberOfKeysDown == 0 )
                	{
                    	isTapPressed = false;
                    	repaint();
                	}                    
               }
          }
     }
}