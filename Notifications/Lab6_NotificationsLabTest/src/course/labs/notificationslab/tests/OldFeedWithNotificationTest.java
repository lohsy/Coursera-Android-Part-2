	package course.labs.notificationslab.tests;

	import android.test.ActivityInstrumentationTestCase2;
	import android.util.Log;

	import com.robotium.solo.Solo;

	import course.labs.notificationslab.TestFrontEndActivity;

	public class OldFeedWithNotificationTest extends
			ActivityInstrumentationTestCase2<TestFrontEndActivity> {
		private Solo solo;

		public OldFeedWithNotificationTest() {
			super(TestFrontEndActivity.class);
		}

		public void setUp() throws Exception {
			solo = new Solo(getInstrumentation());
			getActivity();
		}

		@Override
		public void tearDown() throws Exception {
			solo.finishOpenedActivities();
		}

		public void testRun() {

			int shortDelay = 2000;

			// Clear the log
			solo.clearLog();
			Log.i("mike", "1");
			// Wait for activity:
			// 'course.labs.notificationslab.TestFrontEndActivity'
			solo.waitForActivity(
					course.labs.notificationslab.TestFrontEndActivity.class,
					shortDelay);
			Log.i("mike", "2");
			// Click on Make Tweets Old
			solo.clickOnView(solo
					.getView(course.labs.notificationslab.R.id.age_tweets_button));
			Log.i("mike", "3");
			// Click on Start Main Activity
			solo.clickOnView(solo
					.getView(course.labs.notificationslab.R.id.start_main_button));
			Log.i("mike", "4");
			// Wait for activity: 'course.labs.notificationslab.MainActivity'
			assertTrue(
					"course.labs.notificationslab.MainActivity is not found!",
					solo.waitForActivity(course.labs.notificationslab.MainActivity.class));
			Log.i("mike", "5");
			// Press menu back key
			solo.goBackToActivity("TestFrontEndActivity");
			Log.i("mike", "6");
/*
			// Wait for activity:
			// 'course.labs.notificationslab.TestFrontEndActivity'
	//		assertTrue(
	//				"course.labs.notificationslab.TestFrontEndActivity is not found!",
	//				solo.waitForActivity(course.labs.notificationslab.TestFrontEndActivity.class));
*/

			// Robotium can't check notification area directly
			String msg = getActivity().getString(course.labs.notificationslab.R.string.notification_sent_string);
			assertTrue("Notification was not sent", solo.waitForText(msg));
			Log.i("mike", "7");
		}
	}
