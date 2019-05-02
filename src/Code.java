

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Code {
	public static void main(String[] args) {
		
		System.setProperty("webdriver.chrome.driver", "C:\\Driver\\chromedriver\\chromedriver.exe");
		String completeUrl = "http://hd4me.net";
		int i = 0;
		int currentPage = 1;
		
		
		
		WebDriver driver = new ChromeDriver();
		driver.get(completeUrl);
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		
	do {
		try {
			System.out.println("Sono a pagina: " + currentPage);
			i = 0;
			//Trova gli elementi film (10 per pagina)
			List<WebElement> filmTable = driver.findElements(By.xpath("//table[@id='content_post']"));
			
			while(i < filmTable.size()) {
				
				List<WebElement> tableRows = filmTable.get(i).findElements(By.tagName("tr"));
				
				
				String filmLink = tableRows.get(0).findElement(By.tagName("a")).getAttribute("href");	//Link del film
				String filmName = tableRows.get(0).getText();			//Nome del film
				int temp1 = filmName.lastIndexOf(')');
				filmName = filmName.substring(0, temp1+1);				//Rimuove elementi inutili dal nome
				//Nella riga 14 (index = 14) della tabella c'è il punteggio del film
				String filmRate = tableRows.get(14).getText();		//Punteggio del film del tipo IMDbX.X/XXXX,XXXvotes
				
				//Nella prima pagina, forse per il caricamento troppo veloce filmRate ha un length di 4 (solo IMDb)
				//quando dovrebbe essere tra i 21-22 poichè il suo formato è IMDbX.X/XXXX,XXXvotes e il programma proseguiva salntando.
				//Adesso ricarica l'elemento fino a quando non ha grandezza superiore a 15 ovvero ha caricato il suo voto.
				if(filmRate.length() >15) {
					filmRate = filmRate.substring(4, 7);						//Contiene il punteggio in formato X.X
					double rate = Double.parseDouble(filmRate) ;
					if (rate >= 7.4) {
						System.out.printf("%d) %-50s | %-22s | Voto: %-5.1f |\n", i+1,filmName, filmLink, rate);
					}
						i++;
				}
			}
					//Clicca sulla pagina successiva
					String nextPage = String.format("//a[@href='%s/page/%s']",completeUrl, (currentPage + 1));
					driver.findElement(By.xpath(nextPage)).click(); 
					
					//Aspetta che l'url della pagina successiva sia caricato
					WebDriverWait wait = new WebDriverWait(driver, 20);
					wait.until(ExpectedConditions.urlToBe(completeUrl + "/page/" + (currentPage + 1)));
					
					currentPage++;
					System.out.println();
				
			
				
		}catch(Exception ex) {
			//ex.printStackTrace();
		}
	}while(currentPage != 5);	
	System.out.println("Fine");
	driver.close();
		
	}
}
