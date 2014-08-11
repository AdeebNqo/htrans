/*

Zola Mahlaza <adeebnqo@gmail.com>
Image processing class
August 8th, 2014

http://www.gnu.org/licenses/gpl-3.0.txt

*/
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Graphics2D;
public class ImageProcessor{

	//boundary extension options
	//public static int SYMETRIC = 0;
	public static int PERIODIC = 1;
	public static int ZERO = 2;
	public static int CONTINUATION = 4;

	public ImageProcessor(){

	}
	//input img is in grey scale?
	public BufferedImage SobelOperator(BufferedImage img, int boundaryextension){
		int w = img.getWidth();
		int h = img.getHeight();

		//retrieving the pixel values from the image
		int[][] pixels = new int[w][h];
		for (int i=0; i<w; ++i){
			for (int j=0; j<h; ++j){
				pixels[i][j] = img.getRGB(i,j);
			}
		}

		//sobel kernels
		int[][] Mx = {
			{-1, 0, 1},
			{-2, 0, 2},
			{-1, 0, 1}
		};
		int[][] My = {
			{1, 2, 1},
			{0, 0, 0},
			{-1, -2, -1}
		};
		//applying Mx, My and then returning the resulting img
		return createImage(w, h, Convolute(Mx, My, w, h, pixels, boundaryextension));
	}
	/*

	Method for applying convolution to image using two masks

	@args Mx Array of values that make up mask, x direction
	@args My Array of values that make up mask, y direction
	@args iwidth Image width
	@args iheight Image height
	@args img Pixel array of image
	@args boundaryextension Boundary extension scheme (SYMETRIC | PERIODIC | ZERO | CONTINUATION)

	*/
	private int[][] Convolute(int[][] Mx, int[][] My, int iwidth, int iheight, int[][] img, int boundaryextension){

		//vertical and horizontal gradient
		int[][] Gx = new int[iwidth][iheight];
		int[][] Gy = new int[iwidth][iheight];

		//calculating gradients
		for (int y=0; y<iheight; ++y){
			for (int x=0; x<iwidth; ++x){
				//x direction
				int sum = 0; //var to hold mask*img sum
				for (int my=0; my<3; ++my){
					for (int mx=0; mx<3; ++mx){
						int pixelval = (int) Double.POSITIVE_INFINITY;
						if (y+my >= iheight && x+mx >= iwidth){
							//boundary extension
							if (boundaryextension==ImageProcessor.ZERO){
								pixelval = 0;
							}
							else if(boundaryextension==ImageProcessor.CONTINUATION){
								pixelval = img[iwidth-1][iheight-1];
							}
							else if (boundaryextension==ImageProcessor.PERIODIC){
								pixelval = img[(x+mx)%iwidth][(y+my)%iheight];
							}
						}
						else if (x+mx >= iwidth){
							//boundary extension
							if (boundaryextension==ImageProcessor.ZERO){
								pixelval = 0;
							}
							else if(boundaryextension==ImageProcessor.CONTINUATION){
								pixelval = img[iwidth-1][y+my];
							}
							else if (boundaryextension==ImageProcessor.PERIODIC){
								pixelval = img[(x+mx)%iwidth][y+my];
							}

						}else if (y+my >= iheight){
							//boundary extension
							if (boundaryextension==ImageProcessor.ZERO){
								pixelval = 0;
							}
							else if(boundaryextension==ImageProcessor.CONTINUATION){
								pixelval = img[x+mx][iheight-1];
							}
							else if (boundaryextension==ImageProcessor.PERIODIC){
								pixelval = img[x+mx][(y+my)%iheight];
							}
						}else{
							pixelval = img[x+mx][y+my];
						}
						sum += (pixelval & 0xff) * Mx[mx][my];
					}
				}
				Gx[x][y] = sum; //0xff000000 | ((int)sum << 16 | (int)sum << 8 | (int)sum);

				//y direction
				sum = 0; //var to hold mask*img sum
				for (int my=0; my<3; ++my){
					for (int mx=0; mx<3; ++mx){
						int pixelval = (int) Double.POSITIVE_INFINITY;
						if (y+my >= iheight && x+mx >= iwidth){
							//boundary extension
							if (boundaryextension==ImageProcessor.ZERO){
								pixelval = 0;
							}
							else if(boundaryextension==ImageProcessor.CONTINUATION){
								pixelval = img[iwidth-1][iheight-1];
							}
							else if (boundaryextension==ImageProcessor.PERIODIC){
								pixelval = img[(x+mx)%iwidth][(y+my)%iheight];
							}
						}
						else if (x+mx >= iwidth){
							//boundary extension
							if (boundaryextension==ImageProcessor.ZERO){
								pixelval = 0;
							}
							else if(boundaryextension==ImageProcessor.CONTINUATION){
								pixelval = img[iwidth-1][y+my];
							}
							else if (boundaryextension==ImageProcessor.PERIODIC){
								pixelval = img[(x+mx)%iwidth][y+my];
							}
						}else if (y+my >= iheight){
							//boundary extension
							if (boundaryextension==ImageProcessor.ZERO){
								pixelval = 0;
							}
							else if(boundaryextension==ImageProcessor.CONTINUATION){
								pixelval = img[x+mx][iheight-1];
							}
							else if (boundaryextension==ImageProcessor.PERIODIC){
								pixelval = img[x+mx][(y+my)%iheight];
							}
						}else{
							pixelval = img[x+mx][y+my];
						}
						sum += (pixelval & 0xff) * My[mx][my];
					}
				}
				Gy[x][y] = sum; //0xff000000 | ((int)sum << 16 | (int)sum << 8 | (int)sum);
			}
		}

		int[][] magnitude = new int[iwidth][iheight];
		int maxmagnitude = 0;
		//calculating magnitude of edges
		for (int y=0; y<iheight; ++y){
			for (int x=0; x<iwidth; ++x){
				magnitude[x][y] = (int) Math.sqrt(Math.pow(Gx[x][y], 2) + Math.pow(Gx[x][y], 2));
				if (magnitude[x][y] > maxmagnitude){
					maxmagnitude = magnitude[x][y];
					
				}
			}
		}

		float ratio=(float)maxmagnitude/255;
		int pixelsum = 0;
		for(int y=0; y<iheight; ++y) {
			for(int x=0; x<iwidth; ++x) {
				pixelsum=(int)(magnitude[x][y]/ratio);
				img[x][y] = 0xff000000 | ((int)pixelsum << 16 | (int)pixelsum << 8 | (int)pixelsum);
			}
		}
		return img;
	}

