package com.diwayou.zkm.vm;

import java.util.List;

/**
 * Created by cn40387 on 15/7/21.
 */
public class TreeNode {

    private String text;

    private String href;

    private List<TreeNode> nodes;

    public TreeNode() {
    }

    public TreeNode(String text) {
        this.text = text;
    }

    public TreeNode(String text, List<TreeNode> nodes) {
        this.text = text;
        this.nodes = nodes;
    }

    public TreeNode(String text, String href, List<TreeNode> nodes) {
        this.text = text;
        this.href = href;
        this.nodes = nodes;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<TreeNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<TreeNode> nodes) {
        this.nodes = nodes;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
