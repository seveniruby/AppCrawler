package com.ceshiren.appcrawler.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Node {
    static Stack<Node> stack=new Stack<>();
    static Node current;
    Object data;
    List<Node> children=new ArrayList<>();

    public Node(Object data){
        this.data=data;
    }
    public void append(Node node){
        Node.current.children.add(node);
        Node.stack.push(node);
        Node.current=node;
    }

    public void back(){
        Node.current=Node.stack.pop();
    }

    public void current(){

    }
}
