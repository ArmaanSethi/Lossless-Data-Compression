import java.io.*;
import java.util.*;
import java.lang.*;

enum TypeFichier{TEXT, HYBRID, COMPRESSED_DATA, IDK};

public class CSP {
	public static boolean VERB=true;
	private static final int LETTER=0,SPACE=1,AVERAGE=2,AVG_DIFF=3;
	
	public static boolean isTheSame(String s1, String s2){
		int missing=0;
		int wrong=0;
		try{
			FileInputStream f1= new FileInputStream(s1);
			FileInputStream f2= new FileInputStream(s2);
			BufferedInputStream g1=new BufferedInputStream(f1);
			BufferedInputStream g2=new BufferedInputStream(f2);
			int i1=g1.read();
			int i2=g2.read();
			while(i1!=-1){
				if(i2!=-1){
					if(i1!=i2)wrong++;
					i2=g2.read();
				}
				else missing++;
				i1=g1.read();
			}
			while(g2.read()!=-1)missing++;
			f1.close();
			f2.close();
			if(wrong==0 && missing==0){
				System.out.println("Indentical");
			}
			else{
				System.out.println("Differant");
				System.out.println("Missing : "+missing);
			}
		}
		catch(IOException e){

		}
		
		return (wrong==0)&&(missing==0);
	}
	
	public static double compressionRate(String depart, String arrivée){
		double taux=100-(double)tailleDe(arrivée)/tailleDe(depart)*100;
		System.out.printf("taux de compression : %.1f%s\n",taux,"%");
		return taux;
	}
	
	public static int tailleDe(String ad){
		int taille=0;
		try{
			FileInputStream f= new FileInputStream(ad);
			BufferedInputStream b=new BufferedInputStream(f);
			while(b.read()!=-1)taille++;
			b.close();
		}
		catch(IOException e){
			System.err.println(e);
		}
		return taille;
	}
	
	public static void testerTout(boolean sauver){
		try{
			PrintStream cons=System.out, log;
			if(sauver){
				log = new PrintStream("bilan2.log");
				System.setOut(log);
			}
			RLE rc=new RLE("");
			rc.testerTout(VERB);
//			BwtMtfHm8 b1=new BwtMtfHm8("");
//			b1.testerTout(VERB);
//			BwtMtfRle b2=new BwtMtfRle("");
//			b2.testerTout(VERB);
			Dictionary dc=new Dictionary("");
			dc.testerToutTexte(VERB);
			if(sauver)
				System.setOut(cons);
		}
		catch(IOException e){
			System.err.println(e);
		}
	}
	
	public static TypeFichier typeOf(String ad){
		double [] t=analyserFichier(ad);
		if(t==null)
			return TypeFichier.IDK;
		
		if(t[SPACE]>10 && t[LETTER]>60)
			return TypeFichier.TEXT;
		if(t[SPACE]>5 && t[LETTER]>30)
			return TypeFichier.HYBRID;
		if(Math.abs(t[AVERAGE]-Byte.MAX_VALUE)<5 && Math.abs(t[AVG_DIFF]-Byte.MAX_VALUE/2-10)<3)
			return TypeFichier.COMPRESSED_DATA;
		return TypeFichier.IDK;
	}
	
	private static double[] analyserFichier(String ad){
		try{
			int sizeMax=2000;
			int taille=tailleDe(ad);
			RandomAccessFile r=new RandomAccessFile(ad,"r");
			
			int c;
			int[] val=new int[sizeMax];
			double letters=0, spaces=0, avg=0, std_dev=0;
			Set<Integer> s=new HashSet<Integer>();
			for(int i=0;i<sizeMax;i++){
				r.seek((long)(Math.random()*taille));
				c=r.read();
				s.add(c);
				avg+=c;
				val[i]=c;
				if((c>='a'&& c<='z')||(c>='A'&&c<='Z'))
					letters++;
				if(Character.isSpaceChar(c))
					spaces++;
			}
			r.close();
			avg/=sizeMax;
			for(int i:val){
				std_dev+=Math.pow(i-avg,2);
			}
			std_dev=Math.sqrt(std_dev/(sizeMax-1));
			return new double[] {letters/sizeMax*100,spaces/sizeMax*100,avg,std_dev};
		}
		catch(IOException e){
			System.out.println(e);
			return null;
		}
	}
	
	public static void conseiller(String ad){
		System.out.print("Algorithm recommended for "+ad+" : ");
		switch(typeOf(ad)){
		case TEXT:
			System.out.println("Dictionary");
			break;
		case HYBRID:
			System.out.println("Huffman");
			break;
		case COMPRESSED_DATA:
			System.out.println("RLE");
			break;
		case IDK:
			System.out.println("Huffman");
			break;
		}
	}
	
	public static void help(){
		System.out.println("-------------------- ALGORITHMES --------------------");
		System.out.println();
		System.out.println("Commands :");
		System.out.println("	Compressor           	: c[ID] source destination");
		System.out.println("	Decompressor           	: d[ID] source destination");
		System.out.println("	Estimate           	: e[ID] source");
		System.out.println("	Tester	      	     	: t[ID]");
		System.out.println("[ID] :");
		System.out.println("	RLE          		: [rl]");
		System.out.println("	dictionary 		: [d]");
		System.out.println("Examples :");
		System.out.println("	ch8 test.txt test2.comp");
		System.out.println("	erl test.txt");
		System.out.println("	dd test2.comp test.txt");
		System.out.println("	trl");
	}
	
