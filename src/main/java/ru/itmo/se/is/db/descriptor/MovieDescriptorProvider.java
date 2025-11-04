package ru.itmo.se.is.db.descriptor;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.descriptors.VersionLockingPolicy;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.mappings.converters.EnumTypeConverter;
import ru.itmo.se.is.annotation.EntityDescriptor;
import ru.itmo.se.is.db.converter.ZonedDateTimeConverter;
import ru.itmo.se.is.entity.Coordinates;
import ru.itmo.se.is.entity.Movie;
import ru.itmo.se.is.entity.Person;
import ru.itmo.se.is.entity.value.MovieGenre;
import ru.itmo.se.is.entity.value.MpaaRating;

@ApplicationScoped
public class MovieDescriptorProvider {

    @Produces
    @ApplicationScoped
    @EntityDescriptor
    public ClassDescriptor createMovieDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(Movie.class);
        descriptor.setTableName("movie");
        descriptor.addPrimaryKeyFieldName("id");
        descriptor.setSequenceNumberFieldName("id");
        descriptor.setSequenceNumberName("movie_id_seq");

        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("id");
        idMapping.setIsPrimaryKeyMapping(true);
        descriptor.addMapping(idMapping);

        DirectToFieldMapping nameMapping = new DirectToFieldMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setFieldName("name");
        nameMapping.setIsOptional(false);
        descriptor.addMapping(nameMapping);

        AggregateObjectMapping coordinatesMapping = new AggregateObjectMapping();
        coordinatesMapping.setAttributeName("coordinates");
        coordinatesMapping.setReferenceClass(Coordinates.class);
        coordinatesMapping.addFieldNameTranslation("coordinates_x", "x");
        coordinatesMapping.addFieldNameTranslation("coordinates_y", "y");
        descriptor.addMapping(coordinatesMapping);

        DirectToFieldMapping creationDateMapping = new DirectToFieldMapping();
        creationDateMapping.setAttributeName("creationDate");
        creationDateMapping.setFieldName("creation_data");
        creationDateMapping.setIsOptional(false);
        creationDateMapping.setConverter(new ZonedDateTimeConverter());
        descriptor.addMapping(creationDateMapping);

        DirectToFieldMapping oscarsMapping = new DirectToFieldMapping();
        oscarsMapping.setAttributeName("oscarsCount");
        oscarsMapping.setFieldName("oscars_count");
        oscarsMapping.setIsOptional(false);
        descriptor.addMapping(oscarsMapping);

        DirectToFieldMapping budgetMapping = new DirectToFieldMapping();
        budgetMapping.setAttributeName("budget");
        budgetMapping.setFieldName("budget");
        budgetMapping.setIsOptional(false);
        descriptor.addMapping(budgetMapping);

        DirectToFieldMapping totalBoxOfficeMapping = new DirectToFieldMapping();
        totalBoxOfficeMapping.setAttributeName("totalBoxOffice");
        totalBoxOfficeMapping.setFieldName("total_box_office");
        totalBoxOfficeMapping.setIsOptional(false);
        descriptor.addMapping(totalBoxOfficeMapping);

        DirectToFieldMapping mpaaRatingMapping = new DirectToFieldMapping();
        mpaaRatingMapping.setAttributeName("mpaaRating");
        mpaaRatingMapping.setFieldName("mpaa_rating");
        EnumTypeConverter mpaaConv = new EnumTypeConverter(mpaaRatingMapping, MpaaRating.class, false);
        mpaaRatingMapping.setConverter(mpaaConv);
        descriptor.addMapping(mpaaRatingMapping);

        OneToOneMapping directorMapping = new OneToOneMapping();
        directorMapping.setAttributeName("director");
        directorMapping.setReferenceClass(Person.class);
        directorMapping.addForeignKeyFieldName("director_id", "id");
        directorMapping.setIsOptional(false);
        directorMapping.setCascadeAll(true);
        directorMapping.dontUseIndirection();
        descriptor.addMapping(directorMapping);

        OneToOneMapping screenwriterMapping = new OneToOneMapping();
        screenwriterMapping.setAttributeName("screenwriter");
        screenwriterMapping.setReferenceClass(Person.class);
        screenwriterMapping.addForeignKeyFieldName("screenwriter_id", "id");
        screenwriterMapping.setCascadeAll(true);
        screenwriterMapping.dontUseIndirection();
        descriptor.addMapping(screenwriterMapping);

        OneToOneMapping operatorMapping = new OneToOneMapping();
        operatorMapping.setAttributeName("operator");
        operatorMapping.setReferenceClass(Person.class);
        operatorMapping.addForeignKeyFieldName("operator_id", "id");
        operatorMapping.dontUseIndirection();
        descriptor.addMapping(operatorMapping);

        DirectToFieldMapping lengthMapping = new DirectToFieldMapping();
        lengthMapping.setAttributeName("length");
        lengthMapping.setFieldName("length");
        descriptor.addMapping(lengthMapping);

        DirectToFieldMapping goldenPalmCountMapping = new DirectToFieldMapping();
        goldenPalmCountMapping.setAttributeName("goldenPalmCount");
        goldenPalmCountMapping.setFieldName("golden_palm_count");
        goldenPalmCountMapping.setIsOptional(false);
        descriptor.addMapping(goldenPalmCountMapping);

        DirectToFieldMapping usaBoxOfficeMapping = new DirectToFieldMapping();
        usaBoxOfficeMapping.setAttributeName("usaBoxOffice");
        usaBoxOfficeMapping.setFieldName("usa_box_office");
        usaBoxOfficeMapping.setIsOptional(false);
        descriptor.addMapping(usaBoxOfficeMapping);

        DirectToFieldMapping taglineMapping = new DirectToFieldMapping();
        taglineMapping.setAttributeName("tagline");
        taglineMapping.setFieldName("tagline");
        taglineMapping.setIsOptional(false);
        descriptor.addMapping(taglineMapping);

        DirectToFieldMapping genreMapping = new DirectToFieldMapping();
        genreMapping.setAttributeName("genre");
        genreMapping.setFieldName("genre");
        EnumTypeConverter genreConv = new EnumTypeConverter(genreMapping, MovieGenre.class, false);
        genreMapping.setConverter(genreConv);
        descriptor.addMapping(genreMapping);

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
