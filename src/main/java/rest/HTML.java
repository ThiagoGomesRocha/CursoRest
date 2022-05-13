package rest;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;


import org.junit.Test;

import io.restassured.http.ContentType;

public class HTML {
	
	@Test
	public void deveFazerBuscasComHtml() {
		given()
			.log().all()
		.when()
			.get("https://restapi.wcaquino.me/v2/users")
		.then()
			.log().all()
			.statusCode(200)
			.contentType(ContentType.HTML)
			.body("html.body.div.table.tbody.tr.size()",is(3) )
		;
		
	}

}
