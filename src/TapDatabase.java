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

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotOpenException;

public class TapDatabase {

	static final String SONG_RECORDSTORE = "songs";
	static final String PHONE_RECORDSTORE = "phone";
	RecordStore songRs;
	RecordStore phoneRs;
	
	RecordEnumeration songEnum;
	
	public class SongRecord {
		public int songId;
		public int phoneId;
		public String songName;
		public String contour;
		public String longShort;
		public String phrase;
		public SongRecord( int _songId, int _phoneId, String _songName, String _contour, String _longShort, String _phrase )
		{
			songId = _songId;
			phoneId = _phoneId;
			songName = _songName;
			contour = _contour;
			longShort = _longShort;
			phrase = _phrase;
		}
	}
	
	public class PhoneRecord {
		public String personName;
		public String phoneNumber;
		public PhoneRecord( String _personName, String _phoneNumber)
		{			
			personName = _personName;
			phoneNumber = _phoneNumber;
		}
	}
	
	public TapDatabase() throws RecordStoreException
	{
		songRs = RecordStore.openRecordStore(SONG_RECORDSTORE, true);
		phoneRs = RecordStore.openRecordStore(PHONE_RECORDSTORE, true);
	}
	
	public String stripBars( String input)
	{
		return input.replace('|', ' ');
	}
	
	// returns Song ID
	public int addNewSong( String songName, int phoneId, String contour, String longShort, String phrase) 
	throws RecordStoreFullException, RecordStoreException
	{
		String strData = stripBars(songName) + '|' + phoneId + '|' + contour + '|' + longShort + '|' + phrase;
		byte[] byteData = strData.getBytes();
		return songRs.addRecord(byteData, 0, byteData.length);
	}
	
	// returns Phone ID
	public int addNewPhoneNumber( String personName, String phoneNumber)
	throws RecordStoreFullException, RecordStoreException
	{
		String strData = stripBars(personName) + '|' + stripBars(phoneNumber);
		byte[] byteData = strData.getBytes();
		return phoneRs.addRecord(byteData, 0, byteData.length);
	}
	
	/*public SongRecord getSongRecord( int songId )
	{
	}*/
	
	public SongRecord parseSongRecord( int songId, byte[] data)
	{
		String songString = new String(data);
		
		int pos1 = songString.indexOf('|');
		int pos2;
		
		String songName = songString.substring(0, pos1);
		
		pos2 = songString.indexOf('|', pos1+1);
		
		String phoneIdString = songString.substring(pos1+1, pos2);
		int phoneId = Integer.valueOf(phoneIdString).intValue();
		
		pos1 = pos2;
		pos2 = songString.indexOf('|', pos1+1);		
		
		String contour = songString.substring(pos1+1, pos2);
		
		pos1 = pos2;
		pos2 = songString.indexOf('|', pos1+1);
		
		String longShort = songString.substring(pos1+1, pos2);
		
		pos1 = pos2;
		
		String phrase = songString.substring(pos1+1);
		
		return new SongRecord( songId, phoneId, songName, contour, longShort, phrase);
	}
	
	public PhoneRecord parsePhoneRecord( byte[] data)
	{
		String songString = new String(data);
		int pos1 = songString.indexOf('|');

		String personName = songString.substring(0, pos1);
		
		String phoneNumber = songString.substring(pos1+1);
		
		return new PhoneRecord( personName, phoneNumber);
	}
	
	public SongRecord getFirstSong()
	{
		try
		{
			songEnum = null;
			songEnum = songRs.enumerateRecords(null, null, false);			
		}
		catch (RecordStoreNotOpenException e)
		{
			return null;
		}
		return getNextSong();
	}
	public boolean hasAnotherSong()
	{
		return songEnum.hasNextElement();
	}
	public SongRecord getNextSong()
	{
		if( ! songEnum.hasNextElement())
		{
			return null;
		}
		int recordId;
		byte[] data;
		try 
		{
			recordId = songEnum.nextRecordId();
			data = songRs.getRecord(recordId);
		} 
		catch (InvalidRecordIDException e)
		{
			return null;
		}
		catch (RecordStoreException e)
		{
			return null;
		}
		
		return parseSongRecord( recordId, data );
	}
	public PhoneRecord getPhoneRecord( int phoneId )
	{
		byte[] data;
		try
		{
			data = phoneRs.getRecord(phoneId);
		}
		catch( InvalidRecordIDException e )
		{
			return null;
		}
		catch( RecordStoreException e )
		{
			return null;
		}
		return parsePhoneRecord( data);
	}
	public int countSongs()
	{
		int count;
		
		try
		{
			count = songRs.getNumRecords();
		}
		catch (RecordStoreNotOpenException e)
		{
			count = 0;
		}
		return count;
	}
}
