package rest;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.MatcherAssert;
import org.junit.BeforeClass;
import org.junit.Test;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import suport.User;

public class VerbosTest {
	
		@BeforeClass
	public static void init() {
		RestAssured.baseURI = "https://restapi.wcaquino.me";
		RestAssured.port = 443;
	}

	@Test
	public void deveSalvarUsuario() {
		given()
			.log().all()
			.contentType(ContentType.JSON)
			.body("{\"name\": \"Jose\",\"age\": 50}")
		.when()
			.post("/users")
		.then()
			.log().all()
			.statusCode(201)
			.body("id", is(notNullValue()))
			.body("age", is(50))
		;
	}
	
	@Test
	public void naoDeveSalvarUsuarioSemNome() {
		given()
			.log().all()
			.contentType("application/json")
			.body("{\"age\": 50}")
		.when()
			.post("/users")
		.then()
			.log().all()
			.statusCode(400)
			.body("id", is(nullValue()))
			.body("error", is(notNullValue()))
			.body("error", is("Name é um atributo obrigatório"))
		;
	}
	
	@Test
	public void deveSalvarViaXml() {
		given()
			.log().all()
			.contentType(ContentType.XML)
			.body("<user><name>Jose</name><age>50</age></user>")
		.when()
			.post("/usersXml")
		.then()
			.log().all()
			.statusCode(201)
			.body("user.@id", is(notNullValue()))
			.body("user.name", is("Jose"))
			.body("user.age", is("50"))
		;
	}
	
	@Test
	public void deveAlterarUsuario() {
		given()
			.log().all()
			.contentType(ContentType.JSON)
			.body("{\"name\": \"Judas\",\"age\": 60}")
		.when()
			.put("/users/1")
		.then()
			.log().all()
			.statusCode(200)
			.body("id", is(1))
			.body("name", is("Judas"))
			.body("age", is(60))
		;
	}
	
	@Test
	public void deveTentarAlterarUsuarioInexistente() {
		given()
			.log().all()
			.contentType(ContentType.JSON)
			.body("{\"name\": \"Judas\",\"age\": 60}")
		.when()
			.put("/users/156565")
		.then()
			.log().all()
			.statusCode(400)
			.body("error", is("Registro inexistente"))
		;
	}
	
	
	@Test
	public void deveCustomizarUrl() {
		given()
			.log().all()
			.contentType(ContentType.JSON)
			.body("{\"name\": \"Judas\",\"age\": 60}")
		.when()
			.put("/{entidade}/{id}","users","1")
		.then()
			.log().all()
			.statusCode(200)
			.body("id", is(1))
			.body("name", is("Judas"))
			.body("age", is(60))
		;
	}
	
	@Test
	public void deveCustomizarUrlComPathParam() {
		given()
			.log().all()
			.contentType(ContentType.JSON)
			.body("{\"name\": \"Judas\",\"age\": 60}")
			.pathParam("entidade", "users")
			.pathParam("id", 1)
		.when()
			.put("/{entidade}/{id}")
		.then()
			.statusCode(200)
			.body("id", is(1))
			.body("name", is("Judas"))
			.body("age", is(60))
		;
	}
	
	@Test
	public void deveRemoverUsuario() {
		given()
			.log().all()
		.when()
			.delete("/users/1")
		.then()
			.log().all()
			.statusCode(204)
		;
	}
	
	@Test
	public void deveTentarRemoverUsuarioInexistente() {
		given()
			.log().all()
			.pathParam("entidade", "users")
			.pathParam("id", "156565")
		.when()
			.delete("/{entidade}/{id}")
		.then()
			.log().all()
			.statusCode(400)
			.body("error", is("Registro inexistente"))
		;
	}
	
	@Test
	public void deveSalvarUsuarioUsandoMap() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", "usuario via map");
		params.put("age", 25);
		
		given()
			.log().all()
			.contentType(ContentType.JSON)
			.pathParam("entidade", "users")
			.body(params)
		.when()
			.post("/{entidade}")
		.then()
			.log().all()
			.statusCode(201)
			.body("id", is(notNullValue()))
			.body("name", is("usuario via map"))
			.body("age", is(25))
		;
	}
	
	@Test
	public void deveSalvarUsuarioUsandoObjeto() {
		User user = new User("Usuario Objeto", 35);
		
		given()
			.log().all()
			.contentType(ContentType.JSON)
			.pathParam("entidade", "users")
			.body(user)
		.when()
			.post("/{entidade}")
		.then()
			.log().all()
			.statusCode(201)
			.body("id", is(notNullValue()))
			.body("name", is("Usuario Objeto"))
			.body("age", is(35))
		;
	}
	
	@Test
	public void deveDesserializarObjetoAoSalvarUSuario() {
		User user = new User("Usuario Desserializado", 35);
		User usuarioInserido = 
		given()
			.log().all()
			.contentType(ContentType.JSON)
			.pathParam("entidade", "users")
			.body(user)
		.when()
			.post("/{entidade}")
		.then()
			.log().all()
			.statusCode(201)
			.extract().body().as(User.class)
		;
		assertEquals("Usuario Desserializado", usuarioInserido.getName());
		MatcherAssert.assertThat(usuarioInserido.getAge(), is(35));
	}
	
	@Test
	public void deveSalvarViaXmlUsandoObjeto() {
		User user = new User("Usuario Xml", 40);
		
		given()
			.log().all()
			.contentType(ContentType.XML)
			.body(user)
		.when()
			.post("/usersXml")
		.then()
			.log().all()
			.statusCode(201)
			.body("user.@id", is(notNullValue()))
			.body("user.name", is("Usuario Xml"))
			.body("user.age", is("40"))
		;
	}
	
	@Test
	public void deveDesserializarXmlaoSalvarUsuario() {
		User user = new User("Usuario Xml Desserializado", 45);
		User usuarioInserido =
		given()
			.log().all()
			.contentType(ContentType.XML)
			.body(user)
		.when()
			.post("/usersXml")
		.then()
			.log().all()
			.statusCode(201)
			.extract().body().as(User.class)
		;
		MatcherAssert.assertThat(usuarioInserido.getId(), is(notNullValue()));
		assertEquals("Usuario Xml Desserializado", usuarioInserido.getName());
		MatcherAssert.assertThat(usuarioInserido.getAge(), is(45));
	}
}
