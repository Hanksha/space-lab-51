package com.calderagames.spacelab.util;

import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MPMParser {

	public Element elemTileMap;
	public Element elemObjects;
	
	public MPMParser(String filePath) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new FileInputStream(filePath));

			Element gameMap = doc.getDocumentElement();

			NodeList list = gameMap.getChildNodes();

			for(int i = 0; i < list.getLength(); i++) {
				Node node = list.item(i);
				if(node.getNodeName().equals("TileMap")) {
					elemTileMap = (Element) node;
				}
				else if(node.getNodeName().equals("MapObjects")) {
					elemObjects = (Element) node;
				}
			}

		} catch(ParserConfigurationException e) {
			e.printStackTrace();
		} catch(SAXException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
}
