// CS 0401 Fall 2015
// Outline of the MyPoly class that you must implement for Assignment 5.
// I have provided some data and a couple of methods for you, plus some method headers
// for the methods called from Assig5.java.  You must implement all of those methods but 
// you will likely want to add some other "helper" methods as well, and also some new
// instance variables (esp. for Assig5B.java).

import java.util.*;
import java.awt.*;
import java.awt.geom.*;

public class MyPoly extends Polygon
{
	 // This ArrayList is how we will "display" the points in the MyPoly.  The idea is
	 // that a circle will be created for every (x,y) point in the MyPoly.  To give you
	 // a good start on this, I have implemented the constructors below.
	 private ArrayList<Ellipse2D.Double> thePoints;
	 public Color myColor;
	 private boolean highlighted;
	 private boolean fillIn = false;//Global variables used to decide
	 private int spot = -1;			//if a point will be filled or not
	 
	 // Constructors.  These should work with Assig5.java but you may want to modify
	 // them for Assig5B.java.
	MyPoly()
	{
		super();
		myColor = Color.BLACK;
		thePoints = new ArrayList<Ellipse2D.Double>();
	}

	// This constructor should be a lot of help to see the overall structure of the
	// MyPoly class and how both the inherited Polygon functionality as well as the
	// additional functionality are incorporated.  Note that the first thing done is
	// a call to super to set up the points in the "regular" Polygon.  Then the color
	// is set and the point circles are created to correspond with each point in the
	// Polygon.
	MyPoly(int [] xpts, int [] ypts, int npts, Color col)
	{
		super(xpts, ypts, npts);
		myColor = col;
		thePoints = new ArrayList<Ellipse2D.Double>();
		for (int i = 0; i < npts; i++)
		{
			int x = xpts[i];
			int y = ypts[i];
			addCircle(x, y);
		}
	}
	
	// The setFrameFromCenter() method in Ellipse2D.Double allows the circles to be
	// centered on the points in the MyPoly
	public void addCircle(int x, int y)
	{
		Ellipse2D.Double temp = new Ellipse2D.Double(x, y, 8, 8);
		temp.setFrameFromCenter(x, y, x+4, y+4);
		thePoints.add(temp);
	}
     
	public void translate(int x, int y)
	{
		// You must override this method
		super.translate(x,y);									//Works just fine for polygon class
		for(int i = 0; i<=thePoints.size()-1;i++){				//Adds change to the x and y points of the circles
			thePoints.get(i).x = (int) thePoints.get(i).x+x;
			thePoints.get(i).y = (int) thePoints.get(i).y+y;
		}	
		
	}
    
    // This method is so simple I just figured I would give it to you. 	   
	public void setHighlight(boolean b)
	{
		highlighted = b;	
	}
     
	public void addPoint(int x, int y)
	{
		// You must override this method to add a new point to the end of the
		// MyPoly.  The Polygon version works fine for the "regular" part of the
		// MyPoly but you must add the functionality to add the circle for the
		// point.
		super.addPoint(x,y);	//Again, super works fine for this.
		addCircle(x,y);			//Just use the given method and it works just fine
	}
     
	public MyPoly insertPoint(int x, int y)//Never have I been more frustrated than I was figuring my logic errors out on this part... 
	{
		// Implement this method to return a new MyPoly containing new point (x,y)
		// inserted between the two points in the MyPoly that it is "closest" to.  See
		// the getClosest() method below for help with this.
		
		int close = getClosest(x,y);
		MyPoly temp = new MyPoly();
		int [] xs = new int[npoints+1];
		int [] ys = new int[npoints+1];

		for(int i = 0; i <= xs.length-1; i++){//loop through xs array
		
			if(i == close){// if i equals the index of the closest point
				
				xs[i] = xpoints[i]; //make new points equal to the point that was here before. 
				ys[i] = ypoints[i]; 
				i++;
				
				xs[i] = x;//make next point equal to new/inserted point
				ys[i] = y;	
				i++;
				
				for(int j = i; j <= xs.length-1; j++){//make all subsequent points equal to the points after the closest point in the original array
					xs[j] = xpoints[j-1];//i.e. take the rest of the original array and copy it into the new array, except shifted one index to the right, to make room for the new point
					ys[j] = ypoints[j-1];					
				}
				break;//break after this is done, because now entire array is filled
			}
			else{//if i isnt the index of the closest point simply copy the old point into the new array
				xs[i] = xpoints[i]; 
				ys[i] = ypoints[i]; 
			}
			
		}
		
		temp = new MyPoly(xs,ys,xs.length,myColor);
		temp.setHighlight(true);
		
		return(temp);
	}
	
