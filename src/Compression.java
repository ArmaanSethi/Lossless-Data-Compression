import java.util.*;
import java.io.*;

public abstract class Compression {
	protected String _input;
	
	public abstract void compresser(String fin, boolean v);
	public abstract void decompresser(String fin, boolean v);
	public double estimationComp(){
		System.out.println("Aucune estimation disponible");
		return 0.;
	}
	public abstract void init();
	
	public String int2bin(int i,int t){
		StringBuffer str=new StringBuffer(Integer.toBinaryString(i));
		while(str.length()<t)
			str.insert(0,"0");
		return str.toString();
	}
	
	public static byte string2Byte(String str){
		if(str.charAt(0)=='0')return Byte.parseByte(str,2);
		else{
			if(str.length()>=2)
				return (byte) (Byte.parseByte(str.substring(1),2)-Byte.MAX_VALUE-1);
			else
				return Byte.MIN_VALUE;
		}
	}
	
	public static int string2Int(String str){// avec str en convention unsigned
		if(str.charAt(0)=='0')return Integer.parseInt(str,2);
		else{
			return (int) (Integer.parseInt(	str.substring(1),2)-Integer.MAX_VALUE-1);
		}
	}
	
	public void tester(String dep,String arr, boolean v){
		init();
		_input=dep;
		compresser(dep+"c", v);
		init();
		_input=dep+"c";
		decompresser(arr,v);
		CSP.isTheSame(dep,arr);
	}
	
	public void testerTout(boolean v){
		String rep="test/";
		String fich[]={"c.pak","e.m4a","h.html","j.jpg","p.pptx","s.so","x.xcf","k.html"};
		for(String s :fich)
			tester(rep+s,rep+"2"+s,v);
	}
	
	public void testerToutTexte(boolean v){
		String rep="test/";
		String fich[]={"c.pak","e.m4a","h.html","j.jpg","p.pptx","s.so","x.xcf","k.html"};
		for(String s :fich)
			if(CSP.typeOf(rep+s)==TypeFichier.TEXT)tester(rep+s,rep+"2"+s,v);
	}
	
	public String getDepart(){
		return _input;
	}
	
}
