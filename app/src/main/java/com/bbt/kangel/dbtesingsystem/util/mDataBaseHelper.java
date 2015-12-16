package com.bbt.kangel.dbtesingsystem.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Deque;

/**
 * Created by Kangel on 2015/12/7.
 */
public class mDataBaseHelper extends SQLiteOpenHelper {
    static final String[] CREATE = new String[]{"create table students (SNO varchar(12) primary key ,SNAME varchar(10),MAJOR varchar(20),PASSWORD varchar(12) not null);\n",
            "create table teachers (TNO varchar(12) primary key , TNAME varchar(10), PASSWORD varchar(12) not null);\n",
            "create table deans (DNO varchar(12) primary key , DNAME varchar(10) , PASSWORD varchar(12) not null);\n",
            "create table choiceQuestion (QID integer primary key autoincrement, CONTENT varchar(100) not null unique, CHOICEA varchar(20) not null, CHOICEB varchar(20) not null,CHOICEC varchar(20) not null,CHOICED varchar(20) not null,ANSWER char(1) check(ANSWER in ('A','B','C','D')));\n",
            "create table gapQuestion (QID integer primary key autoincrement, CONTENT varchar(100) not null unique, ANSWER varchar(50) not null);\n",
            "create table essayQuestion (QID integer primary key autoincrement, CONTENT varchar(300) not null unique, ANSWER varchar (300) not null);\n",
            "create table papers(PID integer primary key autoincrement,CREATETIME varchar(10));\n",
            "create table choiceInPapers(PID integer references papers(PID),QID integer references choiceQuestion(QID),SCORE integer not null,primary key(PID,QID));\n",
            "create table gapInPapers(PID integer references papers(PID),QID integer references gapQuestion(QID),SCORE integer not null,primary key(PID,QID));\n",
            "create table essayInPapers(PID integer references papers(PID),QID integer references essayQuestion(QID),SCORE integer not null,primary key(PID,QID));\n",
            "create table grades (SNO varchar(12) references students(SNO),PID varchar(10) references papers(PID),GRADE integer check(GRADE >=0 AND GRADE <=100),primary key (SNO,PID));\n",
            "create table choiceAnswers(SNO varchar(12) references students(SNO), PID integer references papers(PID),QID integer references choiceQuestion(QID) , ANSWER char(1) check(ANSWER in ('A','B','C','D','#')) ,SCORE integer, primary key(SNO,PID,QID));\n",
            "create table gapAnswers(SNO varchar(12) references students(SNO), PID integer references papers(PID),QID integer references gapQuestion(QID) , ANSWER varchar(50),SCORE integer,primary key(SNO,PID,QID));\n",
            "create table essayAnswers(SNO varchar(12) references students(SNO), PID integer references papers(PID),QID integer references essayQuestion(QID) , ANSWER varchar(300),SCORE integer,primary key(SNO,PID,QID));\n",
            "create view choiceScore as select SNO,PID,SUM(SCORE) SUMSCORE from choiceAnswers group by SNO,PID",
            "create view gapScore as select SNO,PID,SUM(SCORE) SUMSCORE from gapAnswers  group by SNO,PID\n",
            "create view essayScore as select SNO,PID,SUM(SCORE) SUMSCORE from essayAnswers  group by SNO,PID"};
    static final String INSERT_STUDENTS = "insert into students values\n" +
            "('201330571059','郑超达','信息安全','123456'),\n" +
            "('201330571001','艾米丽','信息安全','123456'),\n" +
            "('201330571002','李大伟','网络工程','123456'),\n" +
            "('201330571003','陈艾玛','网络工程','123456'),\n" +
            "('201330571004','王露西','计算机科学','123456'),\n" +
            "('201330571005','赵南希','计算机科学','123456'),\n" +
            "('201330571006','周莉莉','软件工程','123456'),\n" +
            "('201330571007','王凯利','软件工程','123456'),\n" +
            "('201330571008','王罗伊','电子信息','123456'),\n" +
            "('201330571009','谢路易','电子信息','123456');";
    static final String INSERT_TEACHERS = "insert into teachers values\n" +
            "('20012057','王楚然','123456'),\n" +
            "('20012058','姜旭','123456'),\n" +
            "('20012059','石亭','123456'),\n" +
            "('20012060','宁晨','123456');";
    static final String INSERT_DEANS = "insert into deans values\n" +
            "('10546552','钊洋','123456'),\n" +
            "('10546553','郝颖','123456');";
    static final String INSERT_CHOICE_QUESTION = "insert into choiceQuestion (CONTENT,CHOICEA,CHOICEB,CHOICEC,CHOICED,ANSWER) values \n" +
            "('在SQL语言中,视图是数据库体系结构中的（    ）。','内模式','模式','外模式','物理模式','C'),\n" +
            "('关系模型中,一个关键字是（    ）。','可由多个任意属性组成','至多由一个属性组成','可由一个或多个其值能惟一标识该关系模式中任何元组的属性组成','以上都不是','C'),\n" +
            "('关系数据库中的关键字是指 (    ) 。','能唯一决定关系的字段','不可改动的专用保留字段','关键的很重要的字段','能唯一标识元组的属性或属性集合','D'),\n" +
            "('在一个关系中如果有这样一个属性存在,它的值能唯一地标识关系中的每一个元组,称这个属性为 (    )','关键字','数据项','主属性','主属性值','A'),\n" +
            "('关系模式分解的结果（    ）。','惟一','不惟一,效果相同','不惟一,效果不同,有正确与否之分','不惟一,效果不同,有应用的不同','D'),\n" +
            "('3NF同时又是（    ）。','1NF','2NF','BCNF','1NF,2NF','D'),\n" +
            "('当B属性函数依赖于A属性时,属性A与B的联系是（    ）。','1对多','多对1','多对多','以上都不是','A'),\n" +
            "('当关系模式R(A,B)已属于3NF,下列说法中正确的是（    ）。','它消除了删除异常','仍存在插入和删除异常','属于BCNF','它消除了插入异常','B'),\n" +
            "('根据关系数据库规范化理论,关系数据库的关系要满足第一范式。下面\"部门\"关系中,因哪个属性而使它不满足第一范式?（    ）。','部门总经理','部门成员','部门名','部门号','B'),\n" +
            "('关系模式规范化的最起码的要求是达到第一范式,即满足（    ）。','每个非码属性都完全依赖于主码','主码属性唯一标识关系中的元组','关系中的元组不可重复','每个属性都是不可分解的数据项','D'),\n" +
            "('关系模式中,满足2NF的范式（    ）。','不可能是1NF','可能是3NF','必定是1NF且必定是3NF','必定不是3NF', 'B'),\n" +
            "('关系数据库规范化的目的是为解决关系数据库中（   ）问题','插入删除异常和数据冗余','提高查询速度','减少数据操作的复杂性','保证数据的安全性和完整性','A'),\n" +
            "('将1NF规范为2NF,应（    ）。','消除非主属性对键的部分函数依赖','消除非主属性对键的传递函数依赖','消除主属性对键的部分函数依赖传递函数依赖','使每一个非主属性都完全依赖于主键','A'),\n" +
            "('任何由两个属性组成的关系（    ）。','可能为1NF ','可能为2NF','可能为3NF','必为3NF','D'),\n" +
            "('若要求分解保持函数依赖,那么模式分解一定能够达到（    ）。','2NF','3NF','BCNF','1NF','B'),\n" +
            "('设有关系W(工号,姓名,工种,定额),将其规范化到第三范式正确的答案是（    ）。','W1(工号,姓名),W2(工种,定额)','W1(工号,工种,定额),W2(工号,姓名)','W1(工号,姓名,工种),W2(工号,定额)','W1(工号,姓名,工种),W2(工种,定额)','D'),\n" +
            "('下述说法正确的是（    ）。','属于BCNF的关系模式不存在存储异常','函数依赖可由属性值决定,不由语义决定','超键就是候选键','码是唯一能决定一个元组的属性或属性组','D'),\n" +
            "('有一教师关系为：课程任务（工号、老师名、职称、课程名、班级名、学时名）,设一位老师可担任多门课,一门课也可由多位老师教,那么：该关系属于（    ）。','非规范关系','1NF关系','2NF关系','3NF关系','A'),\n" +
            "('在关系DB中,任何二元关系模式的最高范式必定是（    ）。','1NF','2NF','3NF','BCNF','A'),\n" +
            "('设有关系模式R（A,B,C,D）及其上的函数依赖集合F={B →A,BC →D},那么关系模式R最高是（    ）的部分依赖。','第一范式','第二范式','第三范式','BCNF范式','A');";
    static final String INSERT_GAP_QUESTION = "insert into gapQuestion (CONTENT,ANSWER) values \n" +
            "('主属性是指_________,在一个关系中,主属性至少_________个,至多可为_________个.','构成关键字的属性或属性集合; 1;字段个数'),\n" +
            "('模式分解的准则是_________和_________.','保持函数依赖性,无损连接性'),\n" +
            "('1NF,2NF,3NF之间的相互关系为________.','达到3NF必达到2NF,达到2NF必达到1NF'),\n" +
            "('操作异常是指________。','插入操作异常：应当录入的数据不能录入；删除操作异常：应当删除的数据不能删除；'),\n" +
            "('从第一范式逐步规范化到第二,第三、BCNF范式的过程,就是逐步消除各种________的过程。','函数依赖'),\n" +
            "('对关系进行规范化的目的是________。','减少冗余,避免操作异常'),\n" +
            "('关系模式由2NF转化为3NF是消除了非主属性对候选键的________。','传递函数依赖'),\n" +
            "('关系模式由3NF转化为BCNF是消除了主属性对候选键的_________和________。','部分函数依赖,传递函数依赖'),\n" +
            "('如果关系R为第2范式,且其中的所有非主属性都不传递依赖于R的任何候选键,则称关系R属于________范式。','3NF'),\n" +
            "('若关系R ∈ 2NF,且它的每一个非主属性都_________则称R ∈3NF。','不传递函数依赖于R的候选键'),\n" +
            "('若关系为1NF,且它的每一非主属性都_________候选关键字,则该关系为2NF。','完全函数依赖于'),\n" +
            "('数据冗余所导致的问题主要有________。','效率低,常导致操作异常'),\n" +
            "('有关系R(A,B,C,D),{B→D,AB→C}则其最高范式是________。','1NF'),\n" +
            "(' 在一个关系R中,若每个数据项都是不可分割的,那么R一定属于________。','1NF'),\n" +
            "('若关系Ｒ中某属性Ａ不是它的关键字,但却是他关系的关键字,则对关系Ｒ而言,称属性Ａ为________。','外关键字或外码'),\n" +
            "('在关系A(S,SN,D)和B(D,CN,NM)中,A的主键是S,B的主键是D,则D在A中称为________。','外键'),\n" +
            "('_________是位于用户和操作系统之间的一层数据管理软件。数据库在建立、使用和维护时由其统一管理、统一控制','DBMS'),\n" +
            "('公司中有多个部门和多名职员,每个职员只能属于一个部门,一个部门可以有多名职员,从职员到部门的联系类型是________。','多对一'),\n" +
            "('假如采用关系数据库系统来实现应用,在数据库设计的_________阶段,需要将E-R模型转换为关系数据模型','逻辑设计'),\n" +
            "('取出关系中的某些列,并消去重复的元组的关系运算称为________。','投影运算 ');";
    static final String INSERT_ESSAY_QUESTION = "insert into essayQuestion (CONTENT,ANSWER) values\n" +
            "('试述数据库系统的概念。','t数据库系统是指在计算机系统中引入数据库后的系统构成,一般由数据库、数据库管理系统（及其开发工具）、应用系统、数据库管理员构成。解析数据库系统和数据库是两个概念。数据库系统是一个人一机系统,数据库是数据库系统的一个组成部分。但是在日常工作中人们常常把数据库系统简称为数据库。希望读者能够从人们讲话或文章的上下文中区分“数据库系统”和“数据库”,不要引起混淆。'),\n" +
            "('试述数据库系统的组成。','数据库系统一般由数据库、数据库管理系统（及其开发工具）、应用系统、数据库管理员和用户构成'),\n" +
            "('试述关系模型的三个组成部分。','关系模型由关系数据结构、关系操作集合和关系完整性约束三部分组成。'),\n" +
            "('所有的视图是否都可以更新？为什么？','自主存取控制方法：定义各个用户对不同数据对象的存取权限。当用户对数据库访问时首先检查用户的存取权限。防止不合法用户对数据库的存取。强制存取控制方法：每一个数据对象被（强制地）标以一定的密级,每一个用户也被（强制地）授予某一个级别的许可证。系统规定只有具有某一许可证级别的用户才能存取某一个密级的数据对象。'),\n" +
            "('试述查询优化的一般准则。','下面的优化策略一般能提高查询效率： ( l ）选择运算应尽可能先做； ( 2 ）把投影运算和选择运算同时进行； ( 3 ）把投影同其前或其后的双目运算结合起来执行； ( 4 ）把某些选择同在它前面要执行的笛卡儿积结合起来成为一个连接运算； ( 5 ）找出公共子表达式； ( 6 ）选取合适的连接算法。'),\n" +
            "('试述查询优化的一般步骤。','各个关系系统的优化方法不尽相同,大致的步骤可以归纳如下： ( l ）把查询转换成某种内部表示,通常用的内部表示是语法树。 ( 2 ）把语法树转换成标准（优化）形式。即利用优化算法,把原始的语法树转换成优化的形式。 ( 3 ）选择低层的存取路径。 ( 4 ）生成查询计划,选择代价最小的。');\n";
    static final String INSERT_PAPER = "insert into papers (CREATETIME) values ('2015-12-12'),\n" +
            "('2015-12-13');";
    static final String INSERT_CHOICE_IN_PAPER = "insert into choiceInpapers values \n" +
            "(1,1,3), \n" +
            "(1,2,3),\n" +
            "(1,3,3),\n" +
            "(1,4,3),\n" +
            "(1,5,3),\n" +
            "(1,6,3),\n" +
            "(1,7,3),\n" +
            "(1,8,3),\n" +
            "(1,9,3),\n" +
            "(1,10,3),\n" +
            "(2,11,3),\n" +
            "(2,12,3),\n" +
            "(2,13,3),\n" +
            "(2,14,3),\n" +
            "(2,15,3),\n" +
            "(2,16,3),\n" +
            "(2,17,3),\n" +
            "(2,18,3),\n" +
            "(2,19,3),\n" +
            "(2,20,3);";
    static final String INSERT_GAP_IN_PAPER = "insert into gapInPapers values\n" +
            "(1,1,4),  \n" +
            "(1,2,4),\n" +
            "(1,3,4),\n" +
            "(1,4,4),\n" +
            "(1,5,4),\n" +
            "(1,6,4),\n" +
            "(1,7,4),\n" +
            "(1,8,4),\n" +
            "(1,9,4),\n" +
            "(1,10,4),\n" +
            "(2,11,4),\n" +
            "(2,12,4),\n" +
            "(2,13,4),\n" +
            "(2,14,4),\n" +
            "(2,15,4),\n" +
            "(2,16,4),\n" +
            "(2,17,4),\n" +
            "(2,18,4),\n" +
            "(2,19,4),\n" +
            "(2,20,4);";
    static final String INSERT_ESSAY_IN_PAPER = "insert into essayInPapers values\n" +
            "(1,1,10),  \n" +
            "(1,2,10),\n" +
            "(1,3,10),\n" +
            "(2,4,10),\n" +
            "(2,5,10),\n" +
            "(2,6,10);";

    public mDataBaseHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (String sql : CREATE) {
            db.execSQL(sql);
        }
        db.execSQL(INSERT_STUDENTS);
        db.execSQL(INSERT_TEACHERS);
        db.execSQL(INSERT_DEANS);
        db.execSQL(INSERT_CHOICE_QUESTION);
        db.execSQL(INSERT_GAP_QUESTION);
        db.execSQL(INSERT_ESSAY_QUESTION);
        db.execSQL(INSERT_PAPER);
        db.execSQL(INSERT_CHOICE_IN_PAPER);
        db.execSQL(INSERT_GAP_IN_PAPER);
        db.execSQL(INSERT_ESSAY_IN_PAPER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
