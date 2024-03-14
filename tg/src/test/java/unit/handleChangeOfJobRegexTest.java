package unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class HandleChangeOfJobTest {

    @Test
    void checkRegex(){
        String cbData = "job_id_2_shift_id_55";
        String chosenJobId = "";
        String shiftId = "";
        Pattern jobPattern = Pattern.compile("job_id_(\\d+)");
        Matcher jobMatcher = jobPattern.matcher(cbData);
        if (jobMatcher.find()){
            chosenJobId = jobMatcher.group(1);
        }
        Pattern shiftPattern = Pattern.compile("shift_id_(\\d+)");
        Matcher shiftMatcher = shiftPattern.matcher(cbData);
        if (shiftMatcher.find()){
            shiftId = shiftMatcher.group(1);
        }

        assertEquals("2",chosenJobId);
        assertEquals("55",shiftId);
    }
}