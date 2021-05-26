package es.animal.protection.animalshelter.domain.service;

import es.animal.protection.animalshelter.domain.exceptions.NotFoundException;
import es.animal.protection.animalshelter.domain.model.Cat;
import es.animal.protection.animalshelter.domain.persistence.CatPersistence;
import es.animal.protection.animalshelter.domain.rest.AdopterMicroservice;
import es.animal.protection.animalshelter.infrastructure.mongodb.entities.CatEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CatService {

    private CatPersistence catPersistence;
    private AdopterMicroservice adopterMicroservice;

    @Autowired
    public CatService(CatPersistence catPersistence, AdopterMicroservice adopterMicroservice) {
        this.catPersistence = catPersistence;
        this.adopterMicroservice =adopterMicroservice;
    }

    public Mono<Cat> create(Cat cat) {
        return this.catPersistence.create(cat);
    }

    public Mono<Cat> read(Integer chip) {
        return this.catPersistence.read(chip);
    }

    public Mono<Cat> update(Integer chip, Cat cat) {
        Mono<CatEntity> catEntity = this.catPersistence.assertCatExist(chip);
        Mono<CatEntity> updateCatEntity;
        if (cat.getAdopterNif() != null) {
            updateCatEntity =  this.createAdoption(catEntity, cat);
        } else if (cat.getColonyRegistry() != null) {
            //return this.assignColony(catEntity, cat);
            return null;
        } else {
            updateCatEntity = this.updateCat(catEntity, cat);
        }
        return this.catPersistence.update(updateCatEntity);

    }

    public Mono<Void> delete(Integer chip) {
        return this.catPersistence.delete(chip);
    }

    public Flux<Cat> findBySociableIsTrueAndDepartureDateIsNull(boolean onlyAdoptable) {
        return this.catPersistence.findBySociableIsTrueAndDepartureDateIsNull(onlyAdoptable);
    }
    private Mono<CatEntity> updateCat(Mono<CatEntity> catEntitySaved, Cat cat) {
        return catEntitySaved.map(catEntity1 -> {
            CatEntity catEntityUpdate = new CatEntity();
            BeanUtils.copyProperties(cat, catEntityUpdate);
            catEntityUpdate.setId(catEntity1.getId());
            return catEntityUpdate;
        });

    }
    private Mono<CatEntity> createAdoption(Mono<CatEntity> catEntitySaved, Cat cat) {
        return this.adopterMicroservice.readByNif(cat.getAdopterNif())
                .switchIfEmpty(Mono.error(
                        new NotFoundException("No exist adopter with nif: " + cat.getAdopterNif())
                )).flatMap(adopter -> {
                    return catEntitySaved.map(catEntity -> {
                        CatEntity catEntityUpdate = new CatEntity();
                        BeanUtils.copyProperties(cat, catEntityUpdate);
                        catEntityUpdate.setAdopter(adopter);
                        catEntityUpdate.setId(catEntity.getId());
                        return catEntityUpdate;
                    });
                });
    }
    /*
    private Mono<Cat> assignColony(Integer chip, Cat cat) {
        Mono<CatEntity> catEntityMono = this.assertCatExist(chip);
        return this.colonyReactive.readByRegistry(cat.getColonyRegistry())
                .switchIfEmpty(Mono.error(
                        new NotFoundException("No exist colony with registry: " + cat.getColonyRegistry())
                ))
                .flatMap(colonyEntity -> {
                    return catEntityMono.map(catEntityActual -> {
                        CatEntity catEntityUpdate = new CatEntity();
                        BeanUtils.copyProperties(cat, catEntityUpdate);
                        catEntityUpdate.setColonyEntity(colonyEntity);
                        catEntityUpdate.setId(catEntityActual.getId());
                        return catEntityUpdate;
                    });
                })
                .flatMap(catEntity -> this.catReactive.save(catEntity))
                .flatMap(catEntitySaved -> Mono.just(catEntitySaved.toCat()));
    }
*/
}
