package edu.uw.tacoma.jwolf059.pubcrawler;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import edu.uw.tacoma.jwolf059.pubcrawler.login.LoginActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by jwolf on 11/15/2016.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest

public class LoginActivityTest {
    /**
     * A JUnit {@link Rule @Rule} to launch your activity under test.
     * Rules are interceptors which are executed for each test method and will run before
     * any of your setup code in the {@link @Before} method.
     * <p>
     * {@link ActivityTestRule} will create and launch of the activity for you and also expose
     * the activity under test. To get a reference to the activity you can use
     * the {@link ActivityTestRule#getActivity()} method.
     */
    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<>(
            LoginActivity.class);


    @Before
    public void logout() {

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(mActivityRule.getActivity());
        sharedPreferences.edit().putBoolean("loggedin", false).commit();
    }


    @Test
    public void testRegister() {

        // Type text and then press the button.
        onView(withId(R.id.userid_edit))
                .perform(typeText("test@uw.edu"));
        onView(withId(R.id.userid_edit)).perform(closeSoftKeyboard());
        onView(withId(R.id.password))
                .perform(typeText("myPassword"));
        onView(withId(R.id.password)).perform(closeSoftKeyboard());
        onView(withId(R.id.sign_in))
                .perform(click());

        onView(withText("Logged In"))
                .inRoot(withDecorView(not(is(
                        mActivityRule.getActivity()
                                .getWindow()
                                .getDecorView()))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testLoginNotRegistered() {
        // Type text and then press the button.
        onView(withId(R.id.userid_edit))
                .perform(typeText("Thisisnotregistered@uw.edu."));
        onView(withId(R.id.userid_edit)).perform(closeSoftKeyboard());
        onView(withId(R.id.password))
                .perform(typeText("test1@#"));
        onView(withId(R.id.password)).perform(closeSoftKeyboard());

        onView(withId(R.id.sign_in))
                .perform(click());

        onView(withText("Incorrect Username or Password:"))
                .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));

    }




}
