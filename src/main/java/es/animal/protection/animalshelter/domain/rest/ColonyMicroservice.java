package es.animal.protection.animalshelter.domain.rest;

import es.animal.protection.animalshelter.domain.model.Colony;
import reactor.core.publisher.Mono;

public interface ColonyMicroservice {

    Mono<Colony> readByRegistry(String registry);

}
