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

        onView(withId(R.id.email)).perform(typeText("test@gmail.com"),closeSoftKeyboard());
        onView(withId(R.id.firstname)).perform(typeText("Tung"),closeSoftKeyboard());
        onView(withId(R.id.lastname)).perform(typeText("Wu"),closeSoftKeyboard());
        onView(withId(R.id.address)).perform(typeText("1 Sussex St,Haymarket,NSW,2000"),closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("123456"),closeSoftKeyboard());
        onView(withId(R.id.sign_in_button)).perform(click());
    }

}