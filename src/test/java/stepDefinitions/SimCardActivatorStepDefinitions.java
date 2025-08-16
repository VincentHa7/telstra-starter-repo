package stepDefinitions;

import au.com.telstra.simcardactivator.SimCardActivator;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(classes = SimCardActivator.class, loader = SpringBootContextLoader.class)
public class SimCardActivatorStepDefinitions {

    @Autowired
    private TestRestTemplate restTemplate;

    private String iccid;
    private String activationResponse;

    @Given("the ICCID number is {string}")
    public void the_iccid_number_is(String iccid) {
        this.iccid = iccid;  // Capture ICCID for this test scenario
    }

    @When("I ask whether the sim card has been activated")
    public void i_ask_whether_the_sim_card_has_been_activated() {
        // Step 1: First activate the SIM card
        String activationUrl = "http://localhost:8080/sim/activate";
        String jsonPayload = "{\"iccid\": \"" + iccid + "\", \"customerEmail\": \"test@example.com\"}";

        // Set up headers for POST request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(jsonPayload, headers);

        // Make POST request to activate SIM card
        restTemplate.postForEntity(activationUrl, entity, String.class);

        // Step 2: Query the activation status
        String queryUrl = "http://localhost:8080/sim/query/" + iccid;
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(queryUrl, String.class);
            activationResponse = response.getBody();
        } catch (Exception e) {
            // Handle 404 or other errors - typically means "Not Activated"
            activationResponse = "Not Activated";
        }
    }

    @Then("I should be told {string}")
    public void i_should_be_told(String expectedAnswer) {
        // Assert that the response from the service matches the expected answer
        assertThat(activationResponse).isEqualTo(expectedAnswer);
    }
}