	// This method will return the index of the first point of the line segment that is
	// closest to the argument (x, y) point.  It uses some methods in the Line2D.Double
	// class and will be very useful when adding a point to the MyPoly.  Read through it
	// and see if you can figure out exactly what it is doing.
	public int getClosest(int x, int y)
	{
		if (npoints == 1)
			return 0;
		else
		{
			Line2D currSeg = new Line2D.Double(xpoints[0], ypoints[0], xpoints[1], ypoints[1]);
			double currDist = currSeg.ptSegDist(x, y);
			double minDist = currDist;
			int minInd = 0;
			for (int ind = 1; ind < npoints; ind++)
			{
				currSeg = new Line2D.Double(xpoints[ind], ypoints[ind],
								xpoints[(ind+1)%npoints], ypoints[(ind+1)%npoints]);
				currDist = currSeg.ptSegDist(x, y);
				if (currDist < minDist)
				{
					minDist = currDist;
					minInd = ind;
				}
			}
			
			return minInd;
			
		}
	}

	public MyPoly removePoint(int x, int y)//Basically the opposite of insert point
	{
		// Implement this method to return a new MyPoly that is the same as the
		// original but without the "point" containing (x, y).  Note that in this
		// case (x, y) is not an actual point in the MyPoly but rather a location
		// on the screen that was clicked on my the user.  If this point falls within
		// any of the point circles in the MyPoly then the point in the MyPoly that
		// the circle represents should be removed.  If the resulting MyPoly has no
		// points (i.e. the last point has been removed) then this method should return
		// null.  If (x,y) is not within any point circles in the MyPoly then the
		// original, unchanged MyPoly should be returned.
		
		boolean containCheck = false;
		MyPoly temp = new MyPoly();
		int [] xs = new int[npoints-1];
		int [] ys = new int[npoints-1];
		
		for(int i = 0; i <= thePoints.size()-1; i++){//loop through xs array
			containCheck = thePoints.get(i).contains(x,y);
			if(containCheck == true){
				if(thePoints.size() == 1){
					return(null);
				}
				break;
			}
		}
		
		if(containCheck == false){
			return(this);
		}

		for(int i = 0; i <= xs.length-1; i++){//loop through xs array
			containCheck = thePoints.get(i).contains(x,y);
			if(containCheck){
				
				xs[i] = xpoints[i+1];//skip past the unwanted point
				ys[i] = ypoints[i+1]; 
				i++;
				
				for(int j = i; j <= xs.length-1; j++){//make all subsequent points equal to the points after the closest point in the original array
					xs[j] = xpoints[j+1];//i.e. take the rest of the original array and copy it into the new array, but move past the unwanted point
					ys[j] = ypoints[j+1];					
				}

				temp = new MyPoly(xs,ys,xs.length,myColor);
				temp.setHighlight(true);
				return(temp);
		
			}
			else{//if i isnt the index of the closest point simply copy the old point into the new array
				xs[i] = xpoints[i]; 
				ys[i] = ypoints[i]; 
			}
		}
		temp = new MyPoly(xs,ys,xs.length,myColor);
		temp.setHighlight(true);
		return(temp);	
	}

