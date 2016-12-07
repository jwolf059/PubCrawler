package edu.uw.tacoma.jwolf059.pubcrawler;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import edu.uw.tacoma.jwolf059.pubcrawler.login.LoginActivity;
import edu.uw.tacoma.jwolf059.pubcrawler.login.RegisterActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by jwolf on 11/15/2016.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest

public class RegisterActivityTest {
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

    @Rule
    public ActivityTestRule<RegisterActivity> mActivityRule2 = new ActivityTestRule<>(
            RegisterActivity.class);


    @Before
    public void logout() {

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(mActivityRule.getActivity());
        sharedPreferences.edit().putBoolean("loggedin", false).commit();

        onView(withId(R.id.register))
                .perform(click());

    }


    @Test
    public void testRegister() {

        Random random = new Random();
        //Generate an email address
        String email = "email" + (random.nextInt(7) + 1)
                + (random.nextInt(8) + 1) + (random.nextInt(9) + 1)
                + (random.nextInt(100) + 1) + (random.nextInt(4) + 1)
                + "@uw.edu";

        // Type text and then press the button.
        onView(withId(R.id.userid_edit))
                .perform(typeText(email));
        onView(withId(R.id.userid_edit)).perform(closeSoftKeyboard());

        onView(withId(R.id.pwd_edit))
                .perform(typeText("myPassword"));
        onView(withId(R.id.pwd_edit)).perform(closeSoftKeyboard());

        onView(withId(R.id.pwdVer_edit))
                .perform(typeText("myPassword"));
        onView(withId(R.id.pwdVer_edit)).perform(closeSoftKeyboard());

        onView(withId(R.id.register_button))
                .perform(click());

        onView(withText("You have successfully registered and logged in"))
                .inRoot(withDecorView(not(is(
                        mActivityRule.getActivity()
                                .getWindow()
                                .getDecorView()))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testRegisterInvalidEmail() {
        // Type text and then press the button.
        onView(withId(R.id.userid_edit))
                .perform(typeText("Jeremy.edu.com"));
        onView(withId(R.id.userid_edit)).perform(closeSoftKeyboard());

        onView(withId(R.id.pwd_edit))
                .perform(typeText("myPassword"));
        onView(withId(R.id.pwd_edit)).perform(closeSoftKeyboard());

        onView(withId(R.id.pwdVer_edit))
                .perform(typeText("myPassword"));
        onView(withId(R.id.pwdVer_edit)).perform(closeSoftKeyboard());

        onView(withId(R.id.register_button))
                .perform(click());

        onView(withText("Enter a valid email address"))
                .inRoot(withDecorView(not(is(
                        mActivityRule.getActivity()
                                .getWindow()
                                .getDecorView()))))
                .check(matches(isDisplayed()));

    }


    @Test
    public void testRegisterBlankEmail() {
        // Type text and then press the button.
        onView(withId(R.id.userid_edit))
                .perform(typeText(""));
        onView(withId(R.id.userid_edit)).perform(closeSoftKeyboard());

        onView(withId(R.id.pwd_edit))
                .perform(typeText("myPassword"));
        onView(withId(R.id.pwd_edit)).perform(closeSoftKeyboard());

        onView(withId(R.id.pwdVer_edit))
                .perform(typeText("myPassword"));
        onView(withId(R.id.pwdVer_edit)).perform(closeSoftKeyboard());

        onView(withId(R.id.register_button))
                .perform(click());

        onView(withText("Enter your Email Address"))
                .inRoot(withDecorView(not(is(
                        mActivityRule.getActivity()
                                .getWindow()
                                .getDecorView()))))
                .check(matches(isDisplayed()));

    }
    @Test
    public void testRegisterInvalidPassword() {

        Random random = new Random();
        //Generate an email address
        String email = "email" + (random.nextInt(7) + 1)
                + (random.nextInt(8) + 1) + (random.nextInt(9) + 1)
                + (random.nextInt(100) + 1) + (random.nextInt(4) + 1)
                + "@uw.edu";

        // Type text and then press the button.
        onView(withId(R.id.userid_edit))
                .perform(typeText(email));
        onView(withId(R.id.userid_edit)).perform(closeSoftKeyboard());

        onView(withId(R.id.pwd_edit))
                .perform(typeText("myPas"));
        onView(withId(R.id.pwd_edit)).perform(closeSoftKeyboard());

        onView(withId(R.id.pwdVer_edit))
                .perform(typeText("myPas"));
        onView(withId(R.id.pwdVer_edit)).perform(closeSoftKeyboard());

        onView(withId(R.id.register_button))
                .perform(click());

        onView(withText("Enter password of at least 6 characters"))
                .inRoot(withDecorView(not(is(
                        mActivityRule.getActivity()
                                .getWindow()
                                .getDecorView()))))
                .check(matches(isDisplayed()));



    }

    @Test
    public void testRegisterEmptyPassword() {

        Random random = new Random();
        //Generate an email address
        String email = "email" + (random.nextInt(7) + 1)
                + (random.nextInt(8) + 1) + (random.nextInt(9) + 1)
                + (random.nextInt(100) + 1) + (random.nextInt(4) + 1)
                + "@uw.edu";

        // Type text and then press the button.
        onView(withId(R.id.userid_edit))
                .perform(typeText(email));
        onView(withId(R.id.userid_edit)).perform(closeSoftKeyboard());

        onView(withId(R.id.pwd_edit))
                .perform(typeText(""));
        onView(withId(R.id.pwd_edit)).perform(closeSoftKeyboard());

        onView(withId(R.id.pwdVer_edit))
                .perform(typeText(""));
        onView(withId(R.id.pwdVer_edit)).perform(closeSoftKeyboard());

        onView(withId(R.id.register_button))
                .perform(click());

        onView(withText("Enter password"))
                .inRoot(withDecorView(not(is(
                        mActivityRule.getActivity()
                                .getWindow()
                                .getDecorView()))))
                .check(matches(isDisplayed()));

    }

    @Test
    public void testRegisterPasswordsDontMatch() {

        Random random = new Random();
        //Generate an email address
        String email = "email" + (random.nextInt(7) + 1)
                + (random.nextInt(8) + 1) + (random.nextInt(9) + 1)
                + (random.nextInt(100) + 1) + (random.nextInt(4) + 1)
                + "@uw.edu";

        // Type text and then press the button.
        onView(withId(R.id.userid_edit))
                .perform(typeText(email));
        onView(withId(R.id.userid_edit)).perform(closeSoftKeyboard());

        onView(withId(R.id.pwd_edit))
                .perform(typeText("mypassword"));
        onView(withId(R.id.pwd_edit)).perform(closeSoftKeyboard());

        onView(withId(R.id.pwdVer_edit))
                .perform(typeText("myPassword"));
        onView(withId(R.id.pwdVer_edit)).perform(closeSoftKeyboard());

        onView(withId(R.id.register_button))
                .perform(click());

        onView(withText("Passwords don't match. Please retype passwords"))
                .inRoot(withDecorView(not(is(
                        mActivityRule.getActivity()
                                .getWindow()
                                .getDecorView()))))
                .check(matches(isDisplayed()));

    }




}
