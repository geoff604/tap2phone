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

import java.lang.Double;
import java.util.Vector;
import java.lang.Math;

public class TapAlgorithm {

	// Determines a contour string for a midi input rhythm
	public static String determine_contour_string( Vector durationValues )
	{
		String contourString = new String("");
		
		int max = durationValues.size() - 1; 
		
		double threshold = 0.25;
		
		for( int i = 0; i < max; i++ )
		{	
			Double nextElement = (Double)durationValues.elementAt( i+1 );
			Double currElement = (Double)durationValues.elementAt( i );
			double difference =  nextElement.doubleValue() - currElement.doubleValue();
			
			if( Math.abs(difference) < threshold )
			{
				contourString = contourString.concat("s");
			}
			else if( difference < 0 )
			{
				contourString = contourString.concat("d");
			}
			else // difference > 0
			{
				contourString = contourString.concat("u");
			}
		}
		return contourString;
	}

	// Calculates the average of the values in the array
	// in the range given by the start and end indicies.
	private static double calculate_average_of_group( Vector number_array,
			int start_index, int end_index )
	{
		
		int max = number_array.size()  - 1; 
		if( !(start_index >= 0 && end_index <= max && start_index <= end_index) )
		{
			return 0;
		}

		int running_total = 0;
		for( int i = start_index; i <= end_index; i++ )
		{
			Double number = (Double)number_array.elementAt( i );
			running_total += number.doubleValue();
		}
		double average = (double)running_total / (end_index - start_index + 1.0 );

		return average;
	}

	// Determines a long-short string for a midi input rhythm.
	// Returns 0 if there were two few beats, otherwise returns 1.
	public static String determine_long_short_string( Vector durationValues )
	{
		int max = durationValues.size() - 1; 
		
		final int LONG_SHORT_WINDOW = 5;
		final int LONG_SHORT_SIDE = 2;  // LONG_SHORT_SIDE = FLOOR( LONG_SHORT_WINDOW / 2 )

		final double THRESHOLD = 0.25;

		if( max < LONG_SHORT_WINDOW - 1 )
		{
			return null;
		}

		String long_short_string = new String("");

		// do the first two beats manually because they have no values to the left of them.
		double average = calculate_average_of_group( durationValues, 0, 
				LONG_SHORT_WINDOW - 1 );
		
		// calculate all the S and L's for each beat
		for( int i = 0; i <= max; i++ )
		{	
			Double curr_value = (Double)durationValues.elementAt(i);
			if( Math.abs( curr_value.doubleValue() - average ) < THRESHOLD )
			{
				long_short_string = long_short_string.concat("A");
			}
			else if( curr_value.longValue() < average )
			{
				long_short_string = long_short_string.concat("S");
			}
			else
			{
				long_short_string = long_short_string.concat("L");
			}

			// if need to recalculate the average
			if( i >= LONG_SHORT_SIDE && i < (max - LONG_SHORT_SIDE) )
			{	
				// calculate the next average
				average = calculate_average_of_group( durationValues, 
						((i+1) - LONG_SHORT_SIDE),
						((i+1) + LONG_SHORT_SIDE) );
			}
		}

		return long_short_string;
	}

	// Determines a phrase2 string for a midi input rhythm
	public static String determine_phrase_string(Vector durationValues)
	{
		int max = durationValues.size() - 1; 
		
		final double THRESHOLD = 0.25;

		int[] weights = new int[ max+1 ];
		
		// initialize weights to zero
		for( int i = 0; i <= max; i++ )
		{	
			weights[i] = 0;
		}

		// determine local peaks
		for( int i = 0; i < max; i++ )
		{	
			Double nextVal = (Double)durationValues.elementAt( i+1 );
			Double currVal = (Double)durationValues.elementAt( i );
			double difference = nextVal.doubleValue() - currVal.doubleValue();
			
			if( Math.abs(difference) < THRESHOLD )
			{
				weights[ i+1 ] += weights[ i ];
			}
			else if( difference < 0 ) // if first note is bigger
			{
				weights[ i ] += 4;
				weights[ i+1 ] += 1;
			}
			else // difference > 0  , i.e. if second note is bigger
			{
				weights[ i ] += 3;
				weights[ i+1 ] += 2;
			}
		}
		weights[ max ] += 4;

		// calculate the string
		String phrase2_string = "";
		for( int i = 0; i <= max; i++ )
		{	
			phrase2_string = phrase2_string.concat("1");
			if( weights[i] == 6 )
			{
				phrase2_string = phrase2_string.concat("p");
			}
		}

		return phrase2_string;
	}
	
	// accepts a vector of longs, and normalizes to a vector of doubles
	public static Vector normalize_duration_values( Vector durationValues )
	{
		int size = durationValues.size();
		Vector newValues = new Vector( size );
		newValues.setSize( size );
		double total = 0;
		for( int i = 0; i < size; i++)
		{
			total += (double)((Long)durationValues.elementAt(i)).longValue();
		}
		
		double average = total / size;
		
		for( int i = 0; i < size; i++)
		{
			double oldVal = (double)((Long)durationValues.elementAt(i)).longValue();
			newValues.setElementAt( new Double( oldVal / average ), i );
		}
		return newValues;
	}
}
