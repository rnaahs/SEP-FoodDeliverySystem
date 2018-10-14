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
import com.sep.assignment1.model.Restaurant;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)

@LargeTest
public class RestaurantSignupActivityTest {
    @Rule
    public ActivityTestRule <RestaurantSignupActivity> rule = new ActivityTestRule<>(RestaurantSignupActivity.class);

    @Test
    public void loginTest() throws Exception{

        onView(withId(R.id.email)).perform(typeText("driver@gmail.com"),closeSoftKeyboard());
        onView(withId(R.id.firstname)).perform(typeText("Hasson"),closeSoftKeyboard());
        onView(withId(R.id.lastname)).perform(typeText("Al"),closeSoftKeyboard());
        onView(withId(R.id.address)).perform(typeText("23 Pitt St, Sydney,NSW,2000"),closeSoftKeyboard());
        onView(withId(R.id.bsb)).perform(typeText("123456"),closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("123456"),closeSoftKeyboard());
        onView(withId(R.id.sign_up_button)).perform(click());
    }
}