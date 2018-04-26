import java.util.*;
import java.io.*;

public class RLE extends Compression{
	private List<Byte> _neut=null;
	private static int _byte=1;
	
	public RLE(String ad){
		init();
		_input=ad;
	}
	
	public void init(){
		_neut=null;
		_byte=1;
	}
	
	public void searchfor(boolean v) throws IOException{
		BufferedInputStream readFlow=new BufferedInputStream(new FileInputStream(_input));
		
		Set<List<Byte>> seen=new HashSet<List<Byte>>();
		byte[] buff=new byte[_byte];
		while(readFlow.read(buff)==_byte){
			List<Byte> l=new ArrayList<Byte>();
			for(byte by:buff)
				l.add(by);
			seen.add(l);
		}
		readFlow.close();
		
		List<Byte> l=new ArrayList<Byte>();
		for(int i=0;i<_byte;i++){
			l.add(Byte.MIN_VALUE);
		}
		try{
			while(seen.contains(l)){
				l=incrementor(l);
			}
			_neut=l;
			if(v)System.out.print("OK ( [");
			for(byte by:l)
				System.out.print(" "+by);
			System.out.println(" ] )");
		}
		catch(NullPointerException e){
			System.out.println(e.getMessage());
			_byte++;
			searchfor(v);
		}
	}
	
	public void compresser(String input, boolean v){
		long time_occured=-System.currentTimeMillis();
		if(v){
			System.out.println("-------- COMPRESSION--------");
			System.out.println("Of                    : "+_input);
			System.out.println("To                  : "+input);
			System.out.println("Method               : Run Length");
		}
		try{
			searchfor(v);
			
			BufferedInputStream readFlow=new BufferedInputStream(new FileInputStream(_input));
			BufferedOutputStream writeFlow=new BufferedOutputStream(new FileOutputStream(input));
			
			writeFlow.write(_byte);
			for(byte by:_neut){
				writeFlow.write(by);
			}
			
			int nb=0;
			byte[] buff=new byte[_byte];
			byte[] prebuff=new byte[_byte];
			
			readFlow.mark(_byte);
			readFlow.read(buff);
			List<Byte> lu=new ArrayList<Byte>(),newest=new ArrayList<Byte>();
			for(byte by:buff){
				lu.add(by);
				newest.add(by);
			}
			readFlow.reset();
			int nbLu=0;
			
			while((nbLu=readFlow.read(buff))==_byte){
				lu=new ArrayList<Byte>();
				for(byte by:buff)
					lu.add(by);
				if(newest.equals(lu) && nb<Byte.MAX_VALUE-1){
					nb++;
				}
				else{
					write(newest,nb,writeFlow);
					for(int i=0;i<lu.size();i++){
						newest.set(i, lu.get(i));
					}
					nb=1;
				}
				for(int i=0;i<_byte;i++){
					prebuff[i]=buff[i];
				}
			}
			if(nbLu!=-1){ 
				writeFlow.write(prebuff);
				if(nbLu!=_byte)
					for(int i=0;i<nbLu;i++)
						writeFlow.write(buff[i]);
			}
			else{
				write(newest,nb-1,writeFlow);
			}
			
			readFlow.close();
			writeFlow.close();
			System.out.println("OK");
			time_occured+=System.currentTimeMillis();
			System.out.println("It took "+time_occured+" ms");
			CSP.compressionRate(_input, input);
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
	
	public void write(List<Byte> chars, int nb, BufferedOutputStream b2) throws IOException{
		if(nb==1)
			for(Byte by:chars)
				b2.write(by);
		else if(nb==2){
			for(Byte by:chars)
				b2.write(by);
			for(Byte by:chars)
				b2.write(by);
		}
		else{
			for(Byte by:_neut)
				b2.write(by);
			b2.write(nb);
			for(Byte by:chars)
				b2.write((byte)by);
		}
	}
	
	public List<Byte> incrementor(List<Byte> l) throws NullPointerException {
		for(int i=0;i<l.size();i++){
			if(l.get(i)==Byte.MAX_VALUE){
				l.set(i, Byte.MIN_VALUE);
			}
			else{
				l.set(i, (byte)(l.get(i)+1));
				return l;
			}
		}
		throw new NullPointerException(" ("+_byte+")");
	}
	
	public void decompresser(String fin, boolean v){
		long time_passed=-System.currentTimeMillis();
		if(v){
			System.out.println("-------- Decompression------");
			System.out.println("Of                    : "+_input);
			System.out.println("To                  : "+fin);
			System.out.println("Method               : Run length");
		}
		try{
			BufferedInputStream readFlow=new BufferedInputStream(new FileInputStream(_input));
			BufferedOutputStream fluxEcriture=new BufferedOutputStream(new FileOutputStream(fin));
			
			_byte=readFlow.read();
			if(v)System.out.println("OK ("+_byte+")");
			if(v)System.out.print(">> Neutre...               ");
			byte[] buff=new byte[_byte];
			readFlow.read(buff);
			byte[] neutre=new byte[_byte];
			if(v)System.out.print("OK ( [");
			for(int i=0;i<_byte;i++){
				neutre[i]=buff[i];
				System.out.print(" "+neutre[i]);
			}
			System.out.println(" ] )");
			
			int nbLu=0;
			while((nbLu=readFlow.read(buff))==_byte){
				int i=0;
				boolean different=false;
				while(i<_byte && !different){
					if(buff[i]!=neutre[i])
						different=true;
					i++;
				}
				if(different){
					fluxEcriture.write(buff);
				}
				else{
					int nb=readFlow.read();
					readFlow.read(buff);
					for(int j=0;j<nb;j++)
						fluxEcriture.write(buff);
				}
			}
			if(nbLu==-1){
				fluxEcriture.write(buff);
			}
			else if(nbLu!=_byte)
				for(int i=0;i<nbLu;i++)
					fluxEcriture.write(buff[i]);
			
			readFlow.close();
			fluxEcriture.close();
			if(v) System.out.println("OK");
			time_passed+=System.currentTimeMillis();
			System.out.println("It took "+time_passed+" ms");
			
		}
		catch(IOException e){
			System.out.println(e);
		}
	}
	
	
	public static void main(String[] f){
		RLE rc=new RLE("test/h.html");
		rc.testerTout(true);
		
	}
	
}
