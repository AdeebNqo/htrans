/*

Zola Mahlaza <adeebnqo@gmail.com>
Image processing class
August 8th, 2014

http://www.gnu.org/licenses/gpl-3.0.txt

*/
import java.awt.image.BufferedImage;
public class ImageProcessor{

	//boundary extension options
	//public static int SYMETRIC = 0;
	public static int PERIODIC = 1;
	public static int ZERO = 2;
	public static int CONTINUATION = 4;

	public ImageProcessor(){

	}

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
		int[][] Gx = {
			{-1, 0, 1},
			{-2, 0, 2},
			{-1, 0, 1}
		};
		int[][] Gy = {
			{1, 2, 1},
			{0, 0, 0},
			{-1, -2, -1}
		};
		//applying Gx, Gy and then returning the resulting img
		return createImage(w, h, Convolute(Gy, w, h, Convolute(Gx, w, h, pixels, boundaryextension), boundaryextension));
	}
	/*

	Method for applying convolution to image

	@args mask Array of values that make up mask
	@args iwidth Image width
	@args iheight Image height
	@args img Pixel array of image
	@args boundaryextension Boundary extension scheme (SYMETRIC | PERIODIC | ZERO | CONTINUATION)

	*/
	private int[][] Convolute(int[][] mask, int iwidth, int iheight, int[][] img, int boundaryextension){
		for (int x=0; x<iwidth; ++x){
			for (int y=0; y<iheight; ++y){

			}
		}
		return null;
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
				System.err.print("["+pixelvalues[x][y]+"]");
			}
			System.err.println();
		}
	}
}
