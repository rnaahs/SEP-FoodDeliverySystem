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

public class AddRestaurantActivityTest {
    @Rule
    public ActivityTestRule <AddRestaurantActivity> rule = new ActivityTestRule<>(AddRestaurantActivity.class);

    @Test
    public void loginTest() throws Exception{

        onView(withId(R.id.add_restaurant_nameET)).perform(typeText("new restaurant name"),closeSoftKeyboard());
        onView(withId(R.id.add_restaurant_typeET)).perform(typeText("test type"),closeSoftKeyboard());
        onView(withId(R.id.add_restaurant_countryET)).perform(typeText("test country"),closeSoftKeyboard());
        onView(withId(R.id.add_restaurant_addressET)).perform(typeText("test Road,test suburb,NSW,2000"),closeSoftKeyboard());
        onView(withId(R.id.add_restaurant_statusET)).perform(typeText("test status"),closeSoftKeyboard());
        onView(withId(R.id.add_restaurantBtn)).perform(click());
    }
}