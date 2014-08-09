/*

Zola Mahlaza <adeebnqo@gmail.com>
Image processing class
August 8th, 2014

http://www.gnu.org/licenses/gpl-3.0.txt

*/
import java.awt.image.BufferedImage;
public class ImageProcessor{

	public ImageProcessor(){

	}

	public BufferedImage GaussianBlur(BufferedImage img, int kernelsize,double sigma){
		int w = img.getWidth();
		int h = img.getHeight();

		//retrieving the pixel values from the image
		double[][] pixels = new double[w][h];
		for (int i=0; i<w; ++i){
			for (int j=0; j<h; ++j){
				pixels[i][j] = img.getRGB(i,j);
			}
		}

		/*

		Applying the filter -- that is applying the dot product

		*/
		double[] mask = get1dMask(kernelsize ,sigma);

		return null;
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
}
