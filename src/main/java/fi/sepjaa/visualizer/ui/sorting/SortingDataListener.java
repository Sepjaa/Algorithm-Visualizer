package fi.sepjaa.visualizer.ui.sorting;

import fi.sepjaa.visualizer.sorting.SortingData;

/**
 * Interface for registering objects that are interested in the current
 * {@link SortingData}.
 *
 * @author Jaakko
 *
 */
public interface SortingDataListener {

	void bind(SortingData data);

	void unBind();

}
