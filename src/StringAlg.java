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

public class StringAlg {

	int m; // pattern length
	int k = 1; // number of errors permitted
	long[] B = new long[4];
	char[] chars = new char[4];
	
	static final int max_errors = 30;
	long[] R = new long[max_errors];
	
	static final int MATCH_NOT_FOUND = 99999;
	static final char PADDING_CHAR = 'Z';
	
	// bit masks
	long aux1; 
	long aux2;
	long last;
	
	private int getCharNum( char inchar )
	{
		if( chars[0] == inchar )
		{
			return 0;
		}
		else if( chars[1] == inchar)
		{
			return 1;
		}
		else if( chars[2] == inchar)
		{
			return 2;
		}
		return 3;
	}
	
	public static String padEndOfString( String origText, int length )
	{
		if( origText.length() >= length)
		{
			return origText;
		}
		int numCharsToAdd = length - origText.length();
		String returnVal = origText;
		for( int i = 0; i < numCharsToAdd; i++)
		{
			returnVal = returnVal.concat("" + PADDING_CHAR);
		}
		return returnVal;
	}
	
	public int matchPatternWithText( String origText, boolean allowSubstitutions )
	{	
		String text = padEndOfString( origText, m);
		
		int max_err = m;
		if(max_err > max_errors)
		{
			max_err = max_errors;
		}
		
		k = 1;
		boolean match_found = false;
		while( !match_found && k < max_err )
		{
			aux1 = 1;
			last = aux1 << (m-1);
			
			R[0] = 0;
			// initialization
			for( int d=1; d<=k; d++ )
			{
				R[d] = aux1;
				aux2 = aux1;
				aux1 = (aux1 << 1) | aux2;
			}
			int position = 0;
			while( !match_found && position < m )
			{
				aux1 = R[0];
				R[0] = ((R[0] << 1) | 1) & 
				         B[ getCharNum( text.charAt(position)) ];
				
				for( int d=1; d<=k; d++)
				{
					aux2 = R[d];
					if( allowSubstitutions )
					{
						R[d] = ((R[d] << 1) & B[ getCharNum( text.charAt(position)) ])					
								| (((aux1 | R[d-1])<< 1) | 1) | aux1;
					}
					else
					{
						R[d] = ((R[d] << 1) & B[ getCharNum( text.charAt(position)) ])					
						| ((R[d-1] << 1) | 1) | aux1;
					}
					aux1 = aux2;
				}
				if( (R[k] & last) != 0)
				{
					match_found = true;					
				}
				else
				{
					position++;
				}
			}
			if( !match_found)
			{
				k++;
			}
		}
		if( match_found )
		{
			return k;
		}
		else
		{
			return MATCH_NOT_FOUND;
		} 
	}
	
	public void setPattern( String pattern, char char1, char char2, char char3)
	{
		chars[0] = char1;
		chars[1] = char2;
		chars[2] = char3; 
		
		if (pattern.length() > 63)
		{
			pattern = pattern.substring(0,62);
		}
		m = pattern.length();
		
		long aux = 1;
		for (int i = 0; i<3; i++)
		{
			B[i] = 0;
			
			for( int j = 0; j<m; j++)
			{
				if( i == getCharNum( pattern.charAt(j)))
				{
					B[i] = B[i] | aux;					
				}
				aux = aux << 1;				
			}
			aux = 1;		
		}
		B[3] = 0;
	}
}
