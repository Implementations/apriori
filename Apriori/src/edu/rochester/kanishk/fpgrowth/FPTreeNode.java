package edu.rochester.kanishk.fpgrowth;

import java.util.LinkedHashSet;
import java.util.Set;

public class FPTreeNode {
	
	FPTreeNode parent;
	private Set<FPTreeNode> children;
	private Item treeItem;
	int count;
	
	public FPTreeNode(Item i) {
		this();
		this.treeItem = i;
	}
	
	public FPTreeNode() {
		this.children = new LinkedHashSet<>();
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
	
	private FPTreeNode insertChild(Item item, int incrementValue) {
		FPTreeNode node = new FPTreeNode(item);
		children.add(node);
		node.parent = this;
		node.count = incrementValue;
		return node;
	}
	
	/**Traverses the children for the given item. If found, then increment the child counter and return it.
	 * Otherwise inserts the item as a new child node and return it.*/
	public FPTreeNode insertAndReturnChild(Item item, int incrementValue) {
		for(FPTreeNode node : children) {
			if(node.treeItem.equals(item)) {
				node.count += incrementValue;
				return node;
			}
		}
		return insertChild(item, incrementValue);
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
	
	/**Removes this node from the tree by dereferencing itself from parent and removing
	 * itself from parent's child set.
	 */
	public void removeNode() {
		this.parent.children.remove(this);
		this.parent = null;
		this.children.removeAll(children);
	}
	
	@Override
	public String toString() {
		return this.count + "::" + treeItem.toString();
	}
}
