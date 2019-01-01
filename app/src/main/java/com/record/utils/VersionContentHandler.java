package com.record.utils;

import com.record.bean.Version;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class VersionContentHandler extends DefaultHandler {
    private String tagName = null;
    private Version version = null;

    public Version getVersion() {
        return this.version;
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        String temp = new String(ch, start, length);
        if (this.tagName.equals("versionNum")) {
            this.version.setVersionNum(temp);
        } else if (this.tagName.equals("versionCode")) {
            this.version.setVersionCode(temp);
        } else if (this.tagName.equals("size")) {
            this.version.setSize(temp);
        } else if (this.tagName.equals("downloadUrl")) {
            this.version.setDownloadUrl(temp);
        } else if (this.tagName.equals("describe")) {
            this.version.setDescribe(temp);
        } else if (this.tagName.equals("updateTime")) {
            this.version.setUpdateTime(temp);
        }
        super.characters(ch, start, length);
    }

    public void endDocument() throws SAXException {
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("version")) {
        }
        this.tagName = "";
    }

    public void startDocument() throws SAXException {
        super.startDocument();
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        this.tagName = localName;
        if (this.tagName.equals("version")) {
            this.version = new Version();
        }
        super.startElement(uri, localName, qName, attributes);
    }
}
