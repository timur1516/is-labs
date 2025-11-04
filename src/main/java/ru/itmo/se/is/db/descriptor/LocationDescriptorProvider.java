package ru.itmo.se.is.db.descriptor;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import ru.itmo.se.is.annotation.EntityDescriptor;
import ru.itmo.se.is.entity.Location;

@ApplicationScoped
public class LocationDescriptorProvider {

    @Produces
    @ApplicationScoped
    @EntityDescriptor
    public ClassDescriptor createLocationDescriptor() {
        ClassDescriptor descriptor = new ClassDescriptor();
        descriptor.setJavaClass(Location.class);
        descriptor.descriptorIsAggregate();

        DirectToFieldMapping x = new DirectToFieldMapping();
        x.setAttributeName("x");
        x.setFieldName("x");
        descriptor.addMapping(x);

        DirectToFieldMapping y = new DirectToFieldMapping();
        y.setAttributeName("y");
        y.setFieldName("y");
        descriptor.addMapping(y);

        DirectToFieldMapping z = new DirectToFieldMapping();
        z.setAttributeName("z");
        z.setFieldName("z");
        descriptor.addMapping(z);
        descriptor.useNoIdentityMap();
        descriptor.getQueryManager().setExistenceCheck("Check database");
        return descriptor;
    }
}
