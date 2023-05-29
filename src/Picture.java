import java.util.ArrayList;
import tester.*;
import javalib.impworld.*;
import javalib.worldimages.*;
import java.awt.Color;

// Represents a Pixel in an Image
interface IPixel {

  // accumulates all the pixels to the right of this one, and returns a list of
  // them
  ArrayList<Pixel> accumulateRight(ArrayList<Pixel> arr);

  // accumulates all the pixels of this pixel and the entire graph and returns it
  // in an arraylist
  ArrayList<ArrayList<Pixel>> accumulateBottom(ArrayList<ArrayList<Pixel>> arr);

  // returns the top most pixel relative to the given pixel
  Pixel findTopMost(Pixel acc);

  // returns the left most pixel of the the given pixel
  Pixel findLeftMost(Pixel acc);

  // sets a this pixel's direction to a given pixel
  void setReference(String dir, APixel n);

  // computes the energy of this pixel
  double computeEnergy();

  // gets the accumulated weight of this pixel
  double computeTotalWeight();

  // computes the seaminfo of this pixel - serves useful for recursive computation
  SeamInfo computeSeam();

  // determines if this pixel, and its right pixels are wellformed
  boolean wellFormedRow();

  // determines if this pixel and the entire network of pixels
  // is wellformed
  boolean wellFormed();

  // accumulates the seaminfo of this pixel and the ones on the right into an
  // arraylist
  ArrayList<SeamInfo> accumulateSeamInfoRight(ArrayList<SeamInfo> arr, boolean init, int idx);

  // updates all the seam info values for horizontal seams, going downwards
  ArrayList<SeamInfo> accumulateSeamInfoHorizontalDown(ArrayList<SeamInfo> arr, boolean init,
      int idx);

  // updates all the seam info values for horizontal seams, but for each column
  ArrayList<SeamInfo> accumulateSeamInfoHorizontal(ArrayList<SeamInfo> arr);

  // accumulates the seaminfo of this pixel and the entire graph into an array
  // list
  ArrayList<SeamInfo> accumulateSeamInfoBottom(ArrayList<SeamInfo> arr);

}

// abstract class representing APixel
abstract class APixel implements IPixel {
  Color color;
  APixel left;
  APixel right;
  APixel top;
  APixel bottom;
  double brightness;

  // constructor for APixel
  APixel(Color color, APixel left, APixel right, APixel top, APixel bottom) {
    this.color = color;
    this.left = left;
    this.right = right;
    this.top = top;
    this.bottom = bottom;
    this.brightness = ((color.getRed() + color.getBlue() + color.getGreen()) / 3.0) / 255.0;
  }
}

// Class representing a BorderPixel
class BorderPixel extends APixel {
  // constructor for borderpixel
  BorderPixel() {
    super(Color.BLACK, null, null, null, null);
  }

  // computes the energy of a border pixel
  public double computeEnergy() {
    return -1;
  }

  // computes the total weight of a borderpixel
  public double computeTotalWeight() {
    return -1;
  }

  // returns the seam of a borderpixel
  public SeamInfo computeSeam() {
    throw new IllegalArgumentException("no seam");
  }

  // accumulates all the pixels to the right of this one, and returns a list of
  // them
  public ArrayList<Pixel> accumulateRight(ArrayList<Pixel> arr) {
    return arr;
  }

  // accumulates all the pixels of this pixel and the entire graph and returns it
  // in an arraylist
  public ArrayList<ArrayList<Pixel>> accumulateBottom(ArrayList<ArrayList<Pixel>> arr) {
    return arr;
  }

  // returns the left most neighbor of a pixel, which is the accumulated pixel
  public Pixel findLeftMost(Pixel acc) {
    return acc;
  }

  // returns the left most neighbor of a pixel, which is the accumulated pixel
  public Pixel findTopMost(Pixel acc) {
    return acc;
  }

  // sets left, right, etc, to a given pixel
  public void setReference(String dir, APixel n) {
    this.left = this;
  }

  // determines if this row of pixels is wellformed
  public boolean wellFormedRow() {
    return true;
  }

  // determines if the entire graph of pixels is well formed
  public boolean wellFormed() {
    return true;
  }

  // updates all the seam info values for horizontal seams, going downwards
  public ArrayList<SeamInfo> accumulateSeamInfoHorizontalDown(ArrayList<SeamInfo> arr, boolean init,
      int idx) {
    return arr;
  }

  // accumulates the seaminfo of this pixel and the ones on the right into an
  // arraylist
  public ArrayList<SeamInfo> accumulateSeamInfoRight(ArrayList<SeamInfo> arr, boolean init,
      int idx) {
    return arr;
  }

  // accumulates the seaminfo of this pixel and the entire graph into an array
  // list
  public ArrayList<SeamInfo> accumulateSeamInfoBottom(ArrayList<SeamInfo> arr) {
    return arr;
  }

  // updates all the seam info values for horizontal seams, but for each column
  public ArrayList<SeamInfo> accumulateSeamInfoHorizontal(ArrayList<SeamInfo> arr) {
    return arr;
  }

}

// Represents an actual Pixel in an image, 
class Pixel extends APixel {
  double energy;
  SeamInfo seam;
  Color ogColor;

  // constructor for pixel class
  Pixel(Color color, APixel left, APixel right, APixel top, APixel bottom) {
    super(color, left, right, top, bottom);
    this.seam = new SeamInfo(this, this.energy, null);
    ogColor = this.color;
  }

  // returns a list of pixels including this and the right neighbors
  public ArrayList<Pixel> accumulateRight(ArrayList<Pixel> arr) {
    arr.add(this);
    return this.right.accumulateRight(arr);

  }

  // returns a list of all pixels in this graph of pixels
  public ArrayList<ArrayList<Pixel>> accumulateBottom(ArrayList<ArrayList<Pixel>> arr) {
    arr.add(this.accumulateRight(new ArrayList<Pixel>()));
    return this.bottom.accumulateBottom(arr);
  }

  // finds the minimum total weight pixel of the top left, top, and topright of
  // this pixel
  APixel findMinSeamAbove() {
    APixel min = this.top.left;
    if (min.computeTotalWeight() == -1
        || this.top.computeTotalWeight() < min.computeTotalWeight()) {
      min = this.top;
    }
    if (this.top.right.computeTotalWeight() != -1
        && this.top.right.computeTotalWeight() < min.computeTotalWeight()) {
      min = this.top.right;
    }
    return min;
  }

  // finds the minimum seam toward the left of this pixel
  APixel findMinSeamLeft() {
    APixel min = this.left.top;
    if (min.computeTotalWeight() == -1
        || this.left.computeTotalWeight() < min.computeTotalWeight()) {
      min = this.left;
    }
    if (this.left.bottom.computeTotalWeight() != -1
        && this.left.bottom.computeTotalWeight() < min.computeTotalWeight()) {
      min = this.left.bottom;
    }
    return min;
  }

  // returns the energy of this pixel
  public double computeEnergy() {
    return this.energy;
  }

  // returns the total accumulatedweight of this pixel
  public double computeTotalWeight() {
    return this.seam.totalWeight;
  }

  // returns the seam of this pixel
  public SeamInfo computeSeam() {
    return this.seam;
  }

  // returns the left most pixel of relative to this pixel
  public Pixel findLeftMost(Pixel acc) {
    return this.left.findLeftMost(this);
  }

  // returns the left most pixel of relative to this pixel
  public Pixel findTopMost(Pixel acc) {
    return this.top.findTopMost(this);
  }

  // sets the left, right, top, bottom of this pixel to a given
  public void setReference(String dir, APixel n) {
    if (dir.equals("t")) {
      this.top = n;
    }
    else if (dir.equals("r")) {
      this.right = n;
    }
    else if (dir.equals("l")) {
      this.left = n;
    }
    else if (dir.equals("b")) {
      this.bottom = n;
    }
    else {
      throw new IllegalArgumentException("Direction incorrect");
    }
  }

  // determines if this row of pixels is well formed
  public boolean wellFormedRow() {
    return this.left.top == this.top.left && this.right.top == this.top.right
        && this.bottom.right == this.right.bottom && this.bottom.left == this.left.bottom
        && this.right.wellFormedRow();
  }

