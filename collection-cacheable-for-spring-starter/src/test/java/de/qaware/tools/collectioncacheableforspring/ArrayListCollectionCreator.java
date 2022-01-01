package de.qaware.tools.collectioncacheableforspring;

import de.qaware.tools.collectioncacheableforspring.creator.CollectionCreator;

import java.util.ArrayList;
import java.util.Collection;

public class ArrayListCollectionCreator implements CollectionCreator {
    @Override
    public boolean canHandle(Class<?> cls) {
        return cls.equals(ArrayList.class);
    }

    @Override
    public <T> Collection<T> create(Collection<T> collection) {
        return new ArrayList<>(collection);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
