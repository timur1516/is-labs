package ru.itmo.se.is.db.descriptor;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.descriptors.VersionLockingPolicy;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.converters.EnumTypeConverter;
import ru.itmo.se.is.annotation.EntityDescriptor;
import ru.itmo.se.is.entity.Location;
import ru.itmo.se.is.entity.Person;
import ru.itmo.se.is.entity.value.Color;
import ru.itmo.se.is.entity.value.Country;

@ApplicationScoped
public class PersonDescriptorProvider {

    @Produces
    @ApplicationScoped
    @EntityDescriptor
    public ClassDescriptor createPersonDescriptor() {
        ClassDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(Person.class);
        descriptor.setTableName("person");
        descriptor.addPrimaryKeyFieldName("id");
        descriptor.setSequenceNumberFieldName("id");
        descriptor.setSequenceNumberName("person_id_seq");

        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("id");
        idMapping.setIsPrimaryKeyMapping(true);
        descriptor.addMapping(idMapping);

        DirectToFieldMapping nameMapping = new DirectToFieldMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setFieldName("name");
        descriptor.addMapping(nameMapping);

        DirectToFieldMapping eyeColorMapping = new DirectToFieldMapping();
        eyeColorMapping.setAttributeName("eyeColor");
        eyeColorMapping.setFieldName("eye_color");
        EnumTypeConverter eyeColorConverter = new EnumTypeConverter(eyeColorMapping, Color.class, false);
        eyeColorMapping.setConverter(eyeColorConverter);
        descriptor.addMapping(eyeColorMapping);

        DirectToFieldMapping hairColorMapping = new DirectToFieldMapping();
        hairColorMapping.setAttributeName("hairColor");
        hairColorMapping.setFieldName("hair_color");
        hairColorMapping.setIsOptional(false);
        EnumTypeConverter hairColorConverter = new EnumTypeConverter(hairColorMapping, Color.class, false);
        hairColorMapping.setConverter(hairColorConverter);
        descriptor.addMapping(hairColorMapping);

        AggregateObjectMapping locationMapping = new AggregateObjectMapping();
        locationMapping.setAttributeName("location");
        locationMapping.setReferenceClass(Location.class);
        locationMapping.addFieldNameTranslation("location_x", "x");
        locationMapping.addFieldNameTranslation("location_y", "y");
        locationMapping.addFieldNameTranslation("location_z", "z");
        descriptor.addMapping(locationMapping);

        DirectToFieldMapping weightMapping = new DirectToFieldMapping();
        weightMapping.setAttributeName("weight");
        weightMapping.setFieldName("weight");
        descriptor.addMapping(weightMapping);

        DirectToFieldMapping nationalityMapping = new DirectToFieldMapping();
        nationalityMapping.setAttributeName("nationality");
        nationalityMapping.setFieldName("nationality");
        EnumTypeConverter natConv = new EnumTypeConverter(nationalityMapping, Country.class, false);
        nationalityMapping.setConverter(natConv);
        descriptor.addMapping(nationalityMapping);

        DirectToFieldMapping versionMapping = new DirectToFieldMapping();
        versionMapping.setAttributeName("version");
        versionMapping.setFieldName("version");
        descriptor.addMapping(versionMapping);

        DatabaseField versionField = new DatabaseField("version");
        VersionLockingPolicy lockingPolicy = new VersionLockingPolicy(versionField);
        lockingPolicy.storeInObject();
        lockingPolicy.setIsCascaded(false);
        descriptor.setOptimisticLockingPolicy(lockingPolicy);
        descriptor.useNoIdentityMap();
        descriptor.getQueryManager().setExistenceCheck("Check database");
        return descriptor;
    }
}