  // determines if this graph of pixels is wellformed
  public boolean wellFormed() {
    return this.wellFormedRow() && this.bottom.wellFormed();
  }

  public ArrayList<SeamInfo> accumulateSeamInfoHorizontalDown(ArrayList<SeamInfo> arr, boolean init,
      int idx) {
    if (init) {
      arr.add(new SeamInfo(this, this.energy, null));
    }
    else {
      APixel min = this.findMinSeamLeft();
      this.seam = new SeamInfo(this, min.computeTotalWeight() + this.energy, min.computeSeam());
      arr.set(idx, this.seam);
    }
    return this.bottom.accumulateSeamInfoHorizontalDown(arr, init, idx + 1);
  }

  // accumulates the seaminfos in this row of pixels
  // init refers to the initialization of the accumulator, where the seam info
  // gets initialized to the pixel's energy
  // whereas otherwise we accumulate
  public ArrayList<SeamInfo> accumulateSeamInfoRight(ArrayList<SeamInfo> arr, boolean init,
      int idx) {
    if (init) {
      arr.add(new SeamInfo(this, this.energy, null));
    }
    else {
      APixel min = this.findMinSeamAbove();
      this.seam = new SeamInfo(this, min.computeTotalWeight() + this.energy, min.computeSeam());
      arr.set(idx, this.seam);
    }
    return this.right.accumulateSeamInfoRight(arr, init, idx + 1);
  }

  // accumulates the seam infos of an entire graph and outputs it into an array
  // list
  public ArrayList<SeamInfo> accumulateSeamInfoBottom(ArrayList<SeamInfo> arr) {
    return this.bottom.accumulateSeamInfoBottom(this.accumulateSeamInfoRight(arr, false, 0));
  }

  // updates all the seam info values for horizontal seams, but for each column
  public ArrayList<SeamInfo> accumulateSeamInfoHorizontal(ArrayList<SeamInfo> arr) {
    return this.right
        .accumulateSeamInfoHorizontal(this.accumulateSeamInfoHorizontalDown(arr, false, 0));
  }

}

// utils class 
class GridUtils {
  // initializes the relationships of the given arraylist of pixels
  // sets the pixels to refer to each other based on their position in the
  // arraylist
  void initRelation(ArrayList<ArrayList<Pixel>> arr) {
    for (int row = 0; row < arr.size(); row += 1) {
      for (int col = 0; col < arr.get(0).size(); col += 1) {
        Pixel curr = arr.get(row).get(col);

        if (col > 0) {
          curr.left = arr.get(row).get(col - 1);
        }
        if (col < arr.get(0).size() - 1) {
          curr.right = arr.get(row).get(col + 1);
        }
        if (row > 0) {
          curr.top = arr.get(row - 1).get(col);
        }
        if (row < arr.size() - 1) {
          curr.bottom = arr.get(row + 1).get(col);
        }
      }
    }
    arr.get(0).get(0).top.left = arr.get(0).get(0).left;
    arr.get(0).get(0).left.top = arr.get(0).get(0).left;
    arr.get(0).get(arr.get(0).size() - 1).top.right = arr.get(0).get(0).left;
    arr.get(0).get(arr.get(0).size() - 1).right.top = arr.get(0).get(0).left;
    arr.get(arr.size() - 1).get(0).bottom.left = arr.get(0).get(0).left;
    arr.get(arr.size() - 1).get(0).left.bottom = arr.get(0).get(0).left;
    arr.get(arr.size() - 1).get(arr.get(0).size() - 1).bottom.right = arr.get(0).get(0).left;
    arr.get(arr.size() - 1).get(arr.get(0).size() - 1).right.bottom = arr.get(0).get(0).left;
  }

  // updates the given list of pixels to the graph of the given pixel
  void updateListVertical(ArrayList<ArrayList<Pixel>> pixels, Pixel firstPixel) {
    pixels.clear();
    pixels = firstPixel.accumulateBottom(pixels);
  }

  // initializes the energy levels of a list of pixels
  void initializeEnergy(ArrayList<ArrayList<Pixel>> pixels) {
    for (ArrayList<Pixel> l : pixels) {
      for (Pixel p : l) {
        double hEnergy = (p.left.top.brightness + 2 * p.left.brightness + p.left.bottom.brightness)
            - (p.top.right.brightness + 2 * p.right.brightness + p.right.bottom.brightness);
        double vEnergy = (p.left.top.brightness + 2 * p.top.brightness + p.top.right.brightness)
            - (p.bottom.left.brightness + 2 * p.bottom.brightness + p.bottom.right.brightness);
        p.energy = Math.sqrt(Math.pow(hEnergy, 2) + Math.pow(vEnergy, 2));
      }
    }
  }

  // puts all pixels of an image into array list
  void retrievePixels(ArrayList<ArrayList<Pixel>> pixels, FromFileImage image) {
    APixel border = new BorderPixel();
    for (int i = 0; i < (int) image.getHeight(); i += 1) {
      ArrayList<Pixel> row = new ArrayList<Pixel>();
      for (int j = 0; j < (int) image.getWidth(); j += 1) {
        APixel left = border;
        APixel top = border;
        APixel bottom = border;
        APixel right = border;
        row.add(new Pixel(image.getColorAt(j, i), left, right, top, bottom));
      }
      pixels.add(row);
    }
  }

  // sets the seaminfo to default values for each pixel
  void resetSeamInfo(ArrayList<ArrayList<Pixel>> pixels) {
    // Loops through each pixel and sets seaminfo to default
    for (ArrayList<Pixel> l : pixels) {
      for (Pixel p : l) {
        p.seam.totalWeight = p.energy;
        p.seam.cameFrom = null;
      }
    }
  }

  // returns the maximum energy pixel value of the given grid of pixels
  double getMaxEnergy(ArrayList<ArrayList<Pixel>> pixels) {
    double max = 0.0;
    // Loops through each pixel and updates max energy
    for (ArrayList<Pixel> l : pixels) {
      for (Pixel p : l) {
        if (p.energy > max) {
          max = p.energy;
        }
      }
    }
    return max;
  }

  // returns the maximum totalweight of the given grid of pixels
  double getMaxTotalWeight(ArrayList<ArrayList<Pixel>> pixels) {
    double max = 0.0;
    // Loops through each pixel and updates max total weight
    for (ArrayList<Pixel> l : pixels) {
      for (Pixel p : l) {
        if (p.computeTotalWeight() > max) {
          max = p.computeTotalWeight();
        }
      }
    }
    return max;
  }

}

// represents a Grid of pixels
class Grid {
  ArrayList<ArrayList<Pixel>> pixels;
  Pixel firstPixel;

  ArrayList<SeamInfo> removedSeams;

  // constructor for Grid
  Grid(ArrayList<ArrayList<Pixel>> pixels) {
    this.pixels = pixels;
    if (pixels.size() < 1) {
      throw new IllegalArgumentException("array list given has no pixels");
    }
    this.firstPixel = pixels.get(0).get(0);
    new GridUtils().initRelation(this.pixels);
    new GridUtils().initializeEnergy(this.pixels);
    removedSeams = new ArrayList<SeamInfo>();

    if (!this.firstPixel.wellFormed()) {
      throw new IllegalArgumentException("not well formed");
    }

  }

  // renders this grid
  WorldImage render() {
    ComputedPixelImage pic = new ComputedPixelImage(this.pixels.get(0).size(), this.pixels.size());
    // Loops through each pixel and adds its attributes to the image
    for (int i = 0; i < pixels.size(); i += 1) {
      for (int j = 0; j < pixels.get(0).size(); j += 1) {
        pic.setPixel(j, i, pixels.get(i).get(j).color);
      }
    }
    return pic;
  }

