import java.util.*;
import java.io.*;

public class MTF extends Compression{
	private List<Byte> tableDistance;
	
	public MTF(String ad){
		_input=ad;
		tableDistance=new ArrayList<Byte>();
		for(int b=Byte.MIN_VALUE;b<=Byte.MAX_VALUE;b++)
			tableDistance.add((byte)b);
	}
	
	public void compresser(String input, boolean v){
		if(v){
			System.out.println("-------- COMPRESSION--------");
			System.out.println("from                    : "+_input);
			System.out.println("to                  : "+input);
			System.out.println("method               : MTF");
		}
		long tps=-System.currentTimeMillis();
		try{
			if(v)System.out.print(">> Compression...          ");
			BufferedInputStream readFlow =new BufferedInputStream(new FileInputStream(_input));
			DataOutputStream writeFlow=new DataOutputStream(new BufferedOutputStream(new FileOutputStream(input)));
			
			int intLu=0;
			while((intLu=readFlow.read())!=-1){
				byte byteLu=(byte)intLu;
				int index=tableDistance.indexOf(byteLu);
				tableDistance.remove((int)index);
				tableDistance.add(0, byteLu);
				writeFlow.writeByte(index+Byte.MIN_VALUE);
			}
			writeFlow.close();
			readFlow.close();
			tps+=System.currentTimeMillis();
			System.out.println("Finished in "+tps+" ms");
			CSP.compressionRate(_input, input);
		}
		catch(IOException e){
			System.out.println(e);
		}
	}
	
	public void decompresser(String input, boolean v){
		if(v){
			System.out.println("------- DECOMPRESSION-------");
			System.out.println("to                    : "+_input);
			System.out.println("from                  : "+input);
			System.out.println("method               : mtf");
		}
		long tps=-System.currentTimeMillis();
		try{
			
			DataInputStream readFlow=new DataInputStream(new BufferedInputStream(new FileInputStream(_input)));
			BufferedOutputStream writeFlow=new BufferedOutputStream(new FileOutputStream(input));
			
			try{
				while(true){
					int index=readFlow.readByte()-Byte.MIN_VALUE;
					byte byteLu=tableDistance.get(index);
					tableDistance.remove(index);
					tableDistance.add(0, byteLu);
					writeFlow.write(byteLu);
				}
			}
			catch(EOFException e){
			}
			
			writeFlow.close();
			readFlow.close();
			if(v)System.out.println("OK");
			tps+=System.currentTimeMillis();
			System.out.println("finished inn "+tps+" ms");
		}
		catch(IOException e){
			System.out.println(e);
		}
	}
	public double estimationComp(){
		System.out.println("No change in size");
		return 1.;
	}
	
	public void init(){
		_input=null;
		tableDistance=new ArrayList<Byte>();
		for(int b=Byte.MIN_VALUE;b<=Byte.MAX_VALUE;b++)
			tableDistance.add((byte)b);
	}
	public static void main(String[] args){
		MTF b=new MTF("");
		b.testerTout(true);
	}
	
}
