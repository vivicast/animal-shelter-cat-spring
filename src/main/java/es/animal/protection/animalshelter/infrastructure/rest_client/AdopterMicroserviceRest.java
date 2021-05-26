package es.animal.protection.animalshelter.infrastructure.rest_client;

import es.animal.protection.animalshelter.domain.exceptions.BadGatewayException;
import es.animal.protection.animalshelter.domain.exceptions.NotFoundException;
import es.animal.protection.animalshelter.domain.model.Adopter;
import es.animal.protection.animalshelter.domain.rest.AdopterMicroservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service(AdopterMicroserviceRest.ADOPTER_CLIENT)
public class AdopterMicroserviceRest implements AdopterMicroservice {

    public static final String ADOPTER_CLIENT = "adopterClient";
    public static final String NIF_VAL = "/{nif}";
    public static final String ADOPTERS = "/adopters";

    private String adopterUri;
    private WebClient.Builder webClientBuilder;

    @Autowired
    public AdopterMicroserviceRest(@Value("${animal.shelter.adopter}") String adopterUri, WebClient.Builder webClientBuilder) {
        this.adopterUri = adopterUri;
        this.webClientBuilder = webClientBuilder;
    }

    public Mono<Adopter> readByNif(String nif) {
        return webClientBuilder.build()
                        .get()
                        .uri(adopterUri + ADOPTERS +NIF_VAL, nif)
                        .exchange()
                .onErrorResume(exception ->
                        Mono.error(new BadGatewayException("Unexpected error. Adopter Microservice. " + exception.getMessage())))
                .flatMap(response -> {
                    if (HttpStatus.NOT_FOUND.equals(response.statusCode())) {
                        return Mono.error(new NotFoundException("Adopter with nif: " + nif));
                    } else if (response.statusCode().isError()) {
                        return Mono.error(new BadGatewayException("Unexpected error: Adopter Microservice."));
                    } else {
                        return response.bodyToMono(Adopter.class);
                    }
                });
    }
}
