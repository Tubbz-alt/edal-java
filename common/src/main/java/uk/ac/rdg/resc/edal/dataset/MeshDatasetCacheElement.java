package uk.ac.rdg.resc.edal.dataset;

import uk.ac.rdg.resc.edal.util.GridCoordinates2D;

import java.io.Serializable;
import java.util.List;

/**
 * Element used in {@link HorizontalMesh4dDataset} cache.
 *
 * @author Jesse Lopez
 */
class MeshDatasetCacheElement implements Serializable {
    private static final long serialVersionUID = 1L;

    List<GridCoordinates2D> outputCoords;
    List<Integer> coordsToRead;

    MeshDatasetCacheElement(List<GridCoordinates2D> outputCoords, List<Integer> coordsToRead) {
        this.outputCoords = outputCoords;
        this.coordsToRead = coordsToRead;
    }

    List<GridCoordinates2D> getOutputCoords() {
        return outputCoords;
    }

    List<Integer> getCoordsToRead() {
        return coordsToRead;
    }
}
