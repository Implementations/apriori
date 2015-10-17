package edu.rochester.kanishk;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Constants {
	
	public static final int AGE_INDEX = 0;
	public static final int WORK_INDEX = 1;
	public static final int EDU_INDEX = 2;
	public static final int MARITAL_INDEX = 3;
	public static final int JOB_INDEX = 4;
	public static final int REL_INDEX = 5;
	public static final int RACE_INDEX = 6;
	public static final int SEX_INDEX = 7;
	public static final int GAIN_INDEX = 8;
	public static final int LOSS_INDEX = 9;
	public static final int HOURS_INDEX = 10;
	public static final int COUNTRY_INDEX = 11;
	
	public static final Charset ENCODING = StandardCharsets.UTF_8;
	
	public static final String GARBAGE = "?";
	
	public static final String YOUTH = "youth";
	
	public static final String MIDDLE_AGE = "middle_age";
	
	public static final String SENIOR = "senior";
	
	public static final String SUPER_SENIOR = "super_senior";
	
	public static final String PART_TIME = "part_time";
	
	public static final String FULL_TIME = "full_time";
	
	public static final String OVERTIME = "overtime";
	
	public static final String BURNOUT = "burnout";
	
	public static final String CAPITAL_NONE = "none";
	
	public static final String LOW = "low";
	
	public static final String HIGH = "high";
	
	public static final String GAIN = "gain_";
	
	public static final String LOSS = "loss_";
	
	public static String[] AGE = {"Private", "Self-emp-not-inc", "Self-emp-inc", "Federal-gov",
			"Local-gov", "State-gov", "Without-pay", "Never-worked"};
	
	public static String [] WORK_CLASS = {"Private", "Self-emp-not-inc", "Self-emp-inc", 
			"Federal-gov", "Local-gov", "State-gov", "Without-pay", "Never-worked"};
	
	public static String [] EDUCATION = {"Bachelors", "Some-college", "11th", "HS-grad", "Prof-school", "Assoc-acdm", "Assoc-voc", "9th", "7th-8th", "12th", "Masters", 
			"1st-4th", "10th", "Doctorate", "5th-6th", "Preschool"};
	
	public static String [] MARITAL = {"Married-civ-spouse", "Divorced", "Never-married", "Separated", "Widowed", 
			"Married-spouse-absent", "Married-AF-spouse"};
	
	public static String [] OCCUPATION = {"Tech-support", "Craft-repair", "Other-service", "Sales", "Exec-managerial", "Prof-specialty", "Handlers-cleaners", "Machine-op-inspct", "Adm-clerical", "Farming-fishing", 
			"Transport-moving", "Priv-house-serv", "Protective-serv", "Armed-Forces"};
	
	public static String [] RELATIONSHIP = {"Wife", "Own-child", "Husband", "Not-in-family", "Other-relative", "Unmarried"};
	
	public static String [] RACE = {"White, Asian-Pac-Islander, Amer-Indian-Eskimo, Other, Black"};
	
	public static String [] SEX = {"MALE", "FEMALE"};
	
	public static String [] COUNTRY = {"United-States", "Cambodia", "England", "Puerto-Rico", "Canada", "Germany",
			"Outlying-US(Guam-USVI-etc)", "India", "Japan", "Greece", "South", "China", "Cuba", "Iran", 
			"Honduras", "Philippines", "Italy", "Poland", "Jamaica", "Vietnam", "Mexico", "Portugal", "Ireland",
			"France", "Dominican-Republic", "Laos", "Ecuador", "Taiwan", "Haiti", "Columbia", "Hungary", "Guatemala",
			"Nicaragua", "Scotland", "Thailand", "Yugoslavia", "El-Salvador", "Trinadad&Tobago",
			"Peru", "Hong", "Holand-Netherlands"};

}