	public boolean contains(int x, int y)
	{
		// Override this method. The Polygon contains() method works fine as long as
		// the Polygon has 3 or more points.  You will override the method so that if
		// a MyPoly has only 2 points or 1 point it will still work.  Think about how
		// you can do this.
		boolean bool = false; 
		
		//click inside the circle for it to work
		if(thePoints.size()==1){
			for(int i = 0; i<=thePoints.size()-1;i++){
				bool = thePoints.get(i).contains(x,y);
			}
			
		}
		
		//Allows user to click around the line, not just on it.
		else if(thePoints.size()==2){
			boolean ptContain = false;
			for(int i = 0; i<=thePoints.size()-1;i++){
				ptContain = thePoints.get(i).contains(x,y);
				if(ptContain){
					break;
				}
			}
			Rectangle2D tempRec = getBounds();
			
			for(int i = 0; i<=thePoints.size()-1;i++){
				ptContain = thePoints.get(i).contains(x,y);
				if(ptContain){
					break;
				}
				ptContain = tempRec.contains(x,y);
				if(ptContain){
					break;
				}
			}
			
			bool = (super.contains(x,y) || ptContain);
		}
		
		//For 3+ points (a polygon)... just click on one of the points or inside it.
		else{
			boolean ptContain = false;
			for(int i = 0; i<=thePoints.size()-1;i++){
				ptContain = thePoints.get(i).contains(x,y);
				if(ptContain){
					break;
				}
			}
			
			bool = (super.contains(x,y) || ptContain);
		}
		return(bool);
	}
	
	public void containFillIn(int x, int y){//Used to set global variables that will be used to fill in points or not.
		
		boolean bool = false;
		
		for(int i = 0; i<=thePoints.size()-1;i++){
			bool = thePoints.get(i).contains(x,y);
			if(bool){
				spot = i;
			}
		}
		if(spot!=-1){
			fillIn = true;
		}
	}
	
	public void draw(Graphics2D g)
	{
		// Implement this method to draw the MyPoly onto the Graphics2D argument g.
		// See MyRectangle2D.java for a simple example of doing this.  In the case of
		// this MyPoly class the method is more complex, since you must handle the
		// special cases of 1 point (draw only the point circle), 2 points (drow the
		// line) and the case where the MyPoly is selected.  You must also use the
		// color of the MyPoly in this method.
	
		if(highlighted){//If the object is highlighted, just draw things 
			g.setColor(myColor);
			g.draw(this);
			for(int i = 0; i<=thePoints.size()-1;i++){
				g.draw(thePoints.get(i));
			}
			if(fillIn&&spot!=-1){//if these were set away from their originals, fill in the spot
				g.setColor(myColor);
				g.fill(thePoints.get(spot));
				fillIn=false;
				spot=-1;
			}
		}
		//if we aren't highlighted
		else{
			if(npoints==1){
				g.setColor(myColor);
				g.draw(thePoints.get(0));
			}
			else if(npoints==2){
				g.setColor(myColor);
				g.draw(this);
			}
			else{
				g.setColor(myColor);
				g.fill(this);
			}
		}		
	}
	  
	public String fileData()
	{
		// Implement this method to return a String representation of this MyPoly
		// so that it can be saved into a text file.  This should produce a single
		// line that is formatted in the following way:
		// x1:y1,x2:y2,x3:y3, ... , |r,g,b
		// Where the points and the r,g,b values are separated by a vertical bar.
		// For two examples, see A5snap.htm and A5Bsnap.htm.
		// Look at the Color class to see how to get the r,g,b values.
		
		
		//Basically just a toString() method. Nothing crazy here
		StringBuilder s = new StringBuilder();
		
		for(int i = 0; i<=npoints-1;i++){
			int temp1 = xpoints[i];
			int temp2 = ypoints[i];
			
			s.append(temp1);
			s.append(",");
			s.append(temp2);
			if(i!=npoints-1){
				s.append(":");
			}
		}
		s.append("|");
		s.append(myColor.getRed()+","+myColor.getGreen()+","+myColor.getBlue());
		
		return(s.toString());
	}

	// These methods are also so simple that I have implemented them.
	public void setColor(Color newColor)
	{
		myColor = newColor;
	}	
	
	public Color getColor()
	{
		return myColor;
	}			  
}