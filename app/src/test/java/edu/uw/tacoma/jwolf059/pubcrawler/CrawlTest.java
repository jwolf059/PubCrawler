package edu.uw.tacoma.jwolf059.pubcrawler;

import org.json.JSONArray;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import edu.uw.tacoma.jwolf059.pubcrawler.model.Pub;

import static org.junit.Assert.*;


import edu.uw.tacoma.jwolf059.pubcrawler.model.Crawl;
import edu.uw.tacoma.jwolf059.pubcrawler.model.Pub;

/**
 * Created by jwolf on 12/6/2016.
 */

public class CrawlTest {

    Crawl mCrawl = new Crawl("Test Crawl");
    Pub p1 = new Pub("Pub 1", 47.2436559, -122.4367085, 3.5, "asdf23143lkdsoi", false, "420 Franklin Street Southeast, Olympia, WA", "Yes");
    Pub p2 = new Pub("Pub 2", 47.16579710000001, -122.5153313, 3.5, "asdf23143lkdsoi", false, "1833 4th Avenue East, Olympia, WA 98506", "Yes");
    Pub p3 = new Pub("Pub 3", 24.3542, 64.3254, 3.5, "asdf23143lkdsoi", false, "1234 NE 206th, Tacoma, WA", "Yes");
    Pub p4 = new Pub("Pub 4", 24.3542, 64.3254, 3.5, "asdf23143lkdsoi", false, "1234 NE 206th, Tacoma, WA", "Yes");
    Pub p5 = new Pub("Pub 5", 24.3542, 64.3254, 3.5, "asdf23143lkdsoi", false, "1234 NE 206th, Tacoma, WA", "Yes");


    @Before
    public void setup() {
        Crawl mCrawl = new Crawl("Test Crawl");
    }

    @Test
    public void testAddPubVaildInput() {
        mCrawl.addPub(p1);
        assertTrue(mCrawl.getmCrawlPath().size() == 1);
    }

    @Test
    public void testAddPubNullPub() {
        mCrawl.addPub(null);
        assertTrue(mCrawl.getmCrawlPath().size() == 0);
    }
    @Ignore("Requires mocked AsyncTask, when mocked data values are not recieved from Google")
    @Test
    public void caculateDistance() {
        mCrawl.addPub(p1);
        mCrawl.addPub(p2);
        mCrawl.caculateDistance();
        assertTrue(1823 == mCrawl.getmDistance().get(1));
    }

    @Test
    public void cleanSpaces() {
        String spaces = "All Spaces,. Be@ Removed #1230";
        assertEquals("All%20Spaces,.%20Be@%20Removed%20%231230", mCrawl.cleanSpace(spaces));
    }

    @Test
    public void getMCrawlPath() {
        mCrawl.addPub(p1);
        mCrawl.addPub(p2);
        mCrawl.addPub(p3);
        mCrawl.addPub(p4);

        ArrayList<Pub> list = mCrawl.getmCrawlPath();
        assertTrue(4 == list.size());
    }

    @Test
    public void getmName() {
        assertEquals("Test Crawl", mCrawl.getmName());
    }
    @Ignore("Requires mocked AsyncTask, when mocked data values are not recieved from Google")
    @Test
    public void testRandomCrawlCreation() {
        ArrayList<Pub> list = new ArrayList<>();
        list.add(p1);
        list.add(p2);
        list.add(p3);
        list.add(p4);
        list.add(p5);
        mCrawl.randomCrawlCreation(list, p1, 3, true);
        assertTrue(mCrawl.getmCrawlPath().size() == 3);
    }
}
