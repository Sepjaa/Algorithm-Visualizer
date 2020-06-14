package fi.sepjaa.visualizer.ui.sorting;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import fi.sepjaa.visualizer.common.AlgorithmExecutor;
import fi.sepjaa.visualizer.common.CommonConstants;
import fi.sepjaa.visualizer.sorting.SortingData;
import fi.sepjaa.visualizer.sorting.algorithm.ImmutableSortingData;
import fi.sepjaa.visualizer.sorting.algorithm.SortingAlgorithm;
import fi.sepjaa.visualizer.sorting.algorithm.SortingAlgorithm.Type;
import fi.sepjaa.visualizer.ui.common.AlgorithmPanel;
import fi.sepjaa.visualizer.ui.common.UiConstants;
import net.miginfocom.swing.MigLayout;

/**
 * Panel containing a {@link SortingVisualizer}, the sort configuration
 * components and a start/stop button.
 *
 * @author Jaakko
 *
 */
@SuppressWarnings("serial")
@Component
public class SortingPanel extends AlgorithmPanel {
	private static final Logger LOG = LoggerFactory.getLogger(SortingPanel.class);

	private final JTextField elements, comparisonSleep, swapSleep;
	private final JComboBox<Type> algorithmSelection;
	private final ImmutableMap<Type, SortingAlgorithm> algorithms;
	private final ImmutableList<SortingDataListener> dataListeners;
	private SortingData data;
	private long elementCount = CommonConstants.DEFAULT_SORTING_ELEMENT_AMOUNT;

	@Autowired
	public SortingPanel(SortingVisualizer visualizer, List<SortingAlgorithm> algorithms, AlgorithmExecutor executor,
			List<SortingDataListener> dataListeners) {
		super(visualizer, executor);
		this.algorithmSelection = new JComboBox<>();
		ImmutableMap.Builder<Type, SortingAlgorithm> builder = new ImmutableMap.Builder<>();
		for (SortingAlgorithm alg : algorithms) {
			builder.put(alg.getType(), alg);
			this.algorithmSelection.addItem(alg.getType());
		}
		this.algorithms = builder.build();
		this.dataListeners = ImmutableList.copyOf(dataListeners);

		this.elements = UiConstants.getIntegerInstance(CommonConstants.DEFAULT_SORTING_ELEMENT_AMOUNT,
				i -> setElementsCount(i));
		this.elements.setPreferredSize(new Dimension((int) 100, this.elements.getHeight()));

		this.comparisonSleep = UiConstants.getIntegerInstance(CommonConstants.DEFAULT_COMPARISON_SLEEP,
				i -> setCompareSleep(i));
		this.comparisonSleep.setPreferredSize(new Dimension((int) 100, this.comparisonSleep.getHeight()));

		this.swapSleep = UiConstants.getIntegerInstance(CommonConstants.DEFAULT_SWAP_SLEEP, i -> setSwapSleep(i));
		this.swapSleep.setPreferredSize(new Dimension((int) 100, this.swapSleep.getHeight()));

		this.algorithmSelection.addActionListener(a -> {
			createData();
			visualizer.setDrawMemory(getAlgorithm().isFullMemoryBufferAlgorithm());
		});

		validate();
	}

	@Override
	protected void createData() {
		if (data == null) {
			data = new SortingData(elementCount, getAlgorithm());
		} else {
			ImmutableSortingData copy = data.getCopy();
			data = new SortingData(elementCount, getAlgorithm(), copy.getSwapSleep(), copy.getComparisonSleep());
		}
		dataListeners.forEach(listener -> listener.bind(data));
	}

	@Override
	public String getTitle() {
		return UiConstants.SORTING_TAB_TITLE;
	}

	private SortingAlgorithm getAlgorithm() {
		Type type = (Type) algorithmSelection.getSelectedItem();
		return this.algorithms.get(type);
	}

	private void setElementsCount(long count) {
		elementCount = count;
	}

	private void setCompareSleep(long sleep) {
		data.setComparisonSleep(sleep);
	}

	private void setSwapSleep(long sleep) {
		data.setSwapSleep(sleep);
	}

	@Override
	protected JPanel createConfig() {
		JPanel config = new JPanel(new MigLayout("insets 0", "[][grow, fill]", "[]15[]15[][][]"));

		config.add(new JLabel(UiConstants.ELEMENTS_LBL), "cell 0 0");
		config.add(elements, "cell 1 0");

		config.add(new JLabel(UiConstants.ALGORITHM_LBL), "cell 0 1");
		config.add(algorithmSelection, "cell 1 1");

		config.add(new JLabel(UiConstants.OPERATION_SLEEP_TIMES_LBL), "cell 0 2, span 2 1");

		config.add(new JLabel(UiConstants.COMPARISON_LBL), "cell 0 3");
		config.add(comparisonSleep, "cell 1 3");

		config.add(new JLabel(UiConstants.SWAP_LBL), "cell 0 4");
		config.add(swapSleep, "cell 1 4");
		return config;
	}

	@Override
	protected Runnable run() {
		return () -> {
			LOG.info("Starting to run sort");
			getAlgorithm().sort(data);
			data.clearNexts();
		};
	}
}
