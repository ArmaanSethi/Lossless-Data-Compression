import java.util.*;

public class Node<T> {
	public Node<T> right;
	public Node<T> left;
	public int weight;
	public T value;
	public final T DEF=null;
	
	public Node(Node<T> tdroite, Node<T> tgauche){
		right=tdroite;
		left=tgauche;
		value=DEF;
		if(right!=null && left!=null)
			weight=right.weight+left.weight;
		else weight=0;
	}
	
	public Node(T tvaleur, int frequency){
		weight=frequency;
		value=tvaleur;
		right=null;
		left=null;
	}
	
	public String toString(){
		return ""+weight;
	}
}

class NoeudComparator<T> implements Comparator<Node<T>>{
	public int compare(Node<T> n1, Node<T> n2){
		if(n1.weight>n2.weight)return 1;
		if(n1.weight==n2.weight)return 0;
		return -1;
	}
}
