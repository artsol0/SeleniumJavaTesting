package com.artsolo.selenium;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TheInternetHerokuappTest {

    private final static WebDriver driver = new ChromeDriver();
    private final static String address = "https://the-internet.herokuapp.com/";

    @BeforeEach
    void setUp() {
        driver.get(address);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDown() {
        driver.quit();
    }

    @Test
    void testHomePageTitle() {
        String expectedPageTitle = "The Internet";
        String actualPageTitle = driver.getTitle();
        assertEquals(expectedPageTitle, actualPageTitle);
    }

    @Test
    void testHomePageLinks() {
        List<WebElement> links = driver.findElements(By.tagName("a"));
        for (WebElement link : links) {
            assertNotNull(link.getAttribute("href"));
        }
    }

    @Test
    void testAddRemoveElements() {
        String addButtonXpath = "/html/body/div[2]/div/div/button";
        String deleteButtonClass = "added-manually";

        driver.navigate().to("https://the-internet.herokuapp.com/add_remove_elements/");

        WebElement addButton = driver.findElement(By.xpath(addButtonXpath));
        assertTrue(addButton.isDisplayed());
        assertTrue(addButton.isEnabled());
        addButton.click();

        WebElement deleteButton = driver.findElement(By.className(deleteButtonClass));
        assertTrue(deleteButton.isDisplayed());
        assertTrue(deleteButton.isEnabled());
        deleteButton.click();

        assertThrows(NoSuchElementException.class, () -> driver.findElement(By.className(deleteButtonClass)));
    }

    @Test
    void testBasicAuth() {
        String username = "admin";
        String password = "admin";
        driver.navigate().to("https://" + username + ":" + password + "@the-internet.herokuapp.com/basic_auth");
        WebElement webElement = driver.findElement(By.xpath("/html/body/div[2]/div/div/p"));
        assertEquals("Congratulations! You must have the proper credentials.", webElement.getText());
    }

    @Test
    void testBrokenImages() {
        driver.navigate().to("https://the-internet.herokuapp.com/broken_images");
        for (WebElement image : driver.findElements(By.cssSelector("img"))) {
            assertNotEquals("0", image.getAttribute("naturalWidth"));
        }
    }

    @Test
    void testCheckBoxes() {
        driver.navigate().to("https://the-internet.herokuapp.com/checkboxes");
        List<WebElement> checkBoxes = driver.findElements(By.xpath("//input[@type='checkbox']"));

        assertFalse(checkBoxes.get(0).isSelected());
        assertTrue(checkBoxes.get(1).isSelected());

        for (WebElement checkBox : checkBoxes) {
            checkBox.click();
        }

        assertTrue(checkBoxes.get(0).isSelected());
        assertFalse(checkBoxes.get(1).isSelected());
    }

    @Test
    void testContextMenu() {
        driver.navigate().to("https://the-internet.herokuapp.com/context_menu");
        WebElement contextMenu = driver.findElement(By.xpath("//*[@id=\"hot-spot\"]"));
        Actions actions = new Actions(driver);
        actions.contextClick(contextMenu).perform();
        driver.switchTo().alert().accept();
    }

    @Test
    void testDragAndDrop() {
        driver.navigate().to("https://the-internet.herokuapp.com/drag_and_drop");
        WebElement boxA = driver.findElement(By.xpath("//*[@id=\"column-a\"]"));
        WebElement boxB = driver.findElement(By.xpath("//*[@id=\"column-b\"]"));
        Actions actions = new Actions(driver);
        actions.dragAndDrop(boxB, boxA).perform();
    }

    @Test
    void testDropDown() {
        driver.navigate().to("https://the-internet.herokuapp.com/dropdown");
        WebElement dropDown = driver.findElement(By.id("dropdown"));
        Select select = new Select(dropDown);

        select.selectByIndex(1);
        assertEquals("Option 1", select.getFirstSelectedOption().getText());

        select.selectByVisibleText("Option 2");
        assertEquals("Option 2", select.getFirstSelectedOption().getText());
    }

    @Test
    void testDynamicControlsCheckBox() {
        driver.navigate().to("https://the-internet.herokuapp.com/dynamic_controls");

        WebElement checkBox = driver.findElement(By.id("checkbox"));
        assertTrue(checkBox.isEnabled());

        WebElement switchButton = driver.findElement(By.xpath("/html/body/div[2]/div/div[1]/form[1]/button"));
        switchButton.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(switchButton));

        WebElement message = driver.findElement(By.id("message"));
        assertEquals("It's gone!", message.getText());

        switchButton.click();
        wait.until(ExpectedConditions.elementToBeClickable(switchButton));

        checkBox = driver.findElement(By.xpath("//*[@id=\"checkbox\"]"));
        assertTrue(checkBox.isEnabled());
    }

    @Test
    void testDynamicControlsField() {
        driver.navigate().to("https://the-internet.herokuapp.com/dynamic_controls");
        WebElement field = driver.findElement(By.xpath("/html/body/div[2]/div/div[1]/form[2]/input"));
        assertFalse(field.isEnabled());

        WebElement switchButton = driver.findElement(By.xpath("/html/body/div[2]/div/div[1]/form[2]/button"));
        switchButton.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(switchButton));

        WebElement message = driver.findElement(By.id("message"));
        assertEquals("It's enabled!", message.getText());

        assertTrue(field.isEnabled());
        field.sendKeys("Test text");

        switchButton.click();
        wait.until(ExpectedConditions.elementToBeClickable(switchButton));

        assertFalse(field.isEnabled());
    }

    @Test
    void testSuccessfulLoginPage() {
        String username = "tomsmith";
        String password = "SuperSecretPassword!";

        driver.navigate().to("https://the-internet.herokuapp.com/login");

        WebElement usernameField = driver.findElement(By.id("username"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.xpath("/html/body/div[2]/div/div/form/button"));

        usernameField.sendKeys(username);
        passwordField.sendKeys(password);
        loginButton.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("flash")));

        assertTrue(alert.getText().contains("You logged into a secure area!"));

        driver.findElement(By.partialLinkText("Logout")).click();

        alert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("flash")));
        assertTrue(alert.getText().contains("You logged out of the secure area!"));
    }

    @Test
    void testFailLoginPage() {
        String username = "user";
        String password = "password";

        driver.navigate().to("https://the-internet.herokuapp.com/login");

        WebElement usernameField = driver.findElement(By.id("username"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.xpath("/html/body/div[2]/div/div/form/button"));

        usernameField.sendKeys(username);
        passwordField.sendKeys(password);
        loginButton.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("flash")));

        assertTrue(alert.getText().contains("Your username is invalid!"));
    }

    @Test
    void testHorizontalSlider() {
        driver.navigate().to("https://the-internet.herokuapp.com/horizontal_slider");
        WebElement slider = driver.findElement(By.xpath("/html/body/div[2]/div/div/div/input"));
        double maxValue = Double.parseDouble(slider.getAttribute("max"));

        Actions actions = new Actions(driver);
        actions.dragAndDropBy(slider, (int) maxValue, 0).perform();
    }

    @Test
    void testHovers() {
        driver.navigate().to("https://the-internet.herokuapp.com/hovers");
        WebElement image = driver.findElement(By.xpath("/html/body/div[2]/div/div/div[2]/img"));
        WebElement info = driver.findElement(By.xpath("/html/body/div[2]/div/div/div[2]/div"));

        assertFalse(info.isDisplayed());

        Actions actions = new Actions(driver);
        actions.moveToElement(image).perform();

        assertTrue(info.isDisplayed());
    }

    @Test
    void testInfiniteScroll() {
        driver.navigate().to("https://the-internet.herokuapp.com/infinite_scroll");
        Actions actions = new Actions(driver);

        for (int i = 0; i < 15; i++) {
            actions.scrollByAmount(0, 50).perform();
        }
    }

    @Test
    void testInputs() {
        driver.navigate().to("https://the-internet.herokuapp.com/inputs");
        WebElement input = driver.findElement(By.xpath("/html/body/div[2]/div/div/div/div/input"));

        for (int i = 0; i < 10; i++) {
            input.sendKeys(Keys.ARROW_UP);
        }

        assertEquals("10", input.getAttribute("value"));
    }

    @Test
    void testKeysPresses() {
        driver.navigate().to("https://the-internet.herokuapp.com/key_presses");
        WebElement result = driver.findElement(By.id("result"));
        WebElement target = driver.findElement(By.id("target"));

        target.sendKeys(Keys.SHIFT);

        assertEquals("You entered: SHIFT", result.getText());
    }

    @Test
    void testWebTable() {
        driver.navigate().to("https://the-internet.herokuapp.com/tables");

        int rows = driver.findElements(By.xpath("/html/body/div[2]/div/div/table[1]/tbody/tr")).size();
        List<String> tableEmails = new ArrayList<>(rows);

        for (int r = 1; r <= rows; r++) {
            tableEmails.add(
                    driver.findElement(
                            By.xpath("/html/body/div[2]/div/div/table[1]/tbody/tr["+ r +"]/td["+ 3 +"]")
                    ).getText()
            );
        }

        assertEquals(4, tableEmails.size());
        assertEquals("fbach@yahoo.com", tableEmails.get(1));
    }

    @Test
    void testTypos() {
        driver.navigate().to("https://the-internet.herokuapp.com/typos");
        String expectedText = "Sometimes you'll see a typo, other times you won't.";
        String actualText = driver.findElement(By.xpath("/html/body/div[2]/div/div/p[2]")).getText();
        assertEquals(expectedText, actualText);
    }

    @Test
    void testStatusCodes() {
        driver.navigate().to("https://the-internet.herokuapp.com/status_codes");
        WebElement successfulLink = driver.findElement(By.partialLinkText("200"));

        try {
            URL url = new URL(successfulLink.getAttribute("href"));
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            assertTrue(urlConnection.getResponseCode() <= 400);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testJsAlerts() {
        driver.navigate().to("https://the-internet.herokuapp.com/javascript_alerts");
        WebElement result = driver.findElement(By.id("result"));

        driver.findElement(By.xpath("//button[@onclick=\"jsAlert()\"]")).click();
        driver.switchTo().alert().accept();
        assertEquals("You successfully clicked an alert", result.getText());

        driver.findElement(By.xpath("//button[@onclick=\"jsConfirm()\"]")).click();
        driver.switchTo().alert().dismiss();
        assertEquals("You clicked: Cancel", result.getText());

        driver.findElement(By.xpath("//button[@onclick=\"jsPrompt()\"]")).click();
        Alert alertWindow = driver.switchTo().alert();
        alertWindow.sendKeys("Test text");
        alertWindow.accept();
        assertEquals("You entered: Test text", result.getText());
    }

    @Test
    void testFileUploading() {
        driver.navigate().to("https://the-internet.herokuapp.com/upload");
        WebElement fileInput = driver.findElement(By.cssSelector("input[type=file]"));
        fileInput.sendKeys("/home/user/TaskResults/result.txt");
        driver.findElement(By.id("file-submit")).click();

        String result = driver.findElement(By.xpath("/html/body/div[2]/div/div/h3")).getText();
        String uploadedFiles = driver.findElement(By.id("uploaded-files")).getText();

        assertEquals("File Uploaded!", result);
        assertEquals("result.txt", uploadedFiles);
    }

    @Test
    void testFileDownloading() {
        driver.navigate().to("https://the-internet.herokuapp.com/download");
        String fileName = "test.txt";
        driver.findElement(By.partialLinkText(fileName)).click();
        try {
            Thread.sleep(2000);
            File file = new File("/home/user/Downloads/" + fileName);
            assertTrue(file.exists());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testMultipleWindows() {
        driver.navigate().to("https://the-internet.herokuapp.com/windows");
        driver.findElement(By.xpath("/html/body/div[2]/div/div/a")).click();

        List<String> windowsIDs = driver.getWindowHandles().stream().toList();

        driver.switchTo().window(windowsIDs.get(1));
        assertEquals("New Window", driver.getTitle());
        driver.close();

        driver.switchTo().window(windowsIDs.get(0));
        assertEquals("The Internet", driver.getTitle());
    }

    @Test
    void testScreenshot() {
        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        Path destination = Path.of("/home/user/Pictures/Screenshots/testscreen.png");
        try {
            Files.copy(src.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
            assertTrue(Files.exists(destination));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
