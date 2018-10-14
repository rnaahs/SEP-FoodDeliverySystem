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

public class BalanceTopupActivityTest {
    @Rule
    public ActivityTestRule <BalanceTopupActivity> rule = new ActivityTestRule<>(BalanceTopupActivity.class);

    @Test
    public void loginTest() throws Exception{

        onView(withId(R.id.topup_card_numET)).perform(typeText("000111222333"),closeSoftKeyboard());
        onView(withId(R.id.topup_card_expireET)).perform(typeText("00/00"),closeSoftKeyboard());
        onView(withId(R.id.topup_card_ccvET)).perform(typeText("123"),closeSoftKeyboard());
        onView(withId(R.id.topup_nameET)).perform(typeText("test name"),closeSoftKeyboard());
        onView(withId(R.id.topup_amountET)).perform(typeText("100"),closeSoftKeyboard());
        onView(withId(R.id.topupBtn)).perform(click());
    }
}