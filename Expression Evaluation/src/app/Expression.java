package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	/** DO NOT create new vars and arrays - they are already created before being sent in
    	 ** to this method - you just need to fill them in.
    	 **/
    	Stack<String> symbols = new Stack<String>();//looks for letters and brackets specifically
    	expr = expr.replaceAll(" ","");//gets rid of spaces because no, just no
    	StringTokenizer tokenStr = new StringTokenizer(expr,delims,true);
    	
    	String temp;
    	String slightlyEditedDelim = "/*+-/()[]";
    	//looks for letters to fill in their values
    	while(tokenStr.hasMoreTokens()) {
    		temp = tokenStr.nextToken();
    		if(temp.charAt(0)>='a'&&temp.charAt(0)<='z'
    				||temp.charAt(0)>='A'&&temp.charAt(0)<='Z'
    				||temp.equals("[")) {
    			symbols.push(temp);
    		}
    	}
    	//not sure if works way intended
    	while(!symbols.isEmpty()) {
    		//pops top two symbols to see if a letter is followed by a bracket
    		temp = symbols.pop();
    		if(temp.equals("[")) {//if it is a bracket it is an array
    			Array symbol = new Array(temp);//creates new array
    			if(arrays.indexOf(symbol)==-1) {
    				
    				arrays.add(symbol);
    			}
    		}
    		else {
    			Variable var = new Variable(temp);
    			if(vars.indexOf(var)==-1) {
    				vars.add(var);
    			}
    		}
    	}	
    }
    	
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	//Note for TA, arrays dont work...neither do a lot of different ways to input parens
    	
    	expr = expr.replaceAll(" ","");
    	StringTokenizer tokenStr = new StringTokenizer(expr,delims,true);
    	int tokenCount = tokenStr.countTokens();
    	int i=0;
    	
    	String []tokenHolder = new String[tokenStr.countTokens()];
    	
    	String currOp;
    	
    	Stack<Float> numbersInit = new Stack<Float>();//temp numbers
    	Stack<String> operatorsInit = new Stack<String>();//temp operators
    	Stack<Float> numbers = new Stack<Float>();
    	Stack<String> operators = new Stack<String>();
    	
    	float idk,finalRes=0,varValue;
    	
		
    	for(i=0;i<tokenCount;i++) {
    		tokenHolder[i]=tokenStr.nextToken();
    	}
    	//fills the stacks
    	for(i=0;i<tokenHolder.length;i++) {
    		if(delims.contains(tokenHolder[i])) {
    			operatorsInit.push(tokenHolder[i]);//pushes into operators stack
    		}
    		//checks if numbers
    		else if (Character.isDigit(tokenHolder[i].charAt(0))) {
    			idk = Float.parseFloat(tokenHolder[i]);//grabs and parses the number
    			numbersInit.push(idk);//pushes into numbers stack	
    		}
    		//checks letter (need arrays still)
    		else if(Character.isLetter(tokenHolder[i].charAt(0))){
    			
    				Variable var = new Variable(tokenHolder[i]);
                    int varIndex = vars.indexOf(var);
                    varValue = vars.get(varIndex).value;
                    numbersInit.push(varValue);
    			
    			/**else {
    			}**/
    		}
    	}
    	
    	//flips the operators into the correct order
    	while(operatorsInit.size()!=0) {
    		operators.push(operatorsInit.pop());
    	}
    	//flips numbers into the correct order for above to also work
    	while(numbersInit.size()!=0) {
			numbers.push(numbersInit.pop());
		}
    	while(operators.size()!=0) {
    		currOp = operators.pop();
    		//needs to do a double check on size because of the initial pop
    		//for precedence purposes
    		if(currOp.contains("(")||currOp.contains(")")) {
    			continue;
    		}
    		
    		if(operators.size()!=0) {
    			if(operators.peek().contains("(")) {
        			numbersInit.push(numbers.pop());
        			operatorsInit.push(currOp);
        		}
        		if(currOp.contains(")")) {
        			numbers.push(doMath(operators.pop(),numbers.pop(),numbers.pop()));
        			numbers.push(numbersInit.pop());
    				operators.push(operatorsInit.pop());
        			
        		}
    			//takes a look at the next operator to check for precedence
    			if((operators.peek().contains("*")||operators.peek().contains("/"))
    					&&(!currOp.contains("*")||!currOp.contains("/"))) {
    				
    				//if precedence is found, one number and one symbol are stored temporarily
    				operatorsInit.push(currOp);
    				numbersInit.push(numbers.pop());
    				//does math and pushes result back
    				idk = doMath(operators.pop(),numbers.pop(),numbers.pop());
    				//puts the stuff from temp back where it should be	
    				numbers.push(idk);
    				numbers.push(numbersInit.pop());
    				operators.push(operatorsInit.pop());
    			}
    			
    			//multiple operators but precedence isnt an issue
    			else {
    				
    					idk = doMath(currOp,numbers.pop(),numbers.pop());
    					numbers.push(idk);
    			}
    		}
    		//if theres only one operator left
    		else {
    			numbers.push(doMath(currOp,numbers.pop(),numbers.pop()));
    		}
    	}
    	
    	//does the celebratory home stretch
		finalRes = numbers.pop();
    	return finalRes;
    }
    	
    //helper for basic math(direct math)
    private static float doMath(String expr, float a, float b) {
    	float c=0;
    	if(expr.equals("+")){
    		c = a+b;
    	}
    	else if(expr.equals("-")){
    		c = a-b;
    	}
    	else if(expr.equals("*")){
    		c = a*b;
    	}
    	else if(expr.equals("/")){
    		c = a/b;
    	}
    	return c;
    }
}