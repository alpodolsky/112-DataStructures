package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */
public class Tree {
	/**
	 * Root node
	 */
	TagNode root=null;
	
	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;
	
	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}
	
	/**
	 * Builds the DOM tree from input HTML file, through scanner passed
	 * in to the constructor and stored in the sc field of this object. 
	 * 
	 * The root of the tree that is built is referenced by the root field of this object.
	 */
	public void build() {
		/** COMPLETE THIS METHOD **/
		//check if empty
		if(!sc.hasNextLine()) return;
		Stack<TagNode> tags = new Stack<TagNode>();
		//make root
		root = new TagNode(sc.nextLine().replace("<","").replace(">",""), null,null);
		tags.push(root);
		
		while(sc.hasNextLine()) {
			String text = sc.nextLine();
			//checks for closing tag
			if(text.charAt(0)=='<'&&text.charAt(1)=='/') {
				tags.pop();
			}
			//checks for the rest of tags
			else if (text.charAt(0)=='<'&&text.charAt(1)!='/'){
				TagNode temp = new TagNode(text.replace("<", "").replace(">", ""),null,null);
				//checks if firstchild is empty
				if(tags.peek().firstChild == null) {
					tags.peek().firstChild = temp;
					tags.push(temp);
				}
				//use sibling if firstchild is used
				else {
					TagNode ptr = tags.peek().firstChild;
					while(ptr.sibling!=null) {
						ptr = ptr.sibling;
					}
					ptr.sibling = temp;
					tags.push(temp);
				}
			}
			//does same as above but for stuff not within <>
			else {
				if(tags.peek().firstChild == null) {
					tags.peek().firstChild = new TagNode(text,null,null);
				}
				else {
					TagNode ptr = tags.peek().firstChild;
					while(ptr.sibling!= null) {
						ptr = ptr.sibling;
					}
					ptr.sibling = new TagNode(text,null,null);
				}
			}
		}
	}
	
	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {
		/** COMPLETE THIS METHOD **/
		replaceTag(root, oldTag, newTag);
	}
	private void replaceTag(TagNode root, String oldTag, String newTag) {
		if(root == null) return;
		else if (root.tag.contentEquals(oldTag)) {
			root.tag = newTag;
		}
		replaceTag(root.firstChild,oldTag,newTag);
		replaceTag(root.sibling,oldTag,newTag);
	}
	
	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 * 
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		/** COMPLETE THIS METHOD **/
		
		boldRow(root,row);
	}
	private void boldRow(TagNode root, int row) {
		if(root==null)return;
		if(root.tag.contentEquals("table")) {
			TagNode ptrRow = root.firstChild;
			int count = 1;
			//loops to needed row
			while(count<row) {
				if(ptrRow.sibling!=null) {
					ptrRow = ptrRow.sibling;
				}
				count++;
			}
			TagNode ptrCol = ptrRow.firstChild;
			//if on the correct row
			while(ptrCol!=null) {
				TagNode temp = new TagNode("b",ptrCol.firstChild,null);
				ptrCol.firstChild = temp;
				ptrCol=ptrCol.sibling;
			}
		}
		boldRow(root.firstChild,row);
		boldRow(root.sibling,row);
	}
	
	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and, 
	 * in addition, all the li tags immediately under the removed tag are converted to p tags. 
	 * 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) {
		/** COMPLETE THIS METHOD **/
		
		remove(root,tag);
	}
	private void remove(TagNode root, String tag){
		if(root == null)return;
		//p,em, b case
		if(tag.contentEquals("p")||tag.contentEquals("em")||tag.contentEquals("b")) {
			if(root.tag.contentEquals(tag)) {
				//change root tag to child tag
				root.tag=root.firstChild.tag;
			
				//ptr to check child level
				TagNode ptr = root.firstChild;
				while(ptr.sibling!=null){
					ptr = ptr.sibling;
				}
				ptr.sibling= root.sibling;
				root.sibling = root.firstChild.sibling;
			
				//move the children underneath up a level
				root.firstChild = root.firstChild.firstChild;
			}
		}
		//ul, ol case: changes li to p
		if(tag.contentEquals("ul")||tag.contentEquals("ol")) {
			if(root.tag.contentEquals(tag)) {
				root.tag = root.firstChild.tag;
				root.tag="p";
				TagNode ptr = root.firstChild;
				while(ptr.sibling!= null) {
					ptr.tag = "p";
					ptr = ptr.sibling;
				}
				ptr.tag="p";
				ptr.sibling = root.sibling;
				root.sibling = root.firstChild.sibling;
				root.firstChild = root.firstChild.firstChild;
			}
		}
		remove(root.firstChild,tag);
		remove(root.sibling,tag);
	}
	
	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */
	public void addTag(String word, String tag) {
		/** COMPLETE THIS METHOD **/
		addTag(root,word.toLowerCase(),tag); 
	}
	private void addTag(TagNode root, String word, String tag) {
		if(root==null)return;
		addTag(root.firstChild,word,tag);
		addTag(root.sibling,word,tag);
		//get to the text level
		if(root.firstChild==null) {
			//need to see if word is in text
			if(root.tag.toLowerCase().contains(word)) {
				//split each word by " "
				String[] text = root.tag.split(" ");
				String tempS = "";
				//loop through the text array
				for(int i = 0;i<text.length;i++) {
					if(!text[i].toLowerCase().contains(word)) {
						tempS += text[i]+" "+"";
					}
					if(text[i].length()<=word.length()+1&&text[i].toLowerCase().contains(word)) {
						
						TagNode temp = new TagNode(text[i],root.firstChild,null);
						root.tag = tag;
						root.firstChild = temp;
						
					}
				}
			}
		}
		
		/**if(root ==null)return;
		if(root.firstChild==null) {//text level
			if(root.tag.toLowerCase().contains(word)) {
				String[]text = root.tag.split(" ");
				int i = 0;
				//only one word
				if(text.length==1) {
					if(text[0].contentEquals(word)) {
						
					}
				}
				if(text[i].contentEquals(word)) {
					String temp = text[i];
					root.tag = tag;
					root.firstChild = new TagNode(temp,root.firstChild,null);
				}
				if(root.tag.toLowerCase().contentEquals(word)){
					String temp = root.tag;
					root.tag = tag;
					root.firstChild = new TagNode(temp,root.firstChild,null);
				}
			}
				//splits the tag by spaces
				String[]text = root.tag.split(" ");
				String holder = "";
				int i = 0;
				if(!text[i].contains(word)) {
					holder+=text[i];
					i++;
				}
			}
		addTag(root.firstChild,word,tag);
		addTag(root.sibling,word,tag);**/
		
		
	}
	
	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines. 
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}
	
	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");	
			}
		}
	}
	
	/**
	 * Prints the DOM tree. 
	 *
	 */
	public void print() {
		print(root, 1);
	}
	
	private void print(TagNode root, int level) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			for (int i=0; i < level-1; i++) {
				System.out.print("      ");
			};
			if (root != this.root) {
				System.out.print("|----");
			} else {
				System.out.print("     ");
			}
			System.out.println(ptr.tag);
			if (ptr.firstChild != null) {
				print(ptr.firstChild, level+1);
			}
		}
	}
}