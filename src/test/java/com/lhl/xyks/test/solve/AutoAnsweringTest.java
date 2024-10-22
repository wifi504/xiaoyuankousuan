package com.lhl.xyks.test.solve;

import com.lhl.xyks.solve.AutoAnswering;
import com.lhl.xyks.solve.DivisionOCR;
import com.lhl.xyks.utils.ConfigParser;
import org.junit.Test;

/**
 * @author lhl
 * @version 1.0
 * Create Time 2024/10/21_17:35
 */
public class AutoAnsweringTest {
    @Test
    public void testExecute() {
        ConfigParser.loadGlobalConfig();
        AutoAnswering.execute(new DivisionOCR(), 10);
    }
}
