package com.featurespace.todolist;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.List;

@Test
public class TestCreate {

    private WebDriver driver;
    private WebElement todoInput;

    @BeforeClass
    public void setChromeDriver() {
//        System.setProperty("webdriver.chrome.driver", "C:\\Users\\cristian\\Downloads\\chromedriver_win32\\chromedriver.exe");
        System.setProperty("webdriver.chrome.driver", "src\\test\\resources\\chromedriver.exe");
    }

    @BeforeMethod
    public void openDriver() {
        driver = new ChromeDriver();
        driver.get("http://todomvc.com/examples/angularjs/#/");

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("header")));

        todoInput = driver.findElement(By.id("new-todo"));
    }

    @AfterMethod
    public void closeDriver() {
        driver.close();
    }

    @Test
    public void testWebPageURL(){
        String pageURL = driver.getCurrentUrl();
        Assert.assertEquals(pageURL, "http://todomvc.com/examples/angularjs/#/");
    }

    @Test
    public void testAddItems() {
        String inputText1 = "random";
        String inputText2 = "random2";

        addNewTodo(inputText1);
        addNewTodo(inputText2);

        List<WebElement> addedTodoList = driver.findElements(By.xpath("//ul[@id='todo-list']/li"));
        Assert.assertNotNull(addedTodoList);
        Assert.assertEquals(addedTodoList.size(), 2);
        Assert.assertEquals(addedTodoList.get(0).getText(), inputText1);
        Assert.assertEquals(addedTodoList.get(1).getText(), inputText2);
    }

    @Test
    public void testDeleteExistingItems() {
        String inputText = "random";

        addNewTodo(inputText);

        List<WebElement> addedTodoList = driver.findElements(By.xpath("//ul[@id='todo-list']/li"));
        Assert.assertNotNull(addedTodoList);
        Assert.assertEquals(addedTodoList.size(), 1);

        WebElement firstListItem = addedTodoList.get(0);
        firstListItem.click();
        WebElement xBtn = addedTodoList.get(0).findElement(By.className("destroy"));
        Assert.assertNotNull(xBtn);

        xBtn.click();
        addedTodoList = driver.findElements(By.xpath("//ul[@id='todo-list']/li"));
        Assert.assertNotNull(addedTodoList);
        Assert.assertEquals(addedTodoList.size(), 0);
    }

    @Test
    public void testEditExistingItem() {
        String inputText = "random";
        String editInputText = "Edit";

        addNewTodo(inputText);

        List<WebElement> addedTodoList = driver.findElements(By.xpath("//ul[@id='todo-list']/li"));
        Assert.assertNotNull(addedTodoList);
        Assert.assertEquals(addedTodoList.size(), 1);

        WebElement firstListItem = addedTodoList.get(0);
        Actions action = new Actions(driver);
        action.doubleClick(firstListItem).perform();
        action.sendKeys(firstListItem, editInputText).perform();
        action.sendKeys(firstListItem, Keys.ENTER).perform();

        addedTodoList = driver.findElements(By.xpath("//ul[@id='todo-list']/li"));
        Assert.assertNotNull(addedTodoList);
        Assert.assertEquals(addedTodoList.size(), 1);

        Assert.assertEquals(addedTodoList.get(0).getText(), inputText + editInputText);
    }

    @Test
    public void testMarkAsCompleted() {
        String inputText = "random";

        addNewTodo(inputText);

        List<WebElement> addedTodoList = driver.findElements(By.xpath("//ul[@id='todo-list']/li"));
        Assert.assertNotNull(addedTodoList);
        Assert.assertEquals(addedTodoList.size(), 1);

        WebElement firstListItem = addedTodoList.get(0);
        WebElement checkBox = firstListItem.findElement(By.xpath("//input[@type='checkbox']"));
        Assert.assertNotNull(checkBox);

        Actions action = new Actions(driver);
        action.click(checkBox).perform();

        addedTodoList = driver.findElements(By.xpath("//ul[@id='todo-list']/li[@class='ng-scope completed']"));
        Assert.assertNotNull(addedTodoList);
        Assert.assertEquals(addedTodoList.size(), 1);
    }

    @Test
    public void testFilterItems() throws InterruptedException {
        String inputText1 = "random";
        String inputText2 = "random2";

        addNewTodo(inputText1);
        addNewTodo(inputText2);

        List<WebElement> addedTodoList = driver.findElements(By.xpath("//ul[@id='todo-list']/li"));
        Assert.assertNotNull(addedTodoList);
        Assert.assertEquals(addedTodoList.size(), 2);

        WebElement firstListItem = addedTodoList.get(0);
        WebElement checkBox = firstListItem.findElement(By.xpath("//input[@type='checkbox'][@ng-model='todo.completed']"));
        Assert.assertNotNull(checkBox);
        Actions action = new Actions(driver);
        action.click(checkBox).perform();

        checkFilter("All", 2, action);
        checkFilter("Active", 1, action);
        checkFilter("Completed", 1, action);
    }

    private void checkFilter(String filterText, int expectedFilteredElements, Actions action) throws InterruptedException {
        WebElement footer = driver.findElement(By.xpath("//footer[@id='footer']"));
        Assert.assertNotNull(footer);

        List<WebElement> footerFiltersList = footer.findElements(By.xpath("//ul[@id='filters']/li"));
        Assert.assertNotNull(footerFiltersList);

        for (WebElement item : footerFiltersList) {
            if (item.getText().equals(filterText)) {
                action.click(item).perform();

                List<WebElement> addedTodoList = driver.findElements(By.xpath("//ul[@id='todo-list']/li"));
                Assert.assertNotNull(addedTodoList);
                Assert.assertEquals(addedTodoList.size(), expectedFilteredElements);

                break;
            }
        }
    }

    private void addNewTodo(String name) {
        todoInput.sendKeys(name);
        todoInput.sendKeys(Keys.ENTER);
    }

}
