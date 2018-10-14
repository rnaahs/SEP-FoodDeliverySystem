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
public class UserSignupActivityTest {
    @Rule
    public ActivityTestRule <UserSignupActivity> rule = new ActivityTestRule<>(UserSignupActivity.class);

    @Test
    public void loginTest() throws Exception{

        onView(withId(R.id.email)).perform(typeText("newuser@gmail.com"),closeSoftKeyboard());
        onView(withId(R.id.firstname)).perform(typeText("newfirstname"),closeSoftKeyboard());
        onView(withId(R.id.lastname)).perform(typeText("newlastname"),closeSoftKeyboard());
        onView(withId(R.id.address)).perform(typeText("new Road,new suburb,NSW,2000"),closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("newopassword"),closeSoftKeyboard());
        onView(withId(R.id.sign_in_button)).perform(click());
    }

}