  // renders a greyscale image of the energy from 0-1
  WorldImage renderGreyScale() {
    ComputedPixelImage pic = new ComputedPixelImage(this.pixels.get(0).size(), this.pixels.size());

    double max = new GridUtils().getMaxEnergy(this.pixels);
    // Loops through each pixel and adds its attributes to the image
    for (int i = 0; i < pixels.size(); i += 1) {
      for (int j = 0; j < pixels.get(0).size(); j += 1) {
        pic.setPixel(j, i,
            new Color((float) (pixels.get(i).get(j).energy / max),
                (float) (pixels.get(i).get(j).energy / max),
                (float) (pixels.get(i).get(j).energy / max)));
      }
    }

    return pic;
  }

  // renders the seam energies
  WorldImage renderSeams() {
    ComputedPixelImage pic = new ComputedPixelImage(this.pixels.get(0).size(), this.pixels.size());

    double max = 255 / new GridUtils().getMaxTotalWeight(this.pixels);
    // Loops through each pixel and adds its attributes to the image
    for (int i = 0; i < pixels.size(); i += 1) {
      for (int j = 0; j < pixels.get(0).size(); j += 1) {
        pic.setPixel(j, i,
            new Color((int) (max * pixels.get(i).get(j).computeTotalWeight()),
                (int) (max * pixels.get(i).get(j).computeTotalWeight()),
                (int) (max * pixels.get(i).get(j).computeTotalWeight())));
      }
    }

    return pic;
  }

  // returns the last row with accumulated seams
  ArrayList<SeamInfo> findVerticalSeams() {
    new GridUtils().resetSeamInfo(this.pixels);

    return this.firstPixel.bottom.accumulateSeamInfoBottom(
        this.firstPixel.accumulateSeamInfoRight(new ArrayList<SeamInfo>(), true, 0));
  }

  // returns the column with the final accumulated horizontal seams
  ArrayList<SeamInfo> findHorizontalSeams() {
    new GridUtils().resetSeamInfo(this.pixels);

    return this.firstPixel.right.accumulateSeamInfoHorizontal(
        this.firstPixel.accumulateSeamInfoHorizontalDown(new ArrayList<SeamInfo>(), true, 0));
  }

  // reinserts the last seam removed
  void reinsert(String dir) {
    SeamInfo toReinsert = removedSeams.get(removedSeams.size() - 1);
    // Loops through as long as there still is something to reinsert
    while (toReinsert != null) {
      Pixel pixelToReinsert = toReinsert.pixel;
      pixelToReinsert.top.setReference("b", pixelToReinsert);
      pixelToReinsert.bottom.setReference("t", pixelToReinsert);

      pixelToReinsert.left.setReference("r", pixelToReinsert);
      pixelToReinsert.right.setReference("l", pixelToReinsert);

      if (toReinsert.cameFrom == null) {
        Pixel tempor = toReinsert.pixel;
        if (dir.equals("h")) {
          if (tempor.top == tempor.top.top) {
            this.firstPixel = tempor;
          }
          else {
            tempor = tempor.findTopMost(tempor);
            this.firstPixel = tempor;
          }
        }
        else if (dir.equals("v")) {
          if (tempor.left == tempor.left.left) {
            this.firstPixel = tempor;
          }
          else {
            tempor = tempor.findLeftMost(tempor);
            this.firstPixel = tempor;
          }
        }
        else {
          throw new IllegalArgumentException("not real direction");
        }

      }

      toReinsert = toReinsert.cameFrom;

    }

    new GridUtils().updateListVertical(this.pixels, this.firstPixel);

    if (!this.firstPixel.wellFormed()) {
      throw new IllegalArgumentException("Not well formed");
    }

    new GridUtils().initializeEnergy(this.pixels);

  }

  // sets the pixels to their default colors
  void fixColors() {
    SeamInfo toFix = removedSeams.get(removedSeams.size() - 1);
    removedSeams.remove(removedSeams.size() - 1);
    // Loops through as long as there still is a pixel to fix
    while (toFix != null) {
      toFix.pixel.color = toFix.pixel.ogColor;
      toFix = toFix.cameFrom;
    }
    new GridUtils().initializeEnergy(this.pixels);

  }

  // returns the pixel with the least accumulated energy to remove vertically
  SeamInfo findMinSeam(boolean isHorizontal) {
    ArrayList<SeamInfo> seams = findVerticalSeams();
    if (isHorizontal) {
      seams = findHorizontalSeams();
    }
    SeamInfo min = seams.get(0);
    // Loops through each seam and updates minimum seam
    for (SeamInfo s : seams) {
      if (s.totalWeight < min.totalWeight) {
        min = s;
      }
    }
    return min;
  }

  // highlights the seam in red before removal
  void highlightSeam(boolean isHorizontal) {
    SeamInfo seamToRemove = this.findMinSeam(false);
    if (isHorizontal) {
      seamToRemove = this.findMinSeam(true);
    }
    // Loops as long as there still is a seam to highlight and remove
    while (seamToRemove != null) {
      seamToRemove.pixel.color = Color.RED;
      seamToRemove = seamToRemove.cameFrom;
    }

  }

  // removes the minimum seam
  void removeSeamVertical() {
    if (findVerticalSeams().size() > 1) {

      SeamInfo seamToRemove = this.findMinSeam(false);

      removedSeams.add(seamToRemove);

      // represents where the next pixel is to remove relative to this current, where
      // 0 : pixel to remove is above the current, 1 : pixel to remove is to the top
      // left, 2: topright
      int directionChange = 0; // 0 = top, 1 = left

      // Loops as long as there still is a seam to remove
      while (seamToRemove != null) {
        Pixel toRemove = seamToRemove.pixel;
        toRemove.left.setReference("r", toRemove.right);
        toRemove.right.setReference("l", toRemove.left);

        if (directionChange == 1) {
          if (toRemove.left == toRemove.left.left) {
            toRemove.right.setReference("b", toRemove.bottom);
            toRemove.bottom.setReference("t", toRemove.right);
          }
          else {
            toRemove.right.setReference("b", toRemove.left.bottom.right);
            toRemove.left.bottom.right.setReference("t", toRemove.right);
            toRemove.left.setReference("b", toRemove.right.bottom.left);
            toRemove.right.bottom.left.setReference("t", toRemove.left);
          }
        }
        else if (directionChange == 2) {
          if (toRemove.right == toRemove.right.right) {
            toRemove.left.setReference("b", toRemove.bottom);
            toRemove.bottom.setReference("t", toRemove.left);
          }
          else {
            toRemove.left.setReference("b", toRemove.right.bottom.left);
            toRemove.right.bottom.left.setReference("t", toRemove.left);
            toRemove.right.setReference("b", toRemove.left.bottom.right);
            toRemove.left.bottom.right.setReference("t", toRemove.right);
          }
        }

        if (seamToRemove.cameFrom != null
            && seamToRemove.cameFrom.pixel.bottom == seamToRemove.pixel.right) {
          directionChange = 2; // goes right
        }
        else if (seamToRemove.cameFrom != null
            && seamToRemove.cameFrom.pixel.bottom == seamToRemove.pixel.left) {
          directionChange = 1; // goes left
        }
        else {
          directionChange = 0;
        }

        if (seamToRemove.cameFrom == null) {
          Pixel tempor = seamToRemove.pixel;
          if (tempor.left == tempor.left.left) {
            this.firstPixel = (Pixel) tempor.right;
          }
          else {
            tempor = tempor.findLeftMost(tempor);
            this.firstPixel = tempor;
          }
        }

        seamToRemove = seamToRemove.cameFrom;
      }

      new GridUtils().updateListVertical(this.pixels, this.firstPixel);
      if (!this.firstPixel.wellFormed()) {
        throw new IllegalArgumentException("Not well formed");
      }

      new GridUtils().initializeEnergy(this.pixels);

    }
  }

