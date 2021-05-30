package es.animal.protection.animalshelter.domain.rest;

import es.animal.protection.animalshelter.domain.model.Adopter;
import reactor.core.publisher.Mono;

public interface AdopterMicroservice {

    Mono<Adopter> readByNif(String nif);

}
