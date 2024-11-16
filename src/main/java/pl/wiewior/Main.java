package pl.wiewior;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Ustawienia sterownika Chrome (podaj ścieżkę do chromedriver, jeśli nie jest w PATH)
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
                WebElement closeAd = driver.findElement(By.xpath("//img[@alt='Ad']"));
                closeAd.click();
            } catch (Exception e) {
                System.out.println("Nie znaleziono reklamy lub nie udało się jej zamknąć.");
            }

            // Szukamy formularza logowania
            WebElement form = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//form[contains(@class, 'form-validate')]")));

            if (form == null) {
                System.out.println("Formularz nie został znaleziony");
                return;
            }

            // Wypełniamy pola formularza logowania
            WebElement usernameInput = driver.findElement(By.name("accountName"));
            WebElement passwordInput = driver.findElement(By.name("accountPassword"));

            if (usernameInput != null) {
                usernameInput.sendKeys("x"); // wpisz nazwę użytkownika
            } else {
                System.out.println("Nie udało się znaleźć pola nazwy użytkownika.");
            }

            if (passwordInput != null) {
                passwordInput.sendKeys("x"); // wpisz hasło
            } else {
                System.out.println("Nie udało się znaleźć pola hasła.");
            }


            // Klikamy przycisk logowania
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

        String desiredDate = "Czwartek, 21.11.2024"; // Wybrana data
        String desiredTime = "23:00"; // Wybrana godzina


        List<WebElement> dayElements = new ArrayList<>();
        for (int i = 0; i <= 7; i++) {
            // Pobieranie wszystkich elementów z datami
            List<WebElement> foundElements = driver.findElements(By.xpath
                    ("/html/body/app-root/app-layout/div/main/section/app-tabs/div/app-new-reservation/div/div[1]/div/div[2]/div[" + i + "]/div/div")); // wciaz znajduje sie tutaj tylko Sobota, 16.11.2024 ??
            dayElements.addAll(foundElements);
        }
        boolean dateFound = false;
        System.out.println(dayElements.listIterator(dayElements.size()));

        // Szukamy odpowiedniej daty
        for (WebElement dayElement : dayElements) {
            WebElement dateElement = dayElement.findElement(By.xpath("//div[contains(@class, 'card-header text-muted day-name')]"));
            String dateText = dateElement.getText(); // Pobieramy tekst daty

            // Sprawdzamy, czy data się zgadza
            if (dateText.equals(desiredDate)) {
                System.out.println("Znaleziono odpowiednią datę: " + dateText);
                dateFound = true;
                break;
            }

        }
        if (!dateFound) {
            System.out.println("Nie znaleziono wybranej daty.");
            return; // Jeśli nie znaleziono daty, kończymy wykonanie
        }


        /*// Pobieranie wszystkich elementów z godzinami
        List<WebElement> timeElements = driver.findElements(By.xpath("//div[@class='time']"));
        boolean timeFound = false;

        // Szukamy odpowiedniej godziny
        for (WebElement timeElement : timeElements) {
            WebElement timeTextElement = timeElement.findElement(By.xpath(".//div[@class='hour']"));
            String timeText = timeTextElement.getText().trim(); // Pobieramy tekst godziny

            // Sprawdzamy, czy godzina się zgadza i czy element nie jest zablokowany
            if (timeText.equals(desiredTime) && !timeElement.getAttribute("class").contains("hour disabled")) {
                System.out.println("Znaleziono odpowiednią godzinę: " + timeText);

                // Klikamy w godzinę, aby ją wybrać
                timeElement.click();
                timeFound = true;
                break;
            }
        }

        if (!timeFound) {
            System.out.println("Nie znaleziono dostępnej godziny.");
            return; // Jeśli nie znaleziono godziny, kończymy wykonanie
        }

        // Oczekiwanie na przycisk rezerwacji
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(), 'Rezerwuj')]")));

       WebElement option1 = driver.findElement(By.id(""));
       option1.click(); //Wcisniecie boxa z akceptacja regulaminu
       if(option1.isSelected()){
       System.out.println("Checkbox if Toggled On");
       }else{
       System.out.println("Checkbox is Toggled Off");
}

        // Kliknięcie przycisku rezerwacji
        WebElement reserveButton = driver.findElement(By.xpath("//button[contains(text(), 'Rezerwuj')]"));
        reserveButton.click();
        System.out.println("Zarezerwowano kort na " + desiredDate + " o godzinie " + desiredTime);*/
        driver.quit();
    }
}
