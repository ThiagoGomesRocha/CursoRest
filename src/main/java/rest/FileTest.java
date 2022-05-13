package rest;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.hamcrest.MatcherAssert;
import org.junit.Test;

public class FileTest {

	@Test
	public void deveObrigarEnvioArquivo() {
		given()
			.log().all()
		.when()
			.post("https://restapi.wcaquino.me/upload")
		.then()
			.log().all()
			.statusCode(404) // abrir issue para mudar para 400
			.body("error", is("Arquivo não enviado"))
		;
	}
	
	@Test
	public void naoDeveEnviarArquivosGrandes() {
		given()
			.log().all()
			.multiPart("arquivo", new File("src/main/resources/bematech.zip"))
		.when()
			.post("https://restapi.wcaquino.me/upload")
		.then()
			.log().all()
			.statusCode(413)
			.body("html.head.title",is("413 Request Entity Too Large"))
			
		;
	}

	@Test
	public void naoDeveEnviarArquivosGrandesComVerificacaoDeTempo() {
		given()
			.log().all()
			.multiPart("arquivo", new File("src/main/resources/bematech.zip"))
		.when()
			.post("https://restapi.wcaquino.me/upload")
		.then()
			.log().all()
			.time(lessThan(5000L))
			.statusCode(413)
			.body("html.head.title",is("413 Request Entity Too Large"))
			
		;
	}
	
	@Test
	public void deveBaixarArquivo() {
		byte[] image =
		given()
			.log().all()
		.when()
			.get("https://restapi.wcaquino.me/download")
		.then()
			.statusCode(200)
			.extract().asByteArray()
		;
		File imagem = new File("src/main/resources/file.jpeg");
		try {
			OutputStream out = new FileOutputStream(imagem);
			out.write(image);
			out.close();
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MatcherAssert.assertThat(imagem.length(), lessThan(100000L));
	}
	

}