	public static void main(String[] args){
		//help();
		System.out.println("		[help] to show commands");
		Scanner sc=new Scanner(System.in);
		boolean continuer=true;
		while(continuer){
			System.out.print(">> ");
			String commande=sc.nextLine();
			String[] commands =commande.split(" ");
			if(commands[0].equals("sys")){
				try{
					StringBuffer strB=new StringBuffer();
					for(int i=1;i<commands.length;i++){
						strB.append(commands[i]+" ");
					}
					Process p=Runtime.getRuntime().exec(strB.toString());
					BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
		            String s;
					while ((s = br.readLine()) != null)
		                System.out.println(s);
		            p.destroy();
					//Runtime.getRuntime().exec(strB.toString());
				}
				catch(Exception e){
					System.out.println("ERREUR DU SYSTEME "+e);
				}
			}
			else{
				switch(commands.length){
				case 0:
					System.out.println("SAISIE NON VALIDE");
					break;
				case 1:
					if(commands[0].equals("q")||commands[0].equals("Q")){
						continuer=false;
					}
					else if(commands[0].equals("t")){
						testerTout(false);
					}
					else if(commands[0].equals("ts")){
						long tps=-System.currentTimeMillis();
						System.out.print("Enregistrement dans \"bilan.log\"...");
						testerTout(true);
						tps+=System.currentTimeMillis();
						System.out.println("OK ("+tps+"ms)");
					}
					else if(commands[0].equals("help")){
						help();
					}
					else if(commands[0].equals("trl")){
						RLE rc=new RLE("");
						rc.testerTout(VERB);
					}
					else if(commands[0].equals("td")){
						Dictionary dc=new Dictionary("");
						dc.testerToutTexte(VERB);
					}
//					else if(syntaxe[0].equals("trh")){
//						BwtMtfHm8 bw=new BwtMtfHm8("");
//						bw.testerTout(VERB);
//					}
//					else if(syntaxe[0].equals("trrl")){
//						BwtMtfRle dc=new BwtMtfRle("");
//						dc.testerTout(VERB);
//					}
					else if(commands[0].equals("rn")){
						try{
							Runtime.getRuntime().exec("cp -r te/test .");
							System.out.println("Dossier de test renouvellÃ©");
						}
						catch(Exception e){
							System.out.println("Erreur systeme "+e);
						}
						
					}
					else if(commands[0].equals("rnlog")){
						try{
							Runtime.getRuntime().exec("rm bilan.log");
							System.out.println("log renouvellÃ©");
						}
						catch(IOException e){
							System.out.println("Erreur systeme");
						}
						
					}
					else{
						System.out.println("SAISIE NON VALIDE");
					}
					break;
				case 2:
					if(commands[0].equals("type")){
						System.out.print("Type de "+commands[1]);
						switch(typeOf(commands[1])){
						case TEXT:
							System.out.println(" : Texte");
							break;
						case HYBRID:
							System.out.println(" : Hybride");
							break;
						case COMPRESSED_DATA:
							System.out.println(" : Données compressées");
							break;
						case IDK:
							System.out.println(" : Inconnu");
							break;
						}
					}
					else if(commands[0].equals("bav")){
						if(commands[1].equals("0")){
							VERB=false;
							System.out.println("Le mode bavard est désactivé");
						}
						else{
							VERB=true;
							System.out.println("Le mode bavard est activé");
						}
					}
					else if(commands[0].equals("conseil")){
						conseiller(commands[1]);
					}
					else if(commands[0].equals("taille")){
						System.out.println("Taille de "+commands[0]+" "+tailleDe(commands[1]));
					}
					else if(commands[0].equals("erl")){
						RLE rc=new RLE(commands[1]);
						rc.estimationComp();
					}
					else if(commands[0].equals("ed")){
						Dictionary dc=new Dictionary(commands[1]);
						dc.estimationComp();
					}
//					else if(syntaxe[0].equals("erh")){
//						BwtMtfHm8 rc=new BwtMtfHm8(syntaxe[1]);
//						rc.estimationComp();
//					}
//					else if(syntaxe[0].equals("errl")){
//						BwtMtfRle dc=new BwtMtfRle(syntaxe[1]);
//						dc.estimationComp();
//					}
					else{
						System.out.println("SAISIE NON VALIDE");
					}
					break;
				case 3:
					if(commands[0].equals("ident")){
						isTheSame(commands[1],commands[2]);
					}
					else if(commands[0].equals("crl")){
						RLE rc=new RLE(commands[1]);
						rc.compresser(commands[2],VERB);
					}
					else if(commands[0].equals("cd")){
						Dictionary dc=new Dictionary(commands[1]);
						dc.compresser(commands[2],VERB);
					}
					else if(commands[0].equals("drl")){
						RLE rc=new RLE(commands[1]);
						rc.decompresser(commands[2],VERB);
					}
					else if(commands[0].equals("dd")){
						Dictionary dc=new Dictionary(commands[1]);
						dc.decompresser(commands[2],VERB);
					}
//					else if(syntaxe[0].equals("drh")){
//						BwtMtfHm8 rc=new BwtMtfHm8(syntaxe[1]);
//						rc.decompresser(syntaxe[2],VERB);
//					}
//					else if(syntaxe[0].equals("drrl")){
//						BwtMtfRle dc=new BwtMtfRle(syntaxe[1]);
//						dc.decompresser(syntaxe[2],VERB);
//					}
					break;
				default:
					System.out.println("SAISIE NON VALIDE");
				}
			}
		}
		sc.close();
	}
}
