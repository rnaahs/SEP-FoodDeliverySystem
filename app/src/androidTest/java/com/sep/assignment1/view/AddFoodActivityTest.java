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

public class AddFoodActivityTest {
    @Rule
    public ActivityTestRule <AddFoodActivity> rule = new ActivityTestRule<>(AddFoodActivity.class);

    @Test
    public void loginTest() throws Exception{

        onView(withId(R.id.add_food_name_et)).perform(typeText("Pizza"),closeSoftKeyboard());
        onView(withId(R.id.add_food_price_et)).perform(typeText("10"),closeSoftKeyboard());
        onView(withId(R.id.add_food_description_et)).perform(typeText("Pizza is our signature"),closeSoftKeyboard());
        onView(withId(R.id.add_food_btn)).perform(click());
    }
}