

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CodeHD4ME {
	public static void main(String[] args) {
		int pagesToCheck = 0;		//Converte il valore in intero pageField
		int pagesChecked = 1;		//Contiene il numero di pagine analizzate per l'esito finale
		int filmFoundOut = 0;		//Contiene il numero dei film che soddisfano i requisiti
		String filmName;			//Contiene il nome del film
		String filmLink;			//Contiene l'url del film 
		String filmData;			//Contiene il nome del film, pagina, punteggio e url da inserire nel file
		String rate = null;			//Contiene il punteggio che l'utente vorrebbe utilizzare come minimo
		double checkRate = 0;		//Converte il valore della stringa checkRate
		double totalRate = 0;			//Contiene il punteggio di ogni film analizzato nel sito
		String completeUrl = null;	//URL utilizzato per navigare nella prima pagina
		String url = null;			//URL utilizzato per navigare dopo la la prima pagina
		String PathChromeDriver;	 //Contiene il path del chrome driver per il controllo dell'esistenza del file
		int length = 0;				//Lunghezza della variabile URL
		int startPage = 1;			//Numero della pagina iniziale, ha valore 1 se l'url non contiene una pagina di partenza 
		int menuResult;				//Contiene il respondo del menu iniziale (ok/annulla)
		boolean allRight = true;
		JTextField urlField = new JTextField(10);
	    JTextField pageField = new JTextField(10);
	    JTextField rateField = new JTextField(10);
	      
	      Object[] fields = {
	    	"<html><font face='Calibri' size='6' color='black'>URL del sito:", urlField,
	    	"<html><font face='Calibri' size='6' color='black'>Quante pagine vuoi analizzare?", pageField,
	    	"<html><font face='Calibri' size='6' color='black'>Punteggio minimo dei film che ti interessano (MAX 10):", rateField
	      };
		
	try {
		//Controllo dell'esistenza del webdriver nel path designato
		PathChromeDriver = "C:\\Driver\\chromedriver";
		File chromedriverFile = new File(PathChromeDriver, "\\chromedriver.exe");
		if (!chromedriverFile.exists()) {
			System.out.println("ChromeDriver.exe non trovato");
			JOptionPane.showMessageDialog(null, "<html><font face='Arial' size='5' color='black'>Non trovo il file chromedriver. exe<br>Assicurarsi che si trovi nel seguente percorso:<br>C:\\Driver\\chromedriver\\<br>", "Errore", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}else {
			System.out.println("Ho trovato il ChromeDriver");
			System.setProperty("webdriver.chrome.driver", "C:\\Driver\\chromedriver\\chromedriver.exe");
		}
		
		do {
		  menuResult = JOptionPane.showConfirmDialog(null, fields, "HD4ME Film Rate Finder", JOptionPane.OK_OPTION);
	      if (menuResult == JOptionPane.OK_OPTION) {
	    	
	    	  try {
	    	  //CAMPO DELLO STAR RATING DEI FILM
		 		rate = rateField.getText();
		        //Sistema automaticamente la virgola con il punto
		        if(rate.contains(",")){
		 			rate = rate.replace(',','.');
		 		}
		 		checkRate = Double.parseDouble(rate);
	    	  }catch(Exception ex) {
	    		  if(checkRate != 0) {
	    			  JOptionPane.showMessageDialog(null, "<html><font face='Arial' size='5' color='black'>Il punteggio dei film non può superare 10 o essere inferiore a 1! <br>Riprova inserendo un numero corretto.<br>(Esempio: 3 oppure 4,5)", "Attenzione", JOptionPane.ERROR_MESSAGE);
	    			  allRight = false;
	    		  }
	    	  }
	      
	    	  //GESTIONE ERRORI INPUT
	    	  if (urlField.getText().equalsIgnoreCase("") || pageField.getText().equalsIgnoreCase("") || checkRate == 0) {	//Campi vuoti
	    		  JOptionPane.showMessageDialog(null, "<html><font face='Arial' size='5' color='black'>Riempi tutti i campi correttamente prima", "Attenzione", JOptionPane.ERROR_MESSAGE);
	    		  allRight = false;
	    	  }else if(checkRate > 10 || checkRate < 1) { //Numero rate non corretto
	    		  JOptionPane.showMessageDialog(null, "<html><font face='Arial' size='5' color='black'>Il punteggio dei film non può superare 10 o essere inferiore a 1! <br>Riprova inserendo un numero corretto.<br>(Esempio: 3 oppure 4,5)", "Attenzione", JOptionPane.ERROR_MESSAGE);
	    		  rateField.setText("");
	    		  allRight = false;
	    	  }else if(Integer.parseInt(pageField.getText()) < 1 ) {	//Numero pagina non corretto
	    		  JOptionPane.showMessageDialog(null, "<html><font face='Arial' size='5' color='black'>Devo controllare almeno una pagina!", "Attenzione", JOptionPane.ERROR_MESSAGE);
	    		  pageField.setText("");
	    		  allRight = false;
	    	  }
	    	  else {
	    		  	allRight = true;
	    		  	//CAMPO DELL'URL
	    		  	url = urlField.getText();
		    	  	length = url.length();
		         
		    	  	//Nel caso in cui l'url termini con il carattere '/' questo viene rimosso
		    	  	if(url.charAt(length-1) == '/') {	                
		    	  		url = url.substring(0, length-1);
		    	  	}
		 		
		    	  	//Trova in che pagina del sito si trova il link immesso e aggiorna l'url
		    	  	startPage = CheckStartPage (url);
		    	  	completeUrl = CheckUrl (url, startPage);
		    	  	
		    	  	//Aggiornare l'url per cercare le pagine successive
		    	  	if(url.contains("page")){
		    	  		int pageIndex = url.indexOf("page", 0);
		    	  		url = url.substring(0, pageIndex-1);
		    	  	}
	    	  
	 			
		 		//CAMPO DELLE PAGINE DA ANALIZZARE
		 		pagesToCheck = Integer.parseInt(pageField.getText());
		 		
		 		//Controllo campi
		 		System.out.println("URL: " + url);
		 		System.out.println("Pagina di partenza : " + startPage);
		 		System.out.println("Voto: " + rate);
		 		System.out.println("Pagine da controllare: " + pageField.getText());
		         
	    	  }
	      }else {
	    	  JOptionPane.showMessageDialog(null, "<html><font face='Arial' size='5' color='black'>Grazie per aver utilizzato questo programma", "Arrivederci" , JOptionPane.INFORMATION_MESSAGE);
	    	  System.exit(0);
	      }
		}while (allRight == false);
		
		
		//Avviso per l'utente
		JOptionPane.showMessageDialog(null, "<html><font face='Arial' size='5' color='black'>Appena questa finestra sarà chiusa partirà il robot che cercherà tutti i film che potrebbero interessarti.<br>"
				+ "Si aprirà una piccola finestra di Chrome che navigherà nelle pagine automaticamente, <br>"
				+ "NON cliccare al suo interno, puoi anche spostarla o ridurla a icona se ti da fastidio.<br>"
				+ "Se la chiudi il programma smette di funzionare. <br><br>Quando il programma avrà terminato il suo lavoro chiuderà da solo Google Chrome e ti avviserà.", "Cineblog Film Star Finder", JOptionPane.INFORMATION_MESSAGE);
		
		System.out.println("Sto per connettermi all'URL: " + completeUrl);
		WebDriver driver = new ChromeDriver();
		driver.get(completeUrl);
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		
		//modifica le dimensioni della finestra:
		Dimension d=new Dimension(200, 300);
		driver.manage().window().setSize(d);
		   
		
		do {
			System.out.println("Sono a pagina: " + startPage);
			int i = 0;
			//Trova gli elementi film (10 per pagina)
			List<WebElement> filmTable = driver.findElements(By.xpath("//table[@id='content_post']"));
			

			while(i < filmTable.size()) {
					
				List<WebElement> tableRows = filmTable.get(i).findElements(By.tagName("tr"));

				filmLink = tableRows.get(0).findElement(By.tagName("a")).getAttribute("href");	//Link del film
				filmName = tableRows.get(0).getText();			//Nome del film
				int temp1 = filmName.lastIndexOf(')');
				filmName = filmName.substring(0, temp1+1);				//Rimuove elementi inutili dal nome
					
				//Nella riga 14 (index = 14) della tabella c'è il punteggio del film
				String filmRate = tableRows.get(14).getText();		//Punteggio del film del tipo IMDbX.X/XXXX,XXXvotes
				
				//Nella prima pagina, forse per il caricamento troppo veloce filmRate ha un length di 4 (solo IMDb)
				//quando dovrebbe essere tra i 21-22 poichè il suo formato è IMDbX.X/XXXX,XXXvotes e il programma proseguiva salntando.
				//Adesso ricarica l'elemento fino a quando non ha grandezza superiore a 15 ovvero ha caricato il suo voto.
				if(filmRate.length() >15) {			
					filmRate = filmRate.substring(4, 7);		//Contiene il punteggio in formato X.X
					totalRate = Double.parseDouble(filmRate) ;
					
					//Se il film ha il punteggio adeguato
					if (totalRate >= checkRate) {
						filmFoundOut++;			//Aggiorna il contatore dei film trovati
						
						//TODO bisogna stampare su file con la stessa formattazione
						System.out.printf("%-50s | %-22s | Voto: %-5.1f |\n", filmName, filmLink, totalRate);
						filmData = filmName + filmLink + totalRate;
						CreateFile (filmData);
					}
					i++;
				}
			}
			System.out.println("Pagina analizzata");
			pagesToCheck--;
			
			if (pagesToCheck!= 0) {
				try {
				//Clicca sulla pagina successiva 	
				startPage++;	
				String nextPage = String.format("//a[@href='%s/page/%s']",url, (startPage));
				driver.findElement(By.xpath(nextPage)).click(); 
				
				//Aspetta che l'url della pagina successiva sia caricato
				WebDriverWait wait = new WebDriverWait(driver, 20); 
				wait.until(ExpectedConditions.urlToBe(url + "/page/" + (startPage) ));
				
				pagesChecked++;
				System.out.println("adesso sono a pagina: " + startPage);
				
				//Se non ci sono più pagine da controllare nonostante il numero immesso dall'utente non sia ancora arrivato a zero o se ci sono problemi:
				}catch(Exception ex) {
					driver.close();
					if (filmFoundOut > 0) {
						CreateFile ("\n\nHo analizzato " + pagesChecked + " pagine al posto di " + (pagesChecked + pagesToCheck) + "per un totale di " + (pagesChecked * 10) + " film, di cui " + filmFoundOut + " corrispondono ai tuoi criteri di ricerca.\n\n\n");
						JOptionPane.showMessageDialog(null, "<html><font face='Arial' size='5' color='black'>Ricerca terminata!<br>Ho analizzato " + pagesChecked + " pagine al posto di " + (pagesChecked + pagesToCheck) + ". (Fino a pag. " + startPage + ")<br>Per un totale di " + (pagesChecked * 10) + " film esaminati. <br><br>Ho trovato " + filmFoundOut + " Film che potrebbero interessarti. <br> Ho salvato i tuoi film nel file \"Lista Film\" sul Desktop.", "Ricerca Terminata", JOptionPane.INFORMATION_MESSAGE);
					}else {
						JOptionPane.showMessageDialog(null, "<html><font face='Arial' size='5' color='black'>Ricerca terminata!<br>Ho analizzato " + pagesChecked + " pagine al posto di " + (pagesChecked + pagesToCheck) + ". (Fino a pag. " + startPage + ")<br>Per un totale di " + (pagesChecked * 10) + " film esaminati. <br><br>Non ho trovato film per te con un punteggio di " + checkRate + " stelle o più.<br>Riprova con un punteggio minore.", "Ricerca Terminata", JOptionPane.INFORMATION_MESSAGE);
					}
					
					System.exit(0);
				}
			}
		}while(pagesToCheck != 0);
			driver.close();
			if (filmFoundOut > 0) {
				CreateFile ("\n\nHo analizzato " + pagesChecked + " pagine, per un totale di " + (pagesChecked * 10) + " film, di cui " + filmFoundOut + " corrispondono ai tuoi criteri di ricerca.\n\n\n");
				JOptionPane.showMessageDialog(null, "<html><font face='Arial' size='5' color='black'>Ricerca terminata correttamente!<br>Ho analizzato " + pagesChecked + " pagine. (Fino a pag. " + startPage + ")<br>Per un totale di " + (pagesChecked * 10) + " film esaminati. <br><br>Ho trovato " + filmFoundOut + " Film che potrebbero interessarti. <br> Ho salvato i tuoi film nel file \"Lista Film\" sul Desktop.", "Ricerca Terminata", JOptionPane.INFORMATION_MESSAGE);
			}else {
				JOptionPane.showMessageDialog(null, "<html><font face='Arial' size='5' color='black'>Ricerca terminata correttamente!<br>Ho analizzato " + pagesChecked + " pagine. (Fino a pag. " + startPage + ")<br>Per un totale di " + (pagesChecked * 10) + " film esaminati. <br><br>Non ho trovato film per te con un punteggio di "+ checkRate + " stelle o più.<br>Riprova con un punteggio minore.", "Ricerca Terminata", JOptionPane.INFORMATION_MESSAGE);
			}
		
		System.exit(0);
		
	}catch (NumberFormatException e) {
		System.out.println("Errore, assicurati di aver inserito dei numeri validi\n");
		JOptionPane.showMessageDialog(null, "<html><font face='Arial' size='5' color='black'>Input non valido, assicurarsi di inserire solo numeri validi", "Errore", JOptionPane.ERROR_MESSAGE);
		System.exit(0);
	}catch(SessionNotCreatedException e) {
		System.out.println("Chrome non trovato o da aggiornare\n");
		JOptionPane.showMessageDialog(null, "<html><font face='Arial' size='5' color='black'>"
				+ "Verifica di aver installato sul tuo pc Google Chrome.<br>"
				+ "Se è già presente ciò significa che il tuo browser ha installato un nuovo aggiornamento!<br><br>"
				+ "Verifica il numero di versione di Chrome inserendo nell'URL: \"chrome://settings/help\".<br>"
				+ "Aggiorna il robot da \"http://chromedriver.chromium.org/downloads\"<br>"
				+ "scaricando la versione corrispondente con lo stesso numero del tuo Browser<br>"
				+ "E' necessario che combacino solo i primi due numeri <br>(es: Chrome vers. 74.0.3729.131 e Robot vers. 74.X.XXXX.X..)<br>"
				+ "<br>Scaricato il robot bisognerà sostituirlo con il precedente <br>"
				+ "che si trova nel percorso: C:\\Driver\\chromedriver\\<br><br>Dopodichè riavviare il programma.", "Attenzione", JOptionPane.ERROR_MESSAGE);
		System.exit(0);
	}catch (NullPointerException e) {
		//e.printStackTrace();
		System.out.println("Browser chiuso manualmente.\n");
		JOptionPane.showMessageDialog(null, "<html><font face='Arial' size='5' color='black'>Hai chiuso il Browser!", "Errore", JOptionPane.ERROR_MESSAGE);
		System.exit(0);
	}catch (WebDriverException e) {
		e.printStackTrace();
		System.out.println("Errore, controllare dati immessi\n");
		JOptionPane.showMessageDialog(null, "<html><font face='Arial' size='5' color='black'>Il Browser è stato chiuso a causa di qualche problema.<br>controlla la connessione o l'url immesso.", "Errore", JOptionPane.ERROR_MESSAGE);
		System.exit(0);		
	}
		
}
	
	
	public static String CheckUrl (String url, int startPage) {
		int urlLength = url.length();
 		//Aggiorna url da cercare
 		String completeUrl = url;
 		
 		//Se l'url non parte dalla prima pagina, ma da una già specificata bisogna aggiornare l'URL togliendo quella pagina:
 		if(url.contains("page")){
 			urlLength = url.length();
 			if(url.charAt(urlLength-2) == '/') {				//https://cb01/page/x
 				char temp = url.charAt(urlLength-1);
 				startPage = Character.getNumericValue(temp);
 				url = url.substring(0, urlLength-7);
 			}else if(url.charAt(urlLength-4) == '/') {   		//https://cb01/page/xxx		
 				String temp = url.substring(urlLength - 3, urlLength);
 				startPage = Integer.parseInt(temp);
 				url = url.substring(0, urlLength-9);
 			}else if(url.charAt(urlLength-5) == '/') {   		//https://cb01/page/xxxx
 				String temp = url.substring(urlLength - 4, urlLength);
 				startPage = Integer.parseInt(temp);
 				url = url.substring(0, urlLength-10);
 			}else {											//https://cb01/page/xx
 				String temp = url.substring(urlLength - 2, urlLength);
 				startPage = Integer.parseInt(temp);
 				url = url.substring(0, urlLength-8);
 			}
 			completeUrl = url + "/page/" + startPage;
 		}
 		return completeUrl;
	}
	
	public static int CheckStartPage (String url) {
		int urlLength = url.length();
		int startPage = 1;
 		
 		//Se l'url non parte dalla prima pagina, ma da una già specificata bisogna individuare il numero di pagina:
 		if(url.contains("page")){
 			urlLength = url.length();
 			if(url.charAt(urlLength-2) == '/') {				////http://hd4me.net/page/x
 				char temp = url.charAt(urlLength-1);
 				startPage = Character.getNumericValue(temp);
 			}else if(url.charAt(urlLength-4) == '/') {   		//http://hd4me.net/page/xxx		
 				String temp = url.substring(urlLength - 3, urlLength);
 				startPage = Integer.parseInt(temp);
 			}else if(url.charAt(urlLength-5) == '/') {   		////http://hd4me.net/page/xxxx
 				String temp = url.substring(urlLength - 4, urlLength);
 				startPage = Integer.parseInt(temp);
 			}else {											////http://hd4me.net/page/xx
 				String temp = url.substring(urlLength - 2, urlLength);
 				startPage = Integer.parseInt(temp);
 			}
 		}
 		return startPage;
	}
	
	
	public static void CreateFile (String text) {
		String desktopPath = System.getProperty("user.home");
		String fileName = desktopPath + "\\Desktop\\Lista Film HD4ME.txt";
		PrintWriter outputStream = null;
		
		try {			
			//Crea un nuovo file solo se non esiste già, altrimenti aggiunge in coda il testo
			outputStream = new PrintWriter (new FileOutputStream (fileName, true));
		}catch (FileNotFoundException e) {
			System.out.println("Errore nell'apertura del file");
			JOptionPane.showMessageDialog(null, "<html><font face='Arial' size='5' color='black'>Non riesco a creare e/o accedere al File sul Desktop", "Errore", JOptionPane.ERROR_MESSAGE);
			System.exit(0);    
		}
		//Inserisce nel file i dati e lo chiude
		outputStream.println(text);
		outputStream.close();
		System.out.println("File scritto correttamente");	
	}
	

}//Fine classe




