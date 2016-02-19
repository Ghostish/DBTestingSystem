package com.bbt.kangel.dbtesingsystem.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Kangel on 2015/12/13.
 */
public class TestDataBaseUtil {
    static final int STUDENT = 0, TEACHER = 1;

    public static Cursor getPeopleList(SQLiteDatabase db, String notInClause) {
        notInClause = notInClause != null ? notInClause : "('')";
        return db.rawQuery("select NAME, ID, TYPE from(\n" +
                "select SNAME NAME, SNO ID, 0 TYPE from students\n" +
                " union\n" +
                " select TNAME NAME, TNO ID, 1 TYPE from teachers\n" +
                " ) where ID not in " + notInClause, null);
    }

    public static Cursor getPaperList(SQLiteDatabase db) {
        return db.rawQuery("select * from papers order by PID", null);
    }

    public static Cursor getPaperUnmarked(SQLiteDatabase db, String PID) {
        Cursor c;
        c = db.rawQuery("select x.SNO ,SNAME ,'unmarked' GRADE , ? PID from students x  where exists (select * from students y, gapAnswers  where PID = ? and x.SNO = y.SNO and gapAnswers.SNO = y.SNO  and gapAnswers.isMarked = 0) or exists (select * from students z, essayAnswers  where PID = ? and x.SNO = z.SNO and essayAnswers.SNO = z.SNO  and essayAnswers.isMarked = 0)", new String[]{PID, PID, PID});
        return c;
    }

    public static Cursor getQuestionUnmarked(SQLiteDatabase db, String sno, String pid) {
        return db.rawQuery("select gapQuestion.CONTENT CONTENT,gapAnswers.ANSWER ANSWER,gapInPapers.SCORE SCORE,gapAnswers.QID QID, 1 TYPE from gapQuestion,gapAnswers,gapInPapers where ISMARKED = 0 and  gapAnswers.PID = ? and gapAnswers.SNO = ? and gapQuestion.QID = gapAnswers.QID and gapInPapers.QID = gapAnswers.QID \n" +
                "union\n" +
                " select essayQuestion.CONTENT CONTENT,essayAnswers.ANSWER ANSWER,essayInPapers.SCORE SCORE,essayAnswers.QID QID,2 TYPE from essayQuestion,essayAnswers,essayInPapers where  ISMARKED = 0 and essayAnswers.PID = ? and essayAnswers.SNO = ? and essayQuestion.QID = essayAnswers.QID and essayInPapers.QID = essayAnswers.QID\n", new String[]{pid, sno, pid, sno});
    }

    /*return a cursor with column SNO,SNAME,PID,GRADE*/
    public static Cursor getGrade(SQLiteDatabase db, String SNO) {
        if (SNO != null) {
            return db.rawQuery("select grades.SNO SNO,SNAME,PID,GRADE from grades ,students where grades.SNO = ? and grades.SNO = students.SNO", new String[]{SNO});
        } else {
            return db.rawQuery("select grades.SNO SNO,SNAME,PID,GRADE from grades ,students where grades.SNO = students.SNO", null);
        }
    }

    public static Cursor getGradeDetail(SQLiteDatabase db, String SNO) {
        return db.rawQuery("select choiceScore.PID PID,choiceScore.SUMSCORE CHOICEGRADE,gapScore.SUMSCORE GAPGRADE,essayScore.SUMSCORE ESSAYGRADE from choiceScore  left join gapScore on choiceScore.PID = gapScore.PID left join essayScore on essayScore.PID = gapScore.PID where choiceScore.SNO = ? and gapScore.SNO = ? and essayScore.SNO = ?", new String[]{SNO, SNO, SNO});
    }

    public static Cursor getGradesList(SQLiteDatabase db, String PID, String orderBy, String fromGrade, String toGrade) {
        return db.rawQuery("select grades.*,SNAME from grades,students where students.SNO = grades.SNO  and PID = " + PID + " and GRADE between " + fromGrade + " and " + toGrade + " order by " + orderBy, null);
    }

    /*返回一张考卷的具体内容
    * 返回的是一个包含三个游标的游标数组，
    * 第一个是选择题游标，包含每道选择题的绝对题号，题目内容，分别四个选项的具体内容，标准答案，以及在该试卷中的分值
    * 第二个是填空题游标，包含每道填空题的绝对题号，题目内容，参考答案，以及在该试卷中的分值
    * 第三个是简答题游标，列的内容和填空题相似*/
    public static Cursor[] getPaperDetail(SQLiteDatabase db, int PID) {
        Cursor[] c = new Cursor[3];
        String[] selectionArg = new String[]{PID + ""};
        c[0] = db.rawQuery("select choiceQuestion.QID QID,CONTENT,CHOICEA,CHOICEB,CHOICEC,CHOICED,ANSWER,SCORE from choiceQuestion,choiceInPapers where PID = ? and choiceQuestion.QID = choiceInPapers.PID", selectionArg);
        c[1] = db.rawQuery("select gapQuestion.QID QID,CONTENT,ANSWER,SCORE from gapQuestion,gapInPapers where PID = ? and gapQuestion.QID = gapInPapers.QID", selectionArg);
        c[2] = db.rawQuery("select essayQuestion.QID QID,CONTENT,ANSWER,SCORE from essayQuestion,essayInPapers where PID = ? and essayQuestion.QID = essayInPapers.QID", selectionArg);
        return c;
    }

    /*返回学生的作答信息
    * 其中选择题包含每题的绝对题号，题目内容，分别四个选项的内容，学生给出的答案，以及系统对这一题的给分。
    * 其中填空题,简答题包含每题的绝对题号，题目内容，学生的给出的答案，以及老师对这一题的评分,score 为-1 表示尚未评分*/
    public static Cursor[] getStudentAnswerDetail(SQLiteDatabase db, String SNO, int PID) {
        Cursor[] c = new Cursor[3];
        String[] selectionArgs = new String[]{SNO, PID + ""};
        c[0] = db.rawQuery("select choiceQuestion.QID QID,CONTENT,CHOICEA,CHOICEB,CHOICEC,CHOICED,choiseAnswers.ANSWER,SCORE from choiseAnswers ,choiceInPapers where SNO = ? and PID = ? and choiseAnswers.QID = choiceQuestion.QID", selectionArgs);
        c[1] = db.rawQuery("select gapQuestion.QID QID,CONTENT,gapAnswers.ANSWER,SCORE from gapAnswers,gapQuestion where SNO = ? and PID = ? and gapAnswers.QID = gapQuestion.QID", selectionArgs);
        c[2] = db.rawQuery("select essayQuestion.QID QID,CONTENT,essayAnswers.ANSWER,SCORE from essayAnswers,essayQuestion where SNO = ? and PID = ? and essayAnswers.QID = essayQuestion.QID", selectionArgs);
        return c;
    }

}
