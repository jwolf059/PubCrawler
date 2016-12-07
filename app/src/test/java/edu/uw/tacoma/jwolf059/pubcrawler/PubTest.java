package edu.uw.tacoma.jwolf059.pubcrawler;

import org.json.JSONArray;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import edu.uw.tacoma.jwolf059.pubcrawler.model.Pub;

import static org.junit.Assert.*;


public class PubTest {

    //Testing Fixtures
    public String testJSON = "{\"html_attributions\" : [],\"next_page_token\" : \"CpQCBgEAAGZZl_3TnyzJk-8891RMKm8X0gQZhQDsUijLr_q5DfbBflaBjA2SOBekiAHuWtJVE1FV-0NNYhCdsW2craHKTQJgRfC5PIfJAkqIH19Lfyd_Mkr-Ap9ZSZOT-OxhvOnGxz2YbWTwctssPIaRI_Nuyu78p-RRaf8o2IlwgW8zpJgrMNU0rcEvdkJifDY0K6TECERbxdx2EdcO9dLe64tnHfO-WFxyqADULDGOU_AMVCbEL0c4Ldq-uxsCOOBfWafIgiUNEA-JnmMf6rnIXlHrPNQEr7jGwjKzE4d0XuVZ7Ubd-OOK9cdcw6PNeGPCCfwSeiW9jH6Hn63_noLPMOIns16RBiZ8ev8o-2N1LEhjCUseEhAG5k9IFslHaM6QoWcyVD2mGhRljRPqkH8cFKDOghiaZZ2UYRHLbg\",\"results\" : [{\"geometry\" : {\"location\" : {\"lat\" : 47.2276313,\"lng\" : -122.477323},\"viewport\" : {\"northeast\" : {\"lat\" : 47.2282571,\"lng\" : -122.47686145},\"southwest\" : {\"lat\" : 47.2274227,\"lng\" : -122.47747685}}},\"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/generic_business-71.png\",\"id\" : \"a4be967c56e552f351be5d04722c194fcc9dab4d\",\"name\" : \"Gig Harbor Brewing Co.\",\"opening_hours\" : {\"open_now\" : false,\"weekday_text\" : []},\"photos\" : [{\"height\" : 412,\"html_attributions\" : [\"\\u003ca href=\\\"https://maps.google.com/maps/contrib/116524697439482025826/photos\\\"\\u003eGig Harbor Brewing Co.\\u003c/a\\u003e\"],\"photo_reference\" : \"CoQBdwAAAEyO4EjQW2p3GsXv847ar9f5V-Jmo3X2-ZyijKTEZ3ydt3t7aE8M1nv3oaiGvtlW2XcS4RmNZwebiJeKOXg2_FeC1Aca3Rh2dvVu61k8ovbcjDyXNzmU1ml3Q1M7uf1omSZJVSGr5Dzs0WNHuJtCcuf1n2Y41I9TCIIkX0QOMCOVEhA06MBefO4W7xgCotRVbuXsGhSArwm7MOLgd2xCrRITL1golBhy6Q\",\"width\" : 412}],\"place_id\" : \"ChIJJ6O5RFJVkFQR-c_eE681BMc\",\"rating\" : 4.2,\"reference\" : \"CmRSAAAAa-MQ48nk6QvzLbASbpjZLNkZqD8PC2mPArl-Ja87-gvZi7OqSGdNWSpewJbKiqPdBMJ-T4LmmEtDUPcQOCJcmlfieN0wZnkU7RtO4ZGv6N--bJLGVRBp0lFHM58pNaMeEhAX8FGazTUYtemSIZSJ08RJGhQhrfu07Wkx7yAi-fWCNqaQbwdSoA\",\"scope\" : \"GOOGLE\",\"types\" : [ \"food\", \"point_of_interest\", \"establishment\" ],\"vicinity\" : \"3120 South Tacoma Way, Tacoma\"}],\"status\" : \"OK\"}";
    public String theTestPlaceID = "ChIJJ6O5RFJVkFQR-c_eE681BMc";
    public String theName = "Frothy Awesome";
    public double theLat = 37.3654;
    public double theLng = 142.3654;
    public double theRating = 3.8;
    public String thePlaceID = "a4be967c56e552f351be5d04722c194fcc9dab4d";
    public boolean isOpen = true;
    public String theAddress = "2341 North 4th Ave, Olympia, WA 98513";
    public String theFood = "Yes";
    public Pub mPub = new Pub(theName, theLat, theLng, theRating, thePlaceID, isOpen, theAddress, theFood);

    @Test
    public void parsePubJSON() {

            ArrayList<Pub> mPubList = Pub.parsePubJSON(testJSON);
            Pub testPub = mPubList.get(0);
            assertEquals("Name does not match", "Gig Harbor Brewing Co.", testPub.getmName());
            assertTrue("theLat does not match", 47.2276313 == testPub.getmLat());
            assertTrue("theLng does not match", -122.477323 == testPub.getmLng());
            assertTrue("theRating does not match", 4.2 == testPub.getmRating() );
            assertEquals("thePlaceID does not match" + testPub.getmPlaceID(), theTestPlaceID, testPub.getmPlaceID());
            assertEquals("IsOpen does not match", false, testPub.getIsOpen() );
            assertEquals("theAddress does not match", "3120 South Tacoma Way, Tacoma" ,testPub.getmAddress());
            assertEquals("hasFood does not match", "Yes", testPub.getmHasFood());
    }


    @Test
    public void testGetIsOpen() {
        assertEquals(isOpen, mPub.getIsOpen());
    }


    @Test
    public void testGetmAddress() {
        assertEquals(theAddress, mPub.getmAddress());
    }

    @Test
    public void testGetmHasFood() {
        assertEquals(theFood, mPub.getmHasFood());
    }


    @Test
    public void testGetmLat() {
        assertTrue(theLat == mPub.getmLat());
    }


    @Test
    public void testGetLng() {
        assertTrue(theLng == mPub.getmLng());
    }


    @Test
    public void testGetmName() {
        assertEquals(theName, mPub.getmName());
    }


    @Test
    public void testGetmPlaceID() {
        assertEquals(thePlaceID, mPub.getmPlaceID());
    }


    @Test
    public void testGetmRating() {
        assertTrue(theRating == mPub.getmRating());
    }
}
