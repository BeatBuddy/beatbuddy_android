import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import be.kdg.teamd.beatbuddy.util.DateUtil;

@RunWith(JUnit4.class)
public class TestDateUtils {

    @Test
    public void TestSecondsToFormattedString(){
        String result = DateUtil.secondsToFormattedString(1);
        Assert.assertEquals("00:01", result);

        result = DateUtil.secondsToFormattedString(60);
        Assert.assertEquals("01:00", result);

        result = DateUtil.secondsToFormattedString(60);
        Assert.assertEquals("01:00", result);

        result = DateUtil.secondsToFormattedString(3600);
        Assert.assertEquals("01:00:00", result);
    }

}
