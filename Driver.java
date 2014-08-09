/*

Zola Mahlaza
Controller class
August 8th, 2014

http://www.gnu.org/licenses/gpl-3.0.txt

*/
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
public class Driver{
	public static void main(String[] args){
		try{
			File img0 = new File(args[0]);
			BufferedImage img = ImageIO.read(img0);
			ImageProcessor imgprocessor = new ImageProcessor();
			saveImage(imgprocessor.GaussianBlur(img , 10, 3, ImageProcessor.PERIODIC), "result.png","png");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void saveImage(BufferedImage img, String filename, String extension) throws IOException{
		File outputfile = new File(filename);
		ImageIO.write(img, extension, outputfile);
	}
}