	/*

	Method for applying Gaussian blur

	@args img Pixel array of image
	@args kernelsize Width of kernel as it's always 1d
	@args sigma Sigma value for Gaussian function
	@args boundaryextension Boundary extension scheme (SYMETRIC | PERIODIC | ZERO | CONTINUATION)
	*/
	public BufferedImage GaussianBlur(BufferedImage img, int kernelsize, double sigma, int boundaryextension){
		int w = img.getWidth();
		int h = img.getHeight();

		//retrieving the pixel values from the image
		int[][] pixels = new int[w][h];
		for (int i=0; i<w; ++i){
			for (int j=0; j<h; ++j){
				pixels[i][j] = img.getRGB(i,j);
			}
		}

		double[] mask = get1dMask(kernelsize ,sigma);
		/*

		Applying the filter -- that is applying the dot product
		x-direction

		*/
		for (int j=0; j<h; ++j){ //running through height
			for (int i=0; i<w; ++i){ //running through width
				double pixelproductsum = 0.0;
				for (int maskindex=0; maskindex<kernelsize; ++maskindex){
					int pixelval = (int) Double.POSITIVE_INFINITY;
					if (maskindex+i >= w){
						//boundary extension
						if (boundaryextension==ImageProcessor.ZERO){
							pixelval = 0;
						}
						else if(boundaryextension==ImageProcessor.CONTINUATION){
							pixelval = pixels[w-1][j];
						}
						else if (boundaryextension==ImageProcessor.PERIODIC){
							pixelval = pixels[(maskindex+i)%w][j];
						}

					}else{
						pixelval = pixels[maskindex+i][j];
					}
					pixelproductsum += mask[maskindex]*(pixelval & 0xff);
					//pixelproductsum += mask[maskindex]*pixelval;
				}
				//pixels[i][j] = (int)pixelproductsum;
				pixels[i][j] = 0xff000000 | ((int)pixelproductsum << 16 | (int)pixelproductsum << 8 | (int)pixelproductsum);
			}
		}
		/*

		Applying the filter -- that is applying the dot product
		y-direction

		*/
		for (int i=0; i<w; ++i){ //running through width
			for (int j=0; j<h; ++j){ //running through height
				double pixelproductsum = 0.0;
				for (int maskindex=0; maskindex<kernelsize; ++maskindex){
					int pixelval = (int) Double.POSITIVE_INFINITY;
					if (maskindex+j >= h){
						//boundary extension
						if (boundaryextension==ImageProcessor.ZERO){
							pixelval = 0;
						}
						else if(boundaryextension==ImageProcessor.CONTINUATION){
							pixelval = pixels[i][h-1];
						}
						else if (boundaryextension==ImageProcessor.PERIODIC){
							pixelval = pixels[i][(maskindex+j)%h];
						}
					}else{
						pixelval = pixels[i][maskindex+j];
					}
					pixelproductsum += mask[maskindex]*(pixelval & 0xff);
					//pixelproductsum += mask[maskindex]*pixelval;
				}
				//pixels[i][j] = (int)pixelproductsum;
				pixels[i][j] = 0xff000000 | ((int)pixelproductsum << 16 | (int)pixelproductsum << 8 | (int)pixelproductsum);
			}
		}
		//printArray(w, h, pixels);
		return createImage(w, h, pixels);
	}
	/*

	Method for calculating 2d Gaussian mask

	@args width Width of kernel
	@args height Height of kernel
	@args sigma Sigma value for Gaussian function
	*/
	public double[][] get2dMask(int width, int height, double sigma){
			double[][] mask = new double[width][height];

			float total = 0;
			for (int x=0; x<width; ++x){
				for(int y=0; y<height; ++y){
					double power = (Math.pow(x, 2)+Math.pow(y, 2)) / (2*Math.pow(sigma, 2));
						mask[x][y] = (1/2*Math.PI*Math.pow(sigma, 2))*Math.exp(-power);
						total += mask[x][y];
				}
			}

			for (int x=0; x<width; ++x){
				for(int y=0; y<height; ++y){
					mask[x][y] = mask[x][y] / total ;
				}
			}
			return mask;
	}
	/*

	Method for calculating 1d Gaussian mask

	@args kernelsize Width of kernel
	@args sigma Sigma value for Gaussian function
	*/
	public double[] get1dMask(int kernelsize, double sigma){
			double[] mask = new double[kernelsize];
			double total = 0;
			for(int x=0; x<kernelsize; ++x){
				double power = (Math.pow(x, 2)) / (2*Math.pow(sigma, 2));
				mask[x] = (1/Math.sqrt(2*Math.PI*Math.pow(sigma, 2)))*Math.exp(-power);
				total += mask[x];
			}
			for (int x=0; x<kernelsize; ++x){
				mask[x] = mask[x] / total ;
			}
			return mask;
	}
	/*

	Method for converting pixel array into an image

	@args width Width of image
	@args height Height of image
	@args pixelvalues Pixel array of image
	*/
	public BufferedImage createImage(int width, int height, int[][] pixelvalues){
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int x=0; x<width; ++x){
			for(int y=0; y<height; ++y){
				img.setRGB(x, y, (int) pixelvalues[x][y]);
			}
		}
		return img;
	}
	/*

	Print array - fordebugging

	@args width Width of image
	@args height Height of image
	@args pixelvalues Pixel array of image
	*/
	private void printArray(int width, int height, int[][] pixelvalues){
		for (int x=0; x<width; ++x){
			for(int y=0; y<height; ++y){
				System.out.print("["+pixelvalues[x][y]+"]");
			}
			System.out.println();
		}
	}

	/*

	Method for drawing finding circles on picture using Hough Transform

	@args img The image to be processed
	*/
	public BufferedImage[] CircleHough(BufferedImage img, BufferedImage origimg){
		int w = img.getWidth();
		int h = img.getHeight();

		//retrieving the pixel values from the image
		int[][] pixels = new int[w][h];
		for (int i=0; i<w; ++i){
			for (int j=0; j<h; ++j){
				pixels[i][j] = img.getRGB(i,j);
			}
		}

		//getting maximum radius of circle
		int rmax = (int) Math.sqrt( Math.pow(w, 2) + Math.pow(h,2) );
		int[][][] accumulator = new int[rmax][w][h];
		for (int i=0; i< rmax; ++i){
			for (int j=0; j<w; ++j){
				for (int k=0; k<h; ++k){
					accumulator[i][j][k] = 0;
				}
			}
		}
		//calculating degrees-radians lookup table
		double[] radlookup = new double[360];
		for (int theta=0; theta<360; ++theta){
			radlookup[theta] = (theta * Math.PI) / 180;
		}
		int maxvote = 0;
		int votes = 0;
		int threshold = -16000000;
	
		/*

		voting

		*/
		double radian = -1;
		int a, b = 0;
		System.err.println("Voting!");
		int maxradius = 0;
		int minradius = 0;

		for (int y=0; y<h; ++y){	
			for (int x=0; x<w; ++x){
				if (pixels[x][y] < threshold){
					pixels[x][y] = -16777216; //make it equal to background
				}
				if (pixels[x][y] != -16777216){
					//System.out.print("0");
					for (int r=0; r<rmax; ++r){
						for (int theta=0; theta<360; ++theta){
							radian = radlookup[theta];//(theta * Math.PI) / 180;
							a = (int)Math.round(x - r * Math.cos(radian));
							b = (int)Math.round(y - r * Math.sin(radian));
							if (a > 0 && a < w && b > 0 && b < h){ //if center is in picture
								//doing the actual vote
								accumulator[r][a][b] += 1;
								++votes;
								if (accumulator[r][a][b] > maxvote){
									maxvote = accumulator[r][a][b];
								}
								if (r >= maxradius){
									maxradius = r;
								}
								else if (r < minradius){
									minradius = r;
								}
							}
						}
					}
				}else{
					//System.out.print("1");
				}
			}
			System.out.print("\r"+(int)((y/(double)h)*100)+"%");
		}
		System.err.println();
		System.err.println("Drawing lines!");


		//drawing hough transform
		BufferedImage houghimg = new BufferedImage(w,h, BufferedImage.TYPE_INT_RGB);

		//processing individual pixels on hough space
		int neighbourhood = (int) (rmax/ 2.5);
		int thresholdx = maxvote-5;
		for (int ax=0; ax<w; ++ax){
			for (int bx=0; bx<h; ++bx){
				for (int r=0; r<rmax; ++r){

					int pixelval =  (int)((accumulator[r][ax][bx] / (double)maxvote) * 255); //normalizing value it's going to be turn into an image
					int rgb = 0xff000000 | (pixelval << 16 | pixelval << 8 | pixelval);
					houghimg.setRGB(ax,bx, rgb);

					if (accumulator[r][ax][bx] > thresholdx){
						
						//comparing pixel with neighbours
						int maxax = ax;
						int maxbx = bx;
						//System.err.println("a: "+ax+", b:"+bx);
						for (int i=1; i<neighbourhood; ++i){
							//same plane
							try{
								if (accumulator[r][ax+i][bx] > accumulator[r][maxax][maxbx]){
									maxax = ax+i;
									maxbx = bx;
								}
							}catch(Exception e){}
							try{
								if (accumulator[r][ax][bx+i] > accumulator[r][maxax][maxbx]){
									maxax = ax;
									maxbx = bx+i;
								}
							}catch(Exception e){}
							try{
								if (accumulator[r][ax-i][bx] > accumulator[r][maxax][maxbx]){
									maxax = ax-i;
									maxbx = bx;
								}
							}catch(Exception e){}
							try{
								if (accumulator[r][ax][bx-i] > accumulator[r][maxax][maxbx]){
									maxax = ax;
									maxbx = bx-i;
								}
							}catch(Exception e){}
							try{
								if (accumulator[r][ax-i][bx-i] > accumulator[r][maxax][maxbx]){
									maxax = ax-i;
									maxbx = bx-i;
								}
							}catch(Exception e){}
							try{
								if (accumulator[r][ax+i][bx+i] > accumulator[r][maxax][maxbx]){
									maxax = ax+i;
									maxbx = bx+i;
								}
							}catch(Exception e){}
							try{
								if (accumulator[r][ax+i][bx-i] > accumulator[r][maxax][maxbx]){
									maxax = ax+i;
									maxbx = bx-i;
								}
							}catch(Exception e){}
							try{
								if (accumulator[r][ax-i][bx+i] > accumulator[r][maxax][maxbx]){
									maxax = ax-i;
									maxbx = bx+i;
								}
							}catch(Exception e){}

							//----------------------------------------------------------------------
							if (r-i > 0){
								try{
									if (accumulator[r-i][ax+i][bx] > accumulator[r][maxax][maxbx]){
										maxax = ax+i;
										maxbx = bx;
									}
								}catch(Exception e){}
								try{
									if (accumulator[r-i][ax][bx+i] > accumulator[r][maxax][maxbx]){
										maxax = ax;
										maxbx = bx+i;
									}
								}catch(Exception e){}
								try{
									if (accumulator[r-i][ax-i][bx] > accumulator[r][maxax][maxbx]){
										maxax = ax-i;
										maxbx = bx;
									}
								}catch(Exception e){}
								try{
									if (accumulator[r-i][ax][bx-i] > accumulator[r][maxax][maxbx]){
										maxax = ax;
										maxbx = bx-i;
									}
								}catch(Exception e){}
								try{
									if (accumulator[r-i][ax-i][bx-i] > accumulator[r][maxax][maxbx]){
										maxax = ax-i;
										maxbx = bx-i;
									}
								}catch(Exception e){}
								try{
									if (accumulator[r-i][ax+i][bx+i] > accumulator[r][maxax][maxbx]){
										maxax = ax+i;
										maxbx = bx+i;
									}
								}catch(Exception e){}
								try{
									if (accumulator[r-i][ax+i][bx-i] > accumulator[r][maxax][maxbx]){
										maxax = ax+i;
										maxbx = bx-i;
									}
								}catch(Exception e){}
								try{
									if (accumulator[r-i][ax-i][bx+i] > accumulator[r][maxax][maxbx]){
										maxax = ax-i;
										maxbx = bx+i;
									}
								}catch(Exception e){}
							}
							//--------------------------------------------------------------------------
							if (r+i <= maxradius){
								try{
									if (accumulator[r+1][ax+i][bx] > accumulator[r][maxax][maxbx]){
										maxax = ax+i;
										maxbx = bx;
									}
								}catch(Exception e){}
								try{
									if (accumulator[r+1][ax][bx+i] > accumulator[r][maxax][maxbx]){
										maxax = ax;
										maxbx = bx+i;
									}
								}catch(Exception e){}
								try{
									if (accumulator[r+1][ax-i][bx] > accumulator[r][maxax][maxbx]){
										maxax = ax-i;
										maxbx = bx;
									}
								}catch(Exception e){}
								try{
									if (accumulator[r+1][ax][bx-i] > accumulator[r][maxax][maxbx]){
										maxax = ax;
										maxbx = bx-i;
									}
								}catch(Exception e){}
								try{
									if (accumulator[r+1][ax-i][bx-i] > accumulator[r][maxax][maxbx]){
										maxax = ax-i;
										maxbx = bx-i;
									}
								}catch(Exception e){}
								try{
									if (accumulator[r+1][ax+i][bx+i] > accumulator[r][maxax][maxbx]){
										maxax = ax+i;
										maxbx = bx+i;
									}
								}catch(Exception e){}
								try{
									if (accumulator[r+1][ax+i][bx-i] > accumulator[r][maxax][maxbx]){
										maxax = ax+i;
										maxbx = bx-i;
									}
								}catch(Exception e){}
								try{
									if (accumulator[r+1][ax-i][bx+i] > accumulator[r][maxax][maxbx]){
										maxax = ax-i;
										maxbx = bx+i;
									}
								}catch(Exception e){}
							}
						}
						//draw circle
						drawCircle(maxax, maxbx, r, origimg);
					}
				}
			}
		}
		//printing the report
		System.err.println("Number of votes: "+votes);

		BufferedImage[] imgs = {origimg, houghimg};
		return imgs;
	}
	public void drawCircle(int x0, int y0, int radius, BufferedImage img){
		  int x = radius, y = 0;
		  int radiusError = 1-x;
		 
		  while(x >= y)
		  {
		    DrawPixel(x + x0, y + y0, img);
		    DrawPixel(y + x0, x + y0, img);
		    DrawPixel(-x + x0, y + y0, img);
		    DrawPixel(-y + x0, x + y0, img);
		    DrawPixel(-x + x0, -y + y0, img);
		    DrawPixel(-y + x0, -x + y0,img);
		    DrawPixel(x + x0, -y + y0,img);
		    DrawPixel(y + x0, -x + y0,img);
		    y++;
		    if (radiusError<0)
		    {
		      radiusError += 2 * y + 1;
		    }
		    else
		    {
		      x--;
		      radiusError += 2 * (y - x + 1);
		    }
		  }
	}
	public void DrawPixel(int x, int y, BufferedImage img){
		try{
			img.setRGB(x, y, 100);
		}catch(ArrayIndexOutOfBoundsException e){}
	}
}
