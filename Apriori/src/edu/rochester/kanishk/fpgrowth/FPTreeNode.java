package edu.rochester.kanishk.fpgrowth;

import java.util.ArrayList;
import java.util.List;

public class FPTreeNode {
	
	FPTreeNode parent;
	private List<FPTreeNode> children;
	private Item treeItem;
	int count;
	
	public FPTreeNode(Item i) {
		this();
		this.treeItem = i;
	}
	
	public FPTreeNode() {
		this.children = new ArrayList<>();
	}
	
	public boolean isRoot() {
		return treeItem == null;
	}
	
	public Item getTreeItem() {
		return treeItem;
	}

	@Override
	public int hashCode() {
		return treeItem.hashCode();
	}
	
	private FPTreeNode insertChild(Item item) {
		FPTreeNode node = new FPTreeNode(item);
		children.add(node);
		node.parent = this;
		node.count = 1;
		return node;
	}
	
	/**Traverses the children for the given item. If found, then increment the child counter and return it.
	 * Otherwise inserts the item as a new child node and return it.*/
	public FPTreeNode insertAndReturnChild(Item item) {
		for(FPTreeNode node : children) {
			if(node.treeItem.equals(item)) {
				node.count += 1;
				return node;
			}
		}
		return insertChild(item);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FPTreeNode other = (FPTreeNode) obj;
		if (treeItem == null) {
			if (other.treeItem != null)
				return false;
		} else if (!treeItem.equals(other.treeItem))
			return false;
		return true;
	}
}
