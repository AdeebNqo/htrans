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
import java.awt.FlowLayout;
import java.awt.ComponentOrientation;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
public class Driver{
	public static void main(String[] args){
		try{
			JFrame frame = new JFrame("ICV");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        		frame.setSize(1000, 1000);
			FlowLayout flow = new FlowLayout();
			frame.setLayout(flow);
	
			JPanel pan1 = new JPanel(); frame.add(pan1);
			JPanel pan2 = new JPanel(); frame.add(pan2);
			JPanel pan3 = new JPanel(); frame.add(pan3);
			JPanel pan4 = new JPanel(); frame.add(pan4);

			frame.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
			

			File img0 = new File(args[0]);
			BufferedImage img = ImageIO.read(img0);
			ImageProcessor imgprocessor = new ImageProcessor();
			//saveImage(imgprocessor.GaussianBlur(img , 4, 2, ImageProcessor.PERIODIC), "result.png","png");
			//saveImage(imgprocessor.SobelOperator(img, ImageProcessor.PERIODIC), "result.png","png");
			//saveImage(imgprocessor.CircleHough(imgprocessor.SobelOperator(img, ImageProcessor.PERIODIC)), "result.png","png");
			
			//original
			JLabel picLabel1 = new JLabel(new ImageIcon(img));
			pan1.add(picLabel1);
			//gaussian blur
			JLabel picLabel2 = new JLabel(new ImageIcon(imgprocessor.GaussianBlur(img , 4, 2, ImageProcessor.PERIODIC)));
			pan2.add(picLabel2);
			//sobel
			JLabel picLabel3 = new JLabel(new ImageIcon(imgprocessor.SobelOperator(imgprocessor.GaussianBlur(img , 4, 2, ImageProcessor.PERIODIC), ImageProcessor.PERIODIC)));
			pan3.add(picLabel3);
			//original
			JLabel picLabel4 = new JLabel(new ImageIcon(imgprocessor.CircleHough(imgprocessor.SobelOperator(imgprocessor.GaussianBlur(img , 4, 2, ImageProcessor.PERIODIC), ImageProcessor.PERIODIC))));
			pan4.add(picLabel4);

			frame.pack();
			frame.setVisible(true);	
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void saveImage(BufferedImage img, String filename, String extension) throws IOException{
		File outputfile = new File(filename);
		ImageIO.write(img, extension, outputfile);
	}
}
