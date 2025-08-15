package au.com.telstra.simcardactivator;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/sim")
public class SimCardController {

    @PostMapping("/activate")
    public ResponseEntity<String> activateSim(@RequestBody SimCard simCard) {
        // Extract ICCID and customer email from the SimCard object
        String iccid = simCard.getIccid();
        String customerEmail = simCard.getCustomerEmail();

        // Print the details (you could also log this)
        System.out.println("Received request to activate SIM card:");
        System.out.println("ICCID: " + iccid + ", Customer Email: " + customerEmail);

        // Define the actuator URL
        String actuatorUrl = "http://localhost:8444/actuate";

        // Prepare JSON payload for the actuator service
        String jsonPayload = "{\"iccid\": \"" + iccid + "\"}";

        // Set up HTTP headers to send the request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create the HTTP entity (headers + body)
        HttpEntity<String> entity = new HttpEntity<>(jsonPayload, headers);

        // Initialize RestTemplate to make HTTP requests
        RestTemplate restTemplate = new RestTemplate();

        try {
            // Make the POST request to the actuator service
            ResponseEntity<String> actuatorResponse = restTemplate.exchange(
                    actuatorUrl, HttpMethod.POST, entity, String.class);

            // Check the response from actuator
            if (actuatorResponse.getStatusCode() == HttpStatus.OK) {
                // You could parse the response to get the "success" field here
                return ResponseEntity.ok("SIM activation successful");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("SIM activation failed");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error calling actuator service: " + e.getMessage());
        }
    }
}
