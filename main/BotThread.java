package main;

/**
 * Created by faahmed on 9/9/16.
 */
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import util.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

// TODO: 10/6/2016 Move utility functions to utility class/package.
/**
 * The PhantomJS thread that fetches shop pages and injects JavaScript to
 * target items specified by the Order used to instantiate the thread.
 * This is where all the 'dirty work' is done.
 */
public class BotThread {
    private static final String               jquery       = Utils.readFile("jquery-2.2.3.min.js", Charset.defaultCharset());
    private        final Long                 startTime    = System.nanoTime()                                        ;
    private        final WebDriver            driver       = Utils.preparePhantomJSDriver()                           ;
    private        final JavascriptExecutor   scripts      = (JavascriptExecutor) driver                              ;
    private        final WebDriverWait        wait         = new WebDriverWait(driver, 5, (long) .1)                  ;
    private              int                  tries        = 1                                                        ;
    private              Order                order                                                                   ;


    public BotThread(Order order, int tries) {
        this.order = order;
        this.tries = tries;
    }

    public int run() throws InterruptedException, IOException {

        if (!getItemPage(3)) {
            driver.quit();
            return 1;
        }
         
        scripts.executeScript(addToCart(order.size));

        if (didFailWhileWaitingForPresenceOfWebElement(".in-cart", "Timed out while adding the item to cart")) {
            driver.quit();
            return 1;
        }
        
        driver.get("https://www.supremenewyork.com/checkout");

        if (didFailWhileWaitingForPresenceOfWebElement("#order_billing_name", "Timed out while loading checkout page")) {
            driver.quit();
            return 1;
        }
        
        scripts.executeScript(setProfile(order));

        System.out.println((float) ((startTime - System.nanoTime())) / 1000000000);

        Thread.sleep(5000);

        File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        Utils.copyFileUsingFileStreams(scrFile, new File("ordercomplete.png"));

        driver.quit();
        order.setAdditionalInfo("The bot functioned properly");
        order.setStatus(Order.Status.SUCCESS);
        return 0;
    }

    private boolean didFailWhileWaitingForPresenceOfWebElement(String selector, String errMsgIfFail) {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(selector)));
        } catch (TimeoutException e) {
            order.setStatus(Order.Status.FAILURE);
            order.setAdditionalInfo(errMsgIfFail);
            return true;
        }
        return false;
    }

    private boolean getItemPage(int tries) {
        int attempts = tries;
        findItem();
        while (didFailWhileWaitingForPresenceOfWebElement("#cctrl", "Timed out while trying to load item page")) {
            attempts--;
            if (attempts == 0){
                System.out.println("Could not find item after given tries");
                return false;
            }
            findItem();
        }
        return true;
    }

    private void findItem(){
        driver.get(getCategoryPage(order));
        scripts.executeScript(jquery);
        scripts.executeScript(setQueryItem(order.keyword, order.color));
    }

    private String getCategoryPage(Order order) {
        switch (order.category) {
            case JACKETS:      return "https://www.supremenewyork.com/shop/all/jackets";
            case SHIRTS:       return "https://www.supremenewyork.com/shop/all/shirts";
            case TOPS_SWEATERS:return "https://www.supremenewyork.com/shop/all/tops_sweaters";
            case SWEATSHIRTS:  return "https://www.supremenewyork.com/shop/all/sweatshirts";
            case PANTS:        return "https://www.supremenewyork.com/shop/all/pants";
            case T_SHIRTS:     return "https://www.supremenewyork.com/shop/all/t-shirts";
            case HATS:         return "https://www.supremenewyork.com/shop/all/hats";
            case BAGS:         return "https://www.supremenewyork.com/shop/all/bags";
            case ACCESSORIES:  return "https://www.supremenewyork.com/shop/all/accessories";
            case SHOES:        return "https://www.supremenewyork.com/shop/all/shoes";
            case SKATE:        return "https://www.supremenewyork.com/shop/all/skate";
            default:           return "";
        }
    }

    public String setQueryItem(String keyword, String color) {
        // TODO: 9/25/16 change script to make keyword not case sensitive?
        StringBuilder sb = new StringBuilder();
        sb.append("$(document).ready(function()\n" +
                "{\n" +
                "    $(\"a:contains('");
        sb.append(keyword);
        sb.append("')\").each(function() //enter item keyword\n" +
                "    {\n" +
                "        $(this).parent().next().children(\"a:contains('");
        sb.append(color);
        sb.append("')\").each(function() //enter color\n" +
                "        {\n" +
                "            this.click();\n" +
                "        });\n" +
                "    });\n" +
                "});");
        return sb.toString();
    }

    public String addToCart(String size) {
        /* TODO: 9/25/16 Change this function to allow lowercase m in medium and to get Large only not XLarge if that
           is passed in.
         */
        StringBuilder sb = new StringBuilder();
        sb.append("$(document).ready(function()\n" +
                "{\n" +
                "    $(\"option:contains('");
        sb.append(size);
        sb.append("')\").each(function()\n" +
                "    {\n" +
                "        var sizeNum = this.getAttribute('value');\n" +
                "        $(\"#size\").val(sizeNum);\n" +
                "        $(\"input.button\").each(function()\n" +
                "        {\n" +
                "            this.click();\n" +
                "        });\n" +
                "    });\n" +
                "});");
        return sb.toString();
    }

    public String setProfile(Order order){
        // TODO: 9/25/16 add the card type and second address field and country
        StringBuilder sb = new StringBuilder();
        sb.append("document.getElementById('order_billing_name').value = '");
        sb.append(order.paymentInfo.name);
        sb.append("';\n" +
                "document.getElementById('order_email').value = '");
        sb.append(order.paymentInfo.email);
        sb.append("';\n" +
                "document.getElementById('order_tel').value = '");
        sb.append(order.paymentInfo.tel);
        sb.append("';\n" +
                "document.getElementById('bo').value = '");
        sb.append(order.paymentInfo.address);
        sb.append("';\n" +
                "document.getElementById('order_billing_zip').value = '");
        sb.append(order.paymentInfo.zip);
        sb.append("';\n" +
                "document.getElementById('order_billing_city').value = '");
        sb.append(order.paymentInfo.city);
        sb.append("';\n" +
                "document.getElementById('order_billing_state').value = '");
        sb.append(order.paymentInfo.state);
        sb.append("';\n" +
                "document.getElementById('cnb').value = '");
        sb.append(order.paymentInfo.number);
        sb.append("';\n" +
                "document.getElementById('credit_card_month').value = '");
        sb.append(order.paymentInfo.expMonth);
        sb.append("';\n" +
                "document.getElementById('credit_card_year').value = '");
        sb.append(order.paymentInfo.expYear);
        sb.append("';\n" +
                "document.getElementById('vval').value = '");
        sb.append(order.paymentInfo.cvv);
        sb.append("';");
        sb.append("\tdocument.getElementsByClassName(\"icheckbox_minimal\")[1].click();\n" +
                "\tdocument.getElementsByClassName(\"checkout\")[0].click();");
        return sb.toString();
    }
}
