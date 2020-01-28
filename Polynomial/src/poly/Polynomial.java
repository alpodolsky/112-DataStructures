package poly;

import java.io.IOException;
import java.util.Scanner;

/**
 * This class implements evaluate, add and multiply for polynomials.
 * 
 * @author runb-cs112
 * @edited Date:2/13/2019
 * @edited By: Alex Podolsky
 *
 */
public class Polynomial {

	/**
	 * Reads a polynomial from an input stream (file or keyboard). The storage format
	 * of the polynomial is:
	 * <pre>
	 *     <coeff> <degree>
	 *     <coeff> <degree>
	 *     ...
	 *     <coeff> <degree>
	 * </pre>
	 * with the guarantee that degrees will be in descending order. For example:
	 * <pre>
	 *      4 5
	 *     -2 3
	 *      2 1
	 *      3 0
	 * </pre>
	 * which represents the polynomial:
	 * <pre>
	 *      4*x^5 - 2*x^3 + 2*x + 3 
	 * </pre>
	 * 
	 * @param sc Scanner from which a polynomial is to be read
	 * @throws IOException If there is any input error in reading the polynomial
	 * @return The polynomial linked list (front node) constructed from coefficients and
	 *         degrees read from scanner
	 */
	public static Node read(Scanner sc) 
	throws IOException {
		Node poly = null;
		while (sc.hasNextLine()) {
			Scanner scLine = new Scanner(sc.nextLine());
			poly = new Node(scLine.nextFloat(), scLine.nextInt(), poly);
			scLine.close();
		}
		return poly;
	}

	
	/**
	 * Returns the sum of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list
	 * @return A new polynomial which is the sum of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	public static Node add(Node poly1, Node poly2) {
		/** COMPLETE THIS METHOD **/
		
		//resultant node
		Node newPoly = null;
		//initiates a loop with both poly's until one reaches null
		while(poly1!=null&&poly2!=null) {
			//adds like terms
			if (poly1.term.degree==poly2.term.degree) {
				//if they add to 0 it moves on
				if((poly1.term.coeff+poly2.term.coeff)==0) {
					poly1=poly1.next;
					poly2=poly2.next;
				}
				//if its not 0 makes a new node
				else {
					newPoly = new Node((poly1.term.coeff+poly2.term.coeff),poly1.term.degree,newPoly);
					poly1=poly1.next;
					poly2=poly2.next;
				}
			}
			//case for poly1 having a greater degree, if so adds poly2 as a node first
			else if (poly1.term.degree>poly2.term.degree) {
				newPoly = new Node(poly2.term.coeff,poly2.term.degree,newPoly);
				poly2 = poly2.next;
			}
			//case for poly2 having a greater degree, if so adds poly1 as a node first
			else if(poly1.term.degree<poly2.term.degree) {
				newPoly = new Node(poly1.term.coeff,poly1.term.degree,newPoly);
				poly1 = poly1.next;
			}
		}
		//if poly1 is empty
		//also catches any nulls that "fall through the cracks" in the above loop
		if(poly1==null) {
			
			//loops through poly2 filling newPoly 1 by 1
			while(poly2!=null) {
				newPoly = new Node(poly2.term.coeff,poly2.term.degree,newPoly);
				poly2 = poly2.next;
			}
		}
		//if poly2 is empty, and same as above but for poly2
		if(poly2==null) {
			//loops through poly2 filling newPoly 1 by 1
			while(poly1!=null) {
				newPoly = new Node(poly1.term.coeff,poly1.term.degree,newPoly);
				poly1 = poly1.next;
			}
		}
		//flips the nodes into correct order
		Node flip = newPoly;
		Node flipped = null;
		while (flip!= null) {
			flipped = new Node(flip.term.coeff,flip.term.degree,flipped);
			flip = flip.next;
			
		}
		return flipped;
	}

	
	/**
	 * Returns the product of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list)
	 * @return A new polynomial which is the product of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	public static Node multiply(Node poly1, Node poly2) {
		Node product = null;//final product
		Node temp = null;//used to reset tempProd basically
		Node tempProd=temp;//this could be null instead of temp but i figure why not
		Node second = poly2;//Necessary for resetting poly2 in the loop below 
		if(poly1==null||poly2==null) {//if either poly is 0 then it would just be 0
			product = null;
		}
		else {
			while(poly1!=null) {
				while(second!=null) {
					tempProd = new Node(poly1.term.coeff*second.term.coeff,poly1.term.degree+second.term.degree,tempProd);
					second = second.next;
				}
				second = poly2;//resets second to be reused
				//figuring out I needed to flip this before sending it to add
				//took longer than I would care to admit :(
				Node flip = tempProd;
				Node flipped = null;
				while (flip!= null) {
					flipped = new Node(flip.term.coeff,flip.term.degree,flipped);
					flip = flip.next;
				}
				product = add(product,flipped);
				tempProd = temp;//resets tempProd
				poly1 = poly1.next;//moves on to next node of poly1
			}
		}
		return product;
    }
		
	
	/**
	 * Evaluates a polynomial at a given value.
	 * 
	 * @param poly Polynomial (front of linked list) to be evaluated
	 * @param x Value at which evaluation is to be done
	 * @return Value of polynomial p at x
	 */
	public static float evaluate(Node poly, float x) {
		/**
		 *  @float value stores the sum of each term
		 *  @Node current represents the front of poly
		 *  @float currentvalue represents an x value and its power
	    **/
		float value = 0;
		Node current = poly;
		//loop continues until current reaches null or the end of poly
		while(current != null) {
			float currentvalue = (float)Math.pow(x,current.term.degree);
			currentvalue*=current.term.coeff;
			value+=currentvalue;
			//moves on to the next term
			current=current.next;
		}
		return value;
	
	}
	
	/**
	 * Returns string representation of a polynomial
	 * 
	 * @param poly Polynomial (front of linked list)
	 * @return String representation, in descending order of degrees
	 */
	public static String toString(Node poly) {
		if (poly == null) {
			return "0";
		} 
		
		String retval = poly.term.toString();
		for (Node current = poly.next ; current != null ;
		current = current.next) {
			retval = current.term.toString() + " + " + retval;
		}
		return retval;
	}	
}