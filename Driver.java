/*

Zola Mahlaza
Controller class
August 8th, 2014

http://www.gnu.org/licenses/gpl-3.0.txt

*/
import java.awt.image.BufferedImage;
public class Driver{
	public static void main(String[] args){
		try{
			File img0 = new File(args[0]);
			BufferedImage img = ImageIO.read(img0);
			ImageProcessor imgprocessor = new ImageProcessor();
			imgprocessor.GaussianBlur(imgprocessor);
	
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
