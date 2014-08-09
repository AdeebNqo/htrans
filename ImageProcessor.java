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
					double pixelval = Double.POSITIVE_INFINITY;
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
					pixelproductsum += mask[maskindex]*pixelval;
				}
				pixels[i][j] = (int) pixelproductsum;
			}
		}
		return createImage(w, h, pixels);
	}

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
	public double[] get1dMask(int kernelsize, double sigma){
			double[] mask = new double[kernelsize];
			float total = 0;
			for(int x=0; x<kernelsize; ++x){
				double power = (Math.pow(x, 2)) / (2*Math.pow(sigma, 2));
				mask[x] = (1/2*Math.PI*Math.pow(sigma, 2))*Math.exp(-power);
				total += mask[x];
			}
			for (int x=0; x<kernelsize; ++x){
				mask[x] = mask[x] / total ;
			}
			return mask;
	}
	/*

	Method for converting pixel array into an image

	*/
	public BufferedImage createImage(int width, int height, int[][] pixelvalues){
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int x=0; x<width; ++x){
			for(int y=0; y<height; ++y){
				img.setRGB(x, y, pixelvalues[x][y]);
			}
		}
		return img;
	}
}
