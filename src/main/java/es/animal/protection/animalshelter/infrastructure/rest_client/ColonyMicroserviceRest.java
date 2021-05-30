package es.animal.protection.animalshelter.infrastructure.rest_client;

import es.animal.protection.animalshelter.domain.exceptions.BadGatewayException;
import es.animal.protection.animalshelter.domain.exceptions.NotFoundException;
import es.animal.protection.animalshelter.domain.model.Colony;
import es.animal.protection.animalshelter.domain.rest.ColonyMicroservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service("colonyClient")
public class ColonyMicroserviceRest implements ColonyMicroservice {

    public static final String REGISTRY_VAL = "/{registry}";
    public static final String COLONIES = "/colonies";

    @Autowired
    public ColonyMicroserviceRest(@Value("${animal.shelter.colony}") String colonyUri, WebClient.Builder webClientBuilder) {
        this.colonyUri = colonyUri;
        this.webClientBuilder = webClientBuilder;
    }

    private String colonyUri;
    private WebClient.Builder webClientBuilder;

    @Override
    public Mono<Colony> readByRegistry(String registry) {
        return webClientBuilder.build()
                .get()
                .uri(colonyUri + COLONIES + REGISTRY_VAL, registry)
                .exchange()
                .onErrorResume(exception ->
                        Mono.error(new BadGatewayException("Unexpected error. Colony Microservice. " + exception.getMessage())))
                .flatMap(response -> {
                    if (HttpStatus.NOT_FOUND.equals(response.statusCode())) {
                        return Mono.error(new NotFoundException("Colony with registry: " + registry));
                    } else if (response.statusCode().isError()) {
                        return Mono.error(new BadGatewayException("Unexpected error: Colony Microservice."));
                    } else {
                        return response.bodyToMono(Colony.class);
                    }
                });
    }

}
