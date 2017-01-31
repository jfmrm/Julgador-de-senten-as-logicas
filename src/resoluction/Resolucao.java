package resoluction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Resolucao {
	public static void main(String[] args) {
		try {
			BufferedReader br = new BufferedReader(new FileReader("Expressoes.in"));
			BufferedWriter bw = new BufferedWriter(new FileWriter("Expressoes.out"));
			int n = Integer.parseInt(br.readLine());
			String line;
			String caso = "caso #";
			int t = 0;
			while (n > 0){
				line = br.readLine();
				n--;
				t++;
				if(isFnc(line)){
					if(isHorn(line)){
						if(isSatisfatible(line)){
							bw.write(caso + t + ": satisfativel\n");
						} else {
							bw.write(caso + t + ": insatisfativel\n");
						}
					} else {
						bw.write(caso + t + ": nem todas as clausulas sao de Horn\n");
					}
				} else {
					bw.write(caso + t + ": nao esta na FNC\n");
				}
			}
			bw.close();
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isFnc(String line){
		int parentesis = 0;
		for(int i = 0; i < line.length(); i++){
			if(line.charAt(i) == '('){
				parentesis++;
			} else if(line.charAt(i) == ')'){
				parentesis--;
			} else if(isOpr(line.charAt(i)) && (parentesis == 0 && (line.charAt(i) != '.'))){
				return false;
			} else if(isOpr(line.charAt(i)) && parentesis == 1 && line.charAt(i) == '.'){
				return false;
			} else if(line.charAt(i) == '>'){
				return false;
			} else if(parentesis > 1){
				return false;
			}
		}
		return true;
	}
	
	public static boolean isHorn(String line){
		int parentesis = 0; 
		int positiveLiterals = 0;
		for(int i = 0; i < line.length(); i++){
			if(line.charAt(i) == '('){
				parentesis++;
			} else if(line.charAt(i) == ')'){
				parentesis--;
				positiveLiterals = 0;
			} else if(isVar(line.charAt(i)) && line.charAt(i-1) != '-' && parentesis > 0){
				positiveLiterals++;
			}
			if (positiveLiterals > 1){
				return false;
			} 
		}
		return true;
	}
	
	public static boolean isVar(char a){
		if(a == 'a' || a == 'b' || a == 'c' || a == 'd'){
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isOpr(char opr){
		if(opr == '+' || opr == '-' || opr == '.' || opr == '>'){
			return true;
		} else {
			return false;
		}
	}

	public static boolean isSatisfatible(String line){
		ArrayList<String> subExpr = new ArrayList<String>();
		ArrayList<String> temp = new ArrayList<String>();
		String newClouse = "";
		int pOpen = 0;
		char a;
		for(int i = 0; i < line.length(); i++){
			if(line.charAt(i) == '('){
				pOpen = i;
			} else if (line.charAt(i) == ')'){
				subExpr.add(line.substring(pOpen, i) + ")");
			} 
		}
		
			for(int i = 0; i < subExpr.size(); i++){ //i subExpr para comparar
				if(subExpr.get(i).length() == 3){
					a = subExpr.get(i).charAt(1);
					for(int j = 0; j < subExpr.size(); j++){ // j expr para ser comparada com i
						for(int t = 0; t < subExpr.get(j).length(); t++){ //t index da string de subExpr para ser comparada
							if(subExpr.get(j).charAt(t) == a && subExpr.get(j).charAt(t-1) == '-'){
								newClouse += subExpr.get(j).substring(0, t-1) + subExpr.get(j).substring(t+1, subExpr.get(j).length());
								temp.add(newClouse);
								if(newClouse.length() < 3){
									return false;
								}
								newClouse = "";
							}
						}
						if(!temp.isEmpty() && !subExpr.contains(temp.get(temp.size()-1))){
							subExpr.add(temp.get(temp.size()-1));
						}
						temp.clear();
					}
				} else if(subExpr.get(i).length() == 4){
					a = subExpr.get(i).charAt(2);
					for(int j = 0; j < subExpr.size(); j++){ // j expr para ser comparada com i
						for(int t = 0; t < subExpr.get(j).length(); t++){ //t index da string de subExpr para ser comparada
							if(subExpr.get(j).charAt(t) == a && subExpr.get(j).charAt(t-1) != '-'){
								newClouse += subExpr.get(j).substring(0, t) + subExpr.get(j).substring(t+2, subExpr.get(j).length());
								temp.add(newClouse);
								if(newClouse.length() < 3){
									return false;
								}
								newClouse = "";
							}
						}
						if(!temp.isEmpty() && !subExpr.contains(temp.get(temp.size()-1))){
							subExpr.add(temp.get(temp.size()-1));
						}
						temp.clear();
					}
				}		
			}
		return true;
	}
}
