package com.ievolve.jwt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import org.springframework.mock.web.MockHttpServletResponse;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class JwtApplicationTests {

	private MockMvc mvc;
	private final ObjectMapper objectMapper = new ObjectMapper();

	String c_u = "jack", s_u = "apple", p = "pass_word";

	@Autowired
	WebApplicationContext context;

	@BeforeEach
	void setMockMvc() {
		mvc = MockMvcBuilders.webAppContextSetup(context)
				.apply(SecurityMockMvcConfigurers.springSecurity())
				.build();
	}

	// ---------------------------------------------------------
	// PRODUCT SEARCH
	// ---------------------------------------------------------

	@Test
	@Order(1)
	public void productSearchStatus() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/api/public/product/search")
				.param("keyword", "tablet"))
				.andExpect(status().is(200))
				.andExpect(jsonPath("$", notNullValue()));
	}

	@Test
	@Order(2)
	public void productSearchWithoutKeyword() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/api/public/product/search"))
				.andExpect(status().is(400));
	}

	@Test
	@Order(3)
	public void productSearchWithProductName() throws Exception {
		MvcResult res = mvc.perform(MockMvcRequestBuilders.get("/api/public/product/search")
				.param("keyword", "tablet"))
				.andExpect(status().is(200))
				.andReturn();

		JsonNode arr = objectMapper.readTree(res.getResponse().getContentAsString());
		assert (arr.isArray() && arr.size() > 0);

		for (int i = 0; i < arr.size(); i++) {
			JsonNode obj = arr.get(i);
			Assertions.assertTrue(obj.get("productName").asText().toLowerCase().contains("tablet"));
		}
	}

	// ---------------------------------------------------------
	// LOGIN TESTS
	// ---------------------------------------------------------

	public String getJSONCreds(String u, String p) throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put("username", u);
		map.put("password", p);
		return objectMapper.writeValueAsString(map);
	}

	public MockHttpServletResponse loginHelper(String u, String p) throws Exception {
		return mvc.perform(MockMvcRequestBuilders.post("/api/public/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(getJSONCreds(u, p)))
				.andReturn().getResponse();
	}

	@Test
	@Order(4)
	public void consumerLoginWithBadCreds() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/api/public/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(getJSONCreds(c_u, "wrong_pass")))
				.andExpect(status().is(401));
	}

	@Test
	@Order(5)
	public void consumerLoginWithValidCreds() throws Exception {
		Assertions.assertEquals(200, loginHelper(c_u, p).getStatus());
		Assertions.assertNotEquals("", loginHelper(c_u, p).getContentAsString());
	}

	// ---------------------------------------------------------
	// JWT AUTH TESTS (CART / SELLER / CONSUMER)
	// ---------------------------------------------------------

	@Test
	@Order(6)
	public void consumerGetCartWithValidJWT() throws Exception {
		String responseContent = loginHelper(c_u, p).getContentAsString();
		JsonNode jsonResponse = objectMapper.readTree(responseContent);
		String token = jsonResponse.get("accessToken").asText();

		mvc.perform(MockMvcRequestBuilders.get("/api/auth/consumer/cart")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().is(200))
				.andExpect(jsonPath("$.cartId", not(equalTo(""))));
	}

	@Test
	@Order(7)
	public void sellerApiWithConsumerJWT() throws Exception {
		String responseContent = loginHelper(c_u, p).getContentAsString();
		JsonNode jsonResponse = objectMapper.readTree(responseContent);
		String token = jsonResponse.get("accessToken").asText();

		mvc.perform(MockMvcRequestBuilders.get("/api/auth/seller/product")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().is(403));
	}

	@Test
	@Order(8)
	public void sellerLoginWithValidCreds() throws Exception {
		Assertions.assertEquals(200, loginHelper(s_u, p).getStatus());
	}

	// ---------------------------------------------------------
	// PRODUCT CREATION / UPDATE / DELETE
	// ---------------------------------------------------------

	public String getProduct(int id, String name, double price, int cId, String cName) throws Exception {
		ObjectNode map = objectMapper.createObjectNode();
		map.put("productId", id);
		map.put("productName", name);
		map.put("price", price);

		ObjectNode mapC = objectMapper.createObjectNode();
		mapC.put("categoryId", cId);
		mapC.put("categoryName", cName);
		map.set("category", mapC);

		return objectMapper.writeValueAsString(map);
	}

	static String createdURI;

	@Test
	@Order(9)
	public void sellerAddNewProduct() throws Exception {
		String responseContent = loginHelper(s_u, p).getContentAsString();
		String token = objectMapper.readTree(responseContent).get("accessToken").asText();

		createdURI = mvc.perform(MockMvcRequestBuilders.post("/api/auth/seller/product")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(getProduct(10, "iPhone 11", 49999.0, 2, "Electronics")))
				.andExpect(status().is(201))
				.andReturn().getResponse().getRedirectedUrl();
	}

	@Test
	@Order(10)
	public void sellerCheckAddedProduct() throws Exception {
		String responseContent = loginHelper(s_u, p).getContentAsString();
		String token = objectMapper.readTree(responseContent).get("accessToken").asText();

		mvc.perform(MockMvcRequestBuilders.get(new URL(createdURI).getPath())
				.header("Authorization", "Bearer " + token))
				.andExpect(status().is(200))
				.andExpect(jsonPath("$.productName", is("iPhone 11")));
	}

	@Test
	@Order(11)
	public void sellerCheckProductFromAnotherSeller() throws Exception {
		String responseContent = loginHelper("glaxo", p).getContentAsString();
		String token = objectMapper.readTree(responseContent).get("accessToken").asText();

		mvc.perform(MockMvcRequestBuilders.get(new URL(createdURI).getPath())
				.header("Authorization", "Bearer " + token))
				.andExpect(status().is(404));
	}

	@Test
	@Order(12)
	public void sellerUpdateProduct() throws Exception {
		String responseContent = loginHelper(s_u, p).getContentAsString();
		String token = objectMapper.readTree(responseContent).get("accessToken").asText();

		mvc.perform(MockMvcRequestBuilders.put("/api/auth/seller/product")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(getProduct(10, "iPhone 12", 58999.0, 2, "Electronics")))
				.andExpect(status().is(200));
	}

	@Test
	@Order(13)
	public void sellerUpdateProductWithWrongId() throws Exception {
		String responseContent = loginHelper(s_u, p).getContentAsString();
		String token = objectMapper.readTree(responseContent).get("accessToken").asText();

		mvc.perform(MockMvcRequestBuilders.put("/api/auth/seller/product")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(getProduct(30, "iPhone 12", 58999.0, 2, "Electronics")))
				.andExpect(status().is(404));
	}

	// ---------------------------------------------------------
	// CART OPERATIONS
	// ---------------------------------------------------------

	public String getCartProduct(String product, int q) throws Exception {
		ObjectNode map = objectMapper.createObjectNode();
		map.set("product", objectMapper.readTree(product));
		map.put("quantity", q);
		return objectMapper.writeValueAsString(map);
	}

	@Test
	@Order(14)
	public void consumerAddProductToCart() throws Exception {
		String responseContent = loginHelper(c_u, p).getContentAsString();
		String token = objectMapper.readTree(responseContent).get("accessToken").asText();

		mvc.perform(MockMvcRequestBuilders.post("/api/auth/consumer/cart")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(getCartProduct(getProduct(10, "iPhone 12", 58999.0, 2, "Electronics"), 1)))
				.andExpect(status().is(200));
	}

	@Test
	@Order(15)
	public void consumerAddProductToCartAgain() throws Exception {
		String responseContent = loginHelper(c_u, p).getContentAsString();
		String token = objectMapper.readTree(responseContent).get("accessToken").asText();

		mvc.perform(MockMvcRequestBuilders.post("/api/auth/consumer/cart")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(getCartProduct(getProduct(10, "iPhone 12", 58999.0, 2, "Electronics"), 1)))
				.andExpect(status().is(409));
	}

	@Test
	@Order(16)
	public void consumerUpdateProductInCart() throws Exception {
		String responseContent = loginHelper(c_u, p).getContentAsString();
		String token = objectMapper.readTree(responseContent).get("accessToken").asText();

		mvc.perform(MockMvcRequestBuilders.put("/api/auth/consumer/cart")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(getCartProduct(
						getProduct(10, "iPhone 12", 58999.0, 2, "Electronics"), 3)))
				.andExpect(status().is(200));
	}

	@Test
	@Order(17)
	public void consumerUpdateProductInCartWithZeroQuantity() throws Exception {
		String responseContent = loginHelper(c_u, p).getContentAsString();
		String token = objectMapper.readTree(responseContent).get("accessToken").asText();

		mvc.perform(MockMvcRequestBuilders.put("/api/auth/consumer/cart")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(getCartProduct(
						getProduct(10, "iPhone 12", 58999.0, 2, "Electronics"), 0)))
				.andExpect(status().is(200));
	}
}
