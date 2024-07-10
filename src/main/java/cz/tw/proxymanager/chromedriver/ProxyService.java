package cz.tw.proxymanager.chromedriver;

import cz.tw.proxymanager.domain.Proxy;
import cz.tw.proxymanager.domain.TwAccount;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class ProxyService {

    public ProxyService() {
        // Set the path to the ChromeDriver executable
        System.setProperty("webdriver.chrome.driver", "/chromedriver/chromedriver.exe");
    }

    public void runProxy(String proxyHost, int proxyPort, String proxyUsername, String proxyPassword) {
        // Create the extension dynamically
        File extensionZip = null;
        try {
            extensionZip = ProxyAuthExtension.createExtension(proxyHost, proxyPort, proxyUsername, proxyPassword);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Create a unique user data directory for each instance
        String userDataDir = System.getProperty("java.io.tmpdir") + File.separator + "chrome_user_data_" + proxyHost + "_" + proxyPort;
        new File(userDataDir).mkdirs();

        // Set ChromeOptions
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-web-security");
        options.addArguments("--allow-running-insecure-content");
        options.addArguments("--user-data-dir=" + userDataDir); // Use the unique user data directory
        options.addExtensions(extensionZip);

        // Create a new instance of the Chrome driver with the options
        WebDriver driver = new ChromeDriver(options);

        // Open the website
        driver.get("https://whatismyipaddress.com/");

        // Wait for a few seconds to see the result (optional)
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Close the browser
        driver.quit();
    }

    public void runProxy(TwAccount twAccount) {
        Proxy proxy = twAccount.getProxy();
        runProxy(proxy.getIpAddress(), proxy.getPort(), proxy.getUsername(), proxy.getPassword());
    }
}
