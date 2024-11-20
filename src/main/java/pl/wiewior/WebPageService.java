package pl.wiewior;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class WebPageService {

    @Value("${app.username}")
    private String username;
    @Value("${app.password}")
    private String password;

    public void bookTenis() {

        System.setProperty("webdriver.chrome.driver", "C:/Users/Wiewior/Downloads/chromedriver.exe");

        // Inicjalizacja WebDriver
        WebDriver driver = new ChromeDriver();

        // Czekaj, aż strona zostanie w pełni załadowana (10 sekund timeout)
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Otwieranie strony logowania
            driver.get("https://app.tenis4u.pl/#/login");

            // Maksymalizacja okna przeglądarki
            driver.manage().window().maximize();

            try {
                WebElement closeAd = driver.findElement(By.xpath("/html/body/app-root/app-ad-display/ngx-smart-modal/div/div/div/button"));
                closeAd.click();
            } catch (Exception e) {
                System.out.println("Nie znaleziono reklamy lub nie udało się jej zamknąć.");
            }


            driver.manage().timeouts().implicitlyWait(Duration.ofMillis(5));

            // Wypełniamy pola formularza logowania
            WebElement usernameInput = driver.findElement(By.name("accountName"));
            WebElement passwordInput = driver.findElement(By.name("accountPassword"));

            if (usernameInput != null) {
                usernameInput.sendKeys(username); // wpisz nazwę użytkownika
            } else {
                System.out.println("Nie udało się znaleźć pola nazwy użytkownika.");
            }

            if (passwordInput != null) {
                passwordInput.sendKeys(password); // wpisz hasło
            } else {
                System.out.println("Nie udało się znaleźć pola hasła.");
            }


            // Klikamy przycisk logowania
            driver.manage().timeouts().implicitlyWait(Duration.ofMillis(5));
            WebElement loginButton = driver.findElement(By.xpath("//button[@type='submit' and text()='Zaloguj']"));
            if (loginButton != null) {
                loginButton.click();

                // Oczekuj na załadowanie strony po zalogowaniu (np. sprawdzenie obecności przycisku rezerwacji kortu)
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(), 'Dashboard')]")));
                System.out.println("Zalogowano pomyślnie.");
            } else {
                System.out.println("Nie znaleziono przycisku logowania.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Przechodzimy do strony rezerwacji
        try {
            driver.get("https://app.tenis4u.pl/#/user/dashboard");
            driver.manage().timeouts().implicitlyWait(Duration.ofMillis(5));
            driver.get("https://app.tenis4u.pl/#/court/190");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String desiredDate = "Czwartek, 28.11.2024"; // Wybrana data
        String desiredTime = "18:00"; // Wybrana godzina


        boolean trueDay = false;
        List<WebElement> dayElements = null;
        while (trueDay == false) {
            driver.navigate().refresh();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            dayElements = driver.findElements(By.xpath("//div[contains(@class, 'card-header text-muted day-name')]"));
            for (WebElement dayElement : dayElements) {
                String dateText = dayElement.getText();

                if (dateText.equals(desiredDate)) {
                    trueDay = true;
                    System.out.println("Udało się!");
                } else {
                    System.out.println("Nie udało się :(");
                }
            }
        }

        boolean dateFound = false;
        for (WebElement dayElement : dayElements) {
            String dateText = dayElement.getText(); // Pobieramy tekst daty


            // Sprawdzamy, czy data się zgadza
            if (dateText.equals(desiredDate)) {
                System.out.println("Znaleziono odpowiednią datę: " + dateText);
                dateFound = true;
                break;
            }
        }
        // Szukamy odpowiedniej daty
        if (!dateFound) {
            System.out.println("Nie znaleziono wybranej daty.");
            return; // Jeśli nie znaleziono daty, kończymy wykonanie
        }

        // Pobieranie wszystkich elementów z godzinami
        List<WebElement> timeElements = driver.findElements(By.xpath("//div[@class='time']"));
        WebElement timeForGame = timeElements.get(120);
        //
        //
        //
        String timeText = timeForGame.getText();

        // Sprawdzamy, czy godzina się zgadza i czy element nie jest zablokowany
        if (timeText.equals(desiredTime) && !timeForGame.getAttribute("class").contains("hour disabled")) {
            System.out.println("Znaleziono odpowiednią godzinę: " + timeText);
            // Klikamy w godzinę, aby ją wybrać
            timeForGame.click();
        }

        // Oczekiwanie na przycisk rezerwacji
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(), 'REZERWUJ')]")));

        WebElement option1 = driver.findElement(By.xpath("/html/body/app-root/app-layout/div/main/section/app-tabs/div/app-new-reservation/div/div[2]/div[1]/app-reservation-summary/ul/li[8]/div/label"));
        option1.click(); //Wcisniecie boxa z akceptacja regulaminu
        if (option1.isSelected()) {
            System.out.println("Checkbox if Toggled Off");
        } else {
            System.out.println("Checkbox is Toggled On");
        }

        // Kliknięcie przycisku rezerwacji
        WebElement reserveButton = driver.findElement(By.xpath("//button[contains(text(), 'REZERWUJ')]"));
        reserveButton.click();
        WebElement confirmationButton = driver.findElement(By.xpath("/html/body/div/div/div[3]/button[1]"));
        confirmationButton.click();
        System.out.println("Zarezerwowano kort na " + desiredDate + " o godzinie " + desiredTime);
        driver.quit();
    }
}
