package com.sep.assignment1.view;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Rule;
import org.junit.runner.RunWith;
import android.support.test.rule.ActivityTestRule;
import org.junit.Test;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.action.ViewActions.*;
import com.sep.assignment1.R;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)

@LargeTest



public class DriverSignupActivityTest {
    @Rule
    public ActivityTestRule <DriverSignupActivity> rule = new ActivityTestRule<>(DriverSignupActivity.class);

    @Test
    public void loginTest() throws Exception{

        onView(withId(R.id.email)).perform(typeText("newdriver@gmail.com"),closeSoftKeyboard());
        onView(withId(R.id.firstname)).perform(typeText("newdriverfirstname"),closeSoftKeyboard());
        onView(withId(R.id.lastname)).perform(typeText("newldriverastname"),closeSoftKeyboard());
        onView(withId(R.id.address)).perform(typeText("new Road,new suburb,NSW,2000"),closeSoftKeyboard());
        onView(withId(R.id.licence)).perform(typeText("12345678"),closeSoftKeyboard());
        onView(withId(R.id.vehicle)).perform(typeText("newvehicle"),closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("driverpassword"),closeSoftKeyboard());
        onView(withId(R.id.sign_up_button)).perform(click());
    }

}