  // removes a horizontal seam
  void removeSeamHorizontal() {
    if (findHorizontalSeams().size() > 1) {
      SeamInfo seamToRemove = this.findMinSeam(true);
      removedSeams.add(seamToRemove);

      // represents where the next pixel is to remove relative to this current, where
      // 0 : pixel to remove is left of current, 1 : pixel to remove is to the left
      // top, 2: left bottom
      int directionChange = 0;

      // Loops as long as there still is a seam to remove
      while (seamToRemove != null) {

        Pixel toRemove = seamToRemove.pixel;

        toRemove.top.setReference("b", toRemove.bottom);
        toRemove.bottom.setReference("t", toRemove.top);

        if (directionChange == 1) {
          // goes down
          if (toRemove.bottom == toRemove.bottom.bottom) {
            toRemove.top.setReference("r", toRemove.right);
            toRemove.right.setReference("l", toRemove.top);
          }
          else {
            // working
            toRemove.top.setReference("r", toRemove.bottom.right.top);
            toRemove.bottom.right.top.setReference("l", toRemove.top);
            toRemove.bottom.setReference("r", toRemove.top.right.bottom);
            toRemove.top.right.bottom.setReference("l", toRemove.bottom);
          }
        }

        // go top
        else if (directionChange == 2) {
          if (toRemove.top == toRemove.top.top) {
            toRemove.bottom.setReference("r", toRemove.right);
            toRemove.right.setReference("l", toRemove.bottom);
          }
          else {
            // working
            toRemove.bottom.setReference("r", toRemove.top.right.bottom);
            toRemove.top.right.bottom.setReference("l", toRemove.bottom);
            toRemove.top.setReference("r", toRemove.bottom.right.top);
            toRemove.bottom.right.top.setReference("l", toRemove.top);
          }

        }

        if (seamToRemove.cameFrom != null
            && seamToRemove.cameFrom.pixel.right == seamToRemove.pixel.top) {
          directionChange = 2; // goes up
        }
        else if (seamToRemove.cameFrom != null
            && seamToRemove.cameFrom.pixel.right == seamToRemove.pixel.bottom) {
          directionChange = 1; // goes down
        }
        else {
          directionChange = 0;
        }

        if (seamToRemove.cameFrom == null) {
          Pixel tempor = seamToRemove.pixel;
          if (tempor.top == tempor.top.top) {
            this.firstPixel = (Pixel) tempor.bottom;
          }
          else {
            tempor = tempor.findTopMost(tempor);
            this.firstPixel = tempor;
          }
        }

        seamToRemove = seamToRemove.cameFrom;
      }

      new GridUtils().updateListVertical(this.pixels, this.firstPixel);
      if (!this.firstPixel.wellFormed()) {
        throw new IllegalArgumentException("Not well formed");
      }
      new GridUtils().initializeEnergy(this.pixels);

    }
  }

}

// represents a BigBang imp world
class Picture extends World {
  Grid picture;
  FromFileImage image;
  int ticks = 0;

  boolean pause;

  boolean removeHorizontal;

  boolean oneIterationVertical;
  boolean oneIterationHorizontal;

  boolean greyScale;
  boolean seamScale;

  boolean reinsertThis;

  boolean isHighlighted;

  ArrayList<String> moves;

  // constructor for Picture
  Picture(String path) {
    this.image = new FromFileImage(path);
    ArrayList<ArrayList<Pixel>> pixels = new ArrayList<ArrayList<Pixel>>();
    new GridUtils().retrievePixels(pixels, this.image);
    this.picture = new Grid(pixels);

    moves = new ArrayList<String>();

    if ((int) (Math.random() * 2) == 1) {
      removeHorizontal = true;
    }
    else {
      removeHorizontal = false;
    }

    oneIterationVertical = false;
    oneIterationHorizontal = false;

    greyScale = false;
    seamScale = false;
    reinsertThis = false;
    isHighlighted = false;
    pause = false;
  }

  // makes the scene
  public WorldScene makeScene() {
    WorldScene test = new WorldScene((int) image.getWidth(), (int) image.getHeight());
    if (greyScale) {
      test.placeImageXY(this.picture.renderGreyScale(), (int) image.getWidth() / 2,
          (int) image.getHeight() / 2);
    }
    else if (seamScale) {
      test.placeImageXY(this.picture.renderSeams(), (int) image.getWidth() / 2,
          (int) image.getHeight() / 2);
    }
    else {
      test.placeImageXY(this.picture.render(), (int) image.getWidth() / 2,
          (int) image.getHeight() / 2);
    }

    return test;

  }

  // performs an action per-tick
  public void onTick() {
    if (pause) {
      if (reinsertThis) {

        String dir = "h";

        if (this.ticks % 2 == 0) {
          dir = moves.get(moves.size() - 1);
          picture.reinsert(dir);
          moves.remove(moves.size() - 1);
        }
        else {
          picture.fixColors();
          reinsertThis = false;
        }

        if (seamScale && ticks % 2 == 1) {
          if (moves.size() > 1) {
            dir = moves.get(moves.size() - 1);
          }
          if (dir.equals("h")) {
            picture.findHorizontalSeams();

          }
          else {
            picture.findVerticalSeams();
          }
          new GridUtils().initializeEnergy(picture.pixels);
        }

        ticks += 1;
      }

      if (oneIterationVertical) {
        if (this.ticks % 2 == 0) {
          picture.highlightSeam(false);
          isHighlighted = true;
        }
        else {
          moves.add("v");
          picture.removeSeamVertical();

          oneIterationVertical = false;
          isHighlighted = false;
        }
        ticks += 1;
      }
      else if (oneIterationHorizontal) {
        if (this.ticks % 2 == 0) {
          picture.highlightSeam(true);
          isHighlighted = true;
        }
        else {
          moves.add("h");
          picture.removeSeamHorizontal();
          oneIterationHorizontal = false;
          isHighlighted = false;
        }
        ticks += 1;
      }
    }

    else if (!pause) {

      if (removeHorizontal) {
        if (this.ticks % 2 == 0) {
          picture.highlightSeam(true);
          isHighlighted = true;
        }
        else {
          moves.add("h");
          picture.removeSeamHorizontal();
          isHighlighted = false;
        }

      }
      else {
        if (this.ticks % 2 == 0) {
          picture.highlightSeam(false);
          isHighlighted = true;
        }
        else {
          moves.add("v");
          picture.removeSeamVertical();

          isHighlighted = false;
        }
      }

      if (ticks % 2 == 1) {
        if ((int) (Math.random() * 2) == 1) {
          removeHorizontal = true;
        }
        else {
          removeHorizontal = false;
        }
      }
      ticks += 1;

    }
  }

  // responds to key events
  public void onKeyEvent(String key) {
    if (key.equals(" ")) {

      if (isHighlighted) {
        isHighlighted = false;
        onTick();
      }
      oneIterationHorizontal = false;
      reinsertThis = false;
      oneIterationVertical = false;

      pause = !pause;
      this.ticks = 0;
    }

    // removes vertical seam
    if (key.equals("v") && pause && !reinsertThis && !oneIterationHorizontal) {

      oneIterationVertical = true;
      this.ticks = 0;

    }

    // removes horizontal seam
    if (key.equals("h") && pause && !reinsertThis && !oneIterationVertical) {
      oneIterationHorizontal = true;
      this.ticks = 0;
    }

    // shows energy of each pixel
    if (key.equals("g")) {
      if (isHighlighted) {
        onTick();
        isHighlighted = false;
      }

      new GridUtils().initializeEnergy(picture.pixels);

      greyScale = !greyScale;
      seamScale = false;
    }

    // shows accumulated seam energies based on what last removal was
    if (key.equals("s")) {
      if (isHighlighted) {
        onTick();
        isHighlighted = false;
      }

      if (moves.size() > 0 && moves.get(moves.size() - 1).equals("h")) {
        picture.findHorizontalSeams();
      }
      else {
        picture.findVerticalSeams();
      }

      new GridUtils().initializeEnergy(picture.pixels);

      seamScale = !seamScale;
      greyScale = false;
    }
    
    // undo
    // only allowed to undo while it is paused, since it doesn't make sense
    // to be undoing while removing at the same time.
    if (key.equals("u") && pause && moves.size() > 0 && !oneIterationVertical
        && !oneIterationHorizontal && ticks % 2 == 0) {
      reinsertThis = true;
    }

  }
}

// represents a SeamInfo
class SeamInfo {
  Pixel pixel; // the pixel we are currently on
  double totalWeight;
  SeamInfo cameFrom; // linkedlist of seams visited so far

