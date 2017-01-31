package truthTable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Gerador {
	
	static ArrayList<String> subExpr = new ArrayList<String>();
	static ArrayList<Integer> exprValue = new ArrayList<Integer>();
	static ArrayList<Boolean> satisfatibility = new ArrayList<Boolean>();
	static String table = "";
	static int ident = 0;
	static String divide = "";
	
	public static void main(String[] args) {
		try{
			BufferedReader br = new BufferedReader(new FileReader("Expressoes.in"));
			String line;
			BufferedWriter writer = new BufferedWriter(new FileWriter("Expressoes.out"));
			String firstLine = br.readLine();
			int n = Integer.parseInt(firstLine);
			int t = 0;
			while (n > 0){
				line = br.readLine();
				n--;
				getExpr(line);
		        for (int i = 0; i < subExpr.size(); i++) {
		            for (int j = i; j > 0 && sort(j - 1, j); j--) {
		                String aux = subExpr.get(j - 1);
		                subExpr.set(j - 1, subExpr.get(j));
		                subExpr.set(j, aux);
		            }
		        }
				int[][] a = generateValues();
				t++;
				table += "Table #" + t + "\n";
				header();
				
				for(int i = 0; i < a.length; i++){
					calculate(a[i]);			
				}
				satisfatibility();
				
				table += "\n";
				writer.write(table);
				
				table = "";
				ident = 0;
				divide = "";
				
				subExpr.clear();
				exprValue.clear();
				satisfatibility.clear();
			}
			writer.close();
			br.close();
		} catch (IOException e){
			e.printStackTrace();
		}
		
	}
	
	public static boolean isOpr(char a){
		if(a == '+' || a == '-' || a == '.' || a == '>'){
			return true;
		} else {
			return false;
		}
	}
	
	public static void getExpr(String line){
		int parentesis = 0;
		
		for(int i = 1; i < line.length() - 1; i++){
			if(line.charAt(i) == '('){
				parentesis++;
			} else if(line.charAt(i) == ')'){
				parentesis--;
			}
			
			if(isOpr(line.charAt(i)) && parentesis == 0){
				if(line.charAt(i) != '-'){
					getExpr(line.substring(1, i));					
				}
				getExpr(line.substring(i+1, line.length() - 1));
				
				if(!subExpr.contains(line)){
					subExpr.add(line);					
				}
			}
		}
		if(line.length() == 1 && !subExpr.contains(line)){
			subExpr.add(line);
		}
	}
	
	private static boolean sort(int i, int j) {
        String exprI = subExpr.get(i);
        String exprJ = subExpr.get(j);
        if (exprJ.length() == exprI.length()) {
            if(exprI.length() == 1) {
                if(exprI.charAt(0) == 't') {
                    return true;
                } else if(exprJ.charAt(0) == 't') {
                    return false;
                } else {
                    return exprI.compareTo(exprJ) > 0;
                }
            }
            return exprI.compareTo(exprJ) > 0;
        }
        return exprI.length() > exprJ.length();
    }
	
	public static int[][] generateValues(){
		int c = 0;
		for(int i = 0; i < subExpr.size(); i++){
			if(subExpr.get(i).length() == 1){
				c++;
			}
		}
		int z = (int)Math.pow(2, c);
		
		int[][] values = new int[z][c];
		int t = z;
		for(int i = 0; i < c; i++){
			for(int j = 0; j < z; j++){
				if(j%t < t/2){
					values[j][i] = 0;
				} else{
					values[j][i] = 1;
				}
			}
			t = t/2;
		}
		return values;
	}
	
	public static int or(int a, int b){
		if(a == 1){
			return 1;
		} else if (b == 1){
			return 1;
		} else {
			return 0;
		}
	}
	
	public static int and(int a, int b){
		if(a == 1 && b == 1){
			return 1;
		} else {
			return 0;
		}
	}
	
	public static int implicate(int a, int b){
		if(a == 0){
			return 1;
		} else if (b == 1){
			return 1;
		} else {
			return 0;
		}
	}
	
	public static int not(int a){
		if(a == 1){
			return 0;
		} else {
			return 1;
		}
	}
	
	public static void calculate(int[] atomicValues){
		int parentesis = 0;
		String expr = "";
		int j = 0;
		for(int i = 0; i < subExpr.size(); i++){
			if(subExpr.get(i).length() == 1){
				exprValue.add(atomicValues[j]);
				j++;
			} else {
				expr = subExpr.get(i);
				for(int z = 1; z < expr.length()-1; z++){
					if(expr.charAt(z) == '('){
						parentesis++;
					} else if (expr.charAt(z) == ')'){
						parentesis--;
					} else if (isOpr(expr.charAt(z)) && parentesis == 0){
						if(expr.charAt(z) == '-'){
							exprValue.add(not(exprValue.get(subExpr.indexOf(expr.substring(z+1, expr.length()-1)))));
						} else {
							if(expr.charAt(z) == '+'){
								exprValue.add(or(exprValue.get(subExpr.indexOf(expr.substring(1, z))), exprValue.get(subExpr.indexOf(expr.substring(z+1, expr.length()-1)))));
							} else if (expr.charAt(z) == '.'){
								exprValue.add(and(exprValue.get(subExpr.indexOf(expr.substring(1, z))), exprValue.get(subExpr.indexOf(expr.substring(z+1, expr.length()-1)))));
							} else if (expr.charAt(z) == '>'){
								exprValue.add(implicate(exprValue.get(subExpr.indexOf(expr.substring(1, z))), exprValue.get(subExpr.indexOf(expr.substring(z+1, expr.length()-1)))));
							}
						}
					}
				}
			}
		}
		buildTable();// montar String
		satisfatibility.add(isSatisfatible());
		exprValue.clear();
	}
	
	public static void header(){
		String aux = "";
		for(int i = 0; i < subExpr.size(); i++){
			aux += "|" + subExpr.get(i);
		}
		aux += "|";
		ident = aux.length();
		for(int i = 0; i < ident; i++){
			table += "-";
		}
		table += "\n" + aux + "\n";
		
		for(int i = 0; i < ident; i++){
			divide += "-";
		}
		table += divide;
		table += "\n";
	}
	
	public static void buildTable(){
		String aux = "";
		int chars = 0;
		for(int i = 0; i < subExpr.size(); i++){
			chars = subExpr.get(i).length();
			aux += "|";
			for(int z = 0; z < chars-1; z++){
				aux += " ";
			}
			aux += exprValue.get(i);
		}
		aux += "|\n";
		table += aux;
		table += divide;
		table += "\n";
	}

	public static boolean isSatisfatible(){
		if(exprValue.get(exprValue.size()-1) == 0){
			return false;
		} else {
			return true;
		}
	}

	public static void satisfatibility(){
		String aux = "";
		boolean refutable = false;
		boolean satisfatible = false;
		
		for(int i = 0; i < satisfatibility.size(); i++){
			if (satisfatibility.get(i)){
				satisfatible = true;
			}
			if(!satisfatibility.get(i)){
				refutable = true;
			}
		}
		if(satisfatible){
			aux += "satisfativel";
		}
		else {
			aux += "insatisfativel";
		}
		if(refutable){
			aux += " e refutavel";
		} else {
			aux += " e taltologia";
		}
		
		table += aux + "\n";
	}
}
