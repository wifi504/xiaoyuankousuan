package com.lhl.xyks.test.pojo;

import com.lhl.xyks.pojo.Question;
import org.junit.Test;

/**
 * @author lhl
 * @version 1.0
 * Create Time 2024/10/21_17:30
 */
public class QuestionTest {
    @Test
    public void testQuestion() {
        Question q1 = new Question(1);
        Question q2 = new Question(2);
        Question q3 = new Question(3);
        System.out.println(q1);
        System.out.println(q2);
        System.out.println(q3);
        q1.updateExpression("3.14*2=?:Test模型");
        q2.updateExpression("100/(5+5)=?:Test模型");
        q3.updateExpression("1588?1432:Test模型");
        System.out.println(q1);
        System.out.println(q2);
        System.out.println(q3);
    }
}