  // Constructor for seaminfo
  SeamInfo(Pixel pixel, double totalWeight, SeamInfo cameFrom) {
    this.pixel = pixel;
    this.totalWeight = totalWeight;
    this.cameFrom = cameFrom;
  }

}

// examples  
class ExamplesPicture {
  // test for bigbang
  void testGame(Tester t) {
    Picture g = new Picture("./balloons.jpg");
    g.bigBang((int) g.image.getWidth(), (int) g.image.getHeight(), 0.005);
  }

  APixel border = new BorderPixel();
  Pixel red = new Pixel(Color.RED, border, border, border, border);
  Pixel blue = new Pixel(Color.BLUE, border, border, border, border);
  Pixel black = new Pixel(Color.BLACK, border, border, border, border);
  Pixel yellow = new Pixel(Color.YELLOW, border, border, border, border);
  Pixel green = new Pixel(Color.GREEN, border, border, border, border);
  Pixel orange = new Pixel(Color.ORANGE, border, border, border, border);
  Pixel purple = new Pixel(Color.MAGENTA, border, border, border, border);
  Pixel gray = new Pixel(Color.gray, border, border, border, border);
  Pixel white = new Pixel(Color.WHITE, border, border, border, border);
  ArrayList<ArrayList<Pixel>> threeBy3 = new ArrayList<ArrayList<Pixel>>();
  ArrayList<Pixel> row1 = new ArrayList<Pixel>();
  ArrayList<Pixel> row2 = new ArrayList<Pixel>();
  ArrayList<Pixel> row3 = new ArrayList<Pixel>();

  ArrayList<ArrayList<Pixel>> twoBy2 = new ArrayList<ArrayList<Pixel>>();

  ArrayList<ArrayList<Pixel>> threeBy3Removed = new ArrayList<ArrayList<Pixel>>();
  ArrayList<Pixel> row1Removed = new ArrayList<Pixel>();
  ArrayList<Pixel> row2Removed = new ArrayList<Pixel>();
  ArrayList<Pixel> row3Removed = new ArrayList<Pixel>();

  // initializes examples
  void initExamples() {
    red = new Pixel(Color.RED, border, border, border, border);
    blue = new Pixel(Color.BLUE, border, border, border, border);
    black = new Pixel(Color.BLACK, border, border, border, border);
    yellow = new Pixel(Color.YELLOW, border, border, border, border);
    green = new Pixel(Color.GREEN, border, border, border, border);
    orange = new Pixel(Color.ORANGE, border, border, border, border);
    purple = new Pixel(Color.MAGENTA, border, border, border, border);
    gray = new Pixel(Color.GRAY, border, border, border, border);
    white = new Pixel(Color.WHITE, border, border, border, border);
    threeBy3 = new ArrayList<ArrayList<Pixel>>();
    row1 = new ArrayList<Pixel>();
    row2 = new ArrayList<Pixel>();
    row3 = new ArrayList<Pixel>();
    row1.add(red);
    row1.add(blue);
    row1.add(black);

    row2.add(yellow);
    row2.add(green);
    row2.add(orange);

    row3.add(purple);
    row3.add(gray);
    row3.add(white);

    threeBy3.add(row1);
    threeBy3.add(row2);
    threeBy3.add(row3);

    row1Removed.add(blue);
    row1Removed.add(black);

    row2Removed.add(yellow);
    row2Removed.add(orange);

    row3Removed.add(purple);
    row3Removed.add(white);

    threeBy3Removed.add(row1Removed);
    threeBy3Removed.add(row2Removed);
    threeBy3Removed.add(row3Removed);
  }

  // initializes a 2x2 grid
  void initExamples2() {
    red = new Pixel(Color.RED, border, border, border, border);
    blue = new Pixel(Color.BLUE, border, border, border, border);
    black = new Pixel(Color.BLACK, border, border, border, border);
    yellow = new Pixel(Color.YELLOW, border, border, border, border);
    twoBy2 = new ArrayList<ArrayList<Pixel>>();
    row1 = new ArrayList<Pixel>();
    row2 = new ArrayList<Pixel>();

    row1.add(red);
    row1.add(blue);

    row2.add(black);
    row2.add(yellow);

    twoBy2.add(row1);
    twoBy2.add(row2);

  }

  // test for initRelation
  boolean testInitRelation(Tester t) {
    this.initExamples();
    new GridUtils().initRelation(threeBy3);
    Pixel first = threeBy3.get(0).get(0);
    Pixel second = threeBy3.get(0).get(1);
    Pixel fourth = threeBy3.get(1).get(0);
    Pixel fifth = threeBy3.get(1).get(1);
    return t.checkExpect(first.right, second) && t.checkExpect(fifth.left, fourth)
        && t.checkExpect(second.bottom, fifth);

  }

  // test for initEnergy
  boolean testInitEnergy(Tester t) {
    this.initExamples();
    new GridUtils().initRelation(threeBy3);
    new GridUtils().initializeEnergy(threeBy3);
    Pixel first = threeBy3.get(0).get(0);
    Pixel second = threeBy3.get(0).get(1);
    Pixel fifth = threeBy3.get(1).get(1);
    return t.checkInexact(first.energy, 1.943, 0.001) && t.checkInexact(fifth.energy, 1.676, 0.001)
        && t.checkInexact(second.energy, 2.064, 0.001);
  }

  // test for computeEnergy
  boolean testComputeEnergy(Tester t) {
    this.initExamples();
    new GridUtils().initRelation(threeBy3);
    new GridUtils().initializeEnergy(threeBy3);
    APixel pixelBorder = new BorderPixel();
    return t.checkInexact(red.computeEnergy(), 1.943, 0.001)
        && t.checkInexact(pixelBorder.computeEnergy(), -1.0, 0.001)
        && t.checkInexact(blue.computeEnergy(), 2.064, 0.001);

  }

  // test for retrievePixels
  boolean testRetrievePixels(Tester t) {
    ComputedPixelImage computed3by3 = new ComputedPixelImage(3, 3);
    computed3by3.setColorAt(0, 0, Color.RED);
    computed3by3.setColorAt(0, 1, Color.BLUE);
    computed3by3.setColorAt(0, 2, Color.BLACK);
    computed3by3.setColorAt(1, 0, Color.YELLOW);
    computed3by3.setColorAt(1, 1, Color.GREEN);
    computed3by3.setColorAt(1, 2, Color.ORANGE);
    computed3by3.setColorAt(2, 0, Color.MAGENTA);
    computed3by3.setColorAt(2, 1, Color.GRAY);
    computed3by3.setColorAt(2, 2, Color.WHITE);
    computed3by3.saveImage("./computed3by3.jpeg");

    FromFileImage image3by3 = new FromFileImage("./computed3by3.jpeg");

    this.initExamples();

    ArrayList<ArrayList<Pixel>> fromImagePixels = new ArrayList<ArrayList<Pixel>>();

    new GridUtils().retrievePixels(fromImagePixels, image3by3);
    return t.checkExpect(fromImagePixels.get(0).get(0).color, threeBy3.get(0).get(0).color)
        && t.checkExpect(fromImagePixels.get(2).get(2).color, threeBy3.get(2).get(2).color)
        && t.checkExpect(fromImagePixels.get(1).get(1).color, threeBy3.get(1).get(1).color);
  }

  // test for updateListVertical
  boolean testUpdateListVertical(Tester t) {
    Pixel firsty = new Pixel(Color.RED, new BorderPixel(), new BorderPixel(), new BorderPixel(),
        new BorderPixel());
    Pixel firstyRight = new Pixel(Color.RED, new BorderPixel(), new BorderPixel(),
        new BorderPixel(), new BorderPixel());
    Pixel firstyBottom = new Pixel(Color.RED, new BorderPixel(), new BorderPixel(),
        new BorderPixel(), new BorderPixel());
    firsty.right = firstyRight;
    firstyRight.left = firsty;
    firsty.bottom = firstyBottom;
    firstyBottom.top = firsty;

    ArrayList<ArrayList<Pixel>> end = new ArrayList<ArrayList<Pixel>>();
    ArrayList<Pixel> row1 = new ArrayList<Pixel>();
    ArrayList<Pixel> row2 = new ArrayList<Pixel>();

    row1.add(firsty);
    row1.add(firstyRight);
    row2.add(firstyBottom);
    end.add(row1);
    end.add(row2);

    this.initExamples();
    new GridUtils().updateListVertical(threeBy3, firsty);
    return t.checkExpect(threeBy3, end);

  }

