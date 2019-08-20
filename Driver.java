package com.project1.driver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import com.utility.ExcelReader;

public class Driver {

	public static String strCountry, strLanguage, strEnvironment,
			strTestSuites, strBrowsers, strParallel;
	public static String strDerivedCountry;
	public static String strDerivedLanguage;
	public static String strDerivedCountryAbrevition;
	public static String strURL;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		try{
			String pattern = "MM_dd_yyyy_hh_mm";
			SimpleDateFormat format = new SimpleDateFormat(pattern);
			System.setOut(new PrintStream(new FileOutputStream("Logs/log-" +format.format(new Date())+".txt")));
		}catch(Exception e){
			System.out.println("Problem in diverting logs to log.txt");
			e.printStackTrace();
		}
		
		System.out
				.println("---------------Preparing For Execution-------------");
		strCountry = System.getProperty("env.COUNTRY");
		strLanguage = System.getProperty("env.LANGUAGE");
		strEnvironment = (System.getProperty("env.ENVIRONMENT") != null) ? System
				.getProperty("env.ENVIRONMENT") : "staging";
		strTestSuites = System.getProperty("env.TESTSUITES");
		strBrowsers = (System.getProperty("env.BROWSERS") != null) ? System
				.getProperty("env.BROWSERS") : "Win_Chrome,Win_Firefox";

		strParallel = System.getProperty("env.PARALLEL");

		System.out.println(strCountry + "--" + strLanguage + "--"
				+ strEnvironment + "--" + strTestSuites + "--" + strBrowsers);

		// Deriving country and language from the availability
		if (strCountry != null) {
			strDerivedCountry = getCountryName(strCountry);
		} else {
			System.out
					.println("Country found as blank in argument. So We will use USA as default country ");
			strDerivedCountry = "USA";
		}

		if (strLanguage != null) {
			strDerivedLanguage = getLanguage(strDerivedCountry, strLanguage)
					.toLowerCase();
		} else {
			System.out
					.println("Language found as blank in argument. So we will use english as default language");
			strDerivedLanguage = "en";
		}
		setCountryAbreviation();

		try {
			List<String> selectedModules;
			if (strTestSuites != null) {
				selectedModules = Arrays.asList(strTestSuites.split(","));
			} else {
				ExcelReader excelConroller = new ExcelReader("Controller.xls");
				selectedModules = excelConroller.getSelectedModuleNames();
			}

			TestNG testng = new TestNG();
			List<XmlSuite> suites = new ArrayList<>();
			String[] allBrowser = strBrowsers.split(",");

			// XmlGroups allGroups = new XmlGroups();

			for (String strBrowser : allBrowser) {

				XmlSuite browserSuite = new XmlSuite();
				browserSuite.setName("My Test Suite - " + strBrowser);
				HashMap<String, String> suiteParameters = new HashMap<>();
				suiteParameters.put("browser", strBrowser);
				browserSuite.setParameters(suiteParameters);
				
				XmlTest test = new XmlTest(browserSuite);
				test.setName(strBrowser + "_Test ");
				
				
				List<XmlClass> classes = new ArrayList<>();
				
				for (String ts : selectedModules) {
					System.out.println("Adding, " + ts + ", module for "
							+ strBrowser);
					

					
					XmlClass aClass = new XmlClass();
					aClass.setName("com.project1.testsuite." + ts);
					classes.add(aClass);

					
				}
				
				test.setClasses(classes);
				
				suites.add(browserSuite);
				// System.out.println("XML for " + strBrowser + "\n");
				 System.out.println(browserSuite.toXml());
			}

			testng.setXmlSuites(suites);
			testng.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
}
