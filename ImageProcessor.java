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
	
	public BufferedImage GaussianBlur(BufferedImage img){
		int w = img.getWidth();
		int h = img.getHeight();
		return null;
	}

	public double[][] getMask(int width, int height, float sigma){
		double[][] mask = new double[width][height];

		float total = 0;
		for (int x=0; x<width; ++x){
			for(int y=0; y<height; ++y){
				double power = (Math.pow(x, 2)+Math.pow(y, 2)) / (2*Math.pow(sigma, 2));
					mask[width][height] = (1/2*Math.PI*Math.pow(sigma, 2))*Math.exp(-power);
					total += mask[width][height];
			}
		}

		for (int x=0; x<width; ++x){
			for(int y=0; y<height; ++y){
				mask[width][height] = mask[width][height] / total ;
			}
		}
		return mask;
	}
}
