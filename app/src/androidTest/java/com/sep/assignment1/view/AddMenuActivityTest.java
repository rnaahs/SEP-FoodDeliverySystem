package com.sep.assignment1.view;

import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.runner.RunWith;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.TextView;
import org.junit.Rule;
import org.junit.Test;
import org.junit.*;
import org.junit.runner.RunWith;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import com.sep.assignment1.R;



import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)

@LargeTest

public class AddMenuActivityTest {
    @Rule
    public ActivityTestRule <AddMenuActivity> rule = new ActivityTestRule<>(AddMenuActivity.class);

    @Test
    public void loginTest() throws Exception{

        onView(withId(R.id.add_menu_name_et)).perform(typeText("Pizza"),closeSoftKeyboard());
        onView(withId(R.id.add_menuInstance_btn)).perform(click());
    }
}