  // test for findVerticalSeams
  boolean testFindVertical(Tester t) {
    this.initExamples();
    ArrayList<SeamInfo> seams = new Grid(threeBy3).findVerticalSeams();
    return t.checkInexact(seams.get(0).pixel.computeTotalWeight(), 5.635, 0.001)
        && t.checkInexact(seams.get(1).pixel.computeTotalWeight(), 5.516, 0.001)
        && t.checkInexact(seams.get(2).pixel.computeTotalWeight(), 5.53, 0.001);
  }

  // test for findHorizontalSeams
  boolean testFindHorizontal(Tester t) {
    this.initExamples();
    ArrayList<SeamInfo> seams = new Grid(threeBy3).findHorizontalSeams();
    return t.checkInexact(seams.get(0).pixel.computeTotalWeight(), 5.217, 0.001)
        && t.checkInexact(seams.get(1).pixel.computeTotalWeight(), 6.033, 0.001)
        && t.checkInexact(seams.get(2).pixel.computeTotalWeight(), 5.422, 0.001);
  }

  // test for removeSeamVertical
  boolean testRemoveSeamVertical(Tester t) {
    this.initExamples2();
    Grid twoByTwo = new Grid(twoBy2);
    new GridUtils().initRelation(twoBy2);
    new GridUtils().initializeEnergy(twoBy2);
    twoByTwo.removeSeamVertical();

    Pixel blueRemaining = new Pixel(Color.BLUE, border, border, border, border);
    Pixel blackRemaining = new Pixel(Color.BLACK, border, border, border, border);

    ArrayList<Pixel> row1R = new ArrayList<Pixel>();
    ArrayList<Pixel> row2R = new ArrayList<Pixel>();
    row1R.add(blueRemaining);
    row2R.add(blackRemaining);

    ArrayList<ArrayList<Pixel>> gridAfterRemoval = new ArrayList<ArrayList<Pixel>>();
    gridAfterRemoval.add(row1R);
    gridAfterRemoval.add(row2R);

    new GridUtils().initRelation(gridAfterRemoval);
    new GridUtils().initializeEnergy(gridAfterRemoval);

    return t.checkExpect(twoByTwo.pixels.get(0).get(0).color, gridAfterRemoval.get(0).get(0).color)
        && t.checkExpect(twoByTwo.pixels.get(1).get(0).color, gridAfterRemoval.get(1).get(0).color);

  }

  // test for removeSeamhorizontal
  boolean testRemoveSeamHorizontal(Tester t) {
    this.initExamples2();
    Grid twoByTwo = new Grid(twoBy2);
    new GridUtils().initRelation(twoBy2);
    new GridUtils().initializeEnergy(twoBy2);
    twoByTwo.removeSeamHorizontal();

    Pixel blueRemaining = new Pixel(Color.BLUE, border, border, border, border);
    Pixel blackRemaining = new Pixel(Color.BLACK, border, border, border, border);

    ArrayList<Pixel> row1R = new ArrayList<Pixel>();
    ArrayList<Pixel> row2R = new ArrayList<Pixel>();
    row1R.add(blackRemaining);
    row1R.add(blueRemaining);

    ArrayList<ArrayList<Pixel>> gridAfterRemoval = new ArrayList<ArrayList<Pixel>>();
    gridAfterRemoval.add(row1R);

    new GridUtils().initRelation(gridAfterRemoval);
    new GridUtils().initializeEnergy(gridAfterRemoval);

    return t.checkExpect(twoByTwo.pixels.get(0).get(0).color, gridAfterRemoval.get(0).get(0).color)
        && t.checkExpect(twoByTwo.pixels.get(0).get(1).color, gridAfterRemoval.get(0).get(1).color);

  }

  // test for render
  boolean testRender(Tester t) {
    Pixel red = new Pixel(Color.RED, new BorderPixel(), new BorderPixel(), new BorderPixel(),
        new BorderPixel());
    ArrayList<Pixel> row = new ArrayList<Pixel>();
    row.add(red);
    ArrayList<ArrayList<Pixel>> pix = new ArrayList<ArrayList<Pixel>>();
    pix.add(row);
    Grid one = new Grid(pix);
    ComputedPixelImage oneRender = (ComputedPixelImage) one.render();
    return t.checkExpect(oneRender.getPixel(0, 0), Color.RED);
  }

  // test for accumulate right
  boolean testAccumulateRight(Tester t) {
    Pixel pixel1 = new Pixel(Color.RED, new BorderPixel(), new BorderPixel(), new BorderPixel(),
        new BorderPixel());
    Pixel pixel2 = new Pixel(Color.RED, new BorderPixel(), new BorderPixel(), new BorderPixel(),
        new BorderPixel());
    Pixel pixel3 = new Pixel(Color.RED, new BorderPixel(), new BorderPixel(), new BorderPixel(),
        new BorderPixel());

    pixel1.right = pixel2;
    pixel2.right = pixel3;

    ArrayList<Pixel> res = new ArrayList<Pixel>();
    res.add(pixel1);
    res.add(pixel2);
    res.add(pixel3);
    return t.checkExpect(pixel1.accumulateRight(new ArrayList<Pixel>()), res);
  }

  // test for accumulateBottom
  boolean testAccumulateBottom(Tester t) {
    Pixel pixel1 = new Pixel(Color.RED, new BorderPixel(), new BorderPixel(), new BorderPixel(),
        new BorderPixel());
    Pixel pixel2 = new Pixel(Color.RED, new BorderPixel(), new BorderPixel(), new BorderPixel(),
        new BorderPixel());
    Pixel pixel3 = new Pixel(Color.RED, new BorderPixel(), new BorderPixel(), new BorderPixel(),
        new BorderPixel());
    pixel1.bottom = pixel2;
    pixel2.bottom = pixel3;

    ArrayList<Pixel> row1 = new ArrayList<Pixel>();
    ArrayList<Pixel> row2 = new ArrayList<Pixel>();
    ArrayList<Pixel> row3 = new ArrayList<Pixel>();
    row1.add(pixel1);
    row2.add(pixel2);
    row3.add(pixel3);
    ArrayList<ArrayList<Pixel>> grid = new ArrayList<ArrayList<Pixel>>();
    grid.add(row1);
    grid.add(row2);
    grid.add(row3);

    return t.checkExpect(pixel1.accumulateBottom(new ArrayList<ArrayList<Pixel>>()), grid);
  }

  // test for computeTotalWeight
  boolean testComputeTotalWeight(Tester t) {
    APixel borderPixel = new BorderPixel();

    Pixel pixel1 = new Pixel(Color.RED, borderPixel, borderPixel, borderPixel, borderPixel);
    pixel1.seam = new SeamInfo(pixel1, 12.5, null);

    return t.checkExpect(borderPixel.computeTotalWeight(), -1.0)
        && t.checkExpect(pixel1.computeTotalWeight(), 12.5);

  }

  // test for computeSeam
  boolean testComputeSeam(Tester t) {
    APixel borderPixel = new BorderPixel();
    Pixel pixel1 = new Pixel(Color.RED, borderPixel, borderPixel, borderPixel, borderPixel);
    SeamInfo seamP1 = new SeamInfo(pixel1, 12.5, null);
    pixel1.seam = seamP1;

    Pixel seamPixel = new Pixel(Color.RED, borderPixel, borderPixel, borderPixel, borderPixel);
    seamPixel.seam = new SeamInfo(seamPixel, 13, pixel1.seam);
    return t.checkException(new IllegalArgumentException("no seam"), borderPixel, "computeSeam")
        && t.checkExpect(pixel1.computeSeam(), new SeamInfo(pixel1, 12.5, null))
        && t.checkExpect(seamPixel.computeSeam(), new SeamInfo(seamPixel, 13, seamP1));
  }

