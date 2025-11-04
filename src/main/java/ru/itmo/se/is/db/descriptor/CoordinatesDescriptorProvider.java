package ru.itmo.se.is.db.descriptor;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import ru.itmo.se.is.annotation.EntityDescriptor;
import ru.itmo.se.is.entity.Coordinates;

@ApplicationScoped
public class CoordinatesDescriptorProvider {

    @Produces
    @ApplicationScoped
    @EntityDescriptor
    public ClassDescriptor createCoordinatesDescriptor() {
        ClassDescriptor descriptor = new ClassDescriptor();
        descriptor.setJavaClass(Coordinates.class);
        descriptor.descriptorIsAggregate();

        DirectToFieldMapping xMapping = new DirectToFieldMapping();
        xMapping.setAttributeName("x");
        xMapping.setFieldName("x");
        descriptor.addMapping(xMapping);

        DirectToFieldMapping yMapping = new DirectToFieldMapping();
        yMapping.setAttributeName("y");
        yMapping.setFieldName("y");
        descriptor.addMapping(yMapping);
        descriptor.useNoIdentityMap();
        descriptor.getQueryManager().setExistenceCheck("Check database");
        return descriptor;
    }
}
