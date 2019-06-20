package co.cleveland.game;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class BlockManager {
	
	private final static String IMAGE_DIR = "Images/";
	  private final static int MAX_BLOCKS_LINES = 15;

	  private final static double MOVE_FACTOR = .25; 

	  private int pWidth, pHeight;    
	  private int width, height;     

	  private int imWidth, imHeight;  
	  private int numCols, numRows;  

	  private int moveSize;       

	  private boolean isMovingRight;  
	  private boolean isMovingLeft;

	  private int xMapHead;    
	  
	private ArrayList<Block> blocksList;   
	    // stores Brick objects which makes up the brick map

	@SuppressWarnings("rawtypes")
	private ArrayList[] columnBlocks;
	    // Brick objects saved in column order 
	    // (faster to search than bricksList)

	  private ImageLoader imsLoader;
	  @SuppressWarnings("rawtypes")
	private ArrayList blockImages = null;    
	         // holds all the images loaded by imsLoader
	 

	public BlockManager(int w, int h, String fnm, ImageLoader il)
	  {
	    pWidth = w; pHeight = h;
	    imsLoader = il;

	    blocksList = new ArrayList<Block>();
	    loadBlocksFile(fnm);
	    initBlocksInfo();
	    createColumns();

	    moveSize = (int)(imWidth * MOVE_FACTOR);
	    if (moveSize == 0) {
	      System.out.println("moveSize cannot be 0, setting it to 1");
	      moveSize = 1;
	    }

	    isMovingRight = false;   // no movement at start
	    isMovingLeft = false;
	    xMapHead = 0;
	  }  

	  private void loadBlocksFile(String fnm){ 
	    String imsFNm = IMAGE_DIR + fnm;
	    System.out.println("Reading bricks file: " + imsFNm);

	    int numStripImages = -1;
	    int numBlocksLines = 0;
	    try {
	      InputStream in = this.getClass().getResourceAsStream(imsFNm);
	      BufferedReader br = new BufferedReader( new InputStreamReader(in));
	      String line;
	      char ch;
	      while((line = br.readLine()) != null) {
	        if (line.length() == 0)  
	          continue;
	        if (line.startsWith("//"))   
	          continue;
	        ch = Character.toLowerCase( line.charAt(0) );
	        if (ch == 's')  
	          numStripImages = getStripImages(line);
	        else {  
	          if (numBlocksLines > MAX_BLOCKS_LINES) 
	            System.out.println("Max reached, skipping bricks line: " + line);
	          else if (numStripImages == -1) 
	            System.out.println("No strip image, skipping bricks line: " + line);
	          else {
	            storeBlocks(line, numBlocksLines, numStripImages);
	            numBlocksLines++;
	          }
	        }
	      }
	      br.close();
	    } 
	    catch (IOException e) 
	    { System.out.println(e);
	      System.exit(1);
	    }
	  }  

	  private int getStripImages(String line){ 
		StringTokenizer tokens = new StringTokenizer(line);
	    if (tokens.countTokens() != 3) {
	      System.out.println("Wrong no. of arguments for " + line);
	      return -1;
	    }
	    else {
	      tokens.nextToken();    // skip command label
	      System.out.print("Bricks strip: ");

	      String fnm = tokens.nextToken();
	      int number = -1;
	      try {
	        number = Integer.parseInt( tokens.nextToken() );
	        imsLoader.loadStripImages(fnm, number);   // store strip image
	        blockImages = imsLoader.getImages( getPrefix(fnm) ); 
	            // store all the images in a global array
	      }
	      catch(Exception e)
	      { System.out.println("Number is incorrect for " + line);  }

	      return number;
	    }
	  }  // end of getStripImages()


	  private String getPrefix(String fnm)
	  // extract name before '.' of filename
	  {
	    int posn;
	    if ((posn = fnm.lastIndexOf(".")) == -1) {
	      System.out.println("No prefix found for filename: " + fnm);
	      return fnm;
	    }
	    else
	      return fnm.substring(0, posn);
	  } // end of getPrefix()



	  
	private void storeBlocks(String line, int lineNo, int numImages){
	    int imageID;
	    for(int x=0; x < line.length(); x++) {
	      char ch = line.charAt(x);
	      if (ch == ' ')   
	        continue;
	      if (Character.isDigit(ch)) {
	        imageID = ch - '0';    
	        if (imageID >= numImages)
	          System.out.println("Image ID " + imageID + " out of range");
	        else  
	          blocksList.add(new Block(imageID, x, lineNo));
	      }
	      else
	        System.out.println("Brick char " + ch + " is not a digit");
	    }
	  } 

	  private void initBlocksInfo(){
	    if (blockImages == null) {
	      System.out.println("No bricks images were loaded");
	      System.exit(1);
	    }
	    if (blocksList.size() == 0) {
	      System.out.println("No bricks map were loaded");
	      System.exit(1);
	    }

	    BufferedImage im = (BufferedImage) blockImages.get(0);
	    imWidth = im.getWidth();
	    imHeight = im.getHeight(); 

	    findNumBlocks();
	    calcMapDimensions();
	    checkForGaps();

	    addBlockDetails();
	  } 

	  
	  private void findNumBlocks()
	  // find maximum number of bricks along the x-axis and y-axis
	  {
	    Block b;
	    numCols = 0;
	    numRows = 0;
	    for (int i=0; i < blocksList.size(); i++) {
	      b = blocksList.get(i);
	      if (numCols < b.getMapX())
	        numCols = b.getMapX();
	      if (numRows < b.getMapY())
	        numRows = b.getMapY();
	    }
	    numCols++;    
	    numRows++;
	  }  


	  private void calcMapDimensions()
	  // convert max number of bricks into max pixel dimensions
	  {
	    width = imWidth * numCols;
	    height = imHeight * numRows;

	    // exit if the width isn't greater than the panel width
	    if (width < pWidth) {
	      System.out.println("Bricks map is less wide than the panel");
	      System.exit(0);
	    }
	  }  // end of calcmapDimensions()


	  private void checkForGaps()
	  /* Check that the bottom map line (numRows-1) has a brick in every 
	     x position from 0 to numCols-1.
	     This prevents 'jack' from falling down a hole at the bottom 
	     of the panel. 
	  */
	  {
	    boolean[] hasBlock = new boolean[numCols];
	    for(int j=0; j < numCols; j++)
	      hasBlock[j] = false;

	    Block b;
	    for (int i=0; i < blocksList.size(); i++) {
	      b = blocksList.get(i);
	      if (b.getMapY() == numRows-1)
	        hasBlock[b.getMapX()] = true;   
	    }

	    for(int j=0; j < numCols; j++)
	      if (!hasBlock[j]) {
	        System.out.println("Gap found in bricks map bottom line at position " + j);
	        System.exit(0);
	      }
	  }  


	  private void addBlockDetails(){
	    Block b;
	    BufferedImage im;
	    for (int i=0; i < blocksList.size(); i++) {
	      b = blocksList.get(i);
	      im = (BufferedImage) blockImages.get( b.getImageID());
	      b.setImage(im);
	      b.setLocY(pHeight, numRows);
	    }
	  }  

	  @SuppressWarnings({ "rawtypes", "unchecked" })
	private void createColumns(){
	    columnBlocks = new ArrayList[numCols];
	    for (int i=0; i < numCols; i++)
	      columnBlocks[i] = new ArrayList();

	    Block b;
	    for (int j=0; j < blocksList.size(); j++) {
	      b = blocksList.get(j);
	      columnBlocks[ b.getMapX() ].add(b);    
	    }
	  } 

	  public void moveRight()
	  { isMovingRight = true;
	    isMovingLeft = false;
	  }  

	  public void moveLeft()
	  { isMovingRight = false;
	    isMovingLeft = true;
	  }

	  public void stayStill()
	  { isMovingRight = false;
	    isMovingLeft = false;
	  }
	  
	  public void update(){
	    if (isMovingRight){
	      xMapHead = (xMapHead + moveSize) % width;
	    }
	    else if (isMovingLeft){
	      xMapHead = (xMapHead - moveSize) % width;
	    }
	  } 


	  public void display(Graphics g){
	    int bCoord = (int)(xMapHead/imWidth) * imWidth;
	    int offset;   
	    if (bCoord >= 0)
	      offset = xMapHead - bCoord;   
	    else  
	      offset = bCoord - xMapHead;   
	    // System.out.println("bCoord: " + bCoord + ", offset: " + offset);


	    if ((bCoord >= 0) && (bCoord < pWidth)) {  
	      drawBlocks(g, 0-(imWidth-offset), xMapHead, width-bCoord-imWidth);   
	      drawBlocks(g, xMapHead, pWidth, 0); 
	    }
	    else if (bCoord >= pWidth)
	      drawBlocks(g, 0-(imWidth-offset), pWidth, width-bCoord-imWidth);  
	    else if ((bCoord < 0) && (bCoord >= pWidth-width+imWidth))
	      drawBlocks(g, 0-offset, pWidth, -bCoord);		
	    else if (bCoord < pWidth-width+imWidth) { 
	      drawBlocks(g, 0-offset, width+xMapHead, -bCoord);    
	      drawBlocks(g, width+xMapHead, pWidth, 0);  
	    } 
	  } 


	  private void drawBlocks(Graphics g, int xStart, int xEnd, int xBlock){
		int xMap = xBlock/imWidth;   
	    @SuppressWarnings("rawtypes")
		ArrayList column;
	    Block b;
	    for (int x = xStart; x < xEnd; x += imWidth) {
	      column = columnBlocks[ xMap ];   
	      for (int i=0; i < column.size(); i++) {   
	         b = (Block) column.get(i);
	         b.display(g, x);   
	      }
	      xMap++;  
	    }
	  }

	  public int getBrickHeight()
	  {  return imHeight; }


	  public int findFloor(int xSprite){
	    int xMap = (int)(xSprite/imWidth);   

	    int locY = pHeight;    
	    @SuppressWarnings("rawtypes")
		ArrayList column = columnBlocks[ xMap ];

	    Block b;
	    for (int i=0; i < column.size(); i++) {
	      b = (Block) column.get(i);
	      if (b.getLocY() < locY)
	        locY = b.getLocY();   
	    }
	    return locY;
	  }  

	  public int getMoveSize()
	  {  return moveSize;  }


	  public boolean insideBlock(int xWorld, int yWorld){
	    Point mapCoord = worldToMap(xWorld, yWorld);
	    @SuppressWarnings("rawtypes")
		ArrayList column = columnBlocks[ mapCoord.x ];

	    Block b;
	    for (int i=0; i < column.size(); i++) {
	      b = (Block) column.get(i);
	      if (mapCoord.y == b.getMapY())
	        return true;
	    }
	    return false;
	  }  


	  private Point worldToMap(int xWorld, int yWorld){
	    xWorld = xWorld % width;   
	    if (xWorld < 0)            
	      xWorld += width;
	    int mapX = (int) (xWorld/imWidth);  

	    yWorld = yWorld - (pHeight-height);  
	    int mapY = (int) (yWorld/imHeight);  

	    if (yWorld < 0)   
	      mapY = mapY-1;  
	    
	    return new Point(mapX, mapY);
	  }

	  
	  public int checkBlockBase(int xWorld, int yWorld, int step)
	  {
	    if (insideBlock(xWorld, yWorld)) {
	      int yMapWorld = yWorld - (pHeight-height);
	      int mapY = (int) (yMapWorld/imHeight); 
	      int topOffset = yMapWorld - (mapY * imHeight);
	      int smallStep = step - (imHeight-topOffset);
	      return smallStep;
	    }
	    return step;   
	  }  


	  public int checkBlockTop(int xWorld, int yWorld, int step){
	    if (insideBlock(xWorld, yWorld)) {
	      int yMapWorld = yWorld - (pHeight-height);
	      int mapY = (int) (yMapWorld/imHeight); 
	      int topOffset = yMapWorld - (mapY * imHeight);
	      int smallStep = step - topOffset;
	      return smallStep;
	    }
	    return step;   
	  }  

}
