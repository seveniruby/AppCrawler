package com.ceshiren.appcrawler.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NodeTest {

    @Test
    void append() {
        Node root=new Node(null);
        root.append(new Node("1"));
    }

    @Test
    void back() {
    }

    @Test
    void current() {
    }
}