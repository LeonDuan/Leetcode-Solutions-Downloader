package com.utexas;
import org.apache.commons.lang3.StringEscapeUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class Main {
    public static WebDriver wd;
    public static WebElement we;
    public static void main(String[] args) throws Exception {
        System.setProperty("webdriver.gecko.driver", "C:/Users/leond/Desktop/geckodriver.exe");
        wd = new FirefoxDriver();
        wd.get("https://leetcode.com/submissions/#/1");

        //enter credentials
        we = wd.findElement(By.id("id_login"));
        we.sendKeys("leonduantian@hotmail.com");
        we = wd.findElement(By.id("id_password"));
        we.sendKeys("d10111213d");
        wd.findElement(By.xpath("//button[@class='btn btn-primary']")).click();

        Set<String> processedQuestions = new HashSet();

        while(true) {
            for (int counter = 0; counter < wd.findElements(By.className("text-success")).size(); counter++) {
                List<WebElement> successes = wd.findElements(By.className("text-success"));
                if (successes.size() == 0) {
                    wd.quit();
                    return;
                } else {
                    we = successes.get(counter);
                }
                we.click();

                //get questionID
                String html = wd.getPageSource();
                int firstSingleQuoteIdx = html.indexOf("'", html.indexOf("questionId"));
                int secondSingleQuoteIdx = html.indexOf("'", firstSingleQuoteIdx + 1);
                String questionID = html.substring(firstSingleQuoteIdx, secondSingleQuoteIdx + 1).replace("'", "");

                //get postfix
                String postFix = getPostFix(wd.findElement(By.id("result_language")).getAttribute("innerHTML"));
                //get problem name
                String problemName = wd.findElements(By.cssSelector("title")).get(0).getAttribute("innerHTML").split(" - ")[0].replace(" ", "").toLowerCase();
                wd.findElement(By.id("edit-code-btn")).click();
                if (processedQuestions.contains(problemName)) {
                    wd.navigate().back();
                    wd.navigate().back();
                    continue;
                }

                //get code
                String code = StringEscapeUtils.unescapeHtml3(wd.findElements(By.cssSelector("textarea")).get(2).getAttribute("innerHTML"));

                //write to a new file
                BufferedWriter writer = new BufferedWriter(new FileWriter("./" + questionID + "." + problemName + postFix));
                writer.write(code);
                writer.close();

                //record this problem
                processedQuestions.add(problemName);

                //go back
                wd.navigate().back();
                wd.navigate().back();
            }

            //go to next page
            String[] arr = wd.getCurrentUrl().split("/");
            arr[arr.length - 1] = Integer.toString(Integer.parseInt(arr[arr.length - 1]) + 1);
            String nextURL = "";
            for (String s : arr) {
                if (!nextURL.equals("")) {
                    nextURL = nextURL + "/";
                }
                nextURL = nextURL + s;
            }
            wd.navigate().to(nextURL);
            Thread.sleep(1000);

        }


    }

    private static String getPostFix(String solutionType) throws Exception{
        if(solutionType.toLowerCase().contains("python")){
            return(".py");
        }
        else if(solutionType.toLowerCase().contains("java")){
            return(".java");
        }
        else if(solutionType.toLowerCase().equals("c")){
            return(".c");
        }
        else{
            return(".s" + solutionType);
        }
    }
}
