import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import twitter4j.*;
import twitter4j.auth.AccessToken;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Twitter_Test {

    @Test(invocationCount = 5)
    public void Tweets() {
        long tweetId;
        long replyTweetId;

        String tweet = getTweetMessage();
        String tweetReply = getTweetReplyMessage(tweet);

        String username = "greta_petkova";
        String password = "Gretka";

        //Getting Twitter client
        Twitter twitter = getTwitterClient();

        //Tweeting custom tweet
        log("Tweeting: " + tweet);
        try {
            Status status = twitter.updateStatus(tweet);
            tweetId = status.getId();
            log("TweetId: " + tweetId);
        } catch (TwitterException e) {
            Assert.fail(e.getMessage());
            return;
        }

        //Tweeting custom reply
        log("Tweeting reply: " + tweetReply);
        try {
            StatusUpdate statusUpdate = new StatusUpdate(tweetReply);
            statusUpdate.setInReplyToStatusId(tweetId);
            Status replyStatus = twitter.updateStatus(statusUpdate);
            replyTweetId = replyStatus.getId();
            log("TweetReplyId: " + replyTweetId);
        } catch (TwitterException e) {
            deleteTweet(twitter, tweetId);
            Assert.fail(e.getMessage());
            return;//explain why
        }

        ChromeOptions options = new ChromeOptions();

        options.setHeadless(true);
        options.addArguments("--disable-dev-shm-usage"); // fixing linux limited resource problems

        WebDriver driver = new ChromeDriver(options);

        driver.get("https://twitter.com/");
        driver.manage().window().setSize(new Dimension(1920,1080)); //fake browser size for headless

        WebDriverWait wait = new WebDriverWait(driver, 5);

        WebElement logInButton;
        try {
            logInButton = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@data-testid='LoginForm_Login_Button']")));
        } catch (TimeoutException e) {
            deleteTweet(twitter, tweetId);
            deleteTweet(twitter, replyTweetId);
            driver.quit();

            Assert.fail("Login Button not found after 5 seconds");
            return;
        }

        WebElement inputName;
        try {
            inputName = driver.findElement(By.xpath("//*[@name='session[username_or_email]']"));

        } catch (NoSuchElementException e) {
            deleteTweet(twitter, tweetId);
            deleteTweet(twitter, replyTweetId);
            driver.quit();

            Assert.fail("Input name not found");
            return;
        }

        WebElement inputPassword;
        try {
            inputPassword = driver.findElement(By.xpath("//*[@name='session[password]']"));
        } catch (NoSuchElementException e) {
            deleteTweet(twitter, tweetId);
            deleteTweet(twitter, replyTweetId);
            driver.quit();

            Assert.fail("Input password not found");
            return;
        }

        inputName.sendKeys(username);
        inputPassword.sendKeys(password);

        logInButton.click();

        WebElement showThisTreadLink;
        try {
            showThisTreadLink = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[href='/" + username + "/status/" + replyTweetId + "']")));
        } catch (TimeoutException e) {
            deleteTweet(twitter, tweetId);
            deleteTweet(twitter, replyTweetId);
            driver.quit();

            Assert.fail("Reply Tweet Link not found after 5 seconds");
            return;
        }

        WebElement tweetContainer;
        try {
            tweetContainer = showThisTreadLink.findElement(By.xpath("./.."));
        } catch (NoSuchElementException e) {
            deleteTweet(twitter, tweetId);
            deleteTweet(twitter, replyTweetId);
            driver.quit();

            Assert.fail("Tweet container not found");
            return;
        }

        WebElement menuToggle;
        try {
            menuToggle = tweetContainer.findElement(By.xpath("//*[@data-testid='caret']"));
        } catch (NoSuchElementException e) {
            deleteTweet(twitter, tweetId);
            deleteTweet(twitter, replyTweetId);
            driver.quit();

            Assert.fail("Dropdown Menu toggle not found");
            return;
        }

        menuToggle.click();

        WebElement pinLink;
        try {
            pinLink = driver.findElement(By.xpath("//*[@data-testid='pin']"));
        } catch (NoSuchElementException e) {
            deleteTweet(twitter, tweetId);
            deleteTweet(twitter, replyTweetId);
            driver.quit();

            Assert.fail("PinkLink not found");
            return;
        }

        WebElement menuContainer;
        try {
            menuContainer = pinLink.findElement(By.xpath("./.."));
        } catch (NoSuchElementException e) {
            deleteTweet(twitter, tweetId);
            deleteTweet(twitter, replyTweetId);
            driver.quit();

            Assert.fail("Menu container not found");
            return;
        }

        List<WebElement> menuLinks;
        try {
            menuLinks = menuContainer.findElements(By.tagName("div"));
        } catch (NoSuchElementException e) {
            deleteTweet(twitter, tweetId);
            deleteTweet(twitter, replyTweetId);
            driver.quit();

            Assert.fail("Menu links not found");
            return;
        }

        WebElement deleteLink;
        try {
            deleteLink = menuLinks.get(0);
        } catch (IndexOutOfBoundsException e) {
            deleteTweet(twitter, tweetId);
            deleteTweet(twitter, replyTweetId);
            driver.quit();

            Assert.fail("Delete link not found");
            return;
        }

        try {
            wait.until(ExpectedConditions.elementToBeClickable(deleteLink));
        } catch (TimeoutException e) {
            deleteTweet(twitter, tweetId);
            deleteTweet(twitter, replyTweetId);
            driver.quit();

            Assert.fail("Delete link is not clickable");
            return;
        }

        deleteLink.click();

        WebElement confirmDeleteButton;
        try {
            confirmDeleteButton = driver.findElement(By.xpath("//*[@data-testid='confirmationSheetConfirm']"));
        } catch (NoSuchElementException e) {
            deleteTweet(twitter, tweetId);
            deleteTweet(twitter, replyTweetId);
            driver.quit();

            Assert.fail("Confirm delete button not found");
            return;
        }

        try {
            wait.until(ExpectedConditions.elementToBeClickable(confirmDeleteButton));
        } catch (TimeoutException e) {
            deleteTweet(twitter, tweetId);
            deleteTweet(twitter, replyTweetId);
            driver.quit();

            Assert.fail("Confirm delete button is not clickable");
            return;
        }

        confirmDeleteButton.click();

        driver.get("https://twitter.com/" + username + "/status/" + replyTweetId);

        try {
            twitter.showStatus(replyTweetId);
            deleteTweet(twitter, tweetId);
            deleteTweet(twitter, replyTweetId);

            Assert.fail("Reply tweet is not deleted");
        } catch (TwitterException e) {
            deleteTweet(twitter, tweetId);
            //If there is an exception it means that the tweet is deleted
        }

        driver.quit();
    }

    private String getTweetMessage() {
        return "This is a test tweet at: " + getNowAsString();
    }

    private String getTweetReplyMessage(String tweet) {
        return "Reply to '" + tweet + "' at: " + getNowAsString();
    }

    private String getNowAsString() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        return dtf.format(now);
    }

    private Twitter getTwitterClient() {
        Twitter twitter = new TwitterFactory().getInstance();

        twitter.setOAuthConsumer(System.getenv("CONSUMERKEYSTR"), System.getenv("CONSUMERSECRESTR"));

        twitter.setOAuthAccessToken(
                new AccessToken(System.getenv("ACCESSTOKENSTR"), System.getenv("ACCESSTOKENSECRETESTR"))
        );

        return twitter;
    }

    private void log(String message) {
        System.out.println("[" + getNowAsString() + "]" + message);
    }

    private void deleteTweet(Twitter twitter, long tweetId) {
        log("Deleting tweet with Id: " + tweetId);
        try {
            twitter.destroyStatus(tweetId);
        } catch (TwitterException e) {
            log("Unable to delete tweet with Id: " + tweetId);
        }
    }
}