  // test for findLeftMost
  boolean testFindLeftmost(Tester t) {
    Pixel pixel1 = new Pixel(Color.RED, new BorderPixel(), new BorderPixel(), new BorderPixel(),
        new BorderPixel());
    Pixel pixel2 = new Pixel(Color.RED, new BorderPixel(), new BorderPixel(), new BorderPixel(),
        new BorderPixel());
    Pixel pixel3 = new Pixel(Color.RED, new BorderPixel(), new BorderPixel(), new BorderPixel(),
        new BorderPixel());

    pixel1.right = pixel2;
    pixel2.right = pixel3;
    pixel2.left = pixel1;
    pixel3.left = pixel2;

    return t.checkExpect(pixel3.findLeftMost(pixel3), pixel1);
  }

  // test for findTopMost
  boolean testFindTopmost(Tester t) {
    Pixel pixel1 = new Pixel(Color.RED, new BorderPixel(), new BorderPixel(), new BorderPixel(),
        new BorderPixel());
    Pixel pixel2 = new Pixel(Color.RED, new BorderPixel(), new BorderPixel(), new BorderPixel(),
        new BorderPixel());
    Pixel pixel3 = new Pixel(Color.RED, new BorderPixel(), new BorderPixel(), new BorderPixel(),
        new BorderPixel());

    pixel1.bottom = pixel2;
    pixel2.bottom = pixel3;
    pixel2.top = pixel1;
    pixel3.top = pixel2;

    return t.checkExpect(pixel3.findTopMost(pixel3), pixel1);
  }

  // test for resetSeamInfo
  boolean testResetSeamInfo(Tester t) {
    this.initExamples();
    new GridUtils().initRelation(threeBy3);
    new GridUtils().initializeEnergy(threeBy3);
    Grid three = new Grid(threeBy3);

    SeamInfo firstSeamI = three.firstPixel.seam;
    // accumulates the total weight
    three.findHorizontalSeams();

    new GridUtils().resetSeamInfo(threeBy3);
    SeamInfo firstSeamRes = threeBy3.get(0).get(0).seam;
    return t.checkExpect(firstSeamRes, firstSeamI);

  }

  // test for setReference
  boolean testSetReference(Tester t) {
    Pixel pixel1 = new Pixel(Color.RED, new BorderPixel(), new BorderPixel(), new BorderPixel(),
        new BorderPixel());

    Pixel pixel2 = new Pixel(Color.RED, new BorderPixel(), new BorderPixel(), new BorderPixel(),
        new BorderPixel());
    Pixel pixel3 = new Pixel(Color.RED, new BorderPixel(), new BorderPixel(), new BorderPixel(),
        new BorderPixel());
    Pixel pixel4 = new Pixel(Color.RED, new BorderPixel(), new BorderPixel(), new BorderPixel(),
        new BorderPixel());
    Pixel pixel5 = new Pixel(Color.RED, new BorderPixel(), new BorderPixel(), new BorderPixel(),
        new BorderPixel());

    pixel1.setReference("r", pixel2);
    pixel1.setReference("t", pixel3);
    pixel1.setReference("l", pixel4);
    pixel1.setReference("b", pixel5);

    return t.checkExpect(pixel1.right, pixel2) && t.checkExpect(pixel1.top, pixel3)
        && t.checkExpect(pixel1.left, pixel4) && t.checkExpect(pixel1.bottom, pixel5)
        && t.checkException(new IllegalArgumentException("Direction incorrect"), pixel1,
            "setReference", "a", pixel2);
  }

  // test for wellFormedrow
  boolean testWellFormedRow(Tester t) {
    Pixel pixel1 = new Pixel(Color.RED, new BorderPixel(), new BorderPixel(), new BorderPixel(),
        new BorderPixel());

    Pixel pixel2 = new Pixel(Color.RED, new BorderPixel(), new BorderPixel(), new BorderPixel(),
        new BorderPixel());
    Pixel pixel3 = new Pixel(Color.RED, new BorderPixel(), new BorderPixel(), new BorderPixel(),
        new BorderPixel());

    pixel1.top.right = pixel2;
    pixel1.right.top = pixel3;

    ArrayList<Pixel> res = new ArrayList<Pixel>();
    res.add(pixel1);
    res.add(pixel2);
    res.add(pixel3);

    APixel border = new BorderPixel();
    Pixel pixel1Correct = new Pixel(Color.RED, border, border, border, border);
    return t.checkExpect(pixel1Correct.wellFormedRow(), true)
        && t.checkExpect(pixel1.wellFormedRow(), false);
  }

  // test for wellformed
  boolean testWellFormed(Tester t) {
    this.initExamples();
    Pixel pixel1 = new Pixel(Color.RED, new BorderPixel(), new BorderPixel(), new BorderPixel(),
        new BorderPixel());

    Pixel pixel2 = new Pixel(Color.RED, new BorderPixel(), new BorderPixel(), new BorderPixel(),
        new BorderPixel());
    Pixel pixel3 = new Pixel(Color.RED, new BorderPixel(), new BorderPixel(), new BorderPixel(),
        new BorderPixel());

    pixel1.top.right = pixel2;
    pixel1.right.top = pixel3;
    return t.checkExpect(threeBy3.get(0).get(0).wellFormed(), true)
        && t.checkExpect(pixel1.wellFormed(), false);
  }

  // test for accumulateSeamInfoRight
  boolean testAccumulateSeamInfoRight(Tester t) {
    Pixel pixel13 = new Pixel(Color.RED, new BorderPixel(), new BorderPixel(), new BorderPixel(),
        new BorderPixel());
    Pixel pixel14 = new Pixel(Color.RED, new BorderPixel(), new BorderPixel(), new BorderPixel(),
        new BorderPixel());
    ArrayList<SeamInfo> res = new ArrayList<SeamInfo>();
    pixel13.right = pixel14;
    res.add(new SeamInfo(pixel13, pixel13.computeEnergy(), null));
    res.add(new SeamInfo(pixel14, pixel14.computeEnergy(), null));
    return t.checkExpect(pixel13.accumulateSeamInfoRight(new ArrayList<SeamInfo>(), true, 0), res);
  }

  // test for accumulateSeamInfoBottom
  boolean testAccumulateSeamInfoBottom(Tester t) {
    APixel borderPixel = new BorderPixel();

    Pixel pixel1 = new Pixel(Color.RED, borderPixel, borderPixel, borderPixel, borderPixel);
    pixel1.seam = new SeamInfo(pixel1, 12.5, null);

    ArrayList<SeamInfo> res = new ArrayList<SeamInfo>();
    res.add(new SeamInfo(pixel1, 0, null));
    return t.checkExpect(pixel1.bottom.accumulateSeamInfoBottom(
        pixel1.accumulateSeamInfoRight(new ArrayList<SeamInfo>(), true, 0)), res);

  }

  // test for highlightSeam
  boolean testHighlightSeam(Tester t) {
    this.initExamples();
    Grid seams = new Grid(threeBy3);
    seams.highlightSeam(false);

    SeamInfo min = seams.findMinSeam(false);
    boolean res = true;

    // Loops as long as there still is a seam to highlight - testing purposes
    while (min != null) {
      if (min.pixel.color != Color.RED) {
        res = false;
      }
      min = min.cameFrom;
    }
    return t.checkExpect(res, true);
  }

  // test for accumulateseaminforight
  boolean testAccumulateSeamInfoHorizontalDown(Tester t) {
    Pixel pixel13 = new Pixel(Color.RED, new BorderPixel(), new BorderPixel(), new BorderPixel(),
        new BorderPixel());
    Pixel pixel14 = new Pixel(Color.RED, new BorderPixel(), new BorderPixel(), new BorderPixel(),
        new BorderPixel());
    ArrayList<SeamInfo> res = new ArrayList<SeamInfo>();

    pixel13.bottom = pixel14;
    res.add(new SeamInfo(pixel13, pixel13.computeEnergy(), null));
    res.add(new SeamInfo(pixel14, pixel14.computeEnergy(), null));

    return t.checkExpect(
        pixel13.accumulateSeamInfoHorizontalDown(new ArrayList<SeamInfo>(), true, 0), res);
  }

