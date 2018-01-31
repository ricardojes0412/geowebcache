package org.geowebcache.grid;

import org.geowebcache.GeoWebCacheException;
import org.geowebcache.config.GWCConfigIntegrationTest;
import org.geowebcache.config.GWCConfigIntegrationTestData;
import org.geowebcache.grid.BoundingBox;
import org.geowebcache.grid.GridSet;
import org.geowebcache.grid.GridSetFactory;
import org.geowebcache.grid.SRS;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import static org.geowebcache.util.TestUtils.isPresent;
import static org.geowebcache.util.TestUtils.notPresent;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;

public class GridSetBrokerTest extends GWCConfigIntegrationTest {

    @Test
    public void testGridSetList() {
        Set<String> names = gridSetBroker.getNames();

        int i = 0;
        for (GridSet grid : gridSetBroker.getGridSets()) {
            assertTrue(names.contains(grid.getName()));
            i++;
        }
        assertEquals(i, names.size());
    }
    
    @Test
    public void testGetDefaultGridsetOld() throws IOException {
        GridSet existingGridSet = gridSetBroker.get(GWCConfigIntegrationTestData.GRIDSET_EPSG4326);
        assertThat(existingGridSet, hasProperty("name", equalTo(GWCConfigIntegrationTestData.GRIDSET_EPSG4326)));
    }
    @Test
    public void testGetDefaultGridSet() throws IOException {
        Optional<GridSet> existingGridSet = gridSetBroker.getGridSet(GWCConfigIntegrationTestData.GRIDSET_EPSG4326);
        assertThat(existingGridSet, isPresent(hasProperty("name", equalTo(GWCConfigIntegrationTestData.GRIDSET_EPSG4326))));
    }
    @Test
    public void testGetGridsetOld() throws IOException {
        GridSet existingGridSet = gridSetBroker.get(GWCConfigIntegrationTestData.GRIDSET_EPSG2163);
        assertThat(existingGridSet, hasProperty("name", equalTo(GWCConfigIntegrationTestData.GRIDSET_EPSG2163)));
    }
    @Test
    public void testGetGridSet() throws IOException {
        Optional<GridSet> existingGridSet = gridSetBroker.getGridSet(GWCConfigIntegrationTestData.GRIDSET_EPSG2163);
        assertThat(existingGridSet, isPresent(hasProperty("name", equalTo(GWCConfigIntegrationTestData.GRIDSET_EPSG2163))));
    }
    @Test
    public void testGetNotPresentGridsetOld() throws IOException {
        GridSet existingGridSet = gridSetBroker.get("DOESNOTEXIST");
        assertThat(existingGridSet, nullValue());
    }
    @Test
    public void testGetNotPresentGridSet() throws IOException {
        Optional<GridSet> existingGridSet = gridSetBroker.getGridSet("DOESNOTEXIST");
        assertThat(existingGridSet, notPresent());
    }

    //add / remove gridset
    @Test
    public void testAddGridset() throws GeoWebCacheException, IOException {
        String gridsetName = "EPSG:3005";
        GridSet epsg3005 = GridSetFactory.createGridSet(gridsetName, SRS.getSRS(gridsetName),
                new BoundingBox(35043.6538, 440006.8768,
                        1885895.3117, 1735643.8497),
                false, null,
                new double[]{ 25000000, 1250000, 500000, 250000 },
                null, GridSetFactory.DEFAULT_PIXEL_SIZE_METER,
                null, 256, 256, false);

        gridSetBroker.addGridSet(epsg3005);

        assertTrue(gridSetBroker.getNames().contains(gridsetName));
        assertEquals(gridSetBroker.get(gridsetName), epsg3005);
    }

    @Test
    public void testAddBadGridset() throws IOException {
        //existing
        GridSet existingGridSet = gridSetBroker.get(GWCConfigIntegrationTestData.GRIDSET_EPSG2163);
        try {
            gridSetBroker.addGridSet(existingGridSet);
            fail("Expected exception adding existing gridset");
        } catch (IllegalArgumentException e) {

        }
        try {
            gridSetBroker.addGridSet(null);
            fail("Expected exception adding null gridset");
        } catch (NullPointerException e) {

        }
    }

    @Test
    public void testRemoveGridsetOld() throws IOException {
        String gridsetToRemove = GWCConfigIntegrationTestData.GRIDSET_EPSG2163;
        //remove the only layer referencing the gridset first
        gridSetBroker.remove(gridsetToRemove);

        assertFalse(gridSetBroker.getNames().contains(gridsetToRemove));
        assertNull(gridSetBroker.get(gridsetToRemove));
    }
    
    @Test
    public void testRemoveGridset() throws IOException {
        String gridsetToRemove = GWCConfigIntegrationTestData.GRIDSET_EPSG2163;
        //remove the only layer referencing the gridset first
        gridSetBroker.removeGridSet(gridsetToRemove);

        assertThat(gridSetBroker.getGridSetNames(), not(hasItem(gridsetToRemove)));
        assertThat(gridSetBroker.getGridSet(gridsetToRemove), notPresent());
    }
}
