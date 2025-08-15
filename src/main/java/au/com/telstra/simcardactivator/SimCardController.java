package au.com.telstra.simcardactivator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/sim")
public class SimCardController {

    @Autowired
    private SimCardRepository simCardRepository;

    @PostMapping("/activate")
    public ResponseEntity<String> activateSim(@RequestBody SimCard simCard) {
        // Extract ICCID and customer email from the SimCard object
        String iccid = simCard.getIccid();
        String customerEmail = simCard.getCustomerEmail();

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
            boolean activationStatus = actuatorResponse.getStatusCode() == HttpStatus.OK;

            // Save the activation status to the database
            SimCard newSimCard = new SimCard();
            newSimCard.setIccid(iccid);
            newSimCard.setCustomerEmail(customerEmail);
            newSimCard.setActive(activationStatus);

            // Save the new SIM card activation record to the database
            simCardRepository.save(newSimCard);

            if (activationStatus) {
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

    // Query endpoint to get SimCard by ID
    @GetMapping("/{simCardId}")
    public ResponseEntity<SimCard> getSimCard(@PathVariable Long simCardId) {
        // Find the SimCard by ID
        SimCard simCard = simCardRepository.findById(simCardId).orElse(null);

        // If not found, return a 404 Not Found response
        if (simCard == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }

        // If found, return the SimCard in the response
        return ResponseEntity.ok(simCard);
    }
}