  // test for accummulateSeamInfoHorizontalDown
  boolean testAccumulateSeamInfoHorizontal(Tester t) {
    APixel borderPixel = new BorderPixel();

    Pixel pixel1 = new Pixel(Color.RED, borderPixel, borderPixel, borderPixel, borderPixel);
    pixel1.seam = new SeamInfo(pixel1, 12.5, null);

    ArrayList<SeamInfo> res = new ArrayList<SeamInfo>();
    res.add(new SeamInfo(pixel1, 0, null));
    return t.checkExpect(pixel1.bottom.accumulateSeamInfoHorizontal(
        pixel1.accumulateSeamInfoHorizontalDown(new ArrayList<SeamInfo>(), true, 0)), res);
  }

  // tests the findMinSeam method
  boolean testFindMinSeam(Tester t) {
    this.initExamples();
    Grid three = new Grid(threeBy3);
    ArrayList<SeamInfo> seams = three.findHorizontalSeams();

    Grid threeVert = new Grid(threeBy3);
    ArrayList<SeamInfo> verticalSeams = three.findVerticalSeams();

    return t.checkExpect(three.findMinSeam(true).pixel, seams.get(0).pixel)
        && t.checkExpect(threeVert.findMinSeam(false).pixel, verticalSeams.get(1).pixel);
  }

  // tests the findMinSeamAbove method
  boolean testFindMinSeamAbove(Tester t) {
    this.initExamples();
    new GridUtils().initRelation(threeBy3);
    new GridUtils().initializeEnergy(threeBy3);
    APixel res = green.findMinSeamAbove();

    return t.checkExpect(res, red);
  }

  // tests the findMinSeamLeft method
  boolean testFindMinSeamLeft(Tester t) {
    this.initExamples();
    new GridUtils().initRelation(threeBy3);
    new GridUtils().initializeEnergy(threeBy3);
    APixel res = green.findMinSeamAbove();

    return t.checkExpect(res, red);
  }

  // tests the fixColors method
  boolean testFixColors(Tester t) {
    this.initExamples();
    new GridUtils().initRelation(threeBy3);
    new GridUtils().initializeEnergy(threeBy3);
    Grid three = new Grid(threeBy3);
    three.removeSeamHorizontal();

    three.reinsert("h");
    three.highlightSeam(true);
    Color blackColorBeforeFix = black.color;

    three.fixColors();
    Color blackAfterFix = black.color;
    return t.checkExpect(blackColorBeforeFix, Color.RED)
        && t.checkExpect(blackAfterFix, Color.BLACK);

  }

  // tests the reinsert method
  boolean testReinsert(Tester t) {
    this.initExamples();
    new GridUtils().initRelation(threeBy3);
    new GridUtils().initializeEnergy(threeBy3);
    Grid three = new Grid(threeBy3);

    Grid threeCopy = new Grid(threeBy3);

    three.removeSeamHorizontal();
    three.removeSeamVertical();
    three.removeSeamHorizontal();

    three.reinsert("h");
    three.reinsert("v");
    three.reinsert("h");

    return t.checkExpect(three.pixels, threeCopy.pixels);

  }

  // test for getMaxEnergy
  boolean testGetMaxEnergy(Tester t) {
    this.initExamples();
    this.initExamples2();
    Grid grid = new Grid(threeBy3);
    Grid grid2 = new Grid(twoBy2);

    return t.checkInexact(new GridUtils().getMaxEnergy(threeBy3), 2.64, 0.1)
        && t.checkInexact(new GridUtils().getMaxEnergy(twoBy2), 1.94, 0.1);
  }

  // test for getMaxTotalWeight
  boolean testGetMaxTotalWeight(Tester t) {
    this.initExamples();
    this.initExamples2();

    ArrayList<SeamInfo> seams3By3 = new Grid(threeBy3).findVerticalSeams();

    ArrayList<SeamInfo> seams2By2 = new Grid(twoBy2).findVerticalSeams();

    return t.checkInexact(new GridUtils().getMaxTotalWeight(threeBy3), 5.64, 0.01)
        && t.checkInexact(new GridUtils().getMaxTotalWeight(twoBy2), 3.43, 0.01);

  }

  // test for renderGreyScale
  boolean testRenderGreyScale(Tester t) {
    this.initExamples();
    this.initExamples2();

    Grid grid2By2 = new Grid(twoBy2);

    ComputedPixelImage greyScale2By2 = new ComputedPixelImage(2, 2);

    greyScale2By2.setPixel(0, 0, new Color((float) 0.766965, (float) 0.766965, (float) 0.766965));
    greyScale2By2.setPixel(1, 0, new Color((float) 0.766965, (float) 0.766965, (float) 0.766965));
    greyScale2By2.setPixel(0, 1, new Color((float) 1.0, (float) 1.0, (float) 1.0));
    greyScale2By2.setPixel(1, 1,
        new Color((float) 0.54232615, (float) 0.54232615, (float) 0.54232615));

    Grid grid3By3 = new Grid(threeBy3);

    ComputedPixelImage greyScale3By3 = new ComputedPixelImage(3, 3);

    greyScale3By3.setPixel(0, 0,
        new Color((float) 0.73680073, (float) 0.73680073, (float) 0.73680073));
    greyScale3By3.setPixel(1, 0,
        new Color((float) 0.7826951, (float) 0.7826951, (float) 0.7826951));
    greyScale3By3.setPixel(2, 0,
        new Color((float) 0.69062984, (float) 0.69062984, (float) 0.69062984));
    greyScale3By3.setPixel(0, 1,
        new Color((float) 0.65149015, (float) 0.65149015, (float) 0.65149015));
    greyScale3By3.setPixel(1, 1,
        new Color((float) 0.6356295, (float) 0.6356295, (float) 0.6356295));
    greyScale3By3.setPixel(2, 1, new Color((float) 1.0, (float) 1.0, (float) 1.0));
    greyScale3By3.setPixel(0, 2,
        new Color((float) 0.8100301, (float) 0.8100301, (float) 0.8100301));
    greyScale3By3.setPixel(1, 2,
        new Color((float) 0.76489276, (float) 0.76489276, (float) 0.76489276));
    greyScale3By3.setPixel(2, 2,
        new Color((float) 0.76827276, (float) 0.76827276, (float) 0.76827276));

    return t.checkExpect(grid2By2.renderGreyScale(), greyScale2By2)
        && t.checkExpect(grid3By3.renderGreyScale(), greyScale3By3);

  }

  // test for renderSeams
  boolean testRenderSeams(Tester t) {
    this.initExamples();
    this.initExamples2();

    Grid grid2By2 = new Grid(twoBy2);

    ComputedPixelImage image2By2 = new ComputedPixelImage(2, 2);
    ArrayList<SeamInfo> seams2By2 = grid2By2.findVerticalSeams();

    image2By2.setPixel(0, 0, new Color(110, 110, 110));
    image2By2.setPixel(1, 0, new Color(110, 110, 110));
    image2By2.setPixel(0, 1, new Color(255, 255, 255));
    image2By2.setPixel(1, 1, new Color(188, 188, 188));

    Grid grid3By3 = new Grid(threeBy3);

    ComputedPixelImage image3By3 = new ComputedPixelImage(3, 3);
    ArrayList<SeamInfo> seams3By3 = new Grid(threeBy3).findVerticalSeams();

    image3By3.setPixel(0, 0, new Color(87, 87, 87));
    image3By3.setPixel(1, 0, new Color(93, 93, 93));
    image3By3.setPixel(2, 0, new Color(82, 82, 82));
    image3By3.setPixel(0, 1, new Color(165, 165, 165));
    image3By3.setPixel(1, 1, new Color(158, 158, 158));
    image3By3.setPixel(2, 1, new Color(201, 201, 201));
    image3By3.setPixel(0, 2, new Color(255, 255, 255));
    image3By3.setPixel(1, 2, new Color(249, 249, 249));
    image3By3.setPixel(2, 2, new Color(250, 250, 250));

    return t.checkExpect(grid2By2.renderSeams(), image2By2)
        && t.checkExpect(grid3By3.renderSeams(), image3By3);
  }

}
