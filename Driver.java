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
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
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
			JPanel pan5 = new JPanel(); frame.add(pan5);

			frame.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);


			File img0 = new File(args[0]);
			BufferedImage img1 = ImageIO.read(img0);
			ImageProcessor imgprocessor = new ImageProcessor();

			int gaussernelsize = 10;
			int gausssigma = 1;

			//original
			JLabel picLabel1 = new JLabel(new ImageIcon(img1));
			pan1.add(picLabel1);
			//grey convert img to greyscale
			int width = img1.getWidth();
			int height = img1.getHeight();
			BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
			for (int i=0; i<width; ++i){
				for (int j=0; j<height; ++j){
					img.setRGB(i,j,img1.getRGB(i,j));
				}
			}
			//gaussian blur
			BufferedImage gaussimg = imgprocessor.GaussianBlur(img , gaussernelsize, gausssigma, ImageProcessor.PERIODIC);
			JLabel picLabel2 = new JLabel(new ImageIcon(gaussimg));
			pan2.add(picLabel2);
			//sobel
			BufferedImage sobelimg = imgprocessor.SobelOperator(gaussimg, ImageProcessor.PERIODIC);
			JLabel picLabel3 = new JLabel(new ImageIcon(sobelimg));
			pan3.add(picLabel3);
			
			BufferedImage[] houghresponse = imgprocessor.CircleHough(sobelimg , ImageIO.read(img0));
			//hough transform
			JLabel picLabel4 = new JLabel(new ImageIcon(houghresponse[1]));
			pan4.add(picLabel4);
			//final			
			JLabel picLabel5 = new JLabel(new ImageIcon(houghresponse[0]));
			pan5.add(picLabel5);

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
	static BufferedImage deepCopy(BufferedImage bi) {
		 ColorModel cm = bi.getColorModel();
		 boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		 WritableRaster raster = bi.copyData(null);
		 return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
}
