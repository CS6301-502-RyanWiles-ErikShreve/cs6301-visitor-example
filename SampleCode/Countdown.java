//
//
// Countdown Applet
//
// Counts down to a paricular time/date
//
// Written by             : David Reilly
// Release details        : Public domain source/binary
// Last modification date : November 21, 1997
//

import java.applet.*;
import java.awt.*;
import java.util.Date;

// Countdown class
public class Countdown extends Applet implements Runnable
{
	// m_Countdown is the Thread object for the applet
	private Thread	 m_Countdown = null;

	// m_Date is the date to which applet will countdown
	private Date m_Date, m_Date2;

	// String which shall be displayed to user
	private String foo, countString;

	private int a = -1;
	
	private Object o = null;

	// PARAMETER SUPPORT:
	private String m_CountdownDate = "January 1, 2000";
	private final String PARAM_CountdownDate = "CountdownDate";

	// Countdown Class Constructor
	public Countdown()
	{
		super();
		
		switch(a)
		{
		case -1:
			break;
		}
		
		if ( o instanceof String )
		{
			int i = 0;
		}
	}
	
	public void test(int j)
	{
		int x[] = new int[]{1, 2};
		x[0] = 1;
		
	}

	// Returns applet information
	public String getAppletInfo()
	{
		return "Name: Countdown\r\n" +
		       "Author: David Reilly\r\n" +
		       "Created with Microsoft Visual J++ Version 1.1";
	}

	// PARAMETER SUPPORT
	//		The getParameterInfo() method returns an array of strings describing
	// the parameters understood by this applet.
	public String[][] getParameterInfo()
	{
		String[][] info =
		{
			{ PARAM_CountdownDate, "String", "Date to which applet will countdown" },
		};
		return info;		
	}

	// Initialise applet
	public void init()
	{
		// The following code retrieves the value of each parameter
		// specified with the <PARAM> tag and stores it in a member
		// variable.		
		String param, param2;

		// CountdownDate: Date to which applet will countdown
		param = getParameter(PARAM_CountdownDate);

		if (param != null) {
			this.m_CountdownDate = param;
			param2 = param;
		}
		
		// Calculate date
		m_Date = new Date(this.m_CountdownDate);
		this.m_Date2 = m_Date;

		// Set background to white
		setBackground(Color.white);

		// Set foreground to red
		setForeground(Color.red);

		// Set font
		setFont ( new Font("Arial", Font.PLAIN, 15) );

		// Resize if available
    	resize(300, 100);

		// Start new thread to calculate date
		start();
	}


	// Countdown Paint Handler
	public void paint(Graphics g)
	{
		FontMetrics fm = g.getFontMetrics();
		
		int strWidth = fm.stringWidth(countString);

		int appletWidth = size().width;

		g.drawString(countString, (appletWidth - strWidth) / 2, 20);
		
	}

	// Start method to launch thread
	public void start()
	{
		// Check to see if thread still active
		if (m_Countdown == null)
		{
			// Create new thread
			m_Countdown = new Thread(this);

			// Start thread
			m_Countdown.start();
		}		
	}
	
	// Stop method to stop thread
	public void stop()
	{
		// Check to see if thread still active
		if (m_Countdown != null)
		{
			// Stop thread
			m_Countdown.stop();

			// Clear thread, so garbage collection can "clean up"
			m_Countdown = null;
		}
	}

	// Run method to recalculate the countdown string
	public void run()
	{
		for (;;)
		{
			try
			{
				Date currentDate = new Date();
				Date newDate;

				if (m_Date.after(currentDate))
				{
					// Calculate difference in dates
					long numericalDifference =  m_Date.getTime() - currentDate.getTime();

					// Divide by 1000 to find number of seconds difference
					numericalDifference = numericalDifference / 1000;
					
					// Get seconds
					int seconds = (int) numericalDifference % 60;

					// Get minutes
					numericalDifference = numericalDifference / 60;
					int minutes = (int) numericalDifference % 60;

					// Get hours
					numericalDifference = numericalDifference / 60;
					int hours = (int) numericalDifference % 24;

					// Get days
					numericalDifference = numericalDifference / 24;
					int days = (int) numericalDifference % 365;

					// Get days
					numericalDifference = numericalDifference / 365;
					int years = (int) numericalDifference;

					// Generate date string
					countString = seconds + " seconds, " +
								  minutes + " minutes, " +
								  hours   + " hours, " +
								  days    + " days, " + 
								  years   + " years remain until target.";
					
					newDate = currentDate;
				}
				else
					countString = "Countdown reached!";

				// Repaint applet canvas
				repaint();
				
				// Sleep for a second
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				// If we're interrupted, then stop thread
				stop();
			}
		}
	}
}