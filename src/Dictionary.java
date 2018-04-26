import java.util.*;
import java.io.*;

public class Dictionary extends Compression{
	private Map<List<Byte>,String> dict=null;
	private List<List<Byte>> listOfWordsPresent=null;
	private List<List<Byte>> textWordbyWord=null;
	private int nbBit;
	
	public Dictionary(String fileAddress){
		_input=fileAddress;
	}
	
	public void compresser(String fileAddress, boolean verbose){
		long temps=-System.currentTimeMillis();
		if(verbose){
			System.out.println("-------- COMPRESSION--------");
			System.out.println("from                    : "+_input);
			System.out.println("to                  : "+fileAddress);
			System.out.println("Method               : Dictionary");
		}
		try{
			if(verbose)System.out.print(">> Dictionary...         ");
			dict=new HashMap<List<Byte>,String>();
			listOfWordsPresent=new ArrayList<List<Byte>>();
			textWordbyWord=new ArrayList<List<Byte>>();
			
			DataInputStream readFlow=new DataInputStream(new BufferedInputStream(new FileInputStream(_input)));
			
			Set<List<Byte>> set=new HashSet<List<Byte>>();
			
			List<Byte> bytesSinceSpace=new ArrayList<Byte>();
			byte byteLu=0;
			while((byteLu=(byte)readFlow.read())!=-1){
				if(byteLu==' '){
					set.add(bytesSinceSpace);
					textWordbyWord.add(bytesSinceSpace);
					bytesSinceSpace=new ArrayList<Byte>();
				}
				else{
					bytesSinceSpace.add(byteLu);
				}
			}
			set.add(bytesSinceSpace);
			textWordbyWord.add(bytesSinceSpace);
			
			readFlow.close();
			nbBit=1;
			while(set.size()>Math.pow(2, nbBit))nbBit++;
			
			listOfWordsPresent=new ArrayList<List<Byte>>(set);
			int i=0;
			for(List<Byte> l:listOfWordsPresent){
				dict.put(l,int2bin(i,nbBit));
				i++;
			}
			DataOutputStream writeFlow=new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileAddress)));
			
			writeFlow.writeInt(listOfWordsPresent.size());
			writeFlow.writeInt(nbBit);
			writeFlow.writeInt(textWordbyWord.size());
			
			for(List<Byte> mot:listOfWordsPresent)
				writeFlow.writeShort(mot.size());
			
			byte[][] dictionnaireByteArray=new byte[listOfWordsPresent.size()][];
			for(int j=0;j<listOfWordsPresent.size();j++){
				dictionnaireByteArray[j]=new byte[listOfWordsPresent.get(j).size()];
				for(int k=0;k<listOfWordsPresent.get(j).size();k++){
					dictionnaireByteArray[j][k]=listOfWordsPresent.get(j).get(k);
					writeFlow.writeByte(dictionnaireByteArray[j][k]);
				}
			}
			
			StringBuffer chainToWrite=new StringBuffer();
			for(List<Byte> l:textWordbyWord){
				chainToWrite.append(dict.get(l));
			}
			
			byte[] data=new byte[(textWordbyWord.size()*nbBit)/8+1];
			for(int j=0;j<data.length-1;j++){
				data[j]=string2Byte(chainToWrite.substring(j*8, j*8+8));
			}
			data[data.length-1]=string2Byte(chainToWrite.substring(data.length*8-8));
			for(byte b:data)
				writeFlow.writeByte(b);
			
			writeFlow.close();
			System.out.println("OK");
			temps+=System.currentTimeMillis();
			System.out.println("Fin de la compression en "+temps+" ms");
			CSP.compressionRate(_input, fileAddress);
		}
		catch(IOException e){
			System.out.println(e);
		}
	}
	
	
	
	public void decompresser(String adresseFichierFinal, boolean verbeux){
		long temps=-System.currentTimeMillis();
		if(verbeux){
			System.out.println("------- Decompression-------");
			System.out.println("from                    : "+_input);
			System.out.println("to                  : "+adresseFichierFinal);
			System.out.println("method               : Dictionary");
		}
		try{
			dict=new HashMap<List<Byte>,String>();
			listOfWordsPresent=new ArrayList<List<Byte>>();
			textWordbyWord=new ArrayList<List<Byte>>();
			
			DataInputStream readFlow=new DataInputStream(new BufferedInputStream(new FileInputStream(_input)));
			
			int section=readFlow.readInt();
			nbBit=readFlow.readInt();
			int nbMots=readFlow.readInt();
			
			int[] sizes=new int[section];
			for(int i=0;i<section;i++){
				sizes[i]=readFlow.readShort();
			}
			
			for(int i=0;i<section;i++){
				List<Byte> word=new ArrayList<Byte>();
				for(int j=0;j<sizes[i];j++){
					word.add(readFlow.readByte());
				}
				listOfWordsPresent.add(word);
			}
			
			StringBuffer chainToWrite=new StringBuffer();
			try{
				while(true){
					byte by=readFlow.readByte();
					chainToWrite.append(int2bin(by,8).substring(int2bin(by,8).length()-8));
				}
			}
			catch(EOFException e){
			}
			
			int bitEnTrop=chainToWrite.length()-nbMots*nbBit;
			if(bitEnTrop!=0){
				chainToWrite.delete(chainToWrite.length()-8+1, 1+chainToWrite.length()-8+bitEnTrop);
			}
			
			readFlow.close();
			
			DataOutputStream writeFlow=new DataOutputStream(new BufferedOutputStream(new FileOutputStream(adresseFichierFinal)));
			
			boolean firstPassage=true;
			int i=0;
			try{
				while(true){
					if(firstPassage){
						firstPassage=false;
					}
					else{
						writeFlow.writeByte(' ');
					}
					int index=Integer.parseInt(chainToWrite.substring(i*nbBit,(i+1)*nbBit),2);
					List<Byte> mot=listOfWordsPresent.get(index);
					for(byte b:mot)
						writeFlow.writeByte(b);
					i++;
				}
			}
			catch(StringIndexOutOfBoundsException e){

			}
			writeFlow.close();
			
			temps+=System.currentTimeMillis();
			System.out.println("Finished inn "+temps+" ms");
		}
		catch(IOException e){
			System.out.println(e);
		}
	}
	
	public void init(){
		_input=null;
		dict=null;
		listOfWordsPresent=null;
		textWordbyWord=null;
	}
	
	public static void main(String[] args){
		Dictionary dc16=new Dictionary("test/k.html");
	}
